package com.example.Spring.Service;

import com.example.Spring.All_data_Handling;
import com.example.Spring.DAO.RegisterMapper;
import com.example.Spring.Entities.UserData;
import com.example.Spring.Thread.email_thread;
import net.sf.json.JSONObject;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class RegisterService{

//    private JavaMailSenderImpl javaMailSender=JavaMailSender();
//    private SimpleMailMessage mailMessage=new SimpleMailMessage();


    @Resource
    RegisterMapper registerMapper;

    All_data_Handling all_data_handling=new All_data_Handling();
    public static Map<String,Integer> email=new HashMap<>();
    private JavaMailSenderImpl javaMailSender;
    private SimpleMailMessage mailMessage;   //意义不明 但是留着吧
    private String[] key;
    private String regiset_1,regiset_2,subject;
    private String real_headimg,real_systemXML;
    private String regEx_name,regEx_phone,regEx_email1,regEx_email2;
    private final String regEx_email=regEx_email1+"@"+regEx_email2;

    /*
    发送邮件服务
     */
    public int SendEmail(String receiver){
        try {
            System.out.println(receiver);
            MimeMessage msg = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            int rand = get_rand();
            String content = regiset_1 + rand + regiset_2;
            helper.setTo(receiver);
            helper.setFrom("phphwo@gmail.com");//一定要写 不然报501错误
            helper.setText(content, true);
            helper.setSubject(subject);
            javaMailSender.send(msg);
            return rand;
        }
        catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }


    /*
    创建线程
     */
    public boolean email_Thread(String receiver){
        try {
            if (receiver == null) {
                return false;
            } else if (email.containsKey(receiver)) {
                return false;
            } else {
                int count = registerMapper.repeat_check_to_email(receiver);//查重
                if (count != 0) {
                    return false;
                }
                int Verification = SendEmail(receiver);
                if (Verification==0){return false;}
                email.put(receiver, Verification);
                ExecutorService t = newCachedThreadPool();
                t.submit(new email_thread(receiver, Verification));//创建新的线程等待用户注册
                return true;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    /*
    注册核心服务
     */
    public String Register_user(JSONObject jsonObject){
        String[] register_userdata = new String[7];
        for (int i=0;i<=6;i++){
            if(i==1){
                i++;
            }
            if (jsonObject.getString(key[i]).isEmpty()){
                return "有空项";
            }
            register_userdata[i]=jsonObject.getString(key[i]);
        }

        String result=register_check(register_userdata);//进行一次数据检测

        //String result="pass";
        System.out.println(result);
        if (!result.equals("pass")){
            return result;
        }
        return register_adduser(register_userdata);
    }


    /*
  添加注册数据
   */
    public String register_adduser(String[] data){
        UserData userData=new UserData();
        userData.setUserID(0);
        userData.setNickName(data[0]);
        userData.setPhone(data[1]);
        userData.setEmail(data[2]);
        userData.setPassword(data[4]);
        userData.setState(200);

        registerMapper.register_adduser_class(userData);//在sql添加数据时 修改实体类中的ID 返回给前台
        RegisterService.email.remove(data[2]);//删除Map中的邮箱
        System.out.println("添加成功:"+userData.getUserID());
        if (data[6].equals("NoAvatar")){  //添加完成后给用户创建用户专用文件夹
            //*****
        }else {
            String path=real_headimg+userData.getUserID()+".jpg";
            all_data_handling.head_base64toIMG(data[6],path);//载入头像
        }
            String path=real_systemXML+userData.getUserID()+"/"+userData.getUserID()+".xml";
            String path_mkdir=real_systemXML+userData.getUserID();
        all_data_handling.creat_systemXML(path_mkdir,path);//创建系统通知XML
        return String.valueOf(userData.getUserID());
    }


    /*
   数据二次检测
    */
    public String register_check(String[] data){
        System.out.println(Arrays.toString(data));
        int count;
        //昵称
        Pattern p = Pattern.compile(regEx_name);
        Matcher m = p.matcher(data[0]);
        if (m.find()){
            return "名称含有非法字符";
        }
        //手机号
        if (data[1]==null||data[1].equals("")){
            data[1]=null;
        }
        else {
            p = Pattern.compile(regEx_phone);
            m = p.matcher(data[1]);
            if (m.find()){
                return "请输入正确的手机号码";
            }
        }
        //邮箱
        p = Pattern.compile(regEx_email);
        m = p.matcher(data[2]);
        //密码
        if (m.find()){
            return "请输入正确的邮箱";
        }
        if(!data[4].equals(data[5])){
            return "两次密码不相同";
        }
        //验证码
        if (!email.containsKey(data[2])){
            return "该邮箱暂未发送验证码或已过期";
        }
        if(!(email.get(data[2]) ==Integer.parseInt(data[3]))){
            return "验证码错误";
        }
        if(data[1]==null){
            count=registerMapper.repeat_check_no_phone(data[2],data[0]);
        }else {
            count=registerMapper.repeat_check(data[1], data[2], data[0]);
        }
        if (count!=0){
            return "用户名or手机号";
        }
        return "pass";
    }


    /*
    线程创建
     */
    private ExecutorService newCachedThreadPool() {
        /*
         *  这里问题略大 如果被恶心容易造成OOM异常  不过反正也没什么人用 后期记得加线程限制
         */
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>());
    }

    public int get_rand(){
        return (int)((Math.random()*9+1)*100000);
    }

    public void setJavaMailSender(JavaMailSenderImpl javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void setMailMessage(SimpleMailMessage mailMessage) {
        this.mailMessage = mailMessage;
    }

    public void setRegiset_1(String regiset_1) {
        this.regiset_1 = regiset_1;
    }

    public void setRegiset_2(String regiset_2) {
        this.regiset_2 = regiset_2;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setKey(String[] key) {
        this.key = key;
    }

    public void setRegEx_name(String regEx_name) {
        this.regEx_name = regEx_name;
    }

    public void setRegEx_phone(String regEx_phone) {
        this.regEx_phone = regEx_phone;
    }

    public void setRegEx_email1(String regEx_email1) {
        this.regEx_email1 = regEx_email1;
    }

    public void setRegEx_email2(String regEx_email2) {
        this.regEx_email2 = regEx_email2;
    }

    public void setReal_headimg(String real_headimg) {
        this.real_headimg = real_headimg;
    }

    public void setReal_systemXML(String real_systemXML) {
        this.real_systemXML = real_systemXML;
    }
}
