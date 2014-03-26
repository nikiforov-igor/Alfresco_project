<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#assign  id = args.htmlid/>
<#if !bubblingLabel??>
    <#assign bubblingLabel = "bj-settings">
</#if>

<div id="${id}">
    <div id="${id}-content">
        <div class="yui-t1" id="bj-dictionary-grid">
            <div id="yui-main-2">
                <div class="yui-b" id="alf-content" style="margin-left: 0;">
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