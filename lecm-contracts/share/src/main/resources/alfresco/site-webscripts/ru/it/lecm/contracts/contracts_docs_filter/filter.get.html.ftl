<#assign id = args.htmlid>
<#if page.url.args.query?? && page.url.args.query != "">
    <#assign query = page.url.args.query/>
</#if>
<#assign statusesFilterKey = '.documents-list-statuses-filter'/>

<div id="contracts-filters" class="contracts-filter-panel">
    <div class="contracts-filters-block">
        <div id="filter-groups-set" class="filterBlock">
        <#if filters??>
            <#list filters as filter>
                <div class="text-cropped <#if query?? && query == filter.value>selected</#if>">
                    <a href="#" class="status-button"
                       title="<#if filter.value == "*">${msg("filter.type.ALL")}<#else>${filter.value}</#if>" onclick="LogicECM.module.Documents.filtersManager.save('${statusesFilterKey}', 'query=${filter.value}', true); return false;">${msg("filter.type." + filter.type)}</a>
                    <span class="total-tasks-count-right">${filter.count}</span><br/>
                </div>
            </#list>
        </#if>
        </div>
    </div>

    <script type="text/javascript">//<![CDATA[
    (function () {
        function init() {
            setTimeout(function () {
                LogicECM.module.Base.Util.setHeight();
            }, 10);
        }

        YAHOO.util.Event.onDOMReady(init);
    })();
    //]]>
    </script>
</div>
