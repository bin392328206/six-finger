# 前言
>文本已收录至我的GitHub仓库，欢迎Star：https://github.com/bin392328206/six-finger                             
> **种一棵树最好的时间是十年前，其次是现在**   
>我知道很多人不玩**qq**了,但是怀旧一下,欢迎加入六脉神剑Java菜鸟学习群，群聊号码：**549684836** 鼓励大家在技术的路上写博客
## 絮叨 
哈哈，有没有发现小六六总是写些用的很少的东西，没办法，这种东西都是用到的时候，百度踩坑，然后怼出来的，如果不写点文章记录下来，那下次又得重新怼一遍前面的过程，所以小六六偷点懒，记录一下，下次就简单了，说说场景吧，小六六目前负责的系统有一个题库的需求，就是要求把学生做错的题目生成一个pdf,或者是历年的真题生成一个pdf，然后给到用户下载，去用笔写，我本来想，这种需求不应该是运营教务那边直接上传就好了，但是他们不干，非得让我们代码生成，然后产品小姐姐又被他们说动了，然后最后只好接了，不过我绝对不是屈服在小姐姐的美色之下，我只是单纯的觉得这个需求又意思。哈哈



## itext介绍
iText是著名的开放源码的站点sourceforge一个项目，是用于生成PDF文档的一个java类库。通过iText不仅可以生成PDF或rtf的文档，而且可以将XML、Html文件转化为PDF文件。

