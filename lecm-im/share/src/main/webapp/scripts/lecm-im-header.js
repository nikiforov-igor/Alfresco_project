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
            this.subscribeToNewMessages();

            this.initButton();
        },

        /// Добавление счетчика
        createNotifyer: function(){
            var btn = YAHOO.util.Dom.get(this.id);
            var div=document.createElement("div");
            div.innerHTML='<div id="myElem" hidden>0</div>';
            btn.appendChild(div);
        },

        /// Инициализатор счетчика в заголовке
        subscribeToNewMessages : function(){

            YAHOO.Bubbling.on("ru.it.lecm.im.update-messages-count", function(layer, args) {

                    var count = args[1].count;
                    var elem = YAHOO.util.Dom.get("myElem");
                    elem.innerHTML = count;
                    if (count > 0)
                    {
                        elem.removeAttribute("hidden");

                    }
                    else
                    {
                        elem.setAttribute("hidden","hidden");
                    }

                }
            );

        },



        // Создание обработчика нажатия на кнопку
        initButton : function(){
            this.widgets.myButton = new YAHOO.widget.Button(this.id, {
                onclick: { fn: function(){
                    var elem = YAHOO.util.Dom.get("ijab");
                    if (elem.hasAttribute("hidden"))
                    {
                        elem.removeAttribute("hidden");
                    }
                    else
                    {
                        elem.setAttribute("hidden","hidden");
                    }

                } }
            });


            /*
             this.widgets.myButton = new YAHOO.widget.Button(this.id, {
             onclick: { fn: toggleChat }
             });
             */
        }
    });
})();


