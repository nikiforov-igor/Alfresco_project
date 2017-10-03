if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Incoming = LogicECM.module.Incoming || {};


(function () {
	var Dom = YAHOO.util.Dom;

	LogicECM.module.Incoming.SearchRepeatedDocuments = function (containerId) {
		LogicECM.module.Incoming.SearchRepeatedDocuments.superclass.constructor.call(this, containerId);

		this.checkboxElements = [];
		return this;
	};

	YAHOO.lang.extend(LogicECM.module.Incoming.SearchRepeatedDocuments, LogicECM.module.AssociationSearchViewer);

	YAHOO.lang.augmentObject(LogicECM.module.Incoming.SearchRepeatedDocuments.prototype, {
		documentRef: null,
		checkboxElements: [],
		firstLoad: true,

		_generateChildrenUrlParams: function (searchTerm) {
            var customSearchTerm;
			if (Dom.get(this.options.controlId + "-picker-searchText").value) {
                customSearchTerm = "lecm-document:present-string:" + Dom.get(this.options.controlId + "-picker-searchText").value;
			} else {
                customSearchTerm = "lecm-document:present-string:" + "";
			}
			var params = "?selectableType=" + this.options.itemType + "&customSearchTerm=" + encodeURIComponent(customSearchTerm) +
				"&size=" + this.options.maxSearchResults + "&nameSubstituteString=" + encodeURIComponent(this.options.nameSubstituteString) +
				"&selectedItemsNameSubstituteString=" + encodeURIComponent(this.getSelectedItemsNameSubstituteString()) +
				"&additionalFilter=" + encodeURIComponent(this.options.additionalFilter);

			if (this.options.rootLocation && this.options.rootLocation.charAt(0) == "/") {
				params += "&xpath=" + encodeURIComponent(this.options.rootLocation);
			}

			params += "&documentRef=" + encodeURIComponent(this.options.documentRef);
			if (this.checkboxElements[0].checked) {
				params += "&selectAll=true";
			} else {
                params += "&selectAll=false";
			}
			for (var i = 1; i < this.checkboxElements.length; i++) {
				if (this.checkboxElements[i].checked) {
					params += "&" + this.checkboxElements[i].name + "=true";
                    atLeastOne = true;
				} else {
                    params += "&" + this.checkboxElements[i].name + "=false";
				}
			}

            var el = Dom.get(this.options.controlId + "_search-repeats-options-search-mode");
            if (el.value == "at_least_one") {
                params += "&searchMode=at_least_one";
            } else {
                params += "&searchMode=all";
			}
			params += "&sortProp=score";

			return params;
		},

        checkSearchField: function SearchRepeatedDocuments_checkSearchField() {

        },
        extendedInit: function SearchRepeatedDocuments__extendedInit() {
            YAHOO.Bubbling.on("dataTableFirstLoad", this.onDataTableFirstLoad, this);

            this.initCheckboxElements();
            YAHOO.util.Event.addListener(Dom.get(this.options.controlId + "_search-repeats-options-attributes-match-select-all"), 'change', this.onChangeSelectAll, null, this);
            YAHOO.util.Event.addListener(Dom.get(this.options.controlId + "_search-repeats-options-switch-link"), 'click', this.clickOnSearchRepeatsOptions, null, this);
            this.setAttributesCheckboxes();
            this.onSearch();

            this.widgets.clearButton = new YAHOO.widget.Button(this.options.controlId + "_search-repeats-options-clearButton");
            this.widgets.clearButton.on("click", this.clearSearchFields, this.widgets.clearButton, this);

            for (var i = 1; i < this.checkboxElements.length; i++) {
                YAHOO.util.Event.addListener(this.checkboxElements[i], 'change', this.onChangeCheckbox, null, this)
            }
        },

        //override
        _updateItems: function AssociationSearchViewer__updateItems(searchTerm)
        {
            // Empty results table - leave tag entry if it's been rendered
            this.widgets.dataTable.set("MSG_EMPTY", this.msg("label.loading"));
            this.widgets.dataTable.showTableMessage(this.msg("label.loading"), YAHOO.widget.DataTable.CLASS_EMPTY);
            this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());

            var successHandler = function AssociationSearchViewer__updateItems_successHandler(sRequest, oResponse, oPayload)
            {
                this.options.parentNodeRef = oResponse.meta.parent ? oResponse.meta.parent.nodeRef : null;
                this.widgets.dataTable.set("MSG_EMPTY", this.msg("form.control.object-picker.items-list.empty"));
                this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
                YAHOO.Bubbling.fire("dataTableFirstLoad", {
                	response: oResponse
				})
            };

            var failureHandler = function AssociationSearchViewer__updateItems_failureHandler(sRequest, oResponse)
            {
                if (oResponse.status == 401)
                {
                    // Our session has likely timed-out, so refresh to offer the login page
                    window.location.reload();
                }
                else
                {
                    try
                    {
                        //var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                        var response = {
                            message:this.msg("search.document.filter.error")
                        };
                        this.widgets.dataTable.set("MSG_ERROR", response.message);
                        this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
                    }
                    catch(e)
                    {
                    }
                }
            };

            // build the url to call the pickerchildren data webscript
            var url = "/node/children" + this._generateChildrenUrlParams(searchTerm);

            if (Alfresco.logger.isDebugEnabled())
            {
                Alfresco.logger.debug("Generated pickerchildren url fragment: " + url);
            }

            // call the pickerchildren data webscript
            this.widgets.dataSource.sendRequest(url,
                {
                    success: successHandler,
                    failure: failureHandler,
                    scope: this
                });

            // the start location is now resolved
            this.startLocationResolved = true;
        },

        onDataTableFirstLoad: function (layer, args) {
			if (this.firstLoad) {
                var response = args[1].response;
                if (response && !response.results.length) {
                    this.clickOnSearchRepeatsOptions();
                }
			}
			this.firstLoad = false;
        },

		onChangeCheckbox: function () {
            for (var i = 1; i < this.checkboxElements.length; i++) {
                if (!this.checkboxElements[i].disabled && !this.checkboxElements[i].checked) {
                    this.checkboxElements[0].checked = false;
                    return;
                }
            }
            this.checkboxElements[0].checked = true;
        },

		onChangeSelectAll: function () {
		    var activeCheckBoxes = this.checkboxElements.filter(function (checkbox) {
                return !checkbox.disabled
            });
            activeCheckBoxes.forEach(function (checkbox) {
                if (this.checkboxElements[0].checked) {
                    checkbox.checked = true;
                } else {
                    checkbox.checked = false;
                }
            }, this);
        },

		setAttributesCheckboxes: function () {
			for (var i = 1; i < this.checkboxElements.length; i++) {
                if (Dom.get(this.checkboxElements[i].id + "-value").textContent != "(Нет)") {
                    this.checkboxElements[i].checked = true;
                } else {
                    this.checkboxElements[i].disabled = true;
                }
			}
            this.onChangeCheckbox();
		},

        clickOnSearchRepeatsOptions: function() {
			var searchRepeatsOptionsControlId = this.options.controlId + "_search-repeats-options-search-attributes",
				switchLinkId = this.options.controlId + "_search-repeats-options-switch-link";
			if (Dom.hasClass(searchRepeatsOptionsControlId, "hidden")) {
				Dom.removeClass(searchRepeatsOptionsControlId, "hidden");
				Dom.get(switchLinkId).text = Alfresco.util.message("label.incoming.search_repeats_options.switch-link.hide");
			} else {
				Dom.addClass(searchRepeatsOptionsControlId, "hidden");
				Dom.get(switchLinkId).text = Alfresco.util.message("label.incoming.search_repeats_options.switch-link.show");
			}
    	},

    	clearSearchFields: function () {
			var searchTextField = Dom.get(this.options.controlId + "-picker-searchText"),
                searchOptionsId = this.options.controlId + "_search-repeats-options";
			if (searchTextField.value) {
                searchTextField.value = "";
			}
            this.checkboxElements[0].checked = false;
            this.onChangeSelectAll();

            var el = Dom.get(searchOptionsId + "-search-mode");
            el.value = "at_least_one";

            this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());
        },

        initCheckboxElements: function () {
            var attributesMatchId = this.options.controlId + "_search-repeats-options-attributes-match";
            this.checkboxElements.push(Dom.get(attributesMatchId + "-select-all"));
            this.checkboxElements.push(Dom.get(attributesMatchId + "-sender"));
            this.checkboxElements.push(Dom.get(attributesMatchId + "-addressee"));
            this.checkboxElements.push(Dom.get(attributesMatchId + "-title"));
            this.checkboxElements.push(Dom.get(attributesMatchId + "-outgoing_number"));
            this.checkboxElements.push(Dom.get(attributesMatchId + "-outgoing_date"));
            this.checkboxElements.push(Dom.get(attributesMatchId + "-subject"));
        }
	}, true);
})();
