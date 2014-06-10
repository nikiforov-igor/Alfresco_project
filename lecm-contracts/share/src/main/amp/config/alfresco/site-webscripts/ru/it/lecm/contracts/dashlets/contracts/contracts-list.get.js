
function main() {
    var dateFilter = args.dateFilter;
    var userFilter = args.userFilter;

    var docs = remote.connect("alfresco").get("/lecm/contracts/getContractsByFilters?dateFilter=" + dateFilter + "&userFilter=" + userFilter);
    var contractsWithMyActiveTasks = [];
    if (docs.status == 200) {
        var filter = "";
        var oResults = eval("(" + docs + ")");
        for each (var ref in oResults){
            var nodeRef = ref.nodeRef;
            filter = filter + " ID:" + nodeRef.replace(":", "\\\\:");

		    if (ref.hasMyActiveTasks == "true") {
			    contractsWithMyActiveTasks.push(nodeRef);
		    }
        }
        if (filter == "") {
            filter += "ID:NOT_REF";
        }
        model.filter = filter;
    }
    model.contractsWithMyActiveTasks = contractsWithMyActiveTasks;
}

main();