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
                <@grid.datagrid id=id showViewForm=false showArchiveCheckBox=false>
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
                                                useFilterByOrg: false,
                                                itemType: rootNode.itemType,
                                                nodeRef: rootNode.nodeRef
                                            },
                                            bubblingLabel:"${bubblingLabel}"
                                        });
                            }
                    }

                    function loadDictionary() {
                        Alfresco.util.Ajax.jsonGet({
                            url: Alfresco.constants.PROXY_URI + "/lecm/dictionary/api/getDictionary",
                            dataObj: {
                                dicName: "Категория события"
                            },
                            successCallback: {
                                scope: this,
                                fn: function (response) {
                                    if (response.json) {
                                        createDatagrid(response.json);
                                    }
                                }
                            },
                            failureMessage: "${msg('message.dictionary.wasnt.load')}"
                        });
                    }

                    function init() {
                        LogicECM.module.Base.Util.loadResources([
                            'scripts/lecm-base/components/advsearch.js',
                            'scripts/lecm-base/components/lecm-datagrid.js',
                            'scripts/lecm-base/components/lecm-toolbar.js',
                            'scripts/lecm-business-journal/business-journal-datagrid.js',
                            'scripts/lecm-business-journal/business-journal-settingsgrid.js',
                            'scripts/lecm-business-journal/business-journal-loggertoolbar.js'
                        ], [
                            'components/data-lists/toolbar.css'
                        ], loadDictionary);
                    }

                    YAHOO.util.Event.onDOMReady(init);
                    //]]></script>
                </@grid.datagrid>
                </div>
            </div>
        </div>
    </div>
</div>
