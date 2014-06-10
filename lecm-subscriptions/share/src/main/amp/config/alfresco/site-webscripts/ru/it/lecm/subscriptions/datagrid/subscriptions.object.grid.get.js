function main()
{
    // get the search sorting fields from the config
    var sortables = config.scoped["Search"]["sorting"].childrenMap["sort"];
    var sortFields = [];
    for (var i = 0, sort, label; i < sortables.size(); i++)
    {
        sort = sortables.get(i);

        // resolve label text
        label = sort.attributes["label"];
        if (label == null)
        {
            label = sort.attributes["labelId"];
            if (label != null)
            {
                label = msg.get(label);
            }
        }

        // create the model object to represent the sort field definition
        sortFields.push(
            {
                type: sort.value,
                label: label ? label : sort.value
            });
    }
    model.sortFields = sortFields;
    var repoconfig = config.scoped['Search']['search'].getChildValue('repository-search');
    // config override can force repository search on/off
    model.searchRepo = (repoconfig != "none");
	model.bubblingLabel = args["bubblingLabel"];
	model.advSearchFormId = args["advSearchFormId"];
}

main();
