if (typeof LogicECM == 'undefined' || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Errands = LogicECM.module.Errands || {};

(function () {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Bubbling = YAHOO.Bubbling;

    LogicECM.module.Errands.ErrandsCancellationActivitiTransitionRadiobuttons = function (containerId) {
        return LogicECM.module.Errands.ErrandsCancellationActivitiTransitionRadiobuttons.superclass.constructor.call(this, containerId);
    };

    YAHOO.lang.extend(LogicECM.module.Errands.ErrandsCancellationActivitiTransitionRadiobuttons, Alfresco.ActivitiTransitions);

    YAHOO.lang.augmentObject(LogicECM.module.Errands.ErrandsCancellationActivitiTransitionRadiobuttons.prototype, {

        options: {
            fieldSeparator: "|",
            fieldsByOption: null,
            selectedValue: null,
            formId: "",
            changeValueFireAction: null
        },
        /**
         * Retrieves, creating if necessary, the hidden field used
         * to hold the selected transition.
         *
         * @method _getHiddenField
         * @return The hidden field element
         * @private
         */
        _getHiddenField: function () {
            // create the hidden field (if necessary)
            var hiddenField = Dom.get(this.options.hiddenFieldId);
            if (!hiddenField) {
                hiddenField = document.createElement('input');
                hiddenField.setAttribute('id', this.options.hiddenFieldId);
                hiddenField.setAttribute('type', 'hidden');
                hiddenField.setAttribute('name', this.options.hiddenFieldName);

                Dom.get(this.id).appendChild(hiddenField);
            }
            return hiddenField;
        },
        _generateTransitionButtons: function () {
            var fields = [];
            // create a submit button for each transition
            for (var i = 0, ii = this.options.transitions.length; i < ii; i++) {

                if (this.options.fieldsByOption.length) {
                    fields = this.options.fieldsByOption[i].split(this.options.fieldSeparator);
                }
                this._generateTransitionButton(this.options.transitions[i], fields);

            }
            YAHOO.Bubbling.fire('mandatoryControlValueUpdated', this);
        },
        /**
         * Generates a YUI button for the given transition.
         * Markup:
         * <div class="value-div">
         *     <div class="radiobutton-container">
         *         <input id="id" name="name" type="type" />
         *          <label for="name" />
         *      </div>
         *      <div class="fields-by-radiobutton">
         *          <CONTROL_1 />
         *          <CONTROL_2 />
         *          ...
         *      </div>
         *      </br>
         *      ...
         * </div>
         *
         * @method _generateTransitionButton
         * @param transition {object} An object representing the transition
         * @private
         */
        _generateTransitionButton: function (transition, fields) {
            // create a button and add to the DOM
            var container, valueDiv, button, label, spaceBr, radioButtonContainer, radioButtonControlsContainer, radioButtonId;

            this._getHiddenField();
            radioButtonControlsContainer = document.createElement('div');
            Dom.addClass(radioButtonControlsContainer, "fields-by-radiobutton");
            radioButtonControlsContainer.setAttribute('id', this.id + '-fields-by-radiobutton-container-' + transition.id);

            radioButtonContainer = document.createElement('div');
            radioButtonContainer.className = "radiobutton-container";

            radioButtonId = this.id + '-' + transition.id;
            button = document.createElement('input');
            button.setAttribute('id', radioButtonId);
            button.setAttribute('type', 'radio');
            button.setAttribute('name', this.id + '-radio-group');
            button.setAttribute('class', 'lecm-radio');
            YAHOO.util.Event.addListener(button, 'click', this.onClick, this, true);
            radioButtonContainer.appendChild(button);

            label = document.createElement('label');
            label.setAttribute('for', radioButtonId);
            label.innerHTML = transition.label;
            radioButtonContainer.appendChild(label);

            container = Dom.get(this.id);
            valueDiv = Selector.query('.value-div', container, true);
            valueDiv.appendChild(radioButtonContainer);
            this.appendFieldsByOption(radioButtonControlsContainer, fields);
            valueDiv.appendChild(radioButtonControlsContainer);
            spaceBr = document.createElement('br');
            valueDiv.appendChild(spaceBr);
            if (this.options.selectedValue == transition.id) {
                button.click();
            }
        },
        /**
         * Event handler called when a transition button is clicked.
         *
         * @method onClick
         * @param e {object} DomEvent
         */
        onClick: function (e) {
            var p_obj = e.target;
            // determine what button was pressed by it's id
            var buttonId = p_obj.id;
            var transitionId = buttonId.substring(this.id.length + 1);

            // get the hidden field
            var hiddenField = this._getHiddenField();

            // set the hidden field value
            Dom.setAttribute(hiddenField, 'value', transitionId);

            if (Alfresco.logger.isDebugEnabled())
                Alfresco.logger.debug('Set transitions hidden field to: ' + transitionId);

            // generate the hidden transitions field
            this._generateTransitionsHiddenField();

            if (this.options.changeValueFireAction) {
                Bubbling.fire(this.options.changeValueFireAction, {
                    selectedValue: transitionId,
                    formId: this.options.formId,
                    fieldId: this.options.hiddenFieldId
                });
            }
            YAHOO.Bubbling.fire('mandatoryControlValueUpdated', this);
        },

        appendFieldsByOption: function (div, fields) {
            if (fields.length) {
                for (var i = 0; i < fields.length; i++) {
                    var fieldEl = Dom.get(this.options.formId + "_prop_" + fields[i].replace(":", "_"));
                    if (!fieldEl) {
                        fieldEl = Dom.get(this.options.formId + "_assoc_" + fields[i].replace(":", "_"));
                    }
                    if (fieldEl) {
                        var fieldControl = fieldEl.parentElement.parentElement.parentElement;
                        div.appendChild(fieldControl);
                    }
                }
            }
        },

        onFormValidationError: function(){
            return null;
        }
    }, true);
})();
