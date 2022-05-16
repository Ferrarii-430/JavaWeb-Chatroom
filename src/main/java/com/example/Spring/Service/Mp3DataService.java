package com.example.Spring.Service;

import com.example.Spring.All_data_Handling;
import com.example.Spring.DAO.LoginMapper;
import com.example.Spring.myHandler;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Properties;

@Service
public class Mp3DataService {
    @Resource
    LoginMapper loginMapper;

    final String symbol="/";
    final String mp3="mp3";
    Properties prop = All_data_Handling.readPropertiesFile();
    All_data_Handling all_data_Handling=new All_data_Handling();

    //真实地址
    final String path_real_friendXML=(String)prop.get("path.friendchat");
    final String path_real_friend_mp3=(String)prop.get("path.friend_mp3");
    final String path_real_mp3All=(String)prop.get("path.real_mp3All");
    final String path_real_chatroomXML=(String)prop.get("path.chatroomXML");
    final String path_real_headimg=(String)prop.get("path.headimg_real");

    //虚拟地址
    final String path_chatroomMP3=(String)prop.get("path.chatroomMP3");
    final String path_chatdataMP3=(String)prop.get("path.chatdataMP3");


    public String base64_to_mp3(JSONObject img_data){
        String type=img_data.getString("type");
        String base64=img_data.getString("base64");
        String SendID=img_data.getString("SendID");
        String real_sendID=String.valueOf(loginMapper.getUserID(SendID));
        String path;
        try {
            if(type.equals("mp3All")) {
                String headimg="no";
                if (all_data_Handling.exist_chatroomXML(path_real_chatroomXML)){
                    return null;
                }
                path=path_real_mp3All;
                String mp3Name=all_data_Handling.base64_to_mp3(path,base64);   //传给处理生成MP3
                path=path_chatroomMP3+mp3Name;
                String nickname=loginMapper.getNickname(SendID);
                if(all_data_Handling.exist_file(SendID,path_real_headimg)){
                    headimg="yes";
                }
                if (ChatDataService.pam.size()!=0){
                    JSONObject jsonObject=new JSONObject();
                    jsonObject.put("type","news_room");
                    jsonObject.put("userid",SendID);jsonObject.put("nickname",nickname);jsonObject.put("chat",path);
                    jsonObject.put("datatype",type);jsonObject.put("headimg",headimg);jsonObject.put("date",all_data_Handling.get_time());
                    myHandler.Push_txtAll(jsonObject);
                }
                all_data_Handling.add_chatroomXML(path_real_chatroomXML,real_sendID,path,nickname,headimg,mp3);   //给chatroomgXML中添加mp3信息
                return mp3Name;
            }
            else if(type.equals("mp3Single")){
                String receiveID=img_data.getString("receive_ID");
                String real_receive_ID=String.valueOf(loginMapper.getUserID(receiveID));
                String nickname=loginMapper.getNickname(SendID);
                String filename=all_data_Handling.creat_chatXMLname(Integer.parseInt(real_sendID),Integer.parseInt(real_receive_ID));
                path=path_real_friend_mp3+filename+symbol;
                String mp3Name=all_data_Handling.base64_to_mp3(path,base64);   //传给处理生成MP3
                path=path_chatdataMP3+filename+symbol+mp3Name;     //需要修改一次地址
                if(loginMapper.Judgment(real_receive_ID)==500){
                    JSONObject jsonObject=new JSONObject();
                    String sid=ChatDataService.pam.get(real_receive_ID);
                    jsonObject.put("ReceiveID",real_sendID);jsonObject.put("SendID",real_receive_ID);//这个实时监听需要把ID反过来
                    jsonObject.put("Nick",nickname);jsonObject.put("Textdata",path);jsonObject.put("type","news");
                    jsonObject.put("datatype","mp3");jsonObject.put("time",all_data_Handling.get_time());
                    myHandler.PushBot_Msg(sid,jsonObject);
                }
                all_data_Handling.add_friendXML(path_real_friendXML,filename,real_sendID,path,nickname,mp3);   //给XML中添加mp3信息
                return mp3Name;
            }
            else {return null;}
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
