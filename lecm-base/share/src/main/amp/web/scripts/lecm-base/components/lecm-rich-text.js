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
				var editorControls, prop, textarea, fn;
				if (!this.options.disabled && this.options.formId == args[1].formId && this.options.fieldId == args[1].fieldId) {
					this.readonly = args[1].readonly;
					if (this.editor) {
						this.editor.getEditor().getBody().setAttribute('contenteditable', !args[1].readonly);
						editorControls = this.editor.getEditor().controlManager.controls;
						for (prop in editorControls) {
							if (editorControls.hasOwnProperty(prop)) {
								editorControls[prop].setDisabled(args[1].readonly);
							}
						}
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
                this.editor = new Alfresco.util.RichEditor("tinyMCE", this.id, this.options.editorParameters);

				// Make sure we persist the dom content from the editor in to the hidden textarea when appropriate
				this.editor.subscribe("onChange", this._handleContentChange, this, true);
				this.editor.subscribe("onPostRender", function() {
					var shortcuts, prop;
					shortcuts = this.editor.getEditor().shortcuts;
					for (prop in shortcuts) {
						if (shortcuts.hasOwnProperty(prop)) {
							shortcuts[prop].func = (function (obj, func) {
								return function() {
									!obj.readonly && func();
								};
							})(this, shortcuts[prop].func);
						}
					}
					LogicECM.module.Base.Util.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);
				}, this, true);

                // render and register event handler
                this.editor.render();

            },

            /**
             * Handles the content being changed in the TinyMCE control.
             *
             * @method _handleContentChange
             * @private
             */
            _handleContentChange: function RichTextControl__handleContentChange() {
                this.editor.save();
                // inform the forms runtime if this field is mandatory
                if (this.options.mandatory) {
                    YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
                }
            }
        });
})();
