
/*---------------------------------------------------------------------------------*/

var cryptoAppletModule = (function () {
	var attributes = {id: 'signApplet', width: 1, height: 1};
	var parameters = {jnlp_href: 'http://127.0.0.1:8080/share/scripts/ItStampApplet.jnlp'};
	var config = {
        "tsPolicy": null,
        "proxyPass": null,
        "proxyUser": null,
        "proxyHost": null,
        "proxyPort": null,
        "decryptNoDec": null,
        "tsURL": "http://www.cryptopro.ru/tsp/tsp.srf",
        "licKey": "MIICQzCCAfCgAwIBAgIQaYQDKGqmddWbpGNWItSd5f0zAKBgYqhQMCAgMFADBlMSAwHgYJKoZIhvcNAQkBFhFpbmZvQGNyeXB0b3Byby5ydTELMAkGA1UEBhMCUlUxEzARBgNVBAoTCkNSWVBUTy1QUk8xHzAdBgNVBAMTFlRlc3QgQ2VudGVyIENSWVBUTy1QUk8wHhcNMDkwNDA3MTIwMjE1WhcNMTQxMDA0MDcwOTQxWjBlMSAwHgYJKoZIhvcNAQkBFhFpbmZvQGNyeXB0b3Byby5ydTELMAkGA1UEBhMCUlUxEzARBgNVBAoTCkNSWVBUTy1QUk8xHzAdBgNVBAMTFlRlc3QgQ2VudGVyIENSWVBUTy1QUk8wYzAcBgYqhQMCAhMwEgYHKoUDAgIjAQYHKoUDAgIeAQNDAARAAuT/0ab2nICa2ux/SnjBzC3T5Zbqy+0iMnmyAuLGfDXmdGQbCXcRjGc/D9DoI6Z+bTt/xMQo/SscaAEgoFzYeaN4MHYwCwYDVR0PBAQDAgHGMA8GA1UdEwEB/wQFMAMBAf8wHQYDVR0OBBYEFG2PXgXZX6yRF5QelZoFMDg3ehAqMBIGCSsGAQQBgjcVAQQFAgMCAAIwIwYJKwYBBAGCNxUCBBYEFHrJxwnbIByWlC/8Rq1tk9BeaRIOMAoGBiqFAwICAwUAA0EAWHPSk7xjIbEOc3Lu8XK1G4u7yTsIu0xa8uGlNU+ZxNVSUnAm3a7QqSfptlt9b0T9Jk39oWN0XHTYSXMKd3djTQ==",
        "password": "123",
        "signatureType": "PKCS7DET",
        "validatorPath": "http://www.cryptopro.ru/ocsp/ocsp.srf",
        "provType": "CSP_CRYPTOPRO",
        "userName": "1",
        "enableTS" : "false",
        "issuerCert": "MIICQzCCAfCgAwIBAgIQaYQDKGqmWbpGNWItSd5fdd0zAKBgYqhQMCAgMFADBlMSAwHgYJKoZIhvcNAQkBFhFpbmZvQGNyeXB0b3Byby5ydTELMAkGA1UEBhMCUlUxEzARBgNVBAoTCkNSWVBUTy1QUk8xHzAdBgNVBAMTFlRlc3QgQ2VudGVyIENSWVBUTy1QUk8wHhcNMDkwNDA3MTIwMjE1WhcNMTQxMDA0MDcwOTQxWjBlMSAwHgYJKoZIhvcNAQkBFhFpbmZvQGNyeXB0b3Byby5ydTELMAkGA1UEBhMCUlUxEzARBgNVBAoTCkNSWVBUTy1QUk8xHzAdBgNVBAMTFlRlc3QgQ2VudGVyIENSWVBUTy1QUk8wYzAcBgYqhQMCAhMwEgYHKoUDAgIjAQYHKoUDAgIeAQNDAARAAuT/0ab2nICa2ux/SnjBzC3T5Zbqy+0iMnmyAuLGfDXmdGQbCXcRjGc/D9DoI6Z+bTt/xMQo/SscaAEgoFzYeaN4MHYwCwYDVR0PBAQDAgHGMA8GA1UdEwEB/wQFMAMBAf8wHQYDVR0OBBYEFG2PXgXZX6yRF5QelZoFMDg3ehAqMBIGCSsGAQQBgjcVAQQFAgMCAAIwIwYJKwYBBAGCNxUCBBYEFHrJxwnbIByWlC/8Rq1tk9BeaRIOMAoGBiqFAwICAwUAA0EAWHPSk7xjIbEOc3Lu8XK1G4u7yTsIu0xa8uGlNU+ZxNVSUnAm3a7QqSfptlt9b0T9Jk39oWN0XHTYSXMKd3djTQ==",
        //"issuerCert" : null,
        "licCert" : "MIICQzCCAfCgAwIBAgddIQaYQDKGqmWbpGNWItSd5f0zAKBgYqhQMCAgMFADBlMSAwHgYJKoZIhvcNAQkBFhFpbmZvQGNyeXB0b3Byby5ydTELMAkGA1UEBhMCUlUxEzARBgNVBAoTCkNSWVBUTy1QUk8xHzAdBgNVBAMTFlRlc3QgQ2VudGVyIENSWVBUTy1QUk8wHhcNMDkwNDA3MTIwMjE1WhcNMTQxMDA0MDcwOTQxWjBlMSAwHgYJKoZIhvcNAQkBFhFpbmZvQGNyeXB0b3Byby5ydTELMAkGA1UEBhMCUlUxEzARBgNVBAoTCkNSWVBUTy1QUk8xHzAdBgNVBAMTFlRlc3QgQ2VudGVyIENSWVBUTy1QUk8wYzAcBgYqhQMCAhMwEgYHKoUDAgIjAQYHKoUDAgIeAQNDAARAAuT/0ab2nICa2ux/SnjBzC3T5Zbqy+0iMnmyAuLGfDXmdGQbCXcRjGc/D9DoI6Z+bTt/xMQo/SscaAEgoFzYeaN4MHYwCwYDVR0PBAQDAgHGMA8GA1UdEwEB/wQFMAMBAf8wHQYDVR0OBBYEFG2PXgXZX6yRF5QelZoFMDg3ehAqMBIGCSsGAQQBgjcVAQQFAgMCAAIwIwYJKwYBBAGCNxUCBBYEFHrJxwnbIByWlC/8Rq1tk9BeaRIOMAoGBiqFAwICAwUAA0EAWHPSk7xjIbEOc3Lu8XK1G4u7yTsIu0xa8uGlNU+ZxNVSUnAm3a7QqSfptlt9b0T9Jk39oWN0XHTYSXMKd3djTQ==",
        "storeName" : "123",
        "validatorType" : "NONE",
        "tmpPath" : "c\:\\tmp",
        "storeType" : "HDImageStore",
        "certB64" : "1"
    };

	var certContainer = 'container';

	var CURRENT_CONTAINER = '';

	function loadConfig(){
		Alfresco.util.Ajax.jsonRequest({
                method: "POST",
                url: Alfresco.constants.PROXY_URI_RELATIVE + "/lecm/signed-docflow/config/applet",
				dataObj : { action : "get"},
                successCallback: {
                    fn: function(response) {
                        var configRes = response.json;
						config.licKey = configRes.licKey;
						config.storeName = configRes.storeName;
						config.licCert = configRes.licCert;
						config.issuerCert = configRes.licCert;

						signApplet.setConfig(config);
                    }
                }

            });
	}
	function getCertInfo(container, base64cert) {
		var result = {};
		if(container){
			var Info = JSON.parse(signApplet.getService().certInfo(signApplet.getService().bytesToBase64(signApplet.getService().getCertFromStore('', container))));
		}
		if(base64cert){
			var Info = JSON.parse(signApplet.getService().certInfo(base64cert));
		}
		var certIssued = Info.certIssued;
		result.container = container;
		var tmp = certIssued.match('CN=(.+?)(?=,)'); //ФИО владельца
		if (tmp)
			result.owner = tmp[1];
		else
			result.owner = ' ';
		tmp = certIssued.match(' O=(.+?)(?=,)'); //Организация
		if (tmp)
			result.organization = tmp[1];
		else
			result.organization = ' ';
		tmp = certIssued.match(' OU=(.+?)(?=,)'); //Подразделение
		if (tmp)
			result.OrgUnit = tmp[1];
		else
			result.OrgUnit = ' ';
		tmp = certIssued.match(' T=(.+?)(?=,)'); //Должность
		if (tmp)
			result.position = tmp[1];
		else
			result.position = ' ';

		result.certSN = Info.certSN; //Серийный номер
		result.certValidBefore = Info.certValidBefore;
		result.certValidAfter = Info.certValidAfter;
		result.fingerprint = Info.certFingerPrint;

		tmp = Info.certIssuer.match(' O=(.+?)(?=,)'); //УЦ
		if (tmp)
			result.issuer = tmp[1];
		else
			result.issuer = '';


		return {
			"owner" : result.owner,
			"owner-position" : result.position,
			"owner-organization" : result.organization,
			"serial-number" : result.certSN,
		    "valid-from": Alfresco.util.toISO8601(Date.parse(result.certValidAfter)),
		    "valid-through": Alfresco.util.toISO8601(Date.parse(result.certValidBefore)),
			"ca" : result.issuer,
			"fingerprint" : result.fingerprint
		};
	}

	return {
		SignMultiple: function (response) {
		var templateUrl = "components/form"
                + "?itemKind={itemKind}"
                + "&itemId={itemId}"
                + "&mode={mode}"
                + "&submitType={submitType}"
                + "&showCancelButton=true"
                + "&formId={formId}";
            var url = YAHOO.lang.substitute (Alfresco.constants.URL_SERVICECONTEXT + templateUrl, {
                itemKind: "type",
                itemId: "lecm-orgstr:employees",
                mode: "create",
                submitType: "json",
                formId: "multiple-sign-form"
            });
			var sd = new Alfresco.module.SimpleDialog("multiple-sign");
			sd.setOptions({
			width: "30em",
			templateUrl: url,
			actionUrl: Alfresco.constants.PROXY_URI + "lecm/signed-docflow/signContent",
			templateRequestParams: {
				obj : JSON.stringify(response.json)
			},
			destroyOnHide: true,
			doBeforeDialogShow: {
				fn: function ( p_form, p_dialog ) {
					p_dialog.dialog.setHeader( "Документы на подпись" );
				}},
			doBeforeAjaxRequest : {
				fn: function(form) {
					var nodeRefList = [];
					var fields = document.forms["multiple-sign-form"].getElementsByTagName("input");

					for(var i = 0; i < fields.length; i++) {
						if(fields[i].checked) nodeRefList.push(fields[i].value);
					}
					if(!nodeRefList.length){
						Alfresco.util.PopupManager.displayMessage({
							text: 'Необходимо выбрать хотя бы один документ для подписи'
						});
						return false;
					}
					cryptoAppletModule.Sign(nodeRefList);
				}
			}
			}).show();
	},

		SendMultiple : function (response) {
		var templateUrl = "components/form"
                + "?itemKind={itemKind}"
                + "&itemId={itemId}"
                + "&mode={mode}"
                + "&submitType={submitType}"
                + "&showCancelButton=true"
                + "&formId={formId}";
            var url = YAHOO.lang.substitute (Alfresco.constants.URL_SERVICECONTEXT + templateUrl, {
                itemKind: "type",
                itemId: "lecm-orgstr:employees",
                mode: "create",
                submitType: "json",
                formId: "multiple-sign-form"
            });
			var sd = new Alfresco.module.SimpleDialog("multiple-sign");
			sd.setOptions({
			width: "30em",
			templateUrl: url,
			actionUrl: Alfresco.constants.PROXY_URI_RELATIVE + "/lecm/signed-docflow/sendContentToPartner",
			templateRequestParams: {
				obj : JSON.stringify(response.json)
			},
			destroyOnHide: true,
			doBeforeDialogShow: {
				fn: function ( p_form, p_dialog ) {
					p_dialog.dialog.setHeader( "Документы на отправку" );
				}},
			doBeforeAjaxRequest : {
				fn: function(form) {
					var nodeRefList = [];
					var fields = document.forms["multiple-sign-form"].getElementsByTagName("input");

					for(var i = 0; i < fields.length; i++) {
						if(fields[i].checked) nodeRefList.push(fields[i].value);
					}
					if(!nodeRefList.length){
						Alfresco.util.PopupManager.displayMessage({
							text: 'Необходимо выбрать хотя бы один документ для отправки'
						});
						return false;
					}
					cryptoAppletModule.SendToContragent(nodeRefList);
				}
			}
			}).show();
	},

		deployApplet : function(afterLoad) {
			if(document.getElementById('signApplet')){
				cryptoAppletModule.startApplet();
				return;
			}
			var app = document.createElement('applet');
			app.id= 'signApplet';
			app.archive= '/share/scripts/signed-docflow/ITStampApplet.jar';
			app.code= 'ru.businesslogic.crypto.userinterface.CryptoApplet.class';
			app.width = '1';
			app.height = '1';
			app.innerHTML = '<param name="signOnLoad" value="false"/>' +
			                '<param name="debug" value="true"/>' +
							'<param name="providerType" value="CSP_CRYPTOPRO"/>' +
                            '<param name="doAfterLoad" value="' + afterLoad + '"/>';
			document.getElementsByTagName('body')[0].appendChild(app);

			cryptoAppletModule.startApplet();
		},

		setCurrentContainer : function(container) {
			CURRENT_CONTAINER = container;
		},

		getCurrentContainer : function() {
			return CURRENT_CONTAINER;
		},

		currentContainer : certContainer,
		startApplet : function() {
			try{
				signApplet.setConfig(config); //загрузка конфига "по-умолчанию"
				loadConfig();
			} catch(ex) {
				console.log(ex);
			}

		},

		reConfig : function(config) {
			certContainer = config.storeName;
			signApplet.setConfig(config);
		},

		reConfigCert : function(certContainer) {
			//config.storeName = certContainer;
			config.certB64 = signApplet.getService().bytesToBase64(signApplet.getService().getCertFromStore('', certContainer));
			signApplet.setConfig(config);
			return;
		},

		getCerts : function(selectId) {
			var options='';
			var containers = signApplet.getService().getKeyStoreList().split('###');
			for(var i=0; i<containers.length; i++) {
				var subject = signApplet.getService().certSubject(signApplet.getService().bytesToBase64(signApplet.getService().getCertFromStore('', containers[i])));
				options += '<option value="' + containers[i] + '">' + subject + '</option>';
			}
			document.getElementById(selectId).innerHTML = options;
		},
		getConfig : function() {
			return config;
		},
		getCertsInfo : function() {
			var result = [];
			var containers = signApplet.getService().getKeyStoreList().split('###');
			var finalCount = 0;
			for(var i=0; i<containers.length; i++) {

				var Info = {};
				try{
					Info = JSON.parse(signApplet.getService().certInfo(signApplet.getService().bytesToBase64(signApplet.getService().getCertFromStore('', containers[i]))));
				} catch(e){
					console.log("bad container");
					continue;
				}
				result[finalCount] = {};
				var certIssued = Info.certIssued;
				result[finalCount].container = containers[i];
				var tmp = certIssued.match('CN=(.+?)(?=,)');
				if (tmp) result[finalCount].SubjectName = tmp[1];
				tmp = certIssued.match('O=(.+?)(?=,)');
				if (tmp) result[finalCount].Organization = tmp[1];
				tmp = certIssued.match('OU=(.+?)(?=,)');
				if (tmp) result[finalCount].OrgUnit = tmp[1];
				finalCount++;
			}
			return result;
		},

		signGTS : function(container) {
			var GUIDsign = signApplet.sign("GUID", "String");
			var TS = new Date();
			var TSsign = signApplet.sign(TS.toString('yyyy-MM-dd hh:mm'), "String");
			return {"guidSign" : GUIDsign, "timestamp" : TS.toString('yyyy-MM-dd hh:mm'), "timestampSign" : TSsign};
		},

		unicloudAuth : function(container, authSuccessCallback) {
			var GUIDsign = signApplet.sign("GUID", "String");
			var TS = new Date();
			var TSsign = signApplet.sign(TS.toString('yyyy-MM-dd hh:mm'), "String");
			var dataObj = {"guidSign" : GUIDsign, "timestamp" : TS.toString('yyyy-MM-dd hh:mm'), "timestampSign" : TSsign};
			var loadingPopup = Alfresco.util.PopupManager.displayMessage({
				text: "Аутентификация",
				spanClass: "wait",
				displayTime: 0,
				modal: true
			});
			loadingPopup.center();
			Alfresco.util.Ajax.jsonRequest({
                method: "POST",
                url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/signed-docflow/unicloud/api/authenticateByCertificate",
                dataObj: dataObj,
                successCallback: {
                    fn: function(response) {
                        var status = response.json.gateResponse.responseType;

						loadingPopup.destroy();

                        if(status == "OK"){
							authSuccessCallback();
                        } else {
							loadingPopup = Alfresco.util.PopupManager.displayMessage({ text: "Не удалось выполнить аутентификацию" });
							YAHOO.lang.later(2500, loadingPopup, loadingPopup.destroy);
                        }
                    }
                },
                failureCallback: {
                	fn: function(){
						loadingPopup.destroy();

						loadingPopup = Alfresco.util.PopupManager.displayMessage({ text: "Не удалось выполнить аутентификацию" });
						YAHOO.lang.later(2500, loadingPopup, loadingPopup.destroy);
                	}
                }

            });
		},

		showAuthenticateForm: function() {
			var templateUrl = "components/form"
				+ "?itemKind={itemKind}"
				+ "&itemId={itemId}"
				+ "&mode={mode}"
				+ "&submitType={submitType}"
				+ "&showCancelButton=true"
				+ "&formId={formId}";

			var url = YAHOO.lang.substitute (Alfresco.constants.URL_SERVICECONTEXT + templateUrl, {
				itemKind: "type",
				itemId: "lecm-orgstr:employees",
				mode: "create",
				submitType: "json",
				formId: "auth-form"
			});

			var sd = new Alfresco.module.SimpleDialog("dialog");
			sd.setOptions({
				width: "50em",
				templateUrl: url,
				actionUrl: null,
				destroyOnHide: true,
				doBeforeDialogShow: {
					fn: function ( p_form, p_dialog ) {
						p_dialog.dialog.setHeader( "Аутенфикация Unicloud" );
					}
				},
				doBeforeAjaxRequest: {
					fn: function() {
						if(!CurrentContainer) {
							Alfresco.util.PopupManager.displayMessage({
								text: 'Необходимо выбрать сертификат!'
							});
							return false;
						}
						cryptoAppletModule.unicloudAuth(CurrentContainer);
						return false;
					}
				}
			}).show();

		},

		Bang : function() {
			var templateUrl = "components/form"
                + "?itemKind={itemKind}"
                + "&itemId={itemId}"
                + "&mode={mode}"
                + "&submitType={submitType}"
                + "&showCancelButton=true"
                + "&formId={formId}";
            var url = YAHOO.lang.substitute (Alfresco.constants.URL_SERVICECONTEXT + templateUrl, {
                itemKind: "type",
                itemId: "lecm-orgstr:employees",
                mode: "create",
                submitType: "json",
                formId: "auth-form"
            });
                var sd = new Alfresco.module.SimpleDialog("dialog");
                sd.setOptions({
                width: "20em",
                templateUrl: url,
                actionUrl: null,
                destroyOnHide: true,
                doBeforeDialogShow: {
                    },
                doBeforeAjaxRequest: {
                }
                }).show();
		},

		// Проверка подписей по сформированному объекту
		CheckSignatures : function(data){
			var checkResult = [];
			for(var i = 0; i < data.length; i++){
				var finalCheckResult = true;
				for(var j = 0; j < data[i].signatures.length; j++){
					var signatures = [];
					var contentURI = new Alfresco.util.NodeRef(data[i].signedContentNodeRef).uri;
					var check = signApplet.check(Alfresco.constants.PROXY_URI + "api/node/content/" + contentURI, "URL", data[i].signatures[j].signatureContent).getResult();
					if(!check) finalCheckResult = false;
					signatures.push({
								"signatureNodeRef" : data[i].signatures[j].signatureNodeRef,
								"isValid" : check
							 }
							);
				}
				checkResult.push({
					"signatures": signatures,
					"contentRef": data[i].signedContentNodeRef,
					"signedContentName": data[i].signedContentName,
					"finalCheckResult": finalCheckResult

				});
			}
			return checkResult;
		},

		CheckSignaturesByNodeRef : function(nodeRefList, checkCallback) {

			if(!(nodeRefList instanceof Array)) {
				nodeRefList = [nodeRefList];
			}

			var nodeRefString = '';
			for(var i = 0; i < nodeRefList.length; i++){
				nodeRefString += nodeRefList[i] + '!!!';
			}

			function checkSignaturesByNodeRef(response) {
				var checkRequest = [];
				var badContent = '';
				for(var i = 0; i < response.json.length; i++){
					var signatures = [];
					for(var j = 0; j < response.json[i].signatures.length; j++){
						signatures.push({
							"signatureNodeRef": response.json[i].signatures[j].nodeRef,
							"signatureContent": response.json[i].signatures[j].signatureContent
						});
					}
					checkRequest.push({
						"signatures": signatures,
						"signedContentNodeRef": response.json[i].signedContentNodeRef,
						"signedContentName": response.json[i].signedContentName
					});
				}
				var checkResult = cryptoAppletModule.CheckSignatures(checkRequest);
				for(var i = 0; i < checkResult.length; i++){
					if(!checkResult[i].finalCheckResult){
						badContent += '<a style="align: left;" href="/share/page/document-attachment?nodeRef=' + checkResult[i].contentRef + '">' + checkResult[i].signedContentName + '</a><br/>';
					}
				}
				if(badContent){
					Alfresco.util.PopupManager.displayPrompt({
						title: 'Подпись файла недействительна',
						text: 'Следующие файлы не прошли проверку:<br/>' + badContent,
						noEscape: true
					});
					return;
				}
				Alfresco.util.PopupManager.displayMessage({
					text: 'Подписи успешно прошли проверку'
					});

				checkCallback();

			}

			Alfresco.util.Ajax.jsonRequest({
                method: "GET",
                url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/signed-docflow/getSignsInfo?signedContentRef=" + nodeRefString,
                successCallback: {
                    fn: checkSignaturesByNodeRef,
                    scope: this
                }

            });
		},

		SendToContragent: function(nodeRefList) {
			if(!(nodeRefList instanceof Array)) {
				nodeRefList = [nodeRefList];
			}

			var nodeRefString = '';
			for(var i = 0; i < nodeRefList.length; i++){
				nodeRefString += nodeRefList[i] + '!!!';
			}

			function sendCallback(){
				Alfresco.util.Ajax.jsonRequest({
				method: "POST",
				url: Alfresco.constants.PROXY_URI_RELATIVE + "/lecm/signed-docflow/sendContentToPartner",
				dataObj: {
					content: nodeRefList
				},
				failureMessage: {
					fn: function(){
						Alfresco.util.PopupManager.displayMessage({
							text: 'Произошла ошибка при отправке'
							});
			}
				},
				successCallback: {
					fn: function(response) {
						//смотрим какой ответ пришел нам с сервера
						//если все хорошо, то выводим сообщение что все хорошо
						//если response.json.gateResponse.responseType != OK то надо пойти в сценарий повторной авторизации
						//и выполнить действие заново
						if(response.serverResponse.status == 200){
							Alfresco.util.PopupManager.displayMessage({
							text: 'Документ отправлен'
							});
						} else {
							Alfresco.util.PopupManager.displayMessage({
							text: 'Произошла ошибка при отправке'
							});
						}
					},
					scope: this
				}
			});
			}

			cryptoAppletModule.CheckSignaturesByNodeRef(nodeRefList, sendCallback);

		},

		MultipleSignFormShow : function(docNodeRef, callBack) {
			Alfresco.util.Ajax.jsonRequest({
                method: "GET",
                url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/signed-docflow/getSignableContent?nodeRef=" + docNodeRef,
                successCallback: {
                    fn: callBack,
					scope:this
                }
            });
		},

		loadSignFromString: function(signatureRaw, nodeRef, callBack, callBackScope) {
			if(!YAHOO.lang.isFunction(callBack)){
				callBack = function(){};
			}
			var signDate, signature, contentURI, result, checkRes, certB64,	signObj, certInfo, res;

			signDate = Alfresco.util.toISO8601(new Date());
			signature = signatureRaw.replace(/^-{1,}.*-{1,}$|\s/gmi, "");
			contentURI = new Alfresco.util.NodeRef(nodeRef).uri;
			result = signApplet.check(Alfresco.constants.PROXY_URI + "api/node/content/" + contentURI, "URL", signature);
			checkRes = result.getResult();
			certB64 = result.getCert();
			signObj = {
				"sign-to-content-association": nodeRef,
				"content": signature,
				"signing-date": signDate
			};
			certInfo = getCertInfo(null, certB64);
			res = YAHOO.lang.merge(signObj, certInfo);

			if(!checkRes) {
				Alfresco.util.PopupManager.displayMessage({
					text: 'Подпись недействительна, загрузка не состоялась'
				});
				return;
			}

			Alfresco.util.Ajax.jsonRequest({
                method: "POST",
                url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/signed-docflow/loadSign",
				dataObj: res,
                successCallback: {
                    fn: function(response) {
						var text = (response.json.success) ? 'Подпись успешно загружена' : 'Подпись прошла проверку, но загрузка не удалась';
						Alfresco.util.PopupManager.displayMessage({
							text: text
						});
						callBack(callBackScope);
					}
                },
				onFailure: {
					fn: function() {
						Alfresco.util.PopupManager.displayMessage({
							text: "Не удалось отправить подпись"
						});
					}
				}
            });
		},

		loadSign : function(nodeRef, callBack, callBackScope){
			if(!callBack){
				callBack = function(){};
			}
			var signature;
			try{
				signature = signApplet.getService().getCertFromFileUI();
			} catch(ex) {
				console.log(ex);
				return;
			}
			this.loadSignFromString(signature, nodeRef, callBack, callBackScope);
		},

		CheckDocumentContent : function(docNodeRef) {

			var harvestNodes = function(response, nodeRefList){
				var nodeRefList = [];
				for(var i = 0; i < response.json.length; i++){
					for(var j = 0; j < response.json[i].content.length; j++){
						nodeRefList.push(response.json[i].content[j].nodeRef);
					}
				}
				cryptoAppletModule.CheckContentSignature(nodeRefList);
			};

			Alfresco.util.Ajax.jsonRequest({
                method: "GET",
                url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/signed-docflow/getSignableContent?nodeRef=" + docNodeRef,
                successCallback: {
                    fn: harvestNodes
                }
            });
		},



		CheckDocumentSignatures : function(docNodeRef){
			jQuery.ajax({
			url:  Alfresco.constants.PROXY_URI_RELATIVE + "lecm/signed-docflow/getDocumentSignsInfo?nodeRef=" + docNodeRef,
			async: false,
			dataType: "JSON",
			type: "GET",
			success: function(data) {
				var checkResult = [];
				for(var i = 0; i < data.length; i++){
					for(var j = 0; j < data[i].signedContent.length; j++){
						for(var k = 0; k < data[i].signedContent[j].signsInfo.length; k++){
							var contentURI = new Alfresco.util.NodeRef(data[i].signedContent[j].nodeRef).uri;
							var check = signApplet.check(Alfresco.constants.PROXY_URI + "api/node/content/" + contentURI, "URL", data[i].signedContent[j].signsInfo[k].signature).getResult();
							checkResult.push({
												"signatureNodeRef" : data[i].signedContent[j].signsInfo[k].nodeRef,
												"updateDate" : Alfresco.util.toISO8601(new Date()),
												"contentNodeRef" : data[i].signedContent[j].nodeRef,
												"isValid" : check
											 }
											);
						}
					}

				}
				jQuery.ajax({
					url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/signed-docflow/signing/update",
					async: false,
					data: JSON.stringify(checkResult),
					dataType: "json",
					success: function(data){
						var resText = '';
						var success = true;
						var badResult = '';
						for(var i = 0;  i < data.length; i++){
							if (data[i].result != 'success')
								success = false;
						}
						if(!success){
							resText = 'Некоторые подписи не удалось обновить';
						} else {
							resText = 'Все подписи обновлены';
						}
						Alfresco.util.PopupManager.displayMessage({
							text: resText
						});
					},
					error: function(){
						Alfresco.util.PopupManager.displayMessage({
							text: 'Не удалось обновить подписи'
						});
					},
					contentType: "application/json",
					type: "POST"

				});
			}
			});
		},
		CheckContentSignature : function(nodeRefList) {
			//cryptoAppletModule.deployApplet();
			if(!(nodeRefList instanceof Array)) {
				nodeRefList = [nodeRefList];
			}
			var nodeRefString = '';
			for(var i = 0; i < nodeRefList.length; i++){
				nodeRefString += nodeRefList[i] + '!!!';
			}

		jQuery.ajax({
			url:  Alfresco.constants.PROXY_URI_RELATIVE + "lecm/signed-docflow/getSignsInfo?signedContentRef=" + nodeRefString,
			async: false,
			dataType: "JSON",
			type: "GET",
			success: function(data) {
				var checkResult = [];
				for(var i = 0; i < data.length; i++){
					for(var j = 0; j < data[i].signatures.length; j++){
						var contentURI = new Alfresco.util.NodeRef(data[i].signedContentNodeRef).uri;
						var check = signApplet.check(Alfresco.constants.PROXY_URI + "api/node/content/" + contentURI, "URL", data[i].signatures[j].signatureContent).getResult();
						checkResult.push({
											"signatureNodeRef" : data[i].signatures[j].nodeRef,
											"updateDate" : Alfresco.util.toISO8601(new Date()),
											"contentNodeRef" : data[i].signedContentNodeRef,
											"isValid" : check
										 }
										);
					}

				}
				jQuery.ajax({
					url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/signed-docflow/signing/update",
					async: false,
					data: JSON.stringify(checkResult),
					dataType: "json",
					success: function(data){
						var resText = '';
						var success = true;
						var badResult = '';
						for(var i = 0;  i < data.length; i++){
							if (data[i].result != 'success')
								success = false;
						}
						if(!success){
							resText = 'Некоторые подписи не удалось обновить';
						} else {
							resText = 'Все подписи обновлены';
						}
						Alfresco.util.PopupManager.displayMessage({
							text: resText
						});
					},
					error: function(){
						Alfresco.util.PopupManager.displayMessage({
							text: 'Не удалось обновить подписи'
						});
					},
					contentType: "application/json",
					type: "POST"

				});
			}
			});
		},

		Sign : function (nodeRefList, callBack, callBackScope) {
			if(!callBack){
				callBack = function(){};
			}
			if(!(nodeRefList instanceof Array)) {
				nodeRefList = [nodeRefList];
			}
			var signObj = [];
			var templateUrl = "components/form"
                + "?itemKind={itemKind}"
                + "&itemId={itemId}"
                + "&mode={mode}"
                + "&submitType={submitType}"
                + "&showCancelButton=true"
                + "&formId={formId}";
            var url = YAHOO.lang.substitute (Alfresco.constants.URL_SERVICECONTEXT + templateUrl, {
                itemKind: "type",
                itemId: "lecm-orgstr:employees",
                mode: "create",
                submitType: "json",
                formId: "auth-form"
            });
			var sd = new Alfresco.module.SimpleDialog("dialog");
			sd.setOptions({
			width: "20em",
			templateUrl: url,
			actionUrl: Alfresco.constants.PROXY_URI + "lecm/signed-docflow/signContent",
			destroyOnHide: true,
			doBeforeDialogShow: {
				fn: function ( p_form, p_dialog ) {
					p_dialog.dialog.setHeader( "Подпись вложения" );
				}},
			doBeforeAjaxRequest: {
				fn : function(form, obj) {
					if(!CurrentContainer) {
						Alfresco.util.PopupManager.displayMessage({
							text: 'Необходимо выбрать сертификат!'
						});
						return;
					}
					form.dataObj = [];
					for(var i = 0; i < nodeRefList.length; i++){
						var signDate = Alfresco.util.toISO8601(new Date());
						var contentURI = new Alfresco.util.NodeRef(nodeRefList[i]).uri;
						var attachSign = signApplet.sign(Alfresco.constants.PROXY_URI + "api/node/content/" + contentURI, "URL");
						var signObj = {
							"sign-to-content-association" : nodeRefList[i],
							"content" : attachSign,
							"signing-date" : signDate
							};
						var certInfo = getCertInfo(CurrentContainer); // изменить для получения данных из созданной подписи
						var res = YAHOO.lang.merge(signObj, certInfo);
						form.dataObj.push(res);
					}
					return true;
				}
			},
			onSuccess: {
                        fn: function(response) {
						var resText = '';
						var badResult = '';
						for(var i = 0;  i < response.json.length; i++){
							if (!response.json[i].success)
								badResult += response.json[i].name + ', ';
						}
						if(badResult){
							resText = 'Следующие документы подписать не удалось:' + badResult;
						} else {
							resText = 'Все документы были успешно подписаны';
						}
						Alfresco.util.PopupManager.displayMessage({
							text: resText
						});
						callBack(callBackScope);
						},
						scope : this
                },
				onFailure: {
					fn: function() {
                            Alfresco.util.PopupManager.displayMessage({
                                text: "Не удалось создать/отправить подпись"
                            });
						}
				}
			}).show();
		}
	};
})();


