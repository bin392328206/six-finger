# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
昨天已经把文件的创建，文件夹的创建 删除等操作讲完了，接下来我们进入我们的正题，没有看过上一篇的可以去看看  
[🔥Java基础之I/O流（一）](https://juejin.im/post/5df700316fb9a016214ccf93)  
送大家几句我读书看到的话，哈哈 我反正看到了就分享一下 余秋雨的  
**人生的路靠自己一步步走去，真正能保护你的是你自己人格的选择和文化的选择，那么反过来，真正能伤害你的同样也是你自己的选择。**


## I/O 工作原理
这个在昨天的介绍中没讲，今天补一下，反正我讲东西，也是一边学一边写，看到了就写一下。





### 磁盘I/O
典型I/O读写磁盘工作原理如下：

![](https://user-gold-cdn.xitu.io/2019/12/16/16f0e21892e81607?w=484&h=485&f=png&s=89056)
> tips: DMA：全称叫直接内存存取（Direct Memory Access），是一种允许外围设备（硬件子系统）直接访问系统主内存的机制。基于 DMA 访问方式，系统主内存与硬件设备的数据传输可以省去CPU 的全程调度

特点
* 读写操作基于系统调用实现
* 读写操作经过用户缓冲区，内核缓冲区，应用进程并不能直接操作磁盘
* 应用进程读操作时需阻塞直到读取到数据

###  网络I/O

![](https://user-gold-cdn.xitu.io/2019/12/16/16f0e58531040b1a?w=482&h=487&f=png&s=91110)

![](https://user-gold-cdn.xitu.io/2019/12/16/16f0e58efb03057d?w=593&h=621&f=png&s=140875)

特点
* 网络I/O读写操作经过用户缓冲区，Sokcet缓冲区
* 服务端线程在从调用recvfrom开始到它返回有数据报准备好这段时间是阻塞的，recvfrom返回成功后，线程开始处理数据报


## Java I/O设计
Java中对数据流进行具体化和实现，关于Java数据流一般关注以下几个点：


- (1) 流的方向
从外部到程序，称为输入流；从程序到外部，称为输出流


- (2) 流的数据单位
程序以字节作为最小读写数据单元，称为字节流，以字符作为最小读写数据单元，称为字符流


- (3) 流的功能角色

![](https://user-gold-cdn.xitu.io/2019/12/16/16f0e5c1e65bf367?w=419&h=295&f=png&s=45957)
从/向一个特定的IO设备（如磁盘，网络）或者存储对象(如内存数组)读/写数据的流，称为节点流； 对一个已有流进行连接和封装，通过封装后的流来实现数据的读/写功能，称为处理流(或称为过滤流)；

## Java流类图结构：

![](https://user-gold-cdn.xitu.io/2019/12/16/16f0e60b2aab92e7?w=1872&h=850&f=png&s=322112)

### 字节流介绍：
处理字节数据的流对象。设备上的数据无论是图片或者dvd，文字，它们都以二进制存储的。二进制的最终都是以一个8位为数据单元进行体现，所以计算机中的最小数据单元就是字节。意味着，字节流可以处理设备上的所有数据，所以字节流一样可以处理字符数据，但是在处理字符上的速度不如字符流。

### 字符流介绍：
因为字符每个国家都不一样，所以涉及到了字符编码问题，那么GBK编码的中文用unicode编码解析是有问题的，所以需要获取中文字节数据的同时+ 指定的编码表才可以解析正确数据。为了方便于文字的解析，所以将字节流和编码表封装成对象，这个对象就是字符流。只要操作字符数据，优先考虑使用字符流体系。

流的体系因为功能不同，但是有共性内容，不断抽取，形成继承体系。该体系一共有四个基类，而且都是抽象类。：
字节流：InputStream OutputStream
字符流：Reader Writer

### 常见字节流：
InputStream：是表示字节输入流的所有类的超类。
- FileInputStream：从文件系统中的某个文件中获得输入字节。哪些文件可用取决于主机环境。FileInputStream    用于读取诸如图像数据之类的原始字节流。要读取字符流，请考虑使用 FileReader。        
- FilterInputStream：包含其他一些输入流，它将这些流用作其基本数据源，它可以直接传输数据或提供一些额外的功能。
- BufferedInputStream：该类实现缓冲的输入流。
- Stream：
- ObjectInputStream：
- PipedInputStream：

OutputStream：此抽象类是表示输出字节流的所有类的超类。
- FileOutputStream：文件输出流是用于将数据写入 File 或 FileDescriptor 的输出流。
- FilterOutputStream：此类是过滤输出流的所有类的超类。
- BufferedOutputStream：该类实现缓冲的输出流。
- PrintStream：
- DataOutputStream：
- ObjectOutputStream：
- PipedOutputStream：

### 常见字符流：
Reader：用于读取字符流的抽象类。子类必须实现的方法只有 read(char[], int, int) 和 close()。

- BufferedReader：从字符输入流中读取文本，缓冲各个字符，从而实现字符、数组和行的高效读取。 可以指定缓冲区的大小，或者可使用默认的大小。大多数情况下，默认值就足够大了。

- LineNumberReader：跟踪行号的缓冲字符输入流。此类定义了方法 setLineNumber(int) 和 getLineNumber()，它们可分别用于设置和获取当前行号。

- InputStreamReader：是字节流通向字符流的桥梁：它使用指定的 charset 读取字节并将其解码为字符。它使用的字符集可以由名称指定或显式给定，或者可以接受平台默认的字符集。

- FileReader：用来读取字符文件的便捷类。此类的构造方法假定默认字符编码和默认字节缓冲区大小都是适当的。要自己指定这些值，可以先在 FileInputStream 上构造一个 InputStreamReader。

- CharArrayReader：
- StringReader：

Writer：写入字符流的抽象类。子类必须实现的方法仅有 write(char[], int, int)、flush() 和 close()。

- BufferedWriter：将文本写入字符输出流，缓冲各个字符，从而提供单个字符、数组和字符串的高效写入。
- OutputStreamWriter：是字符流通向字节流的桥梁：可使用指定的 charset 将要写入流中的字符编码成字节。它使用的字符集可以由名称指定或显式给定，否则将接受平台默认的字符集。
- FileWriter：用来写入字符文件的便捷类。此类的构造方法假定默认字符编码和默认字节缓冲区大小都是可接受的。要自己指定这些值，可以先在 FileOutputStream 上构造一个 OutputStreamWriter。
- PrintWriter：
- CharArrayWriter：
- StringWriter：



## 字节流
在上一篇博客中，我们一直都是在操作文件或者文件夹，并没有给文件中写任何数据。现在我们就要开始给文件中写数据，或者读取文件中的数据
### 字节输出流OutputStream

OutputStream此抽象类，是表示输出字节流的所有类的超类。操作的数据都是字节，定义了输出字节流的基本共性功能方法

![](https://user-gold-cdn.xitu.io/2019/12/16/16f0e63f474f6289?w=1455&h=292&f=png&s=70931)
输出流中定义都是写write方法

![image](https://user-gold-cdn.xitu.io/2019/12/16/16f0e6447468a168?w=798&h=279&f=png&s=70740)
### FileOutputStream类
> OutputStream有很多子类，其中子类FileOutputStream可用来写入数据到文件

> FileOutputStream类，即文件输出流，是用于将数据写入 File的输出流
* 构造方法

![image](https://user-gold-cdn.xitu.io/2019/12/16/16f0e6ed8e085faa?w=763&h=128&f=png&s=43022)
例子
* 将数据写到文件中，代码演示

```
public static void main(String[] args) throws IOException {
    		//创建存储数据的文件。
    		File file = new File("E:\\file.txt");
    		//创建一个用于操作文件的字节输出流对象。一创建就必须明确数据存储目的地。
    		//输出流目的是文件，会自动创建。如果文件存在，则覆盖。
    		FileOutputStream fos = new FileOutputStream(file);
    		//调用父类中的write方法。
    		byte[] data = "abcde".getBytes();
    		fos.write(data);
    		//关闭流资源。
    		fos.close();
    	}
```
> 我们直接new FileOutputStream(file)这样创建对象，写入数据，会覆盖原有的文件，那么我们想在原有的文件中续写内容怎么办呢？
* 构造方法
![image](https://user-gold-cdn.xitu.io/2019/12/16/16f0e7de4b8eee51?w=761&h=122&f=png&s=45029)

例子 给文件中续写数据和换

```
public static void main(String[] args) throws Exception {
    		File file = new File("E:\\file.txt");
    		FileOutputStream fos = new FileOutputStream(file, true);
    		String str = "\r\n"+"HelloIO";
    		fos.write(str.getBytes());
    		fos.close();
    	}
```

![](https://user-gold-cdn.xitu.io/2019/12/16/16f0e7f9041f5e9e?w=1035&h=525&f=png&s=57273)

### 字节输入流InputStream
InputStream此抽象类，是表示字节输入流的所有类的超类。，定义了字节输入流的基本共性功能方法
![image](https://user-gold-cdn.xitu.io/2019/12/16/16f0e871bcf897f9?w=793&h=117&f=png&s=29511)
* int read():读取一个字节并返回，没有字节返回-1
* int read(byte[]): 读取一定量的字节数，并存储到字节数组中，返回读取到的字节数
#### FIleInputStream 类

> InputStream有很多子类，其中子类FileInputStream可用来读取文件内容

> FileInputStream 从文件系统中的某个文件中获得输入字节


* 构造方法

![image](https://user-gold-cdn.xitu.io/2019/12/16/16f0e889c748652a?w=841&h=88&f=png&s=30368)
> 在读取文件中的数据时，调用read方法，实现从文件中读取数据 如果返回的是-1，说明流已经读完了

![image](https://user-gold-cdn.xitu.io/2019/12/16/16f0e8a2adec4e91?w=769&h=61&f=png&s=12398)

例子
```
    public class FileInputStreamDemo {
    	public static void main(String[] args) throws IOException {
    		File file = new File("E:\\file.txt");
    		//创建一个字节输入流对象,必须明确数据源，其实就是创建字节读取流和数据源相关联。
    		FileInputStream fis = new FileInputStream(file);
    		//读取数据。使用 read();一次读一个字节。
    		int ch = 0;
    		while((ch=fis.read())!=-1){
    			System.out.println("ch="+(char)ch);		
    		}
    		// 关闭资源。
    		fis.close();
    	}
    }

```
读取数据read(byte[])方法
> 在读取文件中的数据时，调用read方法，每次只能读取一个，太麻烦了，于是我们可以定义数组作为临时的存储容器，这时可以调用重载的read方法，一次可以读取多个字符，返回读取到的位置。如果是-1说明读完了

![image](https://user-gold-cdn.xitu.io/2019/12/16/16f0e9257854a0da?w=790&h=63&f=png&s=16050)

```
    public class FileInputStreamDemo2 {
    	public static void main(String[] args) throws IOException {
    		/*
    		 * 演示第二个读取方法， read(byte[]);
    		 */
    		File file = new File("E:\\file.txt");
    		// 创建一个字节输入流对象,必须明确数据源，其实就是创建字节读取流和数据源相关联。
    		FileInputStream fis = new FileInputStream(file);		
    		//创建一个字节数组。
    		byte[] buf = new byte[1024];//长度可以定义成1024的整数倍。		
    		int len = 0;
    		while((len=fis.read(buf))!=-1){
    			System.out.println(new String(buf,0,len));
    		}
    		fis.close();
    	}
    }

```

来个例子，用字节流复制图片

```
    public static void main(String[] args) throws Exception{
        //1,明确源和目的。
        File srcFile = new File("E:\\vuepress\\myblog\\docs\\.vuepress\\public\\images\\66.ico");
        File destFile = new File("E:\\vuepress\\myblog\\docs\\.vuepress\\public\\images\\66copy1.ico");

        //2,明确字节流 输入流和源相关联，输出流和目的关联。
        FileInputStream fis = new FileInputStream(srcFile);
        FileOutputStream fos = new FileOutputStream(destFile);

        //3, 使用输入流的读取方法读取字节，并将字节写入到目的中。
        //定义一个缓冲区。
        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = fis.read(buf)) != -1) {
            fos.write(buf, 0, len);// 将数组中的指定长度的数据写入到输出流中。
        }
        //4,关闭资源。
        fos.close();
        fis.close();
    }
```

![](https://user-gold-cdn.xitu.io/2019/12/16/16f0e9de59471a49?w=1376&h=842&f=png&s=270925)



## 字符流
在操作过程中字节流可以操作所有数据，可是当我们操作的文件中有中文字符，并且需要对中文字符做出处理时怎么办呢? 
我们读取中文打印再控制台的时候是一个个字节 一个字节是-127 到128的，那如果我们要直接打印中文的话，就需要用到字符流了


### 字符输入流Reader


> 我们读取拥有中文的文件时，使用的字节流在读取，那么我们读取到的都是一个一个字节。只要把这些字节去查阅对应的编码表，就能够得到与之对应的字符。API中是否给我们已经提供了读取相应字符的功能流对象，Reader，读取字符流的抽象超类

![image](https://user-gold-cdn.xitu.io/2019/12/16/16f0ea579ec89a60?w=608&h=128&f=png&s=17039)

* read():读取单个字符并返回
* read(char[]):将数据读取到数组中，并返回读取的个数
* 
### FileReader类

> 用于读取诸如图像数据之类的原始字节流。要读取字符流，请考虑使用 FileReader

> 打开FileReader的API介绍。用来读取字符文件的便捷类。此类的构造方法假定默认字符编码和默认字节缓冲区大小都是适当的

* 构造方法

![image](https://user-gold-cdn.xitu.io/2019/12/16/16f0ea80e9b97a17?w=770&h=116&f=png&s=38783)

使用FileReader读取包含中文的文件

```
 
    	public static void main(String[] args) throws IOException {
    		//给文件中写中文
    		writeText();
    		//读取文件中的中文
    		readText();
    	}	
    	//读取中文
    	public static void readText() throws IOException {
    		FileReader fr = new FileReader("E:\\test\\a.txt");
    		int ch = 0;
    		while((ch = fr.read())!=-1){
    			//输出的字符对应的编码值
    			System.out.println(ch);
    			//输出字符本身
    			System.out.println((char)ch);
    		}
    	}
    	//写中文
    	public static void writeText() throws IOException {
    		FileOutputStream fos = new FileOutputStream("E:\\test\\a.txt");
    		fos.write("林子很大".getBytes());
    		fos.close();
    	}
    
            //26519
            //林
            //23376
            //子
            //24456
            //很
            //22823
            //大
```
### 字符输入流Writer


> 既然有专门用于读取字符的流对象，那么肯定也有写的字符流对象

![image](https://user-gold-cdn.xitu.io/2019/12/16/16f0ead027b5f0a1?w=512&h=304&f=png&s=53122)

### FileWriter类

* 构造方法

![image](https://user-gold-cdn.xitu.io/2019/12/16/16f0eae60d3e33c2?w=850&h=210&f=png&s=60611)

* 写入字符到文件中，先进行流的刷新，再进行流的关闭
```
    public class FileWriterDemo {
    	public static void main(String[] args) throws IOException {
    		//演示FileWriter 用于操作文件的便捷类。
    		FileWriter fw = new FileWriter("E：\\text\\fw.txt");
    		fw.write("你好,六脉神剑");//这些文字都要先编码。都写入到了流的缓冲区中。
    		fw.flush();//刷新
    		fw.close();// 关闭之前需要刷新它
    	}
    }

```
> flush():将流中的缓冲区缓冲的数据刷新到目的地中，刷新后，流还可以继续使用

> close():关闭资源，但在关闭前会将缓冲区中的数据先刷新到目的地，否则丢失数据，然后在关闭流。流不可以使用。如果写入数据多，一定要一边写一边刷新，最后一次可以不刷新，由close完成刷新并关闭




## 结尾
好了，就讲了最最最基本的字符流 和字节流，区别就是不管是什么都可以用字节流进行I/O操作，但是字符不行，如果是图片最好不要用字符流。明天我们继续讲讲其他的流，其实我这都是最基础的东西，下面的链接讲的高效的I/O方式，有兴趣的可以看看，还不错哦。明天继续讲讲 转换流 和缓冲流，这样Java IO 差不多了，但是还有NIO后面再讲吧。


[各种I/O对比](https://juejin.im/post/5dcbefb45188250d194507b7#heading-10)
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**人才**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
