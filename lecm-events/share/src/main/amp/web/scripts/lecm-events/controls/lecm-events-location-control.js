if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Calendar = LogicECM.module.Calendar || {};

(function()
{
	/**
	 * Alfresco Slingshot aliases
	 */
	var Dom = YAHOO.util.Dom,
		Util = LogicECM.module.Base.Util;

	LogicECM.module.Calendar.LocationControl = function (htmlId)
	{
		this.id = htmlId;
		LogicECM.module.Calendar.LocationControl.superclass.constructor.call(this, htmlId);

		return this;
	};

	YAHOO.extend(LogicECM.module.Calendar.LocationControl, LogicECM.module.AssociationTokenControl, {});

	YAHOO.lang.augmentObject(LogicECM.module.Calendar.LocationControl.prototype, {
		_generateChildrenUrlParams: function (searchTerm, forAutocomplete)
		{
			var additionalFilter = this.options.additionalFilter;
			var allowedNodesFilter = "";

			if (this.options.allowedNodes) {
				if (this.options.allowedNodes.length) {
					for (var i in this.options.allowedNodes) {
						if (allowedNodesFilter.length > 0) {
							allowedNodesFilter += " OR ";
						}
						allowedNodesFilter += "ID:\"" + this.options.allowedNodes[i] + "\"";
					}
				} else {
					allowedNodesFilter = '(ISNULL:"sys:node-dbid" OR NOT EXISTS:"sys:node-dbid")';
				}

				if (additionalFilter != null && additionalFilter.length > 0) {
					var notSingleQueryPattern = /^NOT[\s]+.*(?=\sOR\s|\sAND\s|\s\+|\s\-)/i;
					var singleNotQuery = additionalFilter.indexOf("NOT") == 0 && !notSingleQueryPattern.test(additionalFilter);

					additionalFilter = (!singleNotQuery ? "(" : "") + additionalFilter + (!singleNotQuery ? ")" : "") + " AND (" + allowedNodesFilter + ")";
				} else {
					additionalFilter = allowedNodesFilter;
				}
			}

			if (this.options.ignoreNodes != null && this.options.ignoreNodes.length > 0) {
				var ignoreNodesFilter = "";
				for (var i = 0; i < this.options.ignoreNodes.length; i++) {
					if (ignoreNodesFilter !== "") {
						ignoreNodesFilter += " AND ";
					}
					ignoreNodesFilter += "NOT ID:\"" + this.options.ignoreNodes[i] + "\"";
				}

				var addBrackets = this.options.ignoreNodes.length > 1;
				if (additionalFilter != null && additionalFilter.length > 0) {
					var notSingleQueryPattern = /^NOT[\s]+.*(?=\sOR\s|\sAND\s|\s\+|\s\-)/i;
					var singleNotQuery = additionalFilter.indexOf("NOT") == 0 && !notSingleQueryPattern.test(additionalFilter);

					additionalFilter = (!singleNotQuery ? "(" : "") + additionalFilter + (!singleNotQuery ? ")" : "") + " AND " + (addBrackets ? "(" : "") + ignoreNodesFilter + (addBrackets ? ")" : "");
				} else {
					additionalFilter = ignoreNodesFilter;
				}
			}

			var params = "?selectableType=" + this.options.itemType + "&searchTerm=" + encodeURIComponent(searchTerm) +
				"&size=" + this.getMaxSearchResult(forAutocomplete) +
				"&nameSubstituteString=" + encodeURIComponent(this.options.nameSubstituteString) +
				"&sortProp=" + encodeURIComponent(this.options.sortProp) +
				"&selectedItemsNameSubstituteString=" + encodeURIComponent(this.getSelectedItemsNameSubstituteString()) +
				"&additionalFilter=" + encodeURIComponent(additionalFilter) +
				"&pathRoot=" + encodeURIComponent(this.options.rootLocation) +
				"&pathNameSubstituteString=" + encodeURIComponent(this.options.treeNodeSubstituteString) +
				"&onlyInSameOrg=" + encodeURIComponent("" + this.options.useStrictFilterByOrg) +
				'&doNotCheckAccess=' + encodeURIComponent("" + this.options.doNotCheckAccess) +
				'&rootNodeRef=' + encodeURIComponent("" + this.options.rootNodeRef);

			if (forAutocomplete) {
				params += "&xpath=" + encodeURIComponent(this.options.rootLocation);
			} else {
				params += "&skipCount=" + this.skipItemsCount;
			}

			if (this.options.eventNodeRef != null) {
				params += "&eventNodeRef=" + encodeURIComponent(this.options.eventNodeRef);
			}
			if (this.options.fromDate != null) {
				params += "&fromDate=" + encodeURIComponent(this.options.fromDate);
			}
			if (this.options.toDate != null) {
				params += "&toDate=" + encodeURIComponent(this.options.toDate);
			}

			return params;
		}
	}, true);
})();
