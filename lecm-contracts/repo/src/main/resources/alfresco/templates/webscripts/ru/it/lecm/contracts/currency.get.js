var nodeRef = args["nodeRef"];
var node = search.findNode(nodeRef);
var currency;
if (node.assocs["lecm-contract:currency-assoc"]) {
    currency = node.assocs["lecm-contract:currency-assoc"][0].properties["lecm-currency:alphabetic-code"];
} else {
    currency = "";
}
model.currency = currency;