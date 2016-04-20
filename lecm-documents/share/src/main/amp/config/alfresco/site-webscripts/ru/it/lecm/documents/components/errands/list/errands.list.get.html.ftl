<#assign id = args.htmlid?js_string>

<script type="text/javascript">
    //<![CDATA[
    (function () {
        function init() {
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
    <div class="errands-list-filter">
        <select id="${id}-errands-filter">
            <option value="all">${msg("errandslist.option.all")}</option>
            <option selected value="active">${msg("errandslist.option.active")}</option>
            <option value="complete">${msg("errandslist.option.completed")}</option>
        </select>
       <#if createButton??>
        <#if isErrandsStarter && hasStatemachine && isRegistered && (createButton == "true")>
            <span class="lecm-dashlet-actions">
                <a id="${id}-action-add" href="javascript:void(0);" onclick="errandsComponent.createChildErrand()" class="add" title="${msg("errandslist.add.errand.tooltip")}">${msg("errandslist.add.errand")}</a>
            </span>
        </#if>
       </#if>
    </div>
    <div class="items" id="${id}-errands"></div>
</div>
