package com.example.Spring.Action;

import com.example.Spring.Service.RegisterService;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Controller
public class RegisterController {

    //这里为了方便后面填写邮箱数据 另写注入
    @Resource(name = "registerService")
    RegisterService registerService;


    public void initAll(){init_registerService();}
    public void init_registerService(){ registerService = CtxUtil.getBean(RegisterService.class); }


    @RequestMapping(value = "/user_register",method = RequestMethod.POST)
    @ResponseBody
    public void user_register(HttpServletRequest request, HttpServletResponse response)throws Exception{
        response.setContentType("text/html; charset=UTF-8");
        JSONObject jsonObject= JSONObject.fromObject(request.getParameter("DataName"));
        System.out.println(jsonObject.toString());
        PrintWriter out=response.getWriter();
        out.write(registerService.Register_user(jsonObject));
    }

    @RequestMapping(value = "/user_register_email",method = RequestMethod.POST)
    @ResponseBody
    public void user_register_email(HttpServletRequest request, HttpServletResponse response)throws Exception{
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out=response.getWriter();
        JSONObject jsonObject= JSONObject.fromObject(request.getParameter("DataName"));
        String Email=jsonObject.getString("email");
        if (registerService.email_Thread(Email)){
            out.write("done");
        }else {
            out.write("fail");
        }
    }
}
