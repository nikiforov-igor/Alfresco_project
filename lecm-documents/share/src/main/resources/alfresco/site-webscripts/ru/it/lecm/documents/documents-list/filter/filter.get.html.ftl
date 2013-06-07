<#assign id = args.htmlid>
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

<div id="documents-filter" class="documents-filter-panel">
    <div class="documents-filter-block">
        <h2 class="thin" style="margin-left: 16px;">${filterTitle}</h2>
        <hr/>
        <div id="filter-groups-set" class="filterBlock">
        <#if statusesGroups??>
            <#list statusesGroups as group>
                <div class="text-cropped">
                    <#if isDocListPage>
                        <a href="${url.context}/page/${pageLink}?doctype=${page.url.args.doctype}&query=${group.value}&formId=${group.name}" class="status-button"
                           title="<#if group.value == "*">Все<#else>${group.value}</#if>">${group.name}</a>
                    <#else>
                        <a href="${url.context}/page/${pageLink}?query=${group.value}&formId=${group.name}" class="status-button"
                           title="<#if group.value == "*">Все<#else>${group.value}</#if>">${group.name}</a>
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
            <hr/>
            <div>
                <div id="filter-statuses-set" class="filterBlock">
                <#if statusesList??>
                    <#assign count = 0/>
                    <#list statusesList as status>
                        <div class="text-cropped">

                            <#if isDocListPage>
                                <a href="${url.context}/page/${pageLink}?doctype=${page.url.args.doctype}&query=${status}&formId=${status}"
                                   class="status-button text-broken">${status}</a>
                            <#else>
                                <a href="${url.context}/page/${pageLink}?query=${status}&formId=${status}"
                                   class="status-button text-broken">${status}</a>
                            </#if>
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
