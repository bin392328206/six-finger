# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
为什么写I/O流呢，因为群里有一个群友问了I/O流方面的知识，然后我看了下，我去大脑一片空白，这时候才发现自己的基础是多么的弱，而且就算你要学NIO 你连IO都忘得差不多了，还怎么往下学，我现在的目的，很简单，一步步来，把Java基础打牢，后面的多线程 ，锁 ，JVM都会慢慢讲到的。希望喜欢我的关注我，也可以加群一起学习，虽然说并不一定能给你多大的帮助，但是至少可以给你一个天天学习的氛围吧。


##   I/O历史背景
“对语言设计人员来说，创建好的输入／输出系统是一项特别困难的任务。”      

无论是系统、还是语言的设计中IO的设计都是异常复杂的。面临的最大的挑战一般是如何覆盖所有可能的因素，我们不仅仅要考虑文件、控制台、网络、内存等不同的种类，而且要处理大量的不同的读取方式，如：顺序读取、随机读取，二进制读取、字符读取，按行读取、按字符读取……

 Linux是第一个将设备抽象为文件的操作系统，在Linux中所有的外部设备都可以用读取文件的方法读取，这样编程人员就可以以操作文件的方法操作任何设备。C++在IO方面也做了一些改进――引进了流的概念，我们可以通过cin、cout读写一些对象。Java语言在IO设计方面取得较大的成功，它是完全面向对象的，主要采用装饰器模式避免大量的类，包括了最大的可能性，提供了较好的扩展机制……
 
“Java库的设计者通过创建大量类来攻克这个难题。事实上，Java的IO系统采用了如此多的类，以致刚开始会产生不知从何处入手的感觉（具有讽刺意味的是，Java的IO设计初衷实际要求避免过多的类）。” 上面一段来自《Think in Java》，确实很多初学者刚刚学习java的IO时会比较茫然，不过等我们知道装饰器模式（Decorator）的用意、场景及其在Java的IO包中的使用，你可能会真正领会整个IO的FrameWork。

总结一句话：**计算机的本质就是 输入 处理 输出 这些东西于语言无关，语言只是工具，就好像你要去一个地方，你可以走路去，你也可以坐车，飞机。只不过目的一样。**

## IO概述


回想之前写过的程序，数据都是在内存中，一旦程序运行结束，这些数据都没有了，等下次再想使用这些数据，可是已经没有了。那怎么办呢？能不能把运算完的数据都保存下来，下次程序启动的时候，再把这些数据读出来继续使用呢？其实要把数据持久化存储，就需要把内存中的数据存储到内存以外的其他持久化设备(硬盘、光盘、U盘等)上


* 当需要把内存中的数据存储到持久化设备上这个动作称为输出（写）Output操作
* 当把持久设备上的数据读取到内存中的这个动作称为输入（读）Input操作
* 因此我们把这种输入和输出动作称为IO操作


## IO的分类
Java IO一般包含两个部分：  
- 1.java.io包中堵塞型IO；  
- 2.java.nio包中的非堵塞型IO，通常称为NewIO。

学过操作系统的朋友都知道系统运行的瓶颈一般在于IO操作，一般打开某个IO通道需要大量的时间，同时端口中不一定就有足够的数据，这样read方法就一直等待读取此端口的内容，从而浪费大量的系统资源。有人也许会提出使用java的多线程技术啊！但是在当前进程中创建线程也是要花费一定的时间和系统资源的，因此不一定可取。Java New IO的非堵塞技术主要采用了Observer模式，就是有一个具体的观察者和＝监测IO端口，如果有数据进入就会立即通知相应的应用程序。这样我们就避免建立多个线程，同时也避免了read等待的时间。不过本篇主要讲述java的堵塞型IO，就是我们通常应用的那个包。

Java的IO主要包含三个部分：
- 1.流式部分――IO的主体部分；
- 2.非流式部分――主要包含一些辅助流式部分的类，如：File类、RandomAccessFile类和FileDescriptor等类；
- 3.文件读取部分的与安全相关的类，如：SerializablePermission类。以及与本地操作系统相关的文件系统的类，如：FileSystem类和Win32FileSystem类和WinNTFileSystem类。
 
流式部分可以概括为：两个对应一个桥梁。两个对应指：
- 1.字节流（Byte Stream）和字符流（Char Stream）的对应；
- 2.输入和输出的对应。一个桥梁指：从字节流到字符流的桥梁。对应于输入和输出为InputStreamReader和OutputStreamWriter。

 在流的具体类中又可以具体分为：
 - 1.介质流（Media Stream或者称为原始流Raw Stream）――主要指一些基本的流，他们主要是从具体的介质上，如：文件、内存缓冲区（Byte数组、Char数组、StringBuffer对象）等，读取数据；
 - 2.过滤流（Filter Stream）――主要指所有FilterInputStream/FilterOutputStream和FilterReader/FilterWriter的子类，主要是对其包装的类进行某些特定的处理，如：缓存等。
 - 
 ## IO中的流
 流具有最基本的特点：“One dimension , one direction .” 即流是一维的，同时流是单向的。关于维和我们通常说的一维长度，二维平面，三维空间，四维时空……是同一个概念，流就是一维的。单向就是只可以一个方向（按顺序从头至尾依次）读取，不可以读到某个位置，再返回前面某个位置。流的概念和实际水流的概念基本一致，水只可以从高向低一个方向流动。我们某时在目地喝了一口水，下次在同一个地点喝水已经不是当时的那片水了。
 

