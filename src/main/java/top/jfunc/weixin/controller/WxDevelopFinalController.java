package top.jfunc.weixin.controller;

import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.kit.MsgEncryptKit;
import org.springframework.web.bind.annotation.RequestBody;
import top.jfunc.weixin.handler.BaseMessageHandler;
import com.jfinal.weixin.sdk.msg.InMsgParser;
import com.jfinal.weixin.sdk.msg.in.*;
import com.jfinal.weixin.sdk.msg.out.OutMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import top.jfunc.weixin.handler.MessageHandlerUtil;

/**
 * 将消息处理器放到容器中即可，容器自动发现并排序组成责任链
 * @author xiongshiyan at 2018/7/26 , contact me with email yanshixiong@126.com or phone 15208384257
 */
@RestController
public class WxDevelopFinalController extends BaseWxDevelopController implements ApplicationContextAware{
    private static final Logger logger = LoggerFactory.getLogger(WxDevelopFinalController.class);

    private ApplicationContext applicationContext;
    private BaseMessageHandler firstMessageHandler;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init(){
        logger.info("init...");
        //找到所有的消息处理器
        Map<String, BaseMessageHandler> messageHandlerMap = applicationContext.getBeansOfType(BaseMessageHandler.class);

        //组装消息处理器链
        this.firstMessageHandler = MessageHandlerUtil.doHandlerChain(messageHandlerMap);
    }

    @Override
    public String develop(@RequestBody String xml, HttpServletRequest request , HttpServletResponse response) throws Exception{
        //将请求、响应的编码均设置为UTF-8（防止中文乱码）
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/xml;charset=utf-8");

        if (null == xml || "".equals(xml)) {
            throw new RuntimeException("请不要在浏览器中请求该连接,调试请查看WIKI:http://git.oschina.net/jfinal/jfinal-weixin/wikis/JFinal-weixin-demo%E5%92%8C%E8%B0%83%E8%AF%95");
        }

        // 是否需要解密消息
        if (ApiConfigKit.getApiConfig().isEncryptMessage()) {
            xml = MsgEncryptKit.decrypt(xml,
                    request.getParameter(TIMESTAMP),
                    request.getParameter(NONCE),
                    request.getParameter("msg_signature"));
        }
        if (ApiConfigKit.isDevMode()) {
            logger.info(xml);
        }

        InMsg inMsg = InMsgParser.parse(xml);
        OutMsg outMsg = firstMessageHandler.handleMessage(inMsg);

        String toXml = outMsg.toXml();

        if (ApiConfigKit.isDevMode()) {
            logger.info(toXml);
            System.out.println("--------------------------------------------------------------------------------\n");
        }
        // 是否需要加密消息
        if (ApiConfigKit.getApiConfig().isEncryptMessage()) {
            toXml = MsgEncryptKit.encrypt(toXml,
                    request.getParameter(TIMESTAMP),
                    request.getParameter(NONCE));
        }
        return toXml;

    }
}
