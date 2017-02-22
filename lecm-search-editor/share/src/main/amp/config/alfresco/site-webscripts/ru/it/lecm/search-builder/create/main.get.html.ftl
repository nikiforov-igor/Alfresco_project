<#assign id = args.htmlid>

<script type="text/javascript">
    //<![CDATA[
    (function () {
        var Dom = YAHOO.util.Dom;
        var container;

        (function() {
            function init() {
                LogicECM.module.Base.Util.loadScripts([
                            'scripts/lecm-search-editor/search-queries-editor.js'
                        ],
                        createObjects,
                        ['container',  'datasource']);
                LogicECM.module.Base.Util.loadCSS([
                            'css/lecm-search-editor/search-queries.css'
                        ],
                        null,
                        ['container',  'datasource']);
            }

            function createObjects() {
                drawForm("${id}");
                var editor = new LogicECM.module.SearchQueries.QueryEditor("${id}").setMessages(${messages});
                editor.setOptions({
                    restoreFromCookie:true,
                    resetCookieOnChange:true,
                    bubblingLabel: "query-search-results"
                });
                editor.setRoot("${storeRoot!''}");
            }

            function drawForm(htmlId) {
                Alfresco.util.Ajax.jsonGet({
                        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/control",
                        dataObj: {
                            template: "/ru/it/lecm/base-share/components/controls/select-one-with-webscript-values.ftl",
                            fieldId: "searchQuery-selectType",
                            labelId: "searchQuery-selectType",
                            params: YAHOO.lang.JSON.stringify({
                                webscriptType: "server",
                                webscript: "lecm/search-queries/types",
                                needSort:false,
                                withEmpty: true,
                                changeItemFireAction: "searchQueryChangeDocType"
                            }),
                            htmlid: htmlId
                        },
                        successCallback: {
                            fn: function (response) {
                                container = Dom.get('${id}_container_select_type');
                                if (container) {
                                    container.innerHTML = response.serverResponse.responseText;
                                }
                            }
                        },
                        failureMessage: "${msg('message.failure')}",
                        execScripts: true
                    });
            }

            YAHOO.util.Event.onDOMReady(init);
        })();

    })();
    //]]>
</script>

<div class="metadata-form" id="${id}_metadata">
    <div id="${id}_container_select_type" class="select-doc-type-ctrl"></div>
    <hr/>
    <div id="${id}_container_query_rows">
        <!--[if IE]>
        <iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe>
        <![endif]-->
        <input id="yui-history-field" type="hidden" />
        <div id="${id}-body" class="datagrid">
            <div class="datagrid-meta">
                <h2 id="${id}-title"></h2>
                <div id="${id}-description" class="datagrid-description"></div>
            </div>
            <div id="${id}-datagridBar" class="yui-ge datagrid-bar flat-button hidden1">
                <div class="yui-u first align-center">
                    <div class="item-select">&nbsp;</div>
                </div>
                <div class="yui-u align-right">
                    <div class="items-per-page visible-hidden">
                        <button id="${id}-itemsPerPage-button">${msg("menu.items-per-page")}</button>
                    </div>
                </div>
            </div>
            <div id="${id}-grid" class="grid"></div>
        </div>
    </div>
</div>
