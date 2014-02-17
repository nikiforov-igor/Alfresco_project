function main()
{
    model.bubblingLabel = args["bubblingLabel"];
    model.newRowButton = args["newRowButton"];
    model.searchButtons = args["searchButtons"];
    model.newRowLabel = args["newRowLabel"];

    var showSearchBlock = args["showSearchBlock"];
    var showExSearchBtn = args["showExSearchBtn"];
    var showButtons = args["showButtons"];

    if (showSearchBlock) {
        model.showSearchBlock = (showSearchBlock == 'true');
    }
    if (showExSearchBtn){
        model.showExSearchBtn = (showExSearchBtn == 'true');
    }
    if (showButtons){
        model.showButtons = (showButtons == 'true');
    }
}

main();