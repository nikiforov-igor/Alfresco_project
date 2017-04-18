var value = armWrapper.getAccordeons(args["armRef"]);
if (url.getMatch().indexOf("forDashlet") != -1) {

    function isTypeOK(node) {
        return node.typeShort != "lecm-arm:html-node" && node.typeShort != "lecm-arm:reports-node";
    }

    function concatNodes(childs, armNodes) {
        if (childs && childs.length) {
            var nodes = childs.filter(function (child) {
                return child.typeShort == "lecm-arm:node";
            });
            nodes.forEach(function (node) {
                var isFinalNode = node.children.every(function (item) {
                    return item.typeShort != "lecm-arm:node";
                });
                if (isFinalNode) {
                    armNodes.push(node);
                } else {
                    concatNodes(node.children, armNodes)
                }
            });
        }
    }
    value = value.filter(function (rootNode) {
        var rootIsOk = rootNode.name != "Работа руководителя";
        if (rootIsOk) {
            var children = rootNode.children;
            var armNodes = [];
            if (children && children.length) {
                //проверка вложенных узлов
                concatNodes(children, armNodes);
                if (armNodes && armNodes.length) {
                    rootIsOk = armNodes.some(function (node) {
                            if (node.children && node.children.length) {
                                return node.children.every(function (child) {
                                    return isTypeOK(child);
                                });
                            } else {
                                return true;
                            }
                        })
                } else {
                    rootIsOk = false;
                }
            }
        }
        return rootIsOk;
    });
}
model.values = value;
