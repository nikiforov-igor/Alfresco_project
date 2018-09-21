var types = [];
var dicName = msg.get("ru.it.lecm.dictionaries.errandTypes.name") || "Типы поручений";
var dictinaryErrandsTypes = dictionary.getDictionaryByName(dicName);
if (dictinaryErrandsTypes) {
    types = dictionary.getChildren(dictinaryErrandsTypes.nodeRef);
}

model.types = types;
