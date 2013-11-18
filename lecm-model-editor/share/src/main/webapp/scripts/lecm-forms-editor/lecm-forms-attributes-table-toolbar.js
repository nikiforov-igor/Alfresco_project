/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.FormsEditor = LogicECM.module.FormsEditor || {};

(function () {
    LogicECM.module.FormsEditor.AttributesTableToolbar = function (htmlId) {
        return LogicECM.module.FormsEditor.AttributesTableToolbar.superclass.constructor.call(this, "LogicECM.module.FormsEditor.AttributesTableToolbar", htmlId);
    };

    YAHOO.extend(LogicECM.module.FormsEditor.AttributesTableToolbar, LogicECM.module.Base.Toolbar);

    YAHOO.lang.augmentObject(LogicECM.module.FormsEditor.AttributesTableToolbar.prototype,
        {
            _initButtons: function () {
                this.toolbarButtons["defaultActive"].newRowButton = Alfresco.util.createYUIButton(this, "newRowButton", this.onNewRow,
                    {
                        value: "create"
                    });
            }
        }, true);
})();