<#assign id = args.htmlid>

<script type="text/javascript">//<![CDATA[
    var errandsFilter = new LogicECM.module.Errands.Filter("${id}").setMessages(${messages});
//]]></script>

<div id="${id}-filters" class="toolbar flat-button">
    <div style="padding:5px;">${msg("label.filter")}:</div>
    <div style="padding-left: 10px; padding-bottom: 5px;">
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
        <div class="important-checkbox" style="display: inline-block;">
            <input type="checkbox" class="formsCheckBox" id="${id}-importantCheck">
            <label class="checkbox" for="${id}-importantCheck">${msg("filter.important")}</label>
        </div>
        <div class="control-checkbox" style="display: inline-block;">
            <input type="checkbox" class="formsCheckBox" id="${id}-controlCheck">
            <label class="checkbox" for="${id}-controlCheck">${msg("filter.control")}</label>
        </div>
        <span class="align-right yui-button" id="${id}-applyButton">
            <span class="first-child">
               <button type="button" tabindex="1" title="Применить" onclick="errandsFilter.onApplyButtonClick()">Применить</button>
            </span>
         </span>
    </div>
</div>