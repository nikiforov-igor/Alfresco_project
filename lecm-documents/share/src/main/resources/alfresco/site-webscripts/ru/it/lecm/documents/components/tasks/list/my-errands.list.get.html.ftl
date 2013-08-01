<#assign id = args.htmlid?js_string>

<script type="text/javascript">
    //<![CDATA[
    (function () {
        function init() {
            var errandTasks = new LogicECM.module.Errands.Tasks("${id}").setOptions(
                {
                    itemType: "lecm-errands:document",
                    nodeRef: "${nodeRef}",
                    containerId: "${id}-my-errands",
                    errandsUrl: "${errandsUrl}"
                }).setMessages(${messages});
            errandTasks.onReady();
        }
        YAHOO.util.Event.onContentReady("${id}-my-errands",init);
    })();

    //]]>
</script>

<div class="list-category">
    <div class="list-category-title">${msg("errandslist.label.${label}")}</div>
    <div class="tasks-list-filter">
        <select id="${id}-errands-filter" style="margin-left: 13px;">
            <option selected value="all">${msg("errandslist.option.all")}</option>
            <option value="active">${msg("errandslist.option.active")}</option>
            <option value="complete">${msg("errandslist.option.completed")}</option>
        </select>
       <#if createButton??>
        <#if createButton == "true">
            <span class="lecm-dashlet-actions">
                <a id="${id}-action-add" href="javascript:void(0);" onclick="errands.onAddErrandClick()" class="add" title="${msg("errandslist.add.errand.tooltip")}">${msg("errandslist.add.errand")}</a>
            </span>
        </#if>
       </#if>
    </div>
    <div class="items" id="${id}-my-errands"></div>
</div>
