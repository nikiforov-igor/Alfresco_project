<#assign id = args.htmlid>
<div id="contracts-filters" class="contracts-filter-panel">
    <div class="contracts-filters-block">
        <h2 class="thin" style="margin-left: 16px;">${msg("label.contracts_docs")}</h2>
        <hr/>
        <div id="filter-groups-set" class="filterBlock">
        <#if filters??>
            <#list filters as filter>
                <div class="text-cropped">
                    <a href="${url.context}/page/contracts-documents?query=${filter.value}"  style="margin:5px;"
                       class="status-button" title="<#if filter.value == "*">${msg("filter.type.ALL")}<#else>${filter.value}</#if>">${msg("filter.type." + filter.type)}</a>
                    <span class="total-tasks-count-right">${filter.count}</span><br/>
                </div>
            </#list>
        </#if>
        </div>
        <hr/>
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