## File类
我们先从file类 学起，file类是对文件夹的操作，它并不是操作具体的文件内容，还是操作文件夹和文件

### File类的构造函数

![image](https://user-gold-cdn.xitu.io/2019/12/16/16f0dbc8a3a04e2a?w=850&h=172&f=png&s=56160)
* 通过构造方法创建File对象

```
    public class FileDemo {
    	public static void main(String[] args) {
    		//File构造函数演示
    		String pathName = "e:\\test\\Hello.java";
    		File f1 = new File(pathName);//将Test22文件封装成File对象。注意；有可以封装不存在文件或者文件夹，变成对象。
    		System.out.println(f1);
    		
    		File f2 = new File("e:\\test","Hello.java");
    		System.out.println(f2);
    		
    		//将parent封装成file对象。
    		File dir = new File("e:\\test");
    		File f3 = new File(dir,"Hello.java");
    		System.out.println(f3);
    	}
    }
    
    
        //e:\test\Hello.java
        //e:\test\Hello.java
        //e:\test\Hello.java

```

### File类的获取

创建完了File对象之后，那么File类中都有如下常用方法，可以获取文件相关信息

![image](https://user-gold-cdn.xitu.io/2019/12/16/16f0dc415a0c4896?w=761&h=248&f=png&s=63877)

* 方法演示如下

```
    public class FileMethodDemo {
    	public static void main(String[] args) {
    		//创建文件对象
    		File file = new File("Test22.java");
    		//获取文件的绝对路径，即全路径
    		String absPath = file.getAbsolutePath();
    		//File中封装的路径是什么获取到的就是什么。
    		String path = file.getPath();
    		//获取文件名称
    		String filename = file.getName();
    		//获取文件大小
    		long size = file.length();
    		
    		System.out.println("absPath="+absPath);
    		System.out.println("path="+path);
    		System.out.println("filename="+filename);
    		System.out.println("size="+size);
    	}
    }
    
    //absPath=D:\code\BigData\designPattern\Test22.java
    //path=Test22.java
    //filename=Test22.java
    //size=0

```

### 文件和文件夹的创建与删除等

> 经常上面介绍，我们知道可以通过File获取到文件名称，文件路径(目录)等信息。
接下来演示使用File类创建、删除文件等操作

![image](https://user-gold-cdn.xitu.io/2019/12/16/16f0dc76a9c53571?w=911&h=288&f=png&s=68013)

* 下面来看看

```
    public class FileMethodDemo2 {
    	public static void main(String[] args) throws IOException {
    		// 对文件或者文件加进行操作。
    		File file = new File("e:\\file.txt");
    		// 创建文件，如果文件不存在，创建 true 。 如果文件存在，则不创建 false。 如果路径错误，IOException。
    		boolean b1 = file.createNewFile();
    		System.out.println("b1=" + b1);
    		//-----------删除文件操作-------注意：不去回收站。慎用------
    		 boolean b2 = file.delete();
    		 System.out.println("b2="+b2);
    
    		//-----------需要判断文件是否存在------------
    		 boolean b3 = file.exists();
    		 System.out.println("b3="+b3);
    
    		//-----------对目录操作 创建，删除，判断------------
    		File dir = new File("e:\\abc");
    		//mkdir()创建单个目录。//dir.mkdirs();创建多级目录
    		boolean b4 = dir.mkdir();
    		System.out.println("b4="+b4);
    		//删除目录时，如果目录中有内容，无法直接删除。
    		boolean b5 = dir.delete();
    		//只有将目录中的内容都删除后，保证该目录为空。这时这个目录才可以删除。
    		System.out.println("b5=" + b5);
    
    		//-----------判断文件，目录------------
    		File f = new File("e:\\javahaha");// 要判断是否是文件还是目录，必须先判断存在。
    		// f.mkdir();//f.createNewFile();
    		System.out.println(f.isFile());
    		System.out.println(f.isDirectory());
    	}
    }
    
                //b1=true
                //b2=true
                //b3=false
                //b4=true
                //b5=true
                //false
                //false


```

### listFile()方法
文件都存放在目录（文件夹）中，那么如何获取一个目录中的所有文件或者目录中的文件夹呢？那么我们先想想，一个目录中可能有多个文件或者文件夹，那么如果File中有功能获取到一个目录中的所有文件和文件夹，那么功能得到的结果要么是数组，要么是集合。
![image](https://user-gold-cdn.xitu.io/2019/12/16/16f0dd538d775dbc?w=835&h=93&f=png&s=29191)

* 方法如下
```
    public class FileMethodDemo3 {
    	public static void main(String[] args) {
    	    File dir = new File("e:\\java_code");
        File file1 = new File(dir, "aaa");
        file1.mkdirs();
        File file2 = new File(dir, "a.text");
        file2.createNewFile();
        dir.mkdirs();
        //获取的是目录下的当前的文件以及文件夹的名称。
        String[] names = dir.list();
        for(String name : names){
            System.out.println(name);
        }
        //获取目录下当前文件以及文件对象，只要拿到了文件对象，那么就可以获取其中想要的信息
        File[] files = dir.listFiles();
        for(File file : files){
            System.out.println(file);
        }

    	}
    }        
         //a.text
         //aaa
         //e:\java_code\a.text
         //e:\java_code\aaa
```
### 文件过滤器

通过listFiles()方法，我们可以获取到一个目录下的所有文件和文件夹，但能不能对其进行过滤呢？比如我们只想要一个目录下的指定扩展名的文件，或者包含某些关键字的文件夹呢

我们是可以先把一个目录下的所有文件和文件夹获取到，并遍历当前获取到所有内容，遍历过程中在进行筛选，但是这个动作有点麻烦，Java给我们提供相应的功能来解决这个问题

![image](https://user-gold-cdn.xitu.io/2019/12/16/16f0de08d82a44c2?w=812&h=48&f=png&s=17381)

![image](https://user-gold-cdn.xitu.io/2019/12/16/16f0de08d84b0e9c?w=826&h=147&f=png&s=47588)
* 测试类

```
    public class FileDemo2 {
    	public static void main(String[] args) {
    		//获取扩展名为.java所有文件
    		//创建File对象
    		File file = new File("E:\\test");
    		//获取指定扩展名的文件,由于要对所有文件进行扩展名筛选，因此调用方法需要传递过滤器
    		File[] files = file.listFiles(new MyFileFilter());
    		//遍历获取到的所有符合条件的文件
    		for (File f : files) {
    			System.out.println(f);
    		}
    	}
    }

```
* 自定类继承FilenameFilter过滤器接口

```
    //定义类实现文件名称FilenameFilter过滤器
    class MyFileFilter implements FilenameFilter{
    	public boolean accept(File dir, String name) {
    		return name.endsWith(".java");
    	}
    }

```
值得一提的是 我们要注意这个用法 只能是下一级目录，不能说是2级 就比如例子中的 必须要是test/下的，就是是test/a 下的a.java 也是找不到的

### 递归打印所有子目录中的文键路径
哈哈 这个是我们开始学文件的时候最经常学的一个东西了

```
 public static void main(String[] args) throws Exception {
        File file = new File("e:\\test");
        getFileAll(file);

    }
    
    public static void getFileAll(File file) {
        File[] files = file.listFiles();
        //遍历当前目录下的所有文件和文件夹
        for (File f : files) {
            //判断当前遍历到的是否为目录
            if(f.isDirectory()){
                //是目录，继续获取这个目录下的所有文件和文件夹
                getFileAll(f);
            }else{
                //不是目录，说明当前f就是文件，那么就打印出来
                System.out.println(f);
            }
        }
    }
    
    //e:\test\a.txt
    //e:\test\aa\a.java
    //e:\test\aa\a.txt
    //e:\test\b.java
    
```
### File总结
* File: 文件和目录路径名的抽象表示形式
    * 构造方法:
        * public File(String pathname) 通过给定的文件或文件夹的路径，来创建对应的File对象
        * public File(String parent, String child) 通过给定的父文件夹路径，与给定的文件名称或目录名称来创建对应的File对象
        * public File(File parent,  String child)通过给定的File对象的目录路径，与给定的文件夹名称或文件名称来创建对应的File对象
    
    * 路径的分类
        * 绝对路径, 带盘盘符
        ```
            E:\\work\\a.txt
        ```
        * 相对路径， 不带盘符
        ```
            work\\a.txt
        ```
        * 注意： 当指定一个文件路径的时候，如果采用的是相对路径，默认的目录为 项目的根目录
        
    * 方法
        * public boolean createNewFile()创建文件
            * 返回值为true， 说明创建文件成功
            * 返回值为false，说明文件已存在，创建文件失败
        * public boolean mkdir() 创建单层文件夹
            * 创建文件夹成功，返回 true
            * 创建文件夹失败，返回 false
        * public boolean mkdirs() 创建多层文件夹
        * public boolean delete()
            * 删除此抽象路径名表示的文件或目录
            * 如果此路径名表示一个目录，则该目录必须为空才能删除
        * public boolean isDirectory() 判断是否为文件夹
        * public boolean isFile() 判断是否为文件
        * public boolean exists() 判断File对象对应的文件或文件夹是否存在
        * public String getAbsolutePath() 获取当前File的绝对路径
        * public String getName() 获取当前File对象的文件或文件夹名称
        * public long length() 获取当前File对象的文件或文件夹的大小（字节）
	    * public File[] listFiles() 获取File所代表目录中所有文件或文件夹的绝对路径
	    
	    
## 结尾
好了，今天就讲一些I/O流的基础概念，在加上有个File类，明天继续干我们的流
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**人才**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
