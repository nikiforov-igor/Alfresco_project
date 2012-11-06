/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};


/**
 * LogicECM Header module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Header
 */
LogicECM.module.Header = LogicECM.module.Header || {};

/**
 * Header module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.Header.JsButton
 */
(function () {

    LogicECM.module.Header.JsButton = function (htmlId) {
        return LogicECM.module.Header.JsButton.superclass.constructor.call(
            this,
            "LogicECM.module.Header.JsButton",
            htmlId,
            ["button", "menu", "container", "connection", "json", "selector"]);
    };

    YAHOO.lang.extend(LogicECM.module.Header.JsButton, Alfresco.component.Base, {

        context:null,

        location: {href:"#"},

        onReady: function () {
            context = this;

            Alfresco.logger.info ("A new LogicECM.module.Header.JsButton has been created");



            context.createNotifyer();
            context.startFakeTimer();

            context.initMenu();



        },

        createNotifyer: function(){
            var btn = YAHOO.util.Dom.get(this.id);
            var div=document.createElement("div");
            div.innerHTML='<div id="myElem">1<div/>';
            btn.appendChild(div);
        },

        startFakeTimer : function(){
            var counter = 0;

            function increaseTimer(){
                var elem = YAHOO.util.Dom.get("myElem");
                counter++;
                if (counter > 25){
                    counter =0;

                }
                elem.innerHTML = counter;
            }

            setInterval(increaseTimer, 1500);


        },

        go : function(){

            var container = YAHOO.util.Dom.get('myPassword');
            alert(container.value);

        },

        initMenu : function(){

            // Inject the template from the XHR request into a new DIV element
            var containerDiv = document.createElement("div");
            containerDiv.innerHTML =
                '<div id="jsButton-menu" class="yuimenu menu-with-icons yui-overlay yui-overlay-hidden">' +
                    '   <div class="bd"> '+
                    '       <ul> ' +
                    '           <li> '+
                    '               <a href="#" onclick=\'alert("hello"); return false;\'>Hello!</a>' +
                    '           </li>' +
                    '       </ul>' +
                    '       <div> <input id="myPassword" type="password"/></div>' +
                    '       <div> <div  id="' + context.id +'-goBtn"></div></div>' +
                    '   </div>'+
                    '</div>';
            document.body.insertBefore(containerDiv, document.body.firstChild);

            this.widgets.myButton = new YAHOO.widget.Button(this.id,
                {
                    type: "menu",
                    //menu: this.id + "-sites-menu",
                    menu: "jsButton-menu",
                    lazyloadmenu: false
                });

            var container = YAHOO.util.Dom.get(context.id);
            Alfresco.util.createYUIButton(container, "goBtn", context.go, {label: "создать доверенность"});

        }
    });
})();
