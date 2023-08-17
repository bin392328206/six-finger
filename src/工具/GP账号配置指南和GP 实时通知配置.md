# GP账号配置指南
## 第一步，登陆https://console.developers.google.com
如果是首次登陆则会弹出以下对话框，根据业务出海国家选择一个合适的国家即可

![image.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/6bcd4241d2094e69b8bfd9e47416827b~tplv-k3u1fbpfcp-watermark.image?)

##  第二步，选中对应项目-》进入启用api服务

![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/116d75af7f8f4a1d8f78e6224999b41b~tplv-k3u1fbpfcp-watermark.image?)

## 第三步，搜索Google Play Developer Api

![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/12b8ac32deec45eb8a93f781369c6fbe~tplv-k3u1fbpfcp-watermark.image?)
##  第四步，启用Google Play Developer Api

![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/89cba83abe7543539ead9b5e55b37e2c~tplv-k3u1fbpfcp-watermark.image?)

## 第五步，进入IAM和管理->IAM

![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/46649f2b293e4c13a576c4c5344fea6a~tplv-k3u1fbpfcp-watermark.image?)
##  第六步，创建服务账号

![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/50682a8d372d47beb262d3fdf40d4f14~tplv-k3u1fbpfcp-watermark.image?)
## 第七步，填写相关信息

![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/65cf341752904a298eb5c9663e2b00ed~tplv-k3u1fbpfcp-watermark.image?)

## 第八步，配置角色

![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/be51a1a8e862407492266218c6d61663~tplv-k3u1fbpfcp-watermark.image?)

## 第九步，默认创建密钥


![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/816b0cdaab60444f81d0899019e58d31~tplv-k3u1fbpfcp-watermark.image?)

## 第十步，根据业务需要，选择p12或json格式


![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/7d2f7f392cd54df3801f11347398df01~tplv-k3u1fbpfcp-watermark.image?)


##  第十一步，记住私钥密码：notasecret


![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/1dd9f9fc2de14b4581047db31012a649~tplv-k3u1fbpfcp-watermark.image?)

## 第十二步，访问https://play.google.com/apps/publish  ，设置-》api权限


![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/b9091548ff27468882251cff984b829f~tplv-k3u1fbpfcp-watermark.image?)

## 十三步 把财务数据和管理订单的权限都要打开


![image.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/58cb3b354a9d4d0aa5bf81a08cd52b77~tplv-k3u1fbpfcp-watermark.image?)

## 十四 注意信息
1.  如果您已是 Google Play Developer API 的用户，则可以执行以下这些步骤来关联到您现有的 API 项目。
1.  如果您想关联的 API 项目未列出，请确认您的 Google Play 管理中心帐号是否已指定为"所有者"，且 Google Play Developer API 已启用。
1.  更多信息可参考[Google Play Developer API指南](https://developers.google.com/android-publisher/getting_started?hl=zh-cn)

# GP实时通知配置
借助实时开发者通知 (RTDN) 机制，每当用户的权限在您的应用中发生变化时，您都会收到来自 Google 的通知。RTDN 利用 Google Cloud Pub/Sub，该服务可让您接收推送到您设置的网址的数据或使用客户端库轮询的数据。这些通知允许您立即对订阅状态的变化做出反应，这样就无需轮询 Google Play Developer API

## 第一步 创建主题/订阅

![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/4e13e7d2bce74c4c893bc32273e8554b~tplv-k3u1fbpfcp-watermark.image?)

##  第二步 新建Topics，填写主题ID，点击创建按钮


![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/727440f1019a4a9f94027b51947212cb~tplv-k3u1fbpfcp-watermark.image

## 第三步 分配权限


![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/66100cb3b44d4d4bab6ed4547ca596b9~tplv-k3u1fbpfcp-watermark.image?)

点击右侧【添加主账号】按钮，给google-play-developer-notifications@system.gserviceaccount.com添加权限

![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/e245bb5c262846f28f7e1b3c7e83ba00~tplv-k3u1fbpfcp-watermark.image?)

添加发布权限

![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/717022c05e334c84aeccf07ed0ad6c9e~tplv-k3u1fbpfcp-watermark.image?)

##  第四步 新建【订阅】

![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/8ba0f9cfa24e4727b0865fdd0c2f2970~tplv-k3u1fbpfcp-watermark.image?)
填写订阅id以及需要订阅的主题，注意：若需要消息发送到指定的服务器，订阅类型请选择【推送】，并配置需要推送的url

![image.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/c6ec16d141d3466b93851ac7db44b4de~tplv-k3u1fbpfcp-watermark.image?)
-   点击创建，一个订阅就创建好了

## 第五步 启用实时开发者通知
-   进入[Google Play 管理中心](https://play.google.com/console/?hl=zh-cn)
-   选择需要设置的应用，进入应用页面


![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/c218f4f782e54c969e75e336b6441a86~tplv-k3u1fbpfcp-watermark.image?)

左侧选择【创收】-> 【创收设置】进入设置页面，Google Play结算服务下的主题名称填写第二步新建的主题名称（主题列表可复制主题名称），这是好以后点击【发送测试通知】，如果测试发布成功，则系统会显示一条消息，表明测试发布已成功。若显示不成功，请检查设置是不是不正常

![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/6d1d0e1bf28944a6a9b760c7e579f6e1~tplv-k3u1fbpfcp-watermark.image?)

# 注意

1.  此文在已经建好并上传好应用的前提下设置，
1.  更多信息可参考[Google Play 结算服务](https://developer.android.com/google/play/billing/getting-ready?hl=zh-cn#configure-rtdn)
