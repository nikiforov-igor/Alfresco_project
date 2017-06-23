if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};


(function() {
	LogicECM.module.ActivitiTransitionReservationRadiobuttons = function(containerId) {
		return LogicECM.module.ActivitiTransitionReservationRadiobuttons.superclass.constructor.call(this, containerId);
	};

	YAHOO.lang.extend(LogicECM.module.ActivitiTransitionReservationRadiobuttons, Alfresco.ActivitiTransitions);

	YAHOO.lang.augmentObject(LogicECM.module.ActivitiTransitionReservationRadiobuttons.prototype, {

		/**
		 * Fired by YUI when parent element is available for scripting.
		 * Component initialisation, including instantiation of YUI widgets and event listener binding.
		 *
		 * @method onReady
		 */
		onReady: function ActivitiTransitions_onReady()
		{
			// setup the transitions array
			this._processTransitions();

			// generate buttons for each transition
			this._generateTransitionButtons();
		},

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

			if (transition.id == 'REG_DATE') {
				button.setAttribute('checked', true);
				// get the hidden field
				var hiddenField = this._getHiddenField();

				// set the hidden field value
				Dom.setAttribute(hiddenField, 'value', transition.id);

				// generate the hidden transitions field
				this._generateTransitionsHiddenField();

				YAHOO.Bubbling.fire('mandatoryControlValueUpdated', this);
			}

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

			var SHOW = true;
			var HIDE = false;
			if (transitionId == 'NO_REG_DATE') {
				this.setDateVisibility(HIDE);
			}
			else if (transitionId == 'REG_DATE') {
				this.setDateVisibility(SHOW);
			}

			// get the hidden field
			var hiddenField = this._getHiddenField();

			// set the hidden field value
			Dom.setAttribute(hiddenField, 'value', transitionId);

			if (Alfresco.logger.isDebugEnabled())
				Alfresco.logger.debug('Set transitions hidden field to: ' + transitionId);

			// generate the hidden transitions field
			this._generateTransitionsHiddenField();

			YAHOO.Bubbling.fire('mandatoryControlValueUpdated', this);
		},

		setDateVisibility: function(show) {
			var currentElement = Dom.get(this.id);
			var setDiv=currentElement.parentNode.parentNode;
			var elements = setDiv.children;
			var dateInput = Dom.get('workflow-form_prop_lecmRegnumRes_date');
			var dateValue = dateInput.value;
			for (var i=0; i<elements.length; ++i) {
				if (elements[i].id == 'workflow-form_prop_lecmRegnumRes_date-cntrl-parent') {
					if (show) {
						Dom.removeClass(elements[i], 'hidden');
						// set date:
						dateInput.value = dateValue;
					}
					else {
						Dom.addClass(elements[i], 'hidden');
						// clear date:
						dateInput.value = '';
					}
					break;
				}
			}
		}

	}, true);

})();
