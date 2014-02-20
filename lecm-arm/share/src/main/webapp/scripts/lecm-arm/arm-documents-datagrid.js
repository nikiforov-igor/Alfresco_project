var $siteURL = Alfresco.util.siteURL,
	$combine = Alfresco.util.combinePaths;

(function () {

    var Dom = YAHOO.util.Dom,
        Bubbling = YAHOO.Bubbling;

    LogicECM.module.ARM.DataGrid = function (containerId) {
        LogicECM.module.ARM.DataGrid.superclass.constructor.call(this, containerId)

        this.filtersMeta = null;
        YAHOO.Bubbling.on("activeFiltersChanged", this.onActiveFiltersChanged, this);
        return this;
    };

    YAHOO.lang.extend(LogicECM.module.ARM.DataGrid, LogicECM.module.Base.DataGrid);

    YAHOO.lang.augmentObject(LogicECM.module.ARM.DataGrid.prototype, {
        doubleClickLock: false,

        filtersMeta: null,

        onActiveFiltersChanged: function (layer, args) {
            var obj = args[1];
            if (obj !== null) {
                // Если метка не задана, или метки совпадают - дергаем метод
                var label = obj.bubblingLabel;
                if(this._hasEventInterest(label)){
                    this.filtersMeta = obj.filtersMeta;
                    //обновить данные в гриде! перестраивать саму таблицу не нужно
                    this._setDefaultDataTableErrors(this.widgets.dataTable);

                    if ((this.filtersMeta != null) && (this.filtersMeta.query != null) && this.filtersMeta.query.length > 0) {
                        var searchConfig = this.datagridMeta.searchConfig;
                        if (searchConfig == null) {
                            this.datagridMeta.searchConfig = {};
                            this.datagridMeta.searchConfig.filter = "";
                        }

                        var updatedSearchConfig = YAHOO.lang.merge(searchConfig, {});
                        updatedSearchConfig.filter = updatedSearchConfig.filter + " AND (" + this.filtersMeta.query + ")";

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
				}
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
	    }
    }, true);
})();
