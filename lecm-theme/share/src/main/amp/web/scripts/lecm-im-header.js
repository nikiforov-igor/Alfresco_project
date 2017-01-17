/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
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

        buttonDomElement : null,
        originalBackgroundImage: null,

        highLighterIntervalID : null,

        // функция вызываемая при окончании инициализации базового модуля
        onReady: function () {

            Alfresco.logger.info ("A new LogicECM.module.LecmIM.Messenger has been created");

            this.createNotifyer();
            this.subscribeToNewMessages();

            this.initButton();
            this.initHighLighter();
        },

        /// Добавление счетчика
        createNotifyer: function (){
            var btn = Dom.get(this.id);
            var div = document.createElement("div");
            div.innerHTML = '<div id="msgCounter" class="hidden headerCounter">0</div>';
            btn.appendChild(div);
            Dom.setStyle(btn, 'position', 'relative'); //чтобы спозиционировать счетчик относительно пункта меню "Уведомления";
        },

        createBublingHandler : function (context){
            return function(layer, args) {
                var count = args[1].count;
                var elem = Dom.get("msgCounter");

                if (count > 0) {
                    Dom.removeClass(elem, "hidden");
                    context.startHighlighting(context);
                    elem.innerHTML = (count>99) ? "∞" : count;
                } else {
                    Dom.addClass(elem, "hidden");
                    context.stopHighlighting(context);
                    elem.innerHTML = "0";
                }
            };
        },

        /// Инициализатор счетчика в заголовке
        subscribeToNewMessages: function () {
            YAHOO.Bubbling.on("ru.it.lecm.im.update-messages-count", this.createBublingHandler(this) );
        },

        // Создание обработчика нажатия на кнопку
        initButton : function (){
            this.widgets.myButton = new YAHOO.widget.Button(this.id, {
                onclick: {
                    fn: function() {
                        if (window.iJab) {
                            window.iJab.toggleIsVisible();
                        } else {
                            alert("Messenger not found!");
                        }
                    }
                }
            });
        },

        initHighLighter : function () {
            var msgCounter = Dom.get('msgCounter');
            this.buttonDomElement = Dom.getPreviousSibling(msgCounter.parentElement);
            this.originalBackgroundImage = this.buttonDomElement.style.backgroundImage;
        },

        stopHighlighting : function (context) {
            if (context.highLighterIntervalID){
                clearInterval(context.highLighterIntervalID);
                context.highLighterIntervalID = null;
                context.buttonDomElement.style.backgroundImage = context.originalBackgroundImage;
            }
        },

        startHighlighting : function (context) {
            if (!context.highLighterIntervalID){
                var handler = context.createIntervalHandler(context);
                context.highLighterIntervalID = setInterval(handler, 750);
                handler();
            }
        },

        createIntervalHandler : function(context){
            return function () {
                context.buttonDomElement.style.backgroundImage =
                    (context.buttonDomElement.style.backgroundImage.indexOf("_light.png") > 0 ) ?
                        context.originalBackgroundImage.replace(".png", "_black.png") :
                        context.originalBackgroundImage.replace(".png", "_light.png") ;
            };
        }
    });
})();


