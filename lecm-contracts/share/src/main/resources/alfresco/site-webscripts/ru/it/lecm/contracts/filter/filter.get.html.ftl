<#assign id = args.htmlid>

<div id="contracts-filters">
    <div style="background-color: white; margin: 20px 0; padding: 0; position: relative;">
        <h2 class="thin" style="margin-left:20px">${msg("label.contracts")}</h2>
        <div class="grey-filter-panel">
            <div class="contracts-filter-panel">
                <ul id="filter-groups-set" class="filterBlock">
                <#if statusesGroups??>
                    <#list statusesGroups as group>
                        <li>
                            <a href="${url.context}/page/contracts-list?query=${group.value}"
                               class="status-button text-cropped" title="${group.value}">${group.name}</a>
                        </li>
                    </#list>
                </#if>
                </ul>
            </div>
        </div>
    </div>
    <div style="background-color: white; margin: 20px 0; padding: 0; position: relative;">
        <h2 id="${id}-heading" class="thin">${msg("label.byStatus")}</h2>
        <div class="white-filter-panel">
            <div class="contracts-filter-panel" >
                <ul id="filter-statuses-set" class="filterBlock">
                <#if statusesList??>
                    <#assign count = 0/>
                    <#list statusesList as status>
                        <li>
                            <a href="${url.context}/page/contracts-list?query=${status}"
                               class="status-button text-cropped text-broken">${status}</a>
                        </li>
                        <#assign count = count +1 />
                    </#list>
                </#if>
                </ul>
            </div>
        </div>
    </div>

    <script type="text/javascript">//<![CDATA[
    (function () {
        function init() {
            Alfresco.util.createTwister("${id}-heading", "ContractsStatuses");
        }

        YAHOO.util.Event.onDOMReady(init);
    })();
    //]]>
    </script>
</div>
