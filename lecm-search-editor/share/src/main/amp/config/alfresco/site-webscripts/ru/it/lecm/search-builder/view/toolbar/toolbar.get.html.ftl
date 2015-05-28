<#assign id = args.htmlid>

<script type="text/javascript">//<![CDATA[
(function(){
	function createToolbar() {
	    new LogicECM.module.SearchQueries.Toolbar("${id}").setOptions({
		    queryNodeRef: "${queryNodeRef!''}",
            editPath: "${editPath!''}",
            deletePath: "${deletePath!''}",
            bubblingLabel: "query-view-in-arm"
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
    <div id="preferencesBlock" class="yui-panel">
        <div id="${id}-preference-head" class="hd">${msg("label.preference-block")}</div>
        <div id="${id}-preference-body" class="bd">
            <div id="${id}-preference-content">
                <div id="preferencesBlock-content" >
                    <div id="${id}-preferenceContainer" class="prreference">
                    <#-- Контейнер для отрисовки формы -->
                        <div id="preferencesBlock-forms" class="forms-container form-fields"></div>
                    </div>
                </div>
                <div class="bdft">
                    <div class="yui-u align-right right">
                            <span id="preferencesBlock-rollback-button" class="yui-button yui-push-button search-icon">
                                <span class="first-child">
                                    <button type="button">${msg('label.button.reset')}</button>
                                </span>
                            </span>
                    </div>
                    <div class="yui-u align-right">
                                <span id="preferencesBlock-save-button" class="yui-button yui-push-button search-icon">
                                    <span class="first-child">
                                        <button type="button">${msg('label.button.save')}</button>
                                    </span>
                                </span>
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
