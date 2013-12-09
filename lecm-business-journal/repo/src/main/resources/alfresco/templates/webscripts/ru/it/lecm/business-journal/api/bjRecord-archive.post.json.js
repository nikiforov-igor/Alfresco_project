function main() {
    var items = getMultipleInputValues("nodeRefs");
    var results = [];
    if (typeof items != "string") {
        if (items.length > 0) {
            for (item in items) {
                nodeRef = items[item];
                result =
                {
                    nodeRef: nodeRef,
                    action: "archiveRecord",
                    success: false
                };

                try {
                    result.success = businessJournal.archiveRecord(parseInt(nodeRef));
                }
                catch (e) {
                    result.success = false;
                }

                results.push(result);
            }
        } else {
            var dateArchiveTo = json.has("dateArchiveTo") ? json.get("dateArchiveTo") : null;
            if (dateArchiveTo != null) {
                items = businessJournal.findOldRecords(dateArchiveTo);
                for (item in items) {
                    nodeRef = items[item].getNodeId();
                    result =
                    {
                        nodeRef: nodeRef.toString(),
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
            }
        }
        if (results) {
            if (typeof results == "string") {
                status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, results);
            }
            else if (typeof results.status == "object") {
                status.redirect = true;
                for (var s in results.status) {
                    status[s] = results.status[s];
                }
            }
            else {
                var overallSuccess = true,
                    successCount = 0,
                    failureCount = 0;

                for (var i = 0, j = results.length; i < j; i++) {
                    overallSuccess = overallSuccess && results[i].success;
                    results[i].success ? ++successCount : ++failureCount;
                }
                model.overallSuccess = overallSuccess;
                model.successCount = successCount;
                model.failureCount = failureCount;
                model.results = results;
            }
        }
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