(function () {

    LogicECM.module.Contracts.DataGrid = function (containerId) {
        return LogicECM.module.Contracts.DataGrid.superclass.constructor.call(this, containerId);
    };

    YAHOO.lang.extend(LogicECM.module.Contracts.DataGrid, LogicECM.module.Base.DataGrid);

    YAHOO.lang.augmentObject(LogicECM.module.Contracts.DataGrid.prototype, {}, true);
})();
