var Filters =
{
    /**
     * Types that we want to suppress from the resultset
     */
    IGNORED_TYPES:
        [
            "cm:systemfolder",
            "fm:forums",
            "fm:forum",
            "fm:topic",
            "fm:post"
        ],

    /**
     * Create filter parameters based on input parameters
     *
     * @method getFilterParams
     * @param filter {string} Required filter
     * @param parsedArgs {object} Parsed arguments object literal
     * @return {object} Object literal containing parameters to be used in Lucene search
     */
    getFilterParams: function Filter_getFilterParams(filter) {
        var filterParams =
        {
            query: "",
            limitResults: null,
            sort: [
                {
                    column: "@cm:name",
                    ascending: true
                }
            ],
            language: "fts",
            templates: null
        };

        // Max returned results specified?
        var argMax = filter.max;
        if ((argMax !== null) && !isNaN(argMax)) {
            filterParams.limitResults = argMax;
        }

        // Create query based on passed-in arguments
        var isSimpleFilter = (filter.fromUrl != null && ("" + filter.fromUrl == "true"));

        var filterData = String(filter.curValue || ""),
            filterQuery = filterParams.query;

        var filterId = filter.code ? filter.code : "custom";

        switch (String(filterId)) {
            case "my":
                filterParams.query = " +@cm\\:creator:\"" + person.properties.userName + '"';
                break;

            case "tag":
                // Remove any trailing "/" character
                if (filterData.charAt(filterData.length - 1) == "/") {
                    filterData = filterData.slice(0, -1);
                }
                filterParams.query += "+PATH:\"/cm:taggable/cm:" + search.ISO9075Encode(filterData) + "/member\"";
                break;

            default:
                var query = isSimpleFilter ? documentScript.getFilterQuery(filterId + "|" + filterData) : arm.getQueryByFilter(filter);
                filterParams.query = (query && query != "") ? query : filterQuery;
                break;
        }

        return filterParams;
    }
};
