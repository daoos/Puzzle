package com.knowledge.graph.controller;

import com.alibaba.fastjson.JSONArray;
import org.neo4j.cypher.internal.frontend.v2_3.ast.functions.Str;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by geshuaiqi on 2019/4/5.
 */
@CrossOrigin
@RestController
public class distribute {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public String crawl(URL url){
        String res="None";
        try {

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3*1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //得到输入流
            InputStream inputStream = conn.getInputStream();
            res = readInputStream(inputStream);
        } catch (Exception e) {
            logger.error("crawl: 通过url地址获取文本内容失败 Exception：" + e);
        }
        return res;
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

    // 获取CPU占用率
    public static double getProcessCpuLoad() throws Exception {
        OperatingSystemMXBean bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean();
        double value = (int)(bean.getSystemCpuLoad()*100);
        return value;
    }


    private static String getRAMLoad()
    {
        final long RAM_TOTAL = Runtime.getRuntime().totalMemory();
        final long RAM_FREE = Runtime.getRuntime().freeMemory();
        final long RAM_USED = RAM_TOTAL - RAM_FREE;
        final double RAM_USED_PERCENTAGE = ((RAM_USED * 1.0) / RAM_TOTAL) * 100;
        return (int)(RAM_USED_PERCENTAGE) + "";
    }


    @GetMapping("/cpu")
    public static JSONArray monitor(HttpServletRequest request,Model model) {
        try {

            String res = String.format("[{cpu:%s,ram:%s}]", getProcessCpuLoad(), getRAMLoad());
            JSONArray jsonarray = JSONArray.parseArray(res);
            return jsonarray;
//            return ""+getProcessCpuLoad()+";"+generateRAM();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
