/* global Alfresco */

/**
 * @module logic_ecm/notifications/NotificationsPopup
 * @extends module:alfresco/menus/AlfMenuBarPopup
 * @param declare
 * @param lang
 * @param array
 * @param dom
 * @param domClass
 * @param domStyle
 * @param domConstruct
 * @param on
 * @param xhr
 * @param json
 * @param AlfMenuBarPopup
 * @author LogicECM
 */
define(['dojo/_base/declare',
	'dojo/_base/lang',
	'dojo/_base/array',
	'dojo/dom',
	'dojo/dom-class',
	'dojo/dom-style',
	'dojo/dom-construct',
	'dojo/on',
	'dojo/request/xhr',
	'dojo/json',
	'alfresco/menus/AlfMenuBarPopup'
],
	function (declare, lang, array, dom, domClass, domStyle, domConstruct, on, xhr, json, AlfMenuBarPopup) {

		return declare([AlfMenuBarPopup], {

			notificationsCounterId: 'notificationsCounter',

			refreshCountTime: 60000,

			loadItemsCount: 6,

			skipItemsCount: null,

			readNotifications: null,

			rootWidget: null,

			cssRequirements: [{cssFile: './css/counter-styles.css'}],

			showArrow: false,

			widgets: [{
				name: 'logic_ecm/notifications/NotificationsGroup',
				config: {
					i18nScope: 'notificationsPopup',
					widgets: [{
						i18nScope: 'notificationsPopup',
						name: 'alfresco/header/AlfMenuItem',
						config: {
							i18nScope: 'notificationsPopup',
							label: 'message.notifications.none'
						}
					}]
				}
			}],

			i18nScope: 'notificationsPopup',

			i18nRequirements: [{i18nFile: './properties/NotificationsPopup.properties'}],

			checkVisibleCounter: function (count) {
				domStyle.set(this.notificationsCounterId, 'display', count > 0 ? 'inline-block' : 'none');
				domClass.toggle(this.notificationsCounterId, 'blink', count > 0);
			},

			loadNewNotificationsCount: function () {
				xhr.get(Alfresco.constants.PROXY_URI + 'lecm/notifications/active-channel/api/new-count', {
					handleAs: 'json'
				}).then(lang.hitch(this, function(success) {
					var oResults = success;
					if (oResults && oResults.newCount) {
						var elem = dom.byId(this.notificationsCounterId);
						if (elem) {
							elem.innerHTML = (oResults.newCount > 99) ? '∞' : oResults.newCount;
							this.checkVisibleCounter(oResults.newCount);
						}
					}
				}), lang.hitch(this, function(failure) {
					var status = failure.response.status,
						msg = failure.message,
						exception = failure.response.data ? failure.response.data.exception : null;

					console.warn('Status: %s. Message: %s.', status, msg);
					if (500 === status) {
						console.warn('Server exception: %s', exception);
					}
				}));
			},

			startLoadNewNotifications: function () {
				this.loadNewNotificationsCount();
				setInterval(lang.hitch(this, this.loadNewNotificationsCount), this.refreshCountTime);
			},

			postCreate: function () {
				var widgets;
				this.inherited(arguments);
				if (this.popup && this.popup.domNode) {
					// This ensures that we can differentiate between header menu popups and regular menu popups with our CSS selectors
					domClass.add(this.popup.domNode, 'alf-header-menu-bar');
					domClass.add(this.popup.domNode, 'main-part');
					domConstruct.place('<span class="counter" id="notificationsCounter"></span>', this.domNode);
					this.popup.onOpen = lang.hitch(this, 'initNotifications');
					on(this.popup.domNode, 'scroll', lang.hitch(this, this.onContainerScroll));
					widgets = this.popup.getChildren();
					if (widgets && widgets.length) {
						this.rootWidget = widgets[0];
						this.rootWidget.params.notificationsPopup = this;
					}
				}
				this.startLoadNewNotifications();
			},

			onContainerScroll: function (event) {
				var container = event.currentTarget;
				if (container.scrollTop + container.clientHeight == container.scrollHeight) {
					this.loadNotifications();
				}
			},

			loadNotifications: function (initialLoad) {
				xhr.post(Alfresco.constants.PROXY_URI + 'lecm/notifications/active-channel/api/records', {
					handleAs: 'json',
					headers: {'Content-Type': 'application/json'},
					data: json.stringify({
						skipItemsCount: this.skipItemsCount,
						loadItemsCount: this.loadItemsCount,
						ignoreNotifications: this.readNotifications
					})
				}).then(lang.hitch(this, function(success){
					var items = success.items,
						notificationsConfig;

					this.skipItemsCount += items.length;
					lang.isFunction(this.rootWidget.toggle) && this.rootWidget.toggle(this.skipItemsCount === 0);
					if (items.length) {
						if (initialLoad) {
							array.forEach(this.rootWidget.getChildren(), function (itemWidget) {
								this.removeChild(itemWidget);
							}, this.rootWidget);
						}
						notificationsConfig = array.map(items, function (item) {
							return {
								name: 'logic_ecm/notifications/NotificationsPopupItem',
								config: {
									i18nScope: 'notificationsPopup',
									item: item,
									notificationsPopup: this
								}
							};
						}, this);
						this.rootWidget.processWidgets(notificationsConfig, this.rootWidget.domNode);
					}
				}), lang.hitch(this, function(failure) {
					Alfresco.util.PopupManager.displayMessage({
						text: this.message('message.notifications.load.failure')
					});
				}));
			},

			initNotifications: function () {
				this.skipItemsCount = 0;
				this.readNotifications = [];
				this.loadNotifications(true);
			}
		});
	}
);
