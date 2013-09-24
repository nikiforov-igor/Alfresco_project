<#assign id = args.htmlid?js_string>

<script type="text/javascript">
    //<![CDATA[
    var errands;
    (function () {
        function init() {
            errands = new LogicECM.module.Errands.dashlet.Errands("${id}").setOptions(
                    {
                        itemType: "lecm-errands:document",
                        destination: LogicECM.module.Documents.ERRANDS_SETTINGS.nodeRef,
                        parentDoc:"${nodeRef}"
                    }).setMessages(${messages});
            var errandTasks = new LogicECM.module.Errands.Lists("${id}").setOptions(
                {
                    itemType: "lecm-errands:document",
                    nodeRef: "${nodeRef}",
                    containerId: "${id}-errands",
                    errandsUrl: "${errandsUrl}",
                    <#if isAnchor == "true">
                    anchorId: "${id}"
                    </#if>
                }).setMessages(${messages});
            errandTasks.onReady();
        }
        YAHOO.util.Event.onContentReady("${id}-errands",init);
    })();

    //]]>
</script>

<div class="list-category">
    <div class="list-category-title">${msg("errandslist.label.${label}")}</div>
    <div class="tasks-list-filter">
        <select id="${id}-errands-filter" style="margin-left: 13px;">
            <option value="all">${msg("errandslist.option.all")}</option>
            <option selected value="active">${msg("errandslist.option.active")}</option>
            <option value="complete">${msg("errandslist.option.completed")}</option>
        </select>
       <#if createButton??>
        <#if hasStatemachine && (createButton == "true")>
            <span class="lecm-dashlet-actions">
                <a id="${id}-action-add" href="javascript:void(0);" onclick="errands.onAddErrandClick()" class="add" title="${msg("errandslist.add.errand.tooltip")}">${msg("errandslist.add.errand")}</a>
            </span>
        </#if>
       </#if>
    </div>
    <div class="items" id="${id}-errands"></div>
</div>
