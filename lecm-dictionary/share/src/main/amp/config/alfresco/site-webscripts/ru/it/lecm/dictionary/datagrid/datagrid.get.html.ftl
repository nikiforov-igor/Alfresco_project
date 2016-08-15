<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>
<#assign bubblingLabel = "dictionaries-datagrid">

<@grid.datagrid id=id showViewForm=false showArchiveCheckBox=true>
<script type="text/javascript">//<![CDATA[
(function(){
	function createDatagrid(rootNode) {
	    new LogicECM.module.Dictionary.DataGrid('${id}', rootNode.attributeForShow).setOptions(
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
                    filter: '-ASPECT:"lecm-orgstr-aspects:is-organization-aspect" and ISNOTNULL:"sys:node-dbid"'
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
    var sUrl = Alfresco.constants.PROXY_URI + "/lecm/dictionary/api/getDictionary?dicName=" + encodeURIComponent("${args.dictionaryName}");

	    var callback = {
	        success: function (oResponse) {
	            var oResults = eval("(" + oResponse.responseText + ")");
	            if (oResults != null) {
	                    createDatagrid(oResults);
	            }
	        },
	        failure: function (oResponse) {
	            alert("${msg('message.dictionary.loading.fail')}");
	        }
	    };
	    YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
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
