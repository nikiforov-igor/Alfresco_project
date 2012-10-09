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
}

main();