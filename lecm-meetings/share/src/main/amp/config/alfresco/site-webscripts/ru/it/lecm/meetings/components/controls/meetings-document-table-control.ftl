<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign params = field.control.params/>

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign toolbarId = fieldHtmlId + "-toolbar">
<#assign aDateTime = .now>
<#assign containerId = fieldHtmlId + "-container-" + aDateTime?iso_utc>
<#assign bubblingId = containerId/>

<#assign expandable="false"/>
<#if params.expandable??>
    <#assign expandable = params.expandable/>
</#if>

<#assign showSearch=false/>
<#if params.showSearch?has_content>
    <#assign showSearch = params.showSearch=="true"/>
</#if>

<#assign showLabel = false>
<#if field.control.params.showLabel?? &&  field.control.params.showLabel == "true">
    <#assign showLabel = true>
</#if>

<#assign expandDataSource=""/>
<#if params.expandDataSource?has_content>
    <#assign expandDataSource = params.expandDataSource/>
</#if>


<#assign attributeForShow = ""/>
<#if params.attributeForShow??>
    <#assign attributeForShow = params.attributeForShow/>
</#if>

<#assign toolbar = "true"/>
<#if params.toolbar??>
    <#assign toolbar = params.toolbar/>
</#if>

<#assign refreshAfterCreate = "false"/>
<#if params.refreshAfterCreate??>
    <#assign refreshAfterCreate = params.refreshAfterCreate/>
</#if>

<#assign showActions = "true"/>
<#if params.showActions??>
    <#assign showActions = params.showActions/>
</#if>

<#assign usePagination = "true"/>
<#if params.usePagination??>
    <#assign usePagination = params.usePagination/>
</#if>

<#assign showCreateButton = true/>
<#if field.control.params.showCreateBtn??>
    <#assign showCreateButton = field.control.params.showCreateBtn/>
</#if>

<#assign isTableSortable = false/>
<#if field.control.params.isTableSortable??>
    <#assign isTableSortable = field.control.params.isTableSortable/>
</#if>

<#assign sort = ""/>
<#if field.control.params.sort??>
    <#assign sort = field.control.params.sort/>
</#if>

<#assign jsObjectName = "LogicECM.module.MeetingsDocumentTable"/>
<#if field.control.params.jsObjectName??>
    <#assign jsObjectName = field.control.params.jsObjectName/>
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

<#assign editable = ((params.editable!"true") == "true") && !(field.disabled) && (form.mode?string=="edit") >

<script type="text/javascript">//<![CDATA[
(function() {
	function drawForm(){
		var control = new ${jsObjectName}("${fieldHtmlId}").setMessages(${messages});
		control.setOptions(
			{
				currentValue: "${field.value!""}",
                usePagination: ${usePagination?string},
				messages: ${messages},
				bubblingLabel: "${bubblingId}",
                <#if toolbar == "true">
				toolbarId: "${toolbarId}",
                </#if>
				containerId: "${containerId}",
				datagridFormId: "${params.datagridFormId!"datagrid"}",
				attributeForShow: "${attributeForShow}",
				mode: "${form.mode?string}",
				disabled: ${field.disabled?string},
				isTableSortable: ${isTableSortable?string},
                sort: "${sort?string}",
                externalCreateId: "${form.arguments.externalCreateId!""}",
                refreshAfterCreate: ${refreshAfterCreate?string},
				<#if params.deleteMessageFunction??>
					deleteMessageFunction: "${params.deleteMessageFunction}",
				</#if>
				<#if params.editFormTitleMsg??>
					editFormTitleMsg: "${params.editFormTitleMsg}",
				</#if>
			    <#if params.createFormTitleMsg??>
				    createFormTitleMsg: "${params.createFormTitleMsg}",
				</#if>
				<#if params.viewFormTitleMsg??>
					viewFormTitleMsg: "${params.viewFormTitleMsg}",
				</#if>
                expandable: ${expandable?string},
                <#if expandDataSource?has_content>
                    expandDataSource: "${expandDataSource}",
                </#if>
                showActions: <#if editable>${showActions?string}<#else>false</#if>
			});
	}
	function init() {
        LogicECM.module.Base.Util.loadResources([
	        'scripts/lecm-base/components/advsearch.js',
            'modules/simple-dialog.js',
	        'scripts/lecm-base/components/lecm-datagrid.js',
            'scripts/lecm-base/components/lecm-toolbar.js',
            'scripts/documents/tables/lecm-document-table.js',
            'scripts/lecm-meetings/components/lecm-meetings-document-table.js',
	        <#if params.jsDependencies??>
		        <#list params.jsDependencies?split(",") as js>
			        '${js}',
		        </#list>
	        </#if>
		],
		[
            'css/components/document-table-control.css',
			'css/lecm-meetings/meeting-datagrid.css'
        ], drawForm);
    }
	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div class="control document-table-control with-grid">
	<div class="label-div">
		<#if showLabel>
			<label for="${fieldHtmlId}">
			${field.label?html}:
				<#if isFieldMandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if>
			</label>
		</#if>
	</div>
    <div class="container document-table-width">
        <div class="value-div">
        <#if toolbar == "true" && form.mode?string=="edit">
            <div id="${toolbarId}">
                <@comp.baseToolbar toolbarId true showSearch false>
                    <#if showCreateButton>
                        <div class="new-row">
                                    <span id="${toolbarId}-newRowButton" class="yui-button yui-push-button addAgendaItemSpanNewRow">
                                       <span class="first-child addAgendaItemSpanFirstChild">
                                          <button type="button"
                                                  title="${msg("label.table.row.create.title")}">${msg("label.table.row.create.title")}</button>
                                       </span>
                                    </span>
                        </div>
                    </#if>
                </@comp.baseToolbar>
            </div>
        </#if>
            <div id="${controlId}">
                <@grid.datagrid containerId false>
                    <div class="hidden1">
                        <!-- Action Set "More..." container -->
                        <div id="${containerId}-otherMoreActions">
                            <div class="onActionShowMore"><a href="#" class="show-more"
                                                             title="${msg("actions.more")}"><span></span></a>
                            </div>
                            <div class="more-actions hidden"></div>
                        </div>
                        <div id="${containerId}-otherActionSet" class="action-set simple"></div>
                    </div>
                </@grid.datagrid>
                 <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}"/>
            </div>
        </div>
    </div>
</div>
<div class="clear"></div>