package com.example.Spring;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
class WebSocketController {       //Model控制器 暂时未用到
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView goIndex(){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/Test.html");
        System.out.println("这里是MV："+mv);
        return mv;
    }
}