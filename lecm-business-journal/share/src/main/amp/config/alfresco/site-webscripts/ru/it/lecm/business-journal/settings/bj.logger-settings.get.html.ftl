<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/advsearch.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-datagrid.js"/>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-toolbar.js"/>

<@script type="text/javascript" src="${url.context}/res/scripts/lecm-business-journal/business-journal-datagrid.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-business-journal/business-journal-settingsgrid.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-business-journal/business-journal-loggertoolbar.js"></@script>

<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/data-lists/toolbar.css" />
<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#assign  id = args.htmlid/>
<#if !bubblingLabel??>
    <#assign bubblingLabel = "bj-settings">
</#if>

<div id="${id}">
    <div id="${id}-content">
        <div class="yui-t1" id="bj-dictionary-grid">
            <div id="yui-main-2">
                <div class="yui-b datagrid-content" id="alf-content">
                    <!-- include base datagrid markup-->
                <@grid.datagrid id=id showViewForm=true showArchiveCheckBox=false>
                    <script type="text/javascript">//<![CDATA[
                    function createDatagrid(rootNode) {
                        var datagrid = new LogicECM.module.BusinessJournal.SettingsGrid('${id}').setOptions(
                                {
                                    usePagination:true,
                                    showExtendSearchBlock:false,
                                    bubblingLabel: "${bubblingLabel}",
                                    showCheckboxColumn: true,
                                    showActionColumn: false,
                                }).setMessages(${messages});
                            if (rootNode) {
                                YAHOO.Bubbling.fire("activeGridChanged",
                                        {
                                            datagridMeta: {
                                                itemType: rootNode.itemType,
                                                nodeRef: rootNode.nodeRef
                                            },
                                            bubblingLabel:"${bubblingLabel}"
                                        });
                            }
                    }

                    function loadDictionary() {
                        var sUrl = Alfresco.constants.PROXY_URI + "/lecm/dictionary/api/getDictionary?dicName=" + encodeURIComponent("Категория события");

                        var callback = {
                            success: function (oResponse) {
                                var oResults = eval("(" + oResponse.responseText + ")");
                                if (oResults != null) {
                                        createDatagrid(oResults);
                                }
                            },
                            failure: function (oResponse) {
                                alert("Справочник не был загружен. Попробуйте обновить страницу.");
                            }
                        };
                        YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
                    }


                    function init() {
                        loadDictionary();
                    }

                    YAHOO.util.Event.onDOMReady(init);
                    //]]></script>
                </@grid.datagrid>
                </div>
            </div>
        </div>
    </div>
</div>