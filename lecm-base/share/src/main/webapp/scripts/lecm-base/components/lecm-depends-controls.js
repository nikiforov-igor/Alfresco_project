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
 * DependsControls component.
 *
 * @namespace LogicECM
 * @class LogicECM.DependsControls
 */
(function()
{
	/**
	 * YUI Library aliases
	 */
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		KeyListener = YAHOO.util.KeyListener;

	LogicECM.DependsControls = function(htmlId)
	{
		LogicECM.DependsControls.superclass.constructor.call(this, "LogicECM.DependsControls", htmlId, ["button", "calendar"]);

		return this;
	};

	YAHOO.extend(LogicECM.DependsControls, Alfresco.component.Base,
		{
			firstControlPicker: null,
            firstControlSelect: null,
            secondControlPicker: null,
            secondControlHidden: null,
            secondControlInput: null,

			options:
			{
                firstControlHtmlId: null,
                secondControlHtmlId: null
			},

			onReady: function DependsControls_onReady()
			{
				if (this.options.firstControlHtmlId != null) {
					this.firstControlSelect = Dom.get(this.options.firstControlHtmlId);
					if (this.firstControlSelect != null && !this.firstControlSelect.disabled) {
						Event.on(this.options.firstControlHtmlId, "change", this.onChangeValue, this, true);
						this.firstControlPicker = Alfresco.util.ComponentManager.get(this.options.firstControlHtmlId);
					}
				}
				if (this.options.secondControlHtmlId != null) {
					this.secondControlInput = Dom.get(this.options.secondControlHtmlId);
					if (this.secondControlInput != null && !this.secondControlInput.disabled) {
                        if (!this.firstControlSelect.value || this.firstControlSelect.value.length <= 0) {
                            this.secondControlInput.setAttribute("disabled", "true");
                        }
					}
				}
			},

			onChangeValue: function() {
				if (this.firstControlSelect != null && this.secondControlInput != null) {
					var currentValue = this.firstControlSelect.value;
                    if (currentValue && currentValue.length > 0) {
                        this.secondControlInput.removeAttribute("disabled");
                    } else {
                        this.secondControlInput.setAttribute("disabled", "true");
                    }
                }
            }
		});
})();