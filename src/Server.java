import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
    public static ServerSocket server_socket;
    public static ArrayList<Socket> socketList = new ArrayList<Socket>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入端口号");
        int port = sc.nextInt();
        try {
            server_socket = new ServerSocket(port);
            while (true) {
                Socket socket = server_socket.accept();
                socketList.add(socket); // 把sock对象加入sock集合
                ServerBO_Thread st = new ServerBO_Thread(socket, socketList); // 初始化多线程
                st.start();// 启动多线程
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (server_socket != null) {
                    server_socket.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void encryptWrite(String src, DataOutputStream output) throws IOException {
        // 将一个字符串转化为字符数组
        // System.out.println(src);
        char[] char_arr = src.toCharArray();
        // 加密操作
        for (int i = 0; i < char_arr.length; i++) {
            output.writeChar(char_arr[i] + 13);
        }
        // 用作结束标志符
        output.writeChar(2333);
        output.flush();
    }

    // 读取并解密
    public String readDecrypt(DataInputStream input) throws IOException {
        StringBuffer rtn = new StringBuffer("");
        while (true) {
            int char_src = input.readChar();
            if (char_src != 2333) {
                rtn.append((char) (char_src - 13));
                // rtn=rtn+(char)(char_src-13);
            } else {
                break;
            }
        }
        return rtn.toString();
    }
}

class ServerBO_Thread extends Thread {
    Socket client = null;
    ArrayList<Socket> clients;

    ServerBO_Thread(Socket s, ArrayList<Socket> ss) {// 初始化
        client = s;
        clients = ss;
    }

    public void run() {
        DataInputStream input = null;
        DataOutputStream output = null;
        try {
            input = new DataInputStream(client.getInputStream());
            Server bo = new Server();
            String receive = null;
            String send = null;
            while (true) {// 监视当前客户端有没有发来消息
                if (!client.isClosed()) {
                    receive = bo.readDecrypt(input);
                    clients.trimToSize();
                    String[] param = receive.split("&");
                    if (")start".equals(param[1])) { // 分析客户端发来的内容
                        send = param[0] + "进入聊天室";
                    } else {
                        send = param[0] + "说:    " + param[1];
                    }
                    if (!("exit".equals(param[1]))) {// exit为退出聊天室信号
                        for (Socket socket : clients) { // 遍历socket集合
                            // 把读取到的消息发送给各个客户端
                            if (!socket.isClosed()) {
                                output = new DataOutputStream(socket.getOutputStream());
                                bo.encryptWrite(send, output);
                            }
                        }
                    } else {// 如果有客户端退出
                        for (Socket socket : clients) { // 遍历socke集合
                            if (socket != client) {// 告诉其他人此人退出聊天室
                                if (!(socket.isClosed())) {
                                    output = new DataOutputStream(socket.getOutputStream());
                                    bo.encryptWrite(param[0] + "已退出聊天室", output);
                                }
                            }
                        }
                        output = new DataOutputStream(client.getOutputStream());
                        bo.encryptWrite("exit", output);// 返回信号给要退出的客户端，然后关闭线程
                        client.close();
                        input.close();
                        output.close();
                    }
                } else {
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
