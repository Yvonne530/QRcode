# 📱 二维码加好友系统

本项目实现了一个 **Android 前端 + Spring Boot 后端** 的简单好友添加系统：  
- 每个用户都可以生成属于自己的 **二维码**  
- 二维码中包含唯一的加好友链接  
- 其他用户扫描二维码后，可以访问后端接口，实现好友添加  

---

## 🚀 功能说明

### 前端（Android App）
- 启动应用后自动请求后端接口，获取该用户的专属加好友链接  
- 使用 **ZXing** 生成二维码并显示在页面中  
- 扫码者可通过二维码跳转后端接口，完成加好友操作  

### 后端（Spring Boot 服务）
- **`/api/friend/getLink?userId=xxx`**  
  生成该用户的加好友链接，并返回 JSON  

- **`/addFriend?userId=xxx`**  
  处理加好友逻辑（可扩展：存数据库、返回结果提示等）  

---

## ⚙️ 技术栈

### 前端
- Android (Kotlin, Jetpack Compose)  
- OkHttp (网络请求)  
- ZXing (二维码生成)  

### 后端
- Spring Boot (Java)  
- 内置 Tomcat (HTTP 服务)  

---

## 📂 项目结构
src/main/java/com/example/demo/controller/FriendController.java


### 前端


app/src/main/java/com/example/qrcodeapp/MainActivity.kt


---

## 🖼️ 使用效果

1. 用户打开 Android App  
2. 自动加载后端生成的唯一二维码  
3. 其他用户扫描二维码 → 跳转后端接口 → 触发加好友逻辑  

---

## 🔧 使用方法

### 启动后端
```bash
cd demo
./mvnw spring-boot:run


浏览器测试接口：

http://localhost:8080/api/friend/getLink?userId=testUser


返回示例：

{
  "friendId": "http://10.0.2.2:8080/addFriend?userId=testUser"
}

启动前端

使用 Android Studio 打开 QRCodeApp

运行到 模拟器 或 真机（需保证能访问后端接口）

启动后自动显示二维码

✅ 已实现

每个用户都有唯一的二维码

二维码中包含加好友链接

可扩展数据库存储好友关系

🔮 待扩展

数据库存储好友信息

用户注册 / 登录功能

好友请求审批与通知

HTTPS 支持，确保数据安全



前端展示二维码（前端拿到一个字符串或链接，转成二维码图片展示）。  后端提供数据/接口（告诉前端二维码里面要放什么内容）。
