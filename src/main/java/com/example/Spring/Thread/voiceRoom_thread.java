package com.example.Spring.Thread;

import com.example.Spring.Service.ChatDataService;
import com.example.Spring.myHandler;
import net.sf.json.JSONObject;


public class voiceRoom_thread implements Runnable{
    String send_id,receive_id;
    public voiceRoom_thread(String send_id, String receive_id) {
        /**
         * 这样有个很大的问题 就是会多吃一点服务器资源 后面学到再优化
         */
        this.send_id=send_id;
        this.receive_id=receive_id;
    }

    @Override
    public void run() {
        JSONObject jsonObject=new JSONObject();
        boolean del=true;
        try {
            jsonObject.put("type","voice");jsonObject.put("id",send_id);
            myHandler.PushBot_Msg(ChatDataService.pam.get(receive_id),jsonObject);
            for (int i=1;i<=35;i++){
                Thread.sleep(1000);
                if(!ChatDataService.user_voice_judgment.containsKey(send_id)){
                    del=false;
                    //myHandler.PushBot_Msg(ChatDataService.pam.get(send_id),"voice_start|"+receive_id+"");
                    jsonObject.put("type","voice_startMP3");jsonObject.put("id",receive_id);
                    Thread.sleep(2000);
                    myHandler.PushBot_Msg(ChatDataService.pam.get(send_id),jsonObject);
                    jsonObject.put("id",send_id);
                    myHandler.PushBot_Msg(ChatDataService.pam.get(receive_id),jsonObject);
                    System.out.println("对方已进入 开始通话");
                    jsonObject.clear();
                    break;
                }
            }
            if (del) {
                ChatDataService.user_user_voice.remove(receive_id);//40S 后用户未回应 则删除房间
                ChatDataService.user_voice_judgment.remove(send_id);
                System.out.println("过期 房间已删除");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
