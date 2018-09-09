package top.jfunc.weixin.controller;

import com.jfinal.weixin.sdk.api.ApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import top.jfunc.weixin.utils.MsgEncryptKit;
import top.jfunc.weixin.utils.Signature;
import com.jfinal.kit.HttpKit;
import com.jfinal.weixin.iot.msg.InEquDataMsg;
import com.jfinal.weixin.iot.msg.InEqubindEvent;
import com.jfinal.weixin.sdk.msg.InMsgParser;
import com.jfinal.weixin.sdk.msg.in.*;
import com.jfinal.weixin.sdk.msg.in.card.*;
import com.jfinal.weixin.sdk.msg.in.event.*;
import com.jfinal.weixin.sdk.msg.in.speech_recognition.InSpeechRecognitionResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 这是微信所有消息的总入口，需要在微信公众号后台配置URL
 * @author xiongshiyan at 2018/7/26 , contact me with email yanshixiong@126.com or phone 15208384257
 */
@RequestMapping("${weixin.entry.url:/open/wx/develop}")
public abstract class BaseWxDevelopController {

    public static final String SIGNATURE = "signature";
    public static final String TIMESTAMP = "timestamp";
    public static final String NONCE     = "nonce";
    public static final String ECHO_STR  = "echostr";
    @Autowired
    private ApiConfig apiConfig;
    /**
     * 是否需要对微信消息加解密
     */
    @Value("${weixin.messageEncrypt:false}")
    protected boolean messageEncrypt;
    @Value("${weixin.entry.url.token:weixintoken}")
    private String token;
    private static final Logger logger = LoggerFactory.getLogger(BaseWxDevelopController.class);

