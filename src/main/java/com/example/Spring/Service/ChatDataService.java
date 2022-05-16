package com.example.Spring.Service;

import com.example.Spring.DAO.ChatDateMapper;
import com.example.Spring.DAO.LoginMapper;
import com.example.Spring.Entities.ChatData;
import com.example.Spring.myHandler;
import com.example.Spring.Thread.voiceRoom_thread;
import com.example.Spring.Thread.voideRoom_thread;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Service
public class ChatDataService {

    @Resource
    ChatDateMapper chatDateMapper;

    @Resource
    LoginMapper loginMapper;

    public static Map<String, String> map=new HashMap<String,String>();// key:S_id  val:id
    public static Map<String,String> pam=new HashMap<String,String>();//  key:id    key:S_id
    public static Map<String,String> user_user_voice=new HashMap<String,String>();// key:接收ID   val:发送ID
    public static Map<String,String> user_user_voide=new HashMap<String,String>();// key:接收ID   val:发送ID
    public static Map<String,String> user_voice_judgment=new HashMap<String,String>();
    public static Map<String,String> user_voide_judgment=new HashMap<String,String>();
    public static ArrayList chat_data=new ArrayList();



    /*
    判断对方是否在线 在线则创建voice房间 等待对方进入 不在线就返回告知用户对方不在线  房间ID暂时不做
    */
    public String creatVoice_room(String send_ID,String receive_ID){
        String real_sendID=String.valueOf(loginMapper.getUserID(send_ID));
        String real_receive_ID=String.valueOf(loginMapper.getUserID(receive_ID));
        JSONObject jsonObject=new JSONObject();
        if (user_user_voice.size()>=6){
         return "使用人数已达服务器负荷上限";
        }
        if (send_ID==null||receive_ID==null){
            System.out.println("传输ID为null");
            return "传输ID为null";
        }
        else if(loginMapper.Judgment(real_receive_ID) == 500){
            if (user_user_voice.containsKey(real_sendID)){
                if (user_user_voice.get(real_sendID).equals(real_receive_ID)){//此为加入正在等待的房间
                    if (user_voice_judgment.get(real_receive_ID).equals(real_sendID)){
                        user_voice_judgment.remove(real_receive_ID);//响应被删除则证明已经加入房间
                    }

                    /*
                      双方准备就绪后  通知用户打开websocket开启通话
                     */
                    jsonObject.put("type","voice_start");jsonObject.put("id",real_receive_ID);
                    myHandler.PushBot_Msg(ChatDataService.pam.get(real_sendID),jsonObject);
                    jsonObject.clear();
                    //myHandler.PushBot_Msg(ChatDataService.pam.get(real_sendID),"voice_startMP3|"+real_receive_ID+"");
                    return "用户"+real_sendID+"已经进入房间  开始voice通话";

                }else {
                    return creatVoice_wait_room(real_sendID, real_receive_ID);// 创建房间并等待加入
                }
            }else {//此为房间创建 等待加入
                return creatVoice_wait_room(real_sendID, real_receive_ID);// 创建房间并等待加入
            }
      }
      else{
            System.out.println("未知错误或对方未在线");
            return "对方未在线";
      }
    }


    /**
     *
     * @param send_ID  发送者ID
     * @param receive_ID  接受者ID
     * @return  使用状态
     *
     * 上下方有代码冗余 后期注意优化  功能先上
     */


    /*
   判断对方是否在线 在线则创建voide房间 等待对方进入 不在线就返回告知用户对方不在线
   */
    public String creatVoide_room(String send_ID,String receive_ID){
        String real_sendID=String.valueOf(loginMapper.getUserID(send_ID));
        String real_receive_ID=String.valueOf(loginMapper.getUserID(receive_ID));
        if (send_ID==null||receive_ID==null){
            System.out.println("传输ID为null");
            return "传输ID为null";
        }
        else if(loginMapper.Judgment(real_receive_ID) == 500){
            if (user_user_voide.containsKey(real_sendID)){
                if (user_user_voide.get(real_sendID).equals(real_receive_ID)){//此为加入正在等待的房间
                    if (user_voide_judgment.get(real_receive_ID).equals(real_sendID)){
                        user_voide_judgment.remove(real_receive_ID);//响应被删除则证明已经加入房间
                    }
                    /*
                      双方准备就绪后  通知用户打开websocket开启通话
                     */
                    return "用户"+real_sendID+"已经进入房间  开始voide视频通话";
                }else {
                    return creatVoide_wait_room(real_sendID, real_receive_ID);// 创建房间并等待加入
                }
            }else {//此为房间创建 等待加入
                return creatVoide_wait_room(real_sendID, real_receive_ID);// 创建房间并等待加入
            }
        }
        else{
            System.out.println("未知错误或对方未在线");
            return "对方未在线";
        }
    }


    /*
    创建voice房间并等待加入
    */
    private String creatVoice_wait_room(String send_ID, String receive_ID) {
        user_user_voice.put(receive_ID,send_ID);//此为房间创建 等待加入
        System.out.println("房间已创建:"+user_user_voice);
        ExecutorService t=newCachedThreadPool();
        t.submit(new voiceRoom_thread(send_ID,receive_ID));//创建新的线程等待用户回应
        user_voice_judgment.put(send_ID,receive_ID);//加入等待判断
        return "等待:"+receive_ID+" 进入";
    }


