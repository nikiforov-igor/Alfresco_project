if (typeof LogicECM == 'undefined' || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Review = LogicECM.module.Review || {};
LogicECM.module.Review.ReviewList = LogicECM.module.Review.ReviewList || {};

(function () {

    LogicECM.module.Review.ReviewList.Toolbar = function (containerId, options, messages) {
        LogicECM.module.Review.ReviewList.Toolbar.superclass.constructor.call(this, 'LogicECM.module.Review.ReviewList', containerId);
        this.setOptions(options);
        this.setMessages(messages);
        return this;
    };

    YAHOO.lang.extend(LogicECM.module.Review.ReviewList.Toolbar, LogicECM.module.Base.Toolbar, {
        onNewRow: function () {
            var dataGrid = this.modules.dataGrid;
            if (dataGrid && dataGrid.datagridMeta && dataGrid.datagridMeta.nodeRef.indexOf(":") > 0) {
                dataGrid.showCreateDialog(dataGrid.datagridMeta);
            }
        }
    }, true);
})();
