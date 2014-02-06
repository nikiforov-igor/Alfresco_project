var queryDef = {
    query:args["query"],
    language:"fts-alfresco",
    onerror:"no-results"
};

model.count = searchCounter.query(queryDef);