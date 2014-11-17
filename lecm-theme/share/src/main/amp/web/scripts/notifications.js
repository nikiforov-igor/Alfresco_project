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
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Connect = YAHOO.util.Connect;

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

        buttonDomElement : null,
        originalBackgroundImage: null,

        highLighterIntervalID : null,

        readNotifications: null,

		// функция вызываемая при окончании инициализации базового модуля
		onReady: function () {
			this.createNotifyer();
			this.startLoadNewNotifications();

			this.initButton();
            this.initHighLighter();

            //костыль для инициализации форм, пока живет здесь
            Alfresco.util.Ajax.request(
                {
                    url:Alfresco.constants.URL_SERVICECONTEXT + "lecm/config/init?reset=false",
                    dataObj:{},
                    successCallback:{
                        fn:function (response) {
                        }
                    },
                    failureMessage:"message.failure",
                    execScripts:true
                });
		},

        initHighLighter : function () {
            var msgCounter = Dom.get(this.notificationsCounterId);
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
        },

		// Добавление счетчика
		createNotifyer: function(){
			var btn = Dom.get(this.id);
			var div = document.createElement("div");
			div.innerHTML = '<div id="' + this.notificationsCounterId + '" class="headerCounter">0</div>';
			this.createWindow(div);
			btn.appendChild(div);
            Dom.setStyle(btn, 'position', 'relative'); //чтобы спозиционировать счетчик относительно пункта меню "Уведомления"

			this.checkVisibleCounter(0);
		},

		checkVisibleCounter: function(count) {
            Dom.setStyle(this.notificationsCounterId, "display", count > 0 ? "block" : "none");
            if (count > 0){
                this.startHighlighting(this);
            }else{
                this.stopHighlighting(this);
            }
		},

		createWindow: function(div) {
			var content  = '<div style="visibility: hidden" id="' + this.notificationsWindowId + '" class="yui-panel">';
				content += '    <div id="' + this.notificationsWindowId + '-head" class="hd">' + this.msg("notifications") + '</div>';
				content += '    <div id="' + this.notificationsWindowId + '-body" class="bd">';
				content += '        <div id="' + this.notificationsWindowId + '-main-part" class="main-part">';
				content += '            <div id="' + this.notificationsWindowId + '-content"></div>';
				content += '            <div id="' + this.notificationsWindowId + '-loading">';
				content += '                <img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/lightbox/loading.gif">';
				content += '            </div>';
				content += '        </div>';
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

			Event.onAvailable(this.notificationsWindowId, this.createDialog, this, true);
		},

		createDialog: function() {
			this.notificationsWindow = Alfresco.util.createYUIPanel(this.notificationsWindowId, {
					width: "50em"
				});

			this.widgets.notificationsCloseWindowButton = new YAHOO.widget.Button(this.notificationsWindowId + "-cancel", {
				onclick: {
					fn: this.hideNotificationsWindow,
					scope: this
				}
			});

			YAHOO.util.Event.addListener(this.notificationsWindowId + "-main-part", "scroll", this.onContainerScroll, this);
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
			var sUrl = Alfresco.constants.PROXY_URI + "lecm/notifications/active-channel/api/new-count";
			var callback = {
				success:function (oResponse) {
					if (oResponse.responseText != null && oResponse.responseText.length > 0) {
						var oResults = eval("(" + oResponse.responseText + ")");
						if (oResults && oResults.newCount) {
							var elem = Dom.get(me.notificationsCounterId);
							if (elem != null) {
								elem.innerHTML = (oResults.newCount > 99) ? "∞" : oResults.newCount;
								me.checkVisibleCounter(oResults.newCount);
							}
						} else {
							YAHOO.log("Failed to process XHR transaction.", "info", "example");
						}
					} else {
						YAHOO.log("Failed to process XHR transaction.", "info", "example");
					}
				},
				failure:function (oResponse) {
					YAHOO.log("Failed to process XHR transaction.", "info", "example");
				}
			};
			Connect.asyncRequest('GET', sUrl, callback);
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
			Alfresco.util.Ajax.jsonPost(
				{
					url:Alfresco.constants.PROXY_URI + "lecm/notifications/active-channel/api/records",
					dataObj: {
						skipItemsCount: me.skipItemsCount,
						loadItemsCount: me.loadItemsCount,
						ignoreNotifications: me.readNotifications
					},
					successCallback:{
						fn:function DataGrid_onDataItemCreated_refreshSuccess(response) {
							var items = response.json.items;
							var readNewNotifications = [];
							var container = Dom.get(me.notificationsWindowId + "-content");

							for (var i = 0; i < items.length; i++) {
								var item = items[i];
								me.readNotifications.push(item.nodeRef);

								var div = document.createElement('div');
								var detail = document.createElement('span');

								div.setAttribute('class', 'notification-row');
								if (item.isRead == "false") {
									readNewNotifications.push(item);
									Dom.addClass(div, 'bold');
								}

								detail.innerHTML = item.description;
								detail.setAttribute('class', 'detail');
								div.appendChild(detail);
								div.innerHTML += '<br />' + Alfresco.util.relativeTime(new Date(item.formingDate));
								container.appendChild(div);
							}

							if (readNewNotifications.length > 0) {
								me.setReadNotifications(readNewNotifications);
							}
							Dom.setStyle(this.notificationsWindowId + "-loading", "visibility", "hidden");
							me.showNotificationsWindow();
						},
						scope:this
					},
					failureCallback:{
						fn:function (response) {
							Alfresco.util.PopupManager.displayMessage(
								{
									text:this.msg("message.notifications.load.failure")
								});
						},
						scope:this
					}
				});
		},

		// Инициализатор счетчика-обманки
		startLoadNewNotifications : function() {
			this.loadNewNotificationsCount();
            var self = this;

			setInterval(function() { // bind() не работает в IE
                self.loadNewNotificationsCount();
            }, this.refreshCountTime);
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
			var container = Dom.get(this.notificationsWindowId + "-content");
			container.innerHTML = "";
			this.skipItemsCount = 0;
			this.readNotifications = [];
			this.loadNotifications();
		},

		onContainerScroll: function (event, scope) {
			var container = event.currentTarget;
			if (container.scrollTop + container.clientHeight == container.scrollHeight) {
				Dom.setStyle(scope.notificationsWindowId + "-loading", "visibility", "visible");
				scope.loadNotifications();
			}
		}
	});
})();


