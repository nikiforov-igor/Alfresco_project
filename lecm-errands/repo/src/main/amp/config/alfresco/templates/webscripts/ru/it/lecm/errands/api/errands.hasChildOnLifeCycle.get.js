var errandRef = args["nodeRef"];
var errand = utils.getNodeFromString(errandRef);

var childrenErrands = errands.getChildErrands(errand.nodeRef.toString());
var childrenResolutions = errands.getChildResolutions(errand.nodeRef.toString());

var hasChildOnLifeCycle = false;
var children = [];
if (childrenErrands) {
    children = children.concat(childrenErrands);
}
if (childrenResolutions) {
    children = children.concat(childrenResolutions);
}
if (children && children.length) {
    hasChildOnLifeCycle = children.some(function (child) {
        return (!statemachine.isFinal(child.nodeRef.toString()) && !statemachine.isDraft(child))
    });
}
model.hasChildOnLifeCycle = hasChildOnLifeCycle;