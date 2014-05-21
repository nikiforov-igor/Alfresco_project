<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign id = args.htmlid>
<#assign aDateTime = .now>
<#assign toolbarId = "re-templates-toolbar-" + id/>
<#assign templatesLabel ="templates-" + aDateTime?iso_utc>

<div id="${toolbarId}">
    <@comp.baseToolbar toolbarId true false false>
    <div class="new-row">
        <span id="${toolbarId}-newElementButton" class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button" title="${msg('label.new-template.btn')}">${msg('label.new-template.btn')}</button>
               </span>
        </span>
    </div>
    </@comp.baseToolbar>
</div>

<script type="text/javascript">//<![CDATA[
(function () {
    function initToolbar() {
        new LogicECM.module.ReportsEditor.Toolbar("${toolbarId}").setMessages(${messages}).setOptions({
            bubblingLabel: "${templatesLabel}",
            createFormId: "${args.createFormId!''}",
            newRowDialogTitle: "label.create-template.title"
        });
    }

    YAHOO.util.Event.onContentReady("${toolbarId}", initToolbar);
})();
//]]></script>

<#assign gridId = "re-templates-grid-" + id/>

<div class="yui-t1" id="${gridId}">
    <div class="yui-b" id="alf-content-${gridId}" style="margin-left: 0;">
        <@grid.datagrid id="${gridId}" showViewForm=false>
            <script type="text/javascript">//<![CDATA[

            function createDatagrid() {
                var datagrid = new LogicECM.module.Base.DataGrid('${gridId}').setOptions(
                        {
                            usePagination: true,
                            showExtendSearchBlock: false,
                            forceSubscribing: true,
                            actions: [
                                {
                                    type:"datagrid-action-link-${templatesLabel}",
                                    id:"onActionEdit",
                                    permission:"edit",
                                    label:"${msg("actions.edit")}"
                                },
                                {
                                    type:"datagrid-action-link-${templatesLabel}",
                                    id:"onActionDelete",
                                    permission:"delete",
                                    label:"${msg("actions.delete-row")}",
                                    evaluator: function (rowData) {
                                        var itemData = rowData.itemData;
                                        return this.isActiveItem(itemData);
                                    }
                                }
                            ],
                            bubblingLabel: "${templatesLabel}",
                            showCheckboxColumn: false
                        }).setMessages(${messages});


                YAHOO.Bubbling.fire("activeGridChanged", {
                    datagridMeta: {
                        itemType: "lecm-rpeditor:reportTemplate",
                        useChildQuery: true,
                        nodeRef: LogicECM.module.ReportsEditor.SETTINGS.templatesContainer,
                        actionsConfig: {
                            fullDelete: true,
                            trash: false
                        },
                        sort: "cm:name|true"
                    },
                    bubblingLabel: "${templatesLabel}"
                });
            }

            YAHOO.util.Event.onContentReady('${gridId}', createDatagrid);
            //]]></script>
        </@grid.datagrid>
    </div>
</div>
