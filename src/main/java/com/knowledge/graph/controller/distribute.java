package com.knowledge.graph.controller;

import com.alibaba.fastjson.JSONArray;
import org.neo4j.cypher.internal.frontend.v2_3.ast.functions.Str;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import scala.None;

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

    private static int getDiskusage(){
        File file = new File("/");
        double freesize = file.getFreeSpace() / (1024.0 * 1024 * 1024);
        double totalsize = file.getTotalSpace() / (1024.0 * 1024 * 1024);
        return (int)((1- freesize / totalsize)*100);
    }


    @GetMapping("/cpu")
    public static String monitor(HttpServletRequest request,Model model) {
        try {
            return String.format("cpu:%s,ram:%s,disk:%d,status:1", getProcessCpuLoad(), getRAMLoad(), getDiskusage());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/load/{id}")
    public static JSONArray getload(@PathVariable String id) {
        StringBuilder res = new StringBuilder("[");
        String status = "cpu:0,ram:0,disk:0,status:0,location:\"\"";
        try {
            // https
            status = httpsCrawler.getByURL("https://" + InitailConfig.serverNode.get(id).get_ip() + "/cpu");
            // http
//            status = httpsCrawler.getByURL("http://" + InitailConfig.serverNode.get(id).get_ip() + "/cpu");
        }
        catch (Exception e) {
        }
//        if(status.equals("None")){
//            status = "cpu:0,ram:0,disk:0,status:0,location:\"\"";
//        }
        res.append(String.format("{id:\"%s\",ip:\"%s\",%s,location:\"%s\"}",
        id, InitailConfig.serverNode.get(id).get_ip(),status,InitailConfig.serverNode.get(id).get_location()));
        res.append("]");
        System.out.println(res.toString());
        return JSONArray.parseArray(res.toString());
    }

    @GetMapping("/heartbeat")
    public static JSONArray heartbeat(HttpServletRequest request,Model model) {

        return null;
    }

    public static void main(String[]args){
        String address = "https://120.77.220.71/cpu";
        //绕过Https证书方案1
        System.out.println(httpsCrawler.getByURL(address));
        //只要请求过程中没发生异常，就说明成功绕过Https证书问题；在上例子中红色文字部分是关键
    }

}
