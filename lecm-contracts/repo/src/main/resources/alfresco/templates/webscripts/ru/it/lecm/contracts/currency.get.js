var nodeRef = args["nodeRef"];
var node = search.findNode(nodeRef);
var currency = node.assocs["lecm-contract:currency-assoc"][0].properties["lecm-currency:alphabetic-code"];
model.currency = currency;