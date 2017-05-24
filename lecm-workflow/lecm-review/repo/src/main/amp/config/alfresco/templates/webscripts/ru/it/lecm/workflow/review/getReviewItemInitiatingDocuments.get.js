var reviewItemRef = args['nodeRef'];
var reviewItem = utils.getNodeFromString(reviewItemRef);
var initiatingDocuments = [];
if (reviewItem) {
    initiatingDocuments = reviewItem.sourceAssocs['lecm-review-aspects:related-review-records-assoc'];
}
model.initiatingDocuments = initiatingDocuments;