package com.example.Spring.Action;
import com.example.Spring.Entities.Login;
import com.example.Spring.Service.LoginService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Controller
public class LoginController {
    private static final long serialVersionUID = 1L;

    LoginService loginService;
    int state;
    Login login;

    public void init(){ loginService = CtxUtil.getBean(LoginService.class); }

    @RequestMapping(value = "/login_sql",method = RequestMethod.POST)
    @ResponseBody
    public void ID_Login(HttpServletRequest request,HttpServletResponse response) throws Exception{
        System.out.println("响应到");
        init();
        PrintWriter out = response.getWriter();
        String username=request.getParameter("username");
        String password=request.getParameter("password");
        login=loginService.IDLogin(username,password);
        if(login!=null){
            System.out.println("登录取数据为："+login.getUserID()+"     昵称："+login.getNickName());
            //500异常    404不存在     200正常    405拒绝访问
            state=loginService.Judgment(login.getUserID());
            System.out.println(state+"←状态");
            //后期对于room界面记得做请求拦截
            if(state==500){
                 out.write("500");
            }
            else if(state==405){
                out.write("405");
            }
            else {
                out.write("200");
            }
        }
        else {
            System.out.println("404不存在");
            out.write("404");
        }
    }

    @RequestMapping(value = "/tourist_sql",method = RequestMethod.POST)
    @ResponseBody
    public void tourist_Login(HttpServletRequest request,HttpServletResponse response)throws Exception{
        response.setContentType("text/html; charset=UTF-8");
        init();
        PrintWriter out = response.getWriter();
        String tourist=request.getParameter("tourist_sql");
        if (tourist.equals("tourist")){
            out.print(loginService.tOuristLogin());
        }
        else {
            out.write("fail");
        }
    }
}
