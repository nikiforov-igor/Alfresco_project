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
}

function formatDate(date) {
    return date.getFullYear() + '-' + numPad(date.getMonth() + 1) + '-' + numPad(date.getDate());
}

function numPad(num) {
    return (num < 10) ? '0' + num : num;
}

main();