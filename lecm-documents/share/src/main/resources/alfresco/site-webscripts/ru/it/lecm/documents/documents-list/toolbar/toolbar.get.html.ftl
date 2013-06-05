<#assign id = args.htmlid>

<#assign showCreateButton = false/>
<#if showCreateBtn??>
	<#assign showCreateButton = showCreateBtn/>
</#if>

<#assign showSearchControl = true/>
<#if showSearch??>
    <#assign showSearchControl = showSearch/>
</#if>

<#assign exSearch = false/>
<#if showExSearchBtn??>
    <#assign exSearch = showExSearchBtn/>
</#if>

<#assign newRowButtonLabel = "button.new-row"/>
<#if newRowLabel??>
    <#assign newRowButtonLabel = newRowLabel/>
</#if>

<#assign newRowTitle = "label.create-row.title"/>
<#if newRowDialogTitle??>
    <#assign newRowTitle = newRowDialogTitle/>
</#if>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript">//<![CDATA[
function init() {
    new LogicECM.module.Documents.Toolbar("${id}").setMessages(${messages}).setOptions({
	    bubblingLabel: "${bubblingLabel!'documents'}",
        itemType: "${itemType!'lecm-document:base'}",
        destination:LogicECM.module.Documents.SETTINGS.nodeRef,
        newRowDialogTitle:"${newRowTitle}"
    });
}
YAHOO.util.Event.onDOMReady(init);
//]]></script>
<@comp.baseToolbar id true showSearchControl exSearch>
<#if showCreateButton>
<div class="new-row">
        <span id="${id}-newContractButton" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button" title="${msg(newRowButtonLabel)}">${msg(newRowButtonLabel)}</button>
           </span>
        </span>
</div>
</#if>
</@comp.baseToolbar>