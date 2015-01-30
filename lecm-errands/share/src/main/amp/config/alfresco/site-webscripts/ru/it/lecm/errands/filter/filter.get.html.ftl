<@script type="text/javascript" src="${url.context}/res/scripts/lecm-errands/errands-filter.js"/>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/components/docs-filter.css" />


<#assign id = args.htmlid>

<#assign over = false/>
<#if page.url.args.filterOver?? && page.url.args.filterOver == "true">
    <#assign over = true/>
</#if>
<script type="text/javascript">//<![CDATA[
    var errandsFilter = new LogicECM.module.Errands.Filter("${id}").setOptions({
        filterOver: "${over?string}",
	    gridBubblingLabel: "${args.gridBubblingLabel!'errands'}"
    }).setMessages(${messages});
//]]></script>

<div id="${id}-filters" class="toolbar filter errands-filter">
    <div class="flat-button">
        <label for="${id}-assign">${msg("label.filter-assign")}:</label>
        <span class="align-left yui-button yui-menu-button" id="${id}-assign">
            <span class="first-child">
               <button type="button" tabindex="0"></button>
            </span>
         </span>
        <select id="${id}-assign-menu">
        <#list filtersAssign as filter>
            <option value="${filter.type?html}">${msg("filter." + filter.label)}</option>
        </#list>
        </select>
        <label for="${id}-date">${msg("label.filter-date")}:</label>
        <span class="align-left yui-button yui-menu-button" id="${id}-date">
            <span class="first-child">
               <button type="button" tabindex="0"></button>
            </span>
         </span>
        <select id="${id}-date-menu">
        <#list filtersDate as filter>
            <option value="${filter.type?html}">${msg("filter." + filter.label)}</option>
        </#list>
        </select>
        <span class="important-checkbox">
            <input type="checkbox" class="formsCheckBox" id="${id}-importantCheck">
            <label class="checkbox" for="${id}-importantCheck">${msg("filter.important")}</label>
        </span>
        <span class="control-checkbox" >
            <input type="checkbox" class="formsCheckBox" id="${id}-controlCheck">
            <label class="checkbox" for="${id}-controlCheck">${msg("filter.control")}</label>
        </span>
    </div>
    <span class="align-right yui-button" id="${id}-applyButton">
        <span class="first-child">
           <button type="button" tabindex="1" title="${msg('label.button.apply')}">
               ${msg('label.button.apply')}
           </button>
        </span>
    </span>
</div>