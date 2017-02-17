<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign containerId = fieldHtmlId + "-container">

<#assign allowCreate = true/>
<#if field.control.params.allowCreate??>
	<#assign allowCreate = field.control.params.allowCreate/>
</#if>

<#assign showActions = true/>
<#if field.control.params.showActions??>
	<#assign showActions = field.control.params.showActions/>
</#if>

<div class="control employee-positions with-grid" id="${controlId}">
    <label for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false>
        <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
    <@grid.datagrid containerId false>
        <script type="text/javascript">//<![CDATA[
            (function () {

                function init() {
                    LogicECM.module.Base.DataGrid.prototype.onActionMakePrimary = function DataGridActions_onActionMakePrimary(p_item) {
                        var staffRow = p_item;
                        // Получаем для штатного расписания ссылку на сотрудника
                        Alfresco.util.Ajax.jsonGet({
                            url: Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getStaffEmployeeLink",
                            dataObj: {
                                nodeRef: staffRow.nodeRef
                            },
                            successCallback: {
                                scope: this,
                                fn: function (response) {
                                    var oResult = response.json;
                                    if (oResult) {
                                        Alfresco.util.PopupManager.displayPrompt({
                                            title: this.msg("message.position.primary.title"),
                                            text: this.msg("message.position.primary.prompt", staffRow.itemData["assoc_lecm-orgstr_element-member-position-assoc"].displayValue),
                                            buttons:[
                                                {
                                                    text: this.msg("button.position.makePrimary"),
                                                    handler: {
                                                        obj: this,
                                                        fn: function DataGridActions_onActionMakePrimary_make(event, obj) {
                                                            this.destroy();
                                                            Alfresco.util.Ajax.jsonPost({
                                                                url: Alfresco.constants.PROXY_URI + "/lecm/orgstructure/action/makePrimary",
                                                                dataObj: {
                                                                    nodeRef: oResult.nodeRef
                                                                },
                                                                successCallback: {
                                                                    scope: obj,
                                                                    fn: function DataGrid_onActionEmployeeAdd_onSuccess(response) {
                                                                        YAHOO.Bubbling.fire("datagridRefresh", {
                                                                            bubblingLabel: this.options.bubblingLabel
                                                                        });
                                                                        Alfresco.util.PopupManager.displayMessage({
                                                                            text: this.msg("message.position.primary.success")
                                                                        });
                                                                    }
                                                                },
                                                                failureMessage: obj.msg("message.position.primary.failure")
                                                            });
                                                        }
                                                    }
                                                },
                                                {
                                                    text: this.msg("button.cancel"),
                                                    handler: function DataGridActions_onActionMakePrimary_cancel() {
                                                        this.destroy();
                                                    },
                                                    isDefault: true
                                                }
                                            ]
                                        });
                                    } else {
                                        Alfresco.util.PopupManager.displayMessage({
                                            text: this.msg("message.position.primary.failure")
                                        });
                                    }
                                }
                            },
                            failureMessage: this.msg("message.position.primary.failure")
                        });
                    };

	                var bublingLabel = "${containerId}" + Alfresco.util.generateDomId();

                    var datagrid = new LogicECM.module.Base.DataGrid('${containerId}').setOptions({
                        usePagination: false,
                        showExtendSearchBlock: false,
                        actions: [
	                        <#if form.mode != "view">
		                        {
	                                type: "datagrid-action-link-" + bublingLabel,
	                                id: "onActionMakePrimary",
	                                permission: "edit",
	                                label: "${msg("actions.makePrimary")}",
	                                evaluator: function (rowData) {
	                                    var itemData = rowData.itemData;
	                                    return itemData["prop_lecm-orgstr_primary-position"].value == "false";
	                                }
	                            }
	                        </#if>
                        ],
                        datagridMeta: {
                                useFilterByOrg: false,
                                itemType: "lecm-orgstr:staff-list",
                                datagridFormId: "employee-positions",
                                nodeRef: <#if field.value?? && field.value != "">"${field.value}"<#else>"${form.arguments.itemId}"</#if>
                            },
                        dataSource:"lecm/orgstructure/ds/employee-positions",
                        bubblingLabel: bublingLabel,
                        allowCreate: false,
                        showActionColumn: <#if form.mode == "view">false<#else>true</#if>,
                        showCheckboxColumn: false
                    }).setMessages(${messages});

                    datagrid.draw();
                }

                function loadDeps() {
                    LogicECM.module.Base.Util.loadScripts([
                        'scripts/lecm-base/components/lecm-datagrid.js'
                    ], init);
                }

                YAHOO.util.Event.onDOMReady(function (){
                    loadDeps();
                });

            })();
        //]]></script>
    </@grid.datagrid>
</div>
