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

<#assign newRowBtnType = "defaultActive"/>
<#if newRowButton??>
    <#assign newRowBtnType = newRowButton/>
</#if>

<#assign searchButtonsType = "defaultActive"/>
<#if searchButtons??>
    <#assign searchButtonsType = searchButtons/>
</#if>

<#assign newRowButtonLabel = "button.new-row"/>
<#if newRowLabel??>
    <#assign newRowButtonLabel = newRowLabel/>
</#if>

<#assign newSpanId = "${id}-newRowButton"/>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript">//<![CDATA[
function initToolbar() {
    new LogicECM.module.Base.Toolbar("${id}").setMessages(${messages}).setOptions({
        searchButtonsType: "${searchButtonsType?string}",
        bubblingLabel:"${bubblingLabel!''}",
        newRowButtonType: "${newRowBtnType?string}"
    });
}
YAHOO.util.Event.onDOMReady(initToolbar);
//]]></script>
<@comp.baseToolbar id buttons searchBlock exSearch>
    <div class="new-row">
        <span id="${newSpanId}" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button">${msg(newRowButtonLabel)}</button>
           </span>
        </span>
    </div>
</@comp.baseToolbar>