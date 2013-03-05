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

    var Dom = YAHOO.util.Dom;
    var Event = YAHOO.util.Event;
    var UA = YAHOO.util.UserAction;
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

			var onButtonClick4 = function (e) {
				this.editor._createEndEvent();
			};
			this.widgets.staffButton = Alfresco.util.createYUIButton(this, "new-end-event", onButtonClick4, {});

			var onButtonClick2 = function (e) {
				this.editor._editStatemachine();
			};
            this.widgets.staffButton = Alfresco.util.createYUIButton(this, "machine-properties", onButtonClick2, {});

			var onButtonClick3 = function (e) {
				this.editor._deployStatemachine();
			};
			this.widgets.deployButton = Alfresco.util.createYUIButton(this, "machine-deploy", onButtonClick3, {});

			var onButtonClick5 = function (e) {
				this.editor._exportStatemachine();
			};
			this.widgets.exportButton = Alfresco.util.createYUIButton(this, "machine-export", onButtonClick5, {});

            var onButtonClick7 = function (e) {
                this.editor.formFieldsOnStatus();
            };
            this.widgets.fieldsButton = Alfresco.util.createYUIButton(this, "machine-status-fields", onButtonClick7, {});

            // Import XML
            var importXmlButton = Alfresco.util.createYUIButton(this, "machine-import", function(){},{});
            var inputId = this.id + "-import-xml-input";

            Event.on(inputId, "mouseenter", function() {
                UA.mouseover(importXmlButton);
            });
            Event.on(inputId, "mouseleave", function() {
                UA.mouseout(importXmlButton);
            });
            Event.on(inputId, "change", this.editor._importStatemachine, null, this);

            // Finally show the component body here to prevent UI artifacts on YUI button decoration
            Dom.setStyle(this.id + "-body", "visibility", "visible");

            this.widgets.importButton = importXmlButton;        }
    });
})();
