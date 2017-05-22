<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#assign id = args.htmlid>
<#assign showSearchBlock = true/>

<@grid.datagrid id=id showViewForm=false showArchiveCheckBox=false>
<script type="text/javascript">//<![CDATA[
(function () {
    LogicECM.module.Base.Util.loadResources([
        'modules/simple-dialog.js',
        'scripts/lecm-base/components/advsearch.js',
        'scripts/lecm-base/components/lecm-datagrid.js',
        'scripts/lecm-notifications/notifications-send-settings-datagrid.js'
    ], [
        'components/search/search.css'
    ], createDatagrid);

    function createDatagrid() {
        var datagrid = new LogicECM.module.Templates.DataGrid('${id}').setOptions({
            usePagination: true,
            showExtendSearchBlock:${showSearchBlock?string},
            actions: [
                {
                    type: "datagrid-action-link-templates-settings",
                    id: "onActionEdit",
                    permission: "edit",
                    label: "${msg("actions.edit")}"
                }
            ],
            bubblingLabel: "templates-settings",
            showCheckboxColumn: false,
            showActionColumn: true,
            editForm: "editSendMode",
            searchForm: "search-in-settings",
            datagridMeta: {
                itemType: "lecm-notification-template:template",
                datagridFormId: "send-settings-datagrid",
                useChildQuery: false,
                useFilterByOrg: false,
                nodeRef: LogicECM.module.Templates.TEMPLATES_FOLDER,
                searchConfig: {}
            }
        }).setMessages(${messages});
        datagrid.draw();
    }
})();
//]]></script>
</@grid.datagrid>

