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

		notificationsWindow: null,

		notificationsWindowId: "notificationsWindow",

		notificationsCounterId: "notificationsCounter",

		refreshCountTime: 60000,

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
			div.innerHTML='<div id="' + this.notificationsCounterId + '">0</div>';
			this.createWindow(div);
			btn.appendChild(div);

			this.checkVisibleCounter(0);
		},

		checkVisibleCounter: function(count) {
			if (count > 0) {
				Dom.setStyle(this.notificationsCounterId, "display", "block");
			} else {
				Dom.setStyle(this.notificationsCounterId, "display", "none");
			}
		},

		createWindow: function(div) {
			var content = '<div style="visibility: hidden" id="' + this.notificationsWindowId + '" class="yui-panel">';
			content += '<div id="' + this.notificationsWindowId + '-head" class="hd">' + this.msg("notifications") + '</div>';
			content += '<div id="' + this.notificationsWindowId + '-body" class="bd">';
			content += '<div id="' + this.notificationsWindowId + '-content"></div>';
			content += '<div class="bdft">';
			content += '<span id="' + this.notificationsWindowId + '-cancel" class="yui-button yui-push-button">';
			content += '<span class="first-child">';
			content += '<button type="button" tabindex="0">' + this.msg("notifications.button.close") + '</button>';
			content += '</span>';
			content += '</span>';
			content += '</div>';
			content += '</div>';
			content += '</div>';
			div.innerHTML += content;

			YAHOO.util.Event.onAvailable(this.notificationsWindowId, this.createDialog, this, true);
		},

		createDialog: function() {
			this.notificationsWindow = Alfresco.util.createYUIPanel(this.notificationsWindowId,
				{
					width: "600px"
				});
			this.widgets.notificationsCloseWindowButton = new YAHOO.widget.Button(this.notificationsWindowId + "-cancel", {
				onclick: {
					fn: this.hideNotificationsWindow,
					scope: this
				}
			});
		},

		showNotificationsWindow: function() {
			if (this.notificationsWindow != null) {
				this.notificationsWindow.show();
			}
		},

		hideNotificationsWindow: function() {
			if (this.notificationsWindow != null) {
				this.notificationsWindow.hide();
			}
		},

		loadNewNotificationsCount: function() {
			var me = this;
			var sUrl = Alfresco.constants.PROXY_URI + "/lecm/notifications/active-channel/api/new-count";
			var callback = {
				success:function (oResponse) {
					var oResults = eval("(" + oResponse.responseText + ")");
					if (oResults && oResults.newCount) {
						var elem = YAHOO.util.Dom.get(me.notificationsCounterId);
						if (elem != null) {
							elem.innerHTML = oResults.newCount;
							me.checkVisibleCounter(oResults.newCount);
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

		loadNotifications: function() {
			var me = this;
			var sUrl = Alfresco.constants.PROXY_URI + "/lecm/notifications/active-channel/api/records";
			var callback = {
				success:function (oResponse) {
					var content = YAHOO.util.Dom.get(me.notificationsWindowId + "-content");
					content.innerHTML = oResponse.responseText;
					me.showNotificationsWindow();
					me.loadNewNotificationsCount();
				},
				failure:function (oResponse) {
					YAHOO.log("Failed to process XHR transaction.", "info", "example");
				}
			};
			YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
		},

		/// Инициализатор счетчика-обманки
		startLoadNewNotifications : function(){
			this.loadNewNotificationsCount();
			setInterval(this.loadNewNotificationsCount.bind(this), this.refreshCountTime);
		},

		initButton : function(){
			this.widgets.notificationsButton = new YAHOO.widget.Button(this.id, {
				onclick: {
					fn: this.loadNotifications,
					scope: this
				}
			});
		}
	});
})();


