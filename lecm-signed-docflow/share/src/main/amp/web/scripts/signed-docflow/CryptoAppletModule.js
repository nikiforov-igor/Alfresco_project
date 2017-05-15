if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

function checkForApplet() {
	/*if (!document.getElementsByName('signApplet')) {*/
    if (!document.getElementsByName('cadesplugin')) {
		Alfresco.util.PopupManager.displayMessage({
			text: Alfresco.util.message('lecm.signdoc.msg.applet.not.found')
		});
		return false;
	}
	return true;
}


(function () {
	var currentSigningCert = null,
        sTSAAddress = "",
		config = {
			tsPolicy: null,
			proxyPass: null,
			proxyUser: null,
			proxyHost: null,
			proxyPort: null,
			decryptNoDec: null,
			tsURL: 'http://www.cryptopro.ru/tsp/tsp.srf',
			licKey: 'MIICQzCCAfCgAwIBAgIQaYQDKGqmddWbpGNWItSd5f0zAKBgYqhQMCAgMFADBlMSAwHgYJKoZIhvcNAQkBFhFpbmZvQGNyeXB0b3Byby5ydTELMAkGA1UEBhMCUlUxEzARBgNVBAoTCkNSWVBUTy1QUk8xHzAdBgNVBAMTFlRlc3QgQ2VudGVyIENSWVBUTy1QUk8wHhcNMDkwNDA3MTIwMjE1WhcNMTQxMDA0MDcwOTQxWjBlMSAwHgYJKoZIhvcNAQkBFhFpbmZvQGNyeXB0b3Byby5ydTELMAkGA1UEBhMCUlUxEzARBgNVBAoTCkNSWVBUTy1QUk8xHzAdBgNVBAMTFlRlc3QgQ2VudGVyIENSWVBUTy1QUk8wYzAcBgYqhQMCAhMwEgYHKoUDAgIjAQYHKoUDAgIeAQNDAARAAuT/0ab2nICa2ux/SnjBzC3T5Zbqy+0iMnmyAuLGfDXmdGQbCXcRjGc/D9DoI6Z+bTt/xMQo/SscaAEgoFzYeaN4MHYwCwYDVR0PBAQDAgHGMA8GA1UdEwEB/wQFMAMBAf8wHQYDVR0OBBYEFG2PXgXZX6yRF5QelZoFMDg3ehAqMBIGCSsGAQQBgjcVAQQFAgMCAAIwIwYJKwYBBAGCNxUCBBYEFHrJxwnbIByWlC/8Rq1tk9BeaRIOMAoGBiqFAwICAwUAA0EAWHPSk7xjIbEOc3Lu8XK1G4u7yTsIu0xa8uGlNU+ZxNVSUnAm3a7QqSfptlt9b0T9Jk39oWN0XHTYSXMKd3djTQ==',
			password: '123',
			signatureType: 'PKCS7DET',
			validatorPath: 'https://www.cryptopro.ru/ocsp/ocsp.srf',
			provType: 'CSP_CRYPTOPRO',
			userName: '1',
			enableTS: 'false',
			issuerCert: 'MIICQzCCAfCgAwIBAgIQaYQDKGqmWbpGNWItSd5f0zAKBgYqhQMCAgMFADBlMSAwHgYJKoZIhvcNAQkBFhFpbmZvQGNyeXB0b3Byby5ydTELMAkGA1UEBhMCUlUxEzARBgNVBAoTCkNSWVBUTy1QUk8xHzAdBgNVBAMTFlRlc3QgQ2VudGVyIENSWVBUTy1QUk8wHhcNMDkwNDA3MTIwMjE1WhcNMTQxMDA0MDcwOTQxWjBlMSAwHgYJKoZIhvcNAQkBFhFpbmZvQGNyeXB0b3Byby5ydTELMAkGA1UEBhMCUlUxEzARBgNVBAoTCkNSWVBUTy1QUk8xHzAdBgNVBAMTFlRlc3QgQ2VudGVyIENSWVBUTy1QUk8wYzAcBgYqhQMCAhMwEgYHKoUDAgIjAQYHKoUDAgIeAQNDAARAAuT/0ab2nICa2ux/SnjBzC3T5Zbqy+0iMnmyAuLGfDXmdGQbCXcRjGc/D9DoI6Z+bTt/xMQo/SscaAEgoFzYeaN4MHYwCwYDVR0PBAQDAgHGMA8GA1UdEwEB/wQFMAMBAf8wHQYDVR0OBBYEFG2PXgXZX6yRF5QelZoFMDg3ehAqMBIGCSsGAQQBgjcVAQQFAgMCAAIwIwYJKwYBBAGCNxUCBBYEFHrJxwnbIByWlC/8Rq1tk9BeaRIOMAoGBiqFAwICAwUAA0EAWHPSk7xjIbEOc3Lu8XK1G4u7yTsIu0xa8uGlNU+ZxNVSUnAm3a7QqSfptlt9b0T9Jk39oWN0XHTYSXMKd3djTQ==',
			licCert: 'MIICQzCCAfCgAwIBAgddIQaYQDKGqmWbpGNWItSd5f0zAKBgYqhQMCAgMFADBlMSAwHgYJKoZIhvcNAQkBFhFpbmZvQGNyeXB0b3Byby5ydTELMAkGA1UEBhMCUlUxEzARBgNVBAoTCkNSWVBUTy1QUk8xHzAdBgNVBAMTFlRlc3QgQ2VudGVyIENSWVBUTy1QUk8wHhcNMDkwNDA3MTIwMjE1WhcNMTQxMDA0MDcwOTQxWjBlMSAwHgYJKoZIhvcNAQkBFhFpbmZvQGNyeXB0b3Byby5ydTELMAkGA1UEBhMCUlUxEzARBgNVBAoTCkNSWVBUTy1QUk8xHzAdBgNVBAMTFlRlc3QgQ2VudGVyIENSWVBUTy1QUk8wYzAcBgYqhQMCAhMwEgYHKoUDAgIjAQYHKoUDAgIeAQNDAARAAuT/0ab2nICa2ux/SnjBzC3T5Zbqy+0iMnmyAuLGfDXmdGQbCXcRjGc/D9DoI6Z+bTt/xMQo/SscaAEgoFzYeaN4MHYwCwYDVR0PBAQDAgHGMA8GA1UdEwEB/wQFMAMBAf8wHQYDVR0OBBYEFG2PXgXZX6yRF5QelZoFMDg3ehAqMBIGCSsGAQQBgjcVAQQFAgMCAAIwIwYJKwYBBAGCNxUCBBYEFHrJxwnbIByWlC/8Rq1tk9BeaRIOMAoGBiqFAwICAwUAA0EAWHPSk7xjIbEOc3Lu8XK1G4u7yTsIu0xa8uGlNU+ZxNVSUnAm3a7QqSfptlt9b0T9Jk39oWN0XHTYSXMKd3djTQ==',
			storeName: '123',
			validatorType: 'NONE',
			tmpPath: 'c\:\\tmp',
			storeType: 'HDImageStore',
			certB64: '1'
		};

	LogicECM.CryptoApplet = function (htmlId) {
		var component = Alfresco.util.ComponentManager.find({name: 'LogicECM.CryptoApplet'});
		if (component.length) {
			return component[0];
		}

		LogicECM.CryptoApplet.superclass.constructor.call(this, 'LogicECM.CryptoApplet', htmlId);
		try {
			this.loadConfig();
			/*signApplet.setConfig(config);*/
		} catch (ex) {
			console.log(ex);
		}
		this.id = htmlId;
		this.name = 'LogicECM.CryptoApplet';

		return this;
	};

	YAHOO.extend(LogicECM.CryptoApplet, Alfresco.component.Base,
		{
			/*
			 ==========================================================
			 options = {
			 successCallback: {
			 fn: someFunction,
			 scope: someScope
			 },
			 failureCallback: {
			 fn: someFunction,
			 scope: someScope
			 },
			 doBeforeAjaxCallback: {
			 fn: someFunction,
			 scope: someScope
			 }
			 }
			 ==========================================================
			 */

			/*
			 ==========================================================
			 Подписание
			 ==========================================================
			 */

            signPerformed: false,

			signContent: function (nodeRefList, form) {

                if (this.signPerformed) {
                    return true;
                }

                var i,
					signatures = [],
					sign;

                if (!YAHOO.lang.isArray(nodeRefList)) {
					nodeRefList = [nodeRefList];
				}


                var signCallBack = function(context, errormessage, result) {
                    try {
                        if (errormessage) {
                            Alfresco.util.PopupManager.displayMessage({
                                text: errormessage
                            });
                        } else {
                            if (nodeRefList.length === result.signResult.length) {
                                for (i = 0; i < nodeRefList.length; i++) {
                                    sign = new Signature(currentSigningCert, nodeRefList[i], result.signResult[i]);
                                    if (sign && sign.valid) {
                                        signatures.push(sign);
                                    }
                                }
                            }
                        }

                        if (!signatures.length) {
							Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.create.sign.failed')});
							form.url = null;
							return false;
						}
                        else
                        {
                            form.url = Alfresco.constants.PROXY_URI + 'lecm/signed-docflow/signContent';
                        }
						for (i = 0; i < signatures.length; i++) {
							form.dataObj.push(signatures[i].getJSONInfo());
						}
                    } catch(ex) {
                        Alfresco.util.PopupManager.displayMessage({
                            text: Alfresco.util.message("message.setting.signable.failure")
                        });
                    }
                    context.signPerformed = true;
                    Alfresco.util.Ajax.jsonRequest(form);
                };

                Alfresco.util.Ajax.jsonRequest({
                    method: "POST",
                    url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/signed-docflow/getHashes",
                    dataObj: nodeRefList,
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
                            var docs = response.json;

                            if (!this.useNPAPI) {
                                SignES6Hashes(currentSigningCert.thumbprint, docs, sTSAAddress, signCallBack, this);
                            }
                            else {
                                SignHashes_NPAPI(currentSigningCert.thumbprint, docs, sTSAAddress, signCallBack, this);
                            }
                        },
                        scope: this
                    }
                });
                /*
                var intervalID;

                var waitSignature = function(context) {
                    if (context.signPerformed) {
                        Alfresco.util.Ajax.jsonRequest(form);
                        clearInterval(intervalID);
                    }
                };

                intervalID = setInterval(waitSignature(this), 200);
                */
				return false;
			},
			signAction: function (nodeRefList, options, parentDialog) {

				if (!YAHOO.lang.isArray(nodeRefList)) {
					nodeRefList = [nodeRefList];
				}

                var signform = {};

				this.loadCertsForm({
					parentDialog: parentDialog,
					title: Alfresco.util.message('lecm.signdoc.msg.signing'),
					actionURL: ""/*Alfresco.constants.PROXY_URI + 'lecm/signed-docflow/signContent'*/,
					doBeforeAjaxCallback: {
						scope: this,
						fn: function (form) {
							var signatures, i;

							form.dataObj = [];
                            this.signPerformed = false;
							var action = this.signContent(nodeRefList, form);
                            return action;
						}
					},
					successCallback: {
						scope: this,
						fn: function (response) {
							var i, cb,
								badResult = '',
								message;

							for (i = 0; i < response.json.length; i++) {
								if (response.json[i].signResponse === 'SIGN_ALREADY_EXIST') {
									badResult += response.json[i].name + '</br>';
								}
							}

							if (badResult !== '') {
								Alfresco.util.PopupManager.displayPrompt({
									title: Alfresco.util.message('lecm.signdoc.msg.validation.error'),
									text: Alfresco.util.message('lecm.signdoc.msg.signing.ok.files.already.sign') + ':<br/>' + badResult,
									noEscape: true
								});
							} else {
								message = (response.json.length > 1) ? Alfresco.util.message('lecm.signdoc.msg.docs.subscribed') : Alfresco.util.message('lecm.signdoc.msg.doc.subscribed');
								Alfresco.util.PopupManager.displayMessage({text: message});
							}

							cb = (options) ? options.successCallback : null;
							if (cb && YAHOO.lang.isFunction(cb.fn)) {
								cb.fn.apply(cb.scope, cb.obj);
							}
						}
					},
					failureCallback: {
						scope: this,
						fn: function () {
							Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.sign.send.error')});
						}
					}

				});

			},
			signMultAction: function (docNodeRef, options) {

				this.loadMultipleForm({
					docNodeRef: docNodeRef,
					title: Alfresco.util.message('lecm.signdoc.msg.signing'),
					actionURL: null,
					htmlId: this.id,
					doBeforeAjaxCallback: {
						fn: this.signAction,
						scope: this,
						obj: options
					}
				});
			},
			/*
			 ==========================================================
			 Обновление информации о подписях
			 ==========================================================
			 */

			updateSignsAction: function (nodeRef, options) {

				function updateRequest(signs) {
					var i, checkResult = [];

					for (i = 0; i < signs.length; i++) {
						checkResult.push(signs[i].getCheckInfo());
					}

					Alfresco.util.Ajax.jsonRequest({
						method: 'POST',
						url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/signed-docflow/signing/update',
						dataObj: checkResult,
						successCallback: {
							scope: this,
							fn: function (response) {

								var resText = '',
									success = true,
									cb, i;

								for (i = 0; i < response.json.length; i++) {
									if (response.json[i].result !== 'success')
										success = false;
								}

								if (!success) {
									resText = Alfresco.util.message('lecm.signdoc.msg.some.sign.refresh.failed');
								} else {
									resText = Alfresco.util.message('lecm.signdoc.msg.all.signs.updated');
								}

								Alfresco.util.PopupManager.displayMessage({text: resText});

								cb = (options) ? options.successCallback : null;
								if (cb && YAHOO.lang.isFunction(cb.fn)) {
									cb.fn.apply(cb.scope);
								}
							}
						},
						failureCallback: {
							scope: this,
							fn: function () {
								Alfresco.util.PopupManager.displayMessage({
									text: Alfresco.util.message('lecm.signdoc.msg.signs.update.failed')
								});
							}
						}
					});
				}

				var opts = {
					successCallback: {
						scope: this,
						fn: updateRequest
					}
				};

				this.getSignsByNodeRef(nodeRef, opts);
			},
			/*
			 ==========================================================
			 Получений подписей контента или документа
			 ==========================================================
			 */

			getSignsByNodeRef: function (nodeRef, options) {

				Alfresco.util.Ajax.jsonRequest({
					method: 'GET',
					url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/signed-docflow/getDocumentSignsInfo?nodeRef=' + nodeRef,
					successCallback: {
						scope: this,
						fn: function (response) {
							var signs = [],
								sign, cb, i, j, k;
							//[!]
							for (i = 0; i < response.json.length; i++) {
								for (j = 0; j < response.json[i].signedContent.length; j++) {
									for (k = 0; k < response.json[i].signedContent[j].signsInfo.length; k++) {
										sign = new SignatureFromContent(response.json[i].signedContent[j].nodeRef, response.json[i].signedContent[j].signsInfo[k].signature, response.json[i].signedContent[j].signsInfo[k].nodeRef, response.json[i].signedContent[j].contentHash);
										signs.push(sign);
									}
								}
							}
							
							var partialContentSignature = signs.map(function (sign) {
								return {
									hash: sign.contentHash,
									signedMessage: sign.signature
								}
							});

							verifySignaturesSync(partialContentSignature, function (results) {
								signs.forEach(function (sign, index) {
									sign.valid = results[index].valid;
								});

								cb = (options) ? options.successCallback : null;
								if (cb && YAHOO.lang.isFunction(cb.fn)) {
									cb.fn.call(cb.scope, signs);
								}
							});
						}
					},
					failureCallback: {
						scope: this,
						fn: function () {
							var cb;

							Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.get.sings.info.failed')});

							cb = (options) ? options.failureCallback : null;
							if (cb && YAHOO.lang.isFunction(cb.fn)) {
								cb.fn.apply(cb.scope);
							}
						}
					}
				});
			},
			/*
			 ==========================================================
			 Конфиг и всё такое прочее
			 ==========================================================
			 */

			setCurrentSigningCert: function (cert) {
				currentSigningCert = cert;
				/*config.certB64 = cert.getBase64();*/
				/*signApplet.setConfig(config);*/
			},
            setSTSAAddress: function(paramsTSAAddress) {
                sTSAAddress = paramsTSAAddress;
            },
			getCurrentSigningCert: function () {
				return currentSigningCert;
			},
			//[delete?]
			checkForApplet: function () {
				/*if (!document.getElementsByName('signApplet')) {*/
                if (!document.getElementsByName('cadesplugin')) {
					Alfresco.util.PopupManager.displayMessage({
						text: Alfresco.util.message('lecm.signdoc.msg.applet.not.found')
					});
					return false;
				}
				return true;
			},
			//[delete?]
			getConfig: function () {
				return config;
			},
			loadConfig: function () {
				Alfresco.util.Ajax.jsonRequest({
					method: 'POST',
					url: Alfresco.constants.PROXY_URI_RELATIVE + '/lecm/signed-docflow/config/applet',
					dataObj: {action: 'get'},
					successCallback: {
						fn: function (response) {
							var configRes = response.json;
							config.licKey = configRes.licKey;
							config.storeName = configRes.storeName;
							config.licCert = configRes.licCert;
							config.issuerCert = configRes.licCert;
							/*signApplet.setConfig(config);*/
						}
					},
					failureMessage: Alfresco.util.message('lecm.signdoc.msg.applet.config.load.error')
				});
			},
			getCerts: function () {
				var certs = [],
					i, cert,
					containers = signApplet.getService().getKeyStoreList().split('###');

				for (i = 0; i < containers.length; i++) {
					cert = null;
					cert = new Certificate(containers[i]);
					if (cert && cert.getBase64() && cert.isValid()) {
						certs.push(cert);
					}
				}
				return certs;
			},
			//[delete?]
			checkSignatures: function (signatures) {
				var result = true;
				if (!YAHOO.lang.isArray(signatures)) {
					signatures = [signatures];
				}
				for (var i = 0; i < signatures.length; i++) {
					if (!signatures[i].validate()) {
						Alfresco.util.PopupManager.displayMessage({
							text: Alfresco.util.message('lecm.signdoc.msg.invalid.signature')
						});
						result = false;
					}
				}
				return result;

			},
			/*
			 ==========================================================
			 Загрузка подписи
			 ==========================================================
			 */

			loadSignAction: function (nodeRef, clientLocalSign,  options) {
				function loadRequest (signature){
					var dataObj = {};
					if (signature.getStatus()) {
					    dataObj = signature.getJSONInfo();

					    Alfresco.util.Ajax.jsonRequest({
						    method: 'POST',
						    url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/signed-docflow/loadSign',
						    dataObj: dataObj,
						    successCallback: {
							    scope: this,
							    fn: function (response) {
								    var text = (response.json.signResponse === 'SIGN_OK') ? Alfresco.util.message('lecm.signdoc.msg.signature.loaded') : Alfresco.util.message('lecm.signdoc.msg.sign.tested.download.failed'),
									    cb = (options) ? options.successCallback : null;
								    Alfresco.util.PopupManager.displayMessage({text: text});

								    if (cb && YAHOO.lang.isFunction(cb.fn)) {
									    cb.fn.apply(cb.scope, [response]);
								    }
							    }
						    },
						    failureCallback: {
							    scope: this,
							    fn: function () {
								    Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.load.sign.failed')});

								    var cb = (options) ? options.failureCallback : null;
								    if (cb && YAHOO.lang.isFunction(cb.fn)) {
									    cb.fn.apply(cb.scope);
								    }
							    }
						    }
					    });
				    } else {
					    Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.not.valid.sign.cancel')});
				    }
				}
				
				function verifySign(fileHash){
					signature = new SignatureFromFile(nodeRef, fileHash, clientLocalSign);

					var partialContentSignature = [];
					partialContentSignature[0]= {
							hash: fileHash,
							signedMessage: signature.signatureContent
						};
					verifySignaturesSync(partialContentSignature, function (results) {
						signature.valid = results[0].valid;
						GetCertificateInfo(results[0].certificate, function(result){
							signature.certificate = result;
							loadRequest(signature);
						});
					});
				}
				
				var opts = {
					successCallback: {
						scope: this,
						fn: verifySign
					}
				};
				
				this.getFileHash(nodeRef,  clientLocalSign, opts);

			},
			getFileHash: function(nodeRef, clientLocalSign, opts){
				Alfresco.util.Ajax.jsonRequest({
					method: 'GET',
					url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/signed-docflow/getDocumentSignsInfo?nodeRef=' + nodeRef,
					successCallback: {
						scope: this,
						fn: function (response) {
							var cb;
							hash = response.json[0].signedContent[0].contentHash;

							cb = (opts) ? opts.successCallback : null;
								if (cb && YAHOO.lang.isFunction(cb.fn)) {
									cb.fn.apply(cb.scope, [hash]);
							}
						}
					},
					failureCallback: {
						scope: this,
						fn: function () {
							Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.get.sings.info.failed')});
						}
					}
				});
			},
			exportSignAction: function (nodeRef) {
				document.location.href = Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/signed-docflow/getZip?nodeRef=' + nodeRef;
			},
			loadSignFromString: function (nodeRef, signatureContent, options) {
				var dataObj = {},
					signature = new SignatureFromContent(nodeRef, signatureContent, null);

				if (signature.getStatus()) {
					dataObj = signature.getJSONInfo();

					Alfresco.util.Ajax.jsonRequest({
						method: 'POST',
						url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/signed-docflow/loadSign',
						dataObj: dataObj,
						successCallback: {
							scope: this,
							fn: function (response) {
								var text = (response.json.signResponse === 'SIGN_OK') ? Alfresco.util.message('lecm.signdoc.msg.signature.loaded') : Alfresco.util.message('lecm.signdoc.msg.sign.tested.download.failed'),
									cb = (options) ? options.successCallback : null;
								Alfresco.util.PopupManager.displayMessage({text: text});

								if (cb && YAHOO.lang.isFunction(cb.fn)) {
									cb.fn.apply(cb.scope, [response]);
								}
							}
						},
						failureCallback: {
							scope: this,
							fn: function () {
								Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.load.sign.failed')});

								var cb = (options) ? options.failureCallback : null;
								if (cb && YAHOO.lang.isFunction(cb.fn)) {
									cb.fn.apply(cb.scope);
								}
							}
						}
					});
				} else {
					Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.not.valid.sign.cancel')});
				}
			},
			/*
			 ==========================================================
			 Проверка подписей контента(листа)
			 ==========================================================
			 */

			CheckSignaturesByNodeRefList: function (nodeRefList, options) {

				if (!(nodeRefList instanceof Array)) {
					nodeRefList = [nodeRefList];
				}


				function successCallback(response) {
					var i, j, cb,
						contentSuccess = [],
						contentFailure = [],
						nodeRefList = [],
						signatures,
						signaturesStatus,
						sign,
						contentNodeRef,
						contentName,
						signatureNodeRef,
						signatureContent,
						template,
						badContent = '';

					for (i = 0; i < response.json.length; i++) {
						contentNodeRef = response.json[i].signedContentNodeRef;
						contentName = response.json[i].signedContentName;

						signatures = [];
						signaturesStatus = true;

						for (j = 0; j < response.json[i].signatures.length; j++) {

							signatureNodeRef = response.json[i].signatures[j].nodeRef;
							signatureContent = response.json[i].signatures[j].signatureContent;

							sign = new SignatureFromContent(contentNodeRef, signatureContent, signatureNodeRef);
							signatures.push(sign);
							if (!sign.getStatus()) {
								signaturesStatus = false;
							}
						}

						if (!signaturesStatus) {
							contentFailure.push({
								'signedContentNodeRef': contentNodeRef,
								'signedContentName': contentName
							});
						} else {
							contentSuccess.push({
								'signedContentNodeRef': contentNodeRef,
								'signedContentName': contentName
							});
						}

					}

					for (i = 0; i < contentFailure.length; i++) {
						template = '<a style=align: left; href=/share/page/document-attachment?nodeRef={signedContentNodeRef}>{signedContentName}</a><br/>';
						badContent += YAHOO.lang.substitute(template, contentFailure[i]);
					}

					if (badContent) {
						Alfresco.util.PopupManager.displayPrompt({
							title: Alfresco.util.message('lecm.signdoc.msg.invalid.file.signature'),
							text: Alfresco.util.message('lecm.signdoc.msg.files.verify.failed') + ':<br/>' + badContent,
							noEscape: true
						});
					}

					// Alfresco.util.PopupManager.displayMessage({	text: 'Подписи успешно прошли проверку' });

					for (i = 0; i < contentSuccess.length; i++) {
						nodeRefList.push(contentSuccess[i].signedContentNodeRef);
					}

					cb = (options) ? options.successCallback : null;
					if (cb && YAHOO.lang.isFunction(cb.fn)) {
						cb.fn.apply(cb.scope, [nodeRefList]);
					}

				}


				Alfresco.util.Ajax.jsonRequest({
					method: 'POST',
					url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/signed-docflow/getSignsInfo',
					dataObj: nodeRefList,
					successCallback: {
						fn: successCallback,
						scope: this
					}
				});
			},
			/*
			 ==========================================================
			 Аутенфикация
			 ==========================================================
			 */

			unicloudAuth: function (options) {

				var GUIDsign = signApplet.sign('GUID', 'String');
				var TS = new Date();
				var TSsign = signApplet.sign(TS.toString('yyyy-MM-dd hh:mm'), 'String');
				var dataObj = {'guidSign': GUIDsign, 'timestamp': TS.toString('yyyy-MM-dd hh:mm'), 'timestampSign': TSsign};
				var loadingPopup = Alfresco.util.PopupManager.displayMessage({
					text: Alfresco.util.message('lecm.signdoc.lbl.authentication'),
					spanClass: 'wait',
					displayTime: 0,
					modal: true
				});
				loadingPopup.center();
				Alfresco.util.Ajax.jsonRequest({
					method: 'POST',
					url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/signed-docflow/unicloud/api/authenticateByCertificate',
					dataObj: dataObj,
					successCallback: {
						scope: this,
						fn: function (response) {
							var status = response.json.gateResponse.responseType,
								cb = (options) ? options.successCallback : null;

							loadingPopup.destroyWithAnimationsStop();

							if (status === 'OK') {

								if (cb && YAHOO.lang.isFunction(cb.fn)) {
									cb.fn.apply(cb.scope, cb.obj);
								}

							} else {
								loadingPopup = Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.auth.failed')});
								YAHOO.lang.later(2500, loadingPopup, loadingPopup.destroyWithAnimationsStop);
							}
						}
					},
					failureCallback: {
						scope: this,
						fn: function () {
							loadingPopup.destroyWithAnimationsStop();

							loadingPopup = Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.auth.failed')});
							YAHOO.lang.later(2500, loadingPopup, loadingPopup.destroyWithAnimationsStop);
						}
					}

				});
			},
			authenticateAction: function (options) {
				this.loadCertsForm({
					title: Alfresco.util.message('lecm.signdoc.lbl.authentication'),
					actionURL: null,
					doBeforeAjaxCallback: {
						scope: this,
						fn: function () {
							this.unicloudAuth(options);
						}
					}
				});

			},
			/*
			 ==========================================================
			 Отправка контрагенту DELETE?
			 ==========================================================
			 */

			sendToPartnerAction: function (nodeRefList) {

				if (!YAHOO.lang.isArray(nodeRefList)) {
					nodeRefList = [nodeRefList];
				}

				function checkCallback(nodeRefList) {
					Alfresco.util.Ajax.jsonRequest({
						method: 'POST',
						url: Alfresco.constants.PROXY_URI_RELATIVE + '/lecm/signed-docflow/sendContentToPartner',
						dataObj: {
							content: nodeRefList
						},
						successCallback: {
							scope: this,
							fn: function (response) {
								var i,
									gateResponse,
									success = true;

								for (i = 0; i < response.json.length; i++) {
									gateResponse = response.json[i].gateResponse;
									if (gateResponse.responseType !== 'OK') {
										success = false;
										console.log(gateResponse.message);
									}
								}

								if (success) {
									Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.docs.sent')});
								} else {
									Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.some.docs.not.sent')});
								}
							}
						},
						failureCallback: {
							scope: this,
							fn: function () {
								Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.sending.error')});
							}
						}
					});
				}

				var opts = {
					successCallback: {
						scope: this,
						fn: checkCallback,
						obj: [nodeRefList]
					}
				};

				this.CheckSignaturesByNodeRefList(nodeRefList, opts);

			},
			sendToPartnerActionMultiple: function (docNodeRef) {
				this.loadMultipleForm({
					docNodeRef: docNodeRef,
					title: Alfresco.util.message('lecm.signdoc.ttl.send.to.counterparty'),
					actionURL: null,
					htmlId: this.id,
					doBeforeAjaxCallback: {
						scope: this,
						fn: this.sendToPartnerAction
					}
				});
			},
			/*
			 ==========================================================

			 ==========================================================
			 */

			//delete?
			CheckSignatures: function (data) {
				var checkResult = [];
				for (var i = 0; i < data.length; i++) {
					var finalCheckResult = true;
					for (var j = 0; j < data[i].signatures.length; j++) {
						var signatures = [];
						var contentURI = new Alfresco.util.NodeRef(data[i].signedContentNodeRef).uri;
						var check = signApplet.check(Alfresco.constants.PROXY_URI + 'api/node/content/' + contentURI, 'URL', data[i].signatures[j].signatureContent).getResult();
						if (!check)
							finalCheckResult = false;
						signatures.push({
							signatureNodeRef: data[i].signatures[j].signatureNodeRef,
							isValid: check
						});
					}
					checkResult.push({
						signatures: signatures,
						contentRef: data[i].signedContentNodeRef,
						signedContentName: data[i].signedContentName,
						finalCheckResult: finalCheckResult
					});
				}
				return checkResult;
			},
			/*
			 ==========================================================
			 Формы
			 ==========================================================
			 */



			loadCertsForm: function (params) {

				var url = Alfresco.constants.URL_SERVICECONTEXT + 'components/form',
					form,
					templateRequestParams = {
						itemKind: 'type',
						itemId: 'lecm-orgstr:employees',
						mode: 'create',
						submitType: 'json',
						formId: 'auth-form',
						showCancelButton: true,
						showCaption: false
					};

				form = new Alfresco.module.SimpleDialog(params.htmlId);
				form.setOptions({
					parentDialog: params.parentDialog,
					width: '20em',
					templateUrl: url,
					templateRequestParams: templateRequestParams,
					actionUrl: params.actionURL,
					destroyOnHide: true,
					doAfterDialogHide: {
						scope: this,
						fn: function (p_form, p_dialog) {
							if (p_dialog.options.parentDialog) {
								p_dialog.options.parentDialog.hide();
							}
						}
					},
					doBeforeDialogShow: {
						scope: this,
						fn: function (p_form, p_dialog) {
							p_dialog.dialog.setHeader(params.title);
						}},
					doBeforeAjaxRequest: {
						scope: this,
						fn: function (form, obj) {
							if (!currentSigningCert) {
								Alfresco.util.PopupManager.displayMessage({
									text: Alfresco.util.message('lecm.signdoc.msg.must.select.cert')
								});
								return false;
							}

							var cb = (params) ? params.doBeforeAjaxCallback : null;
							if (cb && YAHOO.lang.isFunction(cb.fn)) {
								cb.fn.apply(cb.scope, [form]);
							}

							if (!params.actionURL) {
								return false;
							}
							return true;

						}
					},
					onSuccess: {
						scope: this,
						fn: function (response) {
							var cb = (params) ? params.successCallback : null;
							if (cb && YAHOO.lang.isFunction(cb.fn)) {
								cb.fn.apply(cb.scope, [response]);
							}
						}
					},
					onFailure: {
						scope: this,
						fn: function () {
							var cb = (params) ? params.failureCallback : null;
							if (cb && YAHOO.lang.isFunction(cb.fn)) {
								cb.fn.apply(cb.scope);
							}
						}
					}
				}).show();
			},
			loadMultipleForm: function (params) {
				
				function recoverButtonsState(dialog) {
					dialog.widgets.cancelButton.set("disabled", false);
					dialog.widgets.okButton.set("disabled", false);
				}
				
				function renderForm(response) {
					var templateRequestParams = {
						itemKind: 'type',
						itemId: 'lecm-orgstr:employees',
						mode: 'create',
						submitType: 'json',
						formId: 'multiple-sign-form',
						showCancelButton: 'true',
						showCaption: false,
						obj: JSON.stringify(response.json)
					};
					var url = Alfresco.constants.URL_SERVICECONTEXT + 'components/form';
					var dialog = new Alfresco.module.SimpleDialog(params.htmlId);
					dialog.setOptions({
						width: '30em',
						templateUrl: url,
						templateRequestParams: templateRequestParams,
						actionUrl: params.actionURL,
						destroyOnHide: true,
						doBeforeDialogShow: {
							scope: this,
							fn: function (p_form, p_dialog) {
								p_dialog.dialog.setHeader(params.title);
								p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
							}},
						doBeforeAjaxRequest: {
							scope: this,
							fn: function (form, obj) {
								var nodeRefList = [],
									fields = document.forms[params.htmlId + '-form'].getElementsByTagName('input'),
									cb = (params) ? params.doBeforeAjaxCallback : null;

								for (var i = 0; i < fields.length; i++) {
									if (fields[i].checked)
										nodeRefList.push(fields[i].value);
								}
								if (!nodeRefList.length) {
									Alfresco.util.PopupManager.displayMessage({
										text: Alfresco.util.message('lecm.signdoc.msg.must.select.one.or.more.docs')
									});
									recoverButtonsState(dialog);
									return false;
								}

								if (cb && YAHOO.lang.isFunction(cb.fn)) {
									cb.fn.apply(cb.scope, [nodeRefList, cb.obj, dialog]);
								}

								if (!params.actionURL) {
									return false;
								}
								return true;
							}
						},
						onSuccess: {
							scope: this,
							fn: function (response) {
								var cb = (params) ? params.successCallback : null;
								if (cb && YAHOO.lang.isFunction(cb.fn)) {
									cb.fn.apply(cb.scope, [response]);
								}
							}
						},
						onFailure: {
							scope: this,
							fn: function () {
								var cb = (params) ? params.failureCallback : null;
								if (cb && YAHOO.lang.isFunction(cb.fn)) {
									cb.fn.apply(cb.scope);
								}
							}
						}
					}).show();
				}

				Alfresco.util.Ajax.jsonRequest({
					method: 'GET',
					url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/signed-docflow/getSignableContent?nodeRef=' + params.docNodeRef,
					successCallback: {
						fn: renderForm,
						scope: this
					}
				});
			},
            onReady: function () {
                //this.initPanel();
                //this.getHtmlElements();
                try {
                    var js = [];
                    this.useNPAPI = !!!window.Promise || !!!cadesplugin.CreateObjectAsync ? true : false;
                    js.push(this.useNPAPI ? 'scripts/signed-docflow/Code.js' : 'scripts/signed-docflow/async_code.js');
                    this.loadJs(js);
                } catch (ex) {
					console.log(ex);
				}
            },
            loadJs: function (js) {
                LogicECM.module.Base.Util.loadScripts(js, function() {YAHOO.Bubbling.fire('onCryptoAppletInit');});
            },
            loadCertificates: function () {
                if (!this.useNPAPI) {
                    GetES6CertsJson(this);
                }
                else {
                    window.addEventListener("message", function (event) {
                        if (event.data === "cadesplugin_loaded") {
                            this.useNPAPI = true;
                        }
                    },
                    false);
                    window.postMessage("cadesplugin_echo_request", "*");
                    FillCertList_NPAPIJson(this);
                }
            }
            /*
             * @Deprecated
             *
			onReady: function () {
				try {
					signApplet.setConfig(config);
					this.loadConfig();
					signApplet.setConfig(config);
					YAHOO.Bubbling.fire('onCryptoAppletInit');
				} catch (ex) {
					console.log(ex);
				}
			}*/
		});
})();

/*-----------------------------------------------------------------------------------------------------*/
/*
 ==========================================================
 Подобие класса для сертификата
 ==========================================================
 */

function Certificate(containerName) {
	var Info,
		certIssued,
		tmp;

	this.owner = '';
	this.organization = '';
	this.OrgUnit = '';
	this.position = '';
	this.certSN = '';
	this.certValidBefore = '';
	this.certValidAfter = '';
	this.fingerprint = '';
	this.certIssuer = '';
	this.base64 = '';
	this.valid = false;
	this.validateDate = null;
	this.container = '';

	if (!containerName) {
		console.log('invalid container name');
		return null;
	}

	try {
		this.base64 = signApplet.getService().bytesToBase64(signApplet.getService().getCertFromStore('', containerName));
		Info = JSON.parse(signApplet.getService().certInfo(this.base64));
	} catch (e) {
		console.log('bad container');
		return null;
	}
	if (!this.base64 || !Info) {
		console.log('error while processing certificate');
		return null;
	}
	certIssued = Info.certIssued;
	this.container = containerName;

	tmp = certIssued.match('CN=(.+?)(?=,)'); //ФИО владельца
	if (tmp) {
		this.owner = tmp[1];
	} else {
		this.owner = ' ';
	}

	tmp = certIssued.match(' O=(.+?)(?=,)'); //Организация
	if (tmp) {
		this.organization = tmp[1];
	} else {
		this.organization = ' ';
	}

	tmp = certIssued.match(' OU=(.+?)(?=,)'); //Подразделение
	if (tmp) {
		this.OrgUnit = tmp[1];
	} else {
		this.OrgUnit = ' ';
	}

	tmp = certIssued.match(' T=(.+?)(?=,)'); //Должность
	if (tmp) {
		this.position = tmp[1];
	} else {
		this.position = ' ';
	}

	this.certSN = Info.certSN; //Серийный номер
	this.certValidBefore = Info.certValidBefore;
	this.certValidAfter = Info.certValidAfter;
	this.fingerprint = Info.certFingerPrint;

	tmp = Info.certIssuer.match(' O=(.+?)(?=,)'); //УЦ
	if (tmp) {
		this.certIssuer = tmp[1];
	} else {
		this.certIssuer = '';
	}

	//Доверяй, но проверяй!
	this.valid = signApplet.getAPI().certIsValid(this.base64);
}
/*
 ==========================================================
 Тот же сертификат, только создаётся из base64
 ==========================================================
 */
function CertificateFromBase64(base64) {
	var Info,
		certIssued,
		tmp;

	this.owner = '';
	this.organization = '';
	this.OrgUnit = '';
	this.position = '';
	this.certSN = '';
	this.certValidBefore = '';
	this.certValidAfter = '';
	this.fingerprint = '';
	this.certIssuer = '';
	this.base64 = base64;
	this.valid = false;
	this.validateDate = null;

	if (!base64) {
		Alfresco.util.PopupManager.displayMessage({text: 'base64 is empty'});
		return null;
	}

	try {
		Info = JSON.parse(signApplet.getService().certInfo(this.base64));
	} catch (e) {
		console.log('bad base64');
		return null;
	}

	if (!Info) {
		return null;
	}
	certIssued = Info.certIssued;

	tmp = certIssued.match('CN=(.+?)(?=,)'); //ФИО владельца
	if (tmp) {
		this.owner = tmp[1];
	} else {
		this.owner = ' ';
	}

	tmp = certIssued.match(' O=(.+?)(?=,)'); //Организация
	if (tmp) {
		this.organization = tmp[1];
	} else {
		this.organization = ' ';
	}

	tmp = certIssued.match(' OU=(.+?)(?=,)'); //Подразделение
	if (tmp) {
		this.OrgUnit = tmp[1];
	} else {
		this.OrgUnit = ' ';
	}

	tmp = certIssued.match(' T=(.+?)(?=,)'); //Должность
	if (tmp) {
		this.position = tmp[1];
	} else {
		this.position = ' ';
	}

	this.certSN = Info.certSN; //Серийный номер
	this.certValidBefore = Info.certValidBefore;
	this.certValidAfter = Info.certValidAfter;
	this.fingerprint = Info.certFingerPrint;

	tmp = Info.certIssuer.match(' O=(.+?)(?=,)'); //УЦ
	if (tmp) {
		this.certIssuer = tmp[1];
	} else {
		this.certIssuer = '';
	}

	//Доверяй, но проверяй!
	this.valid = signApplet.getAPI().certIsValid(this.base64);

}

Certificate.prototype = {
	getCertIssuer: function () {
		return this.certIssuer;
	},
	getOrganization: function () {
		return this.organization;
	},
	getOwner: function () {
		return this.owner;
	},
	getOrgUnit: function () {
		return this.OrgUnit;
	},
	getPosition: function () {
		return this.position;
	},
	getCertSN: function () {
		return this.certSN;
	},
	getCertValidBefore: function () {
		return this.certValidBefore;
	},
	getCertValidAfter: function () {
		return this.certValidAfter;
	},
	getFingerprint: function () {
		return this.fingerprint;
	},
	getBase64: function () {
		return this.base64;
	},
	getIssuer: function () {
		return this.issuer;
	},
	getJSON: function () {
		return {
			'owner-organization': this.getOrganization(),
			'owner': this.getOwner(),
			'owner-position': this.getPosition(),
			'serial-number': this.getCertSN(),
			'valid-through': Alfresco.util.toISO8601(Date.parse(this.getCertValidBefore())),
			'valid-from': Alfresco.util.toISO8601(Date.parse(this.getCertValidAfter())),
			'fingerprint': this.getFingerprint(),
			'ca': this.getCertIssuer()
		};
	},
	validate: function () {
		console.log('not implemented yet');
	},
	isValid: function () {
		this.valid = signApplet.getAPI().certIsValid(this.base64);
		return this.valid;
	},
	getContainer: function () {
		return this.container;
	},
	getHumanReadable: function () {
		var dataObj = {
			owner: this.getOwner(),
			organization: this.getOrganization(),
			OrgUnit: this.getOrgUnit()
		},
		template;
		if (this.valid) {
			template = '<strong>{owner}</strong></br>{organization}</br>{OrgUnit}';
		} else {
			template = '<strong>{owner}</strong></br>{organization}</br>{OrgUnit}</br><span style="color: red;">' + Alfresco.util.message('lecm.signdoc.msg.invalid.cert') + '</span>';
		}

		return YAHOO.lang.substitute(template, dataObj);
	}
};

CertificateFromBase64.prototype = Certificate.prototype;

/*
 ==========================================================
 Собственно классик для подписи
 ==========================================================
 */

function Signature(cert, nodeRef, signResult) {
	var contentURI;

	this.signatureContent = null;
	this.signDate = null;
	this.valid = false;
	this.validateDate = null;
	this.certificate = cert;
	this.contentAssociation = nodeRef;

	if (!cert || !nodeRef) {
		console.log('creating signature with empty arguments');
		return null;
	}

	try {
		this.signDate = Alfresco.util.toISO8601(new Date());
		this.signatureContent = signResult.signature/*signApplet.sign(Alfresco.constants.PROXY_URI + 'api/node/content/' + contentURI, 'URL')*/;
		if (!this.signatureContent) {
			console.log('error while creating signature');
			return null;
		}
		this.valid = true;
	} catch (e) {
		console.log('error while processing signature');
		return null;
	}

}

/*
 ==========================================================
 Подготовка строки в base64 для дальнейшей работы с
 апплетом (удаление служебных заголовков и переносов строк)
 ==========================================================
 */

function prepareBase64(base64Content) {
	return base64Content.replace(/^-{1,}.*-{1,}$|\s/gmi, '');
}

/*
 ==========================================================
 Создание подписи из, собственно, подписи в base64
 ==========================================================
 */

function SignatureFromContent(nodeRef, signatureContent, signatureNodeRef, contentHash) {

	var contentURI, result, now;

	this.signatureNodeRef = signatureNodeRef;
	this.signatureContent = prepareBase64(signatureContent);
	this.contentAssociation = nodeRef;
	this.contentHash = contentHash;
    this.signature = null;
	this.signDate = null;
	this.valid = false;
	this.validateDate = null;
	this.certificate = null;

	if (nodeRef && signatureContent) {
		try {
			try{
				var signData = JSON.parse(this.signatureContent);
				this.signature = signData.signature;
			} catch(err){
				this.signature = this.signatureContent;
			}
			now = Alfresco.util.toISO8601(new Date());
			this.validateDate = now;
			this.signDate = now;
		} catch (e) {
			console.log('error while creating signature');
			return null;
		}
	} else {
		console.log('creating signature with empty arguments');
	}

}

/*
 ==========================================================
 Создание подписи из файла
 ==========================================================
 */

function SignatureFromFile(nodeRef, fileHash, clientLocalSign) {
	var contentURI, result, now, signatureRaw;

	this.signatureContent = null;
	this.contentAssociation = nodeRef;
	this.contentHash = fileHash;
	this.valid = false;
	this.validateDate = null;
	this.certificate = null;
	this.signDate = null;

	if (nodeRef) {
		try{
			var signData = JSON.parse(clientLocalSign);
			this.signatureContent = signData.signature;
		} catch(err){
			this.signatureContent = clientLocalSign;
		}
		//this.certificate = new CertificateFromBase64(result.getCert());
		try {
			if (this.signatureContent && this.signatureContent.length) {
				now = Alfresco.util.toISO8601(new Date());
				this.validateDate = now;
				this.signDate = now;
			} else {
				Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.sign.load.error')});
			}
		} catch (e) {
			Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.sign.load.error')});
		}
	}
}



Signature.prototype = {
	getSignatureContent: function () {
		return this.signatureContent;
	},
	getSignDate: function () {
		return this.signDate;
	},
	getCertificate: function () {
		return this.certificate;
	},
	// validate: function (){
	// 	var contentURI = new Alfresco.util.NodeRef(this.contentAssociation).uri;
	// 	this.valid = signApplet.check(Alfresco.constants.PROXY_URI + 'api/node/content/' + contentURI, 'URL', this.signatureContent).getResult();
	// 	this.validateDate = Alfresco.util.toISO8601(new Date());
	// 	return {
	// 		'updateDate' : this.validateDate,
	// 		'contentNodeRef' : this.contentAssociation,
	// 		'isValid' : this.valid
	// 	};
	// },

	getStatus: function () {
		return this.valid;
	},
	getJSONInfo: function () {
		/*var certInfo = this.certificate.getJSON()*/;
		var signObj = {
            'owner': this.certificate.shortsubject,
            'owner-position': "",
            'owner-organization': "",
            'serial-number': this.certificate.serialNumber,
            'valid-from': Alfresco.util.toISO8601(this.certificate.validFrom),
            'valid-through': Alfresco.util.toISO8601(this.certificate.validTo),
            'ca': this.certificate.shortissuer,
            'fingerprint': this.certificate.thumbprint,
			'sign-to-content-association': this.contentAssociation,
			'content': this.signatureContent,
			'signing-date': this.signDate
		};
		return signObj/*YAHOO.lang.merge(signObj, certInfo)*/;
	}
};

SignatureFromContent.prototype = Signature.prototype;
SignatureFromFile.prototype = Signature.prototype;

if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

function checkForApplet() {
	/*if (!document.getElementsByName('signApplet')) {*/
    if (!document.getElementsByName('cadesplugin')) {
		Alfresco.util.PopupManager.displayMessage({
			text: Alfresco.util.message('lecm.signdoc.msg.applet.not.found')
		});
		return false;
	}
	return true;
}


(function () {
	var currentSigningCert = null,
        sTSAAddress = "",
		config = {
			tsPolicy: null,
			proxyPass: null,
			proxyUser: null,
			proxyHost: null,
			proxyPort: null,
			decryptNoDec: null,
			tsURL: 'http://www.cryptopro.ru/tsp/tsp.srf',
			licKey: 'MIICQzCCAfCgAwIBAgIQaYQDKGqmddWbpGNWItSd5f0zAKBgYqhQMCAgMFADBlMSAwHgYJKoZIhvcNAQkBFhFpbmZvQGNyeXB0b3Byby5ydTELMAkGA1UEBhMCUlUxEzARBgNVBAoTCkNSWVBUTy1QUk8xHzAdBgNVBAMTFlRlc3QgQ2VudGVyIENSWVBUTy1QUk8wHhcNMDkwNDA3MTIwMjE1WhcNMTQxMDA0MDcwOTQxWjBlMSAwHgYJKoZIhvcNAQkBFhFpbmZvQGNyeXB0b3Byby5ydTELMAkGA1UEBhMCUlUxEzARBgNVBAoTCkNSWVBUTy1QUk8xHzAdBgNVBAMTFlRlc3QgQ2VudGVyIENSWVBUTy1QUk8wYzAcBgYqhQMCAhMwEgYHKoUDAgIjAQYHKoUDAgIeAQNDAARAAuT/0ab2nICa2ux/SnjBzC3T5Zbqy+0iMnmyAuLGfDXmdGQbCXcRjGc/D9DoI6Z+bTt/xMQo/SscaAEgoFzYeaN4MHYwCwYDVR0PBAQDAgHGMA8GA1UdEwEB/wQFMAMBAf8wHQYDVR0OBBYEFG2PXgXZX6yRF5QelZoFMDg3ehAqMBIGCSsGAQQBgjcVAQQFAgMCAAIwIwYJKwYBBAGCNxUCBBYEFHrJxwnbIByWlC/8Rq1tk9BeaRIOMAoGBiqFAwICAwUAA0EAWHPSk7xjIbEOc3Lu8XK1G4u7yTsIu0xa8uGlNU+ZxNVSUnAm3a7QqSfptlt9b0T9Jk39oWN0XHTYSXMKd3djTQ==',
			password: '123',
			signatureType: 'PKCS7DET',
			validatorPath: 'https://www.cryptopro.ru/ocsp/ocsp.srf',
			provType: 'CSP_CRYPTOPRO',
			userName: '1',
			enableTS: 'false',
			issuerCert: 'MIICQzCCAfCgAwIBAgIQaYQDKGqmWbpGNWItSd5f0zAKBgYqhQMCAgMFADBlMSAwHgYJKoZIhvcNAQkBFhFpbmZvQGNyeXB0b3Byby5ydTELMAkGA1UEBhMCUlUxEzARBgNVBAoTCkNSWVBUTy1QUk8xHzAdBgNVBAMTFlRlc3QgQ2VudGVyIENSWVBUTy1QUk8wHhcNMDkwNDA3MTIwMjE1WhcNMTQxMDA0MDcwOTQxWjBlMSAwHgYJKoZIhvcNAQkBFhFpbmZvQGNyeXB0b3Byby5ydTELMAkGA1UEBhMCUlUxEzARBgNVBAoTCkNSWVBUTy1QUk8xHzAdBgNVBAMTFlRlc3QgQ2VudGVyIENSWVBUTy1QUk8wYzAcBgYqhQMCAhMwEgYHKoUDAgIjAQYHKoUDAgIeAQNDAARAAuT/0ab2nICa2ux/SnjBzC3T5Zbqy+0iMnmyAuLGfDXmdGQbCXcRjGc/D9DoI6Z+bTt/xMQo/SscaAEgoFzYeaN4MHYwCwYDVR0PBAQDAgHGMA8GA1UdEwEB/wQFMAMBAf8wHQYDVR0OBBYEFG2PXgXZX6yRF5QelZoFMDg3ehAqMBIGCSsGAQQBgjcVAQQFAgMCAAIwIwYJKwYBBAGCNxUCBBYEFHrJxwnbIByWlC/8Rq1tk9BeaRIOMAoGBiqFAwICAwUAA0EAWHPSk7xjIbEOc3Lu8XK1G4u7yTsIu0xa8uGlNU+ZxNVSUnAm3a7QqSfptlt9b0T9Jk39oWN0XHTYSXMKd3djTQ==',
			licCert: 'MIICQzCCAfCgAwIBAgddIQaYQDKGqmWbpGNWItSd5f0zAKBgYqhQMCAgMFADBlMSAwHgYJKoZIhvcNAQkBFhFpbmZvQGNyeXB0b3Byby5ydTELMAkGA1UEBhMCUlUxEzARBgNVBAoTCkNSWVBUTy1QUk8xHzAdBgNVBAMTFlRlc3QgQ2VudGVyIENSWVBUTy1QUk8wHhcNMDkwNDA3MTIwMjE1WhcNMTQxMDA0MDcwOTQxWjBlMSAwHgYJKoZIhvcNAQkBFhFpbmZvQGNyeXB0b3Byby5ydTELMAkGA1UEBhMCUlUxEzARBgNVBAoTCkNSWVBUTy1QUk8xHzAdBgNVBAMTFlRlc3QgQ2VudGVyIENSWVBUTy1QUk8wYzAcBgYqhQMCAhMwEgYHKoUDAgIjAQYHKoUDAgIeAQNDAARAAuT/0ab2nICa2ux/SnjBzC3T5Zbqy+0iMnmyAuLGfDXmdGQbCXcRjGc/D9DoI6Z+bTt/xMQo/SscaAEgoFzYeaN4MHYwCwYDVR0PBAQDAgHGMA8GA1UdEwEB/wQFMAMBAf8wHQYDVR0OBBYEFG2PXgXZX6yRF5QelZoFMDg3ehAqMBIGCSsGAQQBgjcVAQQFAgMCAAIwIwYJKwYBBAGCNxUCBBYEFHrJxwnbIByWlC/8Rq1tk9BeaRIOMAoGBiqFAwICAwUAA0EAWHPSk7xjIbEOc3Lu8XK1G4u7yTsIu0xa8uGlNU+ZxNVSUnAm3a7QqSfptlt9b0T9Jk39oWN0XHTYSXMKd3djTQ==',
			storeName: '123',
			validatorType: 'NONE',
			tmpPath: 'c\:\\tmp',
			storeType: 'HDImageStore',
			certB64: '1'
		};

	LogicECM.CryptoApplet = function (htmlId) {
		var component = Alfresco.util.ComponentManager.find({name: 'LogicECM.CryptoApplet'});
		if (component.length) {
			return component[0];
		}

		LogicECM.CryptoApplet.superclass.constructor.call(this, 'LogicECM.CryptoApplet', htmlId);
		try {
			this.loadConfig();
			/*signApplet.setConfig(config);*/
		} catch (ex) {
			console.log(ex);
		}
		this.id = htmlId;
		this.name = 'LogicECM.CryptoApplet';

		return this;
	};

	YAHOO.extend(LogicECM.CryptoApplet, Alfresco.component.Base,
		{
			/*
			 ==========================================================
			 options = {
			 successCallback: {
			 fn: someFunction,
			 scope: someScope
			 },
			 failureCallback: {
			 fn: someFunction,
			 scope: someScope
			 },
			 doBeforeAjaxCallback: {
			 fn: someFunction,
			 scope: someScope
			 }
			 }
			 ==========================================================
			 */

			/*
			 ==========================================================
			 Подписание
			 ==========================================================
			 */

            signPerformed: false,

			signContent: function (nodeRefList, form) {

                if (this.signPerformed) {
                    return true;
                }

                var i,
					signatures = [],
					sign;

                if (!YAHOO.lang.isArray(nodeRefList)) {
					nodeRefList = [nodeRefList];
				}


                var signCallBack = function(context, errormessage, result) {
                    try {
                        if (errormessage) {
                            Alfresco.util.PopupManager.displayMessage({
                                text: errormessage
                            });
                        } else {
                            if (nodeRefList.length === result.signResult.length) {
                                for (i = 0; i < nodeRefList.length; i++) {
                                    sign = new Signature(currentSigningCert, nodeRefList[i], result.signResult[i]);
                                    if (sign && sign.valid) {
                                        signatures.push(sign);
                                    }
                                }
                            }
                        }

                        if (!signatures.length) {
							Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.create.sign.failed')});
							form.url = null;
							return false;
						}
                        else
                        {
                            form.url = Alfresco.constants.PROXY_URI + 'lecm/signed-docflow/signContent';
                        }
						for (i = 0; i < signatures.length; i++) {
							form.dataObj.push(signatures[i].getJSONInfo());
						}
                    } catch(ex) {
                        Alfresco.util.PopupManager.displayMessage({
                            text: Alfresco.util.message("message.setting.signable.failure")
                        });
                    }
                    context.signPerformed = true;
                    Alfresco.util.Ajax.jsonRequest(form);
                };

                Alfresco.util.Ajax.jsonRequest({
                    method: "POST",
                    url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/signed-docflow/getHashes",
                    dataObj: nodeRefList,
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
                            var docs = response.json;

                            if (!this.useNPAPI) {
                                SignES6Hashes(currentSigningCert.thumbprint, docs, sTSAAddress, signCallBack, this);
                            }
                            else {
                                SignHashes_NPAPI(currentSigningCert.thumbprint, docs, sTSAAddress, signCallBack, this);
                            }
                        },
                        scope: this
                    }
                });
                /*
                var intervalID;

                var waitSignature = function(context) {
                    if (context.signPerformed) {
                        Alfresco.util.Ajax.jsonRequest(form);
                        clearInterval(intervalID);
                    }
                };

                intervalID = setInterval(waitSignature(this), 200);
                */
				return false;
			},
			signAction: function (nodeRefList, options, parentDialog) {

				if (!YAHOO.lang.isArray(nodeRefList)) {
					nodeRefList = [nodeRefList];
				}

                var signform = {};

				this.loadCertsForm({
					parentDialog: parentDialog,
					title: Alfresco.util.message('lecm.signdoc.msg.signing'),
					actionURL: ""/*Alfresco.constants.PROXY_URI + 'lecm/signed-docflow/signContent'*/,
					doBeforeAjaxCallback: {
						scope: this,
						fn: function (form) {
							var signatures, i;

							form.dataObj = [];
                            this.signPerformed = false;
							var action = this.signContent(nodeRefList, form);
                            return action;
						}
					},
					successCallback: {
						scope: this,
						fn: function (response) {
							var i, cb,
								badResult = '',
								message;

							for (i = 0; i < response.json.length; i++) {
								if (response.json[i].signResponse === 'SIGN_ALREADY_EXIST') {
									badResult += response.json[i].name + '</br>';
								}
							}

							if (badResult !== '') {
								Alfresco.util.PopupManager.displayPrompt({
									title: Alfresco.util.message('lecm.signdoc.msg.validation.error'),
									text: Alfresco.util.message('lecm.signdoc.msg.signing.ok.files.already.sign') + ':<br/>' + badResult,
									noEscape: true
								});
							} else {
								message = (response.json.length > 1) ? Alfresco.util.message('lecm.signdoc.msg.docs.subscribed') : Alfresco.util.message('lecm.signdoc.msg.doc.subscribed');
								Alfresco.util.PopupManager.displayMessage({text: message});
							}

							cb = (options) ? options.successCallback : null;
							if (cb && YAHOO.lang.isFunction(cb.fn)) {
								cb.fn.apply(cb.scope, cb.obj);
							}
						}
					},
					failureCallback: {
						scope: this,
						fn: function () {
							Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.sign.send.error')});
						}
					}

				});

			},
			signMultAction: function (docNodeRef, options) {

				this.loadMultipleForm({
					docNodeRef: docNodeRef,
					title: Alfresco.util.message('lecm.signdoc.msg.signing'),
					actionURL: null,
					htmlId: this.id,
					doBeforeAjaxCallback: {
						fn: this.signAction,
						scope: this,
						obj: options
					}
				});
			},
			/*
			 ==========================================================
			 Обновление информации о подписях
			 ==========================================================
			 */

			updateSignsAction: function (nodeRef, options) {

				function updateRequest(signs) {
					var i, checkResult = [];

					for (i = 0; i < signs.length; i++) {
						checkResult.push(signs[i].getCheckInfo());
					}

					Alfresco.util.Ajax.jsonRequest({
						method: 'POST',
						url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/signed-docflow/signing/update',
						dataObj: checkResult,
						successCallback: {
							scope: this,
							fn: function (response) {

								var resText = '',
									success = true,
									cb, i;

								for (i = 0; i < response.json.length; i++) {
									if (response.json[i].result !== 'success')
										success = false;
								}

								if (!success) {
									resText = Alfresco.util.message('lecm.signdoc.msg.some.sign.refresh.failed');
								} else {
									resText = Alfresco.util.message('lecm.signdoc.msg.all.signs.updated');
								}

								Alfresco.util.PopupManager.displayMessage({text: resText});

								cb = (options) ? options.successCallback : null;
								if (cb && YAHOO.lang.isFunction(cb.fn)) {
									cb.fn.apply(cb.scope);
								}
							}
						},
						failureCallback: {
							scope: this,
							fn: function () {
								Alfresco.util.PopupManager.displayMessage({
									text: Alfresco.util.message('lecm.signdoc.msg.signs.update.failed')
								});
							}
						}
					});
				}

				var opts = {
					successCallback: {
						scope: this,
						fn: updateRequest
					}
				};

				this.getSignsByNodeRef(nodeRef, opts);
			},
			/*
			 ==========================================================
			 Получений подписей контента или документа
			 ==========================================================
			 */

			getSignsByNodeRef: function (nodeRef, options) {

				Alfresco.util.Ajax.jsonRequest({
					method: 'GET',
					url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/signed-docflow/getDocumentSignsInfo?nodeRef=' + nodeRef,
					successCallback: {
						scope: this,
						fn: function (response) {
							var signs = [],
								sign, cb, i, j, k;
							//[!]
							for (i = 0; i < response.json.length; i++) {
								for (j = 0; j < response.json[i].signedContent.length; j++) {
									for (k = 0; k < response.json[i].signedContent[j].signsInfo.length; k++) {
										sign = new SignatureFromContent(response.json[i].signedContent[j].nodeRef, response.json[i].signedContent[j].signsInfo[k].signature, response.json[i].signedContent[j].signsInfo[k].nodeRef, response.json[i].signedContent[j].contentHash);
										signs.push(sign);
									}
								}
							}
							
							var partialContentSignature = signs.map(function (sign) {
								return {
									hash: sign.contentHash,
									signedMessage: sign.signature
								}
							});

							verifySignaturesSync(partialContentSignature, function (results) {
								signs.forEach(function (sign, index) {
									sign.valid = results[index].valid;
								});

								cb = (options) ? options.successCallback : null;
								if (cb && YAHOO.lang.isFunction(cb.fn)) {
									cb.fn.call(cb.scope, signs);
								}
							});
						}
					},
					failureCallback: {
						scope: this,
						fn: function () {
							var cb;

							Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.get.sings.info.failed')});

							cb = (options) ? options.failureCallback : null;
							if (cb && YAHOO.lang.isFunction(cb.fn)) {
								cb.fn.apply(cb.scope);
							}
						}
					}
				});
			},
			/*
			 ==========================================================
			 Конфиг и всё такое прочее
			 ==========================================================
			 */

			setCurrentSigningCert: function (cert) {
				currentSigningCert = cert;
				/*config.certB64 = cert.getBase64();*/
				/*signApplet.setConfig(config);*/
			},
            setSTSAAddress: function(paramsTSAAddress) {
                sTSAAddress = paramsTSAAddress;
            },
			getCurrentSigningCert: function () {
				return currentSigningCert;
			},
			//[delete?]
			checkForApplet: function () {
				/*if (!document.getElementsByName('signApplet')) {*/
                if (!document.getElementsByName('cadesplugin')) {
					Alfresco.util.PopupManager.displayMessage({
						text: Alfresco.util.message('lecm.signdoc.msg.applet.not.found')
					});
					return false;
				}
				return true;
			},
			//[delete?]
			getConfig: function () {
				return config;
			},
			loadConfig: function () {
				Alfresco.util.Ajax.jsonRequest({
					method: 'POST',
					url: Alfresco.constants.PROXY_URI_RELATIVE + '/lecm/signed-docflow/config/applet',
					dataObj: {action: 'get'},
					successCallback: {
						fn: function (response) {
							var configRes = response.json;
							config.licKey = configRes.licKey;
							config.storeName = configRes.storeName;
							config.licCert = configRes.licCert;
							config.issuerCert = configRes.licCert;
							/*signApplet.setConfig(config);*/
						}
					},
					failureMessage: Alfresco.util.message('lecm.signdoc.msg.applet.config.load.error')
				});
			},
			getCerts: function () {
				var certs = [],
					i, cert,
					containers = signApplet.getService().getKeyStoreList().split('###');

				for (i = 0; i < containers.length; i++) {
					cert = null;
					cert = new Certificate(containers[i]);
					if (cert && cert.getBase64() && cert.isValid()) {
						certs.push(cert);
					}
				}
				return certs;
			},
			//[delete?]
			checkSignatures: function (signatures) {
				var result = true;
				if (!YAHOO.lang.isArray(signatures)) {
					signatures = [signatures];
				}
				for (var i = 0; i < signatures.length; i++) {
					if (!signatures[i].validate()) {
						Alfresco.util.PopupManager.displayMessage({
							text: Alfresco.util.message('lecm.signdoc.msg.invalid.signature')
						});
						result = false;
					}
				}
				return result;

			},
			/*
			 ==========================================================
			 Загрузка подписи
			 ==========================================================
			 */

			loadSignAction: function (nodeRef, clientLocalSign,  options) {
				function loadRequest (signature){
					var dataObj = {};
					if (signature.getStatus()) {
					    dataObj = signature.getJSONInfo();

					    Alfresco.util.Ajax.jsonRequest({
						    method: 'POST',
						    url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/signed-docflow/loadSign',
						    dataObj: dataObj,
						    successCallback: {
							    scope: this,
							    fn: function (response) {
								    var text = (response.json.signResponse === 'SIGN_OK') ? Alfresco.util.message('lecm.signdoc.msg.signature.loaded') : Alfresco.util.message('lecm.signdoc.msg.sign.tested.download.failed'),
									    cb = (options) ? options.successCallback : null;
								    Alfresco.util.PopupManager.displayMessage({text: text});

								    if (cb && YAHOO.lang.isFunction(cb.fn)) {
									    cb.fn.apply(cb.scope, [response]);
								    }
							    }
						    },
						    failureCallback: {
							    scope: this,
							    fn: function () {
								    Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.load.sign.failed')});

								    var cb = (options) ? options.failureCallback : null;
								    if (cb && YAHOO.lang.isFunction(cb.fn)) {
									    cb.fn.apply(cb.scope);
								    }
							    }
						    }
					    });
				    } else {
					    Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.not.valid.sign.cancel')});
				    }
				}
				
				function verifySign(fileHash){
					signature = new SignatureFromFile(nodeRef, fileHash, clientLocalSign);

					var partialContentSignature = [];
					partialContentSignature[0]= {
							hash: fileHash,
							signedMessage: signature.signatureContent
						};
					verifySignaturesSync(partialContentSignature, function (results) {
						signature.valid = results[0].valid;
						GetCertificateInfo(results[0].certificate, function(result){
							signature.certificate = result;
							loadRequest(signature);
						});
					});
				}
				
				var opts = {
					successCallback: {
						scope: this,
						fn: verifySign
					}
				};
				
				this.getFileHash(nodeRef,  clientLocalSign, opts);

			},
			getFileHash: function(nodeRef, clientLocalSign, opts){
				Alfresco.util.Ajax.jsonRequest({
					method: 'GET',
					url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/signed-docflow/getDocumentSignsInfo?nodeRef=' + nodeRef,
					successCallback: {
						scope: this,
						fn: function (response) {
							var cb;
							hash = response.json[0].signedContent[0].contentHash;

							cb = (opts) ? opts.successCallback : null;
								if (cb && YAHOO.lang.isFunction(cb.fn)) {
									cb.fn.apply(cb.scope, [hash]);
							}
						}
					},
					failureCallback: {
						scope: this,
						fn: function () {
							Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.get.sings.info.failed')});
						}
					}
				});
			},
			exportSignAction: function (nodeRef) {
				document.location.href = Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/signed-docflow/getZip?nodeRef=' + nodeRef;
			},
			loadSignFromString: function (nodeRef, signatureContent, options) {
				var dataObj = {},
					signature = new SignatureFromContent(nodeRef, signatureContent, null);

				if (signature.getStatus()) {
					dataObj = signature.getJSONInfo();

					Alfresco.util.Ajax.jsonRequest({
						method: 'POST',
						url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/signed-docflow/loadSign',
						dataObj: dataObj,
						successCallback: {
							scope: this,
							fn: function (response) {
								var text = (response.json.signResponse === 'SIGN_OK') ? Alfresco.util.message('lecm.signdoc.msg.signature.loaded') : Alfresco.util.message('lecm.signdoc.msg.sign.tested.download.failed'),
									cb = (options) ? options.successCallback : null;
								Alfresco.util.PopupManager.displayMessage({text: text});

								if (cb && YAHOO.lang.isFunction(cb.fn)) {
									cb.fn.apply(cb.scope, [response]);
								}
							}
						},
						failureCallback: {
							scope: this,
							fn: function () {
								Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.load.sign.failed')});

								var cb = (options) ? options.failureCallback : null;
								if (cb && YAHOO.lang.isFunction(cb.fn)) {
									cb.fn.apply(cb.scope);
								}
							}
						}
					});
				} else {
					Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.not.valid.sign.cancel')});
				}
			},
			/*
			 ==========================================================
			 Проверка подписей контента(листа)
			 ==========================================================
			 */

			CheckSignaturesByNodeRefList: function (nodeRefList, options) {

				if (!(nodeRefList instanceof Array)) {
					nodeRefList = [nodeRefList];
				}


				function successCallback(response) {
					var i, j, cb,
						contentSuccess = [],
						contentFailure = [],
						nodeRefList = [],
						signatures,
						signaturesStatus,
						sign,
						contentNodeRef,
						contentName,
						signatureNodeRef,
						signatureContent,
						template,
						badContent = '';

					for (i = 0; i < response.json.length; i++) {
						contentNodeRef = response.json[i].signedContentNodeRef;
						contentName = response.json[i].signedContentName;

						signatures = [];
						signaturesStatus = true;

						for (j = 0; j < response.json[i].signatures.length; j++) {

							signatureNodeRef = response.json[i].signatures[j].nodeRef;
							signatureContent = response.json[i].signatures[j].signatureContent;

							sign = new SignatureFromContent(contentNodeRef, signatureContent, signatureNodeRef);
							signatures.push(sign);
							if (!sign.getStatus()) {
								signaturesStatus = false;
							}
						}

						if (!signaturesStatus) {
							contentFailure.push({
								'signedContentNodeRef': contentNodeRef,
								'signedContentName': contentName
							});
						} else {
							contentSuccess.push({
								'signedContentNodeRef': contentNodeRef,
								'signedContentName': contentName
							});
						}

					}

					for (i = 0; i < contentFailure.length; i++) {
						template = '<a style=align: left; href=\"' + Alfresco.constants.URL_PAGECONTEXT + 'document-attachment?nodeRef={signedContentNodeRef}\">{signedContentName}</a><br/>';
						badContent += YAHOO.lang.substitute(template, contentFailure[i]);
					}

					if (badContent) {
						Alfresco.util.PopupManager.displayPrompt({
							title: Alfresco.util.message('lecm.signdoc.msg.invalid.file.signature'),
							text: Alfresco.util.message('lecm.signdoc.msg.files.verify.failed') + ':<br/>' + badContent,
							noEscape: true
						});
					}

					// Alfresco.util.PopupManager.displayMessage({	text: 'Подписи успешно прошли проверку' });

					for (i = 0; i < contentSuccess.length; i++) {
						nodeRefList.push(contentSuccess[i].signedContentNodeRef);
					}

					cb = (options) ? options.successCallback : null;
					if (cb && YAHOO.lang.isFunction(cb.fn)) {
						cb.fn.apply(cb.scope, [nodeRefList]);
					}

				}


				Alfresco.util.Ajax.jsonRequest({
					method: 'POST',
					url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/signed-docflow/getSignsInfo',
					dataObj: nodeRefList,
					successCallback: {
						fn: successCallback,
						scope: this
					}
				});
			},
			/*
			 ==========================================================
			 Аутенфикация
			 ==========================================================
			 */

			unicloudAuth: function (options) {

				var GUIDsign = signApplet.sign('GUID', 'String');
				var TS = new Date();
				var TSsign = signApplet.sign(TS.toString('yyyy-MM-dd hh:mm'), 'String');
				var dataObj = {'guidSign': GUIDsign, 'timestamp': TS.toString('yyyy-MM-dd hh:mm'), 'timestampSign': TSsign};
				var loadingPopup = Alfresco.util.PopupManager.displayMessage({
					text: Alfresco.util.message('lecm.signdoc.lbl.authentication'),
					spanClass: 'wait',
					displayTime: 0,
					modal: true
				});
				loadingPopup.center();
				Alfresco.util.Ajax.jsonRequest({
					method: 'POST',
					url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/signed-docflow/unicloud/api/authenticateByCertificate',
					dataObj: dataObj,
					successCallback: {
						scope: this,
						fn: function (response) {
							var status = response.json.gateResponse.responseType,
								cb = (options) ? options.successCallback : null;

							loadingPopup.destroyWithAnimationsStop();

							if (status === 'OK') {

								if (cb && YAHOO.lang.isFunction(cb.fn)) {
									cb.fn.apply(cb.scope, cb.obj);
								}

							} else {
								loadingPopup = Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.auth.failed')});
								YAHOO.lang.later(2500, loadingPopup, loadingPopup.destroyWithAnimationsStop);
							}
						}
					},
					failureCallback: {
						scope: this,
						fn: function () {
							loadingPopup.destroyWithAnimationsStop();

							loadingPopup = Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.auth.failed')});
							YAHOO.lang.later(2500, loadingPopup, loadingPopup.destroyWithAnimationsStop);
						}
					}

				});
			},
			authenticateAction: function (options) {
				this.loadCertsForm({
					title: Alfresco.util.message('lecm.signdoc.lbl.authentication'),
					actionURL: null,
					doBeforeAjaxCallback: {
						scope: this,
						fn: function () {
							this.unicloudAuth(options);
						}
					}
				});

			},
			/*
			 ==========================================================
			 Отправка контрагенту DELETE?
			 ==========================================================
			 */

			sendToPartnerAction: function (nodeRefList) {

				if (!YAHOO.lang.isArray(nodeRefList)) {
					nodeRefList = [nodeRefList];
				}

				function checkCallback(nodeRefList) {
					Alfresco.util.Ajax.jsonRequest({
						method: 'POST',
						url: Alfresco.constants.PROXY_URI_RELATIVE + '/lecm/signed-docflow/sendContentToPartner',
						dataObj: {
							content: nodeRefList
						},
						successCallback: {
							scope: this,
							fn: function (response) {
								var i,
									gateResponse,
									success = true;

								for (i = 0; i < response.json.length; i++) {
									gateResponse = response.json[i].gateResponse;
									if (gateResponse.responseType !== 'OK') {
										success = false;
										console.log(gateResponse.message);
									}
								}

								if (success) {
									Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.docs.sent')});
								} else {
									Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.some.docs.not.sent')});
								}
							}
						},
						failureCallback: {
							scope: this,
							fn: function () {
								Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.sending.error')});
							}
						}
					});
				}

				var opts = {
					successCallback: {
						scope: this,
						fn: checkCallback,
						obj: [nodeRefList]
					}
				};

				this.CheckSignaturesByNodeRefList(nodeRefList, opts);

			},
			sendToPartnerActionMultiple: function (docNodeRef) {
				this.loadMultipleForm({
					docNodeRef: docNodeRef,
					title: Alfresco.util.message('lecm.signdoc.ttl.send.to.counterparty'),
					actionURL: null,
					htmlId: this.id,
					doBeforeAjaxCallback: {
						scope: this,
						fn: this.sendToPartnerAction
					}
				});
			},
			/*
			 ==========================================================

			 ==========================================================
			 */

			//delete?
			CheckSignatures: function (data) {
				var checkResult = [];
				for (var i = 0; i < data.length; i++) {
					var finalCheckResult = true;
					for (var j = 0; j < data[i].signatures.length; j++) {
						var signatures = [];
						var contentURI = new Alfresco.util.NodeRef(data[i].signedContentNodeRef).uri;
						var check = signApplet.check(Alfresco.constants.PROXY_URI + 'api/node/content/' + contentURI, 'URL', data[i].signatures[j].signatureContent).getResult();
						if (!check)
							finalCheckResult = false;
						signatures.push({
							signatureNodeRef: data[i].signatures[j].signatureNodeRef,
							isValid: check
						});
					}
					checkResult.push({
						signatures: signatures,
						contentRef: data[i].signedContentNodeRef,
						signedContentName: data[i].signedContentName,
						finalCheckResult: finalCheckResult
					});
				}
				return checkResult;
			},
			/*
			 ==========================================================
			 Формы
			 ==========================================================
			 */



			loadCertsForm: function (params) {

				var url = Alfresco.constants.URL_SERVICECONTEXT + 'components/form',
					form,
					templateRequestParams = {
						itemKind: 'type',
						itemId: 'lecm-orgstr:employees',
						mode: 'create',
						submitType: 'json',
						formId: 'auth-form',
						showCancelButton: 'true'
					};

				form = new Alfresco.module.SimpleDialog(params.htmlId);
				form.setOptions({
					parentDialog: params.parentDialog,
					width: '20em',
					templateUrl: url,
					templateRequestParams: templateRequestParams,
					actionUrl: params.actionURL,
					destroyOnHide: true,
					doAfterDialogHide: {
						scope: this,
						fn: function (p_form, p_dialog) {
							if (p_dialog.options.parentDialog) {
								p_dialog.options.parentDialog.hide();
							}
						}
					},
					doBeforeDialogShow: {
						scope: this,
						fn: function (p_form, p_dialog) {
							p_dialog.dialog.setHeader(params.title);
						}},
					doBeforeAjaxRequest: {
						scope: this,
						fn: function (form, obj) {
							if (!currentSigningCert) {
								Alfresco.util.PopupManager.displayMessage({
									text: Alfresco.util.message('lecm.signdoc.msg.must.select.cert')
								});
								return false;
							}

							var cb = (params) ? params.doBeforeAjaxCallback : null;
							if (cb && YAHOO.lang.isFunction(cb.fn)) {
								cb.fn.apply(cb.scope, [form]);
							}

							if (!params.actionURL) {
								return false;
							}
							return true;

						}
					},
					onSuccess: {
						scope: this,
						fn: function (response) {
							var cb = (params) ? params.successCallback : null;
							if (cb && YAHOO.lang.isFunction(cb.fn)) {
								cb.fn.apply(cb.scope, [response]);
							}
						}
					},
					onFailure: {
						scope: this,
						fn: function () {
							var cb = (params) ? params.failureCallback : null;
							if (cb && YAHOO.lang.isFunction(cb.fn)) {
								cb.fn.apply(cb.scope);
							}
						}
					}
				}).show();
			},
			loadMultipleForm: function (params) {
				
				function recoverButtonsState(dialog) {
					dialog.widgets.cancelButton.set("disabled", false);
					dialog.widgets.okButton.set("disabled", false);
				}
				
				function renderForm(response) {
					var templateRequestParams = {
						itemKind: 'type',
						itemId: 'lecm-orgstr:employees',
						mode: 'create',
						submitType: 'json',
						formId: 'multiple-sign-form',
						showCancelButton: 'true',
						showCaption: false,
						obj: JSON.stringify(response.json)
					};
					var url = Alfresco.constants.URL_SERVICECONTEXT + 'components/form';
					var dialog = new Alfresco.module.SimpleDialog(params.htmlId);
					dialog.setOptions({
						width: '30em',
						templateUrl: url,
						templateRequestParams: templateRequestParams,
						actionUrl: params.actionURL,
						destroyOnHide: true,
						doBeforeDialogShow: {
							scope: this,
							fn: function (p_form, p_dialog) {
								p_dialog.dialog.setHeader(params.title);
								p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
							}},
						doBeforeAjaxRequest: {
							scope: this,
							fn: function (form, obj) {
								var nodeRefList = [],
									fields = document.forms[params.htmlId + '-form'].getElementsByTagName('input'),
									cb = (params) ? params.doBeforeAjaxCallback : null;

								for (var i = 0; i < fields.length; i++) {
									if (fields[i].checked)
										nodeRefList.push(fields[i].value);
								}
								if (!nodeRefList.length) {
									Alfresco.util.PopupManager.displayMessage({
										text: Alfresco.util.message('lecm.signdoc.msg.must.select.one.or.more.docs')
									});
									recoverButtonsState(dialog);
									return false;
								}

								if (cb && YAHOO.lang.isFunction(cb.fn)) {
									cb.fn.apply(cb.scope, [nodeRefList, cb.obj, dialog]);
								}

								if (!params.actionURL) {
									return false;
								}
								return true;
							}
						},
						onSuccess: {
							scope: this,
							fn: function (response) {
								var cb = (params) ? params.successCallback : null;
								if (cb && YAHOO.lang.isFunction(cb.fn)) {
									cb.fn.apply(cb.scope, [response]);
								}
							}
						},
						onFailure: {
							scope: this,
							fn: function () {
								var cb = (params) ? params.failureCallback : null;
								if (cb && YAHOO.lang.isFunction(cb.fn)) {
									cb.fn.apply(cb.scope);
								}
							}
						}
					}).show();
				}

				Alfresco.util.Ajax.jsonRequest({
					method: 'GET',
					url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/signed-docflow/getSignableContent?nodeRef=' + params.docNodeRef,
					successCallback: {
						fn: renderForm,
						scope: this
					}
				});
			},
            onReady: function () {
                //this.initPanel();
                //this.getHtmlElements();
                try {
                    var js = [];
                    this.useNPAPI = !!!window.Promise || !!!cadesplugin.CreateObjectAsync ? true : false;
                    js.push(this.useNPAPI ? 'scripts/signed-docflow/Code.js' : 'scripts/signed-docflow/async_code.js');
                    this.loadJs(js);
                } catch (ex) {
					console.log(ex);
				}
            },
            loadJs: function (js) {
                LogicECM.module.Base.Util.loadScripts(js, function() {YAHOO.Bubbling.fire('onCryptoAppletInit');});
            },
            loadCertificates: function () {
                if (!this.useNPAPI) {
                    GetES6CertsJson(this);
                }
                else {
                    window.addEventListener("message", function (event) {
                        if (event.data === "cadesplugin_loaded") {
                            this.useNPAPI = true;
                        }
                    },
                    false);
                    window.postMessage("cadesplugin_echo_request", "*");
                    FillCertList_NPAPIJson(this);
                }
            }
            /*
             * @Deprecated
             *
			onReady: function () {
				try {
					signApplet.setConfig(config);
					this.loadConfig();
					signApplet.setConfig(config);
					YAHOO.Bubbling.fire('onCryptoAppletInit');
				} catch (ex) {
					console.log(ex);
				}
			}*/
		});
})();

/*-----------------------------------------------------------------------------------------------------*/
/*
 ==========================================================
 Подобие класса для сертификата
 ==========================================================
 */

function Certificate(containerName) {
	var Info,
		certIssued,
		tmp;

	this.owner = '';
	this.organization = '';
	this.OrgUnit = '';
	this.position = '';
	this.certSN = '';
	this.certValidBefore = '';
	this.certValidAfter = '';
	this.fingerprint = '';
	this.certIssuer = '';
	this.base64 = '';
	this.valid = false;
	this.validateDate = null;
	this.container = '';

	if (!containerName) {
		console.log('invalid container name');
		return null;
	}

	try {
		this.base64 = signApplet.getService().bytesToBase64(signApplet.getService().getCertFromStore('', containerName));
		Info = JSON.parse(signApplet.getService().certInfo(this.base64));
	} catch (e) {
		console.log('bad container');
		return null;
	}
	if (!this.base64 || !Info) {
		console.log('error while processing certificate');
		return null;
	}
	certIssued = Info.certIssued;
	this.container = containerName;

	tmp = certIssued.match('CN=(.+?)(?=,)'); //ФИО владельца
	if (tmp) {
		this.owner = tmp[1];
	} else {
		this.owner = ' ';
	}

	tmp = certIssued.match(' O=(.+?)(?=,)'); //Организация
	if (tmp) {
		this.organization = tmp[1];
	} else {
		this.organization = ' ';
	}

	tmp = certIssued.match(' OU=(.+?)(?=,)'); //Подразделение
	if (tmp) {
		this.OrgUnit = tmp[1];
	} else {
		this.OrgUnit = ' ';
	}

	tmp = certIssued.match(' T=(.+?)(?=,)'); //Должность
	if (tmp) {
		this.position = tmp[1];
	} else {
		this.position = ' ';
	}

	this.certSN = Info.certSN; //Серийный номер
	this.certValidBefore = Info.certValidBefore;
	this.certValidAfter = Info.certValidAfter;
	this.fingerprint = Info.certFingerPrint;

	tmp = Info.certIssuer.match(' O=(.+?)(?=,)'); //УЦ
	if (tmp) {
		this.certIssuer = tmp[1];
	} else {
		this.certIssuer = '';
	}

	//Доверяй, но проверяй!
	this.valid = signApplet.getAPI().certIsValid(this.base64);
}
/*
 ==========================================================
 Тот же сертификат, только создаётся из base64
 ==========================================================
 */
function CertificateFromBase64(base64) {
	var Info,
		certIssued,
		tmp;

	this.owner = '';
	this.organization = '';
	this.OrgUnit = '';
	this.position = '';
	this.certSN = '';
	this.certValidBefore = '';
	this.certValidAfter = '';
	this.fingerprint = '';
	this.certIssuer = '';
	this.base64 = base64;
	this.valid = false;
	this.validateDate = null;

	if (!base64) {
		Alfresco.util.PopupManager.displayMessage({text: 'base64 is empty'});
		return null;
	}

	try {
		Info = JSON.parse(signApplet.getService().certInfo(this.base64));
	} catch (e) {
		console.log('bad base64');
		return null;
	}

	if (!Info) {
		return null;
	}
	certIssued = Info.certIssued;

	tmp = certIssued.match('CN=(.+?)(?=,)'); //ФИО владельца
	if (tmp) {
		this.owner = tmp[1];
	} else {
		this.owner = ' ';
	}

	tmp = certIssued.match(' O=(.+?)(?=,)'); //Организация
	if (tmp) {
		this.organization = tmp[1];
	} else {
		this.organization = ' ';
	}

	tmp = certIssued.match(' OU=(.+?)(?=,)'); //Подразделение
	if (tmp) {
		this.OrgUnit = tmp[1];
	} else {
		this.OrgUnit = ' ';
	}

	tmp = certIssued.match(' T=(.+?)(?=,)'); //Должность
	if (tmp) {
		this.position = tmp[1];
	} else {
		this.position = ' ';
	}

	this.certSN = Info.certSN; //Серийный номер
	this.certValidBefore = Info.certValidBefore;
	this.certValidAfter = Info.certValidAfter;
	this.fingerprint = Info.certFingerPrint;

	tmp = Info.certIssuer.match(' O=(.+?)(?=,)'); //УЦ
	if (tmp) {
		this.certIssuer = tmp[1];
	} else {
		this.certIssuer = '';
	}

	//Доверяй, но проверяй!
	this.valid = signApplet.getAPI().certIsValid(this.base64);

}

Certificate.prototype = {
	getCertIssuer: function () {
		return this.certIssuer;
	},
	getOrganization: function () {
		return this.organization;
	},
	getOwner: function () {
		return this.owner;
	},
	getOrgUnit: function () {
		return this.OrgUnit;
	},
	getPosition: function () {
		return this.position;
	},
	getCertSN: function () {
		return this.certSN;
	},
	getCertValidBefore: function () {
		return this.certValidBefore;
	},
	getCertValidAfter: function () {
		return this.certValidAfter;
	},
	getFingerprint: function () {
		return this.fingerprint;
	},
	getBase64: function () {
		return this.base64;
	},
	getIssuer: function () {
		return this.issuer;
	},
	getJSON: function () {
		return {
			'owner-organization': this.getOrganization(),
			'owner': this.getOwner(),
			'owner-position': this.getPosition(),
			'serial-number': this.getCertSN(),
			'valid-through': Alfresco.util.toISO8601(Date.parse(this.getCertValidBefore())),
			'valid-from': Alfresco.util.toISO8601(Date.parse(this.getCertValidAfter())),
			'fingerprint': this.getFingerprint(),
			'ca': this.getCertIssuer()
		};
	},
	validate: function () {
		console.log('not implemented yet');
	},
	isValid: function () {
		this.valid = signApplet.getAPI().certIsValid(this.base64);
		return this.valid;
	},
	getContainer: function () {
		return this.container;
	},
	getHumanReadable: function () {
		var dataObj = {
			owner: this.getOwner(),
			organization: this.getOrganization(),
			OrgUnit: this.getOrgUnit()
		},
		template;
		if (this.valid) {
			template = '<strong>{owner}</strong></br>{organization}</br>{OrgUnit}';
		} else {
			template = '<strong>{owner}</strong></br>{organization}</br>{OrgUnit}</br><span style="color: red;">' + Alfresco.util.message('lecm.signdoc.msg.invalid.cert') + '</span>';
		}

		return YAHOO.lang.substitute(template, dataObj);
	}
};

CertificateFromBase64.prototype = Certificate.prototype;

/*
 ==========================================================
 Собственно классик для подписи
 ==========================================================
 */

function Signature(cert, nodeRef, signResult) {
	var contentURI;

	this.signatureContent = null;
	this.signDate = null;
	this.valid = false;
	this.validateDate = null;
	this.certificate = cert;
	this.contentAssociation = nodeRef;

	if (!cert || !nodeRef) {
		console.log('creating signature with empty arguments');
		return null;
	}

	try {
		this.signDate = Alfresco.util.toISO8601(new Date());
		this.signatureContent = signResult.signature/*signApplet.sign(Alfresco.constants.PROXY_URI + 'api/node/content/' + contentURI, 'URL')*/;
		if (!this.signatureContent) {
			console.log('error while creating signature');
			return null;
		}
		this.valid = true;
	} catch (e) {
		console.log('error while processing signature');
		return null;
	}

}

/*
 ==========================================================
 Подготовка строки в base64 для дальнейшей работы с
 апплетом (удаление служебных заголовков и переносов строк)
 ==========================================================
 */

function prepareBase64(base64Content) {
	return base64Content.replace(/^-{1,}.*-{1,}$|\s/gmi, '');
}

/*
 ==========================================================
 Создание подписи из, собственно, подписи в base64
 ==========================================================
 */

function SignatureFromContent(nodeRef, signatureContent, signatureNodeRef, contentHash) {

	var contentURI, result, now;

	this.signatureNodeRef = signatureNodeRef;
	this.signatureContent = prepareBase64(signatureContent);
	this.contentAssociation = nodeRef;
	this.contentHash = contentHash;
    this.signature = null;
	this.signDate = null;
	this.valid = false;
	this.validateDate = null;
	this.certificate = null;

	if (nodeRef && signatureContent) {
		try {
			try{
				var signData = JSON.parse(this.signatureContent);
				this.signature = signData.signature;
			} catch(err){
				this.signature = this.signatureContent;
			}
			now = Alfresco.util.toISO8601(new Date());
			this.validateDate = now;
			this.signDate = now;
		} catch (e) {
			console.log('error while creating signature');
			return null;
		}
	} else {
		console.log('creating signature with empty arguments');
	}

}

/*
 ==========================================================
 Создание подписи из файла
 ==========================================================
 */

function SignatureFromFile(nodeRef, fileHash, clientLocalSign) {
	var contentURI, result, now, signatureRaw;

	this.signatureContent = null;
	this.contentAssociation = nodeRef;
	this.contentHash = fileHash;
	this.valid = false;
	this.validateDate = null;
	this.certificate = null;
	this.signDate = null;

	if (nodeRef) {
		try{
			var signData = JSON.parse(clientLocalSign);
			this.signatureContent = signData.signature;
		} catch(err){
			this.signatureContent = clientLocalSign;
		}
		//this.certificate = new CertificateFromBase64(result.getCert());
		try {
			if (this.signatureContent && this.signatureContent.length) {
				now = Alfresco.util.toISO8601(new Date());
				this.validateDate = now;
				this.signDate = now;
			} else {
				Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.sign.load.error')});
			}
		} catch (e) {
			Alfresco.util.PopupManager.displayMessage({text: Alfresco.util.message('lecm.signdoc.msg.sign.load.error')});
		}
	}
}



Signature.prototype = {
	getSignatureContent: function () {
		return this.signatureContent;
	},
	getSignDate: function () {
		return this.signDate;
	},
	getCertificate: function () {
		return this.certificate;
	},
	// validate: function (){
	// 	var contentURI = new Alfresco.util.NodeRef(this.contentAssociation).uri;
	// 	this.valid = signApplet.check(Alfresco.constants.PROXY_URI + 'api/node/content/' + contentURI, 'URL', this.signatureContent).getResult();
	// 	this.validateDate = Alfresco.util.toISO8601(new Date());
	// 	return {
	// 		'updateDate' : this.validateDate,
	// 		'contentNodeRef' : this.contentAssociation,
	// 		'isValid' : this.valid
	// 	};
	// },

	getStatus: function () {
		return this.valid;
	},
	getJSONInfo: function () {
		/*var certInfo = this.certificate.getJSON()*/;
		var signObj = {
            'owner': this.certificate.shortsubject,
            'owner-position': "",
            'owner-organization': "",
            'serial-number': this.certificate.serialNumber,
            'valid-from': Alfresco.util.toISO8601(this.certificate.validFrom),
            'valid-through': Alfresco.util.toISO8601(this.certificate.validTo),
            'ca': this.certificate.shortissuer,
            'fingerprint': this.certificate.thumbprint,
			'sign-to-content-association': this.contentAssociation,
			'content': this.signatureContent,
			'signing-date': this.signDate
		};
		return signObj/*YAHOO.lang.merge(signObj, certInfo)*/;
	}
};

SignatureFromContent.prototype = Signature.prototype;
SignatureFromFile.prototype = Signature.prototype;

SignatureFromContent.prototype.getCheckInfo = function () {
	// var contentURI = new Alfresco.util.NodeRef(this.contentAssociation).uri;
	// this.valid = signApplet.check(Alfresco.constants.PROXY_URI + 'api/node/content/' + contentURI, 'URL', this.signatureContent).getResult();
	// this.validateDate = Alfresco.util.toISO8601(new Date());
	return {
		signatureNodeRef: this.signatureNodeRef,
		updateDate: this.validateDate,
		contentNodeRef: this.contentAssociation,
		isValid: this.valid
	};
};
