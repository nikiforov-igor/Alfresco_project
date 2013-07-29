<#assign id = args.htmlid>

<script type="text/javascript">//<![CDATA[
    var errandsFilter = new LogicECM.module.Errands.Filter("${id}").setMessages(${messages});
//]]></script>

<div id="${id}-filters" class="toolbar errands-filter">
    <div style="font-weight: bold;">${msg("label.filter")}:</div>
    <div class="flat-button">
        <label for="${id}-assign">${msg("label.filter-assign")}</label>
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
        <label for="${id}-date">${msg("label.filter-date")}</label>
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
           <button type="button" tabindex="1" title="Применить" onclick="errandsFilter.onApplyButtonClick()">
               Применить
           </button>
        </span>
    </span>
</div>