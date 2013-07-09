<#assign id = args.htmlid>

<script type="text/javascript">//<![CDATA[
var filters = new LogicECM.module.Documents.Filter("${id}").setOptions(
        {
            docType: ("${type}" != "") ? "${type}" : "lecm-base:document",
            gridBubblingLabel: "${args.bubblingLabel!"documents"}"
        }).setMessages(${messages});
//]]></script>

<div id="${id}-filters" class="toolbar flat-button">
    <div>
         <span class="align-left yui-button yui-menu-button" id="${id}-assign">
            <span class="first-child">
               <button type="button" tabindex="0"></button>
            </span>
         </span>
        <select id="${id}-assign-menu">
        <#list filterAssign as filter>
            <option value="${filter.type?html}">${msg("filter." + filter.label)}</option>
        </#list>
        </select>
        <span class="align-right yui-button" id="${id}-applyButton">
            <span class="first-child">
               <button type="button" tabindex="1" title="Применить" onclick="filters.onApplyButtonClick()">Применить</button>
            </span>
         </span>
    </div>
</div>