<#assign id = args.htmlid>
<div id="contracts-filters" class="contracts-filter-panel">
    <div class="contracts-filters-block">
        <h2 class="thin" style="margin-left: 16px;">${msg("label.contracts")}</h2>
        <hr/>
        <div id="filter-groups-set" class="filterBlock">
        <#if statusesGroups??>
            <#list statusesGroups as group>
                <span>
                            <a href="${url.context}/page/contracts-list?query=${group.value}"
                               class="status-button text-cropped" title="${group.value}">${group.name}</a>
                            <div class="total-tasks-count-right">${group.count}</div><br/>
                </span><br/>
            </#list>
        </#if>
        </div>
        <hr/>
    </div>
    <div class="contracts-filters-block">
        <h2 id="${id}-heading" class="thin">${msg("label.byStatus")}</h2>
        <div>
            <hr/>
            <div>
                <div id="filter-statuses-set" class="filterBlock">
                <#if statusesList??>
                    <#assign count = 0/>
                    <#list statusesList as status>
                        <span style="display: inline-block;">
                            <a href="${url.context}/page/contracts-list?query=${status}"
                               class="status-button text-cropped text-broken">${status}</a>
                        </span><br/>
                        <#assign count = count +1 />
                    </#list>
                </#if>
                </div>
            </div>
        </div>
        <hr/>
    </div>

    <script type="text/javascript">//<![CDATA[
    (function () {
        function init() {
            Alfresco.util.createTwister("${id}-heading", "ContractsStatuses");
            setTimeout(function () {
                LogicECM.module.Base.Util.setHeight();
            }, 10);
        }

        YAHOO.util.Event.onDOMReady(init);
    })();
    //]]>
    </script>
</div>
