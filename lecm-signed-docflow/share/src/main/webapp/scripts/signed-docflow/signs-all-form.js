/* global YAHOO, Alfresco, LogicECM, ko, cryptoAppletModule */
(function () {
	"use strict";

	LogicECM.module.SignsAllForm = function LogicECM_module_SignsInfoForm(htmlId, controlId) {

		LogicECM.module.SignsAllForm.superclass.constructor.call(this, "LogicECM.module.SignsAllForm", controlId, null);

		this.htmlId = htmlId;

		this.options = {
			signedContentRef: null,
			refreshBeforeShow: false
		};

		return this;
	};

	YAHOO.extend(LogicECM.module.SignsAllForm, Alfresco.component.Base, {

		signsViewModel: {
			signs: [],

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

					signInfo = this.signs()[i0].signedContent[i1].signsInfo[i2],

					fromISO8601 = Alfresco.util.fromISO8601,
					formatDate = Alfresco.util.formatDate,
					substitute = YAHOO.lang.substitute,

					toJS = ko.mapping.toJS;

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

				signInfo = toJS(signInfo);

				htmlResult += substitute("<div>{position} {FIO} от {signDate}</div>", signInfo, processSignValues);
				htmlResult += substitute("<div>Подпись {isValid} на {lastValidate}</div>", signInfo, processSignValues);

				return htmlResult;
			},

			getSignOwner: function($context) {
				var i0 = $context.$parentContext.$parentContext.$index(),
					i1 = $context.$parentContext.$index(),
					i2 = $context.$index(),

					substitute = YAHOO.lang.substitute,
					toJS = ko.mapping.toJS,

					current = toJS(this.signs()[i0].signedContent[i1].signsInfo[i2]),
					previous;

				if(i2 > 0) {
					previous = toJS(this.signs()[i0].signedContent[i1].signsInfo[i2 - 1]);
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

					signInfo = this.signs()[i0].signedContent[i1].signsInfo[i2];

				signInfo = ko.mapping.toJS(signInfo);

				return substitute("viewAttributes(\"{nodeRef}\");", signInfo);
			},

			isOur: function(object) {
				return object.isOur;
			},

			isTheirs: function(object) {
				return !(object.isOur);
			},

			singsIsEmpty: function() {
				return this.signs().length == 0;
			},

			singsIsNotEmpty: function() {
				return this.signs().length != 0;
			},

			signsInfoIsEmpty: function($context) {
				var i0 = $context.$parentContext.$index(),
					i1 = $context.$index();

				return this.signs()[i0].signedContent[i1].signsInfo.length == 0;
			}
		},

		_initViewModel: function() {
			this.signsViewModel = ko.mapping.fromJS(this.signsViewModel);
			ko.applyBindings(this.signsViewModel, YAHOO.util.Dom.get(this.id));
		},

		_initRefreshButton: function() {
			var divRefreshButton = new YAHOO.util.Element(this.id + "-refresh");
			divRefreshButton.on("click", function() {
				this.refreshSigns();
				this.getSignsInfo();
			}, null, this);
		},

		refreshSigns: function() {
			cryptoAppletModule.CheckContentSignature(this.options.signedContentRef);
		},

		getSignsInfo: function() {
			var Ajax = Alfresco.util.Ajax;

			/* jshint validthis:true */
			function onGetInfoSuccess(response) {
				var plainViewModel,

					CompGet = Alfresco.util.ComponentManager.get,
					merge = YAHOO.lang.merge;

				plainViewModel = merge(this.signsViewModel, { signs: response.json });
				plainViewModel.sortByOrganization();

				this.signsViewModel.signs(response.json);

				CompGet(this.htmlId).dialog.center();
			}
			/* jshint validthis:false */

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
			this._initViewModel();
			this._initRefreshButton();

			if(this.options.refreshBeforeShow) {
				this.refreshSigns();
			}

			this.getSignsInfo();
		}
	});
})();