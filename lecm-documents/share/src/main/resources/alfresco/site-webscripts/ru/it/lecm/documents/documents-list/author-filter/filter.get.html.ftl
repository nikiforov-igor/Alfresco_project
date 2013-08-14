<#assign id = args.htmlid>
<#assign over = false/>
<#if page.url.args.filterOver??>
    <#assign over = true/>
</#if>
<script type="text/javascript">//<![CDATA[
var authorFilter = new LogicECM.module.Documents.AuthorFilter("${id}").setOptions(
        {
            docType: ("${type}" != "") ? "${type}" : "lecm-base:document",
            filterOver: "${over?string}",
            gridBubblingLabel: "${args.bubblingLabel!"documents"}"
        }).setMessages(${messages});
//]]></script>

<div id="${id}-filters" class="toolbar documents-filter">
    <div style="font-weight: bold;">${msg("label.filter")}:</div>
    <div class="flat-button">
         <span class="align-left yui-button yui-menu-button" id="${id}-author">
            <span class="first-child">
               <button type="button" tabindex="0"></button>
            </span>
         </span>
        <select id="${id}-author-menu">
        <#list filtersAuthor as filter>
            <option value="${filter.type?html}">${msg("filter." + filter.label)}</option>
        </#list>
        </select>
    </div>
        <span class="align-right yui-button" id="${id}-applyButton">
            <span class="first-child">
               <button type="button" tabindex="1" title="Применить" onclick="authorFilter.onApplyButtonClick()">
                   Применить
               </button>
            </span>
         </span>
</div>