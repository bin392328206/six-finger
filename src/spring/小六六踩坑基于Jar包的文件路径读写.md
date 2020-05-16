# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
说下背景吧，最近有一个需求就是说，把试卷的题目，动态的生成一个pdf,然后给学员去下载，因为很多学员会希望能有纸质版的做题目的需求。然后网上找些demo,也是把这个需求做完了，但是我当时是在本地，也就是idea的环境中跑，但是变成jar包之后，就又碰到很多坑，这边我记录一下。
- [SpringBoot+ItextPdf生成PDF+页眉+水印](https://juejin.im/post/5ea920886fb9a0437055ad8b)

## Jar包的文件原理
我们常常在代码中读取一些资源文件(比如图片，音乐，文本等等)。在单独运行的时候这些简单的处理当然不会有问题。但是如果我们把代码打成一个jar包以后，即使将资源文件一并打包，这些东西也找不出来了。想想怎么给大家讲清楚。给个例子吧

![](https://user-gold-cdn.xitu.io/2020/5/11/17201d5e25fc729b?w=469&h=336&f=png&s=14021)

大家可以看到这个资源文件夹下有一个fonts的目录，然后我们通过下面的代码，我们可以定位到文件的具体位置

```
  String path = freeMarkerUtil.getClass().getResource("/fonts").getPath();

```

这段代码的path的返回值是：

![](https://user-gold-cdn.xitu.io/2020/5/11/17201d83c0236e5e?w=1325&h=255&f=png&s=58721)

但是我们在代码里面 new File()的时候会报错，说找不到这个文件。但是我们在idea中是可以的，因为idea中是找到的target下的目录。但是打成jar包之后就不行。

因为".../ResourceJar.jar!/resource/...."并不是文件资源定位符的格式 (jar中资源有其专门的URL形式： jar:<url>!/{entry} )。所以，如果jar包中的类源代码用File f=new File(相对路径);的形式，是不可能定位到文件资源的。这也是为什么源代码1打包成jar文件后，调用jar包时会报出FileNotFoundException的症结所在了

## 结论&解决方案

其实结论 就是你不能去读一一个jar里面的文件路径，因为打包成jar之后的路径，其实并不是一个真实的路径了，但是我们可以用如下的方法，读到它的流的具体内容

```
 freeMarkerUtil.getClass().getResourceAsStream("/fonts")
```
然后我们可以把流写到操作系统的一个具体的位置，然后就可以再去读取到那个文件的路径，哈哈 是不是很傻瓜，其实我们可以直接把文件放到操作系统的一个固定的位置，但是这样做的一个不好的地方，如果下一个接手你的人，不熟悉这个代码，维护就会比较困难，但是如果是自己写入到一个地方，就是在代码里面实现了，就不用关心环境的变迁了。

下面是把一个文件里面的所有内容复制道另外一个文件中的代码

```
package pdf.kit;

import java.io.*;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/5/11 14:04
 */
public class IOTest {

    public static void main(String[] args)throws Exception {
        //原始文件地址
        File srcFolder = new File("D:\\fonts");
        //原始文件名
        String srcFloderName = srcFolder.getName();
        //要复制到的文件
        File desFolder = new File("E:", srcFloderName);
        //如果要复制到的文件没有这个文件夹，就创建一个
        if (!desFolder.exists()){
            desFolder.mkdir();
        }
        copyFolder(srcFolder,desFolder);

    }

    //复制文件夹
    private static void copyFolder(File srcFolder, File desFolder) throws IOException {
        //遍历原始文件夹里面的所有文件及文件夹
        File[] files = srcFolder.listFiles();
        for (File srcFile : files) {
            //如果是文件夹
            if (srcFile.isDirectory()){
                //在新的文件夹内创建一个和srcFile文件夹同名文件夹，然后再递归调用，判断文件夹里面的情况，然后做出相应处理
                String srcFileName = srcFile.getName();
                File newFolder = new File(desFolder, srcFileName);
                if (!newFolder.exists()){
                    newFolder.mkdir();
                    copyFolder(srcFile,newFolder);
                }
                //如果是文件
            }else {
                String srcFileName = srcFile.getName();
                File desFile = new File(desFolder, srcFileName);
                copyFile(srcFile,desFile);
            }

        }
    }

    private static void copyFile (File srcFile, File desFile) throws IOException {
        BufferedReader br = new BufferedReader((new InputStreamReader(new FileInputStream(srcFile),"utf-8")));

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(desFile),"utf-8"));
        char[] chars = new char[1024];
        int len;
        while ((len = br.read(chars)) != -1) {
            bw.write(len);
            bw.flush();
        }
        br.close();
        bw.close();
    }


}

```

## 结尾
其实就是一个踩坑的记录，这种小众的东西，写了只是为了给其他踩坑同行的时候搜索用的，随便自己记录一下，防止自己下次忘记，毕竟好记性不如烂笔头。


![](https://user-gold-cdn.xitu.io/2020/4/7/1715405b9c95d021?w=900&h=500&f=png&s=109836)

## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
