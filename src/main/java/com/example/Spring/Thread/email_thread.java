package com.example.Spring.Thread;

import com.example.Spring.Service.RegisterService;

public class email_thread implements Runnable{
    String email;
    int Verification;
    public email_thread(String email, int Verification) {
        this.email=email;
        this.Verification=Verification;
    }


    @Override
    public void run() {
        boolean del=true;
        //线程总共检查10次
        System.out.println("线程启动");
        try {
            for (int i = 1; i <= 185; i++) {
                Thread.sleep(1000);//休眠185S  3min
                if (!RegisterService.email.containsKey(email)){//不存在则证明已经被使用或者到期
                    System.out.println("用户注册成功");
                    del=false;
                    break;
                }
            }
            if(del){
                RegisterService.email.remove(email);
                System.out.println("过期 验证码已删除");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
