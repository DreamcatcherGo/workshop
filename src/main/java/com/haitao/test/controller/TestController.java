package com.haitao.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Description:
 * Created by 王海涛（ht.wang05@zuche.com） on 2017/3/2 18:27
 */
@Controller
@RequestMapping("/testController")
public class TestController{

    public TestController() {
        System.out.println("TestController constructed......");
    }

    @RequestMapping(value="/test",method= RequestMethod.GET)
    public ModelAndView test(){
        ModelAndView mav = new ModelAndView("index.jsp");
        return mav;
    }
}
