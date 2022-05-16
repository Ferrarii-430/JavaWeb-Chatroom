var user;
var pass;

//获取用户填写数据
function getLoginData(){
   user=$("#username-login").val();
   pass=$("#password-login").val();
    console.log(user+pass)
   if(user==""||pass==""){
       alert("输入有空");
   }
   else
   {
       Login();
   }
}

//ajax提交登录数据
function Login(){
    $.ajax({
        type:"POST",
        url:"http://"+ Test +"/login_sql",
        data:{username:user,password:pass},
        dataType:"text",
        async:true,
        error: function () {  //发生错误时的处理
            console.log("错误")
        },
        success: function (data) {
            console.log(data);
            switch (data){
                case "404":Swal.fire("账号或密码错误","","error");break;
                case "500":Swal.fire("该账号已登录","如有疑问请点击反馈","info");break;
                case "405":Swal.fire("账号拒绝访问","如有疑问请点击反馈","error");break;
                case "200":login();break;
                default:Swal.fire("出现未知错误","已发送错误报告到后台","info");break;
            }
        }
    })
}

function login(){
    setCookie("phphwo-username",user,30);
    Swal.fire({
        title:"登录成功",
        text:"2S后自动跳转",
        icon:"success",
        time:2000,
    });
    window.location.href = htp+ Test +Testhtml;
}


//绑定Enter登录键
document.addEventListener("keyup", function(event) {
    event.preventDefault();
    if (event.key == "Enter") {
        getLoginData();
    }
});

//顾名思义 修改cookie值
function setCookie(cname,cvalue,exdays)
{
    var d = new Date();
    d.setTime(d.getTime()+(exdays*24*60*60*1000));
    var expires = "expires="+d.toUTCString();
    document.cookie = cname + "=" + cvalue + "; " + expires;
}


