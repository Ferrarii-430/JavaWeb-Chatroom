let count=60,email_put=false,img_has=false;
let register={};
var result;
var patrn=/[`~!@#$%^&*()_+<>?:"{},.\/;'[\]]/im;
var strRegex = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
const keylist=["username","phone","email","verification","password1","password2","base64"];


//验证码请求
function register_email(){
    email_put=true;
    var email=$("#email").val();
    var data={"email":email};
    var ajax=creat_ajax("POST","user_register_email",data,"text",12000,true);
    ajax.fail(function (){
        Swal.fire({
            title:"发送验证码请求失败",
            text:"西海岸的邮箱有时候可能误报(误报概率极低)，尝试一下游客模式吧，骚瑞。",
            icon:"info",
            confirmButtonText: '好吧'
        }).then(function(isConfirm){
            if (isConfirm.isConfirmed===true){
                // $.cookie('phphwo-username',"",{ expires: 1, domain:'.phphwo.cn',path: '/'});
                // $.cookie('phphwo-password',"",{ expires: 1, domain:'.phphwo.cn',path: '/'});
                 $("form .input-group:eq(3) .div-email .btn-email").css("background-size","100%");
                window.location.href = htp+Test+Loginhtml;
            }
            else if (isConfirm.isConfirmed===false&&isConfirm.dismiss==="backdrop"){
                // $.cookie('phphwo-username',"",{ expires: 1, domain:'.phphwo.cn',path: '/'});
                // $.cookie('phphwo-password',"",{ expires: 1,domain:'.phphwo.cn', path: '/'});
                 $("form .input-group:eq(3) .div-email .btn-email").css("background-size","100%");
                window.location.href = htp+Test+Loginhtml;
            }
        })
    });
    ajax.done(function (data){
        if(data==="done"){
            swal.fire("已发送验证码","5min内有效","success");
        }
        else {
            swal.fire("发送验证码请求失败","邮箱重复或者验证码已经发送过","info");
        }
        $("form .input-group:eq(3) .div-email .btn-email").css("background-size","0%");
        timer();
    });
}


//注册请求
function register_post(){
    var ajax=creat_ajax("POST","user_register",register,"text",10000,false);
    ajax.fail(function (){
        swal.fire("注册请求未知错误","","error");
    });
    ajax.done(function (data){
        console.log(data);
        result=data;
    });
}


//为了减少ajax代码冗余
function creat_ajax(type,url,updata,dataType,time,async){
    return $.ajax({
        type: type,
        url: htp + Test + "/" + url,
        data: {DataName:JSON.stringify(updata)},
        dataType: dataType,
        timeout:time,
        async: async,
    });
}


//发送验证码按钮
$("form .input-group:eq(3) .div-email .btn-email").click(function (){
    $("form .input-group:eq(3) .div-email .btn-email").html("&nbsp;&nbsp;");
    $("form .input-group:eq(3) .div-email .btn-email").css("background-size","100%");
    var email=$("form .input-group:eq(2) input").val();
    if(email.trim()==""||email==null){
        swal.fire("邮箱不合规","","error");
    }
    else {
        if (!strRegex.test(email)) {
            swal.fire("邮箱不合规","","error");
        }
        else {
            if (count != 60) {
                console.log("无效");
            } else {
                console.log("发送");
                register_email();
            }
        }
    }
});
//监听是否上传文件
$("form .show_img #avatar_file").change(function (){
    showImg();//处理上传的图片数据
});

/**
 * @result 此为注册结果
 */
//注册按钮
$("form #register").click(function (e) {
    e.preventDefault();
    var i = 0;
    $("form .input-group").each(function () {
        register[keylist[i]] = $(this).children("input").val();
        i++;
    });
    var result_inspect=data_testing(register);//数据检测
    if (result_inspect!="success"){
        swal.fire(result_inspect,"","info");
    }else {
        register_post();//此为同步操作 返回结果赋值给result
        console.log(isNaN(result));
        console.log(result);
        if (isNaN(result)) {
            swal.fire(result, "注册失败", "error");
        } else {
            $.cookie('phphwo-username',register[keylist[0]],{ expires: 1, path: '/'});
            swal.fire({
                icon: "success",
                title: "注册成功",
                text: "您的数字ID为：" + result + "，请谨记",
                showCloseButton: true,
                confirmButtonText: 'OK',
            }).then(function (isConfirm) {
                if (isConfirm.isConfirmed === true) {
                    if (!isNaN(result)) {
                        window.location.href = htp+Test+Loginhtml;
                    }
                }
            });
        }
    }
});


//显示上传的图片
function showImg() {
    let file;
    filesize=file = document.getElementById('avatar_file').files[0].size;
    if (filesize<maxsize) {
        file = document.getElementById('avatar_file').files[0];
        read = new FileReader();
        read.readAsDataURL(file);
        read.onload = function (read) {
            const img_base64 = read.target.result;
            register[keylist[6]]=img_base64;
            $("form .show_img #avatar_img").attr("src",img_base64).removeAttr("hidden");
            $("form .show_img .text-muted").prop("hidden","hidden");
        }
    }
    else{
        swal.fire("图片大小不能大于2M","","error");
        file_clear();
    }
}


//清空上传文件
function file_clear(){
    const obj = document.getElementById('avatar_file');
    obj.outerHTML=obj.outerHTML;  //成功失败都要清空上传文件
}


//封装一个处理单位数字的函数 CD 60S
function showNum_S(num) {
    if (num < 10) {
        num=num%60;
        return '0' + num;
    }
    else{
        return num % 60;
    }
}


//计时器
function timer(){
    count--;
    $("form .input-group:eq(3) .div-email .btn-email").html(60);
    time = setInterval(function() {
        count--;
        // 需要改变页面上时分秒的值
        $("form .input-group:eq(3) .div-email .btn-email").html(showNum_S(count));
        if (count==0){
            count=60;
            clearInterval(time);
            $("form .input-group:eq(3) .div-email .btn-email").html("发送");
        }
    }, 1000);
}

function data_testing(data) {
    for (let i = 0; i <5;i++) {
        if (i==1){
            i++;
        }
        if (data[keylist[i]]== null||data[keylist[i]].trim()=="") {
            return "有空项";
        }
    }
    if (patrn.test(data[keylist[0]]) || data[keylist[0]].length > 16) {
        return "昵称不合规";
    }
    if (data[keylist[1]]!=""&&data[keylist[1]]!=null){
        if (!(/^1[3456789]\d{9}$/.test(data[keylist[1]]))) {
            return "手机号码不合规"
        }
    }
    if (!email_put){
        return "验证码未发送";
    }
    if(email_put){
        if (!/^\d+$/.test(data[keylist[3]]) || data[keylist[3]].length <= 2) {
            return "验证码不合规"
        }
    }
    if (data[keylist[4]]!=data[keylist[5]]){
        return "密码不一致";
    }
    if (data[keylist[4]].length<3||data[keylist[5]].length<3){
        return "密码位数有点少";
    }
    console.log(data.hasOwnProperty("base64"));
    if (!data.hasOwnProperty(keylist[6]))
    {
        register[keylist[6]]="NoAvatar";
    }
    return "success"
}