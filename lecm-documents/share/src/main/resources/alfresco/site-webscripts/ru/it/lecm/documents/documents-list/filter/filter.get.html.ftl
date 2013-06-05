<#assign id = args.htmlid>
<#assign f_label = filterLabel!'label.documents'>
<#assign pageLink = linkPage!"documents-list"/>

<div id="documents-filter" class="documents-filter-panel">
    <div class="documents-filter-block">
        <h2 class="thin" style="margin-left: 16px;">${msg(f_label)}</h2>
        <hr/>
        <div id="filter-groups-set" class="filterBlock">
        <#if statusesGroups??>
            <#list statusesGroups as group>
                <div class="text-cropped">
                    <a href="${url.context}/page/${pageLink}?query=${group.value}&formId=${group.name}" class="status-button"
                       title="<#if group.value == "*">Все<#else>${group.value}</#if>">${group.name}</a>
                    <span class="total-tasks-count-right">${group.count}</span><br/>
                </div>
            </#list>
        <#else>
            <div class="text-cropped">
                <a href="${url.context}/page/${pageLink}?query=*&formId=Все" class="status-button"
                   title="Все">Все</a>
                <span class="total-tasks-count-right">-</span><br/>
            </div>
        </#if>
        </div>
        <hr/>
    </div>
    <div class="documents-filter-block">
        <h2 id="${id}-heading" class="thin">${msg("label.byStatus")}</h2>
        <div>
            <hr/>
            <div>
                <div id="filter-statuses-set" class="filterBlock">
                <#if statusesList??>
                    <#assign count = 0/>
                    <#list statusesList as status>
                        <div class="text-cropped">
                            <a href="${url.context}/page/${pageLink}?query=${status}&formId=${status}"
                               class="status-button text-broken">${status}</a>
                        </div>
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
            Alfresco.util.createTwister("${id}-heading", "documentsStatuses");
            setTimeout(function () {
                LogicECM.module.Base.Util.setHeight();
            }, 10);
        }

        YAHOO.util.Event.onDOMReady(init);
    })();
    //]]>
    </script>
</div>
