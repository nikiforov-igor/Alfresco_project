<#assign id = args.htmlid?js_string>

<script type="text/javascript">
    //<![CDATA[
    (function () {
        function init() {
            var errandTasks = new LogicECM.module.Errands.Lists("${id}").setOptions(
                {
                    itemType: "lecm-errands:document",
                    nodeRef: "${nodeRef}",
	                filter: "${filter}",
                    <#if isAnchor == "true">
                        anchorId: "${id}"
                    </#if>
                }).setMessages(${messages});
            errandTasks.onReady();
        }
        YAHOO.util.Event.onContentReady("${id}-meErrands",init);
    })();

    //]]>
</script>

<div class="list-category">
    <div class="errands-list-category-title">${msg("errandslist.label.my-errands")}</div>
    <div class="items" id="${id}-meErrands"></div>
</div>
<div class="list-category">
    <div class="errands-list-category-title">${msg("errandslist.label.issued-by-me")}</div>
	<div class="items" id="${id}-issuedByMeErrands"></div>
</div>
<div class="list-category">
    <div class="errands-list-category-title">${msg("errandslist.label.controlled-me")}</div>
	<div class="items" id="${id}-controlledMeErrands"></div>
</div>
<div class="list-category">
    <div class="errands-list-category-title">${msg("errandslist.label.other-list")}</div>
	<div class="items" id="${id}-otherErrands"></div>
</div>
