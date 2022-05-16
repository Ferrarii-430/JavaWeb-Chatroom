package com.example.Spring.Service;

import com.example.Spring.DAO.LoginMapper;
import com.example.Spring.Entities.Login;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoginService {

    @Resource
    LoginMapper loginMapper;

    public Login IDLogin(String ID,String PassWord){
        return loginMapper.IDLogin(ID,PassWord);
    }

    public int Judgment(String ID){
        return loginMapper.Judgment(ID);
    }

    public int getUserID(String ID){ return loginMapper.getUserID(ID);}

    public String getNickname(String ID){return  loginMapper.getNickname(ID); }


    /**
     *游客登录
     */
    public JSONObject tOuristLogin(){
        int id=100001;
        List id_list=new ArrayList();
        for (int i = 0; i < 9; i++) {
            id_list.add(i+id);
        }
        JSONObject jsonObject=new JSONObject();
        List list=loginMapper.getJudgment_list(id_list);
        for (int i = 0; i < list.size(); i++) {
            if (String.valueOf(list.get(i)).equals("200")){
                i++;
                String user="游客账号"+i;
                jsonObject.put("state","none");
                jsonObject.put("user",user);
                jsonObject.put("pass","12580");
                System.out.println("游客模式:   游客ID→"+i+"已登录");
                return jsonObject;
            }
        }
        jsonObject.put("state","fail");
        return jsonObject;
    }


    /**
     *普通用户登录   后期记得挪过来
     */
}
