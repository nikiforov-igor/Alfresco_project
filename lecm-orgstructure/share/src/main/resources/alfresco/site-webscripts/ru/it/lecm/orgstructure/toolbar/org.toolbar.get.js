function main()
{
    model.bubblingLabel = args["bubblingLabel"];
    model.newRowButton = args["newRowButton"];
    model.searchButtons = args["searchButtons"];
    model.newRowLabel = args["newRowLabel"];
    model.showStructureLabel = args["showStructureLabel"];

    var showSearchBlock = args["showSearchBlock"];
    var showExSearchBtn = args["showExSearchBtn"];
    var showButtons = args["showButtons"];
    var showStructure = args["showStructure"];

    if (showSearchBlock) {
        model.showSearchBlock = (showSearchBlock == 'true');
    }
    if (showExSearchBtn){
        model.showExSearchBtn = (showExSearchBtn == 'true');
    }
    if (showStructure){
        model.showStructure = (showStructure == 'true');
    }
    if (showButtons){
        model.showButtons = (showButtons == 'true');
    }
}

main();