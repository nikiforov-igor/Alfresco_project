// Get clients json request as a js object
function main()
{
    var success = false;
    var clientRequest = json.toString();
    var clientJSON = eval('(' + clientRequest + ')');

    var childNodeRef = clientJSON.childNodeRef;
    var childNode;
    if (childNodeRef != null) {
        childNode = search.findNode(childNodeRef);
    }
    var parentNodeRef = clientJSON.parentNodeRef;
    var parentNode;
    if (parentNodeRef != null) {
        parentNode = search.findNode(parentNodeRef);
    }
    if (parentNode != null && childNode != null) {
        var newDictionary = parentNode;
        while (newDictionary != null && newDictionary.getTypeShort() != "lecm-dic:dictionary") {
            newDictionary = newDictionary.parent;
        }

        var oldDictionary = childNode;
        while (oldDictionary != null && oldDictionary.getTypeShort() != "lecm-dic:dictionary") {
            oldDictionary = oldDictionary.parent;
        }

        if (newDictionary != null && oldDictionary != null && newDictionary.equals(oldDictionary)) {
            childNode.move(parentNode);
            success = true;
        }
    }
    model.success = success;
}

main();
