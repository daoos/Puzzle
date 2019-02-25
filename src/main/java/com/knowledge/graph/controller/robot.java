package com.knowledge.graph.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by geshuaiqi on 2018/9/16.
 */
@CrossOrigin
@Controller
public class robot {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @CrossOrigin
    @RequestMapping("/robot")
    public String robotchat(HttpServletRequest request,Model model) {
        // sprint 的模板语法
        return "chatrobot";
    }

    @CrossOrigin
    @RequestMapping("/test")
    public String test(HttpServletRequest request,Model model) {
        // sprint 的模板语法
        return "test";
    }

}
