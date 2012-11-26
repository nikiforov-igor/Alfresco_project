var node = search.findNode(args["nodeRef"]);

var parent = node.getParent();
if(parent.getTypeShort() == "lecm-orgstr:structure"){
    parent = null;
}
model.unit = parent;