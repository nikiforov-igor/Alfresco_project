(function () {
    var searchBox = widgetUtils.findObject(model.jsonModel, 'id', 'HEADER_SEARCH');
    if (searchBox) {
        searchBox.config.linkToFacetedSearch = false; //установка этого флага в false приводит к использованию старой страницы поиска
    }
})();