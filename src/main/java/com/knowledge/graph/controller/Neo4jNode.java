package com.knowledge.graph.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
public class Neo4jNode {

    public static void main(String[] args) throws SQLException  {
        Connection con = DriverManager.getConnection("jdbc:neo4j:http://120.77.220.71:7474", "neo4j", "302899");
        // Querying
        try (Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery("MATCH (n) RETURN n limit 5");
            while (rs.next()) {
                System.out.println(rs.getString("n"));
            }
        }
    }
}
