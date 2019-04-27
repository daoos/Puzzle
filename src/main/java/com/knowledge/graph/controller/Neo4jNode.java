package com.knowledge.graph.controller;

//import net.sf.json.JSONArray;
import com.alibaba.fastjson.JSONArray;
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

@CrossOrigin
@RestController
public class Neo4jNode {

    @GetMapping("/neo4j/{keyword}")
    public JSONArray neo4jSearch(@PathVariable String keyword) throws SQLException{

        StringBuffer res = new StringBuffer("[");
        Connection con = DriverManager.getConnection("jdbc:neo4j:http://120.77.220.71:7474", "neo4j", "302899");
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
                if(i++ > 30){
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
                if(i++ > 30){
                    break;
                }
            }
        }catch (Exception e){
            System.out.println("读取异常2");
        }
        finally {
            con.close();
        }

        res.append("]");
        System.out.println(res.toString());
        return JSONArray.parseArray(res.toString());
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
