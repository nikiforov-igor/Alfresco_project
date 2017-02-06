
// Разговоры со спамом
var spamCall = function(callback)
{
    callback(true);
};

// Авторизация
if (window.Alfresco && window.Alfresco.constants && window.Alfresco.constants.USERNAME ){
    window.iJabPass = Alfresco.constants.USERNAME;
}
else{
    window.iJabPass = 'katamanov';// debug fallback
}

// Скрытие чата
var toggleChat = function()
{
    if (window.iJab)
    {
        window.iJab.toggleIsVisible();
    }
};


// Обновление счетчика непрочитанных сообщений
var updateMessagesCount = function (c)
{
    YAHOO.Bubbling.fire("ru.it.lecm.im.update-messages-count",
        {
            count: c
        });
};

var updateMessagesCountSubscriber = function(callback){
    YAHOO.Bubbling.on("ru.it.lecm.im.update-messages-count", function(layer, args) {
        console.log("Before updateMessagesCount");
        callback(args[1].count);
        console.log(callback);
    });
};

// Разренение логгирования
window.iJabLoggerEnabled = false;

// Конфигурация мессенджера
var iJabConf =
{
    client_type:"xmpp",
    theme:"standard",
    debug:false,
    //avatar_url:"http://dummyimage.com/32x32/?text={username}",
    avatar_url: Alfresco.constants.PROXY_URI_RELATIVE + "slingshot/profile/avatar/{username}",
    enable_roster_manage:false,
    enable_talkto_stranger:true,
    expand_bar_default:true,
    enable_login_dialog:false,
    hide_online_group:false,
    hide_poweredby:true,
    disable_option_setting:true,
    disable_msg_browser_prompt:false,
    enable_talkto_spam:true,
    talkto_spam_function:spamCall,
    talkto_spam_repeat:2,
    xmpp:{
        domain:"localhost",
        http_bind: window.Alfresco ? Alfresco.constants.PROXY_URI_RELATIVE + "/http-bind" : "http://127.0.0.1:7070/http-bind/",
        //http_bind:"http://localhost:7070/http-bind/",
        //http_bind:"http://127.0.0.1:7070/http-bind/",
        host:"",
        port:5222,
        server_type:"ejabberd",
        auto_login:true,
        none_roster:false,
        get_roster_delay:false,
        username_cookie_field:"JID", // не используется
        token_cookie_field:"JIDPWD",
        anonymous_prefix:"",
        max_reconnect:3,
        enable_muc:false,
        muc_servernode:"conference.localhost",
        vcard_search_servernode:"localhost",
        gateways:
        [
//        	{
//        		icon:"http://example.com/msn.png",
//        		name:"MSN Transport",
//        		description:"",
//        		servernode:"msn-transport.anzsoft.com"
//        	}
        ]       
    },
    disable_toolbox:true,
    tools:
    [
    	// {
     //        href:"http://www.google.com",
    	// 	target:"_blank",
    	// 	img:"http://www.google.cn/favicon.ico",
    	// 	text:"Google Search"
    	// },
    	// {
    	// 	href:"http://www.xing.com/",
    	// 	target:"_blank",
    	// 	img:"http://www.xing.com/favicon.ico",
    	// 	text:"Xing"
    	// }
    ],
    shortcuts:
    [
    	// {
    	// 	href:"http://www.anzsoft.com/",
    	// 	target:"_blank",
    	// 	img:"http://www.anzsoft.com/favicon.ico",
    	// 	text:"Go to anzsoft"
    	// },
    	// {
    	// 	href:"http://www.google.com",
    	// 	target:"_blank",
    	// 	img:"http://www.google.cn/favicon.ico",
    	// 	text:"Google Search"
    	// }
    ],
    ijabcometd:{
    }
};