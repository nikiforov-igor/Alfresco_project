
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
                    }
                }
                
            });
	}
	function getCertInfo(container) {
		var result = {};
		var Info = JSON.parse(signApplet.getService().certInfo(signApplet.getService().bytesToBase64(signApplet.getService().getCertFromStore('', container))));
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
	
	var SignMultiple = function (response) {
		var templateUrl = "lecm/components/form"
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
	}
	
	var multipleSign = function(form) {
	
		var templateUrl = "lecm/components/form"
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
		destroyOnHide: true,
		doBeforeDialogShow: { 
			fn: function ( p_form, p_dialog ) {
				p_dialog.dialog.setHeader( "Выбор сертификата" );
			}},             
		}).show();
		
		var container = cryptoAppletModule.getCurrentContainer();
		
		var fields = document.forms["multiple-sign-form"].getElementsByTagName("input");
		for(var i = 0; i < fields.length; i++) {
			var dataObj = {};
			var signDate = Alfresco.util.toISO8601(new Date());
			var contentURI = new Alfresco.util.NodeRef(nodeRef).uri;
			var attachSign = signApplet.sign(Alfresco.constants.PROXY_URI + "api/node/content/" + contentURI, "URL");
			var signObj = {  
				"sign-to-content-association" : nodeRef, 
				"signature-content" : attachSign, 
				"signing-date" : signDate
				};
			var certInfo = getCertInfo(container);
			dataObj = YAHOO.lang.merge(signObj, certInfo);
			
			Alfresco.util.Ajax.jsonRequest({
                method: "POST",
                url: Alfresco.constants.PROXY_URI + "lecm/signed-docflow/signContent",
                dataObj: dataObj,
            });
			
		}
	}
	
	return {
		
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
			loadConfig();
			signApplet.setConfig(config);
		},
		
		reConfig : function(config) {
			certContainer = config.storeName;
			signApplet.setConfig(config);
		},
		
		reConfigCert : function(certContainer) {
			//config.storeName = certContainer;
			config.certB64 = signApplet.getService().bytesToBase64(signApplet.getService().getCertFromStore('', certContainer));
			signApplet.setConfig(config);
			return 
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
			for(var i=0; i<containers.length; i++) {
				result[i] = {};
				var Info = JSON.parse(signApplet.getService().certInfo(signApplet.getService().bytesToBase64(signApplet.getService().getCertFromStore('', containers[i]))));
				var certIssued = Info.certIssued;
				result[i].container = containers[i];
				var tmp = certIssued.match('CN=(.+?)(?=,)'); 
				if (tmp) result[i].SubjectName = tmp[1];
				tmp = certIssued.match('O=(.+?)(?=,)');
				if (tmp) result[i].Organization = tmp[1];
				tmp = certIssued.match('OU=(.+?)(?=,)');
				if (tmp) result[i].OrgUnit = tmp[1];

			}
			return result;
		},
		
		signGTS : function(container) {			
			var GUIDsign = signApplet.sign("GUID", "String");
			var TS = new Date();
			var TSsign = signApplet.sign(TS.toString('yyyy-MM-dd hh:mm'), "String");
			return {"guidSign" : GUIDsign, "timestamp" : TS.toString('yyyy-MM-dd hh:mm'), "timestampSign" : TSsign};
		},
		
		unicloudAuth : function(container) {
			var GUIDsign = signApplet.sign("GUID", "String");
			var TS = new Date();
			var TSsign = signApplet.sign(TS.toString('yyyy-MM-dd hh:mm'), "String");
			var dataObj = {"guidSign" : GUIDsign, "timestamp" : TS.toString('yyyy-MM-dd hh:mm'), "timestampSign" : TSsign};
			Alfresco.util.Ajax.jsonRequest({
                method: "POST",
                url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/signed-docflow/unicloud/api/authenticateByCertificate",
                dataObj: dataObj,
                successCallback: {
                    fn: function(response) {
                        alert(response.json.timestamp);
                    }
                }
                
            });
		},		
		
		Bang : function() {
			var templateUrl = "lecm/components/form"
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
		
		MultipleSignFormShow : function(docNodeRef) {
			Alfresco.util.Ajax.jsonRequest({
                method: "GET",
                url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/signed-docflow/getSignableContent?nodeRef=" + docNodeRef,
                successCallback: {
                    fn: SignMultiple,
					scope:this
                }
            });
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
					contentType: "application/json",
					type: "POST"
					
				});
			}
			});
		},
		
		Sign : function (nodeRefList) {
			if(!(nodeRefList instanceof Array)) {
				nodeRefList = [nodeRefList];
			}
			var signObj = [];
			var templateUrl = "lecm/components/form"
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
	}
})();	
//YAHOO.util.Event.onDOMReady(cryptoAppletModule.deployApplet(false), "The onDOMReady event fired.  The DOM is now safe to modify via script.");
YAHOO.util.Event.onDOMReady(function() {
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
					'<param name="doAfterLoad" value="false"/>';
	document.getElementsByTagName('body')[0].appendChild(app);
	
	cryptoAppletModule.startApplet();
	
});

