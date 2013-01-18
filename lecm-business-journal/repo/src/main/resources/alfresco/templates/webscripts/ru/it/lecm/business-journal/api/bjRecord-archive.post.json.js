function main() {
    var items = getMultipleInputValues("nodeRefs");
    if (typeof items != "string") {
        for (item in items) {
            nodeRef = items[item];
            result =
            {
                nodeRef: nodeRef,
                action: "archiveRecord",
                success: false
            };

            try {
                result.success = businessJournal.archiveRecord(nodeRef);
            }
            catch (e) {
                result.success = false;
            }

            results.push(result);
        }
        model.results = results;
    }
}

function getMultipleInputValues(param) {
    var values = [],
        error = null;
    try {
        if (typeof json != "undefined") {
            if (!json.isNull(param)) {
                var jsonValues = json.get(param);
                for (var i = 0, j = jsonValues.length(); i < j; i++) {
                    values.push(jsonValues.get(i));
                }
            }
        }
    }
    catch (e) {
        error = e.toString();
    }
    return (error !== null ? error : values);
}

main();