<#assign id = args.htmlid>

<#assign buttons = true/>
<#if showButtons??>
	<#assign buttons = showButtons/>
</#if>
<#assign searchBlock = true/>
<#if showSearchBlock??>
	<#assign searchBlock = showSearchBlock/>
</#if>

<#assign showStructureBlock = false/>
<#if showStructure??>
    <#assign showStructureBlock = showStructure/>
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

<#assign newUnitSpanId = "${id}-newRowButton"/>
<#assign structureId = "${id}-structure"/>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript">//<![CDATA[
function init() {
	new LogicECM.module.OrgStructure.Toolbar("${id}").setMessages(${messages}).setOptions({
        searchButtonsType: "${searchButtonsType?string}",
		bubblingLabel:"${bubblingLabel!''}",
        newRowButtonType: "${newRowBtnType?string}"
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
    <#if showStructureBlock >
        <div class="print">
            <span id="${structureId}" class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button">${msg(showStructureLabel)}</button>
               </span>
            </span>
        </div>
    </#if>
</@comp.baseToolbar>