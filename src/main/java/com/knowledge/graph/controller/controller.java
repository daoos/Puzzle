package com.knowledge.graph.controller;

import com.knowledge.graph.model.Person;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by geshuaiqi on 2018/9/16.
 */
@Controller
public class controller {

    @RequestMapping("/")
    public String index(Model model) {
        Person person1 = new Person("长相思", 18);
        Person person2 = new Person("花似伊", 19);
        Person person3 = new Person("柳似伊", 20);
        Person person4 = new Person("柳似伊", 20);

        List<Person> people = new ArrayList<>();
        people.add(person1);
        people.add(person2);
        people.add(person3);
        people.add(person4);

        model.addAttribute("people", people);
        return "index";
    }

    @RequestMapping("/display")
    public String display(Model model) {
        return "display";
    }
}
