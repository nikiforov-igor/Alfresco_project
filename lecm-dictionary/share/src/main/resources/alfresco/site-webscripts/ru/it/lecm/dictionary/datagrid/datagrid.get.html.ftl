<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>
<#assign bubblingLabel = "dictionaries-datagrid">

<#assign plane = false/>
<#if page.url.args.plane?? && page.url.args.plane == "true">
    <#assign plane = true/>
</#if>

<@grid.datagrid id=id showViewForm=true showArchiveCheckBox=true>
<script type="text/javascript">//<![CDATA[
function createDatagrid(rootNode) {
    new LogicECM.module.Dictionary.DataGrid('${id}', rootNode.attributeForShow).setOptions(
            {
                bubblingLabel: "${bubblingLabel}",
                usePagination: true,
                showExtendSearchBlock: true,
                actions: [
                    {
                        type: "datagrid-action-link-${bubblingLabel}",
                        id: "onActionEdit",
                        permission: "edit",
                        label: "${msg("actions.edit")}"
                    },
                    {
                        type: "datagrid-action-link-${bubblingLabel}",
                        id: "onActionVersion",
                        permission: "edit",
                        label: "${msg("actions.version")}"
                    },
                    {
                        type: "datagrid-action-link-${bubblingLabel}",
                        id: "onActionDelete",
                        permission: "delete",
                        label: "${msg("actions.delete-row")}",
                        evaluator: function (rowData) {
                            return this.isActiveItem(rowData.itemData);
                        }
                    },
                    {
                        type: "datagrid-action-link-${bubblingLabel}",
                        id: "onActionRestore",
                        permission: "edit",
                        label: "${msg("actions.restore-row")}",
                        evaluator: function (rowData) {
                            return !this.isActiveItem(rowData.itemData);
                        }
                    }
                ]
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
    var sUrl = Alfresco.constants.PROXY_URI + "/lecm/dictionary/api/getDictionary?dicName=" + encodeURIComponent("${page.url.args.dic!''}");

    var callback = {
        success: function (oResponse) {
            var oResults = eval("(" + oResponse.responseText + ")");
            if (oResults != null) {
                <#if plane>
                    createDatagrid(oResults);
                </#if>
            }
        },
        failure: function (oResponse) {
            alert("Справочник не был загружен. Попробуйте обновить страницу.");
        }
    };
    YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
}

YAHOO.util.Event.onDOMReady(loadDictionary);
//]]></script>
</@grid.datagrid>
