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

<div class="form-field with-grid" id="${controlId}">
    <label for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false>
        <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
    <@grid.datagrid containerId>
        <script type="text/javascript">//<![CDATA[
            (function () {
                YAHOO.util.Event.onDOMReady(function (){
                    LogicECM.module.Base.DataGrid.prototype.onActionMakePrimary = function DataGridActions_onActionMakePrimary(p_item) {
                        var me = this;
                        var staffRow = p_item;
                        // Получаем для штатного расписания ссылку на сотрудника
                        var sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getStaffEmployeeLink?nodeRef=" + staffRow.nodeRef;
                        var callback = {
                            success:function (oResponse) {
                                var oResult = eval("(" + oResponse.responseText + ")");
                                if (oResult) {
                                    Alfresco.util.PopupManager.displayPrompt(
                                            {
                                                title:me.msg("message.position.primary.title"),
                                                text: me.msg("message.position.primary.prompt", staffRow.itemData["assoc_lecm-orgstr_element-member-position-assoc"].displayValue),
                                                buttons:[
                                                    {
                                                        text:me.msg("button.position.makePrimary"),
                                                        handler:function DataGridActions_onActionMakePrimary_make() {
                                                            this.destroy();
                                                            var onSuccess = function DataGrid_onActionEmployeeAdd_onSuccess(response) {
                                                                YAHOO.Bubbling.fire("datagridRefresh",
                                                                        {
                                                                            bubblingLabel:me.options.bubblingLabel
                                                                        });
                                                                Alfresco.util.PopupManager.displayMessage(
                                                                        {
                                                                            text:me.msg("message.position.primary.success")
                                                                        });
                                                            };
                                                            var onFailure = function DataGrid_onActionEmployeeAdd_onFailure(response) {
                                                                Alfresco.util.PopupManager.displayMessage(
                                                                        {
                                                                            text:me.msg("message.position.primary.failure")
                                                                        });
                                                            };
                                                            Alfresco.util.Ajax.jsonRequest(
                                                                    {
                                                                        url:Alfresco.constants.PROXY_URI + "/lecm/orgstructure/action/makePrimary",
                                                                        method:"POST",
                                                                        dataObj:{
                                                                            nodeRef:oResult.nodeRef
                                                                        },
                                                                        successCallback:{
                                                                            fn:onSuccess,
                                                                            scope:this
                                                                        },
                                                                        failureCallback:{
                                                                            fn:onFailure,
                                                                            scope:this
                                                                        }
                                                                    });
                                                        }
                                                    },
                                                    {
                                                        text:me.msg("button.cancel"),
                                                        handler:function DataGridActions_onActionMakePrimary_cancel() {
                                                            this.destroy();
                                                        },
                                                        isDefault:true
                                                    }
                                                ]
                                            });
                                } else {
                                    Alfresco.util.PopupManager.displayMessage(
                                            {
                                                text:this.msg("message.position.primary.failure")
                                            });
                                }
                            },
                            failure:function (oResponse) {
                                Alfresco.util.PopupManager.displayMessage(
                                        {
                                            text:this.msg("message.position.primary.failure")
                                        });
                            }
                        };
                        YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
                    };
                    LogicECM.module.Base.DataGrid.prototype.makePrimaryEvaluator = function DataGridActions_makeBossEvaluatorEvaluator(rowData) {
                        var itemData = rowData.itemData;
                        return itemData["assoc_lecm-orgstr_primary-position"].value == "false";
                    };
                    var datagrid = new LogicECM.module.Base.DataGrid('${containerId}').setOptions({
                        usePagination: false,
                        showExtendSearchBlock: false,
                        actions: [{
                                        type: "action-link-${containerId}",
                                        id: "onActionMakePrimary",
                                        permission: "edit",
                                        label: "${msg("actions.makePrimary")}",
	                                    evaluator:"makePrimaryEvaluator"
                                  }],
                        datagridMeta: {
                                itemType: "lecm-orgstr:staff-list",
                                datagridFormId: "employee-positions",
                                nodeRef: <#if field.value?? && field.value != "">"${field.value}"<#else>"${form.arguments.itemId}"</#if>
                            },
                        dataSource:"lecm/orgstructure/ds/employee-positions",
                        bubblingLabel: "${containerId}",
                        allowCreate: false,
                        showActionColumn: true,
                        showCheckboxColumn: false
                    }).setMessages(${messages});

                    datagrid.draw();
                });

            })();
        //]]></script>
    </@grid.datagrid>
</div>
