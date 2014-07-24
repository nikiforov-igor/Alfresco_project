<@markup id="css">
	<!-- Advanced Search -->
	<#--<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/search/search.css" />-->
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/modules/document-details/historic-properties-viewer.css" />
</@>
<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/advsearch.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-datagrid.js"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-dictionary/dictionary-datagrid.js"/>
	<@script type="text/javascript" src="${url.context}/res/components/form/date-range.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/components/form/number-range.js"></@script>
	<!-- Historic Properties Viewer -->
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/versions.js"></@script>
</@>

<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>
<#assign bubblingLabel = "dictionaries-datagrid">

<#assign plane = false/>
<#if args.plane?? && args.plane == "true">
    <#assign plane = true/>
</#if>

<@grid.datagrid id=id showViewForm=true showArchiveCheckBox=true>
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
    var sUrl = Alfresco.constants.PROXY_URI + "/lecm/dictionary/api/getDictionary?dicName=" + encodeURIComponent("${args.dictionaryName}");
	
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

	YAHOO.util.Event.onDOMReady(loadDictionary);
})();
//]]></script>
</@grid.datagrid>
