/* global Alfresco */

/**
 * @module alfresco/header/AlfMenuBarPopup
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
 * @author logicECM
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

			notificationsCounter: null,

			refreshCountTime: 60000,

			loadItemsCount: 6,

			skipItemsCount: 0,

			rootWidget: null,

			cssRequirements: [{cssFile: './css/counter-styles.css'}],

			showArrow: false,

			widgets: [{
				name: 'alfresco/menus/AlfMenuGroup',
				config: {
					widgets: []
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
						var elem = Dom.get(this.notificationsCounterId);
						if (elem) {
							elem.innerHTML = (oResults.newCount > 99) ? '∞' : oResults.newCount;
							this.checkVisibleCounter(oResults.newCount);
						}
					}
				}), lang.hitch(this, function(failure) {
					Alfresco.util.PopupManager.displayMessage({
						text: this.message('message.new.notifications.count.load.failure')
					});
				}));
			},

			startLoadNewNotifications: function () {
				this.loadNewNotificationsCount();
				setInterval(lang.hitch(this, this.loadNewNotificationsCount), this.refreshCountTime);
			},

			postCreate: function () {
				this.inherited(arguments);
				this.notificationsCounter = dom.byId(this.notificationsCounterId);
				if (this.popup && this.popup.domNode) {
					// This ensures that we can differentiate between header menu popups and regular menu popups with our CSS selectors
					domClass.add(this.popup.domNode, 'alf-header-menu-bar');
					domClass.add(this.popup.domNode, 'main-part');
					domConstruct.place('<span class="counter" id="notificationsCounter"></span>', this.domNode);
					this.popup.onOpen = lang.hitch(this, 'initNotifications');
				}
				this.startLoadNewNotifications();
				on(this.popup.domNode, 'scroll', lang.hitch(this, this.onContainerScroll));
			},

			onContainerScroll: function (event) {
				var container = event.currentTarget;
				if (container.scrollTop + container.clientHeight == container.scrollHeight) {
					this.loadNotifications();
				}
			},

			loadNotifications: function () {
				xhr.post(Alfresco.constants.PROXY_URI + 'lecm/notifications/active-channel/api/records', {
					handleAs: 'json',
					headers: {'Content-Type': 'application/json'},
					data: json.stringify({
						skipItemsCount: this.skipItemsCount,
						loadItemsCount: this.loadItemsCount
					}),
				}).then(lang.hitch(this, function(success){
					var items = success.items;
					this.skipItemsCount += items.length;
					var notificationsConfig = array.map(items, function (item) {
						return {
							name: 'logic_ecm/notifications/NotificationsPopupItem',
							config: {
								item: item,
								notificationsPopup: this
							}
						};
					}, this);
					this.rootWidget.processWidgets(notificationsConfig, this.rootWidget.domNode);
				}), lang.hitch(this, function(failure) {
					Alfresco.util.PopupManager.displayMessage({
						text: this.message('message.notifications.load.failure')
					});
				}));
			},

			initNotifications: function () {
				this.skipItemsCount = 0;
				var widgets = this.popup.getChildren();
				if (widgets && widgets.length) {
					this.rootWidget = widgets[0];
					array.forEach(this.rootWidget.getChildren(), function (itemWidget) {
						this.rootWidget.removeChild(itemWidget);
					}, this);
				}
				this.loadNotifications();
			}
		});
	}
);
