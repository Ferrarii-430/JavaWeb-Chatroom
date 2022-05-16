let socket;
const img_All = "imgAll";
const img_none="imgNone";
const txt_all = "txtAll";
const txt_none="txtNone";
let filesize = 0;
const maxsize = 2 * 1024 * 1024;//2M
let file;
let read;
let img_data;
let txt_data;
let txtnone_data;
var WebSocketID;

$(function () {

    $.cookie('test',"中文符号",{ expires: 1, path: '/'});
    //连接websocket按钮
    $("#ws").click(function () {
        ws_connect();
    });
    //关闭连接websocket按钮
    $("#close").click(function () {
        socket.close();
    });
    //发送websocket数据按钮
    $("#put_txt").click(function (){
        sendMsg(makeTxt_All());
    })
    //发送图片数据按钮
    $("#put_img").click(function (){
        //Upload_img();
    })
    //发送单体数据按钮
    $("#put_nonetxt").click(function () {
        sendMsg(make_none());
    });
});


$(function() {
    $(".example1-4").click(function () {
        $("#big-images").modal('show');
    });

    $(".images").on("click","a",function (event){
        event.preventDefault();//阻止浏览器默认行为
        $("#big-images").modal('show');
    });
});

function Test2(){
    $(".custom-control #customCheck1").prop("checked",false);
}


function bigimages(){
    $("#big-images").modal('show');
}
function bigimages2(){
    $("#imgModal").modal('show');
}

//连接websocket
function ws_connect() {
    if('WebSocket' in window) {
        WebSocketID=getCookie("username");
        console.log(WebSocketID);
        socket = new WebSocket("ws://localhost:8080/myHandler?ID="+WebSocketID+"");

        socket.onopen= function() {
            console.log("websocket连接成功");
        }

        socket.onclose= function() {
            console.log("websocket连接关闭");
        }

        //在这里接收处理服务器过来的信息
        socket.onmessage= function(event) {
            console.log("接收消息");
            var Type=event.data.split("|")[0];
            var data=event.data.split("|")[1];
            var time=event.data.split("|")[2];
            switch(Type){
                case "txtAll": printMsg(data+"   时间："+time); break;
                case "txtNone":printMsg(data+"   时间： "+time+" "); break;
                case "imgAll":Getdate_img(); break;
                case "imgNone":break;
            }
        }
    }
    else {
        alert("该浏览器不支持实时通信功能");
    }
}

//websocket发送数据
function sendMsg(msg) {
    socket.send(msg);
    $("#text").html("");
}

//添加上显示
function printMsg(msg) {
    $("#message").append(msg+ "<br/>");
}

//关闭连接
function closeWebSocket(){
    socket.close();
}

//封装txt
function makeTxt_All(){
    let msg = get_text();
    txt_data=txt_all+"|"+msg;
    return txt_data;
}

//封装img
function makeImg_All(msg){
    img_data=img_All+"|"+msg;
    return img_data;
}

//封装nonetxt    测试-1
function make_none(){
    return txtnone_data=txt_none+"|10001|"+get_text();
}

//获取输入框数据
function get_text(){
    return $("#text").text();
}

//发送图片ajax
function Upload_img(msg){
    $.ajax({
        type:"POST",
        url:"http://localhost:8080/view",
        data:{"base64":msg},
        dataType:"text",
        async:true,
        error: function () {  //发生错误时的处理
            alert("发送图片时出现未知错误");
            file_clear();
        },
        success: function () {
            sendMsg("imgAll|1");  //让websocket知道有人发图片了
        }
    })
}


//获取图片ajax
function Getdate_img(){
    $.ajax({
        type:"GET",
        url:"http://localhost:8080/get_view",
        data:null,
        dataType:"text",
        async:true,
        error: function () {  //发生错误时的处理
            alert("获取图片时出现未知错误");
            file_clear();
        },
        success: function (data) {
            const imgHtml = "<img class='imgAll' src=" + data + " style=\"width: 100px\" />";
            printMsg(imgHtml);
            file_clear();
        }
    })
}


function Test_sql(){
    $.ajax({
        type:"GET",
        url:"http://localhost:8080/login_sql",
        data:null,
        dataType:"text",
        async:true,
        error: function () {  //发生错误时的处理
            console.log("错误")
        },
        success: function (data) {
            printMsg(data);
            console.log("成功")
        }
    })
}


//显示上传图片
function showImg() {
    filesize=file = document.getElementById('fileinp').files[0].size;
    if (filesize<maxsize) {
        file = document.getElementById('fileinp').files[0];
        read = new FileReader();
        read.readAsDataURL(file);
        read.onload = function (read) {
            const img_base64 = read.target.result;
            //sendImg(makeImg_All(img_base64));     好家伙数据太大了发送不了没办只能ajax
            Upload_img(img_base64);
        }
    }
    else{
        alert("图片大小不能大于2M");
        file_clear();
    }
}

//判断是否为PC或手机端端
function goPAGE() {
    if ((navigator.userAgent.match(/(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i))) {

    }
    else {
        /*window.location.href="你的电脑版地址";    */
        alert("PC端语音暂时无法使用，等我配SSL证书下来再说");
    }
}


//清空上传文件
function file_clear(){
    const obj = document.getElementById('fileinp');
    obj.outerHTML=obj.outerHTML;  //成功失败都要清空上传文件
}

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

//关闭前断开连接
window.onbeforeunload= function(event) { socket.close(); }

//修改img src
function setsrc(){
    $("#img-1").attr("src","http://localhost:8080/headimg/10001.jpg")
}

function addimg(){
    $("#message").append("<img src='http://localhost:8080/chatroomimg/111.jpg' />");
}

let list_1=[];
function Test_array(){
    $.ajax({
        type:"GET",
        url:"http://localhost:8080/Test_array",
        data:null,
        dataType:"json",
        async:true,
        error: function () {  //发生错误时的处理
            console.log("错误")
        },
        success: function (data) {
            console.log(data);
            list_1=data;
            console.log(list_1);
            console.log(list_1[0]);
            console.log("成功")
        }
    })
}