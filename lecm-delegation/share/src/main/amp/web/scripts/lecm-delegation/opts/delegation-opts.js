if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Delegation = LogicECM.module.Delegation || {};

LogicECM.module.Delegation.DelegationOpts = LogicECM.module.Delegation.DelegationOpts || {};

(function () {
	"use strict";
	LogicECM.module.Delegation.DelegationOpts = function (containerId) {
		return LogicECM.module.Delegation.DelegationOpts.superclass.constructor.call(
			this,
			"LogicECM.module.Delegation.DelegationOpts",
			containerId,
			["button", "container", "connection", "json", "selector"]);
	};

	/**
	 * Extend from Alfresco.component.Base
	 */
	YAHOO.lang.extend (LogicECM.module.Delegation.DelegationOpts, Alfresco.component.Base);

	/**
	 * Augment prototype with main class implementation, ensuring overwrite is enabled
	 */
	YAHOO.lang.augmentObject (LogicECM.module.Delegation.DelegationOpts.prototype, {
		datagridId: "",
        viewDialog: null,

		options: {
			delegator: null, //nodeRef на делегирующее лицо
			isActive: false, //флаг показывающий активно ли делегирование
            bubblingLabel: ""
		},

		onDelegationOptsPart1: function (result) {
			var contentEl = YAHOO.util.Dom.get(this.id + "-form-content");
			contentEl.innerHTML = '<div id="' + this.datagridId + '">' + result.serverResponse.responseText + "<div>";
			var nodeRef = new Alfresco.util.NodeRef(this.options.delegator);
			var submissionUrl = Alfresco.constants.PROXY_URI_RELATIVE + "lecm/delegation/options/save/" + nodeRef.uri;
			YAHOO.util.Dom.setAttribute ("delegation-opts-part1-form", "action", submissionUrl);
			var formEl = YAHOO.util.Dom.get ("delegation-opts-part1-form");
            var me = this;
			if (formEl) {
				var form = new Alfresco.forms.Form ("delegation-opts-part1-form");
				form.setAJAXSubmit (true, {
					successCallback: {
						fn: function () {
                            me.viewDialog.hide();
                            me.viewDialog.destroy();
							Alfresco.util.PopupManager.displayMessage ({
								text:"Данные обновлены"
							});
							YAHOO.Bubbling.fire("datagridRefresh", {
								bubblingLabel: me.options.bubblingLabel
							});
						},
						scope: this
					},
					failureCallback: {
						fn: function () {
							Alfresco.util.PopupManager.displayMessage ({
								text:"Не удалось обновить данные"
							});
						},
						scope: this
					}
				});
				form.setSubmitAsJSON (true);
				form.setShowSubmitStateDynamically (true, false);
				// Initialise the form
				form.init ();
			}
            YAHOO.util.Dom.removeClass(this.id + "-form", "hidden1");
            this.viewDialog.show();
		},

		onReady: function () {
			this.datagridId = this.id + YAHOO.util.Dom.generateId();
            this.viewDialog = Alfresco.util.createYUIPanel(this.id + "-form",
                {
                    width: "50em"
                });
            this.viewDialog.hide();

			Alfresco.logger.info ("A new LogicECM.module.Delegation.DelegationOpts has been created");

			if (this.options.delegator) {

				var mode = (this.options.isActive) ? "view" : "edit";

				var argsPart1 = {
					htmlid: "delegation-opts-part1",
					datagridId: this.datagridId,
					itemKind: "node",
					itemId: this.options.delegator,
					formId: "delegation-opts-part1",
					mode: mode,
					submitType: "json",
					showCancelButton: false,
					showResetButton: false,
					showSubmitButton: true,
					ignoreNodes: [LogicECM.module.Delegation.Const.employee]
				};
				Alfresco.util.Ajax.request ({
					method: "GET",
					url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
					dataObj: argsPart1,
					successCallback: {
						fn: this.onDelegationOptsPart1,
						scope: this
					},
					failureMessage: "не удалось выполнить запрос, попробуйте обновить страницу",
					execScripts: true
				});

			} else {
				Alfresco.util.PopupManager.displayPrompt ({
					title: "Ошибка отображения параметров делегирования",
					text: "текущий пользователь не привязан к сотруднику и у него нет параметров делегирования!"
				});
			}

			YAHOO.util.Dom.setStyle (this.id + "-body", "visibility", "visible");
		}

	}, true);

})();
