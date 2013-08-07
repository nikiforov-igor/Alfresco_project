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
	LogicECM.ContentSigning = function ContentSigning_constructor(htmlId) {
		LogicECM.ContentSigning.superclass.constructor.call(this, htmlId);

		return this;
	};

	YAHOO.extend(LogicECM.ContentSigning, LogicECM.DocumentComponentBase);

	YAHOO.lang.augmentObject(LogicECM.ContentSigning.prototype,
			{
				newId: null,
				onReady: function() {
					var id = this.newId ? this.newId : this.id;

					Alfresco.util.createTwister(id + "-signing-heading", "ContentSigning");
					Alfresco.util.createTwister(id + "-exchange-heading", "DocumentAttachmentExchange");

				},
				onViewSignature: function(layer, args) {
					var form = new Alfresco.module.SimpleDialog(this.id + "-signs-short-form");

					form.setOptions({
						width: "50em",
						templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
						templateRequestParams: {
							itemKind: "type",
							itemId: "lecm-signed-docflow:sign",
							mode: "create",
							submitType: "json",
							showCancelButton: "true",
							formId: "signs-info-short",
							signedContentRef: this.options.nodeRef
						},
						destroyOnHide: true,
						doBeforeDialogShow:{
							fn: function( p_form, p_dialog ) {
								p_dialog.dialog.setHeader("Просмотр информации о подписях");
								p_form.doBeforeFormSubmit = {
									fn: function() {
										this.setAJAXSubmit(false);
									},
									scope: p_form
								};
							}
						},
						onFailure: {
							fn: function() {
								Alfresco.util.PopupManager.displayMessage({
									text: "Не удалось получить информацию о подписях"
								});
							},
							scope: this
						}
					});

					form.show();
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
