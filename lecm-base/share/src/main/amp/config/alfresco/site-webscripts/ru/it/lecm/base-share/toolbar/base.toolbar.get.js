function main()
{
    model.newRowLabel = args["newRowLabel"];

    var showSearchBlock = args["showSearchBlock"];
    var showExSearchBtn = args["showExSearchBtn"];
    var showButtons = args["showButtons"];
	var showImportXmlBtn = args["showImportXmlBtn"];

    if (showSearchBlock) {
        model.showSearchBlock = (showSearchBlock == 'true');
    }
    if (showExSearchBtn){
        model.showExSearchBtn = (showExSearchBtn == 'true');
    }
    if (showButtons){
        model.showButtons = (showButtons == 'true');
    }
	if (showImportXmlBtn){
		model.showImportXmlBtn = (showImportXmlBtn == 'true');
	}

	var toolbar = {
		name : "LogicECM.module.Base.Toolbar",
		initArgs: [ "\"LogicECM.module.Base.Toolbar\"", "\"" + args["htmlid"] + "\"" ],
		options : {
			searchButtonsType : args["searchButtons"] != null ? args["searchButtons"] : "defaultActive",
			newRowButtonType : args["newRowButton"] != null ? args["newRowButton"] : "defaultActive",
			bubblingLabel : args["bubblingLabel"] != null ? args["bubblingLabel"] : "",
			showImportXml : args["showImportXmlBtn"] != null ? args["showImportXmlBtn"] == 'true' : false
		}
	};

	model.widgets = [toolbar];
}

main();