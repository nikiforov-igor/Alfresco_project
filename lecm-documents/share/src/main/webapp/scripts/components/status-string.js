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
 * DocumentHistory
 *
 * @namespace LogicECM
 * @class LogicECM.DocumentStatusString
 */
(function () {
	/**
	 * YUI Library aliases
	 */
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event;

	/**
	 * DocumentHistory constructor.
	 *
	 * @param {String} htmlId The HTML id of the parent element
	 * @return {LogicECM.DocumentStatusString} The new DocumentHistory instance
	 * @constructor
	 */
	LogicECM.DocumentStatusString = function DocumentStatusString_constructor(htmlId) {
		LogicECM.DocumentStatusString.superclass.constructor.call(this, "LogicECM.DocumentStatusString", htmlId);

		YAHOO.Bubbling.on("updateDocumentPage", this.updateDocumentPage, this);
		return this;
	};

	YAHOO.extend(LogicECM.DocumentStatusString, Alfresco.component.Base,
		{
			options: {
				nodeRef: null,
				propertyName: null
			},

			onReady: function () {
				this.widgets.submitButton = Alfresco.util.createYUIButton(this, "submit-button", this.onSubmit);
				YAHOO.util.Event.addListener(Dom.get(this.id + "-property-value"), "keypress", this.adjustTextareaHeight, this);
			},

			onSubmit: function () {
				var me = this;
				Alfresco.util.Ajax.jsonRequest(
					{
						method: "POST",
						url: Alfresco.constants.PROXY_URI + "lecm/document/api/editDocument",
						dataObj: {
							nodeRef: me.options.nodeRef,
							properties: me.options.propertyName + "=" + Dom.get(this.id + "-property-value").value + "," + me.options.propertyName + "=" + Dom.get(this.id + "-property-value").value
						},
						successMessage: this.msg("message.submit.success"),
						failureMessage: this.msg("message.submit.failure")
					});
			},

			updateDocumentPage: function (layer, args) {
				if (args[1] != null && args[1].title != null) {
					Dom.get(this.id + "-page").innerHTML = args[1].title;
				}
			},

			adjustTextareaHeight: function (event, scope) {
				var textarea = event.currentTarget;
				setTimeout(function() {
					var dif = textarea.scrollHeight - textarea.clientHeight
					if (dif) {
						if (isNaN(parseInt(textarea.style.height))) {
							textarea.style.height = textarea.scrollHeight + "px"
						} else {
							textarea.style.height = parseInt(textarea.style.height) + dif + "px"
						}
					}
				}, 1);
			}
		});
})();
