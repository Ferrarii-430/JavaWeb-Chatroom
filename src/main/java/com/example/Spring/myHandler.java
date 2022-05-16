package com.example.Spring;

import com.example.Spring.Action.CtxUtil;
import com.example.Spring.DAO.LoginMapper;
import com.example.Spring.Service.ChatDataService;
import com.example.Spring.Service.FriendService;
import com.example.Spring.Service.LoginService;
import net.sf.json.JSONObject;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


import javax.annotation.Resource;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.concurrent.CopyOnWriteArraySet;

public class myHandler extends TextWebSocketHandler {

    /**
     * 这里也是一个另类的controller层
     * */

    @Resource
    LoginMapper loginMapper;

    private static int count;
    private static final CopyOnWriteArraySet<WebSocketSession> set = new CopyOnWriteArraySet<>();
    SimpleDateFormat df=new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
    private WebSocketSession session;
    ChatDataService chatDataService;
    LoginService loginService;
    FriendService friendService;
    String current;



    /*
    实例化各种 service
    */
    public void init(){ chatDataService = CtxUtil.getBean(ChatDataService.class); }
    public void init2(){ loginService = CtxUtil.getBean(LoginService.class); }
    public void init3(){ friendService = CtxUtil.getBean(FriendService.class); }


    /*
    在这里解析用户发过来的数据
    */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message){
        JSONObject jsonObject= JSONObject.fromObject(message.getPayload());
        String id =jsonObject.getString("ReceiptID");
        String base64 =jsonObject.getString("base64");
         //会重复的使用Map查询sessionID 人数多添加多线程缓存机制可能会好一点
         //不过服务器也撑不起这么多人用 偷懒了
        PushBot_Msg(chatDataService.getPam(id),base64); //转发base64数据
    }


    /*
    获取当前系统时间
    */
    public String getTime(){
        current = df.format(System.currentTimeMillis());
        return current;
    }

    /*
    添加当前在线人数
    */
    @Override
    public void afterConnectionEstablished(WebSocketSession session)throws Exception {
       String URL= String.valueOf(session.getUri());
       System.out.println(URL);
       String ID=URL.split("=")[1];
       ID=UrlDecode(ID);  //需要进行URL解码   否则有些浏览器中文报错
        if(chatDataService ==null) {
            init();
        }
        if(loginService==null){
            init2();
        }
        int UserID=getUserID(ID);
        this.session = session;
        try{
            set.add(this.session);
            chatDataService.addMap(session.getId(), String.valueOf(UserID));  //添加进Map
            chatDataService.addPam(String.valueOf(UserID),session.getId());  //添加进Pam
            loginMapper.setUserJudgment_500(ID);//修改用户状态500
        }catch(Exception e) {
            e.printStackTrace();
        }
        addOnlineCount(UserID);
        System.out.println("目前连接人数：" + getOnlineCount());
    }


    /*
    减少当前在线人数
    */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        if(chatDataService ==null) {
            init();
        }
        if(loginService==null){
            init2();
        }
        this.session = session;
        set.remove(this.session);
        String id=chatDataService.getMap(session.getId());
        loginMapper.setUserJudgment_200(id);//修改用户状态200
        chatDataService.delPam(id);
        chatDataService.delMap(session.getId());
        subOnlineCount(id);
        System.out.println("目前连接人数：" + getOnlineCount());
    }


    /*
     给指定连接推消息  String版
     */
    public static void PushBot_Msg(String session_id, String message){
        try {
            for (WebSocketSession ssion : set) {
                if (session_id.equals(ssion.getId())) {
                    ssion.sendMessage(new TextMessage(message));
                }
            }
        }
        catch(IOException e) {
        e.printStackTrace();
      }
    }


    /*
     给指定连接推消息  byte版
     */
    public static void PushBot_Msg(String session_id, byte[] message) {
        try {
            for (WebSocketSession ssion : set) {
                if (session_id.equals(ssion.getId())) {
                    ssion.sendMessage(new TextMessage(message));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
     给指定连接推消息  byte版
     */
    public static void PushBot_Msg(String session_id,JSONObject jsonObject) {
        try {
            for (WebSocketSession ssion : set) {
                if (session_id.equals(ssion.getId())) {
                    ssion.sendMessage(new TextMessage(jsonObject.toString()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /*
     给全部连接推送txt
     */
    public void Push_txtAll(String message) {
        //for要在 try里面
        try {
        for(WebSocketSession ssion : set) {
                ssion.sendMessage(new TextMessage(message));
        }
        }catch(IOException e) {
            e.printStackTrace();
        }
    }


    /*
     给全部连接推送txt
     */
    public static void Push_txtAll(JSONObject jsonObject) {
        //for要在 try里面
        try {
            for(WebSocketSession ssion : set) {
                ssion.sendMessage(new TextMessage(jsonObject.toString()));
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
    }


    /*
    给全部连接推送img
    */
    public void Push_imgAll(String message){
        try {
        for(WebSocketSession ssion : set) {
                ssion.sendMessage(new TextMessage(message));
        }
        }catch(IOException e) {
            e.printStackTrace();
        }
    }


    /*
    如果在线则实时更新用户数据 顺便存入XML并判定为未读
    */
    public static boolean websocket_update_userdata(int userID){
        return true;
    }



     /*
     查找真实ID
     */
    public int getUserID(String ID){
        return loginService.getUserID(ID);
    }


    /*
    计数器
    */
    public static int getOnlineCount() {
        return count;
    }

    public void addOnlineCount(int id) {
        count++;
        if(friendService==null){
            init3();
        }
        if (getOnlineCount()>0){//人数必须大于0才执行
            Push_txtAll(friendService.getOnlineUser(id));
        }
        else {
            setOnlineCount(0);
        }
    }
    public void subOnlineCount(String id) {
        count--;
        if(getOnlineCount()>0){//人数不为0时才执行
            Push_txtAll(friendService.delOnlineUser(id));
        }
    }

    public void setOnlineCount(int p){count=p;};

    public String UrlDecode(String id)throws Exception{
        String utf;
        utf = id.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
        utf = URLDecoder.decode(utf, "UTF-8");
        return utf;
    }

}

