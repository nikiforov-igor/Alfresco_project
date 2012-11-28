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
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript">//<![CDATA[
function init() {
	new LogicECM.module.OrgStructure.Toolbar("${id}").setMessages(${messages}).setOptions({
		bubblingLabel:"${bubblingLabel!''}"
	});
}
YAHOO.util.Event.onDOMReady(init);
//]]></script>
<@comp.baseToolbar id buttons searchBlock exSearch>
	<#if initButtons == "newUnitButton">
	<div class="new-row">
            <span id="${id}-newUnitButton" class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button">${msg('button.new-unit')}</button>
               </span>
            </span>
	</div>
	<#elseif initButtons == "newRowButtonStaff">
	<div class="new-row">
            <span id="${id}-newRowButtonStaff" class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button">${msg('button.new-row-staff')}</button>
               </span>
            </span>
	</div>
	<#else>
	<div class="new-row">
            <span id="${id}-newRowButton" class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button">${msg('button.new-row')}</button>
               </span>
            </span>
	</div>
	</#if>
</@comp.baseToolbar>