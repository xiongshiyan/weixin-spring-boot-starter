# 微信开发入口框架

#### 项目介绍
微信开发框架

#### 软件架构
软件架构说明


#### 安装教程

compile("top.jfunc.weixin:weixin-spring-boot-starter:1.8.2")

#### 使用说明

首先在配置文件中配置微信的开发入口 spring.weixin.urlPatterns , 默认为 /open/wx/develop .
##### <1>.~~使用注解 EnableWeixinController 导入 WxDevelopFinalController ,~~ 编写消息处理器并放入容器中 , 继承于 BaseMessageHandler.
##### <2>.不使用注解 , 自己编写 Controller 继承于 BaseWxDevelopController 复写相关的方法并放入容器中即可.


#### 例子
配置参见 WeixinProperties.java类

spring.weixin.wxConfigs[0].appId=xxxxxxx

spring.weixin.wxConfigs[0].appSecret=xxxxxxx

spring.weixin.wxConfigs[0].token=xxxxxxxxxxxx

```
@Component
@Order(1)
public class TextMessageHandler extends BaseMessageHandler{
    @Override
    public boolean canDo(InMsg inMsg) {
        return inMsg instanceof InTextMsg;
    }

    @Override
    public OutMsg handleByMe(InMsg inMsg) throws Exception {
        return new OutTextMsg(inMsg).setContent(new Date() + "");
    }
}
```

```
@Component
@Order(3)
public class LocationMessageHandler extends BaseMessageHandler{
    @Override
    public boolean canDo(InMsg inMsg) {
        return inMsg instanceof InLocationMsg;
    }

    @Override
    public OutMsg handleByMe(InMsg inMsg) throws Exception {
        InLocationMsg inLocationMsg = (InLocationMsg) inMsg;
        return new OutTextMsg(inMsg).setContent(inLocationMsg.getLocation_X() + "---" + inLocationMsg.getLocation_Y());
    }
}
```

```
@Component
@Order(2)
public class VoiceMessageHandler extends BaseMessageHandler{
    @Override
    public boolean canDo(InMsg inMsg) {
        return inMsg instanceof InVoiceMsg;
    }

    @Override
    public OutMsg handleByMe(InMsg inMsg) throws Exception {
        InVoiceMsg inVoiceMsg = (InVoiceMsg) inMsg;
        return new OutTextMsg(inMsg).setContent(inVoiceMsg.getMediaId() + " : " + inVoiceMsg.getMsgId());
    }
}
```