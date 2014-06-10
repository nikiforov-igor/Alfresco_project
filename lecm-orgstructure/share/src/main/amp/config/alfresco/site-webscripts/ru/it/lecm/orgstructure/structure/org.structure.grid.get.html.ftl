<@script type="text/javascript" src="${url.context}/res/components/form/date-range.js"></@script>
<@script type="text/javascript" src="${url.context}/res/components/form/number-range.js"></@script>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/search/search.css" />

<!-- Historic Properties Viewer -->
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/versions.js"></@script>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/modules/document-details/historic-properties-viewer.css" />

<!-- Tree -->
<@link rel="stylesheet" type="text/css" href="${url.context}/res/yui/treeview/assets/skins/sam/treeview.css"/>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-orgstructure/orgstructure-tree.css" />
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-orgstructure/orgstructure-tree.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-orgstructure/orgstructure-utils.js"></@script>


<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/orgstructure/components/orgstructure-tree.ftl" as orgTree/>
<#assign id = args.htmlid>
<#assign showSearchBlock = true/>
<#assign realDelete = false/>
<#if fullDelete??>
	<#assign realDelete = fullDelete/>
</#if>


<!-- include base datagrid markup-->
<@grid.datagrid id=id showViewForm=true showArchiveCheckBox=true>
        <script type="text/javascript">//<![CDATA[
        (function () {
                function createDatagrid() {
                        // Переопределяем метод onActionDelete. Добавляем проверки
                        LogicECM.module.Base.DataGrid.prototype.onActionDelete =
            function DataGridActions_onActionDelete(p_items, owner, actionsConfig, fnDeleteComplete) {
                                var me = this,
                                                items = YAHOO.lang.isArray(p_items) ? p_items : [p_items];
                                var deletedUnit = items[0]; // для Оргструктуры одновременно удалить можно ТОЛЬКО ОДНО подразделение
        // Проверим не является ли подразделение корневым (тогда его нельщзя удалять!)
        var sUrl = Alfresco.constants.PROXY_URI + "lecm/orgstructure/api/getUnitParent?nodeRef=" + deletedUnit.nodeRef;
                                var callback = {
                                        success:function (oResponse) {
                                                var oResults = eval("(" + oResponse.responseText + ")");
                                                if (oResults && !oResults.nodeRef && this.totalRecords == 1) {
                                                        Alfresco.util.PopupManager.displayMessage(
                                                                        {
                                                                                text:me.msg("message.delete.unit.failure.root.unit")
                                                                        });
                                                } else {
                    // Проверим есть ли у подразделения штатные расписания
                    var sUrl = Alfresco.constants.PROXY_URI + "lecm/orgstructure/api/getUnitStaffPositions?nodeRef=" + deletedUnit.nodeRef;
                                                        var callback = {
                                                                success:function (oResponse) {
                                                                        var oResults = eval("(" + oResponse.responseText + ")");
                                                                        if (oResults && oResults.length > 0) {
                                                                                Alfresco.util.PopupManager.displayMessage(
                                                                                                {
                                                                                                        text:me.msg("message.delete.unit.failure.has.composition")
                                                                                                });
                                                                        } else {
                                // проверим нет ли дочерних АКТИВНЫХ подразделений
                                var sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getUnitChildren?nodeRef=" + deletedUnit.nodeRef + "&onlyActive=true";
                                var callback = {
                                    success:function (oResponse) {
                                        var oResults = eval("(" + oResponse.responseText + ")");
                                        if (oResults && oResults.length > 0) { // нельзя удалять - есть дочерние подразделения
                                            Alfresco.util.PopupManager.displayMessage(
                                                    {
                                                        text:me.msg("message.delete.unit.failure.has.children")
                                                    });
                                        } else { // удаляем! вызов метода из грида
                                            me.onDelete(p_items, owner, actionsConfig, fnDeleteComplete, null);
                                        }
                                    },
                                    failure:function (oResponse) {
                                        Alfresco.util.PopupManager.displayMessage(
                                                {
                                                    text:me.msg("message.delete.unit.error")
                                                });
                                    },
                                    argument:{
                                    }
                                };
                                YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
                                                                        }
                                                                },
                                                                failure:function (oResponse) {
                                                                        Alfresco.util.PopupManager.displayMessage(
                                                                                        {
                                                                                                text:me.msg("message.delete.unit.error")
                                                                                        });
                                                                },
                                                                argument:{
                                                                }
                                                        };
                                                        YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
                                                }
                                        },
                                        failure:function (oResponse) {
                                                Alfresco.util.PopupManager.displayMessage(
                                                                {
                                                                        text:me.msg("message.delete.unit.error")
                                                                });
                                        }
                                };
                                YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
                        };

                        new LogicECM.module.Base.DataGrid('${id}').setOptions(
                                        {
                                                usePagination:true,
                                                showExtendSearchBlock:${showSearchBlock?string},
                                                actions:[
                                                        {
                                                                type:"datagrid-action-link-${bubblingLabel!"orgstructure"}",
                                                                id:"onActionEdit",
                                                                permission:"edit",
                                                                label:"${msg("actions.edit")}"
                                                        },
                                                        {
                                                                type:"datagrid-action-link-${bubblingLabel!"orgstructure"}",
                                                                id:"onActionVersion",
                                                                permission:"edit",
                                                                label:"${msg("actions.version")}"
                                                        },
                                                        {
                                                                type:"datagrid-action-link-${bubblingLabel!"orgstructure"}",
                                                                id:"onActionDelete",
                                                                permission:"delete",
                                                                label:"${msg("actions.delete-row")}",
                        evaluator: function (rowData) {
                            if (rowData) {
                                return this.isActiveItem(rowData.itemData);
                            }
                            return false;
                        }
                                                        },
                                                        {
                                                                type:"datagrid-action-link-${bubblingLabel!"employee"}",
                                                                id:"onActionRestore",
                                                                permission:"delete",
                                                                label:"${msg("actions.restore-row")}",
                                                                evaluator: function (rowData) {
                                                                        return !this.isActiveItem(rowData.itemData);
                                                                }
                                                        }
                                                ],
                                                bubblingLabel: "${bubblingLabel!"orgstructure"}",
                                                showCheckboxColumn: false,
                showActionColumn: LogicECM.module.OrgStructure.IS_ENGINEER ? true : false,
                                                attributeForShow:"lecm-orgstr:element-short-name"
                                        }).setMessages(${messages});
                }

                function init() {
                        createDatagrid();
                }

                YAHOO.util.Event.onDOMReady(init);
        })();
        //]]></script>
</@grid.datagrid>

