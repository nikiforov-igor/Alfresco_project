if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};


(function() {
	LogicECM.module.ActivitiTransitionReserveRadiobuttons = function(containerId) {
		return LogicECM.module.ActivitiTransitionReserveRadiobuttons.superclass.constructor.call(this, containerId);
	};

	YAHOO.lang.extend(LogicECM.module.ActivitiTransitionReserveRadiobuttons, Alfresco.ActivitiTransitions);

	YAHOO.lang.augmentObject(LogicECM.module.ActivitiTransitionReserveRadiobuttons.prototype, {

		/**
		 * Fired by YUI when parent element is available for scripting.
		 * Component initialisation, including instantiation of YUI widgets and event listener binding.
		 *
		 * @method onReady
		 */
		
		transitionDecision: null,
		
		onReady: function ActivitiTransitions_onReady()
		{
			// setup the transitions array
			this._processTransitions();

			// generate buttons for each transition
			this._generateTransitionButtons();
			
			// изначально скрываем причину отмены резервирования
			var HIDE = false;
			this.setCommentVisibility(HIDE);
			
            YAHOO.Bubbling.fire("registerValidationHandler",
            		{
            			fieldId: this.id.replace('prop_lecmRegnumRes_decision-container','prop_bpm_comment'),
            			handler: this.checkComment,
            			when: "onchange",
            			message: this.msg('resevartion.reject.reason.not.set')
            		});
			
		},

		checkComment: function checkComment(arg) {
			var transitionDecision = '';
			var decisionElement = Dom.get(arg.id.replace('prop_bpm_comment', 'prop_lecmRegnumRes_decision'));
			if (decisionElement != null) {
				transitionDecision = decisionElement.value
			}
			if ((transitionDecision == 'RESERVED') || (arg && arg.value.trim() != '')) {
				return true;
			}
			else {
				return false;
			}
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
			this.transitionDecision = transitionId;

			var SHOW = true;
			var HIDE = false;
			/*
			 * RESERVED - поле причины отмены скрыто
			 * REJECTED - поле причины отмены показано
			 */
			if (transitionId == 'RESERVED') {
				this.setCommentVisibility(HIDE);
			}
			else if (transitionId == 'REJECTED') {
				this.setCommentVisibility(SHOW);
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

		setCommentVisibility: function(show) {
			var currentElement = Dom.get(this.id);
			var setDiv=currentElement.parentNode.parentNode;
			var elements = setDiv.children;
			for (var i=0; i<elements.length; ++i) {
				if (this.hasClass(elements[i], 'textarea')) {
					if (show) {
						Dom.removeClass(elements[i], 'hidden');
					}
					else {
						Dom.addClass(elements[i], 'hidden');
					}
					break;
				}
			}
		},
		
		hasClass: function hasClass(element, cls) {
		    return (' ' + element.className + ' ').indexOf(' ' + cls + ' ') > -1;
		}

	}, true);

})();
