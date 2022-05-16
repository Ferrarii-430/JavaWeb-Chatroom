var user;
var pass;
var cookie_data;
$.cookie.raw=true;

//顶级屎山

window.onload=function(){
    cookie_data=$.cookie();
    if (cookie_data.hasOwnProperty('phphwo-username')) {
        if (cookie_data['phphwo-username'] == null || cookie_data['phphwo-username'] == "") {
            console.log("未记住用户名");
        } else {
            if (cookie_data.hasOwnProperty('phphwo-username')) {
                if (cookie_data['phphwo-password'] == null || cookie_data['phphwo-password'] == "") {
                    console.log("记住用户名，未记住密码");
                    $(".form-wrapper form .input-name .form-control").val($.cookie("phphwo-username"));
                } else {
                    console.log("都已记住");
                    $(".form-wrapper form .input-name .form-control").val($.cookie("phphwo-username"));
                    $(".form-wrapper form .input-pass .form-control").val($.cookie("phphwo-password"));
                    $(".form-wrapper form .d-flex .custom-checkbox .custom-control-input").attr("checked", true);
                }
            }
            else {
                console.log("记住用户名，未记住密码");
            }
        }
    }
    else {
        console.log("未记住用户名");
    }
}

//获取用户填写数据
function getLoginData(){
    user=$("form .input-name .form-control").val();
    pass=$("form .input-pass .form-control").val();
    console.log(user+pass);
    if(user==""||pass==""){
        swal_currency("输入有空","请检查输入","question");
    }
    else if (testing(user,pass)){
        swal_currency("账户名或密码违规","请检查输入","warning");
    }
    else
    {
        Login();
    }
}

function testing(str1,str2){
    if (patrn_name.test(str1)){
        return false;
    }
    return str2.length <= 2;
}


//ajax提交登录数据
function Login(){
    $.ajax({
        type:"POST",
        url:htp+ Test +"/login_sql",
        data:{username:user,password:pass},
        dataType:"text",
        async:true,
        error: function () {  //发生错误时的处理
            swal_currency("出现未知错误","已发送错误报告到后台","info");
        },
        success: function (data) {
            console.log(data);
            switch (data){
                case "404":swal_currency("账号或密码错误","","error");break;
                case "500":swal_currency("该账号已登录","如有疑问请点击反馈","info");break;
                case "405":swal_currency("账号拒绝访问","如有疑问请点击反馈","error");break;
                case "200":login();break;
                default:swal_currency("出现未知错误","已发送错误报告到后台","info");break;
            }
        }
    })
}
//登录按钮
$(".form-wrapper form .btn-block").click(function (event){
    event.preventDefault();
    getLoginData();
});
//游客按钮
$(".form-wrapper form .tourist").click(function (event){
    event.preventDefault();//阻止浏览器默认行为
    tourist();
});


function login(){
    $.cookie('phphwo-username',user,{ expires: 7, domain:'.phphwo.cn',path: '/'});
    if ($("form .d-flex .custom-checkbox .custom-control-input").get(0).checked){
        console.log("记住密码");
        $.cookie('phphwo-password',pass, { expires: 7, domain:'.phphwo.cn',path: '/'});
    }
    else {
        $.removeCookie('phphwo-password',{path: '/'});
    }
    const Toast = Swal.mixin({
        toast: true,
        position: 'top-end',
        showConfirmButton: false,
        timer: 1500,
        timerProgressBar: true,
        didOpen: (toast) => {
            toast.addEventListener('mouseenter', Swal.stopTimer)
            toast.addEventListener('mouseleave', Swal.resumeTimer)
        }
    })
    Toast.fire({
        icon: 'success',
        title: '登录成功!'
    })
    setTimeout(goTest,1500);
}

function goTest(){
    window.location.href = htp+ Test +Testhtml;
}

function tourist(){
    //游客登录记得修改为不记住密码
    $("form .d-flex .custom-checkbox .custom-control-input").prop("checked",false);

    //这里就不做加密了
    $.ajax({
        type:"POST",
        url:htp+ Test +"/tourist_sql",
        data:{tourist_sql:"tourist"},
        dataType:"JSON",
        async:true,
        error: function () {  //发生错误时的处理
            swal_currency("出现未知错误","已发送错误报告到后台","info");
        },
        success: function (data) {
            console.log(data);
            if (data['state']=="none"){
                user=data['user'];
                pass=data['pass'];
                Login();
            }
            else if(data['state']=="fail") {
                swal_currency("当前暂无游客账号可用","请稍后尝试","error");
            }
            else {
                swal_currency("出现未知错误","已发送错误报告到后台","info");
            }
        }
    })
}

//绑定Enter登录键    写完之后发现好像不需要
// document.addEventListener("keyup", function(event){
//     event.preventDefault();
//     if (event.key == "Enter") {
//
//     }
// });

$(".form-wrapper #bottom_layer .s-bottom-layer-content .make a").click(function (){
    swal_currency("使用须知","严禁狼虎之词发言! 严禁涉黄! 严禁刷屏!","info");
});


//顾名思义 修改cookie值
function setCookie(cname,cvalue,exdays)
{
    var d = new Date();
    d.setTime(d.getTime()+(exdays*24*60*60*1000));
    var expires = "expires="+d.toUTCString();
    document.cookie = cname + "=" + cvalue + "; " + expires;
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