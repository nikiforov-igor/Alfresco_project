var nodeRef = args["sDocument"];
var maxFontSize = args["maxFontSize"];
var minFontSize = args["minFontSize"];
var maxCount = args["maxCount"];
model.docTags = integration.getDocumentTagsBr5(nodeRef, maxFontSize, minFontSize, maxCount);