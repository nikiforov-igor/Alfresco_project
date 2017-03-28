if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};


(function() {
	LogicECM.module.ActivitiTransitionRadiobuttons = function(containerId) {
		return LogicECM.module.ActivitiTransitionRadiobuttons.superclass.constructor.call(this, containerId);
	};

	YAHOO.lang.extend(LogicECM.module.ActivitiTransitionRadiobuttons, Alfresco.ActivitiTransitions);

	YAHOO.lang.augmentObject(LogicECM.module.ActivitiTransitionRadiobuttons.prototype, {
		/**
		 * Retrieves, creating if necessary, the hidden field used
		 * to hold the selected transition.
		 *
		 * @method _getHiddenField
		 * @return The hidden field element
		 * @private
		 */
		_getHiddenField: function() {
			// create the hidden field (if necessary)
			var hiddenField = Dom.get(this.options.hiddenFieldId);
			if (hiddenField === null) {
				hiddenField = document.createElement('input');
				hiddenField.setAttribute('id', this.options.hiddenFieldId);
				hiddenField.setAttribute('type', 'hidden');
				hiddenField.setAttribute('name', this.options.hiddenFieldName);

				Dom.get(this.id).appendChild(hiddenField);
			}

			return hiddenField;
		},
		onFormValidationError: function() {
			return null;
		},
		_generateTransitionButtons: function() {
			// create a submit button for each transition
			for (var i = 0, ii = this.options.transitions.length; i < ii; i++) {
				this._generateTransitionButton(this.options.transitions[i]);
			}
			YAHOO.Bubbling.fire('mandatoryControlValueUpdated', this);
		},
		/**
		 * Generates a YUI button for the given transition.
		 *
		 * @method _generateTransitionButton
		 * @param transition {object} An object representing the transition
		 * @private
		 */
		_generateTransitionButton: function(transition) {
			// create a button and add to the DOM
			var container, button, label, spaceBr;

			this._getHiddenField();

			button = document.createElement('input');
			button.setAttribute('id', this.id + '-' + transition.id);
			button.setAttribute('type', 'radio');
			button.setAttribute('name', this.id + '-radio-group');
			YAHOO.util.Event.addListener(button, 'click', this.onClick, this, true);

			container = Dom.get(this.id);
			container.appendChild(button);

			var label = document.createTextNode(' ' + transition.label);
			container.appendChild(label);

			spaceBr = document.createElement('br');
			container.appendChild(spaceBr);
		},
		/**
		 * Event handler called when a transition button is clicked.
		 *
		 * @method onClick
		 * @param e {object} DomEvent
		 */
		onClick: function(e) {
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

			YAHOO.Bubbling.fire('mandatoryControlValueUpdated', this);
		}
	}, true);

})();
