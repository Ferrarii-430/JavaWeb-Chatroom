let ID = null,file,time,count = 0,receive_id,read,receive_nick,my_nick,webcount=0,getIDcount=0;
var nickname_list= [],system_list=[],chatdata_list=[];
let friendID_list=[],chatdata=[];
var friend_text="friend";
var system_text="system";
var click_number;//当前点击位置
var click_number_more;
var yes_no=["yes","no"];
var type_data=["txtAll","txtSingle","|","imgAll","imgSingle","mp3All","mp3Single"];
var type=type_data[0];
var voide_jurisdiction=true;
var voice_jurisdiction=false;
var ws_first=true;
$.cookie.raw=true;
//手指移动相关
var posStart = 0;//初始化起点坐标
var posEnd = 0;//初始化终点坐标child
var posMove = 0;//初始化滑动坐标

// split很不错 以后不许再用了

//为什么我不用Vue？？？
//为什么？？？为什么？？？为什么？？？


$(function () {
    //验证身份
    setTimeout(get_cookiID,1000);

    //使用教程
    setTimeout(function () {
        $('#pageTour').modal('show');
    }, 1000);

    //触发一次A标签 是界面沉底
    setTimeout(Chatroom_a_click,1000);
});


//触发chatroom a标签函数
function Chatroom_a_click(){
    $(".layout .navigation .nav-group ul li:eq(1) a")[0].click();
}


//绑定图片点击事件
function imgShow(){
    //绑定聊天中的图片点击事件
    $(".layout .content .chat .chat-body .open-chat div figure").on("click","a",function (event){
        event.preventDefault();//阻止浏览器默认行为
        var src=$(this).children("img")[0].src;
        console.log('查看大图：'+src);
        $("#big-images .modal-dialog .modal-content .modal-body .row .col-md-8 figure img").attr("src", src);
        $("#big-images").modal('show');
    })
}


//绑定图片点击事件
function chatroomimgShow(){
    //绑定聊天中的图片点击事件
    $(".layout .content .chat .chat-body #chatroom-messages .message-item figure").on("click","a",function (event){
        event.preventDefault();//阻止浏览器默认行为
        var src=$(this).children("img")[0].src;
        console.log('查看大图：'+src);
        $("#big-images .modal-dialog .modal-content .modal-body .row .col-md-8 figure img").attr("src", src);
        $("#big-images").modal('show');
    })
}


function get_cookiID(){
    var cookie=$.cookie();
    if (cookie.hasOwnProperty('phphwo-username')){
        ID = cookie['phphwo-username'];
        console.log(ID);
        if (ID!=null&&ID!=""){
            ID=decodeURI(ID);
            get_realID();//获取真实ID
            console.log(ID);
            ws_connect();//连接websocket
        }
    }
    else {
        alert("发生未知错误或无Cookie权限,无法提供服务抱歉");
        window.location.href = htp + Test +Loginhtml;
    }
}


//获取真实ID
function get_realID(){
    console.log("执行了GetID");
    //type,url,updata,dataType,time,async
    var updata={"userID":ID};
    var ajax=creat_ajax("POST","get_User_realID",updata,"JSON",10000,false);//必须同步
    ajax.fail(function (){
        Swal.fire({
            title:"请求数据出错,请给我Cookie权限",
            text:"错误数据已发送至后台",
            icon:"info",
            confirmButtonText: '确定'
        }).then(function(isConfirm){
            if (isConfirm.isConfirmed===true){
                //window.location.href = htp + Test +Loginhtml;
                get_realID();
            }
            else if (isConfirm.isConfirmed===false&&isConfirm.dismiss==="backdrop"){
                window.location.href = htp + Test +Loginhtml;
            }
        })
    });
    ajax.done(function (data){
        var user_data=data;
        if (user_data==null||user_data=="") {
            if (getIDcount<2) {
                swal_currency("获取数据失败正在重新获取","服务器可能负荷超载建议稍后登录","error");
                get_realID();
                getIDcount++;
            }
            else {
                window.location.href = htp + Test +Loginhtml;
            }
        }else {
            ID=user_data["id"];
            my_nick=user_data["nick"];
        }
    });
}


//退出时关闭socket连接
window.onbeforeunload = function () {
    socket.Close();
};



//顾名思义 获取cookie值
function getCookie(cname)
{
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i=0; i<ca.length; i++)
    {
        var c = ca[i].trim();
        if (c.indexOf(name)==0)
            return c.substring(name.length,c.length);
    }
    return "";
}


//获取头像函数
function get_myHeadIMG(){
    //理论上获取到真实ID时就可以直接拿到头像了   这一步是多余的
    //type,url,updata,dataType,time,async
    var updata={"userID":ID};
    var ajax=creat_ajax("POST","myhead_img",updata,"text",3000,true);
    ajax.fail(function (){
        swal_currency("请求头像时出现未知错误","错误数据已发送至后台","info");
    });
    ajax.done(function (data){
        console.log(data);
        if (data=="NoAvatar"){
            var name_data=getString_first(my_nick);
            let headimg_data="<span class=\"avatar-title bg-secondary rounded-circle\">" + name_data + "</span>";
            $(".navigation div ul li:eq(7) a figure").html(headimg_data);
            friend_headimg[ID]=headimg_data;
        }else {
            $(".navigation div ul li:eq(7) a figure img").attr("src", data);
            let headimg_data="<img src=\" " + data + "  \" class=\"rounded-circle\" alt=\"image\">";
            friend_headimg[ID]=headimg_data;
        }
    });
}


