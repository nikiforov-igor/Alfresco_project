/* global YAHOO, Alfresco, LogicECM */
(function () {
	"use strict";

	LogicECM.module.SignsShortForm = function LogicECM_module_SignsInfoForm(htmlId) {

		LogicECM.module.SignsShortForm.superclass.constructor.call(this, "LogicECM.module.SignsShortForm", htmlId, null);

		this.ids = {
			divSignsHeader:     "signs-header",
			divSignsContractor: "signs-contractor",
			divSignsOur:        "signs-our",
			divSignsContainer:  "signs-container",
			divRefreshButton:   "refresh"
		};

		this.options = {
			signedContentRef: null
		};

		return this;
	};

	YAHOO.extend(LogicECM.module.SignsShortForm, Alfresco.component.Base, {

		_generateIds: function() {
			var id;

			if(this.ids === undefined) {
				return;
			}

			for(id in this.ids) {
				if(this.ids.hasOwnProperty(id) && this.ids[id] !== "") {
					this.ids[id] = this.id + "-" + this.ids[id];
				}
			}
		},

		_findElements: function() {
			var id,
				Get = YAHOO.util.Dom.get;

			if(this.ids === undefined) {
				return;
			}

			this.elements = this.elements || {};

			for(id in this.ids) {
				if(this.ids.hasOwnProperty(id)) {
					this.elements[id] = Get(this.ids[id]);
				}
			}
		},

		_initRefreshButton: function() {
			var divRefreshButton = new YAHOO.util.Element(this.elements.divRefreshButton);
			divRefreshButton.on("click", this.refreshSigns, null, this);
		},

		refreshSigns: function() {
			this.elements.divSignsContractor.innerHTML = "";
			this.elements.divSignsOur.innerHTML = "";

			cryptoAppletModule.CheckContentSignature(this.options.signedContentRef);

			this.getSignsInfo();
		},

		getSignsInfo: function() {
			var Ajax = Alfresco.util.Ajax;

			function onGetInfoSuccess(response) {

				function processSignValues(key, value) {
					if(key === "valid") {
						return value ? "действительна" : "не действительна";
					} else if(YAHOO.lang.isString(value) && (Alfresco.util.fromISO8601(value) instanceof Date)) {
						return Alfresco.util.formatDate(Alfresco.util.fromISO8601(value));
					}

					return value;
				}

				var i, sign, signContainer, signRow, signLink,
					signsInfo = response.json[0], // [{}, ...]
					signs = signsInfo.signatures,
					signsLength = signs.length;

				/* jshint validthis:true */
				if(signsLength === 0) {
					this.elements.divSignsHeader.innerHTML = YAHOO.lang.substitute("Документ {signedContentName} никто не подписывал.", signsInfo);
					this.elements.divSignsContainer.parentNode.removeChild(this.elements.divSignsContainer);

					return false;
				} else {
					this.elements.divSignsHeader.innerHTML = YAHOO.lang.substitute("Документ {signedContentName} подписали:", signsInfo);
				}
				/* jshint validthis:false */

				/* jshint boss:true */
				for(i = 0; sign = signs[i]; i++) {
					/* jshint boss:false */
					signContainer = document.createElement("div");
					signContainer.className = "signs-row";

					signRow = document.createElement("div");
					signRow.innerHTML = YAHOO.lang.substitute("{ownerPosition} {owner} от {signingDateString}", sign, processSignValues);
					signContainer.appendChild(signRow);

					signRow = document.createElement("div");
					signRow.innerHTML = YAHOO.lang.substitute("Подпись {valid} на {updateDateString}", sign, processSignValues);
					signContainer.appendChild(signRow);

					signLink = document.createElement("a");
					signLink.className = "chain-link";
					signLink.setAttribute("onclick", YAHOO.lang.substitute("viewAttributes('{nodeRef}');", sign));
					signContainer.appendChild(signLink);

					/* jshint validthis:true */
					if(sign.our) {
						this.elements.divSignsOur.appendChild(signContainer);
					} else {
						this.elements.divSignsContractor.appendChild(signContainer);
					}
					/* jshint validthis:false */
				}

				return true;
			}

			function onGetInfoFailure(response) {
				Alfresco.util.PopupManager.displayMessage({
					text: "Не удалось получить информацию о подписях, попробуйте ещё раз"
				});
			}

			Ajax.jsonRequest({
				method: "GET",
				url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/signed-docflow/getSignsInfo",
				dataObj: { "signedContentRef": this.options.signedContentRef },
				successCallback: { fn: onGetInfoSuccess, scope: this },
				failureCallback: { fn: onGetInfoFailure }
			});
		},

		onReady: function()
		{
			this._generateIds();
			this._findElements();
			this._initRefreshButton();

			this.getSignsInfo();
		}
	});
})();