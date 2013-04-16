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

            var statusMenu = new YAHOO.widget.Menu("statusMenu");

            statusMenu.addItems([
                {
                    text: "Добавить статус",
                    onclick: {
                        fn: this.editor._createStatus,
                        scope: this.editor
                    }
                },
                {
                    text: "Добавить финальный статус",
                    onclick: {
                        fn: this.editor._createEndEvent,
                        scope: this.editor
                    }
                }
            ]);

            statusMenu.render("statemachine-status-menu");
            var onClickStatusMenuButton = function(e) {
                statusMenu.moveTo(e.clientX, e.clientY);
                statusMenu.show();
            }
            Alfresco.util.createYUIButton(this, "status-menu-button", onClickStatusMenuButton, {});

            var propertiesMenu = new YAHOO.widget.Menu("propertiesMenu");

            propertiesMenu.addItems([
                {
                    text: "Общие свойства",
                    onclick: {
                        fn: this.editor._editStatemachine,
                        scope: this.editor
                    }
                },
                {
                    text: "Доступ к полям на статусе",
                    onclick: {
                        fn: this.editor.formFieldsOnStatus,
                        scope: this.editor
                    }
                },
                {
                    text: "Доступ к категориям вложений на статусе",
                    onclick: {
                        fn: this.editor.attachmentCategoryPermissions,
                        scope: this.editor
                    }
                }
            ]);

            propertiesMenu.render("statemachine-properties-menu");
            var onClickPropertiesMenuButton = function(e) {
                propertiesMenu.moveTo(e.clientX, e.clientY);
                propertiesMenu.show();
            }
            Alfresco.util.createYUIButton(this, "properties-menu-button", onClickPropertiesMenuButton, {});

            var deployMenu = new YAHOO.widget.Menu("deployMenu");

            deployMenu.addItems([
                {
                    text: "Развернуть машину состояний",
                    onclick: {
                        fn: this.editor._deployStatemachine,
                        scope: this.editor
                    }
                },
                {
                    text: "Восстановить машину состояний по умолчанию",
                    onclick: {
                        fn: this.editor._restoreDefaultStatemachine,
                        scope: this.editor
                    }
                },
                {
                    text: "Восстановить последнюю развернутую машину состояний",
                    onclick: {
                        fn: this.editor._restoreLastDeployedStatemachine,
                        scope: this.editor
                    }
                }
            ]);

            deployMenu.render("statemachine-deploy-menu");
            var onClickDeployMenuButton = function(e) {
                deployMenu.moveTo(e.clientX, e.clientY);
                deployMenu.show();
            }
            Alfresco.util.createYUIButton(this, "deploy-menu-button", onClickDeployMenuButton, {});

            var onButtonClick5 = function (e) {
				this.editor._exportStatemachine();
			};
			this.widgets.exportButton = Alfresco.util.createYUIButton(this, "machine-export", onButtonClick5, {});

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