- [Itext官网](https://itextpdf.com/en)


其实这边我说下又2种方式生成pdf
- 可以用原生的api,去创建pdf 所需要的每一个元素，这种也是一种实现方式，但是怎么说呢？就是你必须学他的api,有点多，哈哈
- 可以直接用itextpdf 这个jar,这样就可以把html直接变成pdf，那我们只需要用一个模板引擎，然后把他渲染，然后拿到这个页面，就可以生成我们需要的pdf了，下面的文章也是用的第二种方式，然后我把大致的过程记录一下



### 引入依赖

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.github.superad.pdf.kit</groupId>
    <artifactId>pdf-kit</artifactId>
    <packaging>jar</packaging>
    <name>${project.artifactId}</name>
    <version>1.1-RELEASE</version>

    <properties>
        <java.src.version>1.7</java.src.version>
        <java.target.version>1.7</java.target.version>
        <project.encoding>UTF-8</project.encoding>
    </properties>

    <parent>
        <groupId>org.sonatype.oss</groupId>
        <artifactId>oss-parent</artifactId>
        <version>7</version>
    </parent>

    <dependencies>
        <!--pdf生成工具类-->
        <!--pdf生成 itext-->
        <dependency>
            <groupId>com.itextpdf</groupId>
            <artifactId>itextpdf</artifactId>
            <version>5.4.2</version>
        </dependency>
        <dependency>
            <groupId>com.itextpdf.tool</groupId>
            <artifactId>xmlworker</artifactId>
            <version>5.4.1</version>
        </dependency>
        <dependency>
            <groupId>com.itextpdf</groupId>
            <artifactId>itext-asian</artifactId>
            <version>5.2.0</version>
        </dependency>
        <dependency>
            <groupId>org.xhtmlrenderer</groupId>
            <artifactId>flying-saucer-pdf</artifactId>
            <version>9.0.3</version>
        </dependency>
        <!--freemarker-->
        <dependency>
            <groupId>org.freemarker</groupId>
            <artifactId>freemarker</artifactId>
            <version>2.3.26-incubating</version>
        </dependency>
        <!--jfreechart-->
        <dependency>
            <groupId>jfree</groupId>
            <artifactId>jfreechart</artifactId>
            <version>1.0.2</version>
        </dependency>

        <!--log-->
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-core</artifactId>
            <version>1.0.13</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.0.13</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-access</artifactId>
            <version>1.0.13</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.7.5</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>log4j-over-slf4j</artifactId>
            <version>1.7.21</version>
        </dependency>

        <!--util-->
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>20.0</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.14.8</version>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-io</artifactId>
            <version>1.3.2</version>
        </dependency>
        <dependency>
            <groupId>commons-lang</groupId>
            <artifactId>commons-lang</artifactId>
            <version>2.6</version>
        </dependency>
        <!--servlet-->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>servlet-api</artifactId>
            <version>2.5</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>


        <!--增加的配置,过滤ttf文件的匹配-->
    <profiles>
        <profile>
            <id>release</id>
            <build>
                <resources>
                    <resource>
                        <directory>src/main/resources</directory>
                        <filtering>true</filtering>
                    </resource>
                </resources>
            </build>
            <distributionManagement>
                <snapshotRepository>
                    <id>oss</id>
                    <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
                </snapshotRepository>
                <repository>
                    <id>oss</id>
                    <url>https://oss.sonatype.org/service/local/staging/deploy/maven2</url>
                </repository>
            </distributionManagement>
        </profile>
    </profiles>


    <licenses>
        <license>
            <name>The Apache Software License, Version 2.0</name>
            <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
            <distribution>repo</distribution>
        </license>
    </licenses>
    <scm>
        <tag>alpha</tag>
        <url>https://github.com/superad/pdf-kit.git</url>
        <connection>scm:git:https://github.com/superad/pdf-kit.git</connection>
        <developerConnection>scm:git:https://github.com/superad/pdf-kit.git</developerConnection>
    </scm>



    <developers>
        <developer>
            <name>fangguangming</name>
            <email>563508194@qq.com</email>
        </developer>
    </developers>




</project>

```


## 启动类

```
package pdf.kit;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import pdf.kit.component.PDFHeaderFooter;
import pdf.kit.component.PDFKit;
import pdf.kit.component.chart.ScatterPlotChart;
import pdf.kit.component.chart.model.XYLine;
import pdf.kit.component.chart.impl.DefaultLineChart;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 360报告
 *
 */
@Slf4j
public class ReportKit360 {

    public static List<XYLine> getTemperatureLineList() {
        List<XYLine> list= Lists.newArrayList();
        for(int i=1;i<=7;i++){
            XYLine line=new XYLine();
            float random=Math.round(Math.random()*10);
            line.setXValue("星期"+i);
            line.setYValue(20+random);
            line.setGroupName("下周");
            list.add(line);
        }
        for(int i=1;i<=7;i++){
            XYLine line=new XYLine();
            float random=Math.round(Math.random()*10);
            line.setXValue("星期"+i);
            line.setYValue(20+random);
            line.setGroupName("这周");
            list.add(line);
        }
        return list;
    }

    public  String createPDF(String templatePath,Object data, String fileName){
        //pdf保存路径
        try {
            //设置自定义PDF页眉页脚工具类
            PDFHeaderFooter headerFooter=new PDFHeaderFooter();
            PDFKit kit=new PDFKit();
            kit.setHeaderFooterBuilder(headerFooter);
            //设置输出路径
            kit.setSaveFilePath("./hello.pdf");

            String saveFilePath=kit.exportToFile(fileName,data);
            return  saveFilePath;
        } catch (Exception e) {
            log.error("PDF生成失败{}", ExceptionUtils.getFullStackTrace(e));
            return null;
        }

    }

    public static void main(String[] args) {

        ReportKit360 kit=new ReportKit360();
        TemplateBO templateBO=new TemplateBO();
        templateBO.setTemplateName("考前-真题+考前");
        templateBO.setFreeMarkerUrl("A 正确");
        templateBO.setITEXTUrl("我们去了猴山，那里有很多的猴子，都是红屁股，有的在玩荡秋千，有的在避雨，有的在自得其乐地挠痒痒............真可爱。还有一只最威武的猴王，它站在最高的山头上，一动也不动地站着，它在看着这一群调皮的小猴子们，不让它们受到大猴子们的欺负 在动物园里，我还看到了很多别的动物。比如说活泼可爱的小熊猫、大熊猫;漂亮大方的丹顶鹤、孔雀;威风凛凛的狮子、老虎;光滑无比的海狮、海豹;还有让人害怕的鳄鱼;庞然大物般的大象;懒惰的黑熊;凶狠的狼和金钱豹;有着美丽花纹的长颈鹿、梅花鹿、斑马等等............这些动物我都很喜欢哦!0");
        templateBO.setJFreeChartUrl("B 错误");
        List<String> scores=new ArrayList<String>();
        scores.add("90");
        scores.add("95");
        scores.add("98");
        templateBO.setScores(scores);
//        List<XYLine> lineList=getTemperatureLineList();
//        DefaultLineChart lineChart=new DefaultLineChart();
//        lineChart.setHeight(500);
//        lineChart.setWidth(300);
//        String picUrl=lineChart.draw(lineList,0);
//        templateBO.setPicUrl(picUrl);
//
//        //散点图
//        String scatterUrl=ScatterPlotChart.draw(ScatterPlotChartTest.getData(),1,"他评得分(%)","自评得分(%)");
//        templateBO.setScatterUrl(scatterUrl);
        String templatePath="E:\\code\\pdf-kit\\src\\test\\resources\\templates";
        String path= kit.createPDF(templatePath,templateBO,"hello.pdf");
        System.out.println(path);



    }


}
```

小六六说下思路，其实很简单，就是从数据库把数据查出来，渲染到模板引擎上，最后拿到那个渲染之后的静态页面，然后把他转成pdf,然后传到oss,返回一个url 给前端



## 模板

```
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="Content-Style-Type" content="text/css"/>
    <title></title>
    <style type="text/css">
        body {
            font-family: pingfang sc light;
        }
        .center{
            text-align: center;
            width: 100%;
        }
    </style>
</head>
<body>
<!--第一页开始-->
<div class="page" >
    <div class="center"><p>${templateName}</p></div>
    <div><p>${ITEXTUrl}</p></div>
    <div>
        <ul>
            <li>${freeMarkerUrl}</li>
            <li>${JFreeChartUrl}</li>
        </ul>
    </div>

</div>
<!--第一页结束-->
<!---分页标记-->
<span style="page-break-after:always;"></span>
<!--第二页开始-->
<div class="page">
    <div>第二页开始了</div>
    <div>列表值:</div>
    <div>
    <#list scores as item>
        <div><p>${item}</p></div>
    </#list>
    </div>

</div>
<span style="page-break-after:always;"></span>
<div class="page">

    aaa
</div>


<!--第二页结束-->
</body>
</html>
```

### pdf生成类

```
package pdf.kit.component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.*;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.xhtmlrenderer.pdf.ITextRenderer;
import pdf.kit.component.builder.HeaderFooterBuilder;
import pdf.kit.component.builder.PDFBuilder;
import pdf.kit.exception.PDFException;
import pdf.kit.util.CustomEvent;
import pdf.kit.util.FreeMarkerUtil;
import pdf.kit.util.PDFBuilder1;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;

@Slf4j
public class PDFKit {

    //PDF页眉、页脚定制工具
    private HeaderFooterBuilder headerFooterBuilder;
    private String saveFilePath;

    /**
     * @description     导出pdf到文件
     * @param fileName  输出PDF文件名
     * @param data      模板所需要的数据
     *
     */
    public String exportToFile(String fileName,Object data){

        String htmlData= FreeMarkerUtil.getContent(fileName, data);
        if(StringUtils.isEmpty(saveFilePath)){
            saveFilePath=getDefaultSavePath(fileName);
        }
        File file=new File(saveFilePath);
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        FileOutputStream outputStream=null;
        try{
            //设置输出路径
            outputStream=new FileOutputStream(saveFilePath);
            //设置文档大小
            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);



            //设置页眉页脚
            PDFBuilder1 builder = new PDFBuilder1();
            writer.setPageEvent(builder);
            writer.setPageEvent(new CustomEvent("www.hqjy.com"));

            //输出为PDF文件
            convertToPDF(writer,document,htmlData);
        }catch(Exception ex){
            throw new PDFException("PDF export to File fail",ex);
        }finally{
            IOUtils.closeQuietly(outputStream);
        }
        return saveFilePath;

    }




    /**
     * 生成PDF到输出流中（ServletOutputStream用于下载PDF）
     * @param ftlPath ftl模板文件的路径（不含文件名）
     * @param data 输入到FTL中的数据
     * @param response HttpServletResponse
     * @return
     */
    public  OutputStream exportToResponse(String ftlPath,Object data,
                                                     HttpServletResponse response){

        String html= FreeMarkerUtil.getContent(ftlPath,data);

        try{
            OutputStream out = null;
            ITextRenderer render = null;
            out = response.getOutputStream();
            //设置文档大小
            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, out);
            //设置页眉页脚
            PDFBuilder builder = new PDFBuilder(headerFooterBuilder,data);
            writer.setPageEvent(builder);
            //输出为PDF文件
            convertToPDF(writer,document,html);
            return out;
        }catch (Exception ex){
            throw  new PDFException("PDF export to response fail",ex);
        }

    }

    /**
     * @description PDF文件生成
     */
    private  void convertToPDF(PdfWriter writer,Document document,String htmlString) throws Exception{
        //获取字体路径
        String fontPath=getFontPath();
        document.open();
        try {
            XMLWorkerHelper.getInstance().parseXHtml(writer,document,
                    new ByteArrayInputStream(htmlString.getBytes()),
                    XMLWorkerHelper.class.getResourceAsStream("/default.css"),
                    Charset.forName("UTF-8"),new XMLWorkerFontProvider(fontPath));
        } catch (IOException e) {
            e.printStackTrace();
            throw new PDFException("PDF文件生成异常",e);
        }finally {
            document.close();
        }

    }
    /**
     * @description 创建默认保存路径
     */
    private  String  getDefaultSavePath(String fileName){
        String classpath=PDFKit.class.getClassLoader().getResource("").getPath();
        String saveFilePath=classpath+"pdf/"+fileName;
        File f=new File(saveFilePath);
        if(!f.getParentFile().exists()){
            f.mkdirs();
        }
        return saveFilePath;
    }

    /**
     * @description 获取字体设置路径
     */
    public static String getFontPath() {
        String classpath=PDFKit.class.getClassLoader().getResource("").getPath();
        String fontpath=classpath+"fonts";
        return fontpath;
    }

    public HeaderFooterBuilder getHeaderFooterBuilder() {
        return headerFooterBuilder;
    }

    public void setHeaderFooterBuilder(HeaderFooterBuilder headerFooterBuilder) {
        this.headerFooterBuilder = headerFooterBuilder;
    }
    public String getSaveFilePath() {
        return saveFilePath;
    }

    public void setSaveFilePath(String saveFilePath) {
        this.saveFilePath = saveFilePath;
    }




}
```

### 设置页眉和页脚 +水印


![](https://user-gold-cdn.xitu.io/2020/4/29/171c4c58f151772e?w=620&h=169&f=png&s=13406)


```
package pdf.kit.util;


