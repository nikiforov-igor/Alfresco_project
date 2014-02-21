if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.ARM = LogicECM.module.ARM|| {};

(function () {
    LogicECM.module.ARM.DataGrid = function (containerId) {
        LogicECM.module.ARM.DataGrid.superclass.constructor.call(this, containerId)

        this.filtersMeta = null;
        YAHOO.Bubbling.on("activeFiltersChanged", this.onActiveFiltersChanged, this);
        YAHOO.Bubbling.on("armNodeSelected", this.onArmNodeSelected, this);

        this.deferredListPopulation = new Alfresco.util.Deferred(["activeFiltersChanged", "onGridTypeChanged", "armNodeSelected"],
            {
                fn: this.populateDataGrid,
                scope: this
            });

        return this;
    };

    YAHOO.lang.extend(LogicECM.module.ARM.DataGrid, LogicECM.module.Base.DataGrid);

    YAHOO.lang.augmentObject(LogicECM.module.ARM.DataGrid.prototype, {
        doubleClickLock: false,

        filtersMeta: null,

        onActionEdit: function (item){
            window.location.href = window.location.protocol + "//" + window.location.host +
                Alfresco.constants.URL_PAGECONTEXT + "document?nodeRef=" + item.nodeRef;
        },

        onArmNodeSelected: function(layer, args) {
            if (args[1].armNode) {
                var datagridMeta = this.datagridMeta;
                var node = args[1].armNode;

                var searchQuery = this.getSearchQuery(node);
                if (searchQuery) {
                    datagridMeta.searchConfig = {
                        filter: searchQuery
                    }
                }
                if (node.data.columns != null && node.data.columns.length > 0) {
                    datagridMeta.columns = node.data.columns;
                } else {
                    datagridMeta.columns = [{
                        dataType:"text",
                        formsName:"prop_cm_name",
                        name:"cm:name",
                        label:"Имя",
                        sortable: true,
                        type:"property"
                    }];
                }

                if (node.data.types != null && node.data.types.length > 0) {
                    datagridMeta.itemType = node.data.types;
                } else {
                    datagridMeta.itemType = "lecm-document:base";
                }
                datagridMeta.recreate = true;
            }
            if(!this.deferredListPopulation.fulfil("armNodeSelected")){
                this.populateDataGrid();
            }
        },

        onActiveFiltersChanged: function (layer, args) {
            var obj = args[1];
            if (obj !== null) {
                // Если метка не задана, или метки совпадают - дергаем метод
                var label = obj.bubblingLabel;
                if(this._hasEventInterest(label)){
                    if (!this.deferredListPopulation.fulfil("activeFiltersChanged")) {
                        this.filtersMeta = obj.filtersMeta;
                        //обновить данные в гриде! перестраивать саму таблицу не нужно
                        this._setDefaultDataTableErrors(this.widgets.dataTable);

                        if ((this.filtersMeta != null) && (this.filtersMeta.query != null)) {
                            var searchConfig = this.datagridMeta.searchConfig;
                            if (searchConfig == null) {
                                this.datagridMeta.searchConfig = {};
                                this.datagridMeta.searchConfig.filter = "";
                            }

                            var updatedSearchConfig = YAHOO.lang.merge(searchConfig, {});
                            updatedSearchConfig.filter += (this.filtersMeta.query.length > 0 ? (" AND (" + this.filtersMeta.query + ")") : "");

                            var offset = 0;
                            if (this.widgets.paginator){
                                offset = ((this.widgets.paginator.getCurrentPage() - 1) * this.options.pageSize);
                            }

                            this.search.performSearch({
                                searchConfig: updatedSearchConfig,
                                searchShowInactive: this.options.searchShowInactive,
                                parent: this.datagridMeta.nodeRef,
                                searchNodes: this.datagridMeta.searchNodes,
                                itemType: this.datagridMeta.itemType,
                                sort:this.datagridMeta.sort,
                                offset:offset,
                                filter: null
                            });
                        }
                    }
                }
            }
        },

	    populateDataGrid: function DataGrid_populateDataGrid() {
		    if (!YAHOO.lang.isObject(this.datagridMeta)) {
			    return;
		    }

		    this.renderDataGridMeta();

		    if (this.datagridMeta.columns != null) {
				var columnParam = {
					json: {
						columns: this.datagridMeta.columns
					}
				};
			    this.onDataGridColumns(columnParam);
		    } else {
			    // Query the visible columns for this list's item type
			    var configURL = "";
			    if (this.options.configURL != null) {
				    configURL = $combine(Alfresco.constants.URL_SERVICECONTEXT, this.options.configURL + "?nodeRef=" + encodeURIComponent(this.options.datagridMeta.nodeRef));
			    } else {
				    configURL = $combine(Alfresco.constants.URL_SERVICECONTEXT, "lecm/components/datagrid/config/columns?itemType=" + encodeURIComponent(this.datagridMeta.itemType) + ((this.datagridMeta.datagridFormId != null && this.datagridMeta.datagridFormId != undefined) ? "&formId=" + encodeURIComponent(this.datagridMeta.datagridFormId) : ""));
			    }

			    Alfresco.util.Ajax.jsonGet(
				    {
					    url: configURL,
					    successCallback:
					    {
						    fn: this.onDataGridColumns,
						    scope: this
					    },
					    failureCallback:
					    {
						    fn: this._onDataGridFailure,
						    obj:
						    {
							    title: this.msg("message.error.columns.title"),
							    text: this.msg("message.error.columns.description")
						    },
						    scope: this
					    }
				    });
		    }
	    },

	    onExpand: function(record) {
	        this.addExpandedRow(record, "Здесь будут связи");
	    },

        getSearchQuery: function (node, buffer, parentId) {
            if (node) {
                var query = node.data.searchQuery;
                if (query && query.length > 0) {
                    if (!buffer) {
                        buffer = [];
                    }
                    if (!parentId) {
                        parentId = node.id;
                    }
                    if (parentId == node.id) {
                        buffer.push(query);
                    }
                }
                return this.getSearchQuery(node.parent, buffer, node.data.armNodeId);
            } else {
                var resultedQuery = "";

                if (buffer) {
                    buffer = buffer.reverse();

                    for (var i = 0; i < buffer.length; i++) {
                        var q = buffer[i];
                        resultedQuery += "(" + q + ") AND "
                    }
                }

                return resultedQuery.length > 4 ?
                    resultedQuery.substring(0, resultedQuery.length - 4) : resultedQuery;
            }
        }
    }, true);
})();
