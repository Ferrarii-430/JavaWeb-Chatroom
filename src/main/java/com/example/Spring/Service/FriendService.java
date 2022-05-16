package com.example.Spring.Service;


import com.example.Spring.All_data_Handling;
import com.example.Spring.DAO.FriendMapper;
import com.example.Spring.DAO.LoginMapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.*;

@Service
public class FriendService {
    List real_userid_list,nickname_ist=new ArrayList();
    Properties prop = All_data_Handling.readPropertiesFile();

    @Resource
    FriendMapper friendMapper;

    @Resource
    LoginMapper loginMapper;

    @Resource
    ImgDataService imgDataService;

    String path=(String)prop.get("path.headimg_real");

    /**
     * 获取用户好友列表，用于页面显示
     */
    public JSONArray get_friend_list(String id) {
        JSONArray jsonArray=new JSONArray();
        real_userid_list=friendMapper.friend_list(String.valueOf(loginMapper.getUserID(id)));//获取登录用户的用户关系列表
        if(!real_userid_list.isEmpty()){
            Collections.sort(real_userid_list);  //必须进行升序排序。不然数据会混乱
            nickname_ist = loginMapper.getNickname_list(real_userid_list);
            int size=real_userid_list.size();//防止多次获取size
            for (int i = 0; i < size; i++) {
                //real_userid_list.set(i,nickname_ist.get(i)+make_friend_list((String)real_userid_list.get(i),path));
                JSONObject jsonObject = make_friend_list((String) real_userid_list.get(i), (String) nickname_ist.get(i), path);
                jsonArray.add(jsonObject);
            }
            return jsonArray;
        }
        return null;
    }


    /**
     * 获取在线用户列表
     */
    public JSONArray getOnlineUser_list(){
        if (ChatDataService.map.size()==1){
            return null;
        }
        JSONArray jsonArray=new JSONArray();
        Collection collection=ChatDataService.map.values();
        List OnlineID_List= new ArrayList(collection);//map转list  用户ID
        Collections.sort(OnlineID_List);
        List OnlineNick_List=loginMapper.getNickname_list(OnlineID_List);
        int size=OnlineNick_List.size();//防止多次获取size
        for (int i = 0; i < size; i++) {
            JSONObject jsonObject = make_friend_list((String) OnlineID_List.get(i), (String) OnlineNick_List.get(i), path);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    /**
     * 判断有无头像
     */
    public JSONObject make_friend_list(String id,String nickname,String path){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("userid",id);
        jsonObject.put("type","addUser");
        jsonObject.put("nickname",nickname);
        if (imgDataService.exist_headimg(id,path)) {
            String imgPath=(String)prop.get("path.headimg");
            jsonObject.put("headimg",imgPath+id+".jpg");
        }
        else {
            jsonObject.put("headimg","NoAvatar");
        }
        return jsonObject;
    }


    /**
    获取新上线用户数据
     */
    public JSONObject getOnlineUser(int id){
        String path=(String)prop.get("path.headimg_real");
        String nick=loginMapper.getNickname(String.valueOf(id));
        return make_friend_list(String.valueOf(id),nick, path);
    }


    /**
     删除下线的用户
     */
    public JSONObject delOnlineUser(String id){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("userid",id);
        jsonObject.put("type","delUser");
        return jsonObject;
    }
}
