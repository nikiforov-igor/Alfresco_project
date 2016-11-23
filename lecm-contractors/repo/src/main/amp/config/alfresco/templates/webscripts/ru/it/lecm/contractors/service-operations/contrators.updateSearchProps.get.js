/*Без ISNOTNULL запрос не отрабатывает - пришлось добавить*/
var searchQuery = 'TYPE:"lecm-contractor:contractor-type" AND ISNOTNULL:"cm:name"';

model.updated = 0;

var propsForUpdate = ["lecm-contractor:fullname", "lecm-contractor:shortname"];
var letsContinue = true;

var i, j,  size, size2, result, skipCount;

skipCount = 0;

while (letsContinue) {
    result = search.query({
        query: searchQuery,
        language: "fts-alfresco",
        page: {maxItems: 1000, skipCount: skipCount},
        onerror: "no-results",
        sort: [
            {
                "column": "@sys:node-dbid",
                "ascending": true
            }]
    });


    letsContinue = result.length > 0;
    skipCount += result.length;

    if (letsContinue) {
        for (i = 0, size = result.length; i < size; i++) {
            var contractor = result[i];
            for (j = 0, size2 = propsForUpdate.length; j < size2; j++) {
                var propValue = contractor.properties[propsForUpdate[j]];
                if (propValue) {
                    contractor.properties[propsForUpdate[j] + "-search"] = contractorsRootObject.formatContractorName(propValue);
                }
            }
            contractor.save();

            model.updated += 1;
        }
    }
}