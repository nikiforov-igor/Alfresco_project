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
	LogicECM.ContentSigning = function ContentSigning_constructor(htmlId) {
		LogicECM.ContentSigning.superclass.constructor.call(this, htmlId);

		return this;
	};

	YAHOO.extend(LogicECM.ContentSigning, LogicECM.DocumentComponentBase);

	YAHOO.lang.augmentObject(LogicECM.ContentSigning.prototype, {
		newId: null,

		onReady: function() {
			var id = this.newId ? this.newId : this.id;

			Alfresco.util.createTwister(id + "-signing-heading", "ContentSigning");
			Alfresco.util.createTwister(id + "-exchange-heading", "DocumentAttachmentExchange");

		},

		onViewSignature: function(event) {
			var form = new Alfresco.module.SimpleDialog(this.id + "-signs-short-form");

			form.setOptions({
				width: "50em",
				templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
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
		},

		onSignDocument: function(event) {
			CryptoApplet.signAction(this.options.nodeRef);
		},

		onRefreshSignatures: function(event) {
			CryptoApplet.updateSignsAction(this.options.nodeRef, {successCallback: {fn: this.onViewSignature, scope: this}});
		},

		onUploadSignature: function(event) {
			CryptoApplet.loadSignAction(this.options.nodeRef);
		},

		onSendDocument: function() {
			var checkOptions = {};

			function showForm() {
				var form = new Alfresco.module.SimpleDialog(this.id + "-send-to-contractor-form");

				form.setOptions({
					width: "50em",
					templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
					templateRequestParams: {
						itemKind: "type",
						itemId: "lecm-contractor:contractor-type",
						mode: "create",
						submitType: "json",
						showCancelButton: "true",
						formId: "send-to-contractor"
					},
					destroyOnHide: true,
					contentRef: this.options.nodeRef,
					doBeforeDialogShow:{
						fn: function(form, dialog) {
							dialog.dialog.setHeader("Отправка контрагенту");
						}
					},
					onFailure: {
						fn: function() {
							Alfresco.util.PopupManager.displayMessage({
								text: "Не удалось получить форму отправки, попробуйте ещё раз"
							});
						}
					}
				});

				form.show();
			};

			checkOptions.successCallback = {};
			checkOptions.successCallback.fn = showForm;
			checkOptions.successCallback.scope = this;

			CryptoApplet.CheckSignaturesByNodeRefList(this.options.nodeRef, checkOptions);

		},

		onSignaturesReceived: function(event) {
			CryptoApplet.updateSignsAction(this.options.nodeRef, {successCallback: {fn: this.onViewSignature, scope: this}});

		},

		_bindAjaxTo: function ContentSigning_bindAjaxTo(method, url, dataObj) {
			var id = this.newId,
				contentRef = this.options.nodeRef;
			return function makeDataRequest(signatureHandler) {

				var loadingPopup = Alfresco.util.PopupManager.displayMessage({
					text: "Пожалуйста, подождите, запрашиваются подписи контрагента",
					spanClass: "wait",
					displayTime: 0,
					modal: true
				});

				loadingPopup.center();

				Alfresco.util.Ajax.jsonRequest({
					method: method,
					url: Alfresco.constants.PROXY_URI_RELATIVE + url,
					dataObj: dataObj,
					successCallback: {
						fn: function(response) {
							var message,
								authSimpleDialog,
								result = response.json;

							function hideAndReload() {
								loadingPopup.destroy();
								window.location.reload();
							}

							loadingPopup.destroy();

							// Выходим, если всё хорошо
							if(result.gateResponse.responseType == "OK") {
								if (result.signatures.length > 1) {
									message = "Подписи успешно получены";
								} else if (result.signatures.length) {
									message = "Подпись успешно получена";
								} else {
									message = "Новых подписей нет";
								}
								loadingPopup = Alfresco.util.PopupManager.displayMessage({ text: message });
								YAHOO.lang.later(2500, null, hideAndReload);

								if(YAHOO.lang.isFunction(signatureHandler)) {
									signatureHandler(contentRef, result.signatures);
								}

								return;
							}

							if(result.gateResponse.responseType == "UNAUTHORIZED") {
								// Показываем форму авторизации, в ином случае
								authSimpleDialog = new Alfresco.module.SimpleDialog(id + "-auth-form");

								authSimpleDialog.setOptions({
									width: "50em",
									templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
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
											simpleDialog.dialog.setHeader("Необходима аутентификация, выберите сертификат");
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
												text: "Не удалось открыть форму аутентификации, попробуйте ещё раз"
											});
										}
									}
								});

								authSimpleDialog.show();
								return;
							} else {
								Alfresco.util.PopupManager.displayPrompt({
									title: "Ошибка при получении подписей документа от контрагента",
									text: YAHOO.lang.substitute("Код ошибки: {responseType}. {message}", result.gateResponse)
								});
							}
						}
					},
					failureCallback: {
						fn: function() {
							loadingPopup.destroy();
							Alfresco.util.PopupManager.displayMessage({
								text: "Не удалось получить подписи"
							});
						}
					}
				});
			};
		},

		_loadSignaturesToRepo: function(contentRef, signatures) {
			var i,
				size = signatures.length;
			for(i = 0; i < size; ++i) {
				CryptoApplet.loadSignFromString(contentRef, signatures[i]);
			}
		},

		_getSignedContentFromPartner: function ContentSigning_getSignedContentFromPartner(response) {
			//надо проверить что в response.json что-то есть, иначе послать лесом...
			var interType = response.json.interactionType,
				templateUrl = "lecm/signed-docflow/getSignedContentFromPartner?method={method}&nodeRef={nodeRef}",
				url,
				makeDataRequest;

			if (YAHOO.lang.isValue(interType)) {
				url = YAHOO.lang.substitute(templateUrl, {
						method: interType,
						nodeRef: this.options.nodeRef
					});

				makeDataRequest = this._bindAjaxTo("GET", url, {});
				makeDataRequest(this._loadSignaturesToRepo);
			} else {
				Alfresco.util.PopupManager.displayPrompt({
					title: "Ошибка при получении подписей документа от контрагента",
					text: "Этот документ не участвовал в ЭДО ни с одним контрагентом. Прежде чем получать подписи, необходимо направить документ контрагенту"
				});
			}
		},

		onRefreshSentDocuments: function(event) {

			//получаем из ноды информацию по тому контрагенту с которым мы взаимодействуем
			Alfresco.util.Ajax.jsonRequest({
				url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/signed-docflow/getContractorInfoBySendedContent",
				dataObj: { nodeRef: this.options.nodeRef },
				successCallback: { fn: this._getSignedContentFromPartner, scope: this },
				failureCallback: { fn: onFailureCallback }
			});

			function onFailureCallback(response) {
				Alfresco.util.PopupManager.displayMessage({
					text: "Не удалось получить данные по контрагенту для документа"
				});
			}
		}
	}, true);
})();
