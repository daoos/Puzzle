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


    @GetMapping("/cpu")
    public static String monitor(HttpServletRequest request,Model model) {
        try {
            return String.format("cpu:%s,ram:%s,status:1", getProcessCpuLoad(), getRAMLoad());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/load/{id}")
    public static JSONArray getload(@PathVariable String id) {
        StringBuilder res = new StringBuilder("[");
        String status = "";
        try {
            status = searchAPI.crawl(new URL("http://" + InitailConfig.serverNode.get(id).get_ip() + "/cpu"));
        }
        catch (Exception e) {}
        if(status.equals("None")){
            status = "cpu:0,ram:0,status:0,location:\"\"";
        }
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

}
