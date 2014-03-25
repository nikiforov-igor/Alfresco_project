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

<#assign createBtnLabel = msg("button.new-row")/>

<#assign newRowButtonLabel = "button.new-row"/>
<#if args.newRowLabel??>
    <#assign newRowButtonLabel = args.newRowLabel/>
<#else>
    <#if args.itemType??>
        <#assign newRowButtonLabel = ("button." + args.itemType?replace(":","_") + ".new")/>
    </#if>
</#if>

<#if msg(newRowButtonLabel) != newRowButtonLabel>
    <#assign createBtnLabel = msg(newRowButtonLabel)/>
</#if>

<#assign newRowTitle = "label.create-row.title"/>
<#if args.newRowDialogTitle??>
    <#assign newRowTitle = args.newRowDialogTitle/>
<#else>
    <#if args.itemType??>
        <#assign newRowTitle = ("label.create-" + args.itemType?replace(":","_"))/>
    </#if>
</#if>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript">//<![CDATA[
function init() {
    new LogicECM.module.Documents.Toolbar("${id}").setMessages(${messages}).setOptions({
	    bubblingLabel: "${args.bubblingLabel!'documents'}",
        itemType: "${args.itemType!'lecm-document:base'}",
        destination:LogicECM.module.Documents.SETTINGS.nodeRef,
        newRowDialogTitle:"${newRowTitle}",
        createDialogWidth:"${args.createDialogWidth!"84em"}",
        createDialogClass:"${args.createDialogClass!""}"
    });
}
YAHOO.util.Event.onDOMReady(init);
//]]></script>
<@comp.baseToolbar id true showSearchControl exSearch>
<#if showCreateButton>
<div class="new-row">
        <span id="${id}-newDocumentButton" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button" title="${createBtnLabel}">${createBtnLabel}</button>
           </span>
        </span>
</div>
</#if>
</@comp.baseToolbar>