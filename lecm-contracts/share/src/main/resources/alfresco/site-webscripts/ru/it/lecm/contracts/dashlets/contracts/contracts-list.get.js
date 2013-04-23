
function main() {
    var dateFilter = args.dateFilter;
    var userFilter = args.userFilter;

    var docs = remote.connect("alfresco").get("/lecm/contracts/getContractsByFilters?dateFilter=" + dateFilter + "&userFilter=" + userFilter);
    if (docs.status == 200) {
        var filter = "";
        var oResults = eval("(" + docs + ")");
        for each (var ref in oResults){
            var ref = ref.nodeRef;
            filter = filter + " ID:" + ref.replace(":", "\\\\:");
        }
        if (filter == "") {
            filter += "ID:NOT_REF";
        }
        model.filter = filter;
    }
}

main();