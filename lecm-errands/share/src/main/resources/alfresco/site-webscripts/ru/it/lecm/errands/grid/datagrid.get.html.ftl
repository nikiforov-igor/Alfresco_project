<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="contracts-grid">
	<div id="yui-main-2">
		<div class="yui-b" id="alf-content" style="margin-left: 0;">
		<@grid.datagrid id=id showViewForm=true>
			<script type="text/javascript">//<![CDATA[
			function createDatagrid() {

                new LogicECM.module.Errands.DataGrid('${id}').setOptions({
                    usePagination: true,
                    useDynamicPagination:true,
                    pageSize: 20,
                    showExtendSearchBlock: true,
                    actions: [
                        {
                            type: "datagrid-action-link-${bubblingLabel!'errands'}",
                            id: "onActionEdit",
                            permission: "edit",
                            label: "${msg("actions.edit")}"
                        },
                        {
                            type: "datagrid-action-link-${bubblingLabel!'errands'}",
                            id: "onActionDelete",
                            permission: "delete",
                            label: "${msg("actions.delete-row")}"
                        },
	                    {
		                    type:"datagrid-action-link-${bubblingLabel!'errands'}",
		                    id:"onActionDuplicate",
		                    permission:"create",
		                    label:"${msg("actions.duplicate-row")}"
	                    }
                    ],
                    allowCreate: false,
                    showActionColumn: true,
                    showCheckboxColumn: false,
                    bubblingLabel: "${bubblingLabel!"errands"}",
                    attributeForShow:"${attributeForShow!"cm:name"}",
                    excludeColumns: ["lecm-errands:is-important", "lecm-errands:baseDocString", "lecm-errands:is-expired"],
                    nowrapColumns: ["lecm-errands:number"]
                }).setMessages(${messages});

                var query = <#if query?? && (query?length > 0)>"${query}"<#else>""</#if>;

                var filter = _generateFilterStr(query.length > 0 ? query : LogicECM.module.Documents.FILTER , "${filterProperty}");
                var archiveFolders = _generateArchiveFoldersStr(LogicECM.module.Documents.SETTINGS.archivePath);

                var formId = <#if formId?? && (formId?length > 0)>
                                (("_" + "${formId}").split(" ").join("_"))
                            <#else>
                                ""
                            </#if>;
                YAHOO.util.Event.onContentReady ('${id}', function () {
                    YAHOO.Bubbling.fire ("activeGridChanged", {
                        datagridMeta: {
                            itemType: "lecm-errands:document",
                            datagridFormId: "datagrid" + formId,
                            nodeRef: LogicECM.module.Documents.SETTINGS.nodeRef,
                            actionsConfig:{
                                fullDelete:true,
                                trash: false
                            },
                            sort:"cm:modified|false",
                            searchConfig: {
                                filter: (filter.length > 0 ?  filter + " AND " : "")
                                        + '(PATH:"' + LogicECM.module.Documents.SETTINGS.draftPath + '//*"'
                                        + ' OR PATH:"' + LogicECM.module.Documents.SETTINGS.documentPath + '//*"'
                                        + ((archiveFolders.length > 0)? (" OR " + archiveFolders + "") : "") + ')'

                            }
                        },
                        bubblingLabel: "${bubblingLabel!"errands"}"
                    });
                });
			}

			function init() {
				createDatagrid();
			}

            function _generateFilterStr(filter, property) {
                if ((filter && filter.length > 0) && (property && property.length > 0)) {
                    var re = /\s*,\s*/;
                    var values = filter.split(re);
                    var shieldProp = property.split("-").join("\\-");
                    var resultFilter = "";
                    var notFilter = "";
                    for (var i = 0; i < values.length; i++) {
                        var value = values[i];
                        if (value.indexOf("!") != 0) {
                            resultFilter += "@" + shieldProp + ":\'" + value + "\' ";
                        } else {
                            value = value.replace("!","");
                            notFilter += "@" + shieldProp + ":\'" + value + "\' ";
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
