(function() {
	// in left but not in right
	function relativeComplement(left, right) {
		return left.filter(function(elem) {
			return right.indexOf(elem) == -1;
		});
	}

	// in left or in right but not in both
	function symmetricDifference(left, right) {
		return relativeComplement(left, right).concat(relativeComplement(right, left));
	}

	//получаем всех наследников типа plane_dictionary_values и hierarchical_dictionary_values
	//получаем список всех справочников и узнаем их типы
	//фильтруем и оставляем только те, которые еще не создавались
	var allTypes = dictionary.getAllDictionaryTypes();
	var existTypes = dictionary.getExistDictionaryTypes();
//	var types = symmetricDifference(allTypes, existTypes);
	var types = relativeComplement(allTypes, existTypes);
	var i, value, name, tmp;
	model.results = {};
	for (i in types) {
		tmp = types[i].split('|');
		value = tmp[0];
		name = (tmp.length > 1 && tmp[1]) ? tmp[1] : value;
		model.results[value] = name;
	}
})();
