package com.knowledge.graph.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by geshuaiqi on 2019/4/5.
 */
@CrossOrigin
@RestController
public class distribute {
    @RequestMapping("/monitor")
    public String index(Model model) {
        return "index";
    }
}