//控制聊天头部
function chatheadnone(){
    var hasclass=$(".layout .navigation .nav-group ul li:eq(1) a span").hasClass("badge-success");
    if (hasclass){
        $(".layout .navigation .nav-group ul li:eq(1) a span").removeClass("badge-success");
    }
    show_message("chatroom-messages");
    $(".layout .chat .chat-header .chat-header-action .list-inline .voice-call").css("display","none");
    $(".layout .chat .chat-header .chat-header-action .list-inline .voide-call").css("display","none");
    $(".layout .chat .chat-header .chat-header-action .list-inline .more").css("display","none");
    $(".layout .chat .chat-header .chat-header-user .avatar").html(head_img_or_txt);
    $(".layout .chat .chat-header .chat-header-user div h5").text("公共聊天已开启敏感词检测");
    type=type_data[0];
    Chatroom_a_click();
    setTimeout(imgShow,1000);
}
function chatheadblock(){
    var hasclass=$(".layout .navigation .nav-group ul li:eq(2) a span").hasClass("badge-warning");
    if (hasclass){
        $(".layout .navigation .nav-group ul li:eq(2) a span").removeClass("badge-warning");
    }
    $(".layout .chat .chat-header .chat-header-action .list-inline .voice-call").css("display","inline-block");
    $(".layout .chat .chat-header .chat-header-action .list-inline .voide-call").css("display","inline-block");
    $(".layout .chat .chat-header .chat-header-action .list-inline .more").css("display","inline-block");
}
function chathead_inline_display(){
    $(".layout .chat .chat-header .chat-header-action .list-inline .voice-call").css("display","inline-block");
    $(".layout .chat .chat-header .chat-header-action .list-inline .voide-call").css("display","inline-block");
    $(".layout .chat .chat-header .chat-header-action .list-inline .more").css("display","inline-block");
}
function chathead_inline_none(){
    $(".layout .chat .chat-header .chat-header-action .list-inline .voice-call").css("display","none");
    $(".layout .chat .chat-header .chat-header-action .list-inline .voide-call").css("display","none");
    $(".layout .chat .chat-header .chat-header-action .list-inline .more").css("display","none");
}



//websocket发送数据
function sendMsg(msg) {
    socket.send(msg);
}


//获取在线名单
function get_online_namelist(){
    var ajax=creat_ajax("GET","get_online_namelist",null,"JSON",8000,false);
    ajax.fail(function (){
        swal_currency("获取在线名单出错","错误数据已发送至后台","error");
    });
    ajax.done(function (data){
        if (data!=null) {
            for (let i = 0; i < data.length; i++) {
                const OnlineUser = data[i];
                if (OnlineUser["userid"] != ID) {
                    appendfriend(OnlineUser["nickname"], OnlineUser["userid"], OnlineUser["headimg"], "Online");
                }
            }
        }
    });
}


//添加单个在线用户列表
function addOnlineUserdata(data){
    appendfriend(data["nickname"],data["userid"],data["headimg"],"Online");
}


//删除单个在线用户列表
function delOnlineUserdata(data){
    var number=$("#chatroom .sidebar-body ul li").length;
    console.log(number);
    for (var i=0;i<number;i++) {
        var id=$('#chatroom .sidebar-body ul li:eq('+i+')').attr("value");
        if (id==data['userid']){
            $('#chatroom .sidebar-body ul li:eq('+i+')').remove();
            break;
        }
    }
}


//获取用户系统通知列表
function get_systemNotice_list(){
    var updata={"userID":ID};
    var ajax=creat_ajax("POST","user_notice",updata,"JSON",8000,false);
    ajax.fail(function (){
        swal_currency("请求用户通知列表时出现未知错误","错误数据已发送至后台","info");
    });
    ajax.done(function (data){
        system_list=data;
        for(let i=0;i<system_list.length;i++){
            var system_data=system_list[i];
            var type=system_data["type"];
            var userid=system_data["userid"];
            var text=system_data["remarks"];
            var state=system_data["state"];
            var time=system_data["time"];
            switch (type){
                case friend_text: appendfriend_apply(userid,text,time,state);break;
                case system_text: appendsystem_notice(userid,text,time,state);break;
                default :console.log("部署通知时出现未知错误");break;
            }
        }
    });
}


//发送添加好友请求
function add_friend(rID,remarks){
    var result="fail";
    if (rID==ID){
        return result;
    }
    $.ajax({
        type: "post",
        url: htp + Test + "/user_add_friend",
        data: {"userID": ID,"receiveID":rID,"remarks":remarks},
        dataType: "text",
        async: false,//这里要同步
        error: function () {
            //发生错误时的处理
            result="fail";
            swal.fire("错误","发送好友请求时出现未知错误","info");
        },
        success: function (data) {
            console.log("添加好友反馈"+data);
            result=data;
        }
    });
    return result;
}


//进行websocket连接
function ws_connect() {
    if('WebSocket' in window) {
        socket = new WebSocket("ws://"+Test+"/myHandler?ID="+ID+"");

        socket.onopen= function() {
            console.log("websocket连接成功");
            if (ws_first) {
                //这个地方有请求冗余 不过为了好区分还是分开来了  也不知道好还是不好
                get_friend_list();//获取好友列表  这里需要同步
                get_myHeadIMG();//获取自己的头像
                get_systemNotice_list();//获取系统通知列表
                if (friendID_list.length != 0) {
                    get_allfriend_chatdata(friendID_list);//获取所有好友聊天数据
                }
                get_chatroom_chatdata();//获取聊天室聊天数据
                get_online_namelist();//获取在线列表
                //  上面获取聊天数据这个地方有个很大的问题 后面可能数据过多导致加载缓慢 做分页处理或者点击加载
                get_click_number_more();//初始化删除函数
                browserRedirect(); //判断是移动端或者PC端 做不同响应
                imgShow();
                ws_first=false;
            }else {
                console.log("二次连接不加载数据");
            }
        }

        socket.onclose= function() {
            console.log("连接关闭 3S后尝试重新连接");
            if (webcount<3){
                webcount++;
                Swal.fire({
                    title:"连接已断开",
                    text:"点击确定尝试重新连接",
                    icon:"info",
                    confirmButtonText: '确定'
                }).then(function(isConfirm){
                    if (isConfirm.isConfirmed===true){
                        ws_connect();
                    }
                    else if (isConfirm.isConfirmed===false&&isConfirm.dismiss==="backdrop"){
                        ws_connect();
                    }
                })
            }
            else {
                Swal.fire({
                    title:"连接失败次数过多",
                    text:"请重新登录",
                    icon:"error",
                    confirmButtonText: '好吧'
                }).then(function(isConfirm){
                    if (isConfirm.isConfirmed===true){
                        window.location.href=htp+ Test +Loginhtml;
                    }
                    else if (isConfirm.isConfirmed===false&&isConfirm.dismiss==="backdrop"){
                        window.location.href=htp+ Test +Loginhtml;
                    }
                })
            }
        }

        //在这里接收处理服务器过来的信息
        socket.onmessage= function(event) {
            console.log("接收消息到服务器消息");
            console.log(event.data.length);
            if (event.data.length<250){
                console.log(event.data);
                var obj=JSON.parse(event.data);  //WebSocket发送JSON数据方法未知 先用这个凑合
                var type=obj["type"];
                console.log(obj["type"]);
                switch(type.trim()){
                    case "voice":show_voice_call(obj);break;
                    case "voide":show_voide_call(obj);break;
                    case "voice_start":start_voice(obj);show_phone_off();break;
                    case "voide_start":start_voide(obj);break;
                    case "voice_startMP3":re_mp3data(obj);break;
                    case "voice_close":voice_close();break;
                    case "voide_close":voide_close();break;
                    case "addUser":addOnlineUserdata(obj);break;
                    case "delUser":delOnlineUserdata(obj);break;
                    case "news_room":append_room_chatdata(obj["userid"],obj["nickname"],obj["chat"],obj["datatype"],obj["headimg"],obj["date"]);down();add_room_Tips();break;
                    case "news":append_chatdata(obj["ReceiveID"],obj["ReceiveID"],obj["Nick"],obj["Textdata"],obj["datatype"],obj["time"]);down();add_Tips(obj["ReceiveID"]);break;
                    default :swal.fire("开启通话出现未知错误");break;
                }
            }
            else {
                const arr = _base64ToArrayBuffer(event.data);
                if(arr instanceof ArrayBuffer) {
                    receiveAudioChunk(arr);
                }else{
                    console.log("未知错误");
                }
            }
        }
    }
    else {
        swal.fire("错误","该浏览器不支持实时通信功能,即无法使用本网站的所有服务","info");
    }
}


