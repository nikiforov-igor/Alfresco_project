if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Delegation = LogicECM.module.Delegation || {};

LogicECM.module.Delegation.DelegationOpts = LogicECM.module.Delegation.DelegationOpts || {};

(function () {

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

		options: {
			delegator: null //nodeRef на делегирующее лицо
		},

		onDelegationOptsPart1: function (result) {
			var formEl = Dom.get(this.id + "-content-part1");
			formEl.innerHTML = result.serverResponse.responseText;
		},

		onDelegationOptsPart2: function (result) {
			var formEl = Dom.get(this.id + "-content-part2");
			formEl.innerHTML = result.serverResponse.responseText;
		},

		_saveDelegationOpts: function () {
			var scope = this;
			return function (event, obj) {

			}
		},

		onReady: function () {

			Alfresco.logger.info ("A new LogicECM.module.Delegation.DelegationOpts has been created");

			if (this.options.delegator) {
				var argsPart1 = {
					htmlid:"delegation-opts-part1",
					itemKind:"node",
					itemId: this.options.delegator,
					formId: "delegation-opts-part1",
					mode: "edit",
					showCancelButton: false,
					showResetButton: false,
					showSubmitButton: false
				};
				Alfresco.util.Ajax.request ({
					method: "GET",
					url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
					dataObj: argsPart1,
					successCallback: {
						fn: this.onDelegationOptsPart1,
						scope: this
					},
					failureMessage: "не удалось выполнить запрос, попробуйте обновить страницу",
					execScripts: true
				});

				var datagrid = new LogicECM.module.Delegation.Procuracy.Grid(this.id);
				datagrid.setOptions({
					usePagination:false,
					showExtendSearchBlock:false,
					showCheckboxColumn: false,
//					dataSource: "lecm/delegation/get/procuracies",
					searchShowInactive: true,
					actions: [/*{
						type: "action-link-procuracy",
						id: "onActionCreate",
						permission: "create",
						label: "создать доверенность",
						evaluator: datagrid.canCreateProcuracy ()
						},*/{
						type: "action-link-procuracy",
						id: "onActionEdit",
						permission: "edit",
						label: "редактировать доверенность",
						evaluator: datagrid.canEditProcuracy ()
						}
					]
				});

				YAHOO.Bubbling.fire ("activeGridChanged", {
					datagridMeta:{
						itemType: "lecm-d8n:procuracy",
						nodeRef: this.options.delegator
					}
				});

				var argsPart2 = {
					htmlid:"delegation-opts-part2",
					itemKind:"node",
					itemId: this.options.delegator,
					formId: "delegation-opts-part2",
					mode: "edit",
					showCancelButton: false,
					showResetButton: false,
					showSubmitButton: false
				}
				Alfresco.util.Ajax.request ({
					method: "GET",
					url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
					dataObj: argsPart2,
					successCallback: {
						fn: this.onDelegationOptsPart2,
						scope: this
					},
					failureMessage: "не удалось выполнить запрос, попробуйте обновить страницу",
					execScripts: true
				});

				Alfresco.util.createYUIButton(this, "btnSaveDelegationOpts", this._saveDelegationOpts (), {
					label: "Сохранить"
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
