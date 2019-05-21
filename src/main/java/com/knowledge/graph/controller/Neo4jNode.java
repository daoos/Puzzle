package com.knowledge.graph.controller;

//import net.sf.json.JSONArray;
import com.alibaba.fastjson.JSONArray;
import org.neo4j.jdbc.impl.ListArray;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;


@CrossOrigin
@RestController
public class Neo4jNode {
    static HashMap<String, String> cache = new HashMap<String, String>();
    static boolean cacheEnable = true;
    static String[] IPs = {"120.77.220.71","34.92.13.105","35.236.82.226"};

    // 静态查询方法，同时供其他模块调用
    public static String neo4jSearchString(String keyword, int ip) throws SQLException{
        StringBuffer res = new StringBuffer();
        Connection con = DriverManager.getConnection("jdbc:neo4j:http://"+IPs[ip]+":7474", "neo4j", "302899");
//        alibaba
//        Connection con = DriverManager.getConnection("jdbc:neo4j:http://120.77.220.71:7474", "neo4j", "302899");
//        hongkong
//        Connection con = DriverManager.getConnection("jdbc:neo4j:http://34.92.13.105:7474", "neo4j", "302899");
//        losangel
//        Connection con = DriverManager.getConnection("jdbc:neo4j:http://35.236.82.226:7474", "neo4j", "302899");


        // Querying
        String sql;
        try (Statement stmt = con.createStatement()) {
            sql = String.format("match (source)-[rela]->(target) where target.name= \"%s\" return source.name,rela.name,target.name", keyword);
            System.out.println(sql);
            ResultSet rs = stmt.executeQuery(sql);
            int i=0;
            while (rs.next()) {
                if(res.length() > 1){
                    res.append(",");
                }
                String source = rs.getString(1);
                String rela = rs.getString(2);
                res.append("{source: \"" + source + "\",target:\"" + keyword + "\",type:\"resolved\",rela: \"" + rela + "\"}");
                if(i++ > 40){ // 不要渲染太多，可观性变差
                    break;
                }
            }
        }catch (Exception e){
            System.out.println("读取异常1");
        }
        finally {
            con.close();
        }

        con = DriverManager.getConnection("jdbc:neo4j:http://120.77.220.71:7474", "neo4j", "302899");
        try (Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(String.format("match (source)-[rela]->(target) where source.name= \"%s\" return source.name,rela.name,target.name", keyword));
            int i=0;
            while (rs.next()) {
                if(res.length() > 1){
                    res.append(",");
                }
                String target = rs.getString(3);
                String rela = rs.getString(2);
                res.append("{source: \"" + keyword + "\",target:\"" + target + "\",type:\"resolved\",rela: \"" + rela + "\"}");
                if(i++ > 50){
                    break;
                }
            }
        }catch (Exception e){
            System.out.println("读取异常2");
        }
        finally {
            con.close();
        }

//        res.append("]");

        return res.toString();
    }

    @GetMapping("/neo4j/{keyword}")
    public JSONArray neo4jSearch(@PathVariable String keyword) throws SQLException{
        if(cacheEnable && cache.containsKey(keyword)){
            System.out.println("Hit"+keyword);
            return JSONArray.parseArray(cache.get(keyword));
        }

        String res = "[" + Neo4jNode.neo4jSearchString(keyword,0) + "]";

        System.out.println(res);
        if(cacheEnable) cache.put(keyword,res);
        return JSONArray.parseArray(res);
    }


    // map-reduce 相关
    @GetMapping("/multi/{ip}/{multiword}")
    public String neo4jMultiSearch(@PathVariable String multiword, @PathVariable int ip) {
        ArrayList<String> words = new ArrayList<String>();
        for(String word : multiword.split(",")){
            words.add(word);
        }
        return MultiSearch(words, ip, 0, words.size());
    }

    public String MultiSearch(List<String> multiword, int ip, int start, int end){
        StringBuilder res = new StringBuilder("来自 " + IPs[ip] + "\n");
        try {
            for (int i=start; i<end; i++) {
                res.append(neo4jSearchString(multiword.get(i),ip));
            }
        }catch (Exception e){
            return "Incorrect para";
        }
        return res.toString();
    }

    static int mapreduce_ip = 0;
    private static synchronized int getip(){
        int target = mapreduce_ip;
        mapreduce_ip = (mapreduce_ip+1)%3;
        return target;
    }

    public class mapreduce extends RecursiveTask<String>{
        private ArrayList<String> query;
        private int start;
        private int end;
        private boolean canCompute = false;


        public mapreduce(ArrayList _query, int _start, int _end, boolean _canCompute){
            query = _query;
            start = _start;
            end = _end;
            canCompute = _canCompute;
        }

        protected String compute() {
            if(canCompute){
                return MultiSearch(query, getip(), start, end)+"\n";
            }

            int gap = (end - start)/3;
            mapreduce task_0 = new mapreduce(query, start, start+gap, true);
            mapreduce task_1 = new mapreduce(query, start+gap, start+2*gap, true);
            mapreduce task_2 = new mapreduce(query, start+2*gap, end, true);
            // fork
            task_0.fork();
            task_1.fork();
            task_2.fork();
            // join
            return task_0.join() + task_1.join() + task_2.join();
        }
    }

    @GetMapping("/mapreduce/{multiword}")
    public String neo4jMultiSearchMapReduce(@PathVariable String multiword) {
        ArrayList<String> words = new ArrayList<String>();
        for(String word : multiword.split(",")){
            words.add(word);
        }
        ForkJoinPool pool = new ForkJoinPool();
        mapreduce task = new mapreduce(words, 0,words.size(), false);
        Future<String> result = pool.submit(task);
        try{
            return result.get();
        }catch(Exception e){
            return "Exception occurs when compute parallel";
        }finally{
            pool.shutdown();
        }

//        return MultiSearch(multiword, 0);
    }


    public static void main(String[] args) throws SQLException  {
        Connection con = DriverManager.getConnection("jdbc:neo4j:http://120.77.220.71:7474", "neo4j", "302899");
        // Querying
        try (Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery("match (e)-[r]->(n) where n.name=\"阿法骨化醇胶囊\" return e.name,r.name,n.name");
            while (rs.next()) {
                System.out.println(rs.getString(1));
                System.out.println(rs.getString(2));
                System.out.println(rs.getString(3) + "\n");
            }
        }finally {
            con.close();
        }
    }
}
