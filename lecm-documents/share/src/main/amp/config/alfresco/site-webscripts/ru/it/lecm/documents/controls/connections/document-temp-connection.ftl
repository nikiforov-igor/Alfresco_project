<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign fieldValue=field.value!"">
<#assign controlId = fieldHtmlId + "-cntrl">
<#assign containerId = fieldHtmlId + "-container-cntrl">
<#assign bubblingId = fieldHtmlId + "-container-bubbling-cntrl-" + .now?long?c>

<#assign showMandatoryIndicator = true>
<#if field.control.params.showMandatoryIndicator?? && field.control.params.showMandatoryIndicator == "false">
    <#assign showMandatoryIndicator = false>
</#if>

<#assign isFieldMandatory = false>
<#if field.control.params.mandatory??>
    <#if field.control.params.mandatory == "true">
        <#assign isFieldMandatory = true>
    </#if>
<#elseif field.mandatory??>
    <#assign isFieldMandatory = field.mandatory>
<#elseif field.endpointMandatory??>
    <#assign isFieldMandatory = field.endpointMandatory>
</#if>

<div class="control document-temp-connection">
    <div class="label-div">
        <label for="${controlId}-autocomplete-input">
        ${field.label?html}:
        <#if isFieldMandatory && showMandatoryIndicator><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if>
        </label>
        <input type="hidden" id="${controlId}-removed" name="${field.name}_removed"/>
        <input type="hidden" id="${controlId}-added" name="${field.name}_added"/>
        <input type="hidden" id="${controlId}-selectedItems"/>
    </div>
    <div class="container">
        <div class="control with-grid" id="${controlId}">
        <@grid.datagrid containerId false>
            <script type="text/javascript">//<![CDATA[
            (function () {

                function init() {
                    LogicECM.module.Base.Util.loadScripts([
                        'scripts/lecm-base/components/advsearch.js',
                        'scripts/lecm-base/components/lecm-datagrid.js',
                        'scripts/documents/connections/document-temp-connection.js'
                    ], initData);
                }

                YAHOO.util.Event.onDOMReady(init);

                function initData() {
                    var sUrl = Alfresco.constants.PROXY_URI + "/lecm/document/connections/api/tempdir";
                    var me = this;
                    var callback = {
                        success:function (oResponse) {
                            var oResults = eval("(" + oResponse.responseText + ")");
                            if (oResults != null) {
                                createControl(oResults.nodeRef);
                            }
                        },
                        failure:function (oResponse) {
                            YAHOO.log("Failed to process XHR transaction.", "info", "example");
                        },
                        argument:{
                            context:this
                        },
                        timeout: 60000
                    };
                    YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
                }

                function createControl(tempdir) {
                    var datagrid = new LogicECM.module.Base.DataGrid("${containerId}").setOptions({
                        usePagination: false,
                        showExtendSearchBlock: false,
                        actions: [
                                {
                                    type: "datagrid-action-link-<#if bubblingId != "">${bubblingId}<#else>-custom</#if>",
                                    id: "onActionEdit",
                                    permission: "edit",
                                    label: "${msg("actions.edit")}"
                                },
                                {
                                    type: "datagrid-action-link-<#if bubblingId != "">${bubblingId}<#else>custom</#if>",
                                    id: "onActionDelete",
                                    permission: "delete",
                                    label: "${msg("actions.delete-row")}"
                                }
                        ],
                        datagridMeta: {
                            itemType: "lecm-connect:connection",
                            useChildQuery: true,
                            nodeRef: tempdir,
                            createFormId: "connection-types-not-filtered",
                            actionsConfig: {
                                fullDelete: "true"
                            },
                            sort: "${field.control.params.sort!"cm:name|true"}"
                        },
                        editForm: "connection-types-not-filtered",
                        bubblingLabel: "${bubblingId}",
                        height: 150,
                        allowCreate: true,
                        showActionColumn: true,
                        showCheckboxColumn: false,
                        fixedHeader: true
                    }).setMessages(${messages});
                    datagrid.draw();

                    var tempConnection = new LogicECM.module.Connection.TempConnection("${controlId}").setOptions({
                        bubblingId: "${bubblingId}",
                        datagrid: datagrid
                    });
                }
            })();
            //]]></script>
        </@grid.datagrid>
    </div>
</div>