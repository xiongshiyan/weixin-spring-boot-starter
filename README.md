# 微信开发入口框架

#### 项目介绍
微信开发框架

#### 软件架构
软件架构说明


#### 安装教程

compile("top.jfunc.weixin:weixin-spring-boot-starter:1.8.0")

#### 使用说明

首先在配置文件中配置微信的开发入口 weixin.entry.url , 默认为 /open/wx/develop .
##### <1>.使用注解 EnableWeixinController 导入 WxDevelopFinalController , 编写消息处理器并放入容器中 , 继承于 BaseMessageHandler.
##### <2>.不使用注解 , 自己编写 Controller 继承于 BaseWxDevelopController 复写相关的方法并放入容器中即可.
