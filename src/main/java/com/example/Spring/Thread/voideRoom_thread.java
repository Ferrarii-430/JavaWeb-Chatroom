package com.example.Spring.Thread;

import com.example.Spring.Service.ChatDataService;
import com.example.Spring.myHandler;
import net.sf.json.JSONObject;

import java.util.Random;

public class voideRoom_thread implements Runnable{
     String send_id,receive_id;
     JSONObject jsonObject=new JSONObject();

    public voideRoom_thread(String send_id, String receive_id) {
        this.send_id=send_id;
        this.receive_id=receive_id;
    }

    @Override
    public void run() {
        boolean del=true;
        try {
            jsonObject.put("type","voide");
            jsonObject.put("id",send_id);
            myHandler.PushBot_Msg(ChatDataService.pam.get(receive_id),jsonObject);
            for (int i=1;i<=35;i++){
                Thread.sleep(1000);
                if(!ChatDataService.user_voide_judgment.containsKey(send_id)){
                    del=false;
                    //myHandler.PushBot_Msg(ChatDataService.pam.get(send_id),"voice_start|"+receive_id+"");
                    Thread.sleep(1000);
                    String roomName="observable-"+getCharAndNumr(6);
                    jsonObject.put("id",receive_id);
                    jsonObject.put("roomName",roomName);
                    jsonObject.put("type","voide_start");
                    myHandler.PushBot_Msg(ChatDataService.pam.get(send_id),jsonObject);
                    Thread.sleep(2000);
                    jsonObject.put("id",send_id);
                    myHandler.PushBot_Msg(ChatDataService.pam.get(receive_id),jsonObject);
                    System.out.println("对方已进入 开始通话");
                    jsonObject.clear();
                    break;
                }
            }
            if (del) {
                ChatDataService.user_user_voide.remove(receive_id);//40S 后用户未回应 则删除房间
                ChatDataService.user_voide_judgment.remove(send_id);
                System.out.println("过期 房间已删除");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String getCharAndNumr(int length) {
        String val = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            // 输出字母还是数字
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            // 字符串
            if ("char".equalsIgnoreCase(charOrNum)) {
                // 取得大写字母还是小写字母
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char) (choice + random.nextInt(26));
            } else if ("num".equalsIgnoreCase(charOrNum)) { // 数字
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }
}
