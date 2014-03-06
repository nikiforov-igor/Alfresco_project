function main() {
    var filtersArray = [];
    var PREF_FILTERS = "ru.it.lecm.arm.current-filters";

    if (typeof json !== "undefined" && json.has("filters")) {
        // список фильтров
        var filters = json.get("filters");
        var filtersJSON = eval('(' + filters.toString() + ')');

        //сохраненные фильтры
        var filtersPref = preferenceService.getPreferences(person.properties["cm:userName"], PREF_FILTERS);
        var currentFilters = findValueByDotNotation(eval('(' + jsonUtils.toJSONString(filtersPref) + ')'), PREF_FILTERS, null);
        if (currentFilters != null) {
	        currentFilters = eval('(' + currentFilters + ')');
        }

        for (var index in filtersJSON) {
            var filter = filtersJSON[index];
            // подменяем фильтр на сохраненный по коду
            if (currentFilters != null && currentFilters.length > 0) {
                var curFilter = findInArray(filter.code, currentFilters);
                if (curFilter != null) {
                    filter = curFilter;
                    filter.curValue = filter.curValue != null ? ("" + filter.curValue).split(",") : [];
                }
            }

            for (var i in filter.values) {
                filter.values[i].checked =  (filter.curValue != null && existInArray(filter.values[i].code.replace(/^\s+/, ''), filter.curValue));
            }

            filtersArray.push(filter);
        }
        model.filters = filtersArray;
    }
}

main();

function findValueByDotNotation(obj, propertyPath, defaultValue) {
    var value = defaultValue ? defaultValue : null;
    if (propertyPath && obj) {
        var currObj = obj;
        var props = propertyPath.split(".");
        for (var i = 0; i < props.length; i++) {
            currObj = currObj[props[i]];
            if (typeof currObj == "undefined") {
                return value;
            }
        }
        return currObj;
    }
    return value;
}

function findInArray(filterCode, filtersArray) {
    for (var i = 0; i < filtersArray.length; i++) {
        var filter = filtersArray[i];
        if (filter.code == filterCode) {
            return filter;
        }
    }
    return null;
}

function existInArray(value, testArray) {
    if (value == null) {
        return false;
    }
    for (var i = 0; i < testArray.length; i++) {
        var testValue = testArray[i];
        if (value == testValue) {
            return true;
        }
    }
    return false;
}
