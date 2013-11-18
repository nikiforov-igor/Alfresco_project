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
    LogicECM.module.FormsEditor.Toolbar = function (htmlId) {
        return LogicECM.module.FormsEditor.Toolbar.superclass.constructor.call(this, "LogicECM.module.FormsEditor.Toolbar", htmlId);
    };

    YAHOO.extend(LogicECM.module.FormsEditor.Toolbar, LogicECM.module.Base.Toolbar);

    YAHOO.lang.augmentObject(LogicECM.module.FormsEditor.Toolbar.prototype,
        {
            _initButtons: function () {
                this.toolbarButtons["defaultActive"].newRowButton = Alfresco.util.createYUIButton(this, "newFormButton", this.onNewRow,
                    {
                        value: "create"
                    });
            }
        }, true);
})();