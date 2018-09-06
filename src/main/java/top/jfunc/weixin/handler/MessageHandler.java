package top.jfunc.weixin.handler;

import com.jfinal.weixin.sdk.msg.in.InMsg;
import com.jfinal.weixin.sdk.msg.out.OutMsg;

/**
 * 处理消息的抽象接口，责任链模式
 * @author xiongshiyan
 *
 */
public interface MessageHandler {
    /**
	 * 该你处理就处理消息的函数
	 * @param inMsg 微信来的消息
	 * @return outMsg 返回消息
     * @throws Exception Exception
	 */
	OutMsg handleMessage(InMsg inMsg) throws Exception;
	/**
	 * 设置你不能处理后续处理的对象
	 * @param nextMessageHandler 下一个处理器
	 */
	void setNextMessageHandler(MessageHandler nextMessageHandler);
}
