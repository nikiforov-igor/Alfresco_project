var errandRef = args["nodeRef"];
var errand = utils.getNodeFromString(errandRef);

var childrenErrands = errands.getChildErrands(errand.nodeRef.toString());
var childrenResolutions = errands.getChildResolutions(errand.nodeRef.toString());

var hasChildOnLifeCycle = false;
if (childrenErrands || childrenResolutions) {
    if (childrenErrands) {
        children = childrenErrands;
    }
    if (childrenResolutions) {
        children = childrenResolutions;
    }
    if (childrenErrands && childrenResolutions) {
        children = childrenErrands.concat(childrenResolutions);
    }
}
if (children && children.length) {
    hasChildOnLifeCycle = children.some(function (child) {
        return (!statemachine.isFinal(child.nodeRef.toString()) && !statemachine.isDraft(child))
    });
}
model.hasChildOnLifeCycle = hasChildOnLifeCycle;