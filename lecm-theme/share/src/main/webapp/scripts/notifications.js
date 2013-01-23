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

		loadItemsCount: 6,

		skipItemsCount: 0,

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
				YAHOO.util.Dom.setStyle(this.notificationsCounterId, "display", "block");
			} else {
				YAHOO.util.Dom.setStyle(this.notificationsCounterId, "display", "none");
			}
		},

		createWindow: function(div) {
			var content  = '<div style="visibility: hidden" id="' + this.notificationsWindowId + '" class="yui-panel">';
				content += '    <div id="' + this.notificationsWindowId + '-head" class="hd">' + this.msg("notifications") + '</div>';
				content += '    <div id="' + this.notificationsWindowId + '-body" class="bd">';
				content += '        <div id="' + this.notificationsWindowId + '-content"></div>';
				content += '        <div id="' + this.notificationsWindowId + '-next"></div>';
				content += '        <div class="bdft">';
				content += '            <span id="' + this.notificationsWindowId + '-cancel" class="yui-button yui-push-button">';
				content += '                <span class="first-child">';
				content += '                    <button type="button" tabindex="0">' + this.msg("notifications.button.close") + '</button>';
				content += '                </span>';
				content += '            </span>';
				content += '        </div>';
				content += '    </div>';
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

			var moreContainer = YAHOO.util.Dom.get(this.notificationsWindowId + "-next");
			moreContainer.innerHTML = '<a id="' + this.notificationsWindowId + '-next-link" href="javascript:void(0);">' + this.msg("notifications.link.more") + '</a>';
			YAHOO.util.Event.on(this.notificationsWindowId + "-next-link", "click", this.showMoreNotifications, null, this);
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

		showMoreNotifications: function() {
			this.loadNotifications();
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

		setReadNotifications: function(p_items) {
			var items = YAHOO.lang.isArray(p_items) ? p_items : [p_items];
			var nodeRefs = [];
			for (var i = 0; i < items.length; ++i) {
				nodeRefs.push ({"nodeRef": items[i].nodeRef});
			}
			Alfresco.util.Ajax.request ({
				method: "POST",
				url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/notifications/active-channel/api/set/read",
				dataObj: nodeRefs,
				requestContentType: "application/json",
				successCallback: {
					fn: function (response) {
						this.loadNewNotificationsCount();
					},
					scope: this
				},
				failureMessage: "не удалось установить прочитанные уведомления"
			});
		},

		loadNotifications: function() {
			var me = this;
			var sUrl = Alfresco.constants.PROXY_URI + "/lecm/notifications/active-channel/api/records?skipItemsCount=" +
				this.skipItemsCount + "&loadItemsCount=" + this.loadItemsCount;

			var callback = {
				success:function (oResponse) {
					var oResults = eval("(" + oResponse.responseText + ")");
					if (oResults && oResults.items) {
						var items = oResults.items;
						me.skipItemsCount += items.length;
						var str = "";
						var readNotifications = [];
						for (var i = 0; i < items.length; i++) {
							str += '<div class="notification-row">';
							if (items[i].readDate == null) {
								str += '<b>';
								readNotifications.push(items[i]);
							}
							str += items[i].formingDate + ": " + items[i].description;
							if (items[i].readDate == null) {
								str += '</b>';
							}
							str += '</div>';
						}

						if (oResults.hasNext == "true") {
							YAHOO.util.Dom.setStyle(me.notificationsWindowId + "-next-link", "display", "block");
						} else {
							YAHOO.util.Dom.setStyle(me.notificationsWindowId + "-next-link", "display", "none");
						}

						var container = YAHOO.util.Dom.get(me.notificationsWindowId + "-content");
						container.innerHTML += str;

						if (readNotifications.length > 0) {
							me.setReadNotifications(readNotifications);
						}
						me.showNotificationsWindow();
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
			this.loadNewNotificationsCount();
			setInterval(this.loadNewNotificationsCount.bind(this), this.refreshCountTime);
		},

		initButton : function(){
			this.widgets.notificationsButton = new YAHOO.widget.Button(this.id, {
				onclick: {
					fn: this.initNotifications,
					scope: this
				}
			});
		},

		initNotifications : function(){
			var container = YAHOO.util.Dom.get(this.notificationsWindowId + "-content");
			container.innerHTML = "";
			this.skipItemsCount = 0;
			this.loadNotifications();
		}
	});
})();


