function main() {
    var countersContainer = "null";
    var query = "/app:company_home/cm:Business_x0020_platform/cm:LECM/cm:Сервис_x0020_Регистрационные_x0020_номера";
    var nodes = search.xpathSearch(query);
    if (nodes.length == 1) {
        countersContainer = nodes[0].nodeRef.toString();
    }
    model.countersContainer = countersContainer;
    model.yearCounterType = 'lecm-regnum:doctype-year-counter';
    model.plainCounterType = 'lecm-regnum:doctype-plain-counter';
    model.parentCounterType = 'lecm-regnum:plain-counter';
}

main();
