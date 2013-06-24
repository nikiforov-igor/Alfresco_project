<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign id = args.htmlid>

<#assign showCreateButton = true/>
<#if showCreateBtn??>
    <#assign showCreateButton = showCreateBtn/>
</#if>

<#assign showSearchControl = false/>
<#if showSearch??>
    <#assign showSearchControl = showSearch/>
</#if>

<#assign exSearch = false/>
<#if showExSearchBtn??>
    <#assign exSearch = showExSearchBtn/>
</#if>

<#assign createBtnLabel = msg("button.new-row")/>
<#if args.newRowLabel??>
    <#assign createBtnLabel = args.newRowLabel/>
</#if>

<#assign newRowTitle = "label.create-row.title"/>
<#if args.newRowDialogTitle??>
    <#assign newRowTitle = args.newRowDialogTitle/>
</#if>

<script type="text/javascript">//<![CDATA[
function init() {
    new LogicECM.module.ReportsEditor.Toolbar("${id}").setMessages(${messages}).setOptions({
        bubblingLabel: "${args.bubblingLabel!'reports'}",
        newRowDialogTitle: "${newRowTitle}"
    });
}
YAHOO.util.Event.onDOMReady(init);
//]]></script>

<@comp.baseToolbar id true showSearchControl exSearch>
    <#if showCreateButton>
    <div class="new-row">
    <span id="${id}-newReportButton" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button" title="${msg(createBtnLabel)}">${msg(createBtnLabel)}</button>
           </span>
    </span>
    </div>
    </#if>
</@comp.baseToolbar>