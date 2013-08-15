function sortByTitle(type1, type2) {
    var title1 = (type1.title || type1.name).toUpperCase(),
        title2 = (type2.title || type2.name).toUpperCase();
    return (title1 > title2) ? 1 : (title1 < title2) ? -1 : 0;
}

function getTypes(){
    var types = base.getRegisteredTypes();
    types.sort(sortByTitle);
    return types;
}

model.types = getTypes();