    @RequestMapping(value = "",method ={ RequestMethod.GET})
    public String develop(HttpServletRequest request) throws Exception{
        //每次请求都会带上这几个除了echostr的字符串，所以在post的时候也可以利用他们验证请求来自微信
        // 微信加密签名
        String signature = request.getParameter(SIGNATURE);
        // 时间戳
        String timestamp = request.getParameter(TIMESTAMP);
        // 随机数
        String nonce = request.getParameter(NONCE);
        // 随机字符串
        String echostr = request.getParameter(ECHO_STR);

        // 通过检验signature 对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        if (Signature.checkSignature(token, signature, timestamp, nonce)) {
            return echostr;
        }else {
            return "你不是微信服务器，请自重！！";
        }
    }

    /**
     * 以下是jfianl-weixin的处理方式 WxDevelopFinalController利用责任链，就有更好的处理方式
     */
    @RequestMapping(value = "",method ={ RequestMethod.POST})
    public String develop(HttpServletRequest request , HttpServletResponse response) throws Exception{
        //将请求、响应的编码均设置为UTF-8（防止中文乱码）
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/xml;charset=utf-8");

        String xml = HttpKit.readData(request);
        logger.info(xml);
        //对消息解密
        if(messageEncrypt){
            xml = MsgEncryptKit.decrypt(xml,
                    request.getParameter(TIMESTAMP),
                    request.getParameter(NONCE),
                    request.getParameter("msg_signature") ,
                    apiConfig);
        }

        InMsg msg = InMsgParser.parse(xml);
        if (msg instanceof InTextMsg)
            return processInTextMsg((InTextMsg) msg);
        else if (msg instanceof InImageMsg)
            return processInImageMsg((InImageMsg) msg);
        else if (msg instanceof InSpeechRecognitionResults)  //update by unas at 2016-1-29, 由于继承InVoiceMsg，需要在InVoiceMsg前判断类型
            return processInSpeechRecognitionResults((InSpeechRecognitionResults) msg);
        else if (msg instanceof InVoiceMsg)
            return processInVoiceMsg((InVoiceMsg) msg);
        else if (msg instanceof InVideoMsg)
            return processInVideoMsg((InVideoMsg) msg);
        else if (msg instanceof InShortVideoMsg)   //支持小视频
            return processInShortVideoMsg((InShortVideoMsg) msg);
        else if (msg instanceof InLocationMsg)
            return processInLocationMsg((InLocationMsg) msg);
        else if (msg instanceof InLinkMsg)
            return processInLinkMsg((InLinkMsg) msg);
        else if (msg instanceof InCustomEvent)
            return processInCustomEvent((InCustomEvent) msg);
        else if (msg instanceof InFollowEvent)
            return processInFollowEvent((InFollowEvent) msg);
        else if (msg instanceof InQrCodeEvent)
            return processInQrCodeEvent((InQrCodeEvent) msg);
        else if (msg instanceof InLocationEvent)
            return processInLocationEvent((InLocationEvent) msg);
        else if (msg instanceof InMassEvent)
            return processInMassEvent((InMassEvent) msg);
        else if (msg instanceof InMenuEvent)
            return processInMenuEvent((InMenuEvent) msg);
        else if (msg instanceof InTemplateMsgEvent)
            return processInTemplateMsgEvent((InTemplateMsgEvent) msg);
        else if (msg instanceof InShakearoundUserShakeEvent)
            return processInShakearoundUserShakeEvent((InShakearoundUserShakeEvent) msg);
        else if (msg instanceof InVerifySuccessEvent)
            return processInVerifySuccessEvent((InVerifySuccessEvent) msg);
        else if (msg instanceof InVerifyFailEvent)
            return processInVerifyFailEvent((InVerifyFailEvent) msg);
        else if (msg instanceof InPoiCheckNotifyEvent)
            return processInPoiCheckNotifyEvent((InPoiCheckNotifyEvent) msg);
        else if (msg instanceof InWifiEvent)
            return processInWifiEvent((InWifiEvent) msg);
        else if (msg instanceof InUserCardEvent)
            return processInUserCardEvent((InUserCardEvent) msg);
        else if (msg instanceof InUpdateMemberCardEvent)
            return processInUpdateMemberCardEvent((InUpdateMemberCardEvent) msg);
        else if (msg instanceof InUserPayFromCardEvent)
            return processInUserPayFromCardEvent((InUserPayFromCardEvent) msg);
        else if (msg instanceof InMerChantOrderEvent)
            return processInMerChantOrderEvent((InMerChantOrderEvent) msg);
        else if (msg instanceof InCardPassCheckEvent)
            return processInCardPassCheckEvent((InCardPassCheckEvent) msg);
        else if (msg instanceof InCardPayOrderEvent)
            return processInCardPayOrderEvent((InCardPayOrderEvent) msg);
        else if (msg instanceof InCardSkuRemindEvent)
            return processInCardSkuRemindEvent((InCardSkuRemindEvent) msg);
        else if (msg instanceof InUserConsumeCardEvent)
            return processInUserConsumeCardEvent((InUserConsumeCardEvent) msg);
        else if (msg instanceof InUserGetCardEvent)
            return processInUserGetCardEvent((InUserGetCardEvent) msg);
        else if (msg instanceof InUserGiftingCardEvent)
            return processInUserGiftingCardEvent((InUserGiftingCardEvent) msg);
            //===================微信智能硬件========================//
        else if (msg instanceof InEqubindEvent)
            return processInEqubindEvent((InEqubindEvent) msg);
        else if (msg instanceof InEquDataMsg)
            return processInEquDataMsg((InEquDataMsg) msg);
            //===================微信智能硬件========================//
        else if (msg instanceof InNotDefinedEvent) {
            logger.error("未能识别的事件类型。 消息 xml 内容为：\n" + xml);
            return processIsNotDefinedEvent((InNotDefinedEvent) msg);
        } else if (msg instanceof InNotDefinedMsg) {
            logger.error("未能识别的消息类型。 消息 xml 内容为：\n" + xml);
            return processIsNotDefinedMsg((InNotDefinedMsg) msg);
        }

        return "error";
    }
    public String getInMsgXml(HttpServletRequest request) {
        return HttpKit.readData(request);
    }

    /**
     * 处理接收到的文本消息
     * @param inTextMsg 处理接收到的文本消息
     */
    protected String processInTextMsg(InTextMsg inTextMsg)throws Exception{
        return "";
    }

    /**
     * 处理接收到的图片消息
     * @param inImageMsg 处理接收到的图片消息
     */
    protected String processInImageMsg(InImageMsg inImageMsg)throws Exception{
        return "";
    }

    /**
     * 处理接收到的语音消息
     * @param inVoiceMsg 处理接收到的语音消息
     */
    protected String processInVoiceMsg(InVoiceMsg inVoiceMsg)throws Exception{
        return "";
    }

    /**
     * 处理接收到的视频消息
     * @param inVideoMsg 处理接收到的视频消息
     */
    protected String processInVideoMsg(InVideoMsg inVideoMsg)throws Exception{
        return "";
    }

    /**
     * 处理接收到的小视频消息
     * @param inShortVideoMsg 处理接收到的小视频消息
     */
    protected String processInShortVideoMsg(InShortVideoMsg inShortVideoMsg)throws Exception{
        return "";
    }

    /**
     * 处理接收到的地址位置消息
     * @param inLocationMsg 处理接收到的地址位置消息
     */
    protected String processInLocationMsg(InLocationMsg inLocationMsg)throws Exception{
        return "";
    }

    /**
     * 处理接收到的链接消息
     * @param inLinkMsg 处理接收到的链接消息
     */
    protected String processInLinkMsg(InLinkMsg inLinkMsg)throws Exception{
        return "";
    }

    /**
     * 处理接收到的多客服管理事件
     * @param inCustomEvent 处理接收到的多客服管理事件
     */
    protected String processInCustomEvent(InCustomEvent inCustomEvent)throws Exception{
        return "";
    }

    /**
     * 处理接收到的关注/取消关注事件
     * @param inFollowEvent 处理接收到的关注/取消关注事件
     */
    protected String processInFollowEvent(InFollowEvent inFollowEvent)throws Exception{
        return "";
    }

    /**
     * 处理接收到的扫描带参数二维码事件
     * @param inQrCodeEvent 处理接收到的扫描带参数二维码事件
     */
    protected String processInQrCodeEvent(InQrCodeEvent inQrCodeEvent)throws Exception{
        return "";
    }

    /**
     * 处理接收到的上报地理位置事件
     * @param inLocationEvent 处理接收到的上报地理位置事件
     */
    protected String processInLocationEvent(InLocationEvent inLocationEvent)throws Exception{
        return "";
    }

    /**
     * 处理接收到的群发任务结束时通知事件
     * @param inMassEvent 处理接收到的群发任务结束时通知事件
     */
    protected String processInMassEvent(InMassEvent inMassEvent)throws Exception{
        return "";
    }

    /**
     * 处理接收到的自定义菜单事件
     * @param inMenuEvent 处理接收到的自定义菜单事件
     */
    protected String processInMenuEvent(InMenuEvent inMenuEvent)throws Exception{
        return "";
    }

    /**
     * 处理接收到的语音识别结果
     * @param inSpeechRecognitionResults 处理接收到的语音识别结果
     */
    protected String processInSpeechRecognitionResults(InSpeechRecognitionResults inSpeechRecognitionResults)throws Exception{
        return "";
    }

    /**
     * 处理接收到的模板消息是否送达成功通知事件
     * @param inTemplateMsgEvent 处理接收到的模板消息是否送达成功通知事件
     */
    protected String processInTemplateMsgEvent(InTemplateMsgEvent inTemplateMsgEvent)throws Exception{
        return "";
    }

    /**
     * 处理微信摇一摇事件
     * @param inShakearoundUserShakeEvent 处理微信摇一摇事件
     */
    protected String processInShakearoundUserShakeEvent(InShakearoundUserShakeEvent inShakearoundUserShakeEvent)throws Exception{
        return "";
    }

    /**
     * 资质认证成功 || 名称认证成功 || 年审通知 || 认证过期失效通知
     * @param inVerifySuccessEvent 资质认证成功 || 名称认证成功 || 年审通知 || 认证过期失效通知
     */
    protected String processInVerifySuccessEvent(InVerifySuccessEvent inVerifySuccessEvent)throws Exception{
        return "";
    }

    /**
     * 资质认证失败 || 名称认证失败
     * @param inVerifyFailEvent 资质认证失败 || 名称认证失败
     */
    protected String processInVerifyFailEvent(InVerifyFailEvent inVerifyFailEvent)throws Exception{
        return "";
    }

    /**
     * 门店在审核事件消息
     * @param inPoiCheckNotifyEvent 门店在审核事件消息
     */
    protected String processInPoiCheckNotifyEvent(InPoiCheckNotifyEvent inPoiCheckNotifyEvent)throws Exception{
        return "";
    }

    /**
     * WIFI连网后下发消息 by unas at 2016-1-29
     * @param inWifiEvent WIFI连网后下发消息
     */
    protected String processInWifiEvent(InWifiEvent inWifiEvent)throws Exception{
        return "";
    }

    /**
     * 1. 微信会员卡二维码扫描领取接口
     * 2. 微信会员卡激活接口
     * 3. 卡券删除事件推送
     * 4. 从卡券进入公众号会话事件推送
     * @param inUserCardEvent InUserCardEvent
     */
    protected String processInUserCardEvent(InUserCardEvent inUserCardEvent)throws Exception{
        return "";
    }

    /**
     * 微信会员卡积分变更
     * @param inUpdateMemberCardEvent 微信会员卡积分变更
     */
    protected String processInUpdateMemberCardEvent(InUpdateMemberCardEvent inUpdateMemberCardEvent)throws Exception{
        return "";
    }

    /**
     * 微信会员卡快速买单
     * @param inUserPayFromCardEvent 微信会员卡快速买单
     */
    protected String processInUserPayFromCardEvent(InUserPayFromCardEvent inUserPayFromCardEvent)throws Exception{
        return "";
    }

    /**
     * 微信小店订单支付成功接口消息
     * @param inMerChantOrderEvent 微信小店订单支付成功接口消息
     */
    protected String processInMerChantOrderEvent(InMerChantOrderEvent inMerChantOrderEvent)throws Exception{
        return "";
    }

    //
    /**
     * 没有找到对应的事件消息
     * @param inNotDefinedEvent 没有对应的事件消息
     */
    protected String processIsNotDefinedEvent(InNotDefinedEvent inNotDefinedEvent)throws Exception{
        return "";
    }

    /**
     * 没有找到对应的消息
     * @param inNotDefinedMsg 没有对应消息
     */
    protected String processIsNotDefinedMsg(InNotDefinedMsg inNotDefinedMsg)throws Exception{
        return "";
    }

    /**
     * 卡券转赠事件推送
     * @param msg 卡券转赠事件推送
     */
    protected String processInUserGiftingCardEvent(InUserGiftingCardEvent msg)throws Exception{
        return "";
    }

    /**
     * 卡券领取事件推送
     * @param msg 卡券领取事件推送
     */
    protected String processInUserGetCardEvent(InUserGetCardEvent msg)throws Exception{
        return "";
    }

    /**
     * 卡券核销事件推送
     * @param msg 卡券核销事件推送
     */
    protected String processInUserConsumeCardEvent(InUserConsumeCardEvent msg)throws Exception{
        return "";
    }

    /**
     * 卡券库存报警事件
     * @param msg 卡券库存报警事件
     */
    protected String processInCardSkuRemindEvent(InCardSkuRemindEvent msg)throws Exception{
        return "";
    }

    /**
     * 券点流水详情事件
     * @param msg 券点流水详情事件
     */
    protected String processInCardPayOrderEvent(InCardPayOrderEvent msg)throws Exception{
        return "";
    }

    /**
     * 审核事件推送
     * @param msg 审核事件推送
     */
    protected String processInCardPassCheckEvent(InCardPassCheckEvent msg)throws Exception{
        return "";
    }

    /**
     * 处理微信硬件绑定和解绑事件
     * @param msg 处理微信硬件绑定和解绑事件
     */
    protected String processInEqubindEvent(InEqubindEvent msg)throws Exception{
        return "";
    }

    /**
     * 处理微信硬件发来数据
     * @param msg 处理微信硬件发来数据
     */
    protected String processInEquDataMsg(InEquDataMsg msg)throws Exception{
        return "";
    }
}