import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.*;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/4/29 11:14
 */
 public class CustomEvent extends PdfPageEventHelper {

    private String waterMark;

    public CustomEvent(String waterMark) {
        this.waterMark = waterMark;
    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        try {
            // 加入水印
            PdfContentByte waterMar = writer.getDirectContentUnder();
            // 开始设置水印
            waterMar.beginText();
            // 设置水印透明度
            PdfGState gs = new PdfGState();
            // 设置填充字体不透明度为0.2f
            gs.setFillOpacity(0.1f);
            // 设置水印字体参数及大小
            BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
                    BaseFont.EMBEDDED);
            waterMar.setFontAndSize(baseFont,60);
            // 设置透明度
            waterMar.setGState(gs);
            // 设置水印对齐方式 水印内容 X坐标 Y坐标 旋转角度
            waterMar.showTextAligned(Element.ALIGN_CENTER, waterMark , 300, 500, 45);
            //结束设置
            waterMar.endText();
            waterMar.stroke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

```


```
package pdf.kit.util;

/**
 * @author 小六六
 * @version 1.0
 * @date 2020/4/29 10:35
 */
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.IOException;

public class PDFBuilder1 extends PdfPageEventHelper {

    private Phrase leftHeader;
    private Phrase rigntHeader;

    public static final int marginX = 90;
    public static final int marginY = 70;

    private static BaseFont baseFont;
    // 生成下划线空白占位符
    private static String Blank;
    // 页眉字体
    private static Font font;
    // 下划线字体
    private static Phrase blankPhrase;

    public PDFBuilder1() {
        //this.leftHeader = new Phrase(header[0], PDFBuilder.font);
        //this.rigntHeader = new Phrase(header[1], PDFBuilder.font);
    }

    static {
        try {
            // 中文字体依赖itext得itext-asian包
            baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 210; i++) {
                sb.append("\u00a0");
            }
            Blank = sb.toString();
            font = new Font(PDFBuilder1.baseFont, 16, Font.UNDEFINED);
            blankPhrase = new Phrase(PDFBuilder1.Blank, new Font(PDFBuilder1.baseFont, Font.DEFAULTSIZE, Font.UNDERLINE));
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param writer
     * @param document
     */
    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        int yMargin = -20;
        float top = document.top(yMargin);
        float bottom=document.bottom(-10);
        // 第一页不生成页眉页脚
        if (document.getPageNumber() == 1) {
            return;
        }
        //生成下划线，使用空格占位
        ColumnText.showTextAligned(writer.getDirectContent(),
                Element.ALIGN_LEFT, PDFBuilder1.blankPhrase,
                document.left(-1), top, 0);
//        //生成左侧页眉
//        ColumnText.showTextAligned(writer.getDirectContent(),
//                Element.ALIGN_LEFT, leftHeader,
//                document.left(), top, 0);
//        //生成右侧页眉
//        ColumnText.showTextAligned(writer.getDirectContent(),
//                Element.ALIGN_RIGHT, rigntHeader,
//                document.right(), top, 0);
        ColumnText.showTextAligned(
                writer.getDirectContent(),
                Element.ALIGN_LEFT,
                new Phrase("我是页眉", font),
                document.left(),
                document.top() + 20, 0);

        //生成下划线，使用空格占位
        ColumnText.showTextAligned(writer.getDirectContent(),
                Element.ALIGN_LEFT, PDFBuilder1.blankPhrase,
                document.left(-1), bottom, 0);

        //生成页脚页数
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, new Phrase("我是页尾", PDFBuilder1.font), document.left(), document.bottom(-30), 0);

    }

}


```

## 结果
![](https://user-gold-cdn.xitu.io/2020/4/29/171c4c696b96847f?w=1367&h=750&f=png&s=102475)

当然这个是没有样式的，得前端把样式画好，再去生成
- [Pdf](https://blog.csdn.net/wumingdu1234/article/details/104418074?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-15.nonecase&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-15.nonecase)
- [对css友好的文档](https://blog.csdn.net/xt7821096/article/details/79583529)
## 结尾
具体的代码我放到一个仓库了，大家有需要的可以去下载
https://gitee.com/ybliu/springboot.git

![](https://user-gold-cdn.xitu.io/2020/4/29/171c4caf11441381?w=1054&h=807&f=png&s=62188)

![](https://user-gold-cdn.xitu.io/2020/4/7/1715405b9c95d021?w=900&h=500&f=png&s=109836)

## 日常求赞
> 好了各位，以上就是这篇文章的全部内容了，能看到这里的人呀，都是**真粉**。

> 创作不易，各位的支持和认可，就是我创作的最大动力，我们下篇文章见

>六脉神剑 | 文 【原创】如果本篇博客有任何错误，请批评指教，不胜感激 ！
