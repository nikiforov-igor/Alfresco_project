function main() {
    var filtersArray = [];
    var PREF_FILTERS = "ru.it.lecm.arm." + json.get("armCode") + ".current-filters";

    if (typeof json !== "undefined" && json.has("filters")) {
        // список фильтров
        var filters = json.get("filters");
        var filtersJSON = eval('(' + filters.toString() + ')');

        //сохраненные фильтры
        var filtersPref = preferenceService.getPreferences(person.properties["cm:userName"], PREF_FILTERS);
        var currentFilters = findValueByDotNotation(eval('(' + jsonUtils.toJSONString(filtersPref) + ')'), PREF_FILTERS, null);
        if (currentFilters) {
            currentFilters = eval('(' + currentFilters + ')');
        }

        var hasFilters = currentFilters && currentFilters.length;
        for (var index in filtersJSON) {
            var filter = filtersJSON[index];

            for (var i in filter.values) {
                filter.values[i].checked = false;
            }

            if (hasFilters) {
                var curFilter = findInArray(filter.code, currentFilters);
                if (curFilter) {
                    var curFilterValuesIndxs = curFilter.value ? curFilter.value.split(",") : [];
                    for (var i = 0; i < curFilterValuesIndxs.length; i++) {
                        filter.values[curFilterValuesIndxs[i]].checked = true;
                    }
                }
            }

            filtersArray.push(filter);
        }
    }
    model.filters = filtersArray;
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