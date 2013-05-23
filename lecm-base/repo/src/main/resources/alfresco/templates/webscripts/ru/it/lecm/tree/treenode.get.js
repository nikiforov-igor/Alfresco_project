<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/documentlibrary/parse-args.lib.js">

model.treenode = getTreeNode();

/* Create collection of folders in the given space */
function getTreeNode() {
    try {
        var items = new Array(),
            hasSubfolders = true,
            ignoredTypes = 'fm:forum,fm:topic',
            evalChildFolders = args["children"] !== "false",
            resultsTrimmed = false,
            argMax = parseInt(args["max"], 10),
            maxItems = isNaN(argMax) ? -1 : argMax;

        // Use helper function to get the arguments
        var parsedArgs = ParseArgs.getParsedArgs();
        if (parsedArgs === null) {
            return;
        }

        // Look for folders in the pathNode - sort by ascending name
        var pagedResult = base.getNotLecmChilds(parsedArgs.pathNode, false, true, ignoredTypes, maxItems, 0, "cm:name", true, "TODO");

        if (pagedResult.hasMoreItems() == true) {
            resultsTrimmed = true;
        }

        for each(item in pagedResult.page)
        {
            if (evalChildFolders) {
                hasSubfolders = item.childFileFolders(false, true, ignoredTypes, 1).page.length > 0;
            }

            items.push(
                {
                    node: item,
                    hasSubfolders: hasSubfolders
                });
        }

        return (
        {
            parent: parsedArgs.pathNode,
            resultsTrimmed: resultsTrimmed,
            items: items
        });
    }
    catch (e) {
        status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, e.toString());
        return;
    }
}