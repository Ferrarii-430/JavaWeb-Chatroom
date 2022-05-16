package com.example.Spring;

import com.example.Spring.Entities.Login;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Controller;
import org.dom4j.io.OutputFormat;
import java.io.FileOutputStream;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import org.dom4j.io.XMLWriter;


@Controller
public class All_data_Handling {
    static int count=1;
    Date date = new Date();
    static Base64.Decoder decoder = Base64.getDecoder();
    static Base64.Encoder encoder = Base64.getEncoder();
    final String strDateFormat = "MM-dd HH:mm a";
    final String system_strDateFormat="yyyy-dd-MM HH:mm:ss";
    final String suffix_xml=".xml";
    final String suffix_jpg=".jpg";
    final String suffix_mp3=".mp3";
    final String suffix_gif=".gif";
    static String properties_windows="E:\\Spring\\src\\main\\resources\\filepath.properties";
    static String properties_linux="/usr/local/tomcat/webapps/ROOT/WEB-INF/classes/linuxPath.properties";
    final String symbol="/";
    static boolean exists=false;

    /**
     注解@PostConstruct，在完成构造函数实例化后就调用该方法        用于对静态类属性的注解
     **/


    /**
     *base64转图片
     * */
    public String base64_to_img(String path,String data){
        String jpg_name=getCharAndNumr(20);
        String Path_file=path+jpg_name+suffix_jpg;
        String Path_file2=path+jpg_name+suffix_gif;
        int type=data.indexOf("/");
        String images_type=data.substring(type+1,type+4);
        System.out.println("图片类型为："+images_type);
        if(GenerateImage(data,Path_file,Path_file2))
        {
            System.out.println("图片生成完成:"+jpg_name);
            if (images_type.equals("gif")){
                return jpg_name+suffix_gif;
            }
            return jpg_name+suffix_jpg;
        }
        else {
            System.out.println("图片生成失败");
            return null;
        }
    }


    /**
     *base64转MP3
     * */
    public String base64_to_mp3(String path,String data){
        String mp3_name=getCharAndNumr(20);
        String Path_file=path+mp3_name+suffix_mp3;
        if(GenerateMP3(data,Path_file))
        {
            System.out.println("MP3生成完成:"+mp3_name);
            return mp3_name+suffix_mp3;
        }
        else {
            System.out.println("MP3生成失败");
            return null;
        }
    }


    /**
     * 进行头像 base64转图片
     * */
    public void head_base64toIMG(String data,String path){
        //if(GenerateImage(data,"E:\\headIMG\\"+ id +".jpg"))
        if(GenerateImage(data,path,path))
        {
            System.out.println("头像生成完成:"+path);
        }
        else {
            System.out.println("头像生成失败");
        }
    }


    public String getHeadIMGpath(String id,String path,String path_real){
        if (exist_file(id,path_real)){
            return path + id + suffix_jpg;
        }
        else {
            return "NoAvatar";
        }
    }


    /**
     * 进行头像 图片转base64
     * ！！！已被弃用！！！！
     * */
    public String head_IMGtobase64(int id){
        String imgPath="E:\\headIMG\\"+ id +".jpg";
        File file = new File(imgPath);
        if (file.exists()) {
            return convertFileToBase64(imgPath);
        }
        else
        {
            return "NoAvatar";
        }
    }


