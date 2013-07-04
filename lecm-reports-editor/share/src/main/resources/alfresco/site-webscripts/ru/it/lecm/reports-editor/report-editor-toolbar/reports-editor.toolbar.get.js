function main() {
    model.previousButton = args["previousButton"] && (args["previousButton"] == 'true');
    model.nextButton = args["nextButton"] && (args["nextButton"] == 'true');
    model.previousPage = args["previousPage"];
    model.nextPage = args["nextPage"];
}

main();