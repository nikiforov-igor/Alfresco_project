<#include "/org/alfresco/include/alfresco-macros.lib.ftl" />

<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-metadata-form-edit.css" />
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-final-actions.css" />
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-metadata-form-edit.css" />

<#assign el=args.htmlid/>

<!-- Markup -->
<div id="${el}">
    <div class="widget-panel-grey widget-bordered-panel">
        <div id="${el}-formContainer">
            <div id="query-actions">
                <div id="final-actions-body" class="document-final-actions document-details-panel">
                    <div class="doclist">
                        <div id="final-actions-actionSet" class="action-set"></div>
                    </div>
                </div>
            </div>
        </div>
        <script type="text/javascript">//<![CDATA[
        (function() {
            function init() {
                LogicECM.module.Base.Util.loadScripts([
                            'scripts/lecm-search-editor/search-queries-editor-actions.js'
                        ],
                        createObjects,
                        ['container',  'datasource']);

            }

            function createObjects() {
                new LogicECM.module.SearchQueries.QueryEditorActions("${el}").setMessages(${messages});
            }

            YAHOO.util.Event.onDOMReady(init);
        })();
        //]]></script>
    </div>
</div>