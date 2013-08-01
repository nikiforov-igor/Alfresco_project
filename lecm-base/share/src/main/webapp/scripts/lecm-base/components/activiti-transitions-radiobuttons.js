if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};


(function() {
	LogicECM.module.ActivitiTransitionRadiobuttons = function(containerId) {
		return LogicECM.module.ActivitiTransitionRadiobuttons.superclass.constructor.call(this, containerId);
	};

	YAHOO.lang.extend(LogicECM.module.ActivitiTransitionRadiobuttons, Alfresco.ActivitiTransitions);

	YAHOO.lang.augmentObject(LogicECM.module.ActivitiTransitionRadiobuttons.prototype, {
		/**
		 * Generates a YUI button for the given transition.
		 *
		 * @method _generateTransitionButton
		 * @param transition {object} An object representing the transition
		 * @private
		 */
		_generateTransitionButton: function ActivitiTransitionRadiobuttons__generateTransitionButton(transition) {
			// create a button and add to the DOM
			var button = document.createElement('input');
			button.setAttribute("id", this.id + "-" + transition.id);
//			button.setAttribute("value", transition.label);
			button.setAttribute("type", "radio");
			button.setAttribute("name", this.id + "-radio-group");

			var container = Dom.get(this.id + "-buttons");
			container.appendChild(button);
			YAHOO.util.Event.addListener(button, "click", this.onClick, this, true);

			var label = document.createTextNode(" " + transition.label);
			container.appendChild(label);
			
			var spaceBr = document.createElement("br");
			container.appendChild(spaceBr);


			// create the YUI button and register the event handler
//			var button = Alfresco.util.createYUIButton(this, transition.id, this.onClick);

			// register the button as a submitElement with the forms runtime instance
//			YAHOO.Bubbling.fire("addSubmitElement", button);
		},
		/**
		 * Event handler called when a transition button is clicked.
		 *
		 * @method onClick
		 * @param e {object} DomEvent
		 */
		onClick: function ActivitiTransitionRadiobuttons_onClick(e) {
			var p_obj = e.target;
			// determine what button was pressed by it's id
			var buttonId = p_obj.id;
			var transitionId = buttonId.substring(this.id.length + 1);

			// get the hidden field
			var hiddenField = this._getHiddenField();

			// set the hidden field value
			Dom.setAttribute(hiddenField, "value", transitionId);

			if (Alfresco.logger.isDebugEnabled())
				Alfresco.logger.debug("Set transitions hidden field to: " + transitionId);

			// generate the hidden transitions field
			this._generateTransitionsHiddenField();

			YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);

			// attempt to submit the form
//			Alfresco.util.submitForm(p_obj.getForm());
		}
	}, true);

})();