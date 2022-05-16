var drone;
var configuration;
var roomName;
var room;
var pc;
// var localVideo = $(".layout .content .sidebar-group:eq(1) #voide #localVideo");
// var remoteVideo = $(".layout .content .sidebar-group:eq(1) #voide #remoteVideo");
var mediaStreamTrack,mediaStreamTrack_1;

function onSuccess() {}

function getRoomName(){
    const hash = Math.floor(Math.random() * 0xFFFFFF).toString(16);
    return 'observable-' + hash;
}


function start_v(Name){
    roomName=Name;
    drone = new ScaleDrone('2OhZBOFFhZTBTD23');
    configuration = {
        iceServers: [{
            urls: 'stun:stun.l.google.com:19302' // 使用谷歌的stun服务
        }]
    };
    drone.on('open', function(error){
        if (error) { return console.error(error);}

        room = drone.subscribe(roomName);
        room.on('open', function(error){
            if (error) {onError(error);}
        });

        // 已经链接到房间后，就会收到一个 members 数组，代表房间里的成员
        // 这时候信令服务已经就绪
        room.on('members', function(members){
            console.log('MEMBERS', members);

            // 如果你是第二个链接到房间的人，就会创建offer
            var isOfferer = members.length === 2;
            startWebRTC(isOfferer);
        });
    });
}


function sendMessage(message) {
    drone.publish({
        room: roomName,
        message
    });
}

function startWebRTC(isOfferer) {
    pc = new RTCPeerConnection(configuration);

    // 当本地ICE Agent需要通过信号服务器发送信息到其他端时
    // 会触发icecandidate事件回调
    pc.onicecandidate = function(event){
        if (event.candidate) {
            sendMessage({ 'candidate': event.candidate });
        }
    };

    // 如果用户是第二个进入的人，就在negotiationneeded 事件后创建sdp
    if (isOfferer) {
        // onnegotiationneeded 在要求sesssion协商时发生
        pc.onnegotiationneeded = function() {
            // 创建本地sdp描述 SDP (Session Description Protocol) session描述协议
            pc.createOffer().then(localDescCreated).catch(onError);
        };
    }

    // 当远程数据流到达时，将数据流装载到video中
    pc.onaddstream = function(event){
        //$(".layout .content .mobile-open #voide .voide-autoplay #remoteVideo").srcObject = event.stream;
        var a= document.getElementById('remoteVideo');
        a.srcObject = event.stream;
        //remoteVideo.srcObject = event.stream;
    };

    // 获取本地媒体流
    navigator.mediaDevices.getUserMedia({
        audio: true,
        video: true,
    }).then( function(stream) {
        // 将本地捕获的视频流装载到本地video中
        //localVideo.srcObject = stream;
        //$(".layout .content .mobile-open #voide .voide-autoplay #localVideo").srcObject = stream;
        mediaStreamTrack = typeof stream.stop === 'function' ? stream : stream.getTracks()[1];
        mediaStreamTrack_1 = typeof stream.stop === 'function' ? stream : stream.getTracks()[0];
        var b=document.getElementById('localVideo');
        b.srcObject = stream;
        // 将本地流加入RTCPeerConnection 实例中 发送到其他端
        pc.addStream(stream);
    },onError);

    // 从Scaledrone监听信令数据
    room.on('data', function(message, client){
        // 消息是我自己发送的，则不处理
        if (client.id === drone.clientId) {
            return;
        }

        if (message.sdp) {
            // 设置远程sdp, 在offer 或者 answer后
            pc.setRemoteDescription(new RTCSessionDescription(message.sdp), function(){
                // 当收到offer 后就接听
                if (pc.remoteDescription.type === 'offer') {
                    pc.createAnswer().then(localDescCreated).catch(onError);
                }
            }, onError);
        }
        else if (message.candidate) {
            // 增加新的 ICE canidatet 到本地的链接中
            pc.addIceCandidate(
                new RTCIceCandidate(message.candidate), onSuccess, onError
            );
        }
    });
}

function localDescCreated(desc) {
    pc.setLocalDescription(desc, function(){
        sendMessage({ 'sdp': pc.localDescription });
    },onError);
}

function onError(error) {
    console.error(error);
}

function stop_voide(){
    mediaStreamTrack && mediaStreamTrack.stop();
    mediaStreamTrack_1 && mediaStreamTrack_1.stop();
}