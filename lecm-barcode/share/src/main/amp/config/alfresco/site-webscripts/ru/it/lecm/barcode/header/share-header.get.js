(function () {
	var barcodeEnabled = remote.connect('alfresco').get('/lecm/barcode/isBarcodeEnabled');
	var barcodeEnabledObj;

	var barcodeSearchWidget = {
		id: 'SEARCH_BARCODE',
		name: 'logic_ecm/search-barcode/searchBarcode',
		config: {
			id: 'SEARCH_BARCODE',
			label: msg.get('lecm.barcode.lbl.search.for.barcode')
		}
	};

	if (barcodeEnabled.status != 200) {
		return;
	}

	barcodeEnabledObj = jsonUtils.toObject(barcodeEnabled);

	if (barcodeEnabledObj.isBarcodeEnabled) {
		var menuBar = widgetUtils.findObject(model.jsonModel, 'id', 'HEADER_APP_MENU_BAR');
		var argWidgets = menuBar.config.widgets;
		if (menuBar) {
			var newWidgets = [];
			for (var i in argWidgets) {
				if (argWidgets[i].id == 'LOGIC_ECM_MORE_MENU_BAR') {
					newWidgets.push(barcodeSearchWidget);
				}
				newWidgets.push(argWidgets[i]);
			}
			menuBar.config.widgets = newWidgets;
		}
	}
	
})();
