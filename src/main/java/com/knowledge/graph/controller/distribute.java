package com.knowledge.graph.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import java.lang.management.OperatingSystemMXBean;
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

    public static double getProcessCpuLoad() throws Exception {

        MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
        ObjectName name    = ObjectName.getInstance("java.lang:type=OperatingSystem");
        AttributeList list = mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });

        if (list.isEmpty())     return Double.NaN;

        Attribute att = (Attribute)list.get(0);
        Double value  = (Double)att.getValue();

        // usually takes a couple of seconds before we get real values
        if (value == -1.0)      return Double.NaN;
        // returns a percentage value with 1 decimal point precision
        System.out.println(value);
        return ((int)(value * 100000) / 10.0);
    }


    private static String generateRAM()
    {
        final long RAM_TOTAL = Runtime.getRuntime().totalMemory();
        final long RAM_FREE = Runtime.getRuntime().freeMemory();
        final long RAM_USED = RAM_TOTAL - RAM_FREE;
        final long RAM_TOTAL_MB = RAM_TOTAL / 8 / 1024;
        final long RAM_FREE_MB = RAM_FREE / 8 / 1024;
        final long RAM_USED_MB = RAM_USED / 8 / 1024;
        final double RAM_USED_PERCENTAGE = ((RAM_USED * 1.0) / RAM_TOTAL) * 100;
        return RAM_TOTAL_MB + "MB TOTAL / " + RAM_FREE_MB + "MB FREE / " + RAM_USED_MB + "MB USED (" + RAM_USED_PERCENTAGE + "%)";
    }


    @RequestMapping("/cpu")
    public String monitor(HttpServletRequest request,Model model) {
        try {
            return ""+getProcessCpuLoad()+";"+generateRAM();
        }catch (Exception e){
        }finally {

        }

        return "monitor";
    }
}
