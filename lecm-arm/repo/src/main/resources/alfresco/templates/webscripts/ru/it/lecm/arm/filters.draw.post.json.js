var filtersArray = [];
if (typeof json !== "undefined" && json.has("filters")) {
    var filters = json.get("filters");
    var filtersJSON = eval('(' + filters.toString() + ')');
    for (var index in filtersJSON) {
        var filter = filtersJSON[index];
        if (filter.values){
            var fValues = filter.values.split(",");
            var valuesArr = [];
            for (var value in fValues) {
                var vArray = fValues[value].split("|");
                var v = {
                    code:vArray[0],
                    title:vArray[1]
                };
                valuesArr.push(v);
            }
            filter.values = valuesArr;
        }
        filtersArray.push(filtersJSON[index]);
    }
    model.filters = filtersArray;
}