if (typeof LogicECM == 'undefined' || !LogicECM) {
    LogicECM = {};
}
LogicECM.module = LogicECM.module || {};
LogicECM.module.Contracts = LogicECM.module.Contracts || {};

(function () {

    LogicECM.module.Contracts.contractStagesJsonTable = function (htmlId) {
        return LogicECM.module.Contracts.contractStagesJsonTable.superclass.constructor.call(this, htmlId);
    };

    YAHOO.extend(LogicECM.module.Contracts.contractStagesJsonTable, LogicECM.module.DocumentTableControl, {

        getActionsList: function(){
            var actions = LogicECM.module.Contracts.contractStagesJsonTable.superclass.getActionsList.call(this);
            if (actions && actions.length) {
                actions.forEach(function(action) {
                    switch(action.id) {
                        case "onMoveTableRowUp":
                            action.evaluator = LogicECM.module.EDS.Evaluators.documentDataTableItemUp;
                            break;
                        case "onMoveTableRowDown":
                            action.evaluator = LogicECM.module.EDS.Evaluators.documentDataTableItemDown;
                            break;
                        default:
                            break;
                    }
                })
            }
            return actions;
        }

    });
})();