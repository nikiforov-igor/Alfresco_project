function main() {
    model.showCreateBtn = args["showCreateBtn"] && (args["showCreateBtn"] == 'true');
    model.showSearch = args["showSearch"] && (args["showSearch"] == 'true');
    model.showExSearchBtn = args["showExSearchBtn"] && (args["showExSearchBtn"] == 'true');
    model.newRowButtonType = args["newRowButtonType"];
}

main();