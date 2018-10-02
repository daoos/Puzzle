package com.knowledge.graph.controller;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

/**
 * Created by geshuaiqi on 2018/9/30.
 */
@RestController
public class searchAPI {
    String baseURL = "https://api.ownthink.com/kg/knowledge?entity=";
    private Logger logger = LoggerFactory.getLogger(getClass());

    public String search(String entity){
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
        return res;
    }

//    @RequestMapping("/search/view")
    @RequestMapping("/display")
    public String test(HttpServletRequest request){
        String entity=request.getParameter("question");
        String fp = "";
        try {
            fp=readFileToString("src/main/resources/templates/display.html");
            String res = searchEntityString(entity);
            fp = fp.replace("\"searchLinkTarget\"",res);
        }catch (Exception e){
            if(entity != null) {
                fp = fp.replace("\"searchLinkTarget\"", "null");
            }
            logger.error("read fail");
        }finally {
            return fp;
        }

    }

    @GetMapping("/search/all/{entity}")
    public JSONObject searchAll(@PathVariable String entity){
        String res = search(entity);
        JSONObject json_res = JSONObject.fromObject(res);
        return json_res;
    }

    @GetMapping("/search/entity/array/{entity}")
    public JSONArray searchEntity(@PathVariable String entity){
        JSONArray jsonarray = JSONArray.fromObject(searchEntityString(entity));
        return jsonarray;
    }

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
        res+="]";
        res = res.replace(",,",",");
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

    public static void main(String[]args){
    }


}
