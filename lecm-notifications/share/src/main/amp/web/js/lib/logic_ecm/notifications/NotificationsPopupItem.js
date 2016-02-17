/* global Alfresco */

/**
 * @module logic_ecm/notifications/NotificationsPopupItem
 * @extends module:alfresco/header/AlfMenuItem
 * @param declare
 * @param lang
 * @param domClass
 * @param domConstruct
 * @param html
 * @param on
 * @param xhr
 * @param json
 * @param template
 * @param AlfMenuItem
 * @author LogicECM
 */
define(['dojo/_base/declare',
	'dojo/_base/lang',
	'dojo/dom-class',
	'dojo/dom-construct',
	'dojo/html',
	'dojo/on',
	'dojo/request/xhr',
	'dojo/json',
	'dojo/text!./templates/NotificationsPopupItem.html',
	'alfresco/header/AlfMenuItem'
],
	function (declare, lang, domClass, domConstruct, html, on, xhr, json, template, AlfMenuItem) {

		return declare([AlfMenuItem], {

			templateString: template,

			cssRequirements: [{cssFile: './css/AlfMenuItem.css'}],

			_markAsRead: function() {
				xhr.post(Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/notifications/active-channel/api/set/read', {
					handleAs: 'json',
					headers: {'Content-Type': 'application/json'},
					data: json.stringify([{
						nodeRef: this.params.item.nodeRef
					}])
				}).then(lang.hitch(this, function(success) {
					domClass.remove(this.notificationNode, 'bold');
					this.params.notificationsPopup.loadNewNotificationsCount();
				}), lang.hitch(this, function(failure) {
					//TODO: dojo i18n
					Alfresco.util.message('не удалось установить прочитанные уведомления');
				}));
			},

			postCreate: function () {
				this.inherited(arguments);
				html.set(this.notificationDetailsNode, this.params.item.description);
				domConstruct.place(Alfresco.util.relativeTime(new Date(this.params.item.formingDate)), this.notificationNode, 'last');
				domClass.toggle(this.notificationNode, 'bold', this.params.item.isRead == 'false');
				on(this.domNode, 'a:click', lang.hitch(this, this._onHrefClick));
			},

			//TODO: ctrl+click handler, open in new tab handler also should mark notification as read!
			_onHrefClick: function(evt) {
				var href = evt.target.href;
				var pathname = evt.target.pathname;
				var pageUrl = href.slice(href.indexOf(pathname), href.length);
				var url = pageUrl.slice(Alfresco.constants.URL_PAGECONTEXT.length, pageUrl.length);
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
