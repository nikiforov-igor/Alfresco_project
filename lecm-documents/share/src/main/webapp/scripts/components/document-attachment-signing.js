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
			cryptoAppletModule.Sign(this.options.nodeRef);
		},

		onRefreshSignatures: function(layer, args) {
			cryptoAppletModule.CheckContentSignature(this.options.nodeRef);
			this.onViewSignature(layer, args);
		},

		onUploadSignature: function(layer, args) {
			cryptoAppletModule.loadSign(this.options.nodeRef);
		},

		onSignableSwitch: function(layer, args) {
			var checkbox = args.checkbox;

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

		onSendDocument: function(layer, args) {
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

		onSignaturesReceived: function(layer, args) {
			alert("onSignaturesReceived");
		},

		onRefreshSentDocuments: function(layer, args) {
			alert("onRefreshSentDocuments");
		}
	}, true);
})();
