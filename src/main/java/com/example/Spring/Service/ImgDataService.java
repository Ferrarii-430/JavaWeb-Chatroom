package com.example.Spring.Service;

import com.example.Spring.All_data_Handling;
import com.example.Spring.DAO.LoginMapper;
import com.example.Spring.myHandler;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Properties;

@Service
public class ImgDataService {

    @Resource
    LoginMapper loginMapper;

    static boolean exists=true;
    final String symbol="/";
    Properties prop = All_data_Handling.readPropertiesFile();
    All_data_Handling all_data_Handling=new All_data_Handling();
    private final String path_img_all=(String)prop.get("path.real_imgAll");
    private final String path_img_single=(String)prop.get("path.imgSingle");
    private final String path_real_friendXML=(String)prop.get("path.friendchat");
    private final String path_chatdataIMG=(String)prop.get("path.chatdataIMG");
    private final String path_headimg=(String)prop.get("path.headimg_real");

    private final String path_chatroomXML=(String)prop.get("path.chatroomXML");
    private final String path_chatroomIMG=(String)prop.get("path.chatroomIMG");
    private final String img="img";

    public String base64_to_img(JSONObject img_data){
        String type=img_data.getString("type");
        String base64=img_data.getString("base64");
        String SendID=img_data.getString("SendID");
        String real_sendID=String.valueOf(loginMapper.getUserID(SendID));
        String path;
        try {
        if(type.equals("imgAll")) {
            String headimg="no";
            if (all_data_Handling.exist_chatroomXML(path_chatroomXML)){
                return null;
            }
            path=path_img_all;
            String imgName=all_data_Handling.base64_to_img(path,base64); //传给处理生成图片
            path=path_chatroomIMG+imgName;
            String nickname=loginMapper.getNickname(SendID);
            if(all_data_Handling.exist_file(real_sendID,path_headimg)){
                headimg="yes";
            }
            if (ChatDataService.pam.size()!=0){
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("type","news_room");
                jsonObject.put("userid",SendID);jsonObject.put("nickname",nickname);jsonObject.put("chat",path);
                jsonObject.put("datatype",type);jsonObject.put("headimg",headimg);jsonObject.put("date",all_data_Handling.get_time());
                myHandler.Push_txtAll(jsonObject);
            }
            all_data_Handling.add_chatroomXML(path_chatroomXML,real_sendID,path,nickname,headimg,img);   //给XML中添加图片信息
            return imgName;
        }
        else if(type.equals("imgSingle")){
            String receiveID=img_data.getString("receive_ID");
            String real_receive_ID=String.valueOf(loginMapper.getUserID(receiveID));
            String nickname=loginMapper.getNickname(SendID);
            String filename=all_data_Handling.creat_chatXMLname(Integer.parseInt(real_sendID),Integer.parseInt(real_receive_ID));
            path=path_img_single+filename+symbol;
            String imgName=all_data_Handling.base64_to_img(path,base64);      //传给处理生成图片
            path=path_chatdataIMG+filename+symbol+imgName;     //需要修改一次地址
            if(loginMapper.Judgment(real_receive_ID)==500){
                JSONObject jsonObject=new JSONObject();
                String sid=ChatDataService.pam.get(real_receive_ID);
                jsonObject.put("ReceiveID",real_sendID);jsonObject.put("SendID",real_receive_ID);//这个实时监听需要把ID反过来
                jsonObject.put("Nick",nickname);jsonObject.put("Textdata",path);jsonObject.put("type","news");
                jsonObject.put("datatype","img");jsonObject.put("time",all_data_Handling.get_time());
                myHandler.PushBot_Msg(sid,jsonObject);
            }
            all_data_Handling.add_friendXML(path_real_friendXML,filename,real_sendID,path,nickname,img);   //给XML中添加图片信息
            return imgName;
        }
        else {return null;}
         } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String img_to_base64(String imgPath){
        return all_data_Handling.convertFileToBase64(imgPath);
    }

    public boolean exist_headimg(String id,String path){
        return all_data_Handling.exist_file(id,path);
    }


//    public String getData() {
//        return data;
//    }
}
