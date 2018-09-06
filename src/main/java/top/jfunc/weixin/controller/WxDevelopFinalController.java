package top.jfunc.weixin.controller;

import com.jfinal.weixin.sdk.kit.MsgEncryptKit;
import top.jfunc.weixin.handler.BaseMessageHandler;
import top.jfunc.weixin.handler.FinalMessageHandler;
import com.jfinal.kit.HttpKit;
import com.jfinal.weixin.sdk.msg.InMsgParser;
import com.jfinal.weixin.sdk.msg.in.*;
import com.jfinal.weixin.sdk.msg.out.OutMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.RestController;
import top.jfunc.weixin.utils.SortUtil;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

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
        //没有就默认处理
        if(null == messageHandlerMap || 0 == messageHandlerMap.size()){
            this.firstMessageHandler = FinalMessageHandler.sharedFinalMessageHandler();
            return;
        }
        logger.info("find " + messageHandlerMap.size() + " message handlers , now combinate them to Chain of Responsibility");

        Collection<BaseMessageHandler> values = messageHandlerMap.values();
        List<BaseMessageHandler> handlers = new ArrayList<>(values);
        SortUtil.sort(handlers);

        //责任链
        for (int i = handlers.size() - 1; i > 0; i--) {
            BaseMessageHandler nextMessageHandler = handlers.get(i);
            logger.info(nextMessageHandler + " is the next of " + handlers.get(i-1));
            handlers.get(i-1).setNextMessageHandler(nextMessageHandler);
        }

        firstMessageHandler = handlers.get(0);
    }

    @Override
    public String develop(HttpServletRequest request , HttpServletResponse response) throws Exception{
        //将请求、响应的编码均设置为UTF-8（防止中文乱码）
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/xml;charset=utf-8");


        String xml = HttpKit.readData(request);
        //对消息解密
        if(messageEncrypt){
            xml = MsgEncryptKit.decrypt(xml,
                    request.getParameter(TIMESTAMP),
                    request.getParameter(NONCE),
                    request.getParameter("msg_signature"));
        }
        logger.info(xml);

        InMsg inMsg = InMsgParser.parse(xml);
        OutMsg outMsg = firstMessageHandler.handleMessage(inMsg);

        String toXml = outMsg.toXml();

        //对消息加密
        if(messageEncrypt){
            toXml = MsgEncryptKit.encrypt(toXml,
                    request.getParameter(TIMESTAMP),
                    request.getParameter(NONCE));
        }
        return toXml;

    }
}
