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
	LogicECM.DocumentAttachmentSigning = function DocumentAttachmentSigning_constructor(htmlId) {
		LogicECM.DocumentAttachmentSigning.superclass.constructor.call(this, htmlId);

		return this;
	};

	YAHOO.extend(LogicECM.DocumentAttachmentSigning, LogicECM.DocumentComponentBase);

	YAHOO.lang.augmentObject(LogicECM.DocumentAttachmentSigning.prototype, {
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

		onViewSignature: function(event) {
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
			cryptoAppletModule.Sign(this.options.nodeRef, this.checkSigned, this);
		},

		checkSigned: function(scope){
			var checkbox = document.getElementById(scope.id + '-signableSwitch');

			Alfresco.util.Ajax.request({
				method: "GET",
				url: Alfresco.constants.PROXY_URI_RELATIVE + "/lecm/signed-docflow/getSignsInfo?signedContentRef=" + scope.options.nodeRef,
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
						if(response.json[0].signatures.length != 0){
							checkbox.disabled = true;
						}
					},
					scope: this
				}
			});
		},

		onRefreshSignatures: function(event) {
			cryptoAppletModule.CheckContentSignature(this.options.nodeRef);
			this.onViewSignature(event);
		},

		onUploadSignature: function(event) {
			cryptoAppletModule.loadSign(this.options.nodeRef, this.checkSigned, this);
		},

		onSignableSwitch: function(event) {
			var checkbox = event.currentTarget;

			Alfresco.util.Ajax.request({
				method: "POST",
				url: Alfresco.constants.PROXY_URI_RELATIVE + "/lecm/signed-docflow/config/aspect",
				dataObj: {
					action: "set",
					node: this.options.nodeRef,
					aspect: "{http://www.it.ru/lecm/model/signed-docflow/1.0}signable",
					enabled: checkbox.checked
				},
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
							this.signingContainer.style.display = "none";
							this.exchangeContainer.style.display = "none";
						}
					},
					scope: this
				}
			});

		},

		onSendDocument: function(event) {
			Alfresco.util.PopupManager.displayMessage({
				text: "Отправка документа контрагенту"
			});
			cryptoAppletModule.SendToContragent(this.options.nodeRef);
			//проверка всех наших подписей на валидность
			//если хоть одна из наших подписей невалидна
			//то ничего никуда не отправляем

			//вызываем сервис, который отправит документ контрагенту
			//this.options.nodeRef это NodeRef на наше вложение
			// Alfresco.util.Ajax.jsonRequest({
			// 	method: "POST",
			// 	url: Alfresco.constants.PROXY_URI_RELATIVE + "/lecm/signed-docflow/sendContentToPartner",
			// 	dataObj: {
			// 		content:[this.options.nodeRef]
			// 	},
			// 	failureMessage: this.msg("message.sending.attachment.failure"),
			// 	successCallback: {
			// 		fn: function(response) {
			// 			//смотрим какой ответ пришел нам с сервера
			// 			//если все хорошо, то выводим сообщение что все хорошо
			// 			//если response.json.gateResponse.responseType != OK то надо пойти в сценарий повторной авторизации
			// 			//и выполнить действие заново
			// 		},
			// 		scope: this
			// 	}
			// });
		},

		onSignaturesReceived: function(event) {
			cryptoAppletModule.CheckContentSignature(this.options.nodeRef);
			this.onViewSignature(event);
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
											var currentContainer = cryptoAppletModule.getCurrentContainer();

											if(currentContainer == "") {
												Alfresco.util.PopupManager.displayMessage({
													text: "Вы не выбрали сертификат"
												});
											} else {
												cryptoAppletModule.unicloudAuth(currentContainer, makeDataRequest);
											}

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
				cryptoAppletModule.loadSignFromString(signatures[i], contentRef);
			}
		},

		onRefreshSentDocuments: function(event) {
			var templateUrl = "lecm/signed-docflow/getSignedContentFromPartner?nodeRef={nodeRef}",
				url = YAHOO.lang.substitute(templateUrl, {
					nodeRef: this.options.nodeRef
				}),
				makeDataRequest;

			makeDataRequest = this._bindAjaxTo("GET", url, {});
			makeDataRequest(this._loadSignaturesToRepo);
		}
	}, true);
})();
