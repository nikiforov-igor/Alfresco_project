<#if field.control.params.nodeRef??>
    <#assign nodeRef = field.control.params.nodeRef>
</#if>

<div id="actions-container"></div>

<script>
	(function() {
		debugger;
		console.log('${form.arguments.itemId}');
		function addButton(item) {
			var btn = new YAHOO.widget.Button({
				label: item.id,
				id: item.id,
				container: "actions-container"
			});

			btn.on('click', clickProxy);
		}

		function clickProxy(p_sType, p_aArgs, p_oItem) {

		}

		function getList(nodeRef) {
			Alfresco.util.Ajax.jsonRequest({
				method: "POST",
				url: Alfresco.constants.PROXY_URI + "lecm/groupActions/list",
				dataObj: {
					items: JSON.stringify(["${form.arguments.itemId}"])
				},
				successCallback: {
					fn: function(oResponse) {
						var json = oResponse.json;
						var actionItems = [];
						var wideActionItems = [];
						for (var i in json) {
							if (!json[i].wide) {
								var btn = new YAHOO.widget.Button({
									label: json[i].id,
									id: json[i].id,
									container: "actions-container",
									onclick: {
										fn: onGroupActionsClickProxy,
										obj: {
											actionId: json[i].id,
											type: json[i].type,
											withForm: json[i].withForm,
											items: JSON.stringify(["${form.arguments.itemId}"]),
											workflowId: json[i].workflowId,
											label: json[i].id
										}
									}
								});

								// btn.on('click', onGroupActionsClickProxy);
							}
						}
					}
				},
				failureCallback: {
					fn: function() {
						
					}
				},
				scope: this,
				execScripts: true
			});
		}

		function onGroupActionsClickProxy(p_sType, p_aArgs, p_oItem){
			// if ("Удаление номенклатурного дела" == p_oItem.actionId) {
			// 	this.deleteND_Propmt.call(this, p_sType, p_aArgs, p_oItem)
			// } else {
				onGroupActionsClick(p_sType, p_aArgs, p_oItem);
			// }
		}

		function onGroupActionsClick(p_sType, p_aArgs, p_oItem) {
			if (p_aArgs.withForm) {
				this._createScriptForm(p_aArgs);
			} else {
				if (p_aArgs.type == "lecm-group-actions:script-action") {
					var me = this;
					Alfresco.util.PopupManager.displayPrompt(
						{
							title: "Выполнение действия",
							text: "Подтвердите выполнение действия \"" + p_aArgs.actionId + "\"",
							buttons: [
								{
									text: "Ок",
									handler: function dlA_onAction_action() {
										this.destroy();
										Alfresco.util.Ajax.jsonRequest({
											method: "POST",
											url: Alfresco.constants.PROXY_URI + "lecm/groupActions/exec",
											dataObj: {
												items: p_aArgs.items,
												actionId: p_aArgs.actionId
											},
											successCallback: {
												fn: function (oResponse) {
													me._actionResponse(p_aArgs.actionId, oResponse);
												}
											},
											failureCallback: {
												fn: function () {
												}
											},
											scope: me,
											execScripts: true
										});

									}
								},
								{
									text: "Отмена",
									handler: function dlA_onActionDelete_cancel() {
										this.destroy();
									},
									isDefault: true
								}
							]
						});
				} else if (p_aArgs.type == "lecm-group-actions:workflow-action") {
					if (this.doubleClickLock) return;
					this.doubleClickLock = true;

					this.options.currentSelectedItems = p_aArgs.items;
					var templateUrl = Alfresco.constants.URL_SERVICECONTEXT;
					var formWidth = "84em";

					templateUrl += "lecm/components/form";
					var templateRequestParams = {
							itemKind: "workflow",
							itemId: p_aArgs.workflowId,
							mode: "create",
							submitType: "json",
							formId: "workflow-form",
							showCancelButton: true
						};
					var responseHandler = function(response) {
							document.location.href = document.location.href;
						}
					var me = this;
                    LogicECM.CurrentModules = LogicECM.CurrentModules || {};
					LogicECM.CurrentModules.WorkflowForm = new Alfresco.module.SimpleDialog("workflow-form").setOptions({
						width: formWidth,
						templateUrl: templateUrl,
						templateRequestParams: templateRequestParams,
						actionUrl: null,
						destroyOnHide: true,
						doBeforeDialogShow: {
							scope: this,
							fn: function(p_form, p_dialog) {
								p_dialog.dialog.setHeader(this.msg("logicecm.workflow.runAction.label", p_aArgs.label));
								var contId = p_dialog.id + "-form-container";
								Dom.addClass(contId, "metadata-form-edit");
								Dom.addClass(contId, "no-form-type");

								this.doubleClickLock = false;

								p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
							}
						},
						onSuccess: {
							scope: this,
							fn: responseHandler
						}
					}).show();
				}
			}
		}

		YAHOO.util.Event.onContentReady("actions-container", getList, this);

	})();
</script>