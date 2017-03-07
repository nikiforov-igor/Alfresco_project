function main() {
    var filtersArray = [];

    if (typeof json !== "undefined" && json.has("filters")) {
        // список фильтров
        var filters = json.get("filters");
        var filtersJSON = eval('(' + filters.toString() + ')');

        for (var index in filtersJSON) {
            var filter = filtersJSON[index];

            for (var i in filter.values) {
                filter.values[i].checked = false;
            }

            if (filter.curValue && filter.curValue.length) {
                for (var i = 0; i < filter.curValue.length; i++) {
                    var selectedIndex = filter.curValue[i];
                    if (filter.values[selectedIndex]) {
                        filter.values[selectedIndex].checked = true;
                    }
                }
            }
            filtersArray.push(filter);
        }
    }
    model.filters = filtersArray;
}

main();