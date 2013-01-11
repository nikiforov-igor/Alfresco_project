function main()
{
    model.bubblingLabel = args["bubblingLabel"];
    model.newRowLabel = args["newRowLabel"];

    if (args["showSearchBlock"]) {
        model.showSearchBlock = (args["showSearchBlock"] == 'true');
    }
    if (args["showExSearchBtn"]){
        model.showExSearchBtn = (args["showExSearchBtn"] == 'true');
    }

    if (args["showButtons"]){
        model.showButtons = (args["showButtons"] == 'true');
    }
}

main();