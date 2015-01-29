(function () {
	var barcodeEnabled = remote.connect('alfresco').get('/lecm/barcode/isBarcodeEnabled');
	var barcodeEnabledObj;

	var barcodeSearchWidget = {
		id: 'SEARCH_BARCODE',
		name: 'logic_ecm/search-barcode/searchBarcode',
		config: {
			id: 'SEARCH_BARCODE',
			label: 'Поиск по ШК'
		}
	};

	if (barcodeEnabled.status != 200) {
		return;
	}

	barcodeEnabledObj = jsonUtils.toObject(barcodeEnabled);

	if (barcodeEnabledObj.isBarcodeEnabled) {
		var menuBar = widgetUtils.findObject(model.jsonModel, 'id', 'HEADER_APP_MENU_BAR');
		if (menuBar) {
			menuBar.config.widgets.push(barcodeSearchWidget);
		}
	}

})();
