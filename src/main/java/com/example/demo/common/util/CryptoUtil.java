package com.example.demo.common.util;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;


public class CryptoUtil {
	
	public static String encrypt(String str) throws NoSuchAlgorithmException, GeneralSecurityException, UnsupportedEncodingException{ 
		Encoder encoder = Base64.getEncoder();
		String enStr = new String(encoder.encode(str.getBytes("UTF-8"))); 
		
		return enStr; 
	} 
	
	public static String decrypt(String str) throws NoSuchAlgorithmException, GeneralSecurityException, UnsupportedEncodingException { 
		Decoder decoder = Base64.getDecoder();
		String deStr = new String(decoder.decode(str.getBytes("UTF-8")));
		
		return deStr;
	}
}
