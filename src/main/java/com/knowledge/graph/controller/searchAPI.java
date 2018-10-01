package com.knowledge.graph.controller;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by geshuaiqi on 2018/9/30.
 */
@RestController
public class searchAPI {
    String baseURL = "https://api.ownthink.com/kg/knowledge?entity=";
    private Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping("/search/{entity}")
    public JSONObject search(@PathVariable String entity){
        String res="None";
        try {
            URL url = new URL( baseURL + java.net.URLEncoder.encode(entity, "utf-8"));
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3*1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //得到输入流
            InputStream inputStream = conn.getInputStream();
            res = readInputStream(inputStream);
        } catch (Exception e) {
            logger.error("通过url地址获取文本内容失败 Exception：" + e);
        }
        JSONObject json_test = JSONObject.fromObject(res);
        return json_test;
    }

    public String readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        logger.info(new String(bos.toByteArray(),"utf-8"));
        return new String(bos.toByteArray(),"utf-8");
    }
}
