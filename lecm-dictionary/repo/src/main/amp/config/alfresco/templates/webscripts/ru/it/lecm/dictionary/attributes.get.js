(function() {
	//получить все проперти которые явно присутствуют у объекта заданного типа
	var dataType = args['dataType'];

	var dictionaryProperties = dictionary.getDictionaryTypeProperties(dataType);
	var i, value, name, tmp;
	model.results = {};
	for (i in dictionaryProperties) {
		tmp = dictionaryProperties[i].split('|');
		value = tmp[0];
		name = (tmp.length > 1 && tmp[1]) ? tmp[1] : value;
		model.results[value] = name;
	}
})();
