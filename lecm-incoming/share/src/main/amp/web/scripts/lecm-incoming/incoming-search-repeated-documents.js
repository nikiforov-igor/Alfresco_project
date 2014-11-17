if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Incoming = LogicECM.module.Incoming || {};


(function () {
	var Dom = YAHOO.util.Dom;

	LogicECM.module.Incoming.SearchRepeatedDocuments = function (containerId) {
		LogicECM.module.Incoming.SearchRepeatedDocuments.superclass.constructor.call(this, containerId);
		return this;
	};

	YAHOO.lang.extend(LogicECM.module.Incoming.SearchRepeatedDocuments, LogicECM.module.AssociationSearchViewer);

	YAHOO.lang.augmentObject(LogicECM.module.Incoming.SearchRepeatedDocuments.prototype, {
		documentRef: null,

		_generateChildrenUrlParams: function (searchTerm) {
			var params = "?selectableType=" + this.options.itemType + "&searchTerm=" + encodeURIComponent(searchTerm) +
				"&size=" + this.options.maxSearchResults + "&nameSubstituteString=" + encodeURIComponent(this.options.nameSubstituteString) +
				"&selectedItemsNameSubstituteString=" + encodeURIComponent(this.getSelectedItemsNameSubstituteString()) +
				"&additionalFilter=" + encodeURIComponent(this.options.additionalFilter);

			if (this.options.rootLocation && this.options.rootLocation.charAt(0) == "/") {
				params += "&xpath=" + encodeURIComponent(this.options.rootLocation);
			}

			params += "&documentRef=" + encodeURIComponent(this.options.documentRef);
			if (Dom.get(this.options.controlId + "-search-similar").checked) {
				params += "&searchSimilar=true";
			}

			return params;
		},

        checkSearchField: function SearchRepeatedDocuments_checkSearchField() {
            var term = Dom.get(this.options.pickerId + "-searchText").value;
            if ((term == null || term == "") && !Dom.get(this.options.controlId + "-search-similar").checked) {
                this.widgets.searchButton.set("disabled", true);
            } else {
                this.widgets.searchButton.set("disabled", false);
            }
        },
        extendedInit: function SearchRepeatedDocuments__extendedInit() {
            Dom.get(this.options.controlId + "-search-similar").onchange = this.checkSearchField.bind(this);
        }
	}, true);
})();