    /*
   创建voide房间并等待加入
   */
    private String creatVoide_wait_room(String send_ID, String receive_ID) {
        user_user_voide.put(receive_ID,send_ID);//此为房间创建 等待加入
        System.out.println("房间已创建:"+user_user_voide);
        ExecutorService t=newCachedThreadPool();
        t.submit(new voideRoom_thread(send_ID,receive_ID));//创建新的线程等待用户回应
        user_voide_judgment.put(send_ID,receive_ID);//加入等待判断
        return "等待:"+receive_ID+" 进入";
    }


    /*
    voice删除房间并关闭off按钮
    */
    public boolean del_Voice_room(String send_ID,String receive_ID){
        JSONObject jsonObject=new JSONObject();
        String real_sendID=String.valueOf(loginMapper.getUserID(send_ID));
        String real_receive_ID=String.valueOf(loginMapper.getUserID(receive_ID));
        jsonObject.put("type","voice_close");
        if (user_user_voice.containsKey(real_sendID)){
            user_user_voice.remove(real_receive_ID);
            myHandler.PushBot_Msg(ChatDataService.pam.get(real_receive_ID),jsonObject);//接收者关闭Voice
            System.out.println("用户退出 房间已删除");
            return true;
        }
        else if(user_user_voice.containsKey(real_receive_ID)){
            user_user_voice.remove(real_receive_ID);
            myHandler.PushBot_Msg(ChatDataService.pam.get(real_receive_ID),jsonObject);//接收者关闭Voice
            System.out.println("用户退出 房间已删除");
            return true;
        }else {
            return false;
        }
    }


    /*
   voide删除房间
   */
    public boolean del_Voide_room(String send_ID,String receive_ID){
        JSONObject jsonObject=new JSONObject();
        String real_sendID=String.valueOf(loginMapper.getUserID(send_ID));
        String real_receive_ID=String.valueOf(loginMapper.getUserID(receive_ID));
        jsonObject.put("type","voide_close");
        if (user_user_voide.containsKey(real_sendID)){
            user_user_voide.remove(real_receive_ID);
            myHandler.PushBot_Msg(ChatDataService.pam.get(real_receive_ID),jsonObject);//接收者关闭Voide
            System.out.println("用户退出 房间已删除");
            return true;
        }
        else if(user_user_voide.containsKey(real_receive_ID)){
            user_user_voide.remove(real_receive_ID);
            myHandler.PushBot_Msg(ChatDataService.pam.get(real_receive_ID),jsonObject);
            System.out.println("用户退出 房间已删除");
            return true;
        }else {
            return false;
        }
    }


    /*
    操作map
    */
    public String getMap(String ws) {
        return map.get(ws);
    }
    public Map<String, String> getMap() {
        return map;
    }
    public void addMap(String ws, String id){ map.put(ws,id); }
    public void delMap(String ws){
        map.remove(ws);
    }

    /*
    操作pam
    */
    public String getPam(String ws) {
        return pam.get(ws);
    }
    public Map<String, String> getPam() {
        return pam;
    }
    public void addPam(String id, String ws){
        pam.put(id,ws);
    }
    public void delPam(String id){
        pam.remove(id);
    }


    /*
    操作user_user_voice
    */
    public String getuser_user_voice(String userid){
        return user_user_voice.get(userid);
    }
    public static Map<String, String> getUser_user_voice() {
        return user_user_voice;
    }


    /*
     获取getUser_user_voice长度
     */
    public int getUser_user_voiceSize(){
        return user_user_voice.size();
    }


    /*
     获取map长度
     */
    public int getmapSize(){
        return map.size();
    }


    /*
    获取map所有key值
    */
    public String get_Mapallkey(){
        Set<String> set=pam.keySet();
        return set.toString();
    }


    /*
      聊天数据添加进入数据库
     */
    public int add(ChatData chatData){
        return chatDateMapper.add(chatData);
    }


    /*
      获取实时用户状态
     */
    public boolean getStateBool(String ID){
         if(loginMapper.Judgment(ID)==405){
             //这里用户不在线就存入数据库 到时候再做吧 先测试其他端口
             System.out.println(loginMapper.Judgment(ID));
             return true;
         }
         else {
             System.out.println(loginMapper.Judgment(getList_1())+"在这里把数据存入数据库");
             return false;
         }
    }


    /*
     封装聊天数据
    */
    public void Chat_data(WebSocketSession session, String sm, String time){
        chat_data.add(getMap(session.getId()));      //此为：发送者ID    0
        chat_data.add(sm.split("[|]")[1]);   //此为：接收者ID    1
        chat_data.add(sm.split("[|]")[2]);   //此为：txt数据     2
        chat_data.add(time);                 //此为：时间数据     3
        System.out.println(chat_data);
    }


    /*
      清空聊天数据数组
     */
    public void clean_Array(){ chat_data.clear(); }


    /*
    获取Chatdata数组指定数值
   */
    public String getList_0(){
        return (String)chat_data.get(0);
    }
    public String getList_1(){
        return (String)chat_data.get(1);
    }
    public String getList_2(){
        return (String)chat_data.get(2);
    }
    public String getList_3(){
        return (String)chat_data.get(3);
    }

    private ExecutorService newCachedThreadPool() {

        /*
         *  这里问题略大 如果被恶心容易造成OOM异常  不过反正也没什么人用 后期记得加线程限制 及使用人数限制
         */
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>());
    }
}

