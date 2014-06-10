var nodeRef = args['nodeRef'];

if (!nodeRef) {
    model.msg = "NodeRef parameter is missing during rating setting";
} else {
    model.msg = "";
    model.rating = documentScript.getRating(nodeRef);
    model.ratedPersonsCount = documentScript.getRatedPersonCount(nodeRef);
    model.myRating = documentScript.getMyRating(nodeRef);
}
