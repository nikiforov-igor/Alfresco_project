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

LogicECM.module.ARM = LogicECM.module.ARM || {};

(function () {
	var Dom = YAHOO.util.Dom;

	LogicECM.module.ARM.EditNode = function (htmlId) {
		LogicECM.module.ARM.EditNode.superclass.constructor.call(this, "LogicECM.module.ARM.EditNode", htmlId);

		YAHOO.Bubbling.on("activeGridChanged", this.onSelectTreeNode, this);
		return this;
	};

	YAHOO.extend(LogicECM.module.ARM.EditNode, Alfresco.component.Base,
		{
			onSelectTreeNode: function(layer, args) {
				if (args[1].datagridMeta != null && args[1].datagridMeta.nodeRef != null && args[1].datagridMeta.itemType != "lecm-arm:arm") {
					var nodeRef = args[1].datagridMeta.nodeRef;
					var me = this;

					Alfresco.util.Ajax.request(
						{
							url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
							dataObj: {
								htmlid: "arm-settings-edit-form",
								itemKind:"node",
								itemId: nodeRef,
								mode: "edit",
								formUI: true,
								submitType:"json",
								showSubmitButton:"true"
							},
							successCallback: {
								fn: function (response) {
									Dom.get(me.id + "-body").innerHTML = response.serverResponse.responseText;

									Dom.get("arm-settings-edit-form-form-submit").value = me.msg("label.save");

									var form = new Alfresco.forms.Form("arm-settings-edit-form-form");
									form.setSubmitAsJSON(true);
									form.setAJAXSubmit(true,
										{
											successCallback:
											{
												fn: me.onSuccess,
												scope: me
											}
										});
									form.init();
								}
							},
							failureMessage: "message.save.failure",
							execScripts: true
						});
				} else {
					Dom.get(this.id + "-body").innerHTML = "";
				}
			},

			onSuccess: function (response) {
				if (response && response.json) {
					YAHOO.Bubbling.fire("itemsListChanged", {
						refreshParent: true
					});
					Alfresco.util.PopupManager.displayMessage(
						{
							text: this.msg( "message.save.success")
						});
				} else {
					Alfresco.util.PopupManager.displayPrompt(
						{
							text: Alfresco.util.message("message.save.failure")
						});
				}
			}
		});
})();
