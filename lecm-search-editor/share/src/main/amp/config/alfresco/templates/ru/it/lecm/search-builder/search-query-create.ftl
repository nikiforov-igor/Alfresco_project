<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<script type="text/javascript">//<![CDATA[
(function() {
    function init() {
        LogicECM.module.Base.Util.loadCSS([
                    'css/lecm-search-editor/search-queries.css'
                ],
                null,
                ['container',  'datasource']);
    }
    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div id="no_menu_page" class="sticky-wrapper">
<@bpage.basePageSimple showToolbar=false>
    <div id="query-bd" class="query-page">
            <div class="yui-gc">
                <div id="query-content" class="query-content">
                    <div class="bordered-panel doc-right-part">
                        <@region id="query-actions" scope="template"/>
                    </div>
                    <div id="main-region">
	                    <@region id="content" scope="template"/>
                        <@region id="search-datagrid" scope="template" />
                    </div>
                </div>
            </div>
    </div>
</@bpage.basePageSimple>
</div>