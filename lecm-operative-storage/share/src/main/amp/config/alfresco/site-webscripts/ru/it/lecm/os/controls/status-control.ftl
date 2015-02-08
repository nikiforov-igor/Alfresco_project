<#if field.control.params.nodeRef??>
    <#assign nodeRef = field.control.params.nodeRef>
</#if>

<div id="actions-container"></div>

<script>
	(function() {

		var grid = Alfresco.util.ComponentManager.find({name:"LogicECM.module.Base.DataGrid_nomenclature"})[0]

		function getList(nodeRef) {
			Alfresco.util.Ajax.jsonRequest({
				method: "POST",
				url: Alfresco.constants.PROXY_URI + "lecm/groupActions/list",
				dataObj: {
					items: JSON.stringify(['${form.arguments.itemId}'])
				},
				successCallback: {
					fn: function(oResponse) {
						var json = oResponse.json;
						var actionItems = [];
						var wideActionItems = [];
						for (var i in json) {
							if (!json[i].wide) {
								var allowedActions = ['Открытие номенклатурного дела', 'Закрытие номенклатурного дела', 'Передача номенклатурного дела в архив'];
								if(allowedActions.indexOf(json[i].id) >= 0) {
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
												items: JSON.stringify(['${form.arguments.itemId}']),
												workflowId: json[i].workflowId,
												label: json[i].id
											}
										}
									});
								}

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
			var actionId = p_aArgs.actionId;
			grid.ActionsClickAdapter(p_aArgs.items, actionId);
		}

		YAHOO.util.Event.onContentReady("actions-container", getList, this);

	})();
</script>