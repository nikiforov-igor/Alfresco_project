function main()
{
    var jsonStr= remote.connect("alfresco").get(encodeURI("/lecm/forms/node/search?xpath=" + args["parent"] + "&titleProperty=cm:name"));
    var obj = eval("("+ jsonStr + ")");

    model.nodeRef = obj.nodeRef;
}

main();
