package com.knowledge.graph.controller;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Created by geshuaiqi on 2019/5/2.
 */

class Node{
    String _ip;
    String _location;

    Node(String ip, String location){
        _ip = ip;
        _location = location;
    }

    public String get_ip() {
        return _ip;
    }

    public String get_location() {
        return _location;
    }
}

@Component
public class InitailConfig implements ApplicationRunner {
    static HashMap<String, Node> serverNode = new HashMap<String, Node>();
    @Override
    public void run(ApplicationArguments args) throws Exception {
        serverNode.put("M0", new Node("120.77.220.71","深圳"));
        serverNode.put("M1", new Node("35.221.173.25","台湾"));
        serverNode.put("M2", new Node("34.92.13.105", "香港"));
    }


}


