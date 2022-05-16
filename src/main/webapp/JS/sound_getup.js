var rec_getup;
/**调用open打开录音请求好录音权限**/
var recOpen_getup=function(success){//一般在显示出录音按钮或相关的录音界面时进行此方法调用，后面用户点击开始录音时就能畅通无阻了
    rec_getup=Recorder({ //本配置参数请参考下面的文档，有详细介绍
        type:"mp3",sampleRate:16000,bitRate:16 //mp3格式，指定采样率hz、比特率kbps，其他参数使用默认配置；注意：是数字的参数必须提供数字，不要用字符串；需要使用的type类型，需提前把格式支持文件加载进来，比如使用wav格式需要提前加载wav.js编码引擎
        ,onProcess:function(buffers,powerLevel,bufferDuration,bufferSampleRate,newBufferIdx,asyncEnd){
            //录音实时回调，大约1秒调用12次本回调
            //可实时绘制波形（extensions目录内的waveview.js、wavesurfer.view.js、frequency.histogram.view.js扩展功能）
            //可利用extensions/sonic.js扩展实时变速变调，此扩展计算量巨大，onProcess需要返回true开启异步模式
            //可实时上传（发送）数据，配合Recorder.SampleData方法，将buffers中的新数据连续的转换成pcm上传，或使用mock方法将新数据连续的转码成其他格式上传，可以参考文档里面的：Demo片段列表 -> 实时转码并上传-通用版；基于本功能可以做到：实时转发数据、实时保存数据、实时语音识别（ASR）等
        }
    });

    //var dialog=createDelayDialog(); //我们可以选择性的弹一个对话框：为了防止移动端浏览器存在第三种情况：用户忽略，并且（或者国产系统UC系）浏览器没有任何回调，此处demo省略了弹窗的代码
    rec_getup.open(function(){//打开麦克风授权获得相关资源
        //dialog&&dialog.Cancel();  //如果开启了弹框，此处需要取消
        //rec_getup.start() 此处可以立即开始录音，但不建议这样编写，因为open是一个延迟漫长的操作，通过两次用户操作来分别调用open和start是推荐的最佳流程
        success&&success();
        console.log("获取权限成功");
        voice_jurisdiction=true;
    },function(msg,isUserNotAllow){//用户拒绝未授权或不支持
        //dialog&&dialog.Cancel();   //如果开启了弹框，此处需要取消
        console.log((isUserNotAllow?"UserNotAllow，":"")+"无法录音:"+msg);
        voice_jurisdiction=false;
    });
};

/**开始录音**/
function recStart_getup(){//打开了录音后才能进行start、stop调用
    if (voice_jurisdiction) rec_getup.start();
};

/**结束录音**/
function recStop_getup(){
    rec_getup.stop(function(blob,duration){
        console.log(blob,(window.URL||webkitURL).createObjectURL(blob),"时长:"+duration+"ms");
        //rec.close();//释放录音资源，当然可以不释放，后面可以连续调用start；但不释放时系统或浏览器会一直提示在录音，最佳操作是录完就close掉
        //rec=null;     不关闭

        //已经拿到blob文件对象想干嘛就干嘛：立即播放、上传
        upload_mp3(type,blob);   //录音到了就上传
    },function(msg){
        console.log("录音失败:"+msg);
        rec_getup.close();//可以通过stop方法的第3个参数来自动调用close
        rec_getup=null;
    });
};


//选择性的弹一个对话框：为了防止移动端浏览器存在第三种情况：用户忽略，并且（或者国产系统UC系）浏览器没有任何回调
function createDelayDialog(){
    //弹框还没写
    recOpen_getup();
    console.log(voice_jurisdiction);
}
