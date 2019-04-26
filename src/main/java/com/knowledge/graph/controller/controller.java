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
public class controller {
    private Logger logger = LoggerFactory.getLogger(getClass());

//    返回首页
    @RequestMapping("/")
    public String index(Model model) {
        return "index";
    }

    @CrossOrigin
    @RequestMapping("/visualization")
    public String visualization(HttpServletRequest request,Model model) {
        // spring 的模板语法
        String searchMethod = request.getParameter("searchby");
        String question = request.getParameter("question");
        if(searchMethod.equals("entity")){
            logger.info(question);
            model.addAttribute("searchTarget", question);
            return "visualization";
        }
        model.addAttribute("searchTarget", "周杰伦");
        return "visualization";
    }

    @CrossOrigin
    @RequestMapping("/3dnodes")
    public String nodes(HttpServletRequest request,Model model) {
        return "3dnodes";
    }

    @CrossOrigin
    @RequestMapping("/voice")
    public String voice(HttpServletRequest request,Model model) {
        return "voice";
    }


    @RequestMapping("/monitor")
    public String monitor(HttpServletRequest request,Model model) {
        return "monitor";
    }
}
