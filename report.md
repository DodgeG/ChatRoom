# report

姓名：巩德志	学号：3200105088

### 服务器设计

运行server.jar后，输入需要监听的端口号，每当有一个客户端连接到服务器时，服务器就新开一个线程进行处理

![image](https://images-tc.oss-cn-beijing.aliyuncs.com/20221231180443324.png)

ServerBO_Thread中最主要的方法是run，监听并读取客户端发来的消息，并通过遍历socket将读取的消息发送给所有客户端。如果有客户端退出，遍历socket将退出的消息发送给所有客户端，关闭线程即可。

![image](https://images-tc.oss-cn-beijing.aliyuncs.com/20221231181221390.png)

实现了encryptWrite和readDecrypt来加密和读写数据流，加密方式为字符ASCII+13，2333在字符串末尾用作结束符

![image](https://images-tc.oss-cn-beijing.aliyuncs.com/20221231181514549.png)

### 客户端设计

运行client.jar后，输入需要连接的端口号和用户名，运行线程收发数据

![image](https://images-tc.oss-cn-beijing.aliyuncs.com/20221231182031156.png)

![image](https://images-tc.oss-cn-beijing.aliyuncs.com/20221231182110615.png)

### 实验结果

开启一个client

![image](https://images-tc.oss-cn-beijing.aliyuncs.com/20221231182248127.png)

开启两个client，发送消息

![image](https://images-tc.oss-cn-beijing.aliyuncs.com/20221231182444718.png)

用户退出聊天

![image](https://images-tc.oss-cn-beijing.aliyuncs.com/20221231182524286.png)
