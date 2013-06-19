/**
 * ReportsEditor module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.ReportsEditor
 */
(function () {

    var Bubbling = YAHOO.Bubbling;

    LogicECM.module.ReportsEditor.Editor = function (htmlId) {
        LogicECM.module.ReportsEditor.Editor.superclass.constructor.call(
            this,
            "LogicECM.module.ReportsEditor.Editor",
            htmlId,
            ["button", "container", "connection"]);
        return this;
    };

    YAHOO.extend(LogicECM.module.ReportsEditor.Editor, Alfresco.component.Base, {
        reportId: null,
        options:{},

        setReportId: function(reportId) {
            this.reportId = reportId;
        },

        draw: function () {

        }
    });

})();