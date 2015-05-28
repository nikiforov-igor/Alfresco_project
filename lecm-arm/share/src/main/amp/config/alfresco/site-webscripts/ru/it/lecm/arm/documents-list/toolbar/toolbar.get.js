function main() {
    model.bubblingLabel = args['bubblingLabel'];
    model.showColumnsBtn = args["showColumnsBtn"] != null ? (args["showColumnsBtn"] == "true") : true;
    model.showFiltersBtn = args["showFiltersBtn"] != null ? (args["showFiltersBtn"] == "true") : true;
    model.showSearchBlock = args["showSearchBlock"] != null ? (args["showSearchBlock"] == "true") : true;
}

main();