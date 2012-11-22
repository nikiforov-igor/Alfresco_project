<#assign id = args.htmlid>
<#--
<#assign showSearch = false/>
<#if showSearchBlock?? && showSearchBlock>
	<#assign showSearch = true/>
</#if>
<#assign showExSearch = false/>
<#if showSearchBlock?? && showExSearchBtn>
	<#assign showExSearch = true/>
</#if>
-->

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript">//<![CDATA[
function init() {
	new LogicECM.module.OrgStructure.Toolbar("${id}").setMessages(${messages}).setOptions({
		bubblingLabel:"${bubblingLabel!''}"
	});
}
YAHOO.util.Event.onDOMReady(init);
//]]></script>
<@comp.baseToolbar id true showSearchBlock showExSearchBtn>
	<#if showNewUnitBtn>
	<div class="new-row">
            <span id="${id}-newUnitButton" class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button">${msg('button.new-unit')}</button>
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