package com.knowledge.graph.controller;

import net.sf.json.JSONArray;
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
        String res = "[";
        Connection con = DriverManager.getConnection("jdbc:neo4j:http://120.77.220.71:7474", "neo4j", "302899");
        // Querying
        try (Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery("match (source)-[rela]->(target) where target.name=\""+keyword+"\" return source.name,rela.name,target.name");
            while (rs.next()) {
                if(res.length() > 1){
                    res += ",";
                }
                String source = rs.getString(1);
                String rela = rs.getString(2);
                res += "{source: \"" + source + "\",target:\"" + keyword + "\",type:\"resolved\",rela: \"" + rela + "\"}";
            }
        }finally {
            con.close();
        }

        con = DriverManager.getConnection("jdbc:neo4j:http://120.77.220.71:7474", "neo4j", "302899");
        try (Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery("match (source)-[rela]->(target) where source.name=\""+keyword+"\" return source.name,rela.name,target.name");
            while (rs.next()) {
                if(res.length() > 1){
                    res += ",";
                }
                String target = rs.getString(3);
                String rela = rs.getString(2);
                res += "{source: \"" + keyword + "\",target:\"" + target + "\",type:\"resolved\",rela: \"" + rela + "\"}";
            }
        }finally {
            con.close();
        }


        res+="]";

        return JSONArray.fromObject(res);
//        return res;
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
