function main()
{
    var showSearchBlock = args["showSearchBlock"];
    var showExSearchBtn = args["showExSearchBtn"];
    var showButtons = args["showButtons"];
    var showButtonsCreate = args["showButtonsCreate"];
	model.bubblingLabel = args["bubblingLabel"];

    if (showSearchBlock) {
        model.showSearchBlock = (showSearchBlock == 'true');
    }
    if (showExSearchBtn){
        model.showExSearchBtn = (showExSearchBtn == 'true');
    }

    if (showButtons){
        model.showButtons = (showButtons == 'true');
    }

    if (showButtonsCreate){
        model.showButtonsCreate = (showButtonsCreate == 'true');
    }
}

main();