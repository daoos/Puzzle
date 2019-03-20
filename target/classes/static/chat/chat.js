////////////////////////////////////////////键盘事件////////////////////////////////

// 按Enter键发送信息
$(document).keydown(function(event){
    if(event.keyCode == 13){
        SendMsg();
    }
});


/////////////////////////////////////////////前台信息处理/////////////////////////////////////////////////////////
// 发送信息
function SendMsg()
{
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

// 初始化变量
var audio = null;

// 合成按钮
function tts(text) {

    audio = btts({
        tex: text,
        tok: '24.bc96799e7250d0f2b95cda27f91574fb.2592000.1555146378.282335-15758186',
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

window.onload = function () {
    tts("我是Puzzle医疗智能问答机器人，很高兴为您服务");
}