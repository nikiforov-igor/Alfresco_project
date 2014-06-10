if (typeof json !== "undefined" && json.has("query")) {
    var queryDef = {
        query:json.get("query"),
        language:"fts-alfresco",
        onerror:"no-results"
    };

    model.count = searchCounter.query(queryDef);
} else {
    model.count = 0;
}
