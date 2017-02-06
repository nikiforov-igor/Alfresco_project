
<#assign id = args.htmlid>
<#assign jsid = args.htmlid?js_string>

<script type="text/javascript">
	(function() {
        function init() {
            LogicECM.module.Base.Util.loadCSS([
                'css/lecm-errands/document-errands-dashlet.css',
                'css/lecm-errands/errands-dashlet.css'
            ]);

            LogicECM.module.Base.Util.loadScripts([
                'scripts/lecm-errands/errands-by-doc-dashlet.js'
            ], createObject);

        }
        function createObject() {
            new LogicECM.dashlet.DocErrands("${id}").setOptions(
                    {
                        maxItems: 50,
                        parentDoc: "${nodeRef}",
                        errandJSON: <#if errand?? >${errand}<#else>{data:[]}</#if>
                    }).setMessages(${messages});
        }

        YAHOO.util.Event.onDOMReady(init);
	})();
</script>

<div class="errands-dashlet-content" id="${id}">
    <span class="my-errands">
        <img src="${url.context}/res/images/lecm-documents/errands/in.png" width="24" title="${msg('label.to-me-errand')}" id="${id}-in-img">
    </span>
    <div class="body issued-to-me-errands-block">
        <div id="${id}-errand"></div>
    </div>
    <span>
        <span class="assigned-errand-img">
            <img src="${url.context}/res/images/lecm-documents/errands/out.png" width="24" title="${msg('label.my-errands')}" id="${id}-out-img">
        </span>
        <div class="total-tasks-count">
            <#if mayCreateReErrand && hasStatemachine && hasPermission && isErrandsStarter && errand??>
            <span class="lecm-dashlet-actions">
                <a id="${id}-action-add" href="javascript:void(0);" onclick="errandsComponent.createReErrand('${errandObj.data[0].nodeRef}', '${errandObj.data[0].date}')" class="add"
                   title="${msg("dashlet.add.reerrand.tooltip")}">${msg("dashlet.add.reerrand")}</a>
            </span>
            </#if>
        </div>
    </span>
    <div class="body issued-by-me-errands-block" id="${id}-issued-paginator">
        <div id="${id}-issued-errands"></div>
    </div>
</div>
