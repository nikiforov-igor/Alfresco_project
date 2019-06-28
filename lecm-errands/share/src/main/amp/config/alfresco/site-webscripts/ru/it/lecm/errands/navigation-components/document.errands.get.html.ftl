<#assign id = args.htmlid>

<div class="errands-form" id="${id}_errands">
    <div class="panel-header">
        <div class="panel-title">${title!msg("label.title")}</div>
    </div>
    <div id="${id}_errands_container"></div>
</div>

<script type="text/javascript">
    var errandsComponent = null;
</script>

<script type="text/javascript">
    //<![CDATA[
    (function() {
        var Event = YAHOO.util.Event;

        LogicECM.module.Documents.ERRANDS_SETTINGS = LogicECM.module.Documents.ERRANDS_SETTINGS || <#if errandsSettings?? >${errandsSettings?string}<#else>{}</#if>;

        function initComponent() {
            new LogicECM.DocumentErrandsList("${id}_errands_container").setOptions({
				nodeRef: '${nodeRef}',
				componentHtmlId: "${componentHtmlId}"
            });

            errandsComponent = new LogicECM.module.Errands.dashlet.Errands("${id}").setOptions({
                itemType: "lecm-errands:document",
                destination: LogicECM.module.Documents.ERRANDS_SETTINGS.nodeRef,
                parentDoc: "${nodeRef}"
            }).setMessages(${messages});

        }

        Event.onContentReady("${id}_errands", function () {
            LogicECM.module.Base.Util.loadResources([
                'scripts/lecm-errands/lecm-document-errands.js',
                'scripts/lecm-errands/lecm-errands-dashlet.js',
                'scripts/lecm-errands/components/document-errands-list.js'
            ],[], initComponent);
        });
    })();
    //]]>
</script>