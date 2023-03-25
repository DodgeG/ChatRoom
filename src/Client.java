import java.io.IOException;
import java.util.Scanner;
import java.net.*;
import java.io.*;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.DataOutputStream;
public class Client{
//服务端ip
    public  String ip = "127.0.0.1";
    //服务端端口
    public  int port = 1234;
    public  DataOutputStream output = null;
    public  Socket socket = null;
    public  DataInputStream input = null;
    public  Scanner sc =new Scanner (System.in);
    public  String send ;
    public  String receive;
    public  String name;
    public String sd = null;
    public static void main(String[]aa){
        Client po = new Client();
        po.start();
    }
    public void start(){
        try{
            
            System.out.println("请输入端口号：");
            port=Integer.parseInt(sc.nextLine());
            System.out.println("请输入你将要使用的昵称：");
            name=sc.nextLine();//获取昵称
            socket = new Socket(ip,port);
            output=new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());
            send = name+"&)start";//把昵称发送到server 告诉所有人有新成员加入聊天室
            System.out.println("(如果要退出聊天室请输入“exit”！)");
            System.out.println("*******成功加入聊天**********");
            System.out.println("");
            encryptWrite(send,output);
            Out out=new Out(output,name,input,socket);
            out.start();//启动发送聊天内容的多线程
            while(true){    
                String receive = readDecrypt(input);
                if("exit".equals(receive)){//如果收到“exit”则退出聊天室
                    System.out.println("*******成功退出聊天**********");
                    input.close();
                    output.close();
                    socket.close();
                    System.exit(0);
                }
                System.out.println(receive);
            }
        }catch(Exception ex){
                ex.printStackTrace();
        }finally{
            try{
                if(socket!=null) socket.close();
                input.close();
                output.close();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }    
    }
    public void encryptWrite(String src,DataOutputStream output)throws IOException{
        //将一个字符串转化为字符数组
        char[] char_arr = src.toCharArray();
        //加密操作
        for(int i = 0;i<char_arr.length;i++){
            output.writeChar(char_arr[i]+13);
        }
        //用作结束标志符
        output.writeChar(2333);
        output.flush();
    }
    //读取并解密
    public String readDecrypt(DataInputStream input)throws IOException{
        StringBuffer rtn = new StringBuffer("");
        while(true){
            int char_src =input.readChar();
            if(char_src!=2333){
                rtn.append((char)(char_src-13));
                // rtn=rtn+(char)(char_src-13);
            }else{
                break;
            }
        }
        return rtn.toString();
    }
}
class Out extends Thread {
    public DataOutputStream output;
    public DataInputStream input;
    public static String name;
    public Socket socket;
    public  Scanner sc =new Scanner (System.in);
    Out(DataOutputStream ot,String n,DataInputStream it,Socket socket){
        output=ot;
        input=it;
        name=n;
    }
    public void run(){
        Client po = new Client();
        try{
            while(true){
                String send=sc.nextLine();//获取用户输入
                String send2=name+"&"+send;//把聊天内容打包成约定形式
                po.encryptWrite(send2,output);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            System.out.println("sfef");
        }
    }
}
 