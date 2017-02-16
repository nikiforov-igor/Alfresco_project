<@markup id="css">
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-documents/documents-list-filter.css" />
</@>
<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-documents/utils/search-queries.js"></@script>
</@>

<@markup id="html">
	<#assign id = args.htmlid>
	<#assign statusesFilterKey = 'documents-list-statuses-filter'/>

	<#assign f_label = args.filterLabel!'label.documents'>
	<#if args.filterLabel??>
	    <#assign f_label = args.filterLabel/>
	<#else>
	    <#if args.itemType??>
	        <#assign f_label = ("label.filter-" + args.itemType?replace(":","_"))/>
	    </#if>
	</#if>

	<#assign filterTitle = msg("label.documents")/>
	<#if msg(f_label) != f_label>
	    <#assign filterTitle = msg(f_label)/>
	</#if>
	<#assign pageLink = args.linkPage!"documents-list"/>

	<#assign isDocListPage = false/>
	<#if page.url.args.doctype?? && page.url.args.doctype != "">
	    <#assign isDocListPage = true/>
	</#if>
	<#if page.url.args.formId?? && page.url.args.formId != "">
	    <#assign formId = page.url.args.formId/>
	</#if>

	<div id="documents-filter" class="documents-filter-panel">
	    <div class="documents-filter-block">
	        <div id="filter-groups-set" class="filterBlock">
	        </div>
	        <hr/>
	    </div>
	    <div class="documents-filter-block">
	        <h2 id="${id}-heading" class="thin">${msg("label.byStatus")}</h2>
	        <div>
	            <div id="filter-statuses-set" class="filterBlock">
	            </div>
	        </div>
	    </div>

	    <script type="text/javascript">//<![CDATA[
	    (function () {
	        var currentFormId = "${formId!""}";

	        function createRow(group) {
	            var div = document.createElement('div');
	            div.setAttribute('class', 'text-cropped');

	            if (currentFormId == group.key || currentFormId == group.id) {
	                Dom.addClass(div, "selected");
	            }
	            return div;
	        }

	        function getFilters(){
	            Alfresco.util.Ajax.jsonGet({
                    url:Alfresco.constants.PROXY_URI_RELATIVE  + "lecm/documents/summary",
                    dataObj:{
                        docType:"${args.itemType}",
                        archive: ("${args.activeDocs!"true"}" == "false"),
                        considerFilter: location.hash.replace(/#(\w+)=/, "")
                    },
                    successCallback:{
                        fn:function(response){
                            var filtersGroups = response.json;
                            if (filtersGroups){
                                var filters = filtersGroups.list;
                                var container = Dom.get('filter-groups-set');
                                container.innerHTML = '';
                                if (filters.length > 0) {
                                    for (var i = 0; i < filters.length; i++) {
                                        var filter = filters[i];

                                        var div = createRow(filter);

                                        var ref = document.createElement('a');
                                        ref.hfef="#";
                                        ref.title = filter.filter == "*" ? ${msg("msg.all")} : filter.filter;
                                        ref.innerHTML = filter.key;
                                        ref.setAttribute('class', 'status-button');
                                        YAHOO.util.Event.on(ref, 'click', function(ev) {
                                            currentFormId = this.key;

                                            LogicECM.module.Documents.filtersManager.save('${statusesFilterKey}', 'query=' + String(this.filter) + '&formId=' + this.key, false);

                                            var filterStr = _generatePropertyFilterStr(String(this.filter), "${args.filterProperty!'lecm-statemachine:status'}");
                                            var archiveFolders = _generatePathsFilterStr(LogicECM.module.Documents.SETTINGS.archivePath);

                                            var formId = (("datagrid_" + this.key).split(" ").join("_"));

                                            YAHOO.Bubbling.fire ("reCreateDatagrid", {
                                                datagridMeta: {
                                                    datagridFormId: formId,
                                                    searchConfig: {
                                                        filter: (filterStr.length > 0 ?  filterStr + " AND " : "")
                                                                + '(PATH:"' + LogicECM.module.Documents.SETTINGS.draftPath + '//*"'
                                                                + ' OR PATH:"' + LogicECM.module.Documents.SETTINGS.documentPath + '//*"'
                                                                + ((archiveFolders.length > 0)? (" OR " + archiveFolders + "") : "") + ')'
                                                    }
                                                },
                                                bubblingLabel: "${args.gridBubblingLabel!''}"
                                            });
                                        }.bind(filter));

                                        div.appendChild(ref);
                                        var count = document.createElement('span');
                                        count.innerHTML = filter.amount;
                                        count.setAttribute('class', 'total-tasks-count-right');
                                        div.appendChild(count);

                                        container.appendChild(div);
                                    }
                                    container.appendChild(document.createElement('br'));
                                }
                            }

                        }
                    },
                    failureMessage: "${msg('message.failure')}",
                    execScripts:true
                });
	        }

	        function getStatuses(){
	            Alfresco.util.Ajax.request(
	                    {
	                        url:Alfresco.constants.PROXY_URI_RELATIVE + "lecm/statemachine/getStatuses",
	                        dataObj:{
	                            docType:"${args.itemType}",
	                            active: ("${args.activeDocs!"true"}" == "true"),
	                            final: ("${args.finalDocs!"false"}" == "true")
	                        },
	                        successCallback:{
	                            fn:function(response){
	                                var statuses = response.json;
	                                var container = Dom.get('filter-statuses-set');
	                                container.innerHTML = '';
	                                if (statuses.length > 0) {
	                                    for (var i = 0; i < statuses.length; i++) {
	                                        var status = statuses[i];

	                                        var div = createRow(status);

	                                        var ref = document.createElement('a');
	                                        ref.hfef="#";
	                                        ref.innerHTML = status.id;
	                                        ref.setAttribute('class', 'status-button text-broken');
	                                        YAHOO.util.Event.on(ref, 'click', function(ev) {
	                                            currentFormId = this.id;

	                                            LogicECM.module.Documents.filtersManager.save('${statusesFilterKey}', 'query=' + this.id + '&formId=' + this.id, false);

	                                            var filterStr = _generatePropertyFilterStr(String(this.id), "${args.filterProperty!'lecm-statemachine:status'}");
	                                            var archiveFolders = _generatePathsFilterStr(LogicECM.module.Documents.SETTINGS.archivePath);

	                                            var formId = (("datagrid_" + this.id).split(" ").join("_"));

	                                            var statusesFilter = "";
	                                            <#if args.includedStatuses?? && (args.includedStatuses?length > 0)>
	                                                statusesFilter = _generatePropertyFilterStr ("${args.includedStatuses}", "lecm-statemachine:status");
	                                            </#if>

	                                            YAHOO.Bubbling.fire ("reCreateDatagrid", {
	                                                datagridMeta: {
	                                                    datagridFormId: formId,
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
	                                        }.bind(status));

	                                        div.appendChild(ref);
	                                        container.appendChild(div);
	                                    }
	                                }
	                            }
	                        },
	                        failureMessage:"message.failure",
	                        execScripts:true
	                    });
	        }

		    function updateView(layer, args) {
			    var gridBublingLabel = "${args.gridBubblingLabel!''}";
			    if (gridBublingLabel.length == 0 || gridBublingLabel == args[1].bubblingLabel) {
				    getFilters(false);
				    getStatuses(false);
			    }
		    }

	        function init() {
	            Alfresco.util.createTwister("${id}-heading", "documentsStatuses");
	            setTimeout(function () {
	                LogicECM.module.Base.Util.setHeight();
	            }, 10);

		        YAHOO.Bubbling.on("onSearchSuccess", updateView);
	        }

	        YAHOO.util.Event.onDOMReady(init);
	    })();
	    //]]>
	    </script>
	</div>
</@>