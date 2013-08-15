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
(function () {

	Alfresco.util.PopupManager.zIndex = 1000;

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
	LogicECM.DocumentSignedDocflow = function DocumentSignedDocflow_constructor(htmlId) {
		LogicECM.DocumentMembers.superclass.constructor.call(this, htmlId);

		return this;
	};

	YAHOO.extend(LogicECM.DocumentSignedDocflow, LogicECM.DocumentComponentBase);

	YAHOO.lang.augmentObject(LogicECM.DocumentSignedDocflow.prototype, {
		newId: null,

		onReady: function DocumentSignedDocflow_onReady() {
			var id = this.newId ? this.newId : this.id;

			var refreshEl = Dom.get(id + "-action-refresh");
			if (refreshEl) {
				refreshEl.onclick = this.onRefresh.bind(this);
			}

			Alfresco.util.createTwister(id + "-heading", "DocumentSignedDocflow");
		},

		onRefresh: function (event) {
			cryptoAppletModule.CheckDocumentSignatures(this.options.nodeRef);
		},

		onSignDocuments:  function (event) {
			cryptoAppletModule.MultipleSignFormShow(this.options.nodeRef, cryptoAppletModule.SignMultiple);
		},

		onSendDocuments: function (event) {
			cryptoAppletModule.MultipleSignFormShow(this.options.nodeRef, cryptoAppletModule.SendMultiple);
		},

		onViewSignatures: function (event) {
			var form = new Alfresco.module.SimpleDialog(this.id + "-signs-all-form");

			form.setOptions({
				width: "50em",
				templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
				templateRequestParams: {
					itemKind: "type",
					itemId: "lecm-signed-docflow:sign",
					mode: "create",
					submitType: "json",
					showCancelButton: "true",
					formId: "signs-info-all",
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
		}
	}, true);
})();
