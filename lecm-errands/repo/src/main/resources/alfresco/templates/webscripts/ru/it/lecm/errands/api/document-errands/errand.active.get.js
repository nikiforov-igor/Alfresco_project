function main() {
    var maxRecord = 1000;
    var skipCount = args.skipCount;
    var maxItems = args.maxItems;
    var path = documentScript.getDraftsPath();
    var docPath = documentScript.getDocumentsPath();
    var maxRecord = errands.getActiveErrands([path,docPath],0,maxRecord);
    var records = errands.getActiveErrands([path,docPath],skipCount,maxItems);
    model.records = records;
    model.skipCount = skipCount;
    model.totalItems = records.length;
    model.maxItems = maxItems;
    model.maxRecord = maxRecord.length;
    if (records != null) {
        model.maxDate = getMaxMin(records, "lecm-errands:limitation-date", true).toString();
        model.minDate = getMaxMin(records, "lecm-errands:work-start-date", false).toString();
    }

}
function getMaxMin(records,property,isMax){
    var date = "";
    if (records != null) {
        for (var i = 0; i < records.length; i++) {
            var dateProp = records[i].properties[property];
            if (dateProp != null) {

                if (date==""){
                    date = new Date(dateProp);
                }

                if (isMax == true) {
                    if (new Date(dateProp) > date) {
                        date = new Date(dateProp);
                    }
                } else {
                    if (new Date(dateProp) < date) {
                        date = new Date(dateProp);
                    }
                }
            }
        }
    }
    return date;
}

main();