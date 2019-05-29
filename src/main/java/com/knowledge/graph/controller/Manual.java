package com.knowledge.graph.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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

    @PostMapping("/uploadNodeInformation")
    @ResponseBody
    public String update(String label, String name, String attrinfo) {
        System.out.println(label);
        System.out.println(name);
        System.out.println(attrinfo);
        return "122";
    }
}
