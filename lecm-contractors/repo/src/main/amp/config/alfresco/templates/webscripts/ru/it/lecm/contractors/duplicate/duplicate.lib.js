function getDuplicatesInfo (model, query) {
    var count = searchCounter.query({
        query: query,
        language: "fts-alfresco",
        onerror: "exception"
    });

    model.hasDuplicate = count > 0;
    model.duplicates = [];
    if (model.hasDuplicate) {
        var results = search.query({
            query: query,
            language: "fts-alfresco",
            onerror: "exception"
        });

        for (var i in results) {
            var result = results[i];
            if (result) {
                model.duplicates.push(result);
            }
        }
    }
}