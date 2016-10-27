<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign aDateTime = .now>
<#assign controlId = fieldHtmlId + "-cntrl">
<#assign containerId = fieldHtmlId + "-container-" + aDateTime?iso_utc>

<#assign allowCreate = true/>
<#if field.control.params.allowCreate??>
    <#assign allowCreate = field.control.params.allowCreate/>
</#if>

<#assign allowDelete = "true"/>
<#if field.control.params.allowDelete??>
    <#assign allowDelete = field.control.params.allowDelete?lower_case/>
</#if>

<#assign showActions = true/>
<#if field.control.params.showActions??>
    <#assign showActions = field.control.params.showActions/>
</#if>

<#assign usePagination = false/>
<#if field.control.params.usePagination??>
    <#assign usePagination = field.control.params.usePagination/>
</#if>

<div class="form-field lecm-checkboxtable with-grid" id="${controlId}">
    <label for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false>
        <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
<@grid.datagrid containerId false>
    <script type="text/javascript">//<![CDATA[
    (function () {

        function init() {
		    LogicECM.module.Base.Util.loadScripts([
				    'scripts/lecm-base/components/advsearch.js',
				    'scripts/lecm-base/components/lecm-datagrid.js',
			    ], createDatagrid);
	    }

	    function createDatagrid() {
		    var datagrid = new LogicECM.module.Base.DataGrid('${containerId}').setOptions({
			    usePagination: ${usePagination?string},
			    disableDynamicPagination: true,
			    showExtendSearchBlock: false,
			    actions: [{
				    type: "datagrid-action-link-${containerId}",
				    id: "onActionEdit",
				    permission: "edit",
				    label: "${msg("actions.edit")}"
			    }
				    <#if allowDelete = "true">
					    ,
					    {
						    type: "datagrid-action-link-${containerId}",
						    id: "onActionDelete",
						    permission: "delete",
						    label: "${msg("actions.delete-row")}"
					    }
				    </#if>
			    ],
			    datagridMeta: {
				    useFilterByOrg: false,
				    itemType: "${field.control.params.itemType!""}",
				    datagridFormId: "${field.control.params.datagridFormId!"datagrid"}",
				    createFormId: "${field.control.params.createFormId!""}",
			    nodeRef: <#if field.value?? && field.value != "">"${field.value}"<#else>"${form.arguments.itemId}"</#if>,
				    actionsConfig: {
					    fullDelete: "${field.control.params.fullDelete!"false"}"
				    }
			    },
			    dataSource:"${field.control.params.ds!"lecm/search"}",
			    bubblingLabel: "${containerId}",
				<#if field.control.params.fixedHeader??>
                    fixedHeader: ${field.control.params.fixedHeader},
				</#if>
			    <#if field.control.params.height??>
				    height: ${field.control.params.height},
			    </#if>
			    <#if field.control.params.configURL??>
				    configURL: "${field.control.params.configURL}",
			    </#if>
			    <#if field.control.params.repoDatasource??>
				    repoDatasource: ${field.control.params.repoDatasource},
			    </#if>
			    allowCreate: ${allowCreate?string},
			    showActionColumn: ${showActions?string},
			    showCheckboxColumn: false
		    }).setMessages(${messages});

		    datagrid.draw();
	    }

	    YAHOO.util.Event.onDOMReady(init);
    })();

    function changeFieldState(control, nodeRef) {
        Alfresco.util.Ajax.jsonRequest({
            method: "PUT",
            url: Alfresco.constants.PROXY_URI + "/lecm/statemachine/editor/datagrid/fields?nodeRef=" + encodeURIComponent(nodeRef) + "&value=" + control.checked,
            failureCallback: {
                fn: function () {
                    alert("Error");
                }
            }
        });
    }
    //]]></script>
</@grid.datagrid>
</div>
