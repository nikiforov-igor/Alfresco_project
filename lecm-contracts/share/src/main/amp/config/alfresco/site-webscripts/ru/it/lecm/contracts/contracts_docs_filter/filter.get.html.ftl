<@markup id="css">
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-contracts/add-docs-filter.css" />
</@>
<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-documents/utils/search-queries.js"/>
</@>
<@markup id="js">
	<#assign id = args.htmlid>
	<#if page.url.args.query?? && page.url.args.query != "">
	    <#assign query = page.url.args.query/>
	</#if>
	<#assign statusesFilterKey = 'documents-list-statuses-filter'/>
	<script type="text/javascript">//<![CDATA[
		//TODO: Переделать
	    var currentFormId = "${query!""}";

	    function onFilterClick(key, value) {
	        LogicECM.module.Documents.filtersManager.save(key, 'query=' + value, false);
	        currentFormId = value;

	        var filterStr = _generatePropertyFilterStr(value, "${args.filterProperty!'lecm-additional-document:additionalDocumentType-text-content'}");
	        var archiveFolders = _generatePathsFilterStr(LogicECM.module.Documents.SETTINGS.archivePath);

	        var statusesFilter = "";
	        <#if args.includedStatuses?? && (args.includedStatuses?length > 0)>
	            statusesFilter = _generatePropertyFilterStr ("${args.includedStatuses}", "lecm-statemachine:status");
	        </#if>

	        YAHOO.Bubbling.fire ("reCreateDatagrid", {
	            datagridMeta: {
	                searchConfig: {
	                    filter: (filterStr.length > 0 ?  filterStr + " AND " : "")
	                            + '(PATH:"' + LogicECM.module.Documents.SETTINGS.draftPath + '//*"'
	                            + ' OR PATH:"' + LogicECM.module.Documents.SETTINGS.documentPath + '//*"'
	                            + ((archiveFolders.length > 0)? (" OR " + archiveFolders + "") : "") + ')'
	                            + (statusesFilter.length > 0 ? ' AND ' + statusesFilter : '')
	                }
	            },
	            bubblingLabel: "${args.gridBubblingLabel!''}"
	        });
	    }
	</script>

	<script type="text/javascript">//<![CDATA[
	    (function () {
		    function updateFilterCount(query, filterId, containerId) {
			    Alfresco.util.Ajax.jsonGet({
					url: Alfresco.constants.PROXY_URI_RELATIVE  + "lecm/contracts/additionalDocsCount",
					dataObj:{
						type: query,
						considerFilter: filterId,
						active: "${args.active?string}"
					},
					successCallback:{
						fn:function(response){
							var result = response.json;
							if (result){
								YAHOO.util.Dom.get(containerId).innerHTML = result.length;
							}
						}
					},
					failureMessage: "${msg("message.failure")}",
					execScripts:true
				});
		    }

	        function updateFilterSelect(containerId, query) {
	            var element = YAHOO.util.Dom.get(containerId);
	            if (element) {
	                if (currentFormId == query){
	                    YAHOO.util.Dom.addClass(element, "selected");
	                } else {
	                    YAHOO.util.Dom.removeClass(element, "selected");
	                }
	            }
	        }

	        function updateCounts(layer, args) {
	            var gridBublingLabel = "${args.gridBubblingLabel!''}";
	            if (gridBublingLabel.length == 0 || gridBublingLabel == args[1].bubblingLabel) {
	                <#list filters as filter>
	                    updateFilterCount("${filter.value}", "${queryFilterId}", "filter-${filter_index}-count");
	                </#list>
	            }

	            <#list filters as filter>
	                updateFilterSelect("filter-${filter_index}-block", "${filter.value}");
	            </#list>
	        }

	        function init() {
	            setTimeout(function () {
	                LogicECM.module.Base.Util.setHeight();
	            }, 10);

		        YAHOO.Bubbling.on("onSearchSuccess", updateCounts);
	        }

	        YAHOO.util.Event.onDOMReady(init);
	    })();
	    //]]>
	    </script>

	<div id="contracts-filters" class="contracts-filter-panel">
	    <div class="contracts-filters-block">
	        <div id="filter-groups-set" class="filterBlock">
	        <#if filters??>
	            <#list filters as filter>
	                <div id="filter-${filter_index}-block" class="text-cropped">
	                    <a href="#" class="status-button"
	                       title="<#if filter.value == "*">${msg("filter.type.ALL")}<#else>${filter.value}</#if>"
	                       onclick="onFilterClick('${statusesFilterKey}', '${filter.value}'); return false;">
	                    ${msg("filter.type." + filter.type)}
	                    </a>
	                    <span id="filter-${filter_index}-count" class="total-tasks-count-right">${filter.count}</span><br/>
	                </div>
	            </#list>
	        </#if>
	        </div>
	    </div>
	</div>
</@>