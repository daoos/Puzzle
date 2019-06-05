package com.knowledge.graph.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.neo4j.driver.v1.*;
import static org.neo4j.driver.v1.Values.parameters;
import static org.neo4j.driver.v1.Values.value;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by geshuaiqi on 2019/5/23.
 */
@CrossOrigin
@Controller
public class Manual {
    private Logger logger = LoggerFactory.getLogger(getClass());
    HashMap<String, String> ipmap = new HashMap<String, String>();
    Manual(){
        ipmap.put("深圳","120.77.220.71");
        ipmap.put("香港","34.92.13.105");
        ipmap.put("洛杉矶","35.236.82.226");
        ipmap.put("台湾","35.221.173.25");
    }

    @RequestMapping("/manual_link")
    public String manual_link(HttpServletRequest request,Model model) {
        return "manual_link";
    }
    @RequestMapping("/manual_node")
    public String manual_node(HttpServletRequest request,Model model) {
        return "manual_node";
    }
    @RequestMapping("/manual_node_update")
    public String manual_node_update(HttpServletRequest request,Model model) {
        return "manual_node_update";
    }
    @RequestMapping("/manual_link_update")
    public String manual_link_update(HttpServletRequest request,Model model) {
        return "manual_link_update";
    }

    @PostMapping("/createNode")
    @ResponseBody
    public Map<String, String> createNode(int region, String label, String name, String attrinfo) {
        Map<String, String> res = new HashMap<String, String>();
        System.out.println(region);
        System.out.println(label);
        System.out.println(name);
        System.out.println(attrinfo);
        if(checkExistNodeByType(region,label,name)){
            res.put("status","repeat");
            return res;
        }
        Map<String, String> attrs = new HashMap<String, String>();
        StringBuilder sql = new StringBuilder("CREATE (a:"+label+"{name:\""+name+"\"");

        for(String line: attrinfo.split("!!!")){
            String[] attr = line.split("###");
            attrs.put(attr[0],attr[1]);
            sql.append(","+attr[0]+":\"" + attr[1] + "\"");
        }
        sql.append(",timestamp:\""+System.currentTimeMillis()+"\"})");
        System.out.println(sql.toString());
        Driver driver = GraphDatabase.driver( "bolt://"+Neo4jNode.IPs[region]+":7687", AuthTokens.basic( "neo4j", "302899" ) );
        Session session = driver.session();
        session.run(sql.toString());
        session.close();
        driver.close();
        res.put("status","succeed");
        return res;
    }

    public static boolean checkExistNodeByType(int ip, String label, String name){
        try {
            if (Neo4jNode.exportnode(label, name, ip) == null) {
                return false;
            }
            return true;
        }catch (Exception e){}
        return false;
    }

    @PostMapping("/createLink")
    @ResponseBody
    public Map<String, String> createLink(int region, String attrinfo) {
        Map<String, String> res = new HashMap<String, String>();
        System.out.println(region);
        System.out.println(attrinfo);

        for(String line : attrinfo.split("!!!")){
            String[] attrs = line.split("###");
            String sql = String.format("match (p), (q) where p.name='%s'and q.name='%s' create (p)-[rel:link{name:'%s',timestamp:'%s'}]->(q)",attrs[0],attrs[2],attrs[1],System.currentTimeMillis());
            System.out.println(sql);
            Driver driver = GraphDatabase.driver( "bolt://"+Neo4jNode.IPs[region]+":7687", AuthTokens.basic( "neo4j", "302899" ) );
            Session session = driver.session();
            session.run(sql);
            session.close();
            driver.close();
            Neo4jNode.cache.remove(attrs[0]);
            Neo4jNode.cache.remove(attrs[2]);
        }
        res.put("status","succeed");
        return res;
    }

    @PostMapping("/updateNode")
    @ResponseBody
    public Map<String, String> updateNode(int serverID, String label, String name, String attr_name, String attr_val, String timestamp) {
        Map<String, String> res = new HashMap<String, String>();
        try{
            String sql = String.format("match (a:%s) where a.name ='%s' set a.%s = '%s'",label,name,attr_name,attr_val);
            System.out.println(sql);
            Driver driver = GraphDatabase.driver( "bolt://"+Neo4jNode.IPs[serverID]+":7687", AuthTokens.basic( "neo4j", "302899" ) );
            Session session = driver.session();
            session.run(sql);
            session.close();
            driver.close();
            res.put("status","succeed");
            Neo4jNode.cache.remove(name);
        }catch (Exception e){
            res.put("status","exception");
        }
        return res;
    }

    @PostMapping("/deleteNodeAttr")
    @ResponseBody
    public Map<String, String> deleteNodeAttr(int serverID, String label, String name, String attr_name, String attr_val, String timestamp) {
        Map<String, String> res = new HashMap<String, String>();
        try{
            String sql = String.format("match (a:%s) where a.name ='%s' remove a.%s",label,name,attr_name,attr_val);
            System.out.println(sql);
            Driver driver = GraphDatabase.driver( "bolt://"+Neo4jNode.IPs[serverID]+":7687", AuthTokens.basic( "neo4j", "302899" ) );
            Session session = driver.session();
            session.run(sql);
            session.close();
            driver.close();
            res.put("status","succeed");
        }catch (Exception e){
            res.put("status","exception");
        }
        return res;
    }

    @PostMapping("/deleteLink")
    @ResponseBody
    public Map<String, String> updateLink(int serverID, String source, String rela, String target) {
        Map<String, String> res = new HashMap<String, String>();
        try{
            String sql = String.format("match (p)-[r]->(q) where p.name ='%s' and q.name='%s' and r.name='%s' delete r",source,target,rela);
            System.out.println(sql);
            Driver driver = GraphDatabase.driver( "bolt://"+Neo4jNode.IPs[serverID]+":7687", AuthTokens.basic( "neo4j", "302899" ) );
            Session session = driver.session();
            session.run(sql);
            session.close();
            driver.close();
            res.put("status","succeed");
            Neo4jNode.cache.remove(source);
            Neo4jNode.cache.remove(target);
        }catch (Exception e){
            res.put("status","exception");
        }
        return res;
    }
}
