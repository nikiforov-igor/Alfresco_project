<@script type="text/javascript" src="${url.context}/res/scripts/lecm-documents/documents-list-author-filter.js"></@script>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/components/docs-filter.css" />


<#assign id = args.htmlid>
<#assign over = false/>
<#if page.url.args.filterOver?? && page.url.args.filterOver == "true">
    <#assign over = true/>
</#if>
<script type="text/javascript">//<![CDATA[
var authorFilter = new LogicECM.module.Documents.AuthorFilter("${id}").setOptions(
        {
            docType: ("${type}" != "") ? "${type}" : "lecm-base:document",
            filterOver: "${over?string}",
            gridBubblingLabel: "${args.gridBubblingLabel!"documents"}"
        }).setMessages(${messages});
//]]></script>

<div id="${id}-filters" class="toolbar filter documents-filter">
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
               <button type="button" tabindex="1" title="${msg("btn.apply")}">
                   ${msg("btn.apply")}
               </button>
            </span>
         </span>
</div>