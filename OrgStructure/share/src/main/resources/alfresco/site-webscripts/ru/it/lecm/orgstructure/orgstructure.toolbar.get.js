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

    var showNRB = false;
    var showNUB = true;
    // default - show structure and Add Unit Button
    var type = args["type"];
    if (type != null && type != '') {
        if (type != 'structure') {
            // hide "New Unit Button", Show "New Row Button"
            showNRB = true;
            showNUB = false;
        }
    }
    model.showNRB = showNRB;
    model.showNUB = showNUB;
}

main();