package top.jfunc.weixin.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.jfunc.weixin.utils.SortUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author xiongshiyan at 2018/9/7 , contact me with email yanshixiong@126.com or phone 15208384257
 */
public class MessageHandlerUtil {
    private static final Logger logger = LoggerFactory.getLogger(MessageHandlerUtil.class);

    public static BaseMessageHandler doHandlerChain(Map<String, BaseMessageHandler> messageHandlerMap) {
        //没有就默认处理
        if(null == messageHandlerMap || 0 == messageHandlerMap.size()){
            return FinalMessageHandler.sharedFinalMessageHandler();
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

        return handlers.get(0);
    }
}
