<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>
<#assign bubblingLabel = "dictionaries-datagrid">

<@grid.datagrid id=id showViewForm=false showArchiveCheckBox=true>
<script type="text/javascript">//<![CDATA[
(function(){
	function createDatagrid(rootNode) {
	    new LogicECM.module.Dictionary.DataGrid('${id}', rootNode).setOptions(
	            {
	                bubblingLabel: "${bubblingLabel}",
	                usePagination: true,
	                showExtendSearchBlock: true,
                    excludeColumns:["deletable"],
	                actions: [
	                    {
	                        type: "datagrid-action-link-${bubblingLabel}",
	                        id: "onActionEdit",
	                        permission: "edit",
	                        label: "${msg("actions.edit")}"
	                    },
                        {
                            type: "datagrid-action-link-${bubblingLabel}",
                            id: "onActionMove",
                            permission: "edit",
                            label: "${msg("actions.move")}",
                            evaluator: function () {
                                return rootNode.plane == "false";
                            }
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
	                            return this.isActiveItem(rowData.itemData) && this.isDeletable(rowData.itemData);
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
			var meta = {
                useFilterByOrg: false,
                itemType: rootNode.itemType,
                nodeRef: rootNode.nodeRef,
                searchConfig: ('lecm-contractor:contractor-type' == rootNode.itemType) ? {
                    filter: 'NOT ASPECT:"lecm-orgstr-aspects:is-organization-aspect"'
                } : null
			};

			if (rootNode.plane == "true") {
                if (meta.searchConfig) {
                    meta.searchConfig.filter += " and PATH: \"" + rootNode.path + "//*\""
				} else {
					meta.searchConfig = {
						filter: "PATH: \"" + rootNode.path + "//*\""
					}
				}
			}

	        YAHOO.Bubbling.fire("activeGridChanged",
	                {
	                    datagridMeta: meta,
	                    bubblingLabel:"${bubblingLabel}"
	                });
	    }
	}

	function loadDictionary() {
        Alfresco.util.Ajax.jsonGet({
            url: Alfresco.constants.PROXY_URI + "/lecm/dictionary/api/getDictionary",
            dataObj: {
                dicName: "${args.dictionaryName}"
            },
            successCallback: {
                scope: this,
                fn: function (response) {
                    if (response.json) {
                        createDatagrid(response.json);
                    }
                }
            },
            failureMessage: "${msg('message.dictionary.loading.fail')}"
        });
	}

    function init() {
        LogicECM.module.Base.Util.loadResources([
            'modules/simple-dialog.js',
            'scripts/lecm-base/components/advsearch.js',
            'scripts/lecm-base/components/lecm-datagrid.js',
            'scripts/lecm-dictionary/dictionary-datagrid.js',
            'components/form/date-range.js',
            'components/form/number-range.js',
            'scripts/lecm-base/components/versions.js'
        ], [
            'modules/document-details/historic-properties-viewer.css'
        ], loadDictionary);
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>
</@grid.datagrid>
