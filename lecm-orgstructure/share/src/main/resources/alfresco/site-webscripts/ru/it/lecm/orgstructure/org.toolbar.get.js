function main()
{
    // Actions
    var myConfig = new XML(config.script),
        xmlActionSet = myConfig.actionSet,
        actionSet = [];

    for each (var xmlAction in xmlActionSet.action)
    {
        actionSet.push(
            {
                id: xmlAction.@id.toString(),
                type: xmlAction.@type.toString(),
                permission: xmlAction.@permission.toString(),
                asset: xmlAction.@asset.toString(),
                href: xmlAction.@href.toString(),
                label: xmlAction.@label.toString()
            });
    }

    model.actionSet = actionSet;

    model.bubblingLabel = args["type"];
    model.typeButton = args["typeButton"];
    model.active = args["active"];

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