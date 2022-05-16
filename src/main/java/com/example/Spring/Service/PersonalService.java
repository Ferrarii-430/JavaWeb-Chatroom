package com.example.Spring.Service;

import com.example.Spring.All_data_Handling;
import com.example.Spring.DAO.FriendMapper;
import com.example.Spring.DAO.LoginMapper;
import com.example.Spring.myHandler;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class PersonalService {
    Properties prop = All_data_Handling.readPropertiesFile();
    All_data_Handling all_data_handling=new All_data_Handling();
    private final String friend="friend";
    private final String system="system";

    private final String path_friend=(String)prop.get("path.friendchat");
    private final String path_IMG=(String)prop.get("path.imgSingle");
    private final String path_MP3=(String)prop.get("path.friend_mp3");
    private final String path_system=(String)prop.get("path.systemXML_real");
    private final String path_chatroomXML=(String)prop.get("path.chatroomXML");
    private final String path_headIMG=(String)prop.get("path.headimg");
    private final String path_headimg=(String)prop.get("path.headimg_real");

    @Resource
    LoginMapper loginMapper;

    @Resource
    FriendMapper friendMapper;

    public String get_myheadIMG(String id){
        return all_data_handling.getHeadIMGpath(String.valueOf(loginMapper.getUserID(id)),path_headIMG,path_headimg);
    }

    public JSONArray get_systemXML(String id)throws Exception{
        return all_data_handling.get_systemXML(path_system,String.valueOf(loginMapper.getUserID(id)));
    }

    public boolean user_add_friend(String id,String receiveID,String remarks){
        String nick=String.valueOf(loginMapper.getNickname(id));
        String receiveID_read=String.valueOf(loginMapper.getUserID(receiveID));
        String real_userid= String.valueOf(loginMapper.getUserID(id));
        List list=friendMapper.friend_list(real_userid);
        if (!list.isEmpty()) {
            for (Object o : list) {
                if (receiveID_read.equals(o)){
                    return false;
                }
            }
        }
        return all_data_handling.add_systemXML(path_system,nick,friend,receiveID_read,remarks,all_data_handling.get_time(),real_userid);
    }

    public boolean set_userXML_state(String  id,int number){
        String id_read=String.valueOf(loginMapper.getUserID(id));
        return all_data_handling.set_systemNoticeXML(path_system,id_read,number);
    }

    public boolean del_systemNoticeXML(String  id,int number){
        String id_read=String.valueOf(loginMapper.getUserID(id));
        return all_data_handling.del_systemNoticeXML(path_system,id_read,number);
    }

    public boolean creat_friendXML(String id,String receiveID,String type,String boole){//预留一个boole用作判断  后面能用
        String filename;
        int id_read=loginMapper.getUserID(id);
        int receiveID_read=loginMapper.getUserID(receiveID);
        String nickname=loginMapper.getNickname(String.valueOf(receiveID_read));
        filename=all_data_handling.creat_chatXMLname(id_read,receiveID_read);
        friendMapper.add_friendRelationship(String.valueOf(id_read),String.valueOf(receiveID_read));
        return all_data_handling.creat_friendXml(path_MP3,path_IMG,path_friend,filename, String.valueOf(receiveID_read),nickname,type);
    }

    public JSONArray get_newfriend_data(String id,String receiveid)throws Exception{
        System.out.println("get_newfriend_data"+id+receiveid);
        String filename;
        int id_read=loginMapper.getUserID(id);
        int receiveID_read=loginMapper.getUserID(receiveid);
        filename=all_data_handling.creat_chatXMLname(id_read,receiveID_read);
        return all_data_handling.get_friendXML(path_friend,filename);
    }

    public JSONArray get_allfriend_chatdata(JSONArray id_list,String id)throws Exception{
        int id_read=loginMapper.getUserID(id);
        return all_data_handling.get_allfriend_chatdata(path_friend,id_list,id_read);
    }

    public JSONArray get_chatroom_chatdata()throws Exception{
        return all_data_handling.get_chatroomXML(path_chatroomXML);
    }


    /*
    获取个人的昵称和ID
     */
    public JSONObject getUser_ID(String ID){
        int ID_real=loginMapper.getUserID(ID);
        String nick=loginMapper.getNickname(String.valueOf(ID_real));
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("id",ID_real);
        jsonObject.put("nick",nick);
        return jsonObject;
    }


    /*
    把收到的聊天数据传入数据处理 进行添加
     */
    public boolean chat_txt_add(JSONObject jsonObject){
        boolean add;
        String SendID=jsonObject.getString("SendID");
        String ReceiptID=jsonObject.getString("ReceiptID");
        String Txtdata=jsonObject.getString("Txtdate");
        String Chat_type=jsonObject.getString("Chat_type");
        String Data_type=jsonObject.getString("Data_type");
        if (SendID!=null&&ReceiptID!=null&&Txtdata!=null&&Chat_type!=null){
            int id_read=loginMapper.getUserID(SendID);
            String nickname=loginMapper.getNickname(SendID);
            switch(Chat_type){     //后面发现 这里可以改if
                case "txtSingle":add=txtSingle(id_read,ReceiptID,SendID,Txtdata,nickname,Data_type);break;
                case "txtAll":add=txtAll(path_chatroomXML,SendID,Txtdata,nickname,Data_type);break;
                default: System.out.println("添加XML错误");add=false;
            }
            return add;
        }
        else {
            return false;
        }
    }

    //准备用来写为全员添加系统通知用  因为是用XML 所以会有惊人的资源占用  慎用!
    public boolean alluser_add_systemNoticeXML(){//预留接口
        return true;
    }


    //私聊数据处理
    public boolean txtSingle(int id_real,String ReceiptID,String SendID,String Txtdata,String nickname,String type){
        int receiveID_read=loginMapper.getUserID(ReceiptID);
        if(loginMapper.Judgment(String.valueOf(receiveID_read))==500){
            JSONObject jsonObject=new JSONObject();
            String sid=ChatDataService.pam.get(String.valueOf(receiveID_read));
            jsonObject.put("ReceiveID",id_real);jsonObject.put("SendID",receiveID_read);//这个实时监听需要把ID反过来
            jsonObject.put("Nick",nickname);jsonObject.put("Textdata",Txtdata);jsonObject.put("type","news");
            jsonObject.put("datatype","txt");jsonObject.put("time",all_data_handling.get_time());
            myHandler.PushBot_Msg(sid,jsonObject);
        }
        String filename=all_data_handling.creat_chatXMLname(id_real,receiveID_read);
        return all_data_handling.add_friendXML(path_friend,filename,SendID,Txtdata,nickname,type);
    }


    //多人聊天数据处理
    public boolean txtAll(String path,String SendID,String Txtdata,String nickname,String type){
        String headimg="no";
        if(all_data_handling.exist_chatroomXML(path_chatroomXML)){
            return false;
        }
        if(all_data_handling.exist_file(SendID,path_headimg)){
            headimg="yes";
        }
        if (ChatDataService.pam.size()!=0){
            JSONObject jsonObject=new JSONObject();
            Txtdata=Sensitive_words(Txtdata);
            jsonObject.put("type","news_room");
            jsonObject.put("userid",SendID);jsonObject.put("nickname",nickname);jsonObject.put("chat",Txtdata);
            jsonObject.put("datatype",type);jsonObject.put("headimg",headimg);jsonObject.put("date",all_data_handling.get_time());
            myHandler.Push_txtAll(jsonObject);
        }
        return all_data_handling.add_chatroomXML(path,SendID,Txtdata,nickname,headimg,type);
    }

    public String Sensitive_words(String text){
        String test=null;
        if (text.indexOf("逼")!=-1){
            test=text.replaceAll("逼","*");
        }
        if (text.indexOf("习")!=-1){
            test=test.replaceAll("习","*");
        }
        if (text.indexOf("平")!=-1){
            test=test.replaceAll("平","*");
        }
        if (text.indexOf("近")!=-1){
            test=test.replaceAll("近","*");
        }
        if (text.indexOf("草")!=-1){
            test=test.replaceAll("草","*");
        }
        if (text.indexOf("靠")!=-1){
            test=test.replaceAll("靠","*");
        }
        if (test==null){
            return text;
        }
        return test;
    }
}
