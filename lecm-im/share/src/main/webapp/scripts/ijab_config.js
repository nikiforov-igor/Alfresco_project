var spamCall = function(callback)
{
    callback(true);
};

function setCookie(c_name,value,exdays)
{
    var exdate=new Date();
    exdate.setDate(exdate.getDate() + exdays);
    var c_value=escape(value) + ((exdays==null) ? "" : "; expires="+exdate.toUTCString());
    document.cookie=c_name + "=" + c_value;
}

setCookie('JID', 'q', new Date() + 1);
setCookie('JIDPWD', 'q', new Date() + 1);

var iBubblingSubscriber = function(callback)
{
    YAHOO.Bubbling.on("ru.it.lecm.im.toggle-chat", function(layer, args) {
        console.log("Before CallBack");
        callback(true);
        console.log(callback);
    });
};

var updateMessagesCount = function (c)
{
    YAHOO.Bubbling.fire("ru.it.lecm.im.update-messages-count",
        {
            count: c
        });
}

var updateMessagesCountSubscriber = function(callback){
    YAHOO.Bubbling.on("ru.it.lecm.im.update-messages-count", function(layer, args) {
        console.log("Before updateMessagesCount");
        callback(args[1].count);
        console.log(callback);
    });
};

var iJabConf =
{
    client_type:"xmpp",
    app_type:"bar",
    theme:"standard",
    debug:false,
    avatar_url:"http://samespace.anzsoft.com/portal_memberdata/portraits/{username}",
    enable_roster_manage:true,
    enable_talkto_stranger:true,
    expand_bar_default:true,
    enable_login_dialog:true,
    hide_online_group:false,
    hide_poweredby:true,
    disable_option_setting:true,
    disable_msg_browser_prompt:false,
    enable_talkto_spam:true,
    talkto_spam_function:spamCall,
    talkto_spam_repeat:2,
    xmpp:{
        domain:"akatamanov-pc",
        //http_bind:"http://akatamanov-pc:8080/palladium/",
        http_bind:"/http-bind/",
        host:"",
        //domain:"localhost",
        //http_bind:"http://127.0.0.1:7070/http-bind/",
        //domain:"ijab.im",
        //http_bind:"http://ijab.im:5280/http-bind/",
        port:5222,
        server_type:"ejabberd",
        auto_login:true,
        none_roster:false,
        get_roster_delay:true,
        username_cookie_field:"JID",
        token_cookie_field:"JIDPWD",
        anonymous_prefix:"",
        max_reconnect:3,
        enable_muc:true,
        muc_servernode:"conference.akatamanov-pc",
        vcard_search_servernode:"vjud.anzsoft.com",
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