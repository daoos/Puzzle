package com.knowledge.graph.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by geshuaiqi on 2018/9/16.
 */
@CrossOrigin
@Controller
public class controller {

    @RequestMapping("/")
    public String index(Model model) {
        return "index";
    }

    @CrossOrigin
    @RequestMapping("/visualization")
    public String visualization(Model model) {
        // sprint 的模板语法
        model.addAttribute("searchTarget", "周杰伦");
        return "visualization";
    }

}
