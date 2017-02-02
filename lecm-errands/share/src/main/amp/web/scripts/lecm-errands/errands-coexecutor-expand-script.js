(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector,
        Bubbling = YAHOO.Bubbling;
    var formId=null, dataGridId, recordEl, dataGrid, reportsTS, record, reportsTSId;

    Bubbling.on('errandCoexecutorReportExpandScriptLoaded', process);

    function process(layer, args) {
        if(!formId) {
            formId = args[1].formId;
            var formContainer = Dom.get(formId + "-form-container");
            dataGridId = formContainer.offsetParent.offsetParent.parentElement.parentElement.id.replace("-body", "");
            recordEl = formContainer.offsetParent.parentElement.previousElementSibling;
            dataGrid = Alfresco.util.ComponentManager.get(dataGridId).dataGrid;
            record = dataGrid.widgets.dataTable.getRecord(recordEl);
            reportsTSId = formContainer.offsetParent.offsetParent.parentElement.parentElement.parentElement.id.replace("-cntrl", "");
            reportsTS = Alfresco.util.ComponentManager.get(reportsTSId);
            prepareReportActions(reportsTS, dataGrid, record);
        }
    }

    function prepareReportActions(reportsTS, dataGrid, record) {
        var actionBlock = Dom.get(formId + "-coexecutor-report-expand-actions");
        var fieldsBlock = Dom.get(formId + "-coexecutor-report-expand-fields");
        var acceptActionBlock = Dom.get(formId + "-acceptActionBlock");
        var declineActionBlock = Dom.get(formId + "-declineActionBlock");
        var transferActionBlock = Dom.get(formId + "-transferActionBlock");
        var editActionBlock = Dom.get(formId + "-editActionBlock");
        var hiddenActionCount = 0;
        if (!reportsTS.showActionsEvaluator.call(dataGrid, record.getData())) {
            acceptActionBlock.style.display = "none";
            declineActionBlock.style.display = "none";
            hiddenActionCount += 2;
        }
        if (!reportsTS.showTransferActionEvaluator.call(dataGrid, record.getData())) {
            transferActionBlock.style.display = "none";
            hiddenActionCount++;
        }
        if (!reportsTS.editActionEvaluator.call(dataGrid, record.getData())) {
            editActionBlock.style.display = "none";
            hiddenActionCount++;
        }
        if (hiddenActionCount == 4) {
            actionBlock.classList.add("hidden");
            fieldsBlock.classList.add("full-width");
        }
        var padding = (fieldsBlock.offsetHeight - actionBlock.offsetHeight)/ 2;
        Dom.setStyle(actionBlock, "padding-top" , padding + "px");
        Dom.setStyle(actionBlock, "padding-bottom" , padding + "px");

        Event.addListener(acceptActionBlock, 'click', function () {
            dataGrid.onActionAcceptCoexecutorReport.call(dataGrid, record.getData());
            Event.removeListener(acceptActionBlock, 'click');
        });
        Event.addListener(declineActionBlock, 'click', function () {
            dataGrid.onActionDeclineCoexecutorReport.call(dataGrid, record.getData());
            Event.removeListener(declineActionBlock, 'click');
        });
        Event.addListener(transferActionBlock, 'click', function () {
            dataGrid.onActionTransferCoexecutorReport.call(dataGrid, record.getData());
            Event.removeListener(transferActionBlock, 'click');
        });
        Event.addListener(editActionBlock, 'click', function () {
            dataGrid.onActionEdit.call(dataGrid, record.getData());
            Event.removeListener(editActionBlock, 'click');
        });
    }
})();