//显示挂断按钮
function show_phone_off(){
    $(".layout .chat .chat-header .chat-header-action .list-inline .off").css("display","inline-block");
    $(".layout .chat .chat-header .chat-header-user div .text-success i").text("正在Voice通话中...");
}


//弹出 voice通话窗口 等待确认
function show_voice_call(data){
    var imgdata;
    console.log(data);
    var id=data["id"];
    receive_id=id;
    var nick=ID_to_Nick(id);
    console.log(nick);
    var img=nick['headimg'];
    if (img=="NoAvatar"){
        nick=nick["nickname"];
        imgdata=getString_first(nick);
        imgdata="<span class=\"avatar-title bg-secondary rounded-circle\">" + imgdata + "</span>";
    }else {
        imgdata="<img src=\" " + img + "  \" class=\"rounded-circle\" alt=\"image\">";
    }
    nick=nick['nickname'];
    $("#call .modal-dialog .modal-content .modal-body .call div h4").text(nick);
    $("#call .modal-dialog .modal-content .modal-body .call div figure").html(imgdata);
    $("#call").modal("show");
}


//弹出 voide通话窗口 等待确认
function show_voide_call(data){
    var imgdata;
    var id=data["id"];
    receive_id=id;
    var nick=ID_to_Nick(id);
    console.log(nick);
    var img=nick['headimg'];
    if (img=="NoAvatar"){
        nick=nick["nickname"];
        imgdata=getString_first(nick);
        imgdata="<span class=\"avatar-title bg-secondary rounded-circle\">" + imgdata + "</span>";
    }else {
        imgdata="<img src=\" " + img + "  \" class=\"rounded-circle\" alt=\"image\">";
    }
    nick=nick['nickname'];
    $("#videoCall .modal-dialog .modal-content .modal-body .call div h4").text(nick);
    $("#videoCall .modal-dialog .modal-content .modal-body .call div figure").html(imgdata);
    $("#videoCall").modal("show");
}


//关闭voice通话
function voice_close(){
    $(".layout .chat .chat-header .chat-header-action .list-inline .voice-call").css("display","inline-block");
    recStop();
}


/**
 *开启voice通话
 */
function start_voice(data){
    console.log(data);
    receive_id=data["id"];
    swal.clickConfirm();
    recStart();//开启通话 传输数据
}


/**
 *开启接收MP3数据
 */
function re_mp3data(id){
    show_phone_off();
    swal.clickConfirm();
    console.log(id["id"]);
    startMp3();
}


/**
 *开启voide视频通话
 */
function start_voide(data){
    receive_id=data["id"];
    var roomName=data["roomName"];
    console.log(roomName);
    swal.clickConfirm();
    $(".layout .content .sidebar-group:eq(1) > #voide").addClass("active");
    $(".layout .content .sidebar-group:eq(1) > #contact-information").removeAttr("class").addClass("sidebar");
    $(".layout .content .sidebar-group:eq(1)").attr("class","sidebar-group mobile-open");
    start_v(roomName);
    timer();
}


//通过ID获取昵称
function ID_to_Nick(id){
    for (let i=0;i<friendID_list.length;i++){
        if (friendID_list[i]==id){
            return nickname_list[i];
        }
    }
}


//接收到数据后 界面固定在最下端
function down(){
    $('.layout .content .chat .chat-body').scrollTo('100%');
}
function up(){
    $('.layout .content .chat .chat-body').scrollTo('0%');
}


//添加导航目录提示和聊天目录提示
function add_Tips(uid){
    if (uid==null){
        console.log(0);
    }
    var hasclass=$(".layout .navigation .nav-group ul li:eq(2) a span").hasClass("badge-warning");
    if(hasclass){
        $(".layout .navigation .nav-group ul li:eq(2) a span").addClass("badge-warning");
    }
    var index=$(".layout .content .sidebar-group:eq(0) #chats .sidebar-body ul li").length;
    for(var i=0;i<index;i++){
        var id=$('.layout .content .sidebar-group:eq(0) #chats .sidebar-body ul li:eq('+i+') .users-list-body div:eq(0) .realID').text();
        if (id==uid){
            $('.layout .content .sidebar-group:eq(0) #chats .sidebar-body ul li:eq('+i+') .users-list-body div:eq(0) h5').addClass("text-primary");
            var first=$('.layout .content .sidebar-group:eq(0) #chats .sidebar-body ul li:eq('+i+') .users-list-body .users-list-action div:eq(0)').hasClass("new-message-count");
            if (first){
                let count = $('.layout .content .sidebar-group:eq(0) #chats .sidebar-body ul li:eq(' + i + ') .users-list-body .users-list-action .new-message-count').text();
                count=parseInt(count);
                count=count+1;
                $('.layout .content .sidebar-group:eq(0) #chats .sidebar-body ul li:eq('+i+') .users-list-body .users-list-action .new-message-count').text(count);
                $('.layout .content .sidebar-group:eq(0) #chats .sidebar-body ul li:eq('+i+') .users-list-body .users-list-action .action-toggle').remove();
            }
            else {
                var first=$('.layout .content .sidebar-group:eq(0) #chats .sidebar-body ul li:eq('+i+') .users-list-body .users-list-action div:eq(0)').addClass("new-message-count");
                $('.layout .content .sidebar-group:eq(0) #chats .sidebar-body ul li:eq('+i+') .users-list-body .users-list-action .new-message-count').text("1");
            }
        }
    }
}


