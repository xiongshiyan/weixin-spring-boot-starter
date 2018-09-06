package top.jfunc.weixin.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Signature {
	 /**
	 * 验证微信签名
	 */
	 public static boolean checkSignature(String token , String signature, String timestamp,String nonce) {
		 String[] arr = new String[] { token, timestamp, nonce };
		 // 将token、timestamp、nonce 三个参数进行字典序排序
		 Arrays.sort(arr);
		 StringBuilder content = new StringBuilder();
		 for (int i = 0; i < arr.length; i++) {
			 content.append(arr[i]);
		 }
		 MessageDigest md = null;
		 String tmpStr = null;
		 try {
			 md = MessageDigest.getInstance("SHA-1");
		 // 将三个参数字符串拼接成一个字符串进行sha1 加密
			 byte[] digest = md.digest(content.toString().getBytes());
			 tmpStr = byte2Str(digest);
		 } catch (NoSuchAlgorithmException e) {
			 e.printStackTrace();
		 }
		
		 content = null;
		 // 将sha1 加密后的字符串可与signature 对比，标识该请求来源于微信
		 return tmpStr != null && tmpStr.equalsIgnoreCase(signature);
	 }

	/**
	* 将字节数组转换为十六进制字符串
	*
	* @param byteArray
	* @return
	*/
	private static String byte2Str(byte[] byteArray) {
		String strDigest = "";
		for (int i = 0; i < byteArray.length; i++) {
			strDigest += byte2HexStr(byteArray[i]);
		}
		return strDigest;
	}

	/**
	* 将字节转换为十六进制字符串
	*
	* @param mByte
	* @return
	*/
	private static String byte2HexStr(byte mByte) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] tempArr = new char[2];
		tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
		tempArr[1] = Digit[mByte & 0X0F];
		
		String s = new String(tempArr);
		return s;
	}
}