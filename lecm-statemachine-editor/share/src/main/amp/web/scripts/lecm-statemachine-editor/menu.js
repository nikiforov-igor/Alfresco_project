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

            //Home
            var onClickHomeMenuButton = function(e) {
                document.location.href = Alfresco.constants.URL_CONTEXT + "page/statemachine";
            };
            Alfresco.util.createYUIButton(this, "homeBtn", onClickHomeMenuButton, {});

            var propertiesMenu = new YAHOO.widget.Menu("propertiesMenu");

            propertiesMenu.addItems([
                {
                    text: Alfresco.util.message("btn.common_properties"),
                    onclick: {
                        fn: this.editor._editStatemachine,
                        scope: this.editor
                    }
                },
                {
                    text: Alfresco.util.message("btn.fields_access_permissions"),
                    onclick: {
                        fn: this.editor.formFieldsOnStatus,
                        scope: this.editor
                    }
                },
                {
                    text: Alfresco.util.message("btn.attachments_access_permissions"),
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
            };
            Alfresco.util.createYUIButton(this, "properties-menu-button", onClickPropertiesMenuButton, {});

            //Deploy
            var onClickDeployMenuButton = function(e) {
                this.editor._deployStatemachine();
            };
            Alfresco.util.createYUIButton(this, "deploy-menu-button", onClickDeployMenuButton, {});

            //Versions
            var onClickVersionsMenuButton = function(e) {
                this.editor.showVersions();
            };
            Alfresco.util.createYUIButton(this, "versions-menu-button", onClickVersionsMenuButton, {});

            //New Status
            var onClickNewStatusMenuButton = function(e) {
                this.editor._createStatus();
            };
            Alfresco.util.createYUIButton(this, "new-status-menu-button", onClickNewStatusMenuButton, {});

            //End Status
            var onClickEndEventMenuButton = function(e) {
                this.editor._createEndEvent();
            };
            Alfresco.util.createYUIButton(this, "end-event-menu-button", onClickEndEventMenuButton, {});

            //New AlternativeStart
            var onClickAlternativeStartMenuButton = function(e) {
                this.editor.editAlternativeStarts();
            };
            Alfresco.util.createYUIButton(this, "alternative-start-menu-button", onClickAlternativeStartMenuButton, {});

            //Export
            var onButtonClick5 = function (e) {
				this.editor._exportStatemachine();
			};
			this.widgets.exportButton = Alfresco.util.createYUIButton(this, "machine-export", onButtonClick5, {});

            // Import XML
            var importXmlButton = Alfresco.util.createYUIButton(this, "machine-import", function(){},{});
            var inputId = this.id + "-import-xml-input";

            Event.on(inputId, "change", this.editor._importStatemachine, null, this);

            // Finally show the component body here to prevent UI artifacts on YUI button decoration
            Dom.setStyle(this.id + "-body", "visibility", "visible");

            this.widgets.importButton = importXmlButton;        }
    });
})();
