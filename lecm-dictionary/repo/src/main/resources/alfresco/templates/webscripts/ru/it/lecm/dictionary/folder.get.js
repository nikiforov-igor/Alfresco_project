/**
 * Получение корневого узла всех справочников (папка Dictionary)
 */
var dictionary = companyhome.childByNamePath("Dictionary");
if (dictionary == null || dictionary == "") {
	dictionary = companyhome.createNode("Dictionary", "cm:folder");
}
model.dictionary = dictionary;