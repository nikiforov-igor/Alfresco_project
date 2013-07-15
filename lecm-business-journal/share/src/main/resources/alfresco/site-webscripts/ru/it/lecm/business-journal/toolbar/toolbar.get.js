function main()
{
    model.bubblingLabel = args["bubblingLabel"];
    model.newRowLabel = args["newRowLabel"];

    if (args["showSearchBlock"]) {
        model.showSearchBlock = (args["showSearchBlock"] == 'true');
    }
    if (args["showExSearchBtn"]){
        model.showExSearchBtn = (args["showExSearchBtn"] == 'true');
    }

    if (args["showButtons"]){
        model.showButtons = (args["showButtons"] == 'true');
    }

    var jsonStr= remote.connect("alfresco").get("/lecm/business-journal/api/getArchiverSettings");
    var archDepth = 30;
    if (jsonStr.status == 200) {
        var obj = eval("("+ jsonStr + ")");
        archDepth = obj["archiverDeep"];
    }

    var date = new Date();
    date.setDate(date.getDate() - archDepth);
    model.defaultArchiveToDate = formatDate(date);
}

function formatDate(date) {
    return date.getFullYear() + '-' + numPad(date.getMonth() + 1) + '-' + numPad(date.getDate());
}

function numPad(num) {
    return (num < 10) ? '0' + num : num;
}

main();