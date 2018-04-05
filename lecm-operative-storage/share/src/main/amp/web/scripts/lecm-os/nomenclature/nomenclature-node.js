if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Nomenclature = LogicECM.module.Nomenclature || {};

(function() {
	var Dom = YAHOO.util.Dom;

	LogicECM.module.Nomenclature.Node = function(htmlId) {
		LogicECM.module.Nomenclature.Node.superclass.constructor.call(this, "LogicECM.module.Nomenclature.Node", htmlId);

		YAHOO.Bubbling.on("activeGridChanged", this.onSelectTreeNode, this);
		return this;
	};

	YAHOO.extend(LogicECM.module.Nomenclature.Node, Alfresco.component.Base, {
		onSelectTreeNode: function(layer, args) {
			if ((args[1].datagridMeta != null) && (args[1].datagridMeta.nodeRef != null)) {
				YAHOO.Bubbling.on("beforeFormRuntimeInit", this.beforeFormInit, this);

				var nodeRef = args[1].datagridMeta.nodeRef;
				var htmlId = Alfresco.util.generateDomId() + "-nomenclature-node-form";
				Alfresco.util.Ajax.request({
					url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
					dataObj: {
						htmlid: htmlId,
						itemKind: "node",
						itemId: nodeRef,
						mode: "edit",
						formUI: true,
						submitType: "json",
						showSubmitButton: "true",
						showCaption: false
					},
					successCallback: {
						scope: this,
						fn: function(response) {
							Dom.get(this.id + "-body").innerHTML = response.serverResponse.responseText;
							Dom.get(htmlId + "-form-submit").value = this.msg("label.save");
						}
					},
					failureMessage: "message.save.failure",
					execScripts: true
				});
			} else {
				Dom.get(this.id + "-body").innerHTML = "";
			}
		},

		beforeFormInit: function (layer, args) {
			YAHOO.Bubbling.unsubscribe("beforeFormRuntimeInit", this.beforeFormInit);
			var form = args[1].runtime;
			form.setSubmitAsJSON(true);
			form.setAJAXSubmit(true, {
				successCallback: {
					scope: this,
					fn: function(response) {
						if (response && response.json) {
							YAHOO.Bubbling.fire("refreshTreeParentNode");
							Alfresco.util.PopupManager.displayMessage({
								text: this.msg("message.save.success")
							});
						} else {
							Alfresco.util.PopupManager.displayPrompt({
								text: Alfresco.util.message("message.save.failure")
							});
						}
					}
				}
			});
		}
	});
})();
