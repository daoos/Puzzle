package com.knowledge.graph.controller;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Created by geshuaiqi on 2019/5/2.
 */


@Component
public class InitailConfig implements ApplicationRunner {
    static HashMap<String, String> serverNode = new HashMap<String, String>();
    @Override
    public void run(ApplicationArguments args) throws Exception {
        serverNode.put("M0-深圳", "120.77.220.71");
        serverNode.put("M1-台湾", "35.221.173.25");
        serverNode.put("M2-香港", "34.92.13.105");
    }
}
