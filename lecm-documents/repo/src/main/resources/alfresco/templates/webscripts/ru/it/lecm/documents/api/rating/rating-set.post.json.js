function main() {
    var msg = "";
    model.error = 1;
    if (json.has("rating") == false || json.get("rating").length == 0) {
        msg = "Rating missing when it setting";
        status.setCode(status.STATUS_BAD_REQUEST, msg);
        model.msg = msg;
        return;
    }
    if (json.has("nodeRef") == false || json.get("nodeRef").length == 0) {
        msg = "NodeRef missing when setting rating";
        status.setCode(status.STATUS_BAD_REQUEST, msg);
        model.msg = msg;
        return;
    }

    var rating = json.get("rating");
    var nodeRef = json.get("nodeRef");
    var setted = documentScript.setMyRating(nodeRef, rating);

    if (parseInt(rating) == parseInt(setted)) {
        model.error = 0;
        model.msg = "Rating successfully set";
    } else {
        model.msg = "Some error is occured during rating setting";
    }

}

main();