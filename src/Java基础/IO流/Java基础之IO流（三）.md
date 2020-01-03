# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
昨天已经把字符流，字节流最基本的实现讲完了，今天我们就讲点高级的实现吧  
[🔥Java基础之I/O流（一）](https://juejin.im/post/5df700316fb9a016214ccf93)    
[🔥Java基础之I/O流（二）](https://juejin.im/post/5df74fed51882512243f9ab8)

## 转换流
顾名思义，就是可以把字符 字节的流相互转换 

### OutputStreamWriter类

OutputStreamWriter 是字符流通向字节流的桥梁：可使用指定的字符编码表，将要写入流中的字符编码成字节。它的作用的就是，将字符串按照指定的编码表转成字节，在使用字节流将这些字节写出去
![image](https://user-gold-cdn.xitu.io/2019/12/16/16f0ebcf5db6184f?w=417&h=142&f=png&s=27556)
* 我们来看看

```
    public static void writeC() throws Exception {
		//创建与文件关联的字节输出流对象
		FileOutputStream fos = new FileOutputStream("c:\\c.txt");
		//创建可以把字符转成字节的转换流对象，并指定编码
		OutputStreamWriter osw = new OutputStreamWriter(fos,"utf-8");
		//调用转换流，把文字写出去，其实是写到转换流的缓冲区中
		osw.write("六脉神剑");//写入缓冲区。
		osw.close();
	}

```


![](https://user-gold-cdn.xitu.io/2019/12/16/16f0ebfb7cbc04c7?w=1034&h=527&f=png&s=92149)

> OutputStreamWriter流对象，它到底如何把字符转成字节输出的呢

> 其实在OutputStreamWriter流中维护自己的缓冲区，当我们调用OutputStreamWriter对象的write方法时，会拿着字符到指定的码表中进行查询，把查到的字符编码值转成字节数存放到OutputStreamWriter缓冲区中。然后再调用刷新功能，或者关闭流，或者缓冲区存满后会把缓冲区中的字节数据使用字节流写到指定的文件中

### InputStreamReader类

> 它使用指定的字符编码表读取字节并将其解码为字符。它使用的字符集可以由名称指定或显式给定，或者可以接受平台默认的字符集

![image](https://user-gold-cdn.xitu.io/2019/12/16/16f0ec02a6407d38?w=388&h=147&f=png&s=26387)

* 试试看

```
  
    	public static void main(String[] args) throws IOException {
    		//演示字节转字符流的转换流
    		readC();
    	}
    	public static void readC() throws IOException{
    		//创建读取文件的字节流对象
    		InputStream in = new FileInputStream("E:\\c.txt");
    		//创建转换流对象 
    		//InputStreamReader isr = new InputStreamReader(in);这样创建对象，会用本地默认码表读取，将会发生错误解码的错误
            InputStreamReader isr = new InputStreamReader(in,"utf-8");
    		//使用转换流去读字节流中的字节
    		int ch = 0;
    		while((ch = isr.read())!=-1){
    			System.out.println((char)ch);
    		}
    		//关闭流
    		isr.close();
    	}
    

```

![](https://user-gold-cdn.xitu.io/2019/12/16/16f0ec2b9993f4fa?w=1241&h=768&f=png&s=116172)

如果是字节流 打印的就是字节了 就是不超过256的数字了,

注意：在读取指定的编码的文件时，一定要指定编码格式，否则就会发生解码错误，而发生乱码现象

## 缓冲流
读取数据量大的文件时，读取的速度会很慢，很影响我们程序的效率，那么，我想提高速度，怎么办？
Java中提高了一套缓冲流，它的存在，可提高IO流的读写速度,
缓冲流，根据流的分类分类字节缓冲流与字符缓冲流

### 字节缓冲流


字节缓冲流根据流的方向，共有2个

* 写入数据到流中，字节缓冲输出流 BufferedOutputStream
* 读取流中的数据，字节缓冲输入流 BufferedInputStream

> 它们的内部都包含了一个缓冲区，通过缓冲区读写，就可以提高了IO流的读写速度

### 字节缓冲输出流 BufferedOutputStream

> 通过字节缓冲流，进行文件的读写操作 写数据到文件的操作

* 构造方法
    * public BufferedOutputStream(OutputStream out)创建一个新的缓冲输出流，以将数据写入指定的底层输出流
     
    
     ```
     
        	public static void main(String[] args) throws IOException {
        		
        		//写数据到文件的方法
        		write();
        	}
        
        	/*
        	 * 写数据到文件的方法
        	 * 1,创建流
        	 * 2，写数据
        	 * 3，关闭流
        	 */
        	private static void write() throws IOException {
        		//创建基本的字节输出流
        		FileOutputStream fileOut = new FileOutputStream("abc.txt");
        		//使用高效的流，把基本的流进行封装，实现速度的提升
        		BufferedOutputStream out = new BufferedOutputStream(fileOut);
        		//2，写数据
        		out.write("hello".getBytes());
        		//3，关闭流
        		out.close();
        	}
        

    ```

![](https://user-gold-cdn.xitu.io/2019/12/17/16f11bcb84f23703?w=1713&h=755&f=png&s=171339)


### 字节缓冲输入流BufferedInputStream

* 构造方法
    * public BufferedInputStream(InputStream in)
    ```
        /*
    	 * 从文件中读取数据
    	 * 1,创建缓冲流对象
    	 * 2，读数据，打印
    	 * 3，关闭
    	 */
    	private static void read() throws IOException {
    		//1,创建缓冲流对象
    		FileInputStream fileIn = new FileInputStream("abc.txt");
    		//把基本的流包装成高效的流
    		BufferedInputStream in = new BufferedInputStream(fileIn);
    		//2，读数据
    		int ch = -1;
    		while ( (ch = in.read()) != -1 ) {
    			//打印
    			System.out.print((char)ch);
    		}
    		//3，关闭
    		in.close();
    	}

    ```
    ### 使用基本的流与高效的流完成复制文件
    
     * 方式1： 采用基本的流，一次一个字节的方式复制	共耗时 224613毫秒
     * 方式2： 采用基本的流，一个多个字节的方式赋值	共耗时     327毫秒
     * 方式3： 采用高效的流，一次一个字节的方式复制	共耗时    2047毫秒
     * 方式4： 采用高效的流，一个多个字节的方式赋值	共耗时      96毫秒
    
```
  /*
     * 需求：将d:\\test.avi文件进行复制
     * 		采用4种方式复制
     * 	
     * 
     * 数据源： d:\\test.avi
     * 目的地1: d:\\copy1.avi
     * 目的地2: d:\\copy2.avi
     * 目的地3: d:\\copy3.avi
     * 目的地4: d:\\copy4.avi
     * 
     * 实现的步骤：
     * 	1，指定数据源
     * 	2，指定目的地
     * 	3，读数据
     * 	4，写数据
     * 	5，关闭流
     * 
     */
    public class CopyAVI {
    	public static void main(String[] args) throws IOException {
    		//开始计时
    		long start = System.currentTimeMillis();
    		//方式1： 采用基本的流，一次一个字节的方式复制
    		//method1("d:\\test.avi", "d:\\copy1.avi");
    		//方式2： 采用基本的流，一个多个字节的方式赋值
    		//method2("d:\\test.avi", "d:\\copy2.avi");
    		//方式3： 采用高效的流，一次一个字节的方式复制
    		//method3("d:\\test.avi", "d:\\copy3.avi");
    		//方式4： 采用高效的流，一个多个字节的方式赋值
    		method4("d:\\test.avi", "d:\\copy4.avi");
    		
    		//结束计时
    		long end = System.currentTimeMillis();
    		//打印耗时多少毫秒
    		System.out.println("共耗时 " +(end - start)+ "毫秒");
    	}
    	
    	//方式4： 采用高效的流，一个多个字节的方式赋值
    	private static void method4(String src, String dest) throws IOException {
    		//1，指定数据源
    		BufferedInputStream in = new BufferedInputStream(new FileInputStream(src));
    		 //2，指定目的地
    		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest));
    		 //3，读数据
    		byte[] buffer = new byte[1024];
    		int len = -1;
    		while ( (len = in.read(buffer)) != -1) {
    			//4,写数据
    			out.write(buffer, 0, len);
    		}
    		 //5，关闭流
    		in.close();
    		out.close();
    	}
    
    	//方式3： 采用高效的流，一次一个字节的方式复制
    	private static void method3(String src, String dest) throws IOException {
    		//1，指定数据源
    		BufferedInputStream in = new BufferedInputStream(new FileInputStream(src));
    		 //2，指定目的地
    		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest));
    		 //3，读数据
    		int ch = -1;
    		while ((ch=in.read()) != -1) {
    			//4，写数据
    			out.write(ch);	
    		}		
    		 //5，关闭流
    		in.close();
    		out.close();
    	}
    
    	//方式2： 采用基本的流，一个多个字节的方式赋值
    	private static void method2(String src, String dest) throws IOException {
    		//1，指定数据源
    		FileInputStream in = new FileInputStream(src);
    		//2，指定目的地
    		FileOutputStream out = new FileOutputStream(dest);
    		//3，读数据
    		byte[] buffer = new byte[1024];
    		int len = -1;
    		while ( (len=in.read(buffer)) != -1) {
    			//4，写数据
    			out.write(buffer, 0, len);
    		}
    		//5，关闭流
    		in.close();
    		out.close();
    	}
    
    	//方式1： 采用基本的流，一次一个字节的方式复制
    	private static void method1(String src, String dest) throws IOException {
    		//1，指定数据源
    		FileInputStream in = new FileInputStream(src);
    		//2，指定目的地
    		FileOutputStream out = new FileOutputStream(dest);
    		//3，读数据
    		int ch = -1;
    		while (( ch=in.read()) != -1) {
    			//4，写数据
    			out.write(ch);
    		}
    		//5，关闭流
    		in.close();
    		out.close();
    	}
    }

```

### 字符缓冲流

* 字符缓冲输入流 BufferedReader
* 字符缓冲输出流 BufferedWriter

### 字符缓冲输出流BufferedWriter

> 将文本写入字符输出流，缓冲各个字符，从而提供单个字符、数组和字符串的高效写入

* 方法:
void newLine( ) 根据当前的系统，写入一个换行符

```
    /*
     * BufferedWriter 字符缓冲输出流
     * 方法
     * 	public void newLine()写入一个行分隔符
     * 
     * 需求： 通过缓冲输出流写入数据到文件
     * 分析：
     * 	1，创建流对象
     * 	2，写数据
     * 	3，关闭流
     * 
     */
    	public static void main(String[] args) throws IOException {
    		//创建流
    		//基本字符输出流
    		FileWriter fileOut = new FileWriter("file.txt");
    		//把基本的流进行包装
    		BufferedWriter out = new BufferedWriter(fileOut);
    		//2，写数据
    		for (int i=0; i<5; i++) {
    			out.write("hello");
    			out.newLine();
    		}
    		//3,关闭流
    		out.close();
    	}

```

### 字符缓冲输入流BufferedReader

> 从字符输入流中读取文本，缓冲各个字符，从而实现字符、数组和行的高效读取

* 方法
    * public String readLine() 读取一个文本行，包含该行内容的字符串，不包含任何行终止符，如果已到达流末尾，则返回 null

```
    /*
     * BufferedReader 字符缓冲输入流
     * 
     * 方法：
     * 	String readLine() 
     * 需求：从文件中读取数据，并显示数据
     */
   
    	public static void main(String[] args) throws IOException {
    		//1,创建流
    		BufferedReader in = new BufferedReader(new FileReader("file.txt"));
    		//2，读数据
    		//一次一个字符
    		//一次一个字符数组
    		//一次读取文本中一行的字符串内容
    		String line = null;
    		while( (line = in.readLine()) != null ){
    			System.out.println(line);
    		}
    		
    		//3,关闭流
    		in.close();
    	}
    

```

其实字符缓冲流 就是多了2个方法 一个是新起一行的 newLine(),还有一个是 readLine方法 读取一行的字符数据。（如果是字符流 其实返回的是字符要char()转换成String）

### 使用字符缓冲流完成文件的复制

```
    /*
     * 采用高效的字符缓冲流，完成文本文件的赋值
     * 
     * 数据源： file.txt
     * 目的地： copyFile.txt
     * 
     * 分析：
     * 	1，指定数据源， 是数据源中读数据，采用输入流
     * 	2,指定目的地，是把数据写入目的地，采用输出流
     * 	3,读数据
     * 	4，写数据
     * 	5，关闭流
     */
    public class CopyTextFile {
    	public static void main(String[] args) throws IOException {
    		//1，指定数据源， 是数据源中读数据，采用输入流
    		BufferedReader in = new BufferedReader(new FileReader("file.txt"));
    		//2,指定目的地，是把数据写入目的地，采用输出流
    		BufferedWriter out = new BufferedWriter(new FileWriter("copyFile.txt"));
    		//3,读数据
    		String line = null;
    		while ( (line = in.readLine()) != null ) {
    			//4，写数据
    			out.write(line);
    			//写入换行符号
    			out.newLine();
    		}
    		//5，关闭流
    		out.close();
    		in.close();
    	}
    }

```
## 流的操作规律
> IO流中对象很多，解决问题(处理设备上的数据时)到底该用哪个对象呢？

> 把IO流进行了规律的总结(四个明确)

* 明确一: 要操作的数据是数据源还是数据目的
    * 源 ： InputStreamReader
    * 目的 : OutputStreamWriter
    * 先根据需求明确要读还是要写

* 明确二: 要造作的数据是字节还是文本
    * 源
        * 字节 : InputStream
        * 文本 : Reader 
    * 目的
        * 字节 : OutputStream
        * 文本 : Writer
        * 已经明确到了具体的体系上

* 明确三: 明确数据所在的具体设备
    * 源设备:
        * 硬盘: 文件 File开头
        * 内存: 数组、字符串
        * 键盘: System.in
        * 网络: Socket
    * 目的设备
        * 硬盘 : 文件 File开头
        * 内存 ：数组，字符串
        * 屏幕 : System.out
        * 网络 ：Socket
        * 完全可以明确具体要使用哪个流对象

* 明确四: 是否需要额外功能呢?
    * 转换吗? 转换流. InputStreamReader OutputStreamWriter
    * 高效吗? 缓冲区对象。Buffered...

* InputStream
    * FileInputStream
    * BufferedInputStream

* OutputStream
    * FileOutputStream
    * BufferedOutputStream

* Writer
    * OutputStreamWriter
    * FileWriter
        * BufferedWriter

* Reader
    * InputStreamReader
    * FileReader
        * BufferedReader

---

## Java IO 总结

* 字节流
    * 字节输入流 InputStream
        * FileInputStream 操作文件的字节输入流
        * BufferedInputStream高效的字节输入流
        
* 字节输出流 OutputStream
    * FileOutputStream 操作文件的字节输出流
    * BufferedOutputStream 高效的字节输出流
	
* 字符流
    * 字符输入流 Reader
        * FileReader 操作文件的字符输入流
        * BufferedReader 高效的字符输入流
        * InputStreamReader 输入操作的转换流(把字节流封装成字符流)
	
    * 字符输出流 Writer
        * FileWriter 操作文件的字符输出流
        * ufferedWriter 高效的字符输出流
        * OutputStreamWriter 输出操作的转换流(把字节流封装成字符流)

* 方法：
    * 读数据方法：
        * read() 一次读一个字节或字符的方法
        * read(byte[]  char[]) 一次读一个数组数据的方法
        * readLine() 一次读一行字符串的方法(BufferedReader类特有方法)
        * readObject() 从流中读取对象(ObjectInputStream特有方法)
	
    * 写数据方法：
        * write(int) 一次写一个字节或字符到文件中
        * write(byte[] char[]) 一次写一个数组数据到文件中
        * write(String) 一次写一个字符串内容到文件中
        * writeObject(Object ) 写对象到流中(ObjectOutputStream类特有方法)
        * newLine() 写一个换行符号(BufferedWriter类特有方法)

* 向文件中写入数据的过程
    * 1，创建输出流对象
    * 2，写数据到文件
    * 3，关闭输出流
	
* 从文件中读数据的过程
    * 1，创建输入流对象
    * 2，从文件中读数据
    * 3，关闭输入流

* 文件复制的过程
    * 1，创建输入流（数据源）
    * 2，创建输出流（目的地）
    * 3，从输入流中读数据
    * 4，通过输出流，把数据写入目的地
    * 5，关闭流
    
## 结尾
Java 基础IO 讲完了，大家照着写一遍，想必对于简单的IO是没有问题的，既然我们说完了 磁盘的IO，接下来我们说说网络编程呗，这也是基础中比较重要的东西了。


[各种I/O对比](https://juejin.im/post/5dcbefb45188250d194507b7#heading-10)
## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**人才**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
