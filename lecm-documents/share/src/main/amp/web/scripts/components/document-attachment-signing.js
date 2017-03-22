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
        doubleClickLock: false,
		onReady: function() {
			var id = this.newId ? this.newId : this.id;

			Alfresco.util.createTwister(id + "-signing-heading", "DocumentAttachmentSigning");
			Alfresco.util.createTwister(id + "-exchange-heading", "DocumentAttachmentExchange");

			this.signingContainer = Dom.get(id + "-signing-container");
			this.exchangeContainer = Dom.get(id + "-exchange-container");
		},

		onViewSignature: function(event) {
            if (this.doubleClickLock) return;
            this.doubleClickLock = true;
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
					signedContentRef: this.options.nodeRef,
					showCaption: false
				},
				destroyOnHide: true,
				doBeforeDialogShow:{
					fn: function( p_form, p_dialog ) {
                        this.doubleClickLock = false;
						Dom.addClass(p_dialog.dialog.element, "visible-force-hidden");
						p_dialog.dialog.setHeader(this.msg("title.signing_info"));
						p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id, force: true}, this);
						p_form.doBeforeFormSubmit = {
							fn: function() {
								this.setAJAXSubmit(false);
								p_dialog.hide();
							},
							scope: p_form
						};
					},
                    scope: this
				},
				onFailure: {
					fn: function() {
						Alfresco.util.PopupManager.displayMessage({
							text: this.msg("msg.get_signing_info_failed")
						});
                        this.doubleClickLock = false;
					},
					scope: this
				},
                onSuccess:{
                    fn:function (response) {
                        this.doubleClickLock = false;
                    },
                    scope:this
                }
			});

			form.show();
		},

		onSignDocument: function(event) {
			CryptoApplet.signAction(this.options.nodeRef, {successCallback: {fn: this.checkSigned, scope: this}});
		},

		checkSigned: function(){
			var checkbox = document.getElementById(this.id + '-signableSwitch');

			Alfresco.util.Ajax.jsonRequest({
				method: "POST",
				url: Alfresco.constants.PROXY_URI_RELATIVE + "/lecm/signed-docflow/getSignsInfo",
				dataObj: [this.options.nodeRef],
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
			CryptoApplet.updateSignsAction(this.options.nodeRef, {successCallback: {fn: this.onViewSignature, scope: this}});
		},

		onUploadSignature: function(event) {
            var localSign = Dom.get(this.id+"-localSign");
            localSign.click();
		},

        handleClientLocalSign: function(evt) {
            if (!window.FileReader) {
                Alfresco.util.PopupManager.displayMessage({	text: 'Подписи не загружены, браузер не поддерживает FileAPI' });
                return;
            }
            var oFile = evt.target.files[0];
            var oFReader = new FileReader();
            if (!YAHOO.lang.isFunction(oFReader.readAsDataURL)) {
                Alfresco.util.PopupManager.displayMessage({	text: 'Подписи не загружены, не поддерживаются некоторые функции FileAPI' });
                return;
            }
            oFReader.onload = this.oFileReaderOnLoad.bind(this);
            oFReader.readAsText(oFile);
        },

        oFileReaderOnLoad: function(oFREvent) {
            var sFileData = oFREvent.target.result;
            CryptoApplet.loadSignAction(this.options.nodeRef, sFileData, {successCallback: {fn: this.checkSigned, scope: this}});
	    },

        onExportSignature: function(event) {
			CryptoApplet.exportSignAction(this.options.nodeRef);
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
							if (this.options.isExchangeEnabled) {
								this.exchangeContainer.style.display = "block";
							}
						} else {
							this.signingContainer.style.display = "none";
							this.exchangeContainer.style.display = "none";
						}
					},
					scope: this
				}
			});

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
								templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
								templateRequestParams: {
									itemKind: "type",
									itemId: "lecm-orgstr:employees",
									formId: "auth-form",
									mode: "create",
									showCancelButton: "true",
									submitType: "json",
									showCaption: false
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

		onSendDocument: function(event) {
			var checkOptions = {};

			function sendDocuments(nodeRef){
				var dataObj = {
					content: nodeRef
				};

				this.bindAjaxSendDocument(dataObj)();
			};

			checkOptions.successCallback = {};
			checkOptions.successCallback.fn = sendDocuments;
			checkOptions.successCallback.scope = this;

			CryptoApplet.CheckSignaturesByNodeRefList(this.options.nodeRef, checkOptions);

		},

		onSignaturesReceived: function(event) {
			CryptoApplet.updateSignsAction(this.options.nodeRef, {successCallback: {fn: this.onViewSignature, scope: this}});
		},

		_bindAjaxTo: function ContentSigning_bindAjaxTo(method, url, dataObj, scope, signatureHandler) {
			var id = this.newId,
				contentRef = this.options.nodeRef;
			return function makeDataRequest() {

				var loadingPopup = Alfresco.util.PopupManager.displayMessage({
					text: this.msg("msg.wait_contractor_sign"),
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
								loadingPopup.destroyWithAnimationsStop();
//								window.location.reload();
							}

							loadingPopup.destroyWithAnimationsStop();

							// Выходим, если всё хорошо
							if(result.gateResponse.responseType == "OK") {
								if (result.signatures.length > 1) {
									message = this.msg("msg.get_signs_success");
								} else if (result.signatures.length) {
									message = this.msg("msg.get_sign_success");
								} else {
									message = this.msg("msg.no_new_signs");
								}
								loadingPopup = Alfresco.util.PopupManager.displayMessage({ text: message });
								YAHOO.lang.later(2500, null, hideAndReload);

								if(YAHOO.lang.isFunction(signatureHandler)) {
									signatureHandler.call(scope || window, contentRef, result);
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
										submitType: "json",
										showCaption: false
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
												CryptoApplet.unicloudAuth({successCallback:{fn: makeDataRequest, scope: this}});
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
								return;
							} else {
								Alfresco.util.PopupManager.displayPrompt({
									title: this.msg("title.get_contractor_signs_failed"),
									text: YAHOO.lang.substitute(this.msg("tmpl.get_contractor_signs_error_text"), result.gateResponse)
								});
							}
						}
					},
					failureCallback: {
						fn: function() {
							loadingPopup.destroyWithAnimationsStop();
							Alfresco.util.PopupManager.displayMessage({
								text: this.msg("msg.get_signs_failed")
							});
						}
					}
				});
			};
		},

		_loadSignaturesToRepo: function(contentRef, result) {
			var i,
				signatures = result.signatures,
				signaturesLength = signatures.length,
				iReadState = YAHOO.util.Dom.get(this.id + "-readState").getElementsByTagName("i")[0],
				iReadStateElem = new YAHOO.util.Element(iReadState),
				spanReceivedCount = YAHOO.util.Dom.get(this.id + "-receivedCount");

			spanReceivedCount.innerHTML = signaturesLength;
			iReadStateElem.addClass(result.isRead ? "icon-ok" : "icon-remove");

			for(i = 0; i < signaturesLength; i++) {
				CryptoApplet.loadSignFromString(contentRef, signatures[i]);
			}
		},

		onRefreshSentDocuments: function(event) {
			var templateUrl = "lecm/signed-docflow/getSignedContentFromPartner?nodeRef={nodeRef}",
				url = YAHOO.lang.substitute(templateUrl, {
					nodeRef: this.options.nodeRef
				}),
				makeDataRequest;

			makeDataRequest = this._bindAjaxTo("GET", url, {}, this, this._loadSignaturesToRepo);
			makeDataRequest();
		}
	}, true);
})();
