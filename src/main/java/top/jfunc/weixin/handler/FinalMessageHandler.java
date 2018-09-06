package top.jfunc.weixin.handler;

import com.jfinal.weixin.sdk.msg.in.InMsg;
import com.jfinal.weixin.sdk.msg.out.OutMsg;
import com.jfinal.weixin.sdk.msg.out.OutTextMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 其他消息处理类处理不了的请求全部有这个类处理，返回一个失败信息
 * @author 熊诗言
 *
 */
public class FinalMessageHandler extends BaseMessageHandler {
	private static final Logger logger = LoggerFactory.getLogger(FinalMessageHandler.class);

	private static final FinalMessageHandler _instance = new FinalMessageHandler();
	private FinalMessageHandler(){}
	public static FinalMessageHandler sharedFinalMessageHandler(){
		return _instance;
	}
	
	@Override
	public boolean canDo(InMsg inMsg) {
		return true;
	}

	@Override
	public OutMsg handleByMe(InMsg inMsg) throws Exception{
		logger.info(inMsg.getMsgType());
		return new OutTextMsg(inMsg).setContent("未处理...");
	}

}