//清空好友ID数组
function clean_friendID_list(){
    if(friendID_list.length!=0) {
        friendID_list.splice(0, friendID_list.length);//清空
    }
}


//清空好友聊天数组
function clean_chatdata_list(){
    if(chatdata_list.length!=0) {
        chatdata_list.splice(0, chatdata_list.length);//清空
    }
}


//获取聊天室聊天数据
function get_chatroom_chatdata(){
    //type,url,dataName,updata,dataType,time,async
    var ajax=creat_ajax("GET","get_chatroom_chatdata",null,"JSON",8000,true);
    ajax.fail(function (){
        swal.fire("请求聊天数据错误","错误数据已发送至后台","info");
    });
    ajax.done(function (data){
        var chatroomdata=data;
        var length=chatroomdata.length;
        for (var i=0;i<length;i++){
            var chatdata=chatroomdata[i];
            append_room_chatdata(chatdata["userid"],chatdata["nickname"],chatdata["chat"],chatdata["type"],chatdata["headimg"],chatdata["date"]);
        }
    })
}


//获取好友列表
function get_friend_list(){
    clean_friendID_list();
    $.ajax({
        type: "POST",
        url: htp + Test + "/user_relationship",
        data: {"userID": ID},
        dataType: "text",
        async: false,//必须同步  保证后面的获取ID列表
        error: function () {  //发生错误时的处理
            swal.fire("请求好友列表时出现未知错误","错误数据已发送至后台","info");
        },
        success: function (data) {
            if (data==null||data=="null"){
                console.log("暂无好友  快加我好友ID  →  10001");
            }
            else {
                nickname_list=JSON.parse(data);
                for(let i=0;i<nickname_list.length;i++){
                    const friend_data = nickname_list[i];
                    appendfriend(friend_data["nickname"],friend_data["userid"],friend_data["headimg"],"friend");
                    appendfriend_chatwindow(friend_data["userid"]);
                    friendID_list.push(friend_data["userid"]);
                }
            }
        }
    });
}


//绑定挂断voice通话按钮事件
$(".layout .chat .chat-header .chat-header-action .list-inline .off").on("click",function (){
    $(".layout .chat .chat-header .chat-header-action .list-inline .off").css("display","none");
    $(".layout .chat .chat-header .chat-header-user div .text-success i").text("你所热爱的，就是你的生活");
    Stopvoice_ing();
    recStop();
})


//绑定挂断Voide通话按钮事件
$(".layout .content .sidebar-group:eq(1) #voide .voide-footer .btn-close").on("click","a",function (){
    Stopvoide_ing();
    $(".layout .content .sidebar-group #contact-information .voide-footer .time #id_S").text("00");
    $(".layout .content .sidebar-group #contact-information .voide-footer .time #id_M").text("00");
    $(".layout .chat .chat-header .chat-header-action .list-inline .voice-call").css("display","none");
    voide_close();
});


//挂断voice通话
function Stopvoice_ing(){
    $.ajax({
        type: "POST",
        url: htp + Test + "/online_Voice_close",
        data: {"userID": ID,"receive_ID":receive_id},
        dataType: "text",
        async: true,
        error: function () {  //发生错误时的处理
            swal.fire("关闭voice通话错误","已发送错误数据到后台,解决后会给您发送通知","error");
        },
        success: function (data) {
            console.log(data);
        }
    });
}


//挂断voide通话
function Stopvoide_ing(){
    $.ajax({
        type: "POST",
        url: htp + Test + "/online_Voide_close",
        data: {"userID": ID,"receive_ID":receive_id},
        dataType: "text",
        async: true,
        error: function () {  //发生错误时的处理
            swal.fire("关闭voide通话错误","已发送错误数据到后台,解决后会给您发送通知","error");
        },
        success: function (data) {
            console.log("挂断是否成功： "+data);
        }
    });
}


//绑定用户列表点击事件
$("#chats .sidebar-body ul").on("click","li",function(){
    chatheadblock();
    $(this).addClass('open-chat');
    $(this).siblings().removeClass('open-chat');
    var nick=$(this).children("div").children("div").eq(0).children("h5").text();
    var headimg=$(this).children("div").eq(0).children("figure").html();
    var id=$("#chats .sidebar-body ul .open-chat .users-list-body .realID").text();
    show_message(id);
    receive_id=id;
    receive_nick=nick;
    type=type_data[1];
    set_chat_header(nick,headimg);
    var primary=$(this).children("div").children("div").eq(0).children("h5").hasClass("text-primary");
    if (primary){
        $(this).children("div").children("div").eq(0).children("h5").removeClass("text-primary");
        $(this).children("div").children("div").eq(1).children("div").remove();
        $(this).children("div").children("div").eq(1).append(toggle);
    }
    imgShow();
});


//切换聊天显示
/**
 *
 * 这里出BUG了记得改  需要修改判断思路 但不影响使用
 */
function show_message(id){
    //var number=$(".layout .chat .chat-body .messages")
    const number = $(".layout .chat .chat-body > .messages").length;
    for(let i=0;i<number;i++){
        var show_id=$(".layout .chat .chat-body .messages").eq(i).attr("id");
        if (show_id==id){
            $(".layout .chat .chat-body .messages").siblings().removeClass('open-chat');
            $(".layout .chat .chat-body .messages").eq(i).addClass('open-chat');
            break;
        }
        else{
            console.log("当前页面搜索聊天中...");
        }
    }
}
//切换头部显示
function set_chat_header(nick,headimg){
  $(".layout .content .chat .chat-header .avatar").html(headimg);
  $(".layout .content .chat .chat-header div h5").text(nick);
}


