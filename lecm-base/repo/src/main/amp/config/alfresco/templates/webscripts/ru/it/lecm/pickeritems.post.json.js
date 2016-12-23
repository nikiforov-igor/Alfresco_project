<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickeritems.lib.js">

    function main() {
        var allItemsResults = [], accessibleItemsResults = [], additionalProperties;
        var showInaccessible = false;
        if (json.has("showInaccessible")) {
            showInaccessible = json.get("showInaccessible");
        }
        var accessibleItems = getPickerItems();
        if (accessibleItems) {
            additionalProperties = accessibleItems.additionalProperties;
            accessibleItemsResults = accessibleItems.results;
        }
        //если ничего не получили или получили не все
        if (showInaccessible && !accessibleItemsResults || accessibleItemsResults.length != json.get("items").length()) {
            //выполняем с правами администратора
            lecmPermission.setRunAsUserSystem();
            var allItems = getPickerItems();
            allItemsResults = allItems.results;
            additionalProperties = allItems.additionalProperties;
            //маркируем как недоступные те результаты, которых нет в accessibleItemsResults
            allItemsResults.forEach(function (item) {
                item.hasAccess = accessibleItemsResults.indexOf(item) >= 0;
            });
        } else {
            accessibleItemsResults.forEach(function (item) {
                item.hasAccess = true;
            });
            allItemsResults = accessibleItemsResults;
        }
        model.results = allItemsResults;
        model.additionalProperties = additionalProperties;
    }
main();
