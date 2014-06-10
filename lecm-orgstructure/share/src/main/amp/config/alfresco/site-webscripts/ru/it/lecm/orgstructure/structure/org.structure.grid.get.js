function main()
{
    model.fullDelete = args["fullDelete"] != null ? args["fullDelete"] == "true" : false;
    model.bubblingLabel = args["bubblingLabel"];
}

main();
