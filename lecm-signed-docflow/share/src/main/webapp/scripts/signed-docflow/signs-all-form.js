/* global YAHOO, Alfresco, LogicECM, ko, cryptoAppletModule */
(function () {
	"use strict";

	LogicECM.module.SignsAllForm = function LogicECM_module_SignsInfoForm(htmlId, controlId) {

		LogicECM.module.SignsAllForm.superclass.constructor.call(this, "LogicECM.module.SignsAllForm", controlId, null);

		this.htmlId = htmlId;

		this.options = {
			signedContentRef: null
		};

		return this;
	};

	YAHOO.extend(LogicECM.module.SignsAllForm, Alfresco.component.Base, {

		_initRefreshButton: function() {
			var divRefreshButton = new YAHOO.util.Element(this.id + "-refresh");
			divRefreshButton.on("click", this.refreshSigns, null, this);
		},

		refreshSigns: function() {
			cryptoAppletModule.CheckContentSignature(this.options.signedContentRef);
			this.getSignsInfo();
		},

		getSignsInfo: function() {
			var Ajax = Alfresco.util.Ajax;

			function onGetInfoSuccess(response) {
				var DomGet = YAHOO.util.Dom.get,
					CompGet = Alfresco.util.ComponentManager.get,

					signsViewModel = {

						signs: response.json,

						sortByOrganization: function() {
							var i, j,

								signs = this.signs,
								signsLength = this.signs.length,

								signedContent,
								signedContentLength;

							function compareByOrganization(left, right) {
								if (left.organization < right.organization) {
									return -1;
								}

								if (left.organization > right.organization) {
									return 1;
								}

								return 0;
							}

							for(i = 0; i < signsLength; i++) {
								signedContent = signs[i].signedContent;
								signedContentLength = signedContent.length;

								for(j = 0; j < signedContentLength; j++) {
									signedContent[j].signsInfo.sort(compareByOrganization);
								}
							}
						},

						getSignDescription: function($context) {
							var htmlResult = "",

								i0 = $context.$parentContext.$parentContext.$index(),
								i1 = $context.$parentContext.$index(),
								i2 = $context.$index(),

								signInfo = this.signs[i0].signedContent[i1].signsInfo[i2],

								fromISO8601 = Alfresco.util.fromISO8601,
								formatDate = Alfresco.util.formatDate,
								substitute = YAHOO.lang.substitute;

							function processSignValues(key, value) {
								var date = fromISO8601(value);

								if(key == "isValid") {
									return value ? "действительна" : "не действительна";
								}

								if(date) {
									return formatDate(date, "dd.mm.yyyy HH:MM:ss");
								}

								return value;
							}

							htmlResult += substitute("<div>{position} {FIO} от {signDate}</div>", signInfo, processSignValues);
							htmlResult += substitute("<div>Подпись {isValid} на {lastValidate}</div>", signInfo, processSignValues);

							return htmlResult;
						},

						getSignOwner: function($context) {
							var i0 = $context.$parentContext.$parentContext.$index(),
								i1 = $context.$parentContext.$index(),
								i2 = $context.$index(),

								substitute = YAHOO.lang.substitute,

								current = this.signs[i0].signedContent[i1].signsInfo[i2],
								previous;

							if(i2 > 0) {
								previous = this.signs[i0].signedContent[i1].signsInfo[i2 - 1];
							}

							if((previous && previous.organization) == current.organization) {
								return "";
							}

							if(current.isOur) {
								return "от нашей организации";
							}

							return substitute("от {organization}", current);
						},

						getViewAttributes: function($context) {
							var i0 = $context.$parentContext.$parentContext.$index(),
								i1 = $context.$parentContext.$index(),
								i2 = $context.$index(),

								substitute = YAHOO.lang.substitute,

								signInfo = this.signs[i0].signedContent[i1].signsInfo[i2];

							return substitute("viewAttributes(\"{nodeRef}\");", signInfo);
						},

						isOur: function(object) {
							return object.isOur;
						},

						isTheirs: function(object) {
							return !(object.isOur);
						},

						singsIsEmpty: function() {
							return this.signs.length == 0;
						},

						singsIsNotEmpty: function() {
							return this.signs.length != 0;
						},

						signsInfoIsEmpty: function($context) {
							var i0 = $context.$parentContext.$index(),
								i1 = $context.$index();

							return this.signs[i0].signedContent[i1].signsInfo.length == 0;
						}
					};

				signsViewModel.sortByOrganization();

				/* jshint validthis:true */
				ko.applyBindings(signsViewModel, DomGet(this.id));
				CompGet(this.htmlId).dialog.center();
				/* jshint validthis:false */
			}

			function onGetInfoFailure() {
				Alfresco.util.PopupManager.displayMessage({
					text: "Не удалось получить информацию о подписях, попробуйте ещё раз"
				});
			}

			Ajax.jsonRequest({
				method: "GET",
				url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/signed-docflow/getDocumentSignsInfo",
				dataObj: { "nodeRef": this.options.signedContentRef },
				successCallback: { fn: onGetInfoSuccess, scope: this },
				failureCallback: { fn: onGetInfoFailure }
			});
		},

		onReady: function()
		{
			this._initRefreshButton();
			this.getSignsInfo();
		}
	});
})();