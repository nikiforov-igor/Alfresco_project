var types = [];

var dictinaryErrandsTypes = dictionary.getDictionaryByName("Типы поручений");
if (dictinaryErrandsTypes) {
    types = dictionary.getChildren(dictinaryErrandsTypes.nodeRef);
}

model.types = types;
