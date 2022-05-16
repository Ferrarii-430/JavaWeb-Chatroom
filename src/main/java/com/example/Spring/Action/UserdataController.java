package com.example.Spring.Action;


import com.example.Spring.Service.FriendService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Controller
public class UserdataController {

    FriendService friendService;

    public void init(){ friendService = CtxUtil.getBean(FriendService.class); }




    /**
     *获取好友列表     目前好友关系库有点乱，后期记得整理
     * */
    @RequestMapping(value = "/user_relationship",method = RequestMethod.POST)
    @ResponseBody
    public void user_relationship(HttpServletRequest request, HttpServletResponse response)throws Exception{
        response.setContentType("text/html; charset=UTF-8");
        if (friendService==null) {
            init();
        }
        String id=request.getParameter("userID");
        System.out.println(id+"用户关系");
        PrintWriter out=response.getWriter();
        out.print(friendService.get_friend_list(id));
    }
}
