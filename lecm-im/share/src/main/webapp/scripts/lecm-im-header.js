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
 * LogicECM LecmIM module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.LecmIM
 */
LogicECM.module.LecmIM = LogicECM.module.LecmIM || {};

/**
 * LecmIM module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.LecmIM.JsButton
 */
(function () {

    LogicECM.module.LecmIM.JsButton = function (htmlId) {
        return LogicECM.module.LecmIM.JsButton.superclass.constructor.call(
            this,
            "LogicECM.module.LecmIM.JsButton",
            htmlId,
            ["button", "menu", "container", "connection", "json", "selector"]);
    };

    YAHOO.lang.extend(LogicECM.module.LecmIM.JsButton, Alfresco.component.Base, {

        // обманка для history.js
        location: {href:"#"},

        // функция вызываемая при окончании инициализации базового модуля
        onReady: function () {

            Alfresco.logger.info ("A new LogicECM.module.LecmIM.JsButton has been created");

            this.createNotifyer();
            this.startFakeTimer();

            this.initButton();
        },

        /// Добавление счетчика
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

        // Создание обработчика нажатия на кнопку
        initMenu : function(){

            this.widgets.myButton = new YAHOO.widget.Button(this.id, {
//                onclick: { fn: toggleChat }
            });
        }
    });
})();


