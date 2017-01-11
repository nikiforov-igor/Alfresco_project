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

	LogicECM.ContentSigning = function(htmlId) {
		LogicECM.ContentSigning.superclass.constructor.call(this, htmlId);

		return this;
	};

	YAHOO.extend(LogicECM.ContentSigning, Alfresco.component.Base);

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
					signedContentRef: this.options.nodeRef,
					showCaption: false
				},
				destroyOnHide: true,
				doBeforeDialogShow: {
					fn: function(p_form, p_dialog) {
						p_dialog.dialog.setHeader(Alfresco.util.message('lecm.signdoc.ttl.view.sign.info'));
                        p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id, force: true}, this);
						p_form.doBeforeFormSubmit = {
							fn: function() {
								this.setAJAXSubmit(false);
                                p_dialog.hide();
							},
							scope: p_form
						};
					}
				},
				onFailure: {
					fn: function() {
						Alfresco.util.PopupManager.displayMessage({
							text: Alfresco.util.message('lecm.signdoc.msg.get.sign.info.error')
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
			CryptoApplet.updateSignsAction(this.options.nodeRef, {
				successCallback: {
					fn: this.onViewSignature,
					scope: this
				}
			});
		},
		onUploadSignature: function(event) {
			CryptoApplet.loadSignAction(this.options.nodeRef);
		},
		onExportSignature: function(event) {
			CryptoApplet.exportSignAction(this.options.nodeRef);
		},
		onSendDocument: function() {

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
						formId: "send-to-contractor",
						showCaption: false
					},
					destroyOnHide: true,
					contentRef: this.options.nodeRef,
					doBeforeDialogShow: {
						fn: function(form, dialog) {
							dialog.dialog.setHeader(Alfresco.util.message('lecm.signdoc.ttl.send.to.counterp'));
						}
					},
					onFailure: {
						fn: function() {
							Alfresco.util.PopupManager.displayMessage({
								text: Alfresco.util.message('lecm.signdoc.msg.get.subm.form.failed')
							});
						}
					}
				});

				form.show();
			}

			CryptoApplet.CheckSignaturesByNodeRefList(this.options.nodeRef, {
				successCallback: {
					scope: this,
					fn: showForm
				}
			});
		},
		onSignaturesReceived: function(event) {
			CryptoApplet.updateSignsAction(this.options.nodeRef, {
				successCallback: {
					fn: this.onViewSignature,
					scope: this
				}
			});
		},
		_bindAjaxTo: function(method, url, dataObj, scope, signatureHandler) {
			var id = this.newId,
				contentRef = this.options.nodeRef;

			return function makeDataRequest() {

				var loadingPopup = Alfresco.util.PopupManager.displayMessage({
					text: Alfresco.util.message('lecm.signdoc.msg.sign.requested'),
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
							}

							loadingPopup.destroyWithAnimationsStop();

							// Выходим, если всё хорошо
							if (result.gateResponse.responseType === "OK") {
								if (result.signatures.length > 1) {
									message = Alfresco.util.message('lecm.signdoc.msg.signs.obtained');
								} else if (result.signatures.length) {
									message = Alfresco.util.message('lecm.signdoc.msg.sign.obtained');
								} else {
									message = Alfresco.util.message('lecm.signdoc.msg.no.new.signs');
								}
								loadingPopup = Alfresco.util.PopupManager.displayMessage({text: message});
								YAHOO.lang.later(2500, null, hideAndReload);

								if (YAHOO.lang.isFunction(signatureHandler)) {
									signatureHandler.call(scope || window, contentRef, result);
								}

								return;
							}

							if (result.gateResponse.responseType === "UNAUTHORIZED") {
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
									doBeforeDialogShow: {
										fn: function(form, simpleDialog) {
											simpleDialog.dialog.setHeader(Alfresco.util.message('lecm.signdoc.msg.auth.req'));
										}
									},
									doBeforeAjaxRequest: {
										fn: function() {
											CryptoApplet.unicloudAuth({
												successCallback: {
													fn: makeDataRequest,
													scope: this
												}
											});
											return false;
										}
									},
									onFailure: {
										fn: function() {
											Alfresco.util.PopupManager.displayMessage({
												text: Alfresco.util.message('lecm.signdoc.msg.open.auth.form.failed')
											});
										}
									}
								});

								authSimpleDialog.show();
								return;
							} else {
								Alfresco.util.PopupManager.displayPrompt({
									title: Alfresco.util.message('lecm.signdoc.msg.get.signs.counter.failed'),
									text: YAHOO.lang.substitute(Alfresco.util.message('lecm.signdoc.msg.error.code') + ": {responseType}. {message}", result.gateResponse)
								});
							}
						}
					},
					failureCallback: {
						fn: function() {
							loadingPopup.destroyWithAnimationsStop();
							Alfresco.util.PopupManager.displayMessage({
								text: Alfresco.util.message('lecm.signdoc.msg.get.signs.failed')
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
			for (i = 0; i < signaturesLength; i++) {
				CryptoApplet.loadSignFromString(contentRef, signatures[i]);
			}
		},
		_getSignedContentFromPartner: function(response) {
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

				makeDataRequest = this._bindAjaxTo("GET", url, {}, this, this._loadSignaturesToRepo);
				makeDataRequest();
			} else {
				Alfresco.util.PopupManager.displayPrompt({
					title: Alfresco.util.message('lecm.signdoc.msg.get.signs.counter.failed'),
					text: Alfresco.util.message('lecm.signdoc.msg.doc.not.involved')
				});
			}
		},
		onRefreshSentDocuments: function(event) {

			//получаем из ноды информацию по тому контрагенту с которым мы взаимодействуем
			Alfresco.util.Ajax.jsonRequest({
				url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/signed-docflow/getContractorInfoBySendedContent",
				dataObj: {
					nodeRef: this.options.nodeRef
				},
				successCallback: {
					fn: this._getSignedContentFromPartner,
					scope: this
				},
				failureCallback: {
					scope: this,
					fn: function(response) {
						Alfresco.util.PopupManager.displayMessage({
							text: Alfresco.util.message('lecm.signdoc.msg.get.counterp.data.failed')
						});
					}
				}
			});

		}
	}, true);
})();
