function getTemplateCode(templateArg) {
    var templateCode;
    if (isNodeRef(templateArg)) {
        var templateNode = search.findNode(templateArg);
        templateCode = templateNode.name;
    } else {
        templateCode = templateArg;
    }
    return templateCode;
}

function isNodeRef(value) {
    var regexNodeRef = new RegExp(/^[^\:^ ]+\:\/\/[^\:^ ]+\/[^ ]+$/);
    var result = false;
    try {
        result = regexNodeRef.test(String(value));
    }
    catch (e) {
    }
    return result;
}