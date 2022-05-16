package com.example.Spring.Action;

import com.example.Spring.Service.LoginService;
import com.example.Spring.Service.PersonalService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;


@Controller
public class PersonalController {
    PersonalService personalService;

    LoginService loginService;

    public void init1(){ personalService = CtxUtil.getBean(PersonalService.class); }
    public void init2(){ loginService = CtxUtil.getBean(LoginService.class); }
    public void initAll(){
        if(personalService==null) {
            init1();
        }
        if(loginService==null) {
            init2();
        }
    }

    @RequestMapping(value = "/myhead_img",method = RequestMethod.POST)
    @ResponseBody
    public void get_headIMG(HttpServletRequest request, HttpServletResponse response) throws Exception{
        response.setContentType("text/html; charset=UTF-8");
        initAll();
        JSONObject jsonObject= JSONObject.fromObject(request.getParameter("dataName"));
        String id=jsonObject.getString("userID");
        PrintWriter out=response.getWriter();//图片数据判断及回传
        out.write(personalService.get_myheadIMG(id));
    }

    /**
     *获取系统通知列表
     * */
    @RequestMapping(value = "/user_notice",method = RequestMethod.POST)
    @ResponseBody
    public void user_notice(HttpServletRequest request, HttpServletResponse response)throws Exception{
        response.setContentType("text/html; charset=UTF-8");
        initAll();
        JSONObject jsonObject= JSONObject.fromObject(request.getParameter("dataName"));
        String id=jsonObject.getString("userID");
        System.out.println(id+"用户获取通知");
        PrintWriter out=response.getWriter();
        out.print(personalService.get_systemXML(id));
    }

    /**
     *处理用户的添加好友请求
     * */
    @RequestMapping(value = "/user_add_friend",method = RequestMethod.POST)
    @ResponseBody
    public void user_add_friend(HttpServletRequest request, HttpServletResponse response)throws Exception{
         response.setContentType("text/html; charset=UTF-8");
         PrintWriter out=response.getWriter();
         String userID=request.getParameter("userID");
         String receiveID=request.getParameter("receiveID");
         String remarks=request.getParameter("remarks");
         initAll();
         if (personalService.user_add_friend(userID,receiveID,remarks)){
             out.write("success");
         }
         else {
             out.write("fail");
         }
    }


    /**
     *修改用户个人的通知阅读状态
     * */
    @RequestMapping(value = "/set_userXML_state",method = RequestMethod.POST)
    @ResponseBody
    public void set_userXML_state(HttpServletRequest request, HttpServletResponse response)throws Exception{
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out=response.getWriter();
        String userID=request.getParameter("userID");
        int click_number= Integer.parseInt(request.getParameter("number"));
        initAll();
        if (personalService.set_userXML_state(userID,click_number)){
            out.write("修改阅读状态成功");
        }
        else {
            out.write("修改阅读状态失败");
        }
    }


    /**
     *删除用户个人的添加好友信息
     * */
    @RequestMapping(value = "/del_systemNoticeXML",method = RequestMethod.POST)
    @ResponseBody
    public void del_systemNoticeXML(HttpServletRequest request, HttpServletResponse response)throws Exception{
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out=response.getWriter();
        String userID=request.getParameter("userID");
        int click_number= Integer.parseInt(request.getParameter("del_number"));
        System.out.println(click_number);
        initAll();
        if(personalService.del_systemNoticeXML(userID,click_number)){
            out.write("删除用户个人系统通知成功");
        }
        else {
            out.write("删除用户个人系统通知失败");
        }
    }


    /**
     *确认添加好友信息 并反馈
     * */
    @RequestMapping(value = "/friendapply_feedback",method = RequestMethod.POST)
    @ResponseBody
    public void friendapply_feedback(HttpServletRequest request, HttpServletResponse response)throws Exception{
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out=response.getWriter();
        initAll();
        String userID=request.getParameter("userID");
        String boole=request.getParameter("boole");
        String friendName=request.getParameter("friendName");
        if (personalService.creat_friendXML(userID,friendName,"txt",boole)){
            if (loginService!=null){
                init2();
            }
            assert loginService != null; //断言
            int id=loginService.getUserID(userID);
            out.print(id);
        }else {
            System.out.println("添加好友后创建XML出现未知错误");
        }
    }


    /**
     *接上面那个东西 这里用来更新添加好友后的数据   希望没人看到这个东西 这玩意完全可以整合上面那个 但优化是后面的事
     * */
    @RequestMapping(value = "/update_newfriend",method = RequestMethod.POST)
    @ResponseBody
    public void update_newfriendXML(HttpServletRequest request, HttpServletResponse response)throws Exception{
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out=response.getWriter();
        JSONObject jsonObject= JSONObject.fromObject(request.getParameter("dataName"));
        String userID=jsonObject.getString("userID");
        String receiveID=jsonObject.getString("receiveID");
        initAll();
        out.print(personalService.get_newfriend_data(userID,receiveID));
    }


    /**
    获取聊天记录数组
     */
    @RequestMapping(value = "/chatdata_array",method = RequestMethod.POST)
    @ResponseBody
    public void chatdata_array(HttpServletRequest request, HttpServletResponse response)throws Exception{
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out=response.getWriter();
        JSONArray ID_list = JSONArray.fromObject(request.getParameter("list_ID"));
        String userID=request.getParameter("userID");
        System.out.println(ID_list);
        System.out.println(ID_list.get(0));
        initAll();
        if (ID_list.get(0) !=null) {
            out.print(personalService.get_allfriend_chatdata(ID_list, userID));
        }else {
            out.write("获取聊天记录失败");
        }
    }


    /**
     回传用户真实ID
     */
    @RequestMapping(value = "/get_User_realID",method = RequestMethod.POST)
    @ResponseBody
    public void get_User_realID(HttpServletRequest request, HttpServletResponse response)throws Exception{
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out=response.getWriter();
        JSONObject jsonObject= JSONObject.fromObject(request.getParameter("dataName"));
        String userID=jsonObject.getString("userID");
        initAll();
        out.print(personalService.getUser_ID(userID));
    }


    /*
     *接收用户发送的聊天数据
     * */
    @RequestMapping(value = "/receive_chatdata",method = RequestMethod.POST)
    @ResponseBody
    public void receive_chatdata(HttpServletRequest request,HttpServletResponse response)throws Exception {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject chat_txt= JSONObject.fromObject((request.getParameter("chat_txt")));
        initAll();
        if (personalService.chat_txt_add(chat_txt)) {
            out.write("成功");
        }
        else {
            out.write("null");
        }
    }


    /*
     *获取聊天室聊天数据
     * */
    @RequestMapping(value = "/get_chatroom_chatdata",method = RequestMethod.GET)
    @ResponseBody
    public void get_chatroom_chatdata(HttpServletResponse response)throws Exception {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        initAll();
        out.print(personalService.get_chatroom_chatdata());
    }
}
