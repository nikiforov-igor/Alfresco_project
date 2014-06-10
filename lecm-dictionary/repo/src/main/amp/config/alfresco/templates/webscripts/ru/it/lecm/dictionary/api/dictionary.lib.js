/**
 * Получение атрибутов объекта
 *
 * @param node объект
 * @param propertiesToSkip массив атрибутов, которые не должны быть включены в конечный список
 * @return {Array} массив атрибутов объекта
 */
function getPropertiesName(node, propertiesToSkip) {
	var propertiesName = [];
	var nodeProp = node.getPropertyNames(true);
	for(var i = 0; i < nodeProp.length; i++) {
		var propName = nodeProp[i];
		if ((propertiesToSkip == null || !(propName in propertiesToSkip)) && propName.indexOf("sys:") != 0) {
			propertiesName.push(propName);
		}
	}
	return propertiesName;
}

/**
 * Получение атрибутов типа объекта
 *
 * @param node объект
 * @param propertiesToSkip массив атрибутов, которые не должны быть включены в конечный список
 * @return {Array} массив атрибутов объекта
 */
function getTypePropertiesName(node, propertiesToSkip) {
	var propertiesName = [];
	var nodeProp = node.getTypePropertyNames(true);
	for(var i = 0; i < nodeProp.length; i++) {
		var propName = nodeProp[i];
		if (propertiesToSkip == null || !(propName in propertiesToSkip)) {
			propertiesName.push(propName);
		}
	}
	return propertiesName;
}