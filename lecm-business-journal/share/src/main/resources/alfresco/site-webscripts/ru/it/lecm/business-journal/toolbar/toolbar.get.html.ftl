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

<#assign newRowButtonLabel = "button.new-row"/>
<#if newRowLabel??>
	<#assign newRowButtonLabel = newRowLabel/>
</#if>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript">//<![CDATA[
function init() {
	new LogicECM.module.BusinessJournal.Toolbar("${id}").setMessages(${messages}).setOptions({
		bubblingLabel:"${bubblingLabel!''}"
	});
}
YAHOO.util.Event.onDOMReady(init);
//]]></script>

<@comp.baseToolbar id buttons true true>
	<#--<div class="new-row">
        <span id="${id}-newRecordButton" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button">${msg(newRowButtonLabel)}</button>
           </span>
        </span>
	</div>-->
</@comp.baseToolbar>