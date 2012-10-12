<#assign id = args.htmlid>
<script type="text/javascript">//<![CDATA[
new LogicECM.module.OrgStructure.Toolbar("${id}").setMessages(${messages});
//]]></script>
<div id="${args.htmlid}-body" class="datalist-toolbar toolbar">
    <div id="${args.htmlid}-headerBar" class="header-bar flat-button theme-bg-2">
        <div class="left">
            <#if showNRB>
            <div class="new-row">
            <span id="${id}-newRowButton" class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button">${msg('button.new-row')}</button>
               </span>
            </span>
            </div>
            </#if>
            <#if showNUB>
            <div class="new-row">
            <span id="${id}-newUnitButton" class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button">${msg('button.new-unit')}</button>
               </span>
            </span>
            </div>
            </#if>
        </div>
    </div>
</div>