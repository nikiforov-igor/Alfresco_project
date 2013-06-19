/**
 * ReportsEditor module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.ReportsEditor
 */
(function () {

    LogicECM.module.ReportsEditor.Menu = function (htmlId) {
        return LogicECM.module.ReportsEditor.Menu.superclass.constructor.call(
            this,
            "LogicECM.module.ReportsEditor.Menu",
            htmlId,
            ["button"]);
    };

    YAHOO.extend(LogicECM.module.ReportsEditor.Menu, Alfresco.component.Base, {
        messages: null,
        editor: null,

        setMessages:function (messages) {
            this.messages = messages;
        },

        setEditor: function(editor) {
            this.editor = editor;
        },

        draw: function(){

        },

        onReady: function () {

        }
    });
})();
