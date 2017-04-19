var value = armWrapper.getAccordeons(args["armRef"]);
if (url.getMatch().indexOf("forDashlet") != -1) {

    value = value.filter(function (rootNode) {
        var rootIsOk = !rootNode.properties["lecm-arm:is-for-secretaries"];
        if (rootIsOk) {
            var children = rootNode.children;
            if (children && children.length) {
                //проверка узлов первого уровня
                rootIsOk = children.some(function (child) {
                    return ("" + child.typeShort) == "lecm-arm:node";
                });
            } else {
                rootIsOk = false;
            }
        }
        return rootIsOk;
    });
}
model.values = value;
