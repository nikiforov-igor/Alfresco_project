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
 * @class LogicECM.module.LecmIM.Messenger
 */
(function () {
    var Dom = YAHOO.util.Dom;

    LogicECM.module.LecmIM.Messenger = function (htmlId) {
        return LogicECM.module.LecmIM.Messenger.superclass.constructor.call(
            this,
            "LogicECM.module.LecmIM.Messenger",
            htmlId,
            ["button", "menu", "container", "connection", "json", "selector"]);
    };

    YAHOO.lang.extend(LogicECM.module.LecmIM.Messenger, Alfresco.component.Base, {

        // обманка для history.js
        location: {href:"#"},

        // функция вызываемая при окончании инициализации базового модуля
        onReady: function () {

            Alfresco.logger.info ("A new LogicECM.module.LecmIM.Messenger has been created");

            this.createNotifyer();
            this.subscribeToNewMessages();

            this.initButton();
        },

        /// Добавление счетчика
        createNotifyer: function(){
            var btn = Dom.get(this.id);
            var div = document.createElement("div");
            div.innerHTML = '<div id="myElem" class="hidden headerCounter">0</div>';
            btn.appendChild(div);
            Dom.setStyle(btn, 'position', 'relative'); //чтобы спозиционировать счетчик относительно пункта меню "Уведомления"
        },

        /// Инициализатор счетчика в заголовке
        subscribeToNewMessages: function() {
            YAHOO.Bubbling.on("ru.it.lecm.im.update-messages-count", function(layer, args) {
                var count = args[1].count;
                var elem = Dom.get("myElem");

                elem.innerHTML = count;
                if (count > 0) {
                    Dom.removeClass(elem, "hidden");
                } else {
                    Dom.addClass(elem, "hidden");
                }
            });
        },

        // Создание обработчика нажатия на кнопку
        initButton : function(){
            this.widgets.myButton = new YAHOO.widget.Button(this.id, {
                onclick: {
                    fn: function() {
//                        var elem = Dom.get("ijab");
//                        if (Dom.hasClass(elem, "hidden")) {
//                            Dom.removeClass(elem, "hidden");
//                        } else {
//                            Dom.addClass(elem, "hidden");
//                        }

                        if (window.iJab) {
                            window.iJab.toggleIsVisible();
                        } else {
                            alert("Messanger not found!");
                        }
                    }
                }
            });


            /*
             this.widgets.myButton = new YAHOO.widget.Button(this.id, {
             onclick: { fn: toggleChat }
             });
             */
        }
    });
})();


