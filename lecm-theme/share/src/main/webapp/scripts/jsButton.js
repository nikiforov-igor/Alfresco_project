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

        // обманка для history.js
        location: {href:"#"},

        // функция вызываемая при окончании инициализации базового модуля
        onReady: function () {

            Alfresco.logger.info ("A new LogicECM.module.Header.JsButton has been created");

            this.createNotifyer();
            this.startFakeTimer();

            this.initMenu();
        },

        /// Добавление счетчика обманки
        createNotifyer: function(){
            var btn = YAHOO.util.Dom.get(this.id);
            var div=document.createElement("div");
            div.innerHTML='<div id="myElem">1</div>';
            btn.appendChild(div);
        },

        /// Инициализатор счетчика-обманки
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

        /// Показывает во всплывающем окне значение из поля ввода
        go : function(){
            var container = YAHOO.util.Dom.get('myPassword');
            alert(container.value);
        },

        // Создание выпадающего меню
        initMenu : function(){

            // Содержимое меню можно запрашивать с сервера
//            var containerDiv = document.createElement("div");
//            containerDiv.innerHTML =
//                '<div id="jsButton-menu" class="yuimenu menu-with-icons yui-overlay yui-overlay-hidden">' +
//                    '   <div class="bd"> '+
//                    '       <ul> ' +
//                    '           <li> '+
//                    '               <a href="#" onclick=\'alert("hello"); return false;\'>Hello!</a>' +
//                    '           </li>' +
//                    '       </ul>' +
//                    '       <div> <input id="myPassword" type="password"/></div>' +
//                    '       <div> <div  id="' + this.id +'-goBtn"></div></div>' +
//                    '   </div>'+
//                    '</div>';
//            document.body.insertBefore(containerDiv, document.body.firstChild);

            /*
            this.widgets.myButton = new YAHOO.widget.Button(this.id,
                {
                    type: "menu",
                    //////menu: this.id + "-sites-menu",
                    menu: "jsButton-menu",
                    lazyloadmenu: false
                });

            var container = YAHOO.util.Dom.get(this.id);

            // Обработчик кнопки в меню
            Alfresco.util.createYUIButton(container, "goBtn", this.go, {label: "Показать введённое значение"});
              */

            // var toggleChat = function(e)
            // {
            //     YAHOO.Bubbling.fire("ru.it.lecm.im.toggle-chat",
            //         {
            //             message: "Hello World."
            //         });

            // };

            this.widgets.myButton = new YAHOO.widget.Button(this.id, {
//                onclick: { fn: toggleChat }
            });

            //this.widgets.myButton.on("click", toggleChat);
        }
    });
})();


