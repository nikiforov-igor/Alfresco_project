<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign itemId = args["itemId"]>
<#assign id = itemId?replace(":|/", "_", "r")>
<#assign datagridId = id + "-dtgrd">
<#assign editable = args["editable"] == "true"/>
<#assign isApproval = args["isApproval"] == "true"/>
<#assign routeRef = args["routeRef"]!''>
<#assign mainFormId = args["mainFormId"]!''>
<#assign isExpandAutomatically = args["isExpandAutomatically"] == "true"/>

<script>
(function(){
	LogicECM.CurrentModules = LogicECM.CurrentModules || {};
	LogicECM.CurrentModules["${id}"] = new LogicECM.module.Base.DataGrid("${datagridId}");
	LogicECM.CurrentModules["${id}"].setMessages(${messages});
	LogicECM.CurrentModules["${id}"].setOptions({
		usePagination: false,
		showExtendSearchBlock: false,
		showCheckboxColumn: false,
		bubblingLabel: "${datagridId}",
		overrideSortingWith: false,
		expandable: false,
        isExpandAutomatically: ${isExpandAutomatically?string},
		showActionColumn: ${editable?string},
		<#if editable>
		actions: [{
			type:"datagrid-action-link-${datagridId}",
			id:"onMoveTableRowUp",
			permission:"edit",
			label:"${msg('actions.up')}",
			evaluator: LogicECM.module.Routes.Evaluators.stageItemUp
		},{
			type:"datagrid-action-link-${datagridId}",
			id:"onMoveTableRowDown",
			permission:"edit",
			label:"${msg('actions.down')}",
			evaluator: LogicECM.module.Routes.Evaluators.stageItemDown
		},{
			type:"datagrid-action-link-${datagridId}",
			id:"onActionDelete",
			permission:"delete",
			label:"${msg('actions.delete-row')}",
			evaluator: LogicECM.module.Routes.Evaluators.stageItemEdit
		}, {
			type:"datagrid-action-link-${datagridId}",
			id:"onActionEdit",
			permission:"edit",
			label:"${msg('actions.edit')}",
			evaluator: LogicECM.module.Routes.Evaluators.stageItemDelete
		}],
		</#if>
		excludeColumns: [
			<#if !isApproval>
			'lecmApproveAspects:approvalState',
			</#if>
			'lecmApproveAspects:approvalDecision',
			'lecmApproveAspects:hasComment',
			'lecmWorkflowRoutes:stageItemEmployeeAssoc',
			'lecmWorkflowRoutes:stageItemMacrosAssoc',
			'lecmWorkflowRoutes:stageItemOrder'
		]
	});
	LogicECM.CurrentModules["${id}"].moveLock = false;

<#if editable>

	function onMoveTableRow(direction, rowData, actionEl, moveRowFunction) {
		if (!this.moveLock) {
            this.moveLock = true;
            var fields = this.datagridColumns.map(function (element) {
                return element.name.replace(':', '_');
            });
            var nameSubstituteStrings = this.datagridColumns.map(function (element) {
                return element.nameSubstituteString;
            });
            Alfresco.util.Ajax.jsonPost({
                url: Alfresco.constants.PROXY_URI + "lecm/workflow/routes/changeStageItemOrder?direction=" + direction,
                dataObj: {
                    nodeRef: rowData.nodeRef,
                    fields: fields.join(','),
                    nameSubstituteStrings: nameSubstituteStrings.join(',')
                },
                successCallback: {
                    scope: this,
                    fn: function (successResponse) {
                        var dataTable = this.widgets.dataTable;
                        var count = dataTable.getRecordSet().getLength();
                        var numSelectItem = dataTable.getTrIndex(actionEl);
                        if (this.widgets.paginator) {
                            numSelectItem = numSelectItem + ((this.widgets.paginator.getCurrentPage() - 1) * this.options.pageSize);
                        }
                        var record1 = dataTable.getRecord(numSelectItem);
                        moveRowFunction.call(this, dataTable, count, numSelectItem, record1, successResponse.json.firstItem, successResponse.json.secondItem);
                        this.moveLock = false;
                    }
                },
                failureCallback: {
                    scope: this,
                    fn: function () {
                        this.moveLock = false;
                    }
                },
                failureMessage: this.msg('message.failure')
            });
        }
	}

	LogicECM.CurrentModules["${id}"].onMoveTableRowUp = function (rowData, actionEl, actionsConfig, confirmFunction) {
		onMoveTableRow.call(this, 'up', rowData, actionEl, function (dataTable, count, numSelectItem, record1, firstItem, secondItem) {
			if (numSelectItem > 0) {
				var record2 = dataTable.getRecord(numSelectItem - 1);
				// удаляем верхнюю запись(Если она осталась на другой странице, не страшно)
				dataTable.deleteRow(record2);
				//сначала добавляем запись с которой обменялись, т.к. если на странице не остаётся записей, скрипт падает.
				dataTable.addRow(secondItem, numSelectItem);
				//удаляем "исходную" запись
				dataTable.deleteRow(record1);
				if ((this.widgets.paginator && numSelectItem % this.widgets.paginator.getRowsPerPage() !== 0) || !this.widgets.paginator) {
					//если запись не самая верхняя, добавляем ее
					dataTable.addRow(firstItem, numSelectItem - 1);
				}
			}
		});
	};
	LogicECM.CurrentModules["${id}"].onMoveTableRowDown = function (rowData, actionEl, actionsConfig, confirmFunction) {
		onMoveTableRow.call(this, 'down', rowData, actionEl, function (dataTable, count, numSelectItem, record1, firstItem, secondItem) {
			if (numSelectItem < count - 1) {
				var record2 = dataTable.getRecord(numSelectItem + 1);
				// удаляем верхнюю запись(Если она осталась на другой странице, не страшно)
				dataTable.deleteRow(record2);
				//сначала добавляем запись с которой обменялись, т.к. если на странице не остаётся записей, скрипт падает.
				dataTable.addRow(secondItem, numSelectItem);
				//удаляем "исходную" запись
				dataTable.deleteRow(record1);
				//если запись не самая верхняя, добавляем ее
				dataTable.addRow(firstItem, numSelectItem + 1);
			}
		});
	};
	<#if isApproval>
        LogicECM.CurrentModules["${id}"].getCustomCellFormatter = LogicECM.module.Approval.StageExpanded.getCustomCellFormatter;
	</#if>
    LogicECM.CurrentModules["${id}"].onActionDelete = function (p_items, owner, actionsConfig, fnDeleteComplete) {
        this.onDelete(p_items, owner, actionsConfig, function() {
			<#if isApproval>
				YAHOO.Bubbling.fire('stageItemDeleted');
			</#if>
            YAHOO.Bubbling.fire('routeStagesUpdate',{
                formId: "${mainFormId}",
                routeRef: "${routeRef}"
            });
        }, null);
    };
</#if>

	YAHOO.util.Event.onContentReady("${datagridId}", function () {
		YAHOO.Bubbling.fire("activeGridChanged", {
			datagridMeta:{
				useFilterByOrg: false,
				itemType: LogicECM.module.Routes.Const.ROUTES_CONTAINER.stageItemType,
				nodeRef: '${itemId}',
				useChildQuery: true,
				sort: 'lecmWorkflowRoutes:stageItemOrder|true',
				searchConfig: {
					filter: ""
				},
				actionsConfig: {
					fullDelete: true,
					trash: false
				}
			},
			bubblingLabel: "${datagridId}"
		});
	});

})();
</script>

<div id="${datagridId}" class="stagesDatagridExpanded">
	<@grid.datagrid datagridId false />
</div>
<div class="clear"></div>
