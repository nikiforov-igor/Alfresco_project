/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};

LogicECM.module.SearchQueries = LogicECM.module.SearchQueries || {};


(function () {
    var Dom = YAHOO.util.Dom,
        Bubbling = YAHOO.Bubbling;

    LogicECM.module.SearchQueries.QueryEditorActions = function (htmlId) {
        LogicECM.module.SearchQueries.QueryEditorActions.superclass.constructor.call(this, "LogicECM.module.SearchQueries.QueryEditorActions", htmlId, ["container", "json"]);
        return this;
    };

    YAHOO.extend(LogicECM.module.SearchQueries.QueryEditorActions, Alfresco.component.Base,
        {
            _buttons: [
                {label: 'msg.action.create', action: "onAdd"},
                {label: 'msg.action.search', action: "onSearch"},
                {label: 'msg.action.save', action: "onSave"},
                {label: 'msg.action.close', action: "onClose"}
            ],

            onReady: function () {
                this.initButtons();
            },

            initButtons: function () {
                Dom.setStyle(this.id, 'display', 'block');

                var container = document.getElementById(this.id + '-formContainer');
                var actionsContainer = document.getElementById(this.id + '-formContainer-actions');
                if (actionsContainer) {
                    actionsContainer.innerHTML = '';
                } else {
                    actionsContainer = document.createElement('div');
                    actionsContainer.id = this.id + '-formContainer-actions';
                }

                for (var index in this._buttons) {
                    var button = this._buttons[index];
                    var buttonDiv = document.createElement('div');
                    buttonDiv.className = 'query-button-grey text-cropped';
                    buttonDiv.innerHTML = this.msg(button.label);
                    buttonDiv.onclick = this._fnActionHandler.bind(this, button);
                    actionsContainer.appendChild(buttonDiv);
                }
                container.insertBefore(actionsContainer, container.firstChild);

                document.getElementById(this.id + '-formContainer-actions').disabled = true;
            },

            _fnActionHandler: function (button) {
                if (typeof this[button.action] == "function") {
                    this[button.action].call(this);
                }
                return true;
            },

            onAdd: function () {
                Bubbling.fire("addNewSearchRow");
            },

            onSearch: function () {
                Bubbling.fire("searchByConfig");
            },

            onSave: function () {
                Bubbling.fire("saveSearchConfigForUser");
            },

            onClose: function () {
                Bubbling.fire("deleteAllSearchRows");
            }
        });
})();
