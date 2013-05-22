function main() {
    var nodeRefStr = args["nodeRef"];
    itemNameSubstituteString = "{cm:name}";
    if (args["nameSubstituteString"]) {
        itemNameSubstituteString = args["nameSubstituteString"];
    }

    model.visibleName = substitude.formatNodeTitle(nodeRefStr, ("" + itemNameSubstituteString));
}
main();
