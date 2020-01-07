# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
昨天的文章，全是理论，没有一行代码，但是计算机网络，本来就是很枯燥的一门课程，如果能理解它的概念，对你开发室友莫大的好处的，今天我们来学习一下Java中是怎么具体使用它的。   
[🔥Java基础之网络编程（一）](https://juejin.im/post/5df84c566fb9a0164c7baa0e)

## 访问网络的传输过程（上网的流程）

1. 首先输入 网址 电脑会自己去dns域名解析服务器去找到 对应的ip 然后返回给请求的那台机器 上网的这台机器 通过交换机 路由器 接到 isp网络运营商  他会一层一层的通过
自己的ip mac地址 和目标服务器的 ip 和mac 然后每次的中转路由 他的目标ip是固定的 跳转的mac是通过路由算法得到 最终就可以连接到目标服务器 然后就可以上网的 这就是
网络的传输过程


## 自己对 ISO 网络7层模型的理解
- 从上到下 
- 第七层 应用层 类似于 百度这种网站 就算一个应用 软件 各种客户端 能产生网络流量的程序 就是应用层
- 第六层 表示层  就是 在传输之前 是否进行加密 或者压缩 
- 第五层 会话层 2个应用就行通信需要建立会话连接 类似于tcp的三次握手 四次挥手  我们可以在这一层 查木马 netstat -n  
- 第四层 传输层  可靠传输tcp  不可靠输层 udp等 
- 第三层 网络层 ip 负责最佳路径的规范 
- 第二层 数据链路层 规范 帧的开始和结束
- 第一层 物理层 就是硬件设备 接口的标准 电器标准 传输速度优化
## 对数据的封装过程
- 上三层  就是封装数据我们从数据库查出来的数据 然后传输
- 传输层  tcp头 + 上层数据 == 叫数据段
- 网络层 ip头+tcp头 + 上层数据 == 叫数据包
- 数据链路层 mac头+ip头+tcp头 + 上层数据 +fcs对数据的校验 ==叫数据帧 
- 物理层  把数据链路层的数据 封装成 0101 ==bit


## IP地址与端口号
> 要想使网络中的计算机能够进行通信，必须为每台计算机指定一个标识号，通过这个标识号来指定接受数据的计算机或者发送数据的计算机

> 在TCP/IP协议中，这个标识号就是IP地址，它可以唯一标识一台计算机，目前，IP地址广泛使用的版本是IPv4，它是由4个字节大小的二进制数来表示，如：00001010000000000000000000000001。由于二进制形式表示的IP地址非常不便记忆和处理，因此通常会将IP地址写成十进制的形式，每个字节用一个十进制数字(0-255)表示，数字间用符号“.”分开，如 "192.168.1.100"

> 随着计算机网络规模的不断扩大，对IP地址的需求也越来越多，IPV4这种用4个字节表示的IP地址面临枯竭，因此IPv6 便应运而生了，IPv6使用16个字节表示IP地址，它所拥有的地址容量约是IPv4的8×1028倍，达到2128个（算上全零的），这样就解决了网络地址资源数量不够的问题

> 通过IP地址可以连接到指定计算机，但如果想访问目标计算机中的某个应用程序，还需要指定端口号。在计算机中，不同的应用程序是通过端口号区分的。端口号是用两个字节（16位的二进制数）表示的，它的取值范围是0 ~ 65535，其中，1024以下的端口号用于一些知名的网络服务和应用，用户的普通应用程序需要使用1024以上的端口号，从而避免端口号被另外一个应用或服务所占用

> 接下来通过一个图例来描述IP地址和端口号的作用，如下图所示

![image](https://user-gold-cdn.xitu.io/2019/12/17/16f134a50e83207b?w=655&h=226&f=png&s=87552)

> 从上图中可以清楚地看到，位于网络中一台计算机可以通过IP地址去访问另一台计算机，并通过端口号访问目标计算机中的某个应用程序
        
### InetAddress

> 了解了IP地址的作用，我们看学习下JDK中提供了一个InetAdderss类，该类用于封装一个IP地址，并提供了一系列与IP地址相关的方法，下表中列出了InetAddress类的一些常用方法

![image](https://user-gold-cdn.xitu.io/2019/12/17/16f134b98d3fcd34?w=736&h=247&f=png&s=62669)

> 上图中，列举了InetAddress的四个常用方法。其中，前两个方法用于获得该类的实例对象，第一个方法用于获得表示指定主机的InetAddress对象，第二个方法用于获得表示本地的InetAddress对象。通过InetAddress对象便可获取指定主机名，IP地址等，接下来通过一个案例来演示InetAddress的常用方法

```
    public class Example01 {
    	public static void main(String[] args) throws Exception {
    		InetAddress local = InetAddress.getLocalHost();
    		InetAddress remote = InetAddress.getByName("www.baidu.com");
    		System.out.println("本机的IP地址：" + local.getHostAddress());
    		System.out.println("IP地址：" + remote.getHostAddress());
    		System.out.println("主机名为：" + remote.getHostName());
    	}
    }

```    
        
 ## tcp 是如何实现可靠传输的
- 停止等待协议   超时重传 
- 目前用的是流水线传输 提高信道的利用率
- 以字节为单位的滑动窗口技术
    
## UDP通讯
### DatagramPacket
前面介绍了UDP是一种面向无连接的协议，因此，在通信时发送端和接收端不用建立连接。UDP通信的过程就像是货运公司在两个码头间发送货物一样。在码头发送和接收货物时都需要使用集装箱来装载货物，UDP通信也是一样，发送和接收的数据也需要使用“集装箱”进行打包，为此JDK中提供了一个DatagramPacket类，该类的实例对象就相当于一个集装箱，用于封装UDP通信中发送或者接收的数据

想要创建一个DatagramPacket对象，首先需要了解一下它的构造方法。在创建发送端和接收端的DatagramPacket对象时，使用的构造方法有所不同，接收端的构造方法只需要接收一个字节数组来存放接收到的数据，而发送端的构造方法不但要接收存放了发送数据的字节数组，还需要指定发送端IP地址和端口号

![image](https://user-gold-cdn.xitu.io/2019/12/17/16f132e69867672c?w=790&h=61&f=png&s=17514)

> 使用该构造方法在创建DatagramPacket对象时，指定了封装数据的字节数组和数据的大小，没有指定IP地址和端口号。很明显，这样的对象只能用于接收端，不能用于发送端。因为发送端一定要明确指出数据的目的地(ip地址和端口号)**而接收端不需要明确知道数据的来源，只需要接收到数据即可**

![image](https://user-gold-cdn.xitu.io/2019/12/17/16f132e69060256b?w=827&h=57&f=png&s=25155)

> 使用该构造方法在创建DatagramPacket对象时，不仅指定了封装数据的字节数组和数据的大小，还指定了数据包的目标IP地址（addr）和端口号（port）。该对象通常用于发送端，因为在**发送数据时必须指定接收端的IP地址和端口号，就好像发送货物的集装箱上面必须标明接收人的地址一样**

> 上面我们讲解了DatagramPacket的构造方法，接下来对DatagramPacket类中的常用方法进行详细地讲解，如下表所示

![image](https://user-gold-cdn.xitu.io/2019/12/17/16f132e6a5e59404?w=816&h=192&f=png&s=49689)

### DatagramSocket

> DatagramPacket数据包的作用就如同是“集装箱”，可以将发送端或者接收端的数据封装起来。然而运输货物只有“集装箱”是不够的，还需要有码头。在程序中需要实现通信只有DatagramPacket数据包也同样不行，为此JDK中提供的一个DatagramSocket类。DatagramSocket类的作用就类似于码头，使用这个类的实例对象就可以发送和接收DatagramPacket数据包，发送数据的过程如下图所示

![image](https://user-gold-cdn.xitu.io/2019/12/17/16f1331a10b221d9?w=465&h=199&f=png&s=99054)

> 在创建发送端和接收端的DatagramSocket对象时，使用的构造方法也有所不同，下面对DatagramSocket类中常用的构造方法进行讲解

![image](https://user-gold-cdn.xitu.io/2019/12/17/16f1331a13156e9d?w=819&h=64&f=png&s=19754)

> **该构造方法用于创建发送端的DatagramSocket对象**，在创建DatagramSocket对象时，并没有指定端口号，此时，系统会分配一个没有被其它网络程序所使用的端口号

![image](https://user-gold-cdn.xitu.io/2019/12/17/16f1331a1326e95a?w=750&h=61&f=png&s=20110)

> **该构造方法既可用于创建接收端的DatagramSocket对象**，又可以创建发送端的DatagramSocket对象，在创建接收端的DatagramSocket对象时，必须要指定一个端口号，这样就可以监听指定的端口

> 上面我们讲解了DatagramSocket的构造方法，接下来对DatagramSocket类中的常用方法进行详细地讲解

![image](https://user-gold-cdn.xitu.io/2019/12/17/16f133ba5f8cdc5b?w=561&h=125&f=png&s=29997)

### UDP网络程序
> 要实现UDP通信需要创建一个发送端程序和一个接收端程序，很明显，在通信时只有接收端程序先运行，才能避免因发送端发送的数据无法接收，而造成数据丢失。因此，首先需要来完成接收端程序的编写

* UDP完成数据的发送

```
    /*
     *发送端
     * 1,创建DatagramSocket对象
     * 2，创建DatagramPacket对象，并封装数据
     * 3，发送数据
     * 4，释放流资源
     */
    public class UDPSend {
    	public static void main(String[] args) throws IOException {
    		//1,创建DatagramSocket对象
    		DatagramSocket sendSocket = new DatagramSocket();
    		//2，创建DatagramPacket对象，并封装数据
    		//public DatagramPacket(byte[] buf, int length, InetAddress address,  int port)
    		//构造数据报包，用来将长度为 length 的包发送到指定主机上的指定端口号。
    		byte[] buffer = "hello,UDP".getBytes();
    		DatagramPacket dp = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("10.0.142.9"), 6666);
    		//3，发送数据
    		//public void send(DatagramPacket p) 从此套接字发送数据报包
    		sendSocket.send(dp);
    		//4，释放流资源
    		sendSocket.close();
    	}
    }

```

* UDP完成数据的接收

```
    /*
     * UDP接收端
     * 
     * 1,创建DatagramSocket对象
     * 2,创建DatagramPacket对象
     * 3,接收数据存储到DatagramPacket对象中
     * 4,获取DatagramPacket对象的内容
     * 5,释放流资源
     */
    public class UDPReceive {
    	public static void main(String[] args) throws IOException {
    		//1,创建DatagramSocket对象,并指定端口号
    		DatagramSocket receiveSocket = new DatagramSocket(6666);
    		//2,创建DatagramPacket对象, 创建一个空的仓库
    		byte[] buffer = new byte[1024*64];
    		DatagramPacket dp = new DatagramPacket(buffer, 1024);
    		//3,接收数据存储到DatagramPacket对象中
    		receiveSocket.receive(dp);
    		//4,获取DatagramPacket对象的内容
    		//谁发来的数据  getAddress()
    		InetAddress ipAddress = dp.getAddress();
    		String ip = ipAddress.getHostAddress();//获取到了IP地址
    		//发来了什么数据  getData()
    		byte[] data = dp.getData();
    		//发来了多少数据 getLenth()
    		int length = dp.getLength();
    		//显示收到的数据
    		String dataStr = new String(data,0,length);
    		System.out.println("IP地址："+ip+ "数据是"+ dataStr);
    		//5,释放流资源
    		receiveSocket.close();
    	}
    }

```

![](https://user-gold-cdn.xitu.io/2019/12/18/16f1824faadc17e2?w=1690&h=728&f=png&s=136573)

### 数据互发

- Send
```
package com.atguigu.ct.producer.Test.AA;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/*
 *发送端
 * 1,创建DatagramSocket对象
 * 2，创建DatagramPacket对象，并封装数据
 * 3，发送数据
 * 4，释放流资源
 */
public class UDPSend {
    @SuppressWarnings("resource")
    public static void main(String[] args) throws Exception {
        //这个是发送的线程
        new Thread(()->{  Scanner sc = new Scanner(System.in);
            boolean b = true;
            DatagramSocket ds= null;
            while(b){
                String next = sc.next();

                byte[] data = next.getBytes();

                InetAddress inet;
                try {
                    inet = InetAddress.getByName("10.0.142.9");
                    DatagramPacket dp = new DatagramPacket(data, data.length,inet,6666);

                    ds = new DatagramSocket();

                    ds.send(dp);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }}).start();

        //这个是接收的线程
        new Thread(new Runnable() {

            @Override
            public void run() {
                // 接受
                DatagramSocket ds;
                try {
                    ds = new DatagramSocket(6667);

                    byte[] bytes = new byte[1024 * 64];
                    boolean b = true;
                    while (b) {
                        DatagramPacket dp = new DatagramPacket(bytes, bytes.length);
                        ds.receive(dp);
                        // 拆包
                        // ip , 端口号 ， 数据
                        String ip = dp.getAddress().getHostAddress();
                        int port = dp.getPort();

                        int len = dp.getLength();
                        System.out.println(ip + " : " + port + "\t" + new String(bytes, 0, len));
                    }
                    // ds.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

```
- Receive

```
package com.atguigu.ct.producer.Test.AA;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/*
 * UDP接收端
 *
 * 1,创建DatagramSocket对象
 * 2,创建DatagramPacket对象
 * 3,接收数据存储到DatagramPacket对象中
 * 4,获取DatagramPacket对象的内容
 * 5,释放流资源
 */
public class UDPReceive {
    @SuppressWarnings("resource")
    public static void main(String[] args) throws Exception {
        //接收线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 接受
                DatagramSocket ds;
                try {
                    ds = new DatagramSocket(6666);


                    byte[] bytes = new byte[1024*64];
                    boolean b = true;
                    while(b){
                        DatagramPacket dp = new DatagramPacket(bytes, bytes.length);
                        ds.receive(dp);
                        // 拆包
                        //  ip , 端口号  ， 数据
                        String ip = dp.getAddress().getHostAddress();
                        int port = dp.getPort();

                        int len = dp.getLength();
                        System.out.println(ip +" : "+port +"\t"+ new String(bytes, 0, len));
                    }
                    //				ds.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //发送线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                Scanner sc = new Scanner(System.in);
                boolean b = true;
                DatagramSocket ds= null;
                while(b){
                    String next = sc.next();

                    byte[] data = next.getBytes();

                    InetAddress inet;
                    try {
                        inet = InetAddress.getByName("10.0.142.9");
                        DatagramPacket dp = new DatagramPacket(data, data.length,inet,6667);

                        ds = new DatagramSocket();

                        ds.send(dp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }
}
```

![](https://user-gold-cdn.xitu.io/2019/12/18/16f1830918b6006e?w=1506&h=327&f=png&s=54757)
一个就是用while实现了多个线程的数据互发，还有一个就是发送方的端口是随机的，如果不指定的话



## TCP通信
> TCP通信同UDP通信一样，都能实现两台计算机之间的通信，通信的两端都需要创建socket对象

> 区别在于，UDP中只有发送端和接收端，不区分客户端与服务器端，计算机之间可以任意地发送数据

> 而TCP通信是严格区分客户端与服务器端的，在通信时，必须先由客户端去连接服务器端才能实现通信，服务器端不可以主动连接客户端，并且服务器端程序需要事先启动，等待客户端的连接

> 在JDK中提供了两个类用于实现TCP程序，一个是ServerSocket类，用于表示服务器端，一个是Socket类，用于表示客户端

> 通信时，首先创建代表服务器端的ServerSocket对象，该对象相当于开启一个服务，并等待客户端的连接，然后创建代表客户端的Socket对象向服务器端发出连接请求，服务器端响应请求，两者建立连接开始通信


### ServerSocket

> 在开发TCP程序时，首先需要创建服务器端程序。JDK的java.net包中提供了一个ServerSocket类，该类的实例对象可以实现一个服务器段的程序。通过查阅API文档可知，ServerSocket类提供了多种构造方法，接下来就对ServerSocket的构造方法进行逐一地讲解

![image](https://user-gold-cdn.xitu.io/2019/12/18/16f18358d1bbc464?w=521&h=60&f=png&s=15166)

> 使用该构造方法在创建ServerSocket对象时，就可以将其绑定到一个指定的端口号上（参数port就是端口号）

> 接下来学习一下ServerSocket的常用方法，如表所示

![image](https://user-gold-cdn.xitu.io/2019/12/18/16f18358d54c4b7b?w=612&h=169&f=png&s=41099)

> ServerSocket对象负责监听某台计算机的某个端口号，在创建ServerSocket对象后，需要继续调用该对象的accept()方法，接收来自客户端的请求。当执行了accept()方法之后，服务器端程序会发生阻塞，直到客户端发出连接请求，accept()方法才会返回一个Scoket对象用于和客户端实现通信，程序才能继续向下执行

### Socket

> 讲解了ServerSocket对象可以实现服务端程序，但只实现服务器端程序还不能完成通信，此时还需要一个客户端程序与之交互，为此JDK提供了一个Socket类，用于实现TCP客户端程序

> Socket类同样提供了多种构造方法，接下来就对Socket的常用构造方法进行详细讲解

![image](https://user-gold-cdn.xitu.io/2019/12/18/16f1839f0ec27685?w=772&h=61&f=png&s=19412)

> 使用该构造方法在创建Socket对象时，会根据参数去连接在指定地址和端口上运行的服务器程序，其中参数host接收的是一个字符串类型的IP地址

![image](https://user-gold-cdn.xitu.io/2019/12/18/16f1839f1154eef1?w=813&h=75&f=png&s=21911)

> 该方法在使用上与第二个构造方法类似，参数address用于接收一个InetAddress类型的对象，该对象用于封装一个IP地址

> 在以上Socket的构造方法中，最常用的是第一个构造方法

> 接下来学习一下Socket的常用方法，如表所示
![image](https://user-gold-cdn.xitu.io/2019/12/18/16f183d2b5f54f0c?w=886&h=214&f=png&s=32317)

![image](https://user-gold-cdn.xitu.io/2019/12/18/16f183d2b897c0e2?w=866&h=210&f=png&s=35808)

> 在Socket类的常用方法中，getInputStream()和getOutStream()方法分别用于获取输入流和输出流。当客户端和服务端建立连接后，数据是以IO流的形式进行交互的，从而实现通信

> 接下来通过一张图来描述服务器端和客户端的数据传输，如下图所示

![image](https://user-gold-cdn.xitu.io/2019/12/18/16f183d2b8d848c6?w=668&h=223&f=png&s=52865)
### 简单的TCP网络程序

> 了解了ServerSocket、Socket类的基本用法，为了让大家更好地掌握这两个类的使用，接下来通过一个TCP通信的案例来进一步学习

> 要实现TCP通信需要创建一个服务器端程序和一个客户端程序，为了保证数据传输的安全性，首先需要实现服务器端程序

- TCP 服务器端

```
/*
 * TCP 服务器端
 *
 * 1,创建服务器ServerSocket对象（指定服务器端口号）
 * 2，开启服务器了，等待客户端的连接，当客户端连接后，可以获取到连接服务器的客户端Socket对象
 * 3,给客户端反馈信息
 * 4,关闭流资源
 */
public class TCPServer {
    public static void main(String[] args) throws IOException {
        //1,创建服务器ServerSocket对象（指定服务器端口号）
        ServerSocket ss = new ServerSocket(6789);
        //2，开启服务器了，等待客户端的连接，当客户端连接后，可以获取到连接服务器的客户端Socket对象
        Socket s = ss.accept();
        //3,给客户端反馈信息
        /*
         * a,获取客户端的输出流
         * b,在服务端端，通过客户端的输出流写数据给客户端
         */
        //a,获取客户端的输出流
        OutputStream out = s.getOutputStream();
        //b,在服务端端，通过客户端的输出流写数据给客户端
        out.write("你已经连接上了服务器".getBytes());
        //4,关闭流资源
        out.close();
        s.close();
        //ss.close();  服务器流 通常都是不关闭的
    }
}
```
-  TCP 客户端

```
/*
 * TCP 客户端
 *
 * 1，创建客户端Socket对象,（指定要连接的服务器地址与端口号）
 * 2,获取服务器端的反馈回来的信息
 * 3,关闭流资源
 */
public class TCPClient {
    public static void main(String[] args) throws IOException {
        //1，创建客户端Socket对象,（指定要连接的服务器地址与端口号）
        Socket s = new Socket("10.0.142.9", 6789);
        //2,获取服务器端的反馈回来的信息
        InputStream in = s.getInputStream();
        //获取获取流中的数据
        byte[] buffer = new byte[1024];
        //把流中的数据存储到数组中，并记录读取字节的个数
        int length = in.read(buffer);
        //显示数据
        System.out.println( new String(buffer, 0 , length) );
        //3,关闭流资源
        in.close();
        s.close();
    }
}
```
![](https://user-gold-cdn.xitu.io/2019/12/18/16f1843a9dc66e4e?w=1593&h=543&f=png&s=141306)
需要注意的是 首先必須开启服务端，不然会连不上的。


## IO流和网络编程综合练习（聊天室）
* 服务器

```
public class Server2 {
    Socket socket = null;
    boolean flag = true;
    public void run() throws Exception{
        //创建服务器socket对象
        ServerSocket ss = new ServerSocket(12343);
        while(flag){
            //获取socket
            socket = ss.accept();
            //写
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //获取输出流
                    try {
                        Scanner sc = new Scanner(System.in);
                        OutputStream os = socket.getOutputStream();
                        while(true){
                            String data = sc.nextLine();
                            os.write(data.getBytes());
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            //读
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 获取输入流
                    try {
                        InputStream is = socket.getInputStream();
                        //一行一行读，设置编码
                        BufferedReader bd = new BufferedReader(new InputStreamReader(is, "utf-8"));
                        String data = null;
                        while((data = bd.readLine())!= null){
                            //明确是谁发送过来的(获取ip)
                            System.out.println(socket.getInetAddress().getHostAddress()+"客户端发送到服务器 : "+data);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        ss.close();
    }

    public static void main(String[] args) throws Exception {
        Server2 server2 = new Server2();
        server2.run();
    }
}
```
- 客户端

```
public class Client2 {
    public static void main(String[] args) throws Exception {
        //创建客户端socket对象
        Socket socket = new Socket("127.0.0.1",12343);
        //获取输入流
        final InputStream is = socket.getInputStream();
        //一行一行读。设置编码格式
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true){
                        byte[] by = new byte[1024];
                        int len = is.read(by);
                        System.out.println("服务器返回数据 : "+new String(by,0,len));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //获取输出流
        OutputStream os = socket.getOutputStream();
        BufferedWriter bw =  new BufferedWriter(new OutputStreamWriter(os,"utf-8"));
        Scanner sc = new Scanner(System.in);
        boolean flag = true;
        while(flag){
            String data = sc.nextLine();
            bw.write(data);
            bw.newLine();
            bw.flush();
        }
        is.close();
        os.close();
        socket.close();
    }
}
```

![](https://user-gold-cdn.xitu.io/2019/12/18/16f186acacc73f90?w=552&h=149&f=png&s=18195)
![](https://user-gold-cdn.xitu.io/2019/12/18/16f186aa63f5bc4a?w=564&h=175&f=png&s=17406)

### 知识点总结

* IP地址：用来唯一表示我们自己的电脑的，是一个网络标示
* 端口号： 用来区别当前电脑中的应用程序的
* UDP: 传送速度快，但是容易丢数据，如视频聊天
* TCP: 传送稳定，不会丢失数据，如文件的上传、下载
* UDP程序交互的流程
    * 发送端
    ```
        1,创建DatagramSocket对象
        2，创建DatagramPacket对象，并封装数据
        3，发送数据
        4，释放流资源
    ```
    * 接收端
    ```
        1,创建DatagramSocket对象
        2,创建DatagramPacket对象
        3,接收数据存储到DatagramPacket对象中
        4,获取DatagramPacket对象的内容
        5,释放流资源
    ```

* TCP程序交互的流程     
    * 客户端
    ```
        1,创建客户端的Socket对象
        2,获取Socket的输出流对象
        3,写数据给服务器
        4,获取Socket的输入流对象
        5，使用输入流，读反馈信息
        6,关闭流资源
    ```
    * 服务器端
    ```
        1，创建服务器端ServerSocket对象，指定服务器端端口号
        2，开启服务器，等待着客户端Socket对象的连接，如有客户端连接，返回客户端的Socket对象
        3,通过客户端的Socket对象，获取客户端的输入流，为了实现获取客户端发来的数据
        4,通过客户端的输入流，获取流中的数据
        5,通过客户端的Socket对象，获取客户端的输出流，为了实现给客户端反馈信息
        6,通过客户端的输出流，写数据到流中
        7,关闭流资源

    ```
    
    
## 结尾
网络编程基础就讲这么多了，大家可以操作一下试试，下面的打算讲讲多线程,锁咯


## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
