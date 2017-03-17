<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

(function() {
	// Переопределяем поведение построения фильтра - получаем только непосредственно дочерние результаты
	getFilterParams = function getFilterParams(filterData, parentXPath)
	{
		var query = " +PATH:\"" + parentXPath + "/*\"";
		var columns = [];
		if (filterData) {
			var keyWord = filterData.match(/.*:(.*)$/)[1];

			if(keyWord) {
				columns.push('lecm-os:nomenclature-year-section-year:' + keyWord);
				columns.push('lecm-os:nomenclature-unit-section-index:' + keyWord);
				columns.push('cm:title:' + keyWord);
			}
		}

		var params = "",
			or = " OR",
			ampersand = " @";
		for (var i=0; i < columns.length; i++) {
			var namespace = columns[i].split(":");
			if (!columns[i+1]) {
				or = "";
				ampersand = " @";
			}

			var searchTerm = escapeString(trimString(namespace[2]));
			var searchArray = searchTerm.split(" ");
			var filter = "";
			for (var j = 0; j < searchArray.length; j++) {
				var asterisk = namespace[1] == 'nomenclature-year-section-year' ? '' : '*'
				filter += '"' + asterisk + searchArray[j] + asterisk + '"';
				if (j < searchArray.length - 1) {
					filter += " OR ";
				}
			}

			params += ampersand + namespace[0]+"\\:" + namespace[1] + ":"+ '(' + filter + ')' + or;
		}
		if (params) {
			//если явный поиск - то ищем неограниченно глубоко
			query = " +PATH:\"" + parentXPath + "//*\" AND " + "(" + params + " )";
		}
		return query;
	}

	var data = getPickerChildrenItems();

	model.results = data.results;
	model.parent = data.parent;
	model.rootNode = data.rootNode;
	model.additionalProperties = data.additionalProperties;

})();