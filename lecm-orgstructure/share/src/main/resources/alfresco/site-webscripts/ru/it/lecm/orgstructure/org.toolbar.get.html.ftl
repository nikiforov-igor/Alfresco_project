<#assign id = args.htmlid>

<#assign buttons = true/>
<#if showButtons??>
	<#assign buttons = showButtons/>
</#if>
<#assign searchBlock = true/>
<#if showSearchBlock??>
	<#assign searchBlock = showSearchBlock/>
</#if>
<#assign exSearch = false/>
<#if showExSearchBtn??>
	<#assign exSearch = showExSearchBtn/>
</#if>
<#assign initButtons = "newRowButton"/>
<#if typeButton??>
	<#assign initButtons = typeButton/>
</#if>
<#assign searchActive = true/>
<#if active??>
	<#assign searchActive = active/>
</#if>

<#assign newRowButtonLabel = "button.new-row"/>
<#if newRowLabel??>
	<#assign newRowButtonLabel = newRowLabel/>
</#if>

<#if initButtons == "newUnitButton">
	<#assign newUnitSpanId = "${id}-newUnitButton"/>
<#elseif initButtons == "newRowButtonStaff">
	<#assign newUnitSpanId = "${id}-newRowButtonStaff"/>
<#else>
	<#assign newUnitSpanId = "${id}-newRowButton"/>
</#if>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript">//<![CDATA[
function init() {
	new LogicECM.module.OrgStructure.Toolbar("${id}").setMessages(${messages}).setOptions({
		searchActive: "${searchActive?string}",
		bubblingLabel:"${bubblingLabel!''}"
	});
}
YAHOO.util.Event.onDOMReady(init);
//]]></script>
<@comp.baseToolbar id buttons searchBlock exSearch>
	<div class="new-row">
        <span id="${newUnitSpanId}" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button">${msg(newRowButtonLabel)}</button>
           </span>
        </span>
	</div>
</@comp.baseToolbar>