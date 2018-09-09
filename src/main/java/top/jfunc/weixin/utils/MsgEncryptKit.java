package top.jfunc.weixin.utils;

import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.encrypt.WXBizMsgCrypt;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

/**
 * 意义和使用都跟jfinal-weixin中的 MsgEncryptKit一样, 只是ApiConfig改为传参
 * @see com.jfinal.weixin.sdk.kit.MsgEncryptKit
 */
public class MsgEncryptKit {

    private static final String format = "<xml><ToUserName><![CDATA[toUser]]></ToUserName><Encrypt><![CDATA[%1$s]]></Encrypt></xml>";

    public static String encrypt(String msg, String timestamp, String nonce ,
                                 ApiConfig apiConfig) {
        try {
            WXBizMsgCrypt pc = new WXBizMsgCrypt(apiConfig.getToken(), apiConfig.getEncodingAesKey(), apiConfig.getAppId());
            return pc.encryptMsg(msg, timestamp, nonce);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String encryptedMsg, String timestamp, String nonce, String msgSignature ,
                                 ApiConfig apiConfig) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader sr = new StringReader(encryptedMsg);
            InputSource is = new InputSource(sr);
            Document document = db.parse(is);

            Element root = document.getDocumentElement();
            NodeList nodelist1 = root.getElementsByTagName("Encrypt");
            // NodeList nodelist2 = root.getElementsByTagName("MsgSignature");

            String encrypt = nodelist1.item(0).getTextContent();
            // String msgSignature = nodelist2.item(0).getTextContent();

            String fromXML = String.format(format, encrypt);

            String encodingAesKey = apiConfig.getEncodingAesKey();
            if (encodingAesKey == null) {
                throw new IllegalStateException("encodingAesKey can not be null, config encodingAesKey first.");
            }
            WXBizMsgCrypt pc = new WXBizMsgCrypt(apiConfig.getToken(), encodingAesKey, apiConfig.getAppId());
            return pc.decryptMsg(msgSignature, timestamp, nonce, fromXML);    // 此处 timestamp 如果与加密前的不同则报签名不正确的异常
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
