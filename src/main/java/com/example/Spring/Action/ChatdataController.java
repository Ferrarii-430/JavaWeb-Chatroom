package com.example.Spring.Action;

import com.example.Spring.Entities.ChatData;
import com.example.Spring.Service.ChatDataService;
import com.example.Spring.Service.FriendService;
import com.example.Spring.Service.ImgDataService;
import com.example.Spring.Service.Mp3DataService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.lang.annotation.ElementType;

@Controller
public class ChatdataController {
    ImgDataService imgDataService;
    ChatDataService chatDataService;
    Mp3DataService mp3DataService;
    FriendService friendService;

    public void init_imgDataService(){ imgDataService = CtxUtil.getBean(ImgDataService.class); }
    public void init_chatDataService(){ chatDataService = CtxUtil.getBean(ChatDataService.class); }
    public void init_mp3DataService(){ mp3DataService = CtxUtil.getBean(Mp3DataService.class); }
    public void init_friendService(){ friendService = CtxUtil.getBean(FriendService.class); }

    public void initAll(){
        if(chatDataService==null) {
            init_chatDataService();
        }
        if(imgDataService==null) {
            init_imgDataService();
        }
        if(mp3DataService==null){
            init_mp3DataService();
        }
    }


    /*
     *接收图片数据并保存在本地
     * */
    @RequestMapping(value = "/upload_img",method = RequestMethod.POST)
    @ResponseBody
    public void upload_img(HttpServletRequest request,HttpServletResponse response)throws Exception{
        initAll();
        PrintWriter out=response.getWriter();
        JSONObject jsonObject= JSONObject.fromObject(request.getParameter("img_data"));
        out.print(imgDataService.base64_to_img(jsonObject));
    }


    /*
     *获取当前在前在线人数      虽然这个可能没啥用，接口先留着吧
     * */
    @RequestMapping(value = "/get_online_number",method = RequestMethod.GET)
    @ResponseBody
    public void get_online_number(HttpServletResponse response)throws Exception{
        initAll();
        PrintWriter out=response.getWriter();
        out.write(chatDataService.getmapSize());//返回在线人数
    }


    /*
     *获取当前在前在线名单
     * */
    @RequestMapping(value = "/get_online_namelist",method = RequestMethod.GET)
    @ResponseBody
    public void get_onlne_namelist(HttpServletResponse response)throws Exception{
        response.setContentType("text/html; charset=UTF-8");
        if(friendService==null){
            init_friendService();
        }
        PrintWriter out=response.getWriter();
        out.print(friendService.getOnlineUser_list());//返回实时在线用户数据
    }


    /*
     *用户请求Voice通话
     * */
    @RequestMapping(value = "/online_Voice",method = RequestMethod.POST)
    @ResponseBody
    public void online_Voice(HttpServletRequest request, HttpServletResponse response)throws Exception{
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out=response.getWriter();
        String userID=request.getParameter("userID");
        String receive_ID=request.getParameter("receive_ID");
        initAll();
        out.write(chatDataService.creatVoice_room(userID,receive_ID));
    }


    /*
     *用户关闭Voice通话 并删除房间
     * */
    @RequestMapping(value = "/online_Voice_close",method = RequestMethod.POST)
    @ResponseBody
    public void online_Voice_close(HttpServletRequest request, HttpServletResponse response)throws Exception{
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out=response.getWriter();
        String userID=request.getParameter("userID");
        String receive_ID=request.getParameter("receive_ID");
        initAll();
        if (chatDataService.del_Voice_room(userID,receive_ID)){
            out.write("success");
        }else {
            out.write("fail");
        }
    }


    /*
     *用户请求Voide视频通话
     * */
    @RequestMapping(value = "/online_Voide",method = RequestMethod.POST)
    @ResponseBody
    public void online_Voide(HttpServletRequest request, HttpServletResponse response)throws Exception{
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out=response.getWriter();
        String userID=request.getParameter("userID");
        String receive_ID=request.getParameter("receive_ID");
        if (userID != null && receive_ID != null) {
            initAll();
            out.write(chatDataService.creatVoide_room(userID, receive_ID));
        }
        else {
            out.write("失败");
        }
    }


    /*
     *用户关闭Voide通话 并删除房间
     * */
    @RequestMapping(value = "/online_Voide_close",method = RequestMethod.POST)
    @ResponseBody
    public void online_Voide_close(HttpServletRequest request, HttpServletResponse response)throws Exception {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        String userID = request.getParameter("userID");
        String receive_ID = request.getParameter("receive_ID");
        System.out.println(userID+receive_ID);
        if (userID != null && receive_ID != null) {
            initAll();
            System.out.println("开始关闭房间");
            if (chatDataService.del_Voide_room(userID, receive_ID)) {
                out.write("成功");
            } else {
                out.write("失败");
            }
        }
        else {
            out.write("未知错误");
        }
    }


    /*
     *接收MP3数据并保存在本地
     * */
    @RequestMapping(value = "/upload_mp3",method = RequestMethod.POST)
    @ResponseBody
    public void upload_mp3(HttpServletRequest request,HttpServletResponse response)throws Exception{
        initAll();
        PrintWriter out=response.getWriter();
        JSONObject jsonObject= JSONObject.fromObject(request.getParameter("mp3_data"));
        out.print(mp3DataService.base64_to_mp3(jsonObject));
    }

}
