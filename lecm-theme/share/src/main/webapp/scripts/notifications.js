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
 * @class LogicECM.module.Header.Notifications
 */
(function () {

	LogicECM.module.Header.Notifications = function (htmlId) {
		return LogicECM.module.Header.Notifications.superclass.constructor.call(
			this,
			"LogicECM.module.Header.Notifications",
			htmlId,
			["button", "menu", "container", "connection", "json", "selector"]);
	};

	YAHOO.lang.extend(LogicECM.module.Header.Notifications, Alfresco.component.Base, {

		// обманка для history.js
		location: {href:"#"},

		// функция вызываемая при окончании инициализации базового модуля
		onReady: function () {
			this.createNotifyer();
			this.startLoadNewNotifications();

			this.initButton();
		},

		/// Добавление счетчика
		createNotifyer: function(){
			var btn = YAHOO.util.Dom.get(this.id);
			var div=document.createElement("div");
			div.innerHTML='<div id="notificationsCounter">0</div>';
			btn.appendChild(div);
		},

		loadNewNotifications: function() {
			var sUrl = Alfresco.constants.PROXY_URI + "/lecm/notifications/active-channel/api/new-count";
			var callback = {
				success:function (oResponse) {
					var oResults = eval("(" + oResponse.responseText + ")");
					if (oResults && oResults.newCount) {
						var elem = YAHOO.util.Dom.get("notificationsCounter");
						if (elem != null) {
							elem.innerHTML = oResults.newCount;
						}
					} else {
						YAHOO.log("Failed to process XHR transaction.", "info", "example");
					}
				},
				failure:function (oResponse) {
					YAHOO.log("Failed to process XHR transaction.", "info", "example");
				}
			};
			YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
		},

		/// Инициализатор счетчика-обманки
		startLoadNewNotifications : function(){
			this.loadNewNotifications();
			setInterval(this.loadNewNotifications, 60000);
		},

		loadNotifications : function(e){
			alert("notifications");
		},

		// Создание выпадающего меню
		initButton : function(){
			this.widgets.notificationsButton = new YAHOO.widget.Button(this.id, {
				onclick: { fn: this.loadNotifications }
			});
		}
	});
})();


