/* global Alfresco */

/**
 * @module logic_ecm/notifications/NotificationsPopupItem
 * @extends module:alfresco/header/AlfMenuItem
 * @param declare
 * @param lang
 * @param domAttr
 * @param domClass
 * @param domConstruct
 * @param html
 * @param on
 * @param xhr
 * @param json
 * @param template
 * @param AlfMenuItem
 * @param event
 * @param array
 * @param touch
 * @author LogicECM
 */
define(['dojo/_base/declare',
	'dojo/_base/lang',
	'dojo/dom-attr',
	'dojo/dom-class',
	'dojo/dom-construct',
	'dojo/html',
	'dojo/on',
	'dojo/request/xhr',
	'dojo/json',
	'dojo/text!./templates/NotificationsPopupItem.html',
	'alfresco/header/AlfMenuItem',
	'dojo/_base/event',
	'dojo/_base/array',
	'dojo/touch'
],
	function (declare, lang, domAttr, domClass, domConstruct, html, on, xhr, json, template, AlfMenuItem, event, array, touch) {

		return declare([AlfMenuItem], {

			templateString: template,

			cssRequirements: [{cssFile: './css/AlfMenuItem.css'}],

			_toggleRead: function (isRead) {
				var idx;
				if (isRead) {
					this.params.notificationsPopup.readNotifications.push(this.params.item.nodeRef);
					domAttr.set(this.notificationNode, 'title', this.message('message.notification.item.read.title'));
					domClass.replace(this.notificationNode, 'read', 'unread');
				} else {
					idx = this.params.notificationsPopup.readNotifications.indexOf(this.params.item.nodeRef);
					if (idx > -1) {
						this.params.notificationsPopup.readNotifications.splice(idx, 1);
					}
					domAttr.set(this.notificationNode, 'title', this.message('message.notification.item.unread.title'));
					domClass.replace(this.notificationNode, 'unread', 'read');
				}
			},

			_markAsRead: function() {
				xhr.post(Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/notifications/active-channel/api/toggle/read', {
					handleAs: 'json',
					headers: {'Content-Type': 'application/json'},
					data: json.stringify([{
						nodeRef: this.params.item.nodeRef
					}])
				}).then(lang.hitch(this, function(success) {
					this._toggleRead(success.resp[this.params.item.nodeRef] === 'true');
					this.params.notificationsPopup.loadNewNotificationsCount();
				}), lang.hitch(this, function(failure) {
					Alfresco.util.PopupManager.displayMessage({
						text: this.message('message.notification.item.set.read.failure')
					});
				}));
			},

			postCreate: function () {
				this.inherited(arguments);
				html.set(this.notificationDetailsNode, this.params.item.description);
				domConstruct.place(Alfresco.util.relativeTime(new Date(this.params.item.formingDate)), this.notificationNode, 'last');
				this._toggleRead(this.params.item.isRead === 'true');
				on(this.domNode, 'a:click', lang.hitch(this, this._onHrefClick));

				domAttr.set(this._enableNotifications, 'title', this.message("message.notifications.subscribe.title"));
				on(this._enableNotifications, 'i:click', lang.hitch(this, this._onEnableClick));

				domAttr.set(this._disableNotifications, 'title', this.message("message.notifications.unsubscribe.title"));
				on(this._disableNotifications, 'i:click', lang.hitch(this, this._onDisableClick));

				this._changeButtonStatus(this, this.params.item.isEnabled);/*default state*/
				domClass.add(this.notificationActions, "hidden");

				on(this.domNode, touch.enter, function (evt) {
					domClass.remove(this.notificationActions, "hidden");
				}.bind(this));
				on(this.domNode, touch.leave, function (evt) {
					domClass.add(this.notificationActions, "hidden");
				}.bind(this));
			},

			_onEnableClick: function(evt) {
				event.stop(evt);
				this._changeButtonStatus(this, true);
				xhr.post(Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/notifications/template/subscribe', {
					handleAs: 'json',
					headers: {'Content-Type': 'application/json'},
					data: json.stringify({
						template: this.params.item.template
					})
				}).then(lang.hitch(this, function(success) {
					this.updateOnSuccess(true);
				}), lang.hitch(this, function(failure) {
					Alfresco.util.PopupManager.displayMessage({
						text: this.message('message.notifications.subscribe.failure', this.params.item.template)
					});
				}));

				return false;
			},

			_onDisableClick: function(evt) {
				event.stop(evt);
				this._changeButtonStatus(this, false);
				xhr.post(Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/notifications/template/unsubscribe', {
					handleAs: 'json',
					headers: {'Content-Type': 'application/json'},
					data: json.stringify({
						template: this.params.item.template
					})
				}).then(lang.hitch(this, function(success) {
					this.updateOnSuccess(false);
				}), lang.hitch(this, function(failure) {
					Alfresco.util.PopupManager.displayMessage({
						text: this.message('message.notifications.unsubscribe.failure', this.params.item.template)
					});
				}));

				return false;
			},

			updateOnSuccess: function (setEnabled) {
				this.params.item.isEnabled = setEnabled;
				array.forEach(this.params.notificationsPopup.rootWidget.getChildren(), function (notificationItem) {
					if (notificationItem.params.item.template == this.params.item.template) {
						this.params.item.isEnabled = setEnabled;
						this._changeButtonStatus(notificationItem, setEnabled);
					}
				}, this);
			},

			_changeButtonStatus: function (item, isNotificationEnable) {
				domClass.toggle(item._enableNotifications, "hidden", isNotificationEnable);
				domClass.toggle(item._disableNotifications, "hidden", !isNotificationEnable);
			},

			//TODO: ctrl+click handler, open in new tab handler also should mark notification as read!
			_onHrefClick: function(evt) {
				var href = evt.target.href;
				var pathname = evt.target.pathname;
				var pageUrl = href.slice(href.indexOf(pathname), href.length);
				var url = pageUrl.slice(Alfresco.constants.URL_PAGECONTEXT.length, pageUrl.length);
				evt.preventDefault();
				this.alfPublish('ALF_NAVIGATE_TO_PAGE', {
					url: url,
					type: this.targetUrlType,
					target: this.targetUrlLocation
				});
			},

			_onClick: function (evt) {
				this._setSelected(false);
				this._markAsRead();
			}
		});
	}
);
