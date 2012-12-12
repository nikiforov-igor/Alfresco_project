/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};


/**
 * LogicECM StatemachineEditor module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.StatemachineEditor.StatemachineEditor
 */
LogicECM.module.StatemachineEditor = LogicECM.module.StatemachineEditor || {};

/**
 * StatemachineEditor module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.StatemachineEditor
 */
(function () {

    var Dom = YAHOO.util.Dom
    var Bubbling = YAHOO.Bubbling;
    LogicECM.module.StatemachineEditor.Menu = function (htmlId) {
        return LogicECM.module.StatemachineEditor.Menu.superclass.constructor.call(
            this,
            "LogicECM.module.StatemachineEditor.Menu",
            htmlId,
            ["button", "container", "connection", "json", "selector"]);
    };

    YAHOO.extend(LogicECM.module.StatemachineEditor.Menu, Alfresco.component.Base, {
        roots:{},
        messages: null,
		editor: null,

        setMessages:function (messages) {
            this.messages = messages;
        },

		setEditor: function(editor) {
			this.editor = editor;
		},

        draw: function draw() {
            function reloadPage(type) {
                var url = window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_PAGECONTEXT;
                window.location.href = url + (type != null && type != '' ? type : "StatemachineEditor");
            }

            // Создание кнопок
            var onButtonClick1 = function (e) {
                this.editor._createStatus();
            };
            this.widgets.employeesButton = Alfresco.util.createYUIButton(this, "new-status", onButtonClick1, {});

            var onButtonClick2 = function (e) {
				this.editor._editStatemachine();
			};
            this.widgets.staffButton = Alfresco.util.createYUIButton(this, "machine-properties", onButtonClick2, {});

			var onButtonClick3 = function (e) {
				this.editor._deployStatemachine();
			};
			this.widgets.deployButton = Alfresco.util.createYUIButton(this, "machine-deploy", onButtonClick3, {});

		}

    });
})();
