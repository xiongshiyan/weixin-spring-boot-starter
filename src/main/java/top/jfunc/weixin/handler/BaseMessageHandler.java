package top.jfunc.weixin.handler;

import com.jfinal.weixin.sdk.msg.in.InMsg;
import com.jfinal.weixin.sdk.msg.out.OutMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认的消息处理器，所有的消息处理器都必须继承于此类
 * @author xiongshiyan
 */
public abstract class BaseMessageHandler implements MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(BaseMessageHandler.class);

	protected MessageHandler nextMessageHandler;
    private MessageHandler finalMessageHandler;
	
	public BaseMessageHandler() {
		//默认的最终处理器，自己也可以通过setter方法改变
		this.finalMessageHandler = FinalMessageHandler.sharedFinalMessageHandler();
	}

	public void setFinalMessageHandler(MessageHandler finalMessageHandler) {
		this.finalMessageHandler = finalMessageHandler;
	}

    @Override
    public void setNextMessageHandler(MessageHandler nextMessageHandler) {
        this.nextMessageHandler = nextMessageHandler;
    }

	/**
	 * 模板方法模式实现主要的逻辑，是自己处理还是交给下游处理
	 */
	@Override
	public OutMsg handleMessage(InMsg inMsg) throws Exception{
	    //1.我可以处理
        if(canDo(inMsg)){
		    return handleByMe(inMsg);
        }

        //2.有下一个处理器
        if(null != nextMessageHandler){
            return nextMessageHandler.handleMessage(inMsg);
        }

        //3.最终处理器
        return finalMessageHandler.handleMessage(inMsg);
	}

	/**
	 * 给出你处理的额条件
     * @param inMsg 微信消息
	 * @return true if 你能处理
	 */
	public abstract boolean canDo(InMsg inMsg);
	/**
	 * 你具体的处理
	 * @param inMsg 微信消息
	 * @return 返回消息
     * @throws Exception Exception
	 */
	public abstract OutMsg handleByMe(InMsg inMsg) throws Exception;
}
