<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

function main() {
    var data = [];
    var allowedLists = review.getAllowedReviewList();
    var filter = getFilterForAvailableElement(allowedLists);
    data = getPickerChildrenItems(filter);

    model.parent = data.parent;
    model.rootNode = data.rootNode;
    model.results = data.results;
    model.additionalProperties = data.additionalProperties;
}

main();