    public boolean GenerateMP3(String imgStr,String imgFilePath){
        try {
            if (imgStr == null) {
                // 图像数据为空
                System.out.println("MP3数据为空");
                return false;
            }
            try {
                // Base64解码
                byte[] result=decoder.decode(imgStr);
                // 生成jpeg图片
                OutputStream out= new FileOutputStream(imgFilePath);
                out.write(result);
                out.flush();
                out.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 进行base64转图片
     * */
    public boolean GenerateImage(String imgStr,String imgFilePath_JPG,String imgFilePath_GIF) {
        String images_type;
        String images_data;
        // 对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) {
            // 图像数据为空
            System.out.println("图像数据为空");
            return false;
        }
        try {
            //对Base64进行整理否则容易报错
            if (imgStr.contains("data:")) {
                int type=imgStr.indexOf("/");
                images_type=imgStr.substring(type+1,type+4);
                System.out.println("图像类型为"+images_type);
                int start = imgStr.indexOf(",");
                imgStr = imgStr.substring(start + 1);
            }
            else {
                return false;
            }
            imgStr = imgStr.replaceAll("\r|\n", "");
            imgStr = imgStr.trim();
            // Base64解码
            if (images_type.equals("gif")){
                images_data=imgFilePath_GIF;
            }
            else {
                images_data=imgFilePath_JPG;
            }
            byte[] result=decoder.decode(imgStr);
            // 生成jpeg图片

            OutputStream out= new FileOutputStream(images_data);
            out.write(result);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 进行图片转base64
     * */
    public  String convertFileToBase64(String imgPath) {
        // 对字节数组进行Base64编码，得到Base64编码的字符串
        byte[] data = null;
        // 读取图片字节数组
        try {
            InputStream in = new FileInputStream(imgPath);
            System.out.println("文件大小（字节）="+in.available());
            data = new byte[in.available()];
            in.read(data);
            in.close();
            return new String(encoder.encode(data), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 添加好友后创建friendXML保存聊天数据       前面数据应该不多暂时不用理 后期再做到期删除
     * */
    public boolean creat_friendXml(String pathMP3,String pathIMG,String pathXML,String filename,String receiveID_read,String nickname,String type){
        try {
            File file;
            // 1、创建document对象
            Document document = DocumentHelper.createDocument();
            // 2、创建根节点friend_chat
            Element friend_chat = document.addElement("friend_chat");
            // 3、向friend_chat节点添加version属性
            // 4、生成子节点及子节点内容
            Element chatdata = friend_chat.addElement("chatdata").addAttribute("id","chat");
            Element userid = chatdata.addElement("userid");
            Element nick = chatdata.addElement("nickname");
            Element chat = chatdata.addElement("chat");
            Element datatype = chatdata.addElement("type");
            Element date = chatdata.addElement("date");
            userid.setText(receiveID_read);
            nick.setText(nickname);
            chat.setText("我们已经是好友了 来打声招呼吧.");
            datatype.setText(type);
            date.setText(get_time());
            saveDocument(document, new File(pathXML+filename+suffix_xml));//创建好友XML文件
            file=new File(pathMP3+filename);  //创建好友图片文件夹
            file.mkdirs();
            file=new File(pathIMG+filename);  //创建好友音频文件夹
            file.mkdirs();
            System.out.println("生成xml成功");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("生成xml失败");
            return false;
        }
    }


    /**
     * 修改friendXML中保存的聊天数据
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!暂时不用!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * */
    public void set_friendXML(){
        try {
            // 创建SAXReader对象
            SAXReader sr = new SAXReader();
            // 关联xml
            Document document = sr.read("E:\\dataXML\\666.xml");

            // 获取根元素
            Element root = document.getRootElement();
            // 获取student标签
            Element student = root.element("student");
            // 利用student标签添加属性
            Element addAttribute = student.addAttribute("身份证号", "XXXXX");

            // 在xml的某一个标签里修改一个属性
            Attribute id_xg = student.attribute("id");
            id_xg.setText("it002");

            // 修改xml里某一个元素

            // 根据标签修改元素
            // 获取sex标签
            Element sex = student.element("sex");
            sex.setText("AXX");
            // 根据属性值修改元素
            List<Element> elements = student.elements();
            for (Element element : elements) {
                if (element.getText().endsWith("明")) {
                    element.setText("小小小小小明");
                }
            }
            // 调用下面的静态方法完成xml的写出
            saveDocument(document, new File("students.xml"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 删除好友通知XML     写一半发现这个没有用   留着呗
     */
    public void del_friendNoticeXML(String path,String userID,String nodeID){
        String ID="friend-"+nodeID;
        try {
            SAXReader sr = new SAXReader();
            // 关联xml
            Document document = sr.read(path + userID + symbol + userID + suffix_xml);
            Element systemdata = (Element) document.selectSingleNode("//systemdata[@id="+ID+"]");
            systemdata.detach();
            saveDocument(document, new File(path + userID + symbol + userID + suffix_xml));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 给friendXML文件添加标签和数据，这里用处是添加私人聊天记录   顺便也能查内鬼有无发送违规数据 狗命要紧
     * */
    public boolean add_friendXML(String path,String filename,String SendID,String Txtdate,String nick,String type){
        try {
            // 创建SAXReader对象
            SAXReader sr = new SAXReader();
            // 关联xml
            Document document = sr.read(path+filename+suffix_xml);
            // 获取根元素
            Element root = document.getRootElement();
            //在根元素中添加
            Element chatdata = root.addElement("chatdata").addAttribute("id", "chat");
            Element userid = chatdata.addElement("userid");
            Element nickname = chatdata.addElement("nickname");
            Element chat = chatdata.addElement("chat");
            Element datatype = chatdata.addElement("type");
            Element date = chatdata.addElement("date");
            userid.setText(SendID);
            nickname.setText(nick);
            chat.setText(Txtdate);
            datatype.setText(type);
            date.setText(get_time());
            // 调用下面的静态方法完成xml的写出
            saveDocument(document, new File(path+filename+suffix_xml));
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 给chatroomXML文件添加标签和数据
     * @param path 这个是直接指向XML文件的 不需要其他后缀
     * */
    public boolean add_chatroomXML(String path,String SendID,String Txtdata,String nick,String IMG,String type){
        try {
            // 创建SAXReader对象
            SAXReader sr = new SAXReader();
            // 关联xml
            Document document = sr.read(path);
            // 获取根元素
            Element root = document.getRootElement();
            //在根元素中添加
            Element chatdata = root.addElement("chatdata").addAttribute("id", "chat");
            Element userid = chatdata.addElement("userid");
            Element nickname = chatdata.addElement("nickname");
            Element headIMG = chatdata.addElement("headimg");
            Element chat = chatdata.addElement("chat");
            Element datatype = chatdata.addElement("type");
            Element date = chatdata.addElement("date");
            userid.setText(SendID);
            nickname.setText(nick);
            headIMG.setText(IMG);
            chat.setText(Txtdata);
            datatype.setText(type);
            date.setText(get_time());
            // 调用下面的静态方法完成xml的写出
            saveDocument(document, new File(path));
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 创建chatroomXML文件
     */
    public void creat_chatroomXML(String filename){
        try {
            Properties prop = readPropertiesFile();
            String Version= prop.get("Chatroom_Description").toString();
            System.out.println(Version);
            // 1、创建document对象
            Document document = DocumentHelper.createDocument();
            // 2、创建根节点friend_chat
            Element System_notification = document.addElement("chatroom_data");
            // 3、向system_friend节点添加属性
            // 4、生成子节点及子节点内容
            Element chatroom = System_notification.addElement("chatroom").addAttribute("id","chat_data");
            Element userid = chatroom.addElement("userid");
            Element nickname = chatroom.addElement("nickname");
            Element headIMG = chatroom.addElement("headimg");
            Element chat = chatroom.addElement("chat");
            Element datatype = chatroom.addElement("type");
            Element date = chatroom.addElement("date");
            datatype.setText("txt");
            userid.setText("10001");
            nickname.setText("权限汪蔡国师");
            headIMG.setText("yes");
            chat.setText(Version);
            date.setText(get_time());
            saveDocument(document, new File(filename));
            System.out.println("生成chatroomXML成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("生成chatroomXML失败");
        }
    }


    /**
     * 创建系统通知XML
     */
    public void creat_systemXML(String path,String filename){
        try {
            Properties prop = readPropertiesFile();
            String Version= prop.get("Version_Description").toString();
            System.out.println(Version);
            // 1、创建document对象
            Document document = DocumentHelper.createDocument();
            // 2、创建根节点friend_chat
            Element System_notification = document.addElement("System_notification");
            // 3、向system_friend节点添加属性
            // 4、生成子节点及子节点内容
            Element systemdata = System_notification.addElement("systemdata").addAttribute("id","system-notice");
            Element type = systemdata.addElement("type");
            Element userid = systemdata.addElement("userid");
            Element remarks = systemdata.addElement("remarks");
            Element state = systemdata.addElement("state");
            Element time = systemdata.addElement("time");
            type.setText("system");
            userid.setText("权限汪蔡国师");
            remarks.setText(Version);
            time.setText(get_time_system());
            state.setText("Unread");  //Read
            Element systemdata_addfriend = System_notification.addElement("systemdata").addAttribute("id","friend-10002");
            Element type_addfriend = systemdata_addfriend.addElement("type");
            Element userid_addfriend = systemdata_addfriend.addElement("userid");
            Element remarks_addfriend = systemdata_addfriend.addElement("remarks");
            Element state_addfriend = systemdata_addfriend.addElement("state");
            Element time_addfriend = systemdata_addfriend.addElement("time");
            type_addfriend.setText("friend");
            userid_addfriend.setText("权限汪蔡国师");
            remarks_addfriend.setText("默认向新用户发送添加好友通知，只要主动我们就会有故事");
            time_addfriend.setText(get_time_system());
            state_addfriend.setText("Unread");  //Read
            File file=new File(path);
            if (file.mkdirs()) {
                //saveDocument(document, new File("E:\\usersystem\\"+filename+"\\"+ filename +".xml"));//此处需要更改
                saveDocument(document, new File(filename));
                System.out.println("生成systemXML成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("生成systemXML失败");
        }
    }


    /**
     * 删除系统通知XML
     */
    public boolean del_systemNoticeXML(String path,String userID,int number){
        try {
            SAXReader sr = new SAXReader();
            // 关联xml
            Document document = sr.read(path + userID + symbol + userID + suffix_xml);
            Element root = document.getRootElement();//获取节点属性
            List<Element> SystemNotice_list = root.elements("systemdata");
            Element info = SystemNotice_list.get(number);
            info.detach();
            saveDocument(document, new File(path + userID + symbol + userID + suffix_xml));
            System.out.println("删除成功");
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 添加好友系统通知XML   也可与其他通知通用
     */
    public boolean add_systemXML(String path,String nick,String type_s,String receiveID,String remarks_s,String time_s,String sendid){
        try {
            String friend="'"+"friend-"+sendid+"'";
            // 创建SAXReader对象
            SAXReader sr = new SAXReader();
            // 关联xml
            //Document document = sr.read("E:\\dataXML\\"+filename+".xml");
            Document document = sr.read(path + receiveID + symbol + receiveID + suffix_xml);
            // 获取根元素
            Element root = document.getRootElement();
            Element info = (Element)root.selectSingleNode("//systemdata[@id="+friend+"]");
            if (info!=null){  //判断一下是否已经发送了添加信息
                return false;
            }
            //在根元素中添加
            Element systemdata = root.addElement("systemdata").addAttribute("id", "friend-"+sendid+"");
            Element type = systemdata.addElement("type");
            Element userid = systemdata.addElement("userid");
            Element remarks = systemdata.addElement("remarks");
            Element time = systemdata.addElement("time");
            Element state = systemdata.addElement("state");
            type.setText(type_s);
            userid.setText(nick);
            remarks.setText(remarks_s);
            time.setText(time_s);
            state.setText("Unread");  //Read
            // 调用下面的静态方法完成xml的写出
            saveDocument(document, new File(path + receiveID + symbol + receiveID + suffix_xml));
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 修改系统通知XML
     */
    public boolean set_systemNoticeXML(String path,String userID,int number){
        try {
            // 创建SAXReader对象
            SAXReader sr = new SAXReader();
            // 关联xml
            Document document = sr.read(path + userID + symbol + userID + suffix_xml);
            Element root = document.getRootElement();//获取节点属性
            // 获取根元素
            List<Element> SystemNotice_list = root.elements("systemdata");
            Element info = SystemNotice_list.get(number).element("state");
            info.setText("Read");
            // 调用下面的静态方法完成xml的写出
            saveDocument(document, new File(path + userID + symbol + userID + suffix_xml));
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取系统通知XML
     */
    public JSONArray get_systemXML(String path,String id)throws Exception{
        File xml = new File(path+id+symbol+id+suffix_xml);
        //return getStrings(xml);
        return getStrings_system(xml);
    }


    //通用的XML获取方法      *****List<String>版本*******
    private List<String> getStrings(File xml) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document doc = reader.read(xml);//转换为文件
        Element root = doc.getRootElement();//获取节点属性
        List<Element> elements = root.elements();   //element 节点
        List<String> system_list=new ArrayList<>();
        String data = "";
        for (Element element : elements) {
            List<Element> subEs = element.elements();
            for (Element subE : subEs) {
                data=data+subE.getText()+"|";
            }
            system_list.add(data);
            data="";
        }
        return system_list;
    }


    //通用的XML获取方法      *****JSON版本*******
    private JSONArray getStrings_system(File xml) throws DocumentException {
        JSONArray jsonarray=new JSONArray();
        JSONObject jsObject=new JSONObject();
        SAXReader reader = new SAXReader();
        Document doc = reader.read(xml);//转换为文件
        Element root = doc.getRootElement();//获取节点属性
        List<Element> elements = root.elements();   //element 节点
        for (Element element : elements) {
            List<Element> subEs = element.elements();
            for (Element subE : subEs) {
                jsObject.put(subE.getName(),subE.getText());
            }
            jsonarray.add(jsObject);
            jsObject.clear();
        }
        return jsonarray;
    }


    //通用的XML获取方法      *****JSON版本*******
    private JSONArray getStrings_friend(File xml) throws DocumentException {
        JSONArray jsonarray=new JSONArray();
        JSONObject jsObject=new JSONObject();
        SAXReader reader = new SAXReader();
        Document doc = reader.read(xml);//转换为文件
        Element root = doc.getRootElement();//获取节点属性
        List<Element> elements = root.elements();   //element 节点
        for (Element element : elements) {
            List<Element> subEs = element.elements();
            for (Element subE : subEs) {
                jsObject.put(subE.getName(),subE.getText());
            }
            jsonarray.add(jsObject);
            jsObject.clear();
        }
        return jsonarray;
    }


    //通用的XML获取方法      *****JSON版本*******
    private JSONObject getStrings(File xml,int id) throws DocumentException {
        JSONObject jsObject=new JSONObject();
        SAXReader reader = new SAXReader();
        Document doc = reader.read(xml);//转换为文件
        Element root = doc.getRootElement();//获取节点属性
        List<Element> elements = root.elements();   //element 节点
        List<String> system_list=new ArrayList<>();
        String data = "";
        /**
         * 这个地方后期需要更改成JS 键值对的形式 优化客户端
         */
        for (Element element : elements) {
            List<Element> subEs = element.elements();
            for (Element subE : subEs) {
                data=data+subE.getText()+"|";
            }
            system_list.add(data);
            data="";
        }
        jsObject.accumulate(String.valueOf(id),system_list);
        return jsObject;
    }


    //通用的XML获取方法      *****JSON版本*******
    private JSONArray getStrings_chatroom(File xml) throws DocumentException {
        JSONArray jsonarray=new JSONArray();
        JSONObject jsObject=new JSONObject();
        SAXReader reader = new SAXReader();
        Document doc = reader.read(xml);  //转换为文件
        Element root = doc.getRootElement();  //获取节点属性
        List<Element> elements = root.elements();   //element 节点
        for (Element element : elements) {
            List<Element> subEs = element.elements();
            for (Element subE : subEs) {
                jsObject.put(subE.getName(),subE.getText());
            }
            jsonarray.add(jsObject);
            jsObject.clear();
        }
        return jsonarray;
    }


    /**
     * 获取所有好友聊天数据 返回JSON数据
     */
    public JSONArray get_allfriend_chatdata(String path,JSONArray id_list,int id)throws Exception{
        JSONArray all_chatdata=new JSONArray();
        for (Object o : id_list) {
            String filename = creat_chatXMLname(id, Integer.parseInt(o.toString()));
            File xml = new File(path + filename + suffix_xml);
            //此为旧版语句
            //all_chatdata.add(JSONArray.fromObject(getStrings(xml,Integer.parseInt(o.toString()))));
            all_chatdata.add(getStrings(xml,Integer.parseInt(o.toString())));
        }
        return all_chatdata;
    }


    /**
     * 返回当前系统时间--用于日常使用
     */
    public String get_time(){
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        return sdf.format(date);
    }

    /**
     * 返回当前系统时间--用于系统通知使用
     */
    public String get_time_system(){
        SimpleDateFormat sdf = new SimpleDateFormat(system_strDateFormat);
        return sdf.format(date);
    }


    /**
     * 获取friendXML文件中的数据
     */
    public JSONArray get_friendXML(String path,String filename)throws Exception{
        File xml = new File(path+filename+suffix_xml);
        return getStrings_friend(xml);
    }


    /**
     * 获取chatroomXML文件中的数据
     */
    public JSONArray get_chatroomXML(String path)throws Exception{
        File xml = new File(path);
        if(exist_chatroomXML(path)){
            exists=false;
            return null;
        }
        return getStrings_chatroom(xml);
    }


    public void setCount(int count) {
        this.count = count;
    }


    /**
     * 保存XML文件
     */
    public static void saveDocument(Document document, File xmlFile) throws IOException {
        Writer osWrite = new OutputStreamWriter(new FileOutputStream(xmlFile), StandardCharsets.UTF_8);// 创建输出流
        OutputFormat format = OutputFormat.createPrettyPrint(); // 获取输出的指定格式
        format.setEncoding("UTF-8");// 设置编码 ，确保解析的xml为UTF-8格式
        XMLWriter writer = new XMLWriter(osWrite, format);// XMLWriter
        // 指定输出文件以及格式
        writer.write(document);// 把document写入xmlFile指定的文件(可以为被解析的文件或者新创建的文件)
        writer.flush();
        writer.close();
    }


    /**
     * 判断图片文件是否存在
     */
    public boolean exist_file(String id,String path){
        String imgPath;
        imgPath=path+id+".jpg";
        System.out.println(imgPath);
        File file = new File(imgPath);
        return file.exists();
    }


    /**
     * 读Properties文件
     */
    public static Properties readPropertiesFile() {
        Properties prop = new Properties();
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(properties_linux));
            //prop.load(in);//直接这么写，如果properties文件中有汉字，则汉字会乱码。因为未设置编码格式。
            prop.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return prop;
    }


    /**
     * 确定聊天数据XML文件的名字
     */
    public String creat_chatXMLname(int id_read,int receiveID_read){
        String filename;
        if (id_read<receiveID_read){
            filename=id_read+"-"+receiveID_read;
        }
        else {
            filename=receiveID_read+"-"+id_read;
        }
        return filename;
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


    //检测chatroomXML是否被创建
    public boolean exist_chatroomXML(String path) {
        if (exists) {
            try {
                File file = new File(path);
                if (!file.exists()){
                    creat_chatroomXML(path);
                    return true;
                }
                exists = false;
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
        }
        else {
            return false;
        }
    }

}



