﻿////////////////////////////////////////////键盘事件////////////////////////////////

// 按Enter键发送信息
$(document).keydown(function(event){
    if(event.keyCode == 13){
        $('#submit2').text('语音输入');
        SendMsg();
        recognizing = false;
        recognition.stop();
        final_transcript = '';
        interim_transcript = '';
        $('#text').val('');
    }
});


// 发送信息
function SendMsg()
{
    var audios = document.getElementsByTagName("audio");
    console.log(audios);
    for(var i=0; i<audios.length;i++){
        audios[i].pause();
        audios[i].remove();
    }
    var text = document.getElementById("text");
    if (text.value == "" || text.value == null)
    {
        alert("发送信息为空，请输入！")
    }
    else
    {
        AddMsg('default', SendMsgDispose(text.value));
        var retMsg = AjaxSendMsg(text.value)
        AddMsg('小龙', retMsg);
        tts(retMsg);
        text.value = "";
    }
}
// 发送的信息处理
function SendMsgDispose(detail)
{
    detail = detail.replace("\n", "<br>").replace(" ", "&nbsp;")
    return detail;
}

// 增加信息
function AddMsg(user,content)
{
    var str = CreadMsg(user, content);
    var msgs = document.getElementById("msgs");
    msgs.innerHTML = msgs.innerHTML + str;
}

// 生成内容
function CreadMsg(user, content)
{
    var str = "";
    if(user == 'default')
    {
        str = "<div class=\"msg guest\"><div class=\"msg-right\"><div class=\"msg-host headDefault\"></div><div class=\"msg-ball\" title=\"今天 17:52:06\">" + content +"</div></div></div>"
    }
    else
    {
        str = "<div class=\"msg robot\"><div class=\"msg-left\" worker=\"" + user + "\"><div class=\"msg-host photo\"></div><div class=\"msg-ball\" title=\"今天 17:52:06\">" + content + "</div></div></div>";
    }
    return str;
}



/////////////////////////////////////////////////////////////////////// 后台信息处理 /////////////////////////////////////////////////////////////////////////////////

// 发送
function AjaxSendMsg(question)
{
    console.log(question);
    var retStr = "";
    $.ajax({
        type: "GET",
        async:false,
        url: "/qa/"+question,
        error: function (request) {
            retStr = "你好";
        },
        success: function (data) {
            console.log(data);
            retStr = data;
        }
    });
    return retStr;
}



// 合成按钮
function tts(text) {

    audio = btts({
        tex: text,
        tok: "24.c7d837fcdd65185ef7dceafd63e97ab0.2592000.1561133668.282335-15758186",
        spd: 5,
        pit: 5,
        vol: 15,
        per: 0
    }, {
        volume: 0.3,
        autoDestory: true,
        timeout: 10000,
        hidden: true,
        onInit: function(htmlAudioElement) {

        },
        onSuccess: function(htmlAudioElement) {
            audio = htmlAudioElement;
            audio.play();
        },
        onError: function(text) {
            alert(text)
        },
        onTimeout: function() {
            alert('timeout')
        }
    });
}

var hasstarted = false
function robotstart() {
    if(hasstarted == false){
        tts("我是Puzzle医疗智能问答机器人，很高兴为您服务");
        hasstarted = true;
    }
}

window.onload = function () {
    hasstarted = false;
    robotstart();
}


// window.onload = function () {
//     tts("我是Puzzle医疗智能问答机器人，很高兴为您服务");
// }