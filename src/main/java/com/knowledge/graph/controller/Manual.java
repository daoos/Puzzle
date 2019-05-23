package com.knowledge.graph.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by geshuaiqi on 2019/5/23.
 */
@CrossOrigin
@Controller
public class Manual {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping("/manual")
    public String monitor(HttpServletRequest request,Model model) {
        return "manual";
    }
}
