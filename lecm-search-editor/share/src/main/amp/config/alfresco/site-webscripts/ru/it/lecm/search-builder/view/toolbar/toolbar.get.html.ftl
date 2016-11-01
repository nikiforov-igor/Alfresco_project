<#assign aDateTime = .now?string["dd-MM-yyyy_HH-mm-ss"]>
<#assign panelId = args.htmlid + aDateTime >
<#assign id = args.htmlid >

<script type="text/javascript">//<![CDATA[
(function(){
	function createToolbar() {
	    new LogicECM.module.SearchQueries.Toolbar("${id}").setOptions({
		    queryNodeRef: "${queryNodeRef!''}",
            editPath: "${editPath!''}",
            deletePath: "${deletePath!''}",
            bubblingLabel: "query-view-in-arm",
            panelId: "${panelId}"
	    }).setMessages(${messages});
	}

    function init() {
        LogicECM.module.Base.Util.loadResources([
            'scripts/lecm-base/components/lecm-toolbar.js',
            'scripts/lecm-arm/arm-documents-datagrid.js',
            'scripts/lecm-search-editor/search-query-toolbar.js'
        ], [], createToolbar);
        LogicECM.module.Base.Util.loadCSS([
                    'css/lecm-search-editor/search-queries.css'
                ],
                null,
                []);
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<@comp.baseToolbar id true false false>
<div class="hidden1">
    <div id="${panelId}-preferencesBlock" class="yui-panel">
        <div id="${panelId}-preference-head" class="hd">${msg("label.preference-block")}</div>
        <div id="${panelId}-preference-body" class="bd">
            <div id="${panelId}-preference-content">
                <div id="preferencesBlock-content" >
                    <div id="${panelId}-preferenceContainer" class="prreference">
                    <#-- Контейнер для отрисовки формы -->
                        <div id="preferencesBlock-forms" class="forms-container form-fields"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="create-row">
        <span id="${id}-editQueryButton" class="yui-button yui-push-button">
            <span class="first-child">
                <button type="button">${msg('lecm.editSearchQueryButton')}</button>
            </span>
        </span>
        <span id="${id}-columnsButton" class="yui-button yui-push-button">
            <span class="first-child">
                <button type="button">${msg('msg.action.pref')}</button>
            </span>
        </span>
        <span id="${id}-deleteQueryButton" class="yui-button yui-push-button">
                <span class="first-child">
                    <button type="button">${msg('lecm.deleteSearchQueryButton')}</button>
                </span>
        </span>
</div>
</@comp.baseToolbar>
