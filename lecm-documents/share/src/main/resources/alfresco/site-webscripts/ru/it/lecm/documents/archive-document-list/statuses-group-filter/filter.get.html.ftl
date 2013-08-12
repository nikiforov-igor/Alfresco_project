<#assign id = args.htmlid>
<#assign statusesFilterKey = 'documents-list-statuses-filter'/>

<#assign f_label = args.filterLabel!'label.documents'>
<#if args.filterLabel??>
    <#assign f_label = args.filterLabel/>
<#else>
    <#if args.itemType??>
        <#assign f_label = ("label.filter-" + args.itemType?replace(":","_"))/>
    </#if>
</#if>

<#assign filterTitle = msg("label.documents")/>
<#if msg(f_label) != f_label>
    <#assign filterTitle = msg(f_label)/>
</#if>
<#assign pageLink = args.linkPage!"documents-list"/>

<#assign isDocListPage = false/>
<#if page.url.args.doctype?? && page.url.args.doctype != "">
    <#assign isDocListPage = true/>
</#if>
<#if page.url.args.formId?? && page.url.args.formId != "">
    <#assign formId = page.url.args.formId/>
</#if>

<#assign filterOver = false/>
<#if page.url.args.filterOver??>
    <#assign filterOver = true/>
</#if>

<div id="documents-filter" class="documents-filter-panel">
    <div class="documents-filter-block">
        <div id="filter-groups-set" class="filterBlock">
        <#if statusesGroups??>
            <#list statusesGroups as group>
                <div class="text-cropped <#if formId?? && formId == group.name>selected</#if>">
                    <#if isDocListPage>
                        <a href="#"
                           class="status-button" title="<#if group.value == "*">Все<#else>${group.value}</#if>" onclick="LogicECM.module.Documents.filtersManager.save('${statusesFilterKey}', 'query=${group.value}&formId=${group.name}', true); return false;">${group.name}</a>
                    <#else>
                        <a href="#"
                           class="status-button" title="<#if group.value == "*">Все<#else>${group.value}</#if>" onclick="LogicECM.module.Documents.filtersManager.save('${statusesFilterKey}', 'query=${group.value}&formId=${group.name}', true); return false;">${group.name}</a>
                    </#if>
                    <span class="total-tasks-count-right">${group.count}</span><br/>
                </div>
            </#list>
        <#--<#else>-->
            <#--<div class="text-cropped">-->
                <#--<#if isDocListPage>-->
                    <#--<a href="${url.context}/page/${pageLink}?doctype=${page.url.args.doctype}&query=*&formId=Все" class="status-button"-->
                       <#--title="Все"</#if>">Все</a>-->
                <#--<#else>-->
                    <#--<a href="${url.context}/page/${pageLink}?query=*&formId=Все" class="status-button" title="Все">Все</a>-->
                <#--</#if>-->
                <#--<span class="total-tasks-count-right">-</span><br/>-->
            <#--</div>-->
        </#if>
        </div>
        <hr/>
    </div>
    <div class="documents-filter-block">
        <h2 id="${id}-heading" class="thin">${msg("label.byStatus")}</h2>
        <div>
            <div id="filter-statuses-set" class="filterBlock">
            <#if statusesList??>
                <#assign count = 0/>
                <#list statusesList as status>
                    <div class="text-cropped <#if formId?? && formId == status>selected</#if>">

                        <#if isDocListPage>
                            <a href="#"
                               class="status-button text-broken" onclick="LogicECM.module.Documents.filtersManager.save('${statusesFilterKey}', 'query=${status}&formId=${status}', true); return false;">${status}</a>
                        <#else>
                            <a href="#"
                               class="status-button text-broken" onclick="LogicECM.module.Documents.filtersManager.save('${statusesFilterKey}', 'query=${status}&formId=${status}', true); return false;">${status}</a>
                        </#if>
                    </div>
                    <#assign count = count +1 />
                </#list>
            </#if>
            </div>
        </div>
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
