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
 * @class LogicECM.DocumentMembers
 */
(function() {
	/**
	 * YUI Library aliases
	 */
	var Dom = YAHOO.util.Dom,
			Event = YAHOO.util.Event;

	/**
	 * DocumentHistory constructor.
	 *
	 * @param {String} htmlId The HTML id of the parent element
	 * @return {LogicECM.DocumentMembers} The new DocumentHistory instance
	 * @constructor
	 */
	LogicECM.DocumentAttachmentSigning = function DocumentAttachmentSigning_constructor(htmlId) {
		LogicECM.DocumentAttachmentSigning.superclass.constructor.call(this, htmlId);

		return this;
	};

	YAHOO.extend(LogicECM.DocumentAttachmentSigning, LogicECM.DocumentComponentBase);

	YAHOO.lang.augmentObject(LogicECM.DocumentAttachmentSigning.prototype,
			{
				newId: null,
				signingContainer: null,
				exchangeContainer: null,
				onReady: function() {
					var id = this.newId ? this.newId : this.id;

					Alfresco.util.createTwister(id + "-signing-heading", "DocumentAttachmentSigning");
					Alfresco.util.createTwister(id + "-exchange-heading", "DocumentAttachmentExchange");

					this.signingContainer = Dom.get(id + "-signing-container");
					this.exchangeContainer = Dom.get(id + "-exchange-container");
				},
				onViewSignature: function(layer, args) {
					alert("onViewSignature");
				},
				onSignDocument: function(layer, args) {
					alert("onSignDocument");
				},
				onRefreshSignatures: function(layer, args) {
					alert("onRefreshSignatures");
				},
				onUploadSignature: function(layer, args) {
					alert("onUploadSignature");
				},
				onSignableSwitch: function(layer, args) {
					var checkbox = args.checkbox;

					Alfresco.util.Ajax.request({
						method: "GET",
						url: Alfresco.constants.PROXY_URI_RELATIVE + "/lecm/signed-docflow/setSignable?nodeRef=" + this.options.nodeRef + "&action=" + checkbox.checked,
						requestContentType: "application/json",
						responseContentType: "application/json",
						failureCallback: {
							fn: function(response) {
								Alfresco.util.PopupManager.displayMessage({
									text: msg("message.setting.signable.failure")
								});
							},
							scope: this
						},
						successCallback: {
							fn: function(response) {
								if (checkbox.checked) {
									this.signingContainer.style.display = "block";
									this.exchangeContainer.style.display = "block";
								} else {
									this.signingContainer.style.display = "none"
									this.exchangeContainer.style.display = "none";
								}
							},
							scope: this
						}
					});

				},
				onSendDocument: function(layer, args) {
					alert("onSendDocument");
				},
				onSignaturesReceived: function(layer, args) {
					alert("onSignaturesReceived");
				},
				onRefreshSentDocuments: function(layer, args) {
					alert("onRefreshSentDocuments");
				}
			}, true);
})();
