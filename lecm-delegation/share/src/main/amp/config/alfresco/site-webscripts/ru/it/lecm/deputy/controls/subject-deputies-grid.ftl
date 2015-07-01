<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign formId = args.htmlid>
<#assign aDateTime = .now>
<#assign controlId = fieldHtmlId + "-cntrl">
<#assign containerId = fieldHtmlId + "-container-" + aDateTime?iso_utc>
<#assign objectId = field.name?replace("-", "_")>

<#assign allowCreate = true/>
<#if field.control.params.allowCreate??>
    <#assign allowCreate = field.control.params.allowCreate/>
</#if>

<#assign allowDelete = "true"/>
<#if field.control.params.allowDelete??>
    <#assign allowDelete = field.control.params.allowDelete?lower_case/>
</#if>

<#assign allowEdit = "true"/>
<#if field.control.params.allowEdit??>
    <#assign allowEdit = field.control.params.allowEdit?lower_case/>
</#if>

<#assign showActions = true/>
<#if field.control.params.showActions??>
    <#assign showActions = field.control.params.showActions/>
</#if>

<#assign useBubbling = "true"/>
<#if field.control.params.useBubbling??>
    <#assign useBubbling = field.control.params.useBubbling?lower_case/>
<#else>
    <#assign useBubbling = "true"/>
</#if>

<#if useBubbling = "false">
    <#assign bubblingId = ""/>
<#else>
    <#assign bubblingId = containerId/>
</#if>

<#assign usePagination = false/>
<#if field.control.params.usePagination??>
    <#assign usePagination = field.control.params.usePagination/>
</#if>

<#assign showLabel = true/>
<#if field.control.params.showLabel??>
    <#assign showLabel = field.control.params.showLabel == "true"/>
</#if>

<div class="control with-grid" id="${controlId}">
    <#if showLabel>
        <label for="${controlId}" id="${controlId}-dtg-label">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false>
            <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
    </#if>
    <@grid.datagrid containerId false>
        <script type="text/javascript">//<![CDATA[
        (function () {

            function replaceLabel() {
                if(LogicECM.module.Deputy.Const.dictionaryDesc) {
                    var label = document.getElementById("${controlId}-dtg-label");
                    label.innerHTML = "Заместители по критерию: " + LogicECM.module.Deputy.Const.dictionaryDesc;
                }
            }

            YAHOO.util.Event.onAvailable("${controlId}-dtg-label", replaceLabel);


            function init() {
                LogicECM.module.Base.Util.loadScripts([
                    'scripts/lecm-base/components/advsearch.js',
                    'scripts/lecm-base/components/lecm-datagrid.js',
                    'scripts/lecm-deputy/deputy-subjects-datagrid.js'
                ], createDatagrid);
            }
            YAHOO.util.Event.onDOMReady(init);
            function createDatagrid() {

                var components = Alfresco.util.ComponentManager.find({id:'${formId}'});
                var currentEmployeeRef;
                if (components && components.length && components[0].options) {
                    currentEmployeeRef = components[0].options.nodeRef;
                }

                var datagrid = new LogicECM.module.Deputy.SubjectsGrid('${containerId}').setOptions({
                    currentEmployeeRef: currentEmployeeRef,
                    createFormTitleMsg: 'label.subject.deputy.grid',
                    editFormTitleMsg: 'label.subject.deputy.grid',
                    overrideSortingWith: false,
                    bubblingLabel: "subject-deputies-datagrid",
                    usePagination: false,
                    dataSource: 'lecm/deputyWithSubjects/list',
                    showCheckboxColumn: false,
                    allowCreate: true,
                    expandable: true,
                    expandDataSource: 'lecm/deputy/subjects-grid',
                    expandDataObj: {
                        itemType: LogicECM.module.Deputy.Const.itemType
                    },
                    datagridMeta:{
                        itemType: "lecm-deputy:deputy",
                        sort: 'lecm-deputy:employee-assoc-text-content|true',
                        nodeRef: <#if field.value?? && field.value != "">"${field.value}"<#else>"${form.arguments.itemId}"</#if>,
                        datagridFormId: "subject-deputy-grid",
                        actionsConfig: {
                            fullDelete: true
                        }
                    },
                    actions: [
                        {
                            type: "datagrid-action-link-subject-deputies-datagrid",
                            id: "onActionEdit",
                            permission: "edit",
                            label: "${msg("actions.edit")}"
                        },
                        {
                            type: "datagrid-action-link-subject-deputies-datagrid",
                            id: "onActionDelete",
                            permission: "delete",
                            label: "${msg("actions.delete-row")}"
                        }
                    ]
                });
                datagrid.draw();
            }
        })();
        //]]></script>
    </@grid.datagrid>
</div>
<div class="clear"></div>
