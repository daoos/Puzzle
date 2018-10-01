package com.knowledge.graph.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by geshuaiqi on 2018/9/30.
 */
public class test {
    private static String getWebCon(String domain) {
        StringBuffer sb = new StringBuffer();
        try {
            java.net.URL url = new java.net.URL(domain);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            //System.out.println(sb.toString());
            in.close();
        } catch (Exception e) { // Report any errors that arise
            sb.append(e.toString());
            System.err.println(e);
            System.err.println("Usage:   java   HttpClient   <URL>   [<filename>]");
        }
        return sb.toString();
    }

    public static void main(String[] arg){
        try {
            String str = "https://api.ownthink.com/kg/knowledge?entity=" + java.net.URLEncoder.encode("刘德华", "utf-8");
            str = new String(str.getBytes("ISO-8859-1"), "UTF-8");
            System.out.println(str);
        }
        catch (Exception e){

        }
    }

    private final static String ENCODE = "GBK";
    /**
     * URL 解码
     *
     * @return String
     * @author lifq
     * @date 2015-3-17 下午04:09:51
     */
    public static String getURLDecoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLDecoder.decode(str, ENCODE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * URL 转码
     *
     * @return String
     * @author lifq
     * @date 2015-3-17 下午04:10:28
     */
    public static String getURLEncoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLEncoder.encode(str, ENCODE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

}
