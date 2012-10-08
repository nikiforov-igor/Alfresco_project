/**
 * Main entrypoint for component webscript logic
 *
 * @method main
 */
function main()
{
    // Actions
    var actionSet = [],
        myConfig = new XML(config.script),
        xmlActionSet = myConfig.actionSet;

    for each (var xmlAction in xmlActionSet.action)
    {
        actionSet.push(
            {
                id: xmlAction.@id.toString(),
                type: xmlAction.@type.toString(),
                permission: xmlAction.@permission.toString(),
                href: xmlAction.@href.toString(),
                label: xmlAction.@label.toString()
            });
    }

    model.actionSet = actionSet;
}

main();