package com.knowledge.graph.controller;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


/**
 * Created by geshuaiqi on 2018/9/30.
 */
@CrossOrigin
@RestController
public class searchAPI {
    String baseURL = "https://api.ownthink.com/kg/knowledge?entity=";
    private Logger logger = LoggerFactory.getLogger(getClass());

    static public String crawl(URL url){
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
//            logger.error("crawl: 通过url地址获取文本内容失败 Exception：" + e);
        }
        return res;
    }


    public String search(String entity){
        String res = "";
        try{
            URL url = new URL( baseURL + java.net.URLEncoder.encode(entity, "utf-8"));
            res = crawl(url);
        }catch (Exception e) {
            logger.error("search: 通过url地址获取文本内容失败 Exception：" + e);
        }
        return res;

    }

    @RequestMapping("/display")
    public ModelAndView display(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView("display");
        modelAndView.addObject("hello", "老王");
        return modelAndView;
    }

    @GetMapping("/search/all/{entity}")
    public JSONObject searchAll(@PathVariable String entity){
        String res = search(entity);
        JSONObject json_res = JSONObject.fromObject(res);
        return json_res;
    }

    @CrossOrigin
    @GetMapping("/search/entity/array/{entity}")
    public JSONArray searchEntity(@PathVariable String entity){
        JSONArray jsonarray = JSONArray.fromObject(searchEntityString(entity));
        return jsonarray;
    }

    @CrossOrigin
    @GetMapping("/search/entity/dbread/{entity}")
    public JSONArray searchEntityforDB(@PathVariable String entity){
        try {
            JSONArray jsonarray = JSONArray.fromObject(searchEntityString(entity));
            return jsonarray;
        }catch (Exception e){
            return JSONArray.fromObject("[{result:\'None\'}]");
        }
    }

    @CrossOrigin
    @GetMapping("/search/entity/string/{entity}")
    public String searchEntityString(@PathVariable String entity){
        JSONObject json = searchAll(entity).getJSONObject("data");
        String[] jsonlist = json.getString("avp").replace("\"","").replace("[","").replace("]","").split(",");
        String res = "[";
        for(int i=0;i<jsonlist.length;i+=2){
            if(res.length() > 1){
                res += ",";
            }
            String target = jsonlist[i+1];
            String relationship = jsonlist[i];
            if(target.contains(entity) == false) {
                res += "{source: \"" + entity + "\",target:\"" + target + "\",type:\"resolved\",rela: \"" + relationship + "\"}";
            }
        }

        //add description
        if(res.length() > 1){
            res += ",";
        }
        res += "{source: \"" + entity + "\",target:\"" + json.getString("desc") + "\",type:\"desc\",rela: \"desc\"}";
        res+="]";
        res = res.replace(",,",",");
        return res;
    }

    public String searchEntities(@PathVariable String content){
        String[] demo = NERProcessing(content);
        String res = "[";
        for(int index=0;index<demo.length;index++) {
            String entity = demo[index];
            JSONObject json = searchAll(entity).getJSONObject("data");
            String[] jsonlist = json.getString("avp").replace("\"", "").replace("[", "").replace("]", "").split(",");
            for (int i = 0; i < jsonlist.length; i += 2) {
                if (res.length() > 1) {
                    res += ",";
                }
                String target = jsonlist[i + 1];
                String relationship = jsonlist[i];
                if (target.contains(entity) == false) {
                    res += "{source: \"" + entity + "\",target:\"" + target + "\",type:\"resolved\",rela: \"" + relationship + "\"}";
                }
            }
            //add description
            if (res.length() > 1) {
                res += ",";
            }
            res += "{source: \"" + entity + "\",target:\"" + json.getString("desc") + "\",type:\"desc\",rela: \"desc\"}";
        }
        res+="]";
        res = res.replace(",,",",");
        return res;
    }

    private String[] NERProcessing(String content){
        String baseURL = "http://www.geshuaiqi.com:5000/ner/";
        String res = "";
        try{
            URL url = new URL( baseURL + java.net.URLEncoder.encode(content, "utf-8"));
            res = crawl(url);
        }catch (Exception e) {
            logger.error("NERProcessing: 可能是谷歌云上的NER模块崩了：" + e);
        }
        JSONObject json_res = JSONObject.fromObject(res);
        String[] personlist = json_res.getString("PER").split(";");
        String[] loclist =  json_res.getString("LOC").split(";");
        String[] orglist =  json_res.getString("ORG").split(";");
        String[] list = new String[personlist.length+orglist.length+loclist.length];
        int count=0;
        for(int i=0;i<personlist.length;i++){
            list[count++] = personlist[i];
        }
        for(int i=0;i<loclist.length;i++){
            list[count++] = personlist[i];
        }
        for(int i=0;i<orglist.length;i++){
            list[count++] = personlist[i];
        }
        return list;
    }


    public static String readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return new String(bos.toByteArray(),"utf-8");
    }

    private String readFileToString(String filepath) throws FileNotFoundException, IOException {
        StringBuilder sb = new StringBuilder();
        String s = "";
        BufferedReader br = new BufferedReader(new FileReader(filepath));
        while ((s = br.readLine()) != null) {
            sb.append(s + "\n");
        }
        br.close();
        String str = sb.toString();
        return str;
    }


    // 机器人智能问答接口
    @CrossOrigin
    @GetMapping("/qa/{question}")
    public String robotqa(@PathVariable String question){
        System.out.println(question);
        try{
            URL url = new URL( "http://www.dearwhy.top:5000/" + java.net.URLEncoder.encode(question, "utf-8"));
            String data = crawl(url);
            System.out.println(data);
            return data;
        }catch (Exception e){
            e.printStackTrace();
            return "访问flask接口时出错";
        }

    }

    public static void main(String[]args){
        new searchAPI().NERProcessing("刘备和毛泽东率领三十万大军");
    }


}
