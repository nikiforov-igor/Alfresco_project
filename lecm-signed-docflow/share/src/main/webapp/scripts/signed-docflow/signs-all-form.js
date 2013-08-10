/* global YAHOO, Alfresco, LogicECM, ko */
(function () {
	"use strict";

	LogicECM.module.SignsAllForm = function LogicECM_module_SignsInfoForm(htmlId) {

		LogicECM.module.SignsAllForm.superclass.constructor.call(this, "LogicECM.module.SignsAllForm", htmlId, null);

		this.options = {
			signedContentRef: null
		};

		return this;
	};

	YAHOO.extend(LogicECM.module.SignsAllForm, Alfresco.component.Base, {

		_initRefreshButton: function() {
			var divRefreshButton = new YAHOO.util.Element(this.id + "-refresh");
			divRefreshButton.on("click", this.refreshSigns);
		},

		refreshSigns: function() {
			console.log(">>> refreshSigns was executed!"); // TODO: remove this console.log line, uncomment ajax-lines

//			var Ajax = Alfresco.util.Ajax;
//
//			Ajax.jsonRequest({
//				method: "POST",
//				url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/signed-docflow/###__YOUR_SERVICE_URL__###",
//				dataObj: { "signedContentRef": this.options.signedContentRef },
//				successCallback: { fn: this.getSignsInfo, scope: this },
//				failureCallback: {
//					fn: function() {
//						Alfresco.util.PopupManager.displayMessage({
//							text: "Не удалось обновить информацию о подписях, попробуйте ещё раз"
//						});
//					}
//				}
//			});
//
//			this.getSignsInfo();
		},

		getSignsInfo: function() {
			var Ajax = Alfresco.util.Ajax;

			function onGetInfoSuccess(response) {
				var Get = YAHOO.util.Dom.get,

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
							var i0 = $context.$parentContext.$parentContext.$index(),
								i1 = $context.$parentContext.$index(),
								i2 = $context.$index(),

								substitute = YAHOO.lang.substitute,

								signInfo = this.signs[i0].signedContent[i1].signsInfo[i2];

							return substitute("{position} {FIO} от {signDate}", signInfo);
						},

						getSignOwner: function($context) {
							var i0 = $context.$parentContext.$parentContext.$index(),
								i1 = $context.$parentContext.$index(),
								i2 = $context.$index(),

								current = this.signs[i0].signedContent[i1].signsInfo[i2],
								previous;

							if(i2 > 0) {
								previous = this.signs[i0].signedContent[i1].signsInfo[i2 - 1];
							}

							if((previous && previous.organization) === current.organization) {
								return "";
							} else {
								if(current.isOur) {
									return "наша организация";
								}
							}

							return current.organization;
						},

						getViewAttributes: function($context) {
							var i0 = $context.$parentContext.$parentContext.$index(),
								i1 = $context.$parentContext.$index(),

								substitute = YAHOO.lang.substitute,

								signedContent = this.signs[i0].signedContent[i1];

							return substitute("viewAttributes(\"{nodeRef}\");", signedContent);
						},

						isOur: function(object) {
							return object.isOur;
						},

						isTheirs: function(object) {
							return !(object.isOur);
						},

						singsIsEmpty: function() {
							return this.signs.length === 0;
						}
					};

				signsViewModel.sortByOrganization();
				/* jshint validthis:true */
				ko.applyBindings(signsViewModel, Get(this.id));
				/* jshint validthis:false */
			}

			function onGetInfoFailure(response) {
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