<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="contracts-grid">
	<div id="yui-main-2">
		<div class="yui-b" id="alf-content" style="margin-left: 0;">
		<@grid.datagrid id=id showViewForm=true>
			<script type="text/javascript">//<![CDATA[
			function createDatagrid() {

                new LogicECM.module.Contracts.DataGrid('${id}').setOptions({
                    usePagination: true,
                    useDynamicPagination:true,
                    pageSize: 20,
                    showExtendSearchBlock: true,
                    actions: [
                        {
                            type: "datagrid-action-link-${bubblingLabel!'contracts'}",
                            id: "onActionEdit",
                            permission: "edit",
                            label: "${msg("actions.edit")}"
                        },
                        {
                            type: "datagrid-action-link-${bubblingLabel!'contracts'}",
                            id: "onActionDelete",
                            permission: "delete",
                            label: "${msg("actions.delete-row")}"
                        },
	                    {
		                    type:"datagrid-action-link-${bubblingLabel!'contracts'}",
		                    id:"onActionDuplicate",
		                    permission:"create",
		                    label:"${msg("actions.duplicate-row")}"
	                    }
                    ],
                    allowCreate: false,
                    showActionColumn: true,
                    showCheckboxColumn: false,
                    bubblingLabel: "${bubblingLabel!"contracts"}",
                    attributeForShow:"lecm-contract:regNumProject",
                    excludeColumns: ["lecm-document:creator-ref", "lecm-contract:currency-assoc"]
                }).setMessages(${messages});

                var filter = _generateFilterStr(LogicECM.module.Contracts.FILTER);
                var archiveFolders = _generateArchiveFoldersStr(LogicECM.module.Contracts.SETTINGS.archivePath);
                var formId = (LogicECM.module.Contracts.FORM_ID == "") ? "" : "_"+LogicECM.module.Contracts.FORM_ID.split(" ").join("_");
                var datagridFormId = "datagrid" + formId;
                YAHOO.util.Event.onContentReady ('${id}', function () {
                    YAHOO.Bubbling.fire ("activeGridChanged", {
                        datagridMeta: {
                            itemType: "lecm-contract:document",
                            datagridFormId: datagridFormId,
                            nodeRef: LogicECM.module.Contracts.SETTINGS.nodeRef,
                            actionsConfig:{
                                fullDelete:true,
                                trash: false
                            },
                            sort:"cm:modified|false",
                            searchConfig: {
                                filter: (filter.length > 0 ?  filter + " AND " : "")
                                        + '(PATH:"' + LogicECM.module.Contracts.SETTINGS.draftPath + '//*"'
                                        + ' OR PATH:"' + LogicECM.module.Contracts.SETTINGS.documentPath + '//*"'
                                        + ((archiveFolders.length > 0)? (" OR " + archiveFolders + "") : "") + ')'

                            }
                        },
                        bubblingLabel: "${bubblingLabel!"contracts"}"
                    });
                });
			}

			function init() {
				createDatagrid();
			}

            function _generateFilterStr(filter) {
                if (filter) {
                    var re = /\s*,\s*/;
                    var statuses = filter.split(re);

                    var resultFilter = "";
                    var notFilter = "";
                    for (var i = 0; i < statuses.length; i++) {
                       /* if (resultFilter.length > 0) {
                            resultFilter += " AND ";
                        }*/
                        var status = statuses[i];
                        if (status.indexOf("!") != 0) {
                            resultFilter += "@lecm\\-statemachine:status:\'" + status + "\' ";
                        } else {
                            status = status.replace("!","");
                            notFilter += "@lecm\\-statemachine:status:\'" + status + "\' ";
                        }

                    }
                    return (resultFilter.length > 0 ? "(" + resultFilter + ")" : "")
                            + (resultFilter.length > 0 && notFilter.length > 0 ? " AND " : "")
                            + (notFilter.length > 0 ? "NOT (" + notFilter + ")" : "");
                }
                return "";
            }

            function _generateArchiveFoldersStr(paths) {
                if (paths) {
                    var archPaths = paths.split(",");
                    var result = "";
                    for (var i = 0; i < archPaths.length; i++) {
                        if (result.length > 0) {
                            result += " OR ";
                        }
                        result += 'PATH:"' + archPaths[i] + '//*"' ;
                    }
                    return result;
                }
                return "";
            }

			YAHOO.util.Event.onDOMReady(init);
			//]]></script>
		</@grid.datagrid>
		</div>
	</div>
</div>
