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
			var formEl = YAHOO.util.Dom.get(this.id + "-content-part1");
			formEl.innerHTML = result.serverResponse.responseText;
//			YAHOO.util.Event.addListener ("radioDelegateByFunc", "change", this._delegateByFunc ());
//			YAHOO.util.Event.addListener ("radioDelegateAllFunc", "change", this._delegateAllFunc ());
			var nodeRef = new Alfresco.util.NodeRef(this.options.delegator);
			var submissionUrl = Alfresco.constants.PROXY_URI_RELATIVE + "lecm/delegation/options/save/" + nodeRef.uri;
			YAHOO.util.Dom.setAttribute ("delegation-opts-part1-form", "action", submissionUrl);
//			YAHOO.util.Event.addListener ("delegation-opts-part1-form", "submit", function (form) {
//				alert (YAHOO.lang.JSON.stringify (form, [], 4));
//			});
			var datagrid = new LogicECM.module.Delegation.Procuracy.Grid(this.id);
			datagrid.setOptions({
				usePagination:false,
				showExtendSearchBlock:false,
				showCheckboxColumn: false,
				dataSource: "lecm/delegation/get/procuracies",
				searchShowInactive: true,
				editForm: "editProcuracy",
				actions: [
					{
						type: "action-link-procuracy",
						id: "onActionEdit",
						permission: "edit",
						label: "редактировать доверенность"
					},
					{
						type: "action-link-procuracy",
						id: "onActionDelete",
						permission: "delete",
						label: "удалить доверенность"
					}
				]
			});

			YAHOO.Bubbling.fire ("activeGridChanged", {
				datagridMeta:{
					itemType: "lecm-d8n:procuracy",
					nodeRef: this.options.delegator
				}
			});
		},

//		_saveDelegationOpts: function () {
//			var scope = this;
//			return function (event, obj) {
//
//			}
//		},

		_delegateByFunc: function () {
			var scope = this;
			return function (event, obj) {
				alert ("делегировать по бизнес функциям");
			}
		},

		_delegateAllFunc: function () {
			var scope = this;
			return function (event, obj) {
				alert ("делегировать все функции");
			}
		},

		onReady: function () {

			Alfresco.logger.info ("A new LogicECM.module.Delegation.DelegationOpts has been created");

			if (this.options.delegator) {

				var argsPart1 = {
					htmlid: "delegation-opts-part1",
					datagridId: this.id,
					itemKind: "node",
					itemId: this.options.delegator,
					formId: "delegation-opts-part1",
					mode: "edit",
					submitType: "json",
					showCancelButton: false,
					showResetButton: false,
					showSubmitButton: true
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

//				Alfresco.util.createYUIButton(this, "btnSaveDelegationOpts", this._saveDelegationOpts (), {
//					label: "Сохранить"
//				});

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
