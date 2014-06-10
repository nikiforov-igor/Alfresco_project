<#include "/org/alfresco/components/form/form.dependencies.inc">
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-documents/documents-journal-menu.css" />
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-documents/documents-journal-menu.js"></@script>

<#assign id = args.htmlid/>

<div id="menu-buttons">
<#list filtersSet as filter>
    <div id="${filter.type}" class="journal-filter">
        <#list filter.cases as case>
            <span id="${filter.type}-${case.id}" title="${case.label}" class="hidden1"><#if case.value??>${case.value}</#if></span>
        </#list>
    </div>
    <#if filter.label??>
        <#assign label = filter.label/>
    <#else>
        <#assign label = msg(filter.label-id)/>
    </#if>
    <span id="menu-buttons-${filter.type}" class="yui-button yui-push-button menu-button">
            <span class="first-child">
                <button type="button" title="${label}">&nbsp;</button>
            </span>
    </span>
</#list>
</div>

<script type="text/javascript">//<![CDATA[

(function() {
    function init() {
        var menu = new LogicECM.module.DocumentsJournal.Menu("menu-buttons");
        menu.setMessages(${messages});
    }
    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>