//绑定系统通知点击事件
$("#archived .sidebar-body ul").on("click","li",function(){
    click_number=$(this).index();
    var data=$(this).children("div").children("div").eq(0).children("span").text();
    var type=data.split("|")[0];
    var userid=data.split("|")[1];
    var text=data.split("|")[2];
    var state=data.split("|")[3];
    var time=data.split("|")[4];
    console.log(data);
    if (type==friend_text){
        newfriend_notification(userid,text,time,state);
    }
    else if(type==system_text){
        newsystem_notification(userid,text,time,state);
    }
    else {
        Swal.fire("获取数据出现未知错误","已发送错误数据到后台,解决后会给您发送通知","error");
    }
    if (state=="Unread"){
        set_systemState(click_number);
        del_primaryClass(click_number);
    }
});


//添加好友通知点击事件函数
function newfriend_notification(friend_name,remarks,time){
    swal.fire({
        title:"新的好友申请",
        showCloseButton: true,
        showCancelButton: true,
        confirmButtonText: '接受',
        cancelButtonText:'拒绝',
        html:"<span>用户: &nbsp;<span style='font-weight:bold'>"+friend_name+"&nbsp;</span> 给您发送新的好友申请</span><br/>"  +
            "<br/><span>这是他给你的备注↓</span><br/>",
        footer: time,
        input:'textarea',
        inputValue:""+remarks+"",
    }).then(function(isConfirm){
        if (isConfirm.isConfirmed===true){
            Swal.fire(
                '添加成功!',
                '你们已经是好友了.',
                'success'
            )
            friendapply_feedback(yes_no[0],friend_name);
            del_user_systemNotice(click_number);
        }
        else if (isConfirm.isConfirmed===false&&isConfirm.dismiss==="cancel"){
            Swal.fire(
                '您已拒绝!',
                '为什么不加我QaQ',
                'question'
            )
            del_user_systemNotice(click_number);
        }
        else if (isConfirm.isConfirmed===false&&isConfirm.dismiss==="backdrop"){
            Swal.fire(
                '鸟我一下呗',
                '为什么不加我 0.0',
                'warning'
            )
        }
    })
    swal.disableInput();
}


//系统通知点击事件函数
function newsystem_notification(userid,text,time){
    swal.fire({
        title:""+userid+" 发来的通知",
        showCloseButton: true,
        html: "<div style='white-space: pre-line;'>"+text+"</div>",
        footer:time,
        confirmButtonText: '朕知道了',
    });
    swal.disableInput();
}


//添加好友按钮绑定事件
$("#addfriend").click(function (){
    //bootstrap模态框无法用Jq获取值 只能用原生js
    var id=$(this).parent().parent().find('#emails').val();
    var message=$(this).parent().parent().find('#message').val();
    var result=add_friend(id,message);
    console.log("result:"+result);
    if(result=="success"){
        $(this).parent().parent().find('#emails').val("");
        $(this).parent().parent().find('#message').val("");
        const Toast = Swal.mixin({
            toast: true,
            position: 'top-end',
            showConfirmButton: false,
            timer: 3000,
            timerProgressBar: true,
            didOpen: (toast) => {
                toast.addEventListener('mouseenter', Swal.stopTimer)
                toast.addEventListener('mouseleave', Swal.resumeTimer)
            }
        })

        Toast.fire({
            icon: 'success',
            title: '添加好友信息发送成功!'
        })
    }else {
        to_end("查无此人或你们已经是好友");
    }
    $("#addFriends").modal("hide");
});


//绑定电话voice按钮
$(".layout .content .chat .chat-header .chat-header-action .list-inline li:eq(1) a").click(function (){
    var span="&nbsp;&nbsp;<span class=\"text-success\">calling...</span>";
    var name=$(".layout .content .chat .chat-header div h5").text();
    var head=$(".layout .content .chat .chat-header .avatar").html();
    $("#call .modal-dialog .modal-content .modal-body .call div h4").text(name);
    $("#call .modal-dialog .modal-content .modal-body .call div h4").append(span);
    $("#call .modal-dialog .modal-content .modal-body .call div figure").html(head);
});


//绑定视频通话voide按钮
$(".layout .content .chat .chat-header .chat-header-action .list-inline li:eq(2) a").click(function (){
    var span="&nbsp;&nbsp;<span class=\"text-success\">video calling...</span>";
    var name=$(".layout .content .chat .chat-header div h5").text();
    var head=$(".layout .content .chat .chat-header .avatar").html();
    $("#videoCall .modal-dialog .modal-content .modal-body .call div h4").text(name);
    $("#videoCall .modal-dialog .modal-content .modal-body .call div h4").append(span);
    $("#videoCall .modal-dialog .modal-content .modal-body .call div figure").html(head);
    //receive_id
});


//绑定-确定-视频电话voide按钮
$("#videoCall .modal-dialog .modal-content .modal-body .call div .action-button button:eq(1)").click(function (){
    if (voide_jurisdiction) {
        var voide;
        let Toast;
        voide = $.ajax(
            {
                type: "POST",
                url: htp + Test + "/online_Voide",
                data: {"userID": ID, "receive_ID": receive_id},
                dataType: "text",
                async: true,
                timeout: 30000,//40S后取消
                error: function () {  //发生错误时的处理
                    //这个地方不太好写错误反馈  换到socket中写
                },
                success: function (data) {
                    console.log(data);
                    if (data == "对方未在线") {
                        swal.clickConfirm();
                        swal_currency(data, "", "warning");
                    }
                }
            }
        );
        Toast = Swal.mixin({
            toast: true,
            position: 'top-end',
            showConfirmButton: true,
            timer: 30000,
            timerProgressBar: true,
            didOpen: (toast) => {
                toast.addEventListener('mouseenter', Swal.stopTimer)
                toast.addEventListener('mouseleave', Swal.resumeTimer)
            }
        })
        Toast.fire({
            icon: 'info',
            title: '正在等待对方接听!',
            confirmButtonText: "取消"
        }).then((result) => {
            if (result.isConfirmed) {
                voide.abort();
            }
        })
    }
    else {
        swal_currency("移动端H5暂不支持","手机浏览器可操作性较低只能用App,PC端无异常","info");
    }
});




//绑定-确定-电话voice按钮
$("#call .modal-dialog .modal-content .modal-body .call div .action-button button:eq(1)").click(function (){
    if (voice_jurisdiction) {
        var voice;
        let Toast;
        voice = $.ajax(
            {
                type: "POST",
                url: htp + Test + "/online_Voice",
                data: {"userID": ID, "receive_ID": receive_id},
                dataType: "text",
                async: true,
                timeout: 30000,//30S后取消
                error: function () {  //发生错误时的处理
                    //这个地方不太好写错误反馈  换到switch中写
                },
                success: function (data) {
                    console.log(data);
                    if (data == "对方未在线") {
                        swal.clickConfirm();
                        swal_currency("对方未在线", "", "warning");
                    }
                }
            }
        );
        Toast = Swal.mixin({
            toast: true,
            position: 'top-end',
            showConfirmButton: true,
            timer: 30000,
            timerProgressBar: true,
            didOpen: (toast) => {
                toast.addEventListener('mouseenter', Swal.stopTimer)
                toast.addEventListener('mouseleave', Swal.resumeTimer)
            }
        })
        Toast.fire({
            icon: 'info',
            title: '正在等待对方接听!',
            confirmButtonText: "取消"
        }).then((result) => {
            if (result.isConfirmed) {
                voice.abort();
            }
        })
    }
    else {
        swal_currency("没有麦克风权限","不给我权限录不了音","info");
    }
});


//chat-foot 发送聊天数据按钮事件绑定
$(".layout .content .chat .chat-footer .form-buttons .btn-primary").click(function (){
    const txt = $(".layout .content .chat .chat-footer > div > .form-control").val();
    if (txt!=null&&txt!="") {
        if (receive_id == "" || receive_id == null) {
            receive_id = $(".layout .content .chat .chat-header div h5").text();
        }
        send_chat_data(txt, type);
        $(".layout .content .chat .chat-footer > div > .form-control").text("");
    }
    else {
        swal_currency("发送失败","消息不能为空","question");
    }
});


//发送图片事件绑定
$(".layout .content .chat .chat-footer .form-buttons .send-image").click(function (){
    document.getElementById("image").click();
});


//发送语音事件绑定 固定为MP3
//touchstart  第一次按下时触发开始录音
$(".layout .content .chat .chat-footer .form-buttons .send-mp3").on("touchstart",function (event){
    event.preventDefault();//阻止浏览器默认行为
    if (!voice_jurisdiction){
        swal_currency("未获取麦克风权限","不给我权限发不了语音","error");
        showBlackBoxNone();
    }
    else {
        posStart = 0;
        posStart = event.touches[0].pageY;//获取起点坐标
        recStart_getup();  //先开启录音
        showBlackBoxSpeak();
    }
});
//touchmove  会不停的触发 捕获触摸点 直到松开
$(".layout .content .chat .chat-footer .form-buttons .send-mp3").on("touchmove",function (event){
    event.preventDefault();//阻止浏览器默认行为
    posMove = event.targetTouches[0].pageY;//获取滑动实时坐标
    if (posStart - posMove < 200) {
        //隐藏录音 显示暂停
        showBlackBoxSpeak();
    } else {
        //显示录音 隐藏暂停
        showBlackBoxPause();
    }
});
//touchend  手指松开后触发的事件
$(".layout .content .chat .chat-footer .form-buttons .send-mp3").on("touchend",function (event){
    event.preventDefault();//阻止浏览器默认行为
    posEnd = 0;
    posEnd = event.changedTouches[0].pageY;//获取终点坐标
    recStop_getup(); //先停止
    //初始化状态

    if (posStart - posEnd < 200) {
        swal.fire("发送成功","","success");

    } else {
        swal.fire("取消发送","","info");
    }
    showBlackBoxNone();
});


//监听是否上传文件
$(".layout .content .chat .chat-footer .form-buttons #image").change(function (){
    showImg();//处理上传的图片数据
});


//显示上传的图片
function showImg() {
    filesize=file = document.getElementById('image').files[0].size;
    if (filesize<maxsize) {
        file = document.getElementById('image').files[0];
        read = new FileReader();
        read.readAsDataURL(file);
        read.onload = function (read) {
            const img_base64 = read.target.result;
            upload_img(type,img_base64);
        }
    }
    else{
        swal.fire("图片大小不能大于2M","","error");
        file_clear();
    }
}


//清空上传文件
function file_clear(){
    const obj = document.getElementById('image');
    obj.outerHTML=obj.outerHTML;  //成功失败都要清空上传文件
}


//上传图片到服务器
function upload_img(type,msg){
    var img_data;
    if(type=="txtAll"){
        img_data={"type":type_data[3],"SendID":ID,"base64":msg,"Data_type":"img"};
    }
    else {
        img_data={"type":type_data[4],"SendID":ID,"receive_ID":receive_id,"base64":msg,"Data_type":"img"};
    }
    if (receive_id==""||receive_id==null){
        receive_id=$(".layout .content .chat .chat-header div h5").text();
    }
    $.ajax({
        type:"POST",
        url:htp + Test + "/upload_img",
        data:{"img_data":JSON.stringify(img_data)},
        dataType:"text",
        async:true,
        error: function () {  //发生错误时的处理
            swal.fire("发送图片时出现未知错误","","error");
            file_clear();
        },
        success: function (data) {
            console.log(data);
            if (data!=null&&data!="null"){
                //第三个可改为 my_nick
                append_chatdata(receive_id,ID,receive_nick,msg,"img",get_timeing());
            }
        }
    })
}


//上传音频到服务器
function upload_mp3(type,msg){
    var img_data;
    var reader = new FileReader();
    reader.readAsDataURL(msg);
    reader.onloadend = function() {   //reader被改变时才提交 否则不动
        var base64 = (/.+;\s*base64\s*,\s*(.+)$/i.exec(reader.result)||[])[1];
        if(type=="txtAll"){
            img_data={"type":type_data[5],"SendID":ID,"base64":base64,"Data_type":"mp3"};
        }
        else {
            img_data={"type":type_data[6],"SendID":ID,"receive_ID":receive_id,"base64":base64,"Data_type":"mp3"};
        }
        if (receive_id==""||receive_id==null){
            receive_id=$(".layout .content .chat .chat-header div h5").text();
        }
        $.ajax({
            type:"POST",
            url:htp + Test + "/upload_mp3",
            data:{"mp3_data":JSON.stringify(img_data)},
            dataType:"text",
            async:true,
            error: function () {  //发生错误时的处理
                swal.fire("发送音频时出现未知错误","","error");
                file_clear();
            },
            success: function (data) {
                if (data!=null&&data!="null"){
                    var obj_url = window.URL.createObjectURL(msg);
                    append_chatdata(receive_id,ID,receive_nick,obj_url,"mp3",get_timeing());
                    down();
                }
            }
        })
    }
}

//点击后修改XML中的阅读状态
function set_systemState(number){
    $.ajax({
        type: "POST",
        url: htp + Test + "/set_userXML_state",
        data: {"userID": ID,"number":number},
        dataType: "text",
        async: true,
        error: function () {  //发生错误时的处理
            swal.fire("修改阅读状态时出现未知错误","已发送错误数据，我也不知道为什么，但问题不大又不是不能用","error");
        },
        success: function (data) {
            console.log(data);
        }
    });
}


//去除蓝色提示格式
function del_primaryClass(number){
    $("#archived .sidebar-body ul li").eq(number).children("div").children("div").eq(0).children("h5").removeClass("text-primary");
    if ($("#archived .sidebar-body ul li").eq(number).children("div").children("div").eq(1).children("div").eq(0).text()==1){
        $("#archived .sidebar-body ul li").eq(number).children("div").children("div").eq(1).children("div").eq(0).remove();
    }
}


//清空通知列表
function del_systemList(){
    $("#archived .sidebar-body ul").html("");
}

//清空好友列表
function del_friendList(){
    $("#chats .sidebar-body ul").html("");
}


//删除用户个人系统通知
function del_user_systemNotice(number){
    $.ajax({
        type: "POST",
        url: htp + Test + "/del_systemNoticeXML",
        data: {"userID": ID,"del_number":number},
        dataType: "text",
        async: true,
        error: function () {  //发生错误时的处理
            swal.fire("删除系统通知时出现未知错误","已发送错误数据到后台，我也不知道为什么，但问题不大又不是不能用QaQ","error");
        },
        success: function (data) {
            console.log(data);
            del_systemList();
            get_systemNotice_list();
        }
    });
}


//返回用户对好友申请的结果
function friendapply_feedback(boole,receiveID){
    $.ajax({
        type: "POST",
        url: htp + Test + "/friendapply_feedback",
        data: {"userID": ID,"boole":boole,"friendName":receiveID},
        dataType: "text",
        async: true,
        error: function () {  //发生错误时的处理
            swal.fire("删除系统通知时出现未知错误","已发送错误数据，我也不知道为什么，但问题不大又不是不能用","error");
        },
        success: function (data) {
            console.log(data);
            update_newFriend(data,receiveID);
            del_friendList();//清空
            get_friend_list();//更新数据
            del_systemList();//清空
            get_systemNotice_list();//更新数据
        }
    });
}


//绑定删除通知按钮    这个地方有问题 第一次按下不触发，希望后面能解决 虽然有点影响使用 但能凑合
function get_click_number_more(){
    $("#archived .sidebar-body .list-group .list-group-item .users-list-body .users-list-action .action-toggle .dropdown div").on("click","a:eq(1)",function(){
        click_number_more=$(this).parents("li").index();
    });
    if (click_number_more!=null) {
        del_user_systemNotice(click_number_more);
    }
}


//添加好友后需要更新好友列表 这里就不重新刷新一次数据了
/**
 *
 * @param userid 我自己的ID
 * @param receiveid 对方的昵称
 */
function update_newFriend(userid,receiveid){
    var updata={"userID":userid,"receiveID":receiveid};
    var ajax=creat_ajax("POST","update_newfriend",updata,"JSON",8000,false);
    ajax.fail(function (){
        swal.fire("请求聊天数据错误","错误数据已发送至后台","info");
    });
    ajax.done(function (data){
        console.log(data);
        var D=data[0];
        appendfriend_chatwindow(D["userid"]);
        for (var i=0;i<data.length;i++){
            var result=data[i];
            var userid_real = result["userid"];
            var nick = result["nickname"];
            var text = result["chat"];
            var type = result["type"];
            var time = result["date"];
            append_chatdata(userid_real,userid_real,nick,text,type,time);
        }
    })
}


//自动消失窗口函数 固定2秒后消失
function to_end(text){
    const Toast = Swal.mixin({
        toast: true,
        position: 'top-end',
        showConfirmButton: false,
        timer: 2000,
        timerProgressBar: true,
        didOpen: (toast) => {
            toast.addEventListener('mouseenter', Swal.stopTimer)
            toast.addEventListener('mouseleave', Swal.resumeTimer)
        }
    })
    Toast.fire({
        icon: 'error',
        title: text,
    })
}


//发送聊天数据
function send_chat_data(txtdata,chat_type){
    //var chat_txt={"SendID": ID,"ReceiptID":receive_id,"Txtdate":txtdata,"Chat_type":chat_type,"Data_type":"txt"};
    var chat_txt;
    var type_or=type;
    chat_txt={"Chat_type":chat_type,"SendID":ID,"ReceiptID":receive_id,"Txtdate":txtdata,"Data_type":"txt"};
    $.ajax({
        type: "POST",
        url: htp + Test + "/receive_chatdata",
        data:{"chat_txt":JSON.stringify(chat_txt)},
        dataType: "text",
        async: true,
        error: function () {  //发生错误时的处理
            swal.fire("发送聊天数据出错","已发送错误数据到后台,解决后会给您发送通知","error");
        },
        success: function (data) {
            if (data!=null&&data!="null"){
                if (type_or=="txtSingle") {
                    append_chatdata(receive_id,ID, my_nick,txtdata,"txt", get_timeing());
                }else {
                    //发送会给所有人发送一次，所以不需要添加
                    //append_room_chatdata(ID,my_nick,txtdata,"txt","yes",get_timeing());
                }
                down();
            }
        }
    });
}


/**
 * @param ID_list 用户好友ID列表
 * 这里有个很大的问题 但好友数据很多的时候会造成卡顿 记得做分页加载
 */
//获取所有好友聊天记录
function get_allfriend_chatdata(ID_list){
    var receiveID;//聊天记录输入的ID
    let chat=[];//聊天数据
    $.ajax({
        type: "POST",
        url: htp + Test + "/chatdata_array",
        data: {"list_ID":JSON.stringify(ID_list),"userID": ID},
        dataType: "JSON",
        async: true,
        error: function () {  //发生错误时的处理
            swal.fire("获取聊天记录出现错误","已发送错误数据到后台,解决后会给您发送通知","error");
        },
        success: function (data) {
            clean_chatdata_list();
            chatdata_list=data;
            for(var i=0;i<chatdata_list.length;i++){
                chatdata = chatdata_list[i];
                for(var tmp in chatdata){
                    receiveID=tmp;
                    chat=chatdata[tmp];
                    //这个地方固定住了没办法，之前没想到 改不了
                    /**
                     * 我又发现了可以改的方法 后期再改
                     */
                    for(var j=0;j<chat.length;j++) {
                        var userid = chat[j].split("|")[0];
                        var nick = chat[j].split("|")[1];
                        var text = chat[j].split("|")[2];
                        var type = chat[j].split("|")[3];
                        var time = chat[j].split("|")[4];
                        append_chatdata(receiveID,userid,nick,text,type,time);
                    }
                }
            }
        }
    });
}


//获取数组所有KEY值
function getObjectKeys(object)
{
    var keys = [];
    for (var property in object)
        keys.push(property);
    return keys;
}


//关闭voide通话
function voide_close(){
    stop_voide();
    clearInterval(time);
    $(".layout .content .sidebar-group:eq(1)").removeAttr("class").addClass("sidebar-group");
    $(".layout .content .sidebar-group:eq(1)>#voide").removeAttr("class").addClass("sidebar");
    $(".layout .content .sidebar-group:eq(1)>#contact-information").addClass("active");
    count=0;
}


//显示voide视频通话的计时器
function timer(){
    time = setInterval(function() {
        count++;
        // 需要改变页面上时分秒的值
        $(".layout .content .sidebar-group:eq(1) #voide .voide-footer .time .id_S").html(showNum_S(count));
        $(".layout .content .sidebar-group:eq(1) #voide .voide-footer .time .id_M").html(showNum_M(count));
    }, 1000);
}
//封装一个处理单位数字的函数
function showNum_S(num) {
    var math;
    if (num < 10) {
        math=num%60;
        return '0' + math;
    }
    else if (num>=10&&num<=60){
        return num % 60;
    }
    else {
        math=Math.floor(num%60);
        if (math<10){
            return '0' + math;
        }else {
            return math;
        }
    }
}
function showNum_M(num) {
    var math;
    if (num > 60) {
        math=Math.floor(num / 60);
        if (math<10){
            return '0' + math;
        }
        else {
            return math;
        }
    }
    else {
        return '00';
    }
}


//对Date的扩展，将 Date 转化为指定格式的String
Date.prototype.Format = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1, // 月份
        "d+": this.getDate(), // 日
        "h+": this.getHours(), // 小时
        "m+": this.getMinutes(), // 分
        "s+": this.getSeconds(), // 秒
        "q+": Math.floor((this.getMonth() + 3) / 3), // 季度
        "S": this.getMilliseconds() // 毫秒
    };
    if (/(y+)/.test(fmt))
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    }
    if (this.getHours()>12){
        fmt=fmt+" 下午";
    }
    else {
        fmt=fmt+" 上午";
    }
    return fmt;
}


//获取指定的Date格式
function get_timeing(){
    var time = new Date().Format("MM-dd hh:mm");
    return time;
}


//判断用户是否为移动端
function browserRedirect() {
    var sUserAgent = navigator.userAgent.toLowerCase();
    var bIsIpad = sUserAgent.match(/ipad/i) == "ipad";
    var bIsIphoneOs = sUserAgent.match(/iphone os/i) == "iphone os";
    var bIsMidp = sUserAgent.match(/midp/i) == "midp";
    var bIsUc7 = sUserAgent.match(/rv:1.2.3.4/i) == "rv:1.2.3.4";
    var bIsUc = sUserAgent.match(/ucweb/i) == "ucweb";
    var bIsAndroid = sUserAgent.match(/android/i) == "android";
    var bIsCE = sUserAgent.match(/windows ce/i) == "windows ce";
    var bIsWM = sUserAgent.match(/windows mobile/i) == "windows mobile";
    if (bIsIpad || bIsIphoneOs || bIsMidp || bIsUc7 || bIsUc || bIsAndroid || bIsCE || bIsWM) {
        if (is_weixn()){
            //是微信则...
        }
        // $(".layout .chat .chat-header .chat-header-action .list-inline .voice-call").css("display","none");
        // $(".layout .chat .chat-header .chat-header-action .list-inline .voide-call").css("display","none");
        // $(".layout .chat .chat-header .chat-header-action .list-inline .more").css("display","none");
        $(".layout .chat .chat-header .chat-header-user div h5").text("公共聊天已开启敏感词检测");
        //移动端先获取权限
        createDelayDialog();
        voide_jurisdiction=false;
    }
     else {
        // $(".layout .chat .chat-header .chat-header-action .list-inline .voice-call").css("display","none");
        // $(".layout .chat .chat-header .chat-header-action .list-inline .voide-call").css("display","none");
        // $(".layout .chat .chat-header .chat-header-action .list-inline .more").css("display","none");
        $(".layout .chat .chat-header .chat-header-user div h5").text("为防止自己进局子，公共聊天已开启敏感词检测，见谅");
        voide_jurisdiction=true;//电脑端先暂时给true
        console.log("pc");
    }
}


//判断用户是否为微信浏览器
function is_weixn(){
    var ua = navigator.userAgent.toLowerCase();
    if(ua.match(/MicroMessenger/i)=="micromessenger") {
        return true;
    } else {
        return false;
    }
}


//添加收到聊天室数据时的提示
function add_room_Tips(){
    var hasclass=$(".layout .navigation .nav-group ul li:eq(1) a span").hasClass("badge-success");
    if(!hasclass){
        $(".layout .navigation .nav-group ul li:eq(1) a span").addClass("badge-success");
    }
}


//隐藏录音 显示暂停
var showBlackBoxPause = function () {
    //bt_recoding.value = '松开手指，取消发送';
    $(".blackBoxSpeak").css("display","none");
    $(".blackBoxPause").css("display","block");
}

//隐藏录音
var showBlackBoxNone = function () {
    $(".blackBoxSpeak").css("display","none");
    $(".blackBoxPause").css("display","none");
}

//显示录音 隐藏暂停
var showBlackBoxSpeak = function () {
    //bt_recoding.value = '松开 结束';
    $(".blackBoxSpeak").css("display","block");
    $(".blackBoxPause").css("display","none");
}


//为了减少ajax代码冗余  顺便提升下复杂度
function creat_ajax(type,url,updata,dataType,time,async){
    return $.ajax({
        type: type,
        url: htp + Test + "/" + url,
        data: {dataName: JSON.stringify(updata)},
        dataType: dataType,
        timeout:time,
        async: async,
    });
}