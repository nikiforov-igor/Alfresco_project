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

			// var refreshEl = Dom.get(id + "-action-refresh");
			// if (refreshEl) {
			// 	refreshEl.onclick = this.onRefresh.bind(this);
			// }

			Alfresco.util.createTwister(id + "-heading", "DocumentSignedDocflow");
		},

		onRefresh: function (event) {
			CryptoApplet.updateSignsAction(this.options.nodeRef, {successCallback: {fn: this.onViewSignatures, scope: this}});
		},

		onSignDocuments:  function (event) {
			CryptoApplet.signMultAction(this.options.nodeRef);
		},

		bindAjaxSendDocument: function(dataObj) {
			return function makeDataRequest() {

				var loadingPopup = Alfresco.util.PopupManager.displayMessage({
					text: this.msg("msg.wait_while_sending"),
					spanClass: "wait",
					displayTime: 0,
					modal: true
				});

				loadingPopup.center();

				Alfresco.util.Ajax.jsonRequest({
					method: "POST",
					url: Alfresco.constants.PROXY_URI_RELATIVE + "/lecm/signed-docflow/sendContentToPartner",
					dataObj: dataObj,
					successCallback: {
						fn: function(response) {
							var message,
								authSimpleDialog,

								responses = response.json,

								good = responses.filter(function(v) { return v.gateResponse.responseType == "OK"; }),
								bad = responses.filter(function(v) { return v.gateResponse.responseType != "OK"; }),
								partner = responses.filter(function(v) { return v.gateResponse.responseType == "PARTNER_ERROR"; }),
								unauthorized = responses.filter(function(v) { return v.gateResponse.responseType == "UNAUTHORIZED"; });

							function hideAndReload() {
								loadingPopup.destroyWithAnimationsStop();
								window.location.reload();
							}

							loadingPopup.destroyWithAnimationsStop();

							console.log(">>> Всего отправлено документов: " + responses.length);
							console.log("Документов, со статусом \"ОК\": " + good.length);
							console.log("Документов, со статусом отличным от \"OK\": " + bad.length);
							console.log("Документов, со статусом \"PARTNER_ERROR\": " + partner.length);

							// Выходим, если всё хорошо
							if(good.length == responses.length) {
								message = this.msg((responses.length > 1) ? "msg.documents_sent" : "msg.document_sent");
								loadingPopup = Alfresco.util.PopupManager.displayMessage({ text: message });
								YAHOO.lang.later(2500, null, hideAndReload);
								return;
							}
							if(unauthorized.length == 0) {
								return;
							}

							// Показываем форму авторизации, в ином случае
							authSimpleDialog = new Alfresco.module.SimpleDialog("${htmlId}-auth-form");

							authSimpleDialog.setOptions({
								width: "50em",
								templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
								templateRequestParams: {
									itemKind: "type",
									itemId: "lecm-orgstr:employees",
									formId: "auth-form",
									mode: "create",
									showCancelButton: "true",
									submitType: "json"
								},
								actionUrl: null,
								destroyOnHide: true,
								doBeforeDialogShow:{
									fn: function(form, simpleDialog) {
										simpleDialog.dialog.setHeader(this.msg("msg.auth_needed"));
									}
								},
								doBeforeAjaxRequest: {
									fn: function() {
											CryptoApplet.unicloudAuth({successCallback: {fn: makeDataRequest, scope: this}});
										return false;
									}
								},
								onFailure: {
									fn: function() {
										Alfresco.util.PopupManager.displayMessage({
											text: this.msg("msg.auth_form_failed")
										});
									}
								}
							});

							authSimpleDialog.show();
						}
					},
					failureCallback: {
						fn: function() {
							loadingPopup.destroyWithAnimationsStop();
							Alfresco.util.PopupManager.displayMessage({
								text: this.msg("msg.documents_send_failed")
							});
						}
					}
				});
			}
		},

		onSendDocuments: function (event) {
			var formParams = {},
				checkOptions = {};

			formParams.docNodeRef = this.options.nodeRef;
			formParams.actionURL = null;
			formParams.title = this.msg('title.documents_to_send');
			formParams.htmlId = this.id;

			function sendDocuments(nodeRef){
				var dataObj = {
					content: nodeRef
				};

				this.bindAjaxSendDocument(dataObj)();
			};

			checkOptions.successCallback = {};
			checkOptions.successCallback.fn = sendDocuments;
			checkOptions.successCallback.scope = this;

			formParams.doBeforeAjaxCallback = {};
			formParams.doBeforeAjaxCallback.fn = CryptoApplet.CheckSignaturesByNodeRefList;
			formParams.doBeforeAjaxCallback.scope = CryptoApplet;
			formParams.doBeforeAjaxCallback.obj = checkOptions;

			CryptoApplet.loadMultipleForm(formParams);


		},

		onViewSignatures: function (event) {
            Alfresco.util.PopupManager.zIndex = 100003;
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
						p_dialog.dialog.setHeader(this.msg("title.signing_info"));
                        p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
						window.tmpDialog = p_dialog;
						p_form.doBeforeFormSubmit = {
							fn: function() {
								this.setAJAXSubmit(false);
                                tmpDialog.hide();
							},
							scope: p_form
						};
					}
				},
				onFailure: {
					fn: function() {
						Alfresco.util.PopupManager.displayMessage({
							text: this.msg("msg.get_signing_info_failed")
						});
					},
					scope: this
				}
			});

			form.show();
		}
	}, true);
})();
