<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign gridId = args.htmlid/>
<#assign controlId = gridId + "-cntrl">
<#assign containerId = gridId + "-container">
<#assign nodeRef = args.nodeRef/>

<div class="form-field with-grid" id="bjHistory-${controlId}">
    <#--<label for="${controlId}">${msg("label.history.second")}:</label>-->
    

<@grid.datagrid containerId true gridId+"form" true>
    <script type="text/javascript">//<![CDATA[
    (function () {
        YAHOO.util.Event.onDOMReady(function (){
            var datagrid = new LogicECM.module.BusinessJournal.DataGrid('${containerId}').setOptions({
                usePagination: true,
                pageSize: 10,
                showExtendSearchBlock: true,
                datagridMeta: {
                    itemType: "lecm-busjournal:bjRecord",
                    datagridFormId: "bjHistory",
                    createFormId: "",
                    nodeRef: "${nodeRef}",
                    actionsConfig: {
                        fullDelete: "false"
                    }
                },
                dataSource:"lecm/business-journal/ds/history",
                allowCreate: false,
                showActionColumn: false,
                showCheckboxColumn: false,
                bubblingLabel: "${bubblingLabel!"bj-history-records"}",
                attributeForShow:"lecm-busjournal:bjRecord-date"
            }).setMessages(${messages});

            datagrid.draw();
        });

    })();
    //]]></script>

    <#--<!--[if IE]>-->
    <#--<iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe>-->
    <#--<![endif]&ndash;&gt;-->
    <#--<input id="yui-history-field" type="hidden" />-->
    <#--<div id="${gridId}-body" class="datagrid">-->
        <#--<div class="datagrid-meta">-->
        <#--<@grid.viewForm gridId+"form"/>-->
            <#--<h2 id="${gridId}-title"></h2>-->
            <#--<div id="${gridId}-description" class="datagrid-description"></div>-->
        <#--</div>-->
        <#--<div id="${gridId}-datagridBar" class="yui-ge datagrid-bar flat-button" style="display:none">-->
            <#--<div class="yui-u first align-center">-->
                <#--<div class="item-select">&nbsp;</div>-->
                <#--<div id="${gridId}-paginator" class="paginator"></div>-->
            <#--</div>-->
            <#--<div class="yui-u align-right">-->
                <#--<div class="items-per-page" style="visibility: hidden;">-->
                    <#--<button id="${gridId}-itemsPerPage-button">${msg("menu.items-per-page")}</button>-->
                <#--</div>-->
            <#--</div>-->
        <#--</div>-->

        <#--<div id="${gridId}-toolbar" style="display: none; margin-bottom: 3px;">-->
         <#--<span id="${gridId}-newRowButton" class="yui-button yui-push-button">-->
               <#--<span class="first-child">-->
                  <#--<button type="button">${msg('actions.add')}</button>-->
               <#--</span>-->
         <#--</span>-->
        <#--</div>-->
    <#--&lt;#&ndash;<#if showArchiveCheckBox>&ndash;&gt;-->
        <#--<div align="right" style="padding: 0.5em;">-->
            <#--<input type="checkbox" class="formsCheckBox" id="${gridId}-cbShowArchive" onChange="YAHOO.Bubbling.fire('archiveCheckBoxClicked', null)">-->
            <#--<label class="checkbox" for="${gridId}-cbShowArchive">Вторичные упоминания</label>-->
        <#--</div>-->
    <#--&lt;#&ndash;</#if>&ndash;&gt;-->
        <#--<div id="${gridId}-grid" class="grid"></div>-->

        <#--<div id="${gridId}-datagridBarBottom" class="yui-ge datagrid-bar datagrid-bar-bottom flat-button">-->
            <#--<div class="yui-u first align-center">-->
                <#--<div class="item-select">&nbsp;</div>-->
                <#--<div id="${gridId}-paginatorBottom" class="paginator"></div>-->
            <#--</div>-->
        <#--</div>-->

        <#--<!-- Action Sets &ndash;&gt;-->
        <#--<div style="display:none">-->
            <#--<!-- Action Set "More..." container &ndash;&gt;-->
            <#--<div id="${gridId}-moreActions">-->
                <#--<div class="onActionShowMore"><a href="#" class="show-more" title="${msg("actions.more")}"><span></span></a></div>-->
                <#--<div class="more-actions hidden"></div>-->
            <#--</div>-->

            <#--<!-- Action Set Templates &ndash;&gt;-->
            <#--<div id="${gridId}-actionSet" class="action-set simple">-->
            <#--&lt;#&ndash;<#if actionSet??>&ndash;&gt;-->
                <#--&lt;#&ndash;<#list actionSet as action>&ndash;&gt;-->
                    <#--&lt;#&ndash;<div class="${action.id}"><a rel="${action.permission!""}" href="${action.href}" class="datagrid-action-link ${action.type}" title="${msg(action.label)}"><span>${msg(action.label)}</span></a></div>&ndash;&gt;-->
                <#--&lt;#&ndash;</#list>&ndash;&gt;-->
            <#--&lt;#&ndash;</#if>&ndash;&gt;-->
            <#--</div>-->
        <#--</div>-->
    <#--</div>-->

</@grid.datagrid>
</div>