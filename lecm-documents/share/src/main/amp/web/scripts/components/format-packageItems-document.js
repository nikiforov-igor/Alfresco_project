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

(function()
{
	var Dom = YAHOO.util.Dom;

	LogicECM.module.FormatPackageItemsDocument = function (fieldHtmlId)
	{
		LogicECM.module.FormatPackageItemsDocument.superclass.constructor.call(this, "LogicECM.module.FormatPackageItemsDocument", fieldHtmlId, [ "container"]);

		return this;
	};

	YAHOO.extend(LogicECM.module.FormatPackageItemsDocument, Alfresco.component.Base,
		{
			options:
			{
				substituteString: "{cm:name}"
			},

			onReady: function AssociationCheckboxes_onReady()
			{
				var documentRef = null;

				var form = Dom.get(this.id).form;
				if (form != null && form["assoc_packageItems"] != null) {
					documentRef = form["assoc_packageItems"].value;
				}

				if (documentRef != null && documentRef.length > 0) {
					this.loadFormatString(documentRef);
				}
			},

			loadFormatString: function(nodeRef) {
				var me = this;
				Alfresco.util.Ajax.jsonRequest(
					{
						url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
						method: "POST",
						dataObj:
						{
							nodeRef: nodeRef,
							substituteString: this.options.substituteString
						},
						successCallback:
						{
							fn: function (response, obj) {
								var oResult = eval("(" + response.serverResponse.responseText + ")");
								if (oResult != null && oResult.formatString != null) {
									var displayValueInput = Dom.get(me.id + "-valueDisplay");
									if (displayValueInput != null) {
										displayValueInput.innerHTML = oResult.formatString;
									}
								}
							},
							scope: this
						},
						failureMessage: this.msg("message.formatString.failure")
					});
			}
		});
})();