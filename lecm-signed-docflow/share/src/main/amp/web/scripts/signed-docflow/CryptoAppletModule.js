if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

function checkForApplet() {
	if (!document.getElementsByName('signApplet')) {
		Alfresco.util.PopupManager.displayMessage({
			text: 'Не обнаружен крипто-апплет. Дальнейшая работа с ЮЗД невозможна'
		});
		return false;
	}
	return true;
}


(function () {
	var currentSigningCert = null,
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
			signApplet.setConfig(config);
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

			signContent: function (nodeRefList) {
				var i,
					signatures = [],
					sign;


				if (!YAHOO.lang.isArray(nodeRefList)) {
					nodeRefList = [nodeRefList];
				}

				for (i = 0; i < nodeRefList.length; i++) {
					sign = new Signature(currentSigningCert, nodeRefList[i]);
					if (sign && sign.valid) {
						signatures.push(sign);
					}
				}
				return signatures;

			},
			signAction: function (nodeRefList, options) {

				if (!YAHOO.lang.isArray(nodeRefList)) {
					nodeRefList = [nodeRefList];
				}

				this.loadCertsForm({
					title: 'Подписание',
					actionURL: Alfresco.constants.PROXY_URI + 'lecm/signed-docflow/signContent',
					doBeforeAjaxCallback: {
						scope: this,
						fn: function (form) {
							var signatures, i;

							form.dataObj = [];
							signatures = this.signContent(nodeRefList);
							if (!signatures.length) {
								Alfresco.util.PopupManager.displayMessage({text: 'Не удалось создать подписи, данные не отправлены'});
								form.options.actionURL = null;
								return false;
							}
							for (i = 0; i < signatures.length; i++) {
								form.dataObj.push(signatures[i].getJSONInfo());
							}
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
									title: 'Ошибка валидации',
									text: 'Процесс подписания успешно завершился, но следующие файлы подписать не удалось так как они уже были подписаны:<br/>' + badResult,
									noEscape: true
								});
							} else {
								message = (response.json.length > 1) ? 'Документы успешно подписаны' : 'Документ успешно подписан';
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
							Alfresco.util.PopupManager.displayMessage({text: 'Ошибка при отправке подписей'});
						}
					}

				});

			},
			signMultAction: function (docNodeRef, options) {

				this.loadMultipleForm({
					docNodeRef: docNodeRef,
					title: 'Подписание',
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
									resText = 'Некоторые подписи не удалось обновить';
								} else {
									resText = 'Все подписи обновлены';
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
									text: 'Не удалось обновить подписи'
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
										sign = new SignatureFromContent(response.json[i].signedContent[j].nodeRef, response.json[i].signedContent[j].signsInfo[k].signature, response.json[i].signedContent[j].signsInfo[k].nodeRef);
										signs.push(sign);
									}
								}
							}

							cb = (options) ? options.successCallback : null;
							if (cb && YAHOO.lang.isFunction(cb.fn)) {
								cb.fn.apply(cb.scope, [signs]);
							}
						}
					},
					failureCallback: {
						scope: this,
						fn: function () {
							var cb;

							Alfresco.util.PopupManager.displayMessage({text: 'Не удалось получить информацию о подписях'});

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
				config.certB64 = cert.getBase64();
				signApplet.setConfig(config);
			},
			getCurrentSigningCert: function () {
				return currentSigningCert;
			},
			//[delete?]
			checkForApplet: function () {
				if (!document.getElementsByName('signApplet')) {
					Alfresco.util.PopupManager.displayMessage({
						text: 'Не обнаружен крипто-апплет. Дальнейшая работа с ЮЗД невозможна'
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
							signApplet.setConfig(config);
						}
					},
					failureMessage: 'Ошибка при загрузке конфигурации для крипто-апплета'
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
							text: 'Подпись не верна'
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

			loadSignAction: function (nodeRef, options) {
				var dataObj = {},
					signature = new SignatureFromFile(nodeRef);
				if (signature.getStatus()) {
					dataObj = signature.getJSONInfo();

					Alfresco.util.Ajax.jsonRequest({
						method: 'POST',
						url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/signed-docflow/loadSign',
						dataObj: dataObj,
						successCallback: {
							scope: this,
							fn: function (response) {
								//[!] Взять на заметку
								var text = (response.json.signResponse === 'SIGN_OK') ? 'Подпись успешно загружена' : 'Подпись прошла проверку, но загрузка не удалась так как данный документ уже был подписан этой подписью',
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
								Alfresco.util.PopupManager.displayMessage({text: 'Загрузка подписи не удалась'});

								var cb = (options) ? options.failureCallback : null;
								if (cb && YAHOO.lang.isFunction(cb.fn)) {
									cb.fn.apply(cb.scope);
								}
							}
						}
					});
				} else {
					Alfresco.util.PopupManager.displayMessage({text: 'Подпись не действительна, загрузка отменена'});
				}
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
								var text = (response.json.signResponse === 'SIGN_OK') ? 'Подпись успешно загружена' : 'Подпись прошла проверку, но загрузка не удалась так как данный документ уже был подписан этой подписью',
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
								Alfresco.util.PopupManager.displayMessage({text: 'Загрузка подписи не удалась'});

								var cb = (options) ? options.failureCallback : null;
								if (cb && YAHOO.lang.isFunction(cb.fn)) {
									cb.fn.apply(cb.scope);
								}
							}
						}
					});
				} else {
					Alfresco.util.PopupManager.displayMessage({text: 'Подпись не действительна, загрузка отменена'});
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
							title: 'Подпись файла недействительна',
							text: 'Следующие файлы не прошли проверку:<br/>' + badContent,
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
					text: 'Аутентификация',
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

							loadingPopup.destroy();

							if (status === 'OK') {

								if (cb && YAHOO.lang.isFunction(cb.fn)) {
									cb.fn.apply(cb.scope, cb.obj);
								}

							} else {
								loadingPopup = Alfresco.util.PopupManager.displayMessage({text: 'Не удалось выполнить аутентификацию'});
								YAHOO.lang.later(2500, loadingPopup, loadingPopup.destroy);
							}
						}
					},
					failureCallback: {
						scope: this,
						fn: function () {
							loadingPopup.destroy();

							loadingPopup = Alfresco.util.PopupManager.displayMessage({text: 'Не удалось выполнить аутентификацию'});
							YAHOO.lang.later(2500, loadingPopup, loadingPopup.destroy);
						}
					}

				});
			},
			authenticateAction: function (options) {
				this.loadCertsForm({
					title: 'Аутентификация',
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
									Alfresco.util.PopupManager.displayMessage({text: 'Документы успешно отправлены'});
								} else {
									Alfresco.util.PopupManager.displayMessage({text: 'Произошла ошибка при отправке документов, некоторые документы не были отправлены'});
								}
							}
						},
						failureCallback: {
							scope: this,
							fn: function () {
								Alfresco.util.PopupManager.displayMessage({text: 'Произошла ошибка при отправке'});
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
					title: 'Отправка контрагенту',
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
					width: '20em',
					templateUrl: url,
					templateRequestParams: templateRequestParams,
					actionUrl: params.actionURL,
					destroyOnHide: true,
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
									text: 'Необходимо выбрать сертификат!'
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

				function renderForm(response) {
					var templateRequestParams = {
						itemKind: 'type',
						itemId: 'lecm-orgstr:employees',
						mode: 'create',
						submitType: 'json',
						formId: 'multiple-sign-form',
						showCancelButton: 'true',
						obj: JSON.stringify(response.json)
					};
					var url = Alfresco.constants.URL_SERVICECONTEXT + 'components/form';
					var form = new Alfresco.module.SimpleDialog(params.htmlId);
					form.setOptions({
						width: '30em',
						templateUrl: url,
						templateRequestParams: templateRequestParams,
						actionUrl: params.actionURL,
						destroyOnHide: true,
						doBeforeDialogShow: {
							scope: this,
							fn: function (p_form, p_dialog) {
								p_dialog.dialog.setHeader(params.title);
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
										text: 'Необходимо выбрать хотя бы один документ'
									});
									return false;
								}

								if (cb && YAHOO.lang.isFunction(cb.fn)) {
									cb.fn.apply(cb.scope, [nodeRefList, cb.obj]);
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
				try {
					signApplet.setConfig(config);
					this.loadConfig();
					signApplet.setConfig(config);
					YAHOO.Bubbling.fire('onCryptoAppletInit');
				} catch (ex) {
					console.log(ex);
				}
			}
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
			template = '<strong>{owner}</strong></br>{organization}</br>{OrgUnit}</br><span style="color: red;">Сертификат не валиден!</span>';
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

function Signature(cert, nodeRef) {
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
		contentURI = new Alfresco.util.NodeRef(nodeRef).uri;
		this.signatureContent = signApplet.sign(Alfresco.constants.PROXY_URI + 'api/node/content/' + contentURI, 'URL');
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

function SignatureFromContent(nodeRef, signatureContent, signatureNodeRef) {

	var contentURI, result, now;

	this.signatureNodeRef = signatureNodeRef;
	this.signatureContent = prepareBase64(signatureContent);
	this.contentAssociation = nodeRef;
	this.signDate = null;
	this.valid = false;
	this.validateDate = null;
	this.certificate = null;

	if (nodeRef && signatureContent) {
		try {
			contentURI = new Alfresco.util.NodeRef(this.contentAssociation).uri;
			result = signApplet.check(Alfresco.constants.PROXY_URI + 'api/node/content/' + contentURI, 'URL', this.signatureContent);
			this.valid = result.getResult();
			this.certificate = new CertificateFromBase64(result.getCert());
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

function SignatureFromFile(nodeRef) {
	var contentURI, result, now, signatureRaw;

	this.signatureContent = null;
	this.contentAssociation = nodeRef;
	this.valid = false;
	this.validateDate = null;
	this.certificate = null;
	this.signDate = null;

	if (nodeRef) {
		try {
			signatureRaw = signApplet.getService().getCertFromFileUI();
			this.signatureContent = prepareBase64(signatureRaw);
			if (this.signatureContent && this.signatureContent.length) {
				contentURI = new Alfresco.util.NodeRef(this.contentAssociation).uri;
				result = signApplet.check(Alfresco.constants.PROXY_URI + 'api/node/content/' + contentURI, 'URL', this.signatureContent);
				this.valid = result.getResult();
				this.certificate = new CertificateFromBase64(result.getCert());
				now = Alfresco.util.toISO8601(new Date());
				this.validateDate = now;
				this.signDate = now;
			} else {
				Alfresco.util.PopupManager.displayMessage({text: 'Ошибка при загрузке подписи'});
			}
		} catch (e) {
			Alfresco.util.PopupManager.displayMessage({text: 'Ошибка при загрузке подписи'});
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
		var certInfo = this.certificate.getJSON();
		var signObj = {
			'sign-to-content-association': this.contentAssociation,
			'content': this.signatureContent,
			'signing-date': this.signDate
		};
		return YAHOO.lang.merge(signObj, certInfo);
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
