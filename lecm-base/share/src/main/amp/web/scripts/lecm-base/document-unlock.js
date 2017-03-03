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
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module.UnlockNode = LogicECM.module.UnlockNode || {};

(function() {
	LogicECM.module.UnlockNode.unlock = function(file, callbackFn) {
		Alfresco.util.PopupManager.displayPrompt({
			title: Alfresco.util.message('lecm.unlock.msg.unlock.confirm'),
			text: Alfresco.util.message('lecm.unlock.msg.unlock.version.features'), // the text to display for the user, mandatory
			modal: true,
			buttons: [
				{
					text: Alfresco.util.message('lecm.unlock.yes'),
					handler: handleYes
				}, {
					text: Alfresco.util.message('lecm.unlock.no'),
					handler: function() {
						this.hide();
					}
				}
			]
		});

		function handleYes() {
			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/unlock',
				dataObj: {
					nodeRef: file.nodeRef
				},
				successCallback: {
					scope: this,
					fn: function () {
						if (YAHOO.lang.isFunction(callbackFn)) {
							callbackFn();
						} else {
							window.location.reload();
						}
					}
				},
				onFailure: {
					fn: function () {
						Alfresco.util.PopupManager.displayMessage(
							{
								text:  Alfresco.util.message('lecm.unlock.msg.unlock.doc.error')
							});
					},
					scope: this
				},
				execScripts: true,
				scope: this
			});
			this.hide();
		}
	};

	YAHOO.Bubbling.fire("registerAction", {
		actionName: "onUnlockAction",
		fn: LogicECM.module.UnlockNode.unlock
	});
})();
