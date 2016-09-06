<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />
<#include "lecm-dnd-uploader-container.ftl">

<#assign params = field.control.params/>

<script type="text/javascript">//<![CDATA[
(function() {
	function init() {
		LogicECM.module.Base.Util.loadCSS([
			'css/lecm-base/components/lecm-dnd-uploader-control-with-preview.css'
		]);
	}
	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div class="control dnd-uploader dnd-uploader-with-preview editmode">
	<input id="${fieldHtmlId}" type="hidden" class="autocomplete-input" name="${field.name}" value="${field.value?html}"/>
	<input type="hidden" id="${fieldHtmlId}-removed" name="${field.name}_removed"/>
	<input type="hidden" id="${fieldHtmlId}-added" name="${field.name}_added"/>

<#assign showAttsLabel = true/>
<#assign showAttsList = true/>
<#if params.showAttsLabel?? && params.showAttsLabel == "false">
	<#assign showAttsLabel = false/>
</#if>
<#assign suppressRefreshEvent = "false"/>
<#if params.suppressRefreshEvent?? && params.suppressRefreshEvent == "true">
	<#assign suppressRefreshEvent = "true"/>
</#if>
<#if params.showAttsList?? && params.showAttsList == "false">
	<#assign showAttsList = false/>
</#if>
<#if showAttsLabel>
	<div class="label-div">
		<label>
		${field.label?html}:
			<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if>
		</label>
	</div>
</#if>
	<div class="container">
		<@renderDndUploaderContainerHTML fieldHtmlId field form suppressRefreshEvent "true"/>
		<div>
			<div class="buttons-div">
				<span class="show-preview-button">
					<input type="button" id="${fieldHtmlId}-show-preview-button" name="-" value=""/>
				</span>
			</div>
			<div class="value-div">
			<#if showAttsList>
				<ul id="${fieldHtmlId}-attachments" class="attachments-list"></ul>
			</#if>
			</div>
		</div>
	</div>
</div>
<div class="clear"></div>
