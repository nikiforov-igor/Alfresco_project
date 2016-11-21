<#include "/org/alfresco/components/component.head.inc">
<#include "association-tree-picker-dialog.inc.ftl">

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign pickerId = controlId + "-picker">
<#assign params = field.control.params>

<#assign showParentNodeInTreeView = true>
<#if params.showParentNodeInTreeView?? && params.showParentNodeInTreeView == "false">
	<#assign showParentNodeInTreeView = false>
</#if>

<#assign useDeferedReinit = false>
<#if params.useDeferedReinit?? && params.useDeferedReinit == "true">
    <#assign useDeferedReinit = true>
</#if>
<#assign fieldName = field.name>
<#if params.fieldName??>
    <#assign fieldName = params.fieldName>
</#if>

<div id="${controlId}-edt" class="control association-tree-control editmode">
	<div class="label-div">
		<label for="${controlId}">
		${field.label?html}:
			<#if field.endpointMandatory!false || field.mandatory!false>
				<span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
			</#if>
		</label>
	</div>
	<div id="${controlId}" class="container">
        <input type="hidden" id="${controlId}-added" name="${fieldName}_added"/>
        <input type="hidden" id="${controlId}-removed" name="${fieldName}_removed"/>
        <input type="hidden" id="${fieldHtmlId}" name="${fieldName}" value="${field.value?html}" />

		<div class="value-div">
            <div id="${pickerId}-treeSelector" class="yui-u panel-left tree object-finder">
                <div id="${pickerId}-groups" class="picker-items tree-items ygtv-highlight">

                </div>
            </div>
			<div id="${controlId}-currentValueDisplay" class="control-selected-values hidden1"></div>
		</div>
	</div>
	<div id="${controlId}-autocomplete-container"></div>
</div>
<div class="clear"></div>

<script type="text/javascript">
	<#assign optionSeparator="|">
	<#assign labelSeparator=":">

	(function() {
		function init() {
			LogicECM.module.Base.Util.loadResources([
				'scripts/lecm-base/components/lecm-association-tree-control.js'
			], [
                'css/lecm-base/components/association-tree-control.css'
            ], createControl);
		}
		function createControl(){
			new LogicECM.module.AssociationTreeControl("${fieldHtmlId}").setOptions({
                <#if params.rootLocationArg??>
                    rootLocation: "${form.arguments[params.rootLocationArg]}",
                <#elseif params.rootLocation??>
                    rootLocation: "${params.rootLocation}",
                </#if>

                showParentNodeInTreeView: ${showParentNodeInTreeView?string},
                <#if field.mandatory??>
                    mandatory: ${field.mandatory?string},
                <#elseif field.endpointMandatory??>
                    mandatory: ${field.endpointMandatory?string},
                </#if>
                <#if params.sortProp??>
                    sortProp: "${params.sortProp}",
                </#if>
                <#if params.treeNodeSubstituteString??>
                    treeNodeSubstituteString: "${params.treeNodeSubstituteString}",
                </#if>
                <#if params.treeNodeTitleSubstituteString??>
                    treeNodeTitleSubstituteString: "${params.treeNodeTitleSubstituteString}",
                </#if>
                <#if params.changeItemsFireAction??>
                    changeItemsFireAction: "${params.changeItemsFireAction}",
                </#if>
                <#if args.ignoreNodes??>
                    ignoreNodes: "${args.ignoreNodes}".split(","),
                </#if>
				currentValue: "${field.value!''}",
                <#if params.rootNodeScript??>
                    rootNodeScript: "${params.rootNodeScript}",
                </#if>
                <#if params.treeBranchesDatasource??>
                    treeBranchesDatasource: "${params.treeBranchesDatasource}",
                </#if>

				itemType: "${params.endpointType ! field.endpointType}",
				useDeferedReinit: ${useDeferedReinit?string},
				fieldId: "${field.configName}",
				formId: "${args.htmlid}"
			}).setMessages( ${messages} );
		}
		YAHOO.util.Event.onDOMReady(init);
	})();
</script>
