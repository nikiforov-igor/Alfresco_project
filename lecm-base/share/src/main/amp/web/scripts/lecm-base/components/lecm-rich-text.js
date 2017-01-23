/**
 * Rich text control component.
 *
 * This component renders a TinyMCE editor.
 *
 * @namespace LogicECM
 * @class LogicECM.RichTextControl
 */
(function()
{
	var Dom = YAHOO.util.Dom;
    /**
     * RichTextControl constructor.
     *
     * @param {String} htmlId The HTML id of the parent element
     * @param {String} name The name of the component
     * @return {LogicECM.RichTextControl} The new RichTextControl instance
     * @constructor
     */
    LogicECM.RichTextControl = function(htmlId, name)
    {
        // NOTE: This allows us to have a subclass
        var componentName = (typeof name == "undefined" || name === null) ? "LogicECM.RichTextControl" : name;
		YAHOO.Bubbling.on("readonlyControl", this.onReadonlyControl, this);
        return LogicECM.RichTextControl.superclass.constructor.call(this, componentName, htmlId, ["button"]);
    };

    YAHOO.extend(LogicECM.RichTextControl, Alfresco.component.Base,
        {
            /**
             * Object container for initialization options
             *
             * @property options
             * @type object
             */
            options:
            {
                /**
                 * The current value
                 *
                 * @property currentValue
                 * @type string
                 */
                currentValue: "",

                /**
                 * Flag to determine whether the picker is in disabled mode
                 *
                 * @property disabled
                 * @type boolean
                 * @default false
                 */
                disabled: false,

                /**
                 * Flag to indicate whether the field is mandatory
                 *
                 * @property mandatory
                 * @type boolean
                 * @default false
                 */
                mandatory: false,

                /**
                 * Object to hold the parameters for the editor
                 *
                 * @property editorParameters
                 * @type object
                 */
                editorParameters: null,

				fieldId: null,

				formId: null
            },

			readonly: false,

            /**
             * The editor instance for the control
             *
             * @property editor
             * @type object
             */
            editor: null,

			onReadonlyControl : function (layer, args) {
				var textarea, fn;
				if (!this.options.disabled && this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
					this.readonly = args[1].readonly;
					if (this.editor) {
						// ALFFIVE-101
						// Текущая версия tinyMCE находится в таком состоянии, когда старое API уже частями удалено
						// А нового функционала, отвечающего за динамическое изменение режима readonly еще нет.
						// 
						// Динамическая смена режима реализована на основании коммита 
						// https://github.com/tinymce/tinymce/commit/8d6172d4cd3404a2051365b900d67fa305a82520
						
						var activeEditor = this.editor.getEditor();
						
						this.readonly && activeEditor.selection.controlSelection.hideResizeRect();
						
						activeEditor.getBody().contentEditable = !this.readonly;
						activeEditor.theme.panel.find("*").disabled(this.readonly);
						
						!this.readonly && activeEditor.nodeChanged();
						
						// Необходимо для блокирования обработки shortcut'ов, никак не влияет на отображение
						// но потенциально может быть опасно!
						activeEditor.hidden = this.readonly;
					}
					textarea = Dom.get(this.id);
					if (textarea) {
						fn = args[1].readonly ? textarea.setAttribute : textarea.removeAttribute;
						fn.call(textarea, "readonly", "");
					}
				}
			},

            /**
             * Fired by YUI when parent element is available for scripting.
             * Component initialisation, including instantiation of YUI widgets and event listener binding.
             *
             * @method onReady
             */
            onReady: function RichTextControl_onReady()
            {
                if (Alfresco.logger.isDebugEnabled())
                {
                    Alfresco.logger.debug("Rendering rich text control for element '" + this.id +
                        "', value = '" + this.options.currentValue + "'");
                    Alfresco.logger.debug("Editor parameters for element '" + this.id + "': " +
                        YAHOO.lang.dump(this.options.editorParameters));
                }

                if (!this.options.disabled)
                {
                    // always render the TinyMCE editor for non content properties
                    // that are not disabled
                    this._renderEditor();
                }

            },

            /**
             * Creates and renders the TinyMCE editor
             *
             * @method _renderEditor
             * @private
             */
            _renderEditor: function RichTextControl__renderEditor() {
                // create the editor instance
                this.editor = new Alfresco.util.RichEditor("LECMTinyMCE", this.id, this.options.editorParameters);

                if (!this.options.currentValue || this.options.currentValue.indexOf("mimetype=text/html") !== -1)
                {
                   this.editor.getEditor().settings.forced_root_block = "p";
                }
				this.editor.subscribe("PostRender", function() {
					LogicECM.module.Base.Util.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);
				}, this, true);

                // render and register event handler
                this.editor.render();
                // Make sure we persist the dom content from the editor in to the hidden textarea when appropriate 
                var _this = this;
                this.editor.getEditor().on('BeforeSetContent Change keyup', function(e) {
                   _this._handleContentChange();
                });
            },

            /**
             * Handles the content being changed in the TinyMCE control.
             *
             * @method _handleContentChange
             * @private
             */
            _handleContentChange: function RichTextControl__handleContentChange()
            {
                // save the current contents of the editor to the underlying textarea
                if (this.editor.isDirty())
                {
                    this.editor.save();

                    // inform the forms runtime if this field is mandatory
                    if (this.options.mandatory)
                    {
                        YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
                    }
                }
            }
        });
})();
