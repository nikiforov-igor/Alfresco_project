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
 * DateRange component.
 *
 * @namespace LogicECM
 * @class LogicECM.DateRange
 */
(function()
{
	/**
	 * YUI Library aliases
	 */
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		KeyListener = YAHOO.util.KeyListener;

	/**
	 * Alfresco Slingshot aliases
	 */
	var $html = Alfresco.util.encodeHTML;

	LogicECM.DateRange = function(htmlId)
	{
		LogicECM.DateRange.superclass.constructor.call(this, "LogicECM.DateRange", htmlId, ["button", "calendar"]);

		return this;
	};

	YAHOO.extend(LogicECM.DateRange, Alfresco.component.Base,
		{
			endDateHidden: null,
			endDateInput: null,
			unlimitedCheckbox: null,

			options:
			{
				startDateHtmlId: null,
				endDateHtmlId: null,
				unlimitedHtmlId: null
			},

			onReady: function DateRange_onReady()
			{
				if (this.options.endDateHtmlId != null) {
					this.endDateHidden = Dom.get(this.options.endDateHtmlId);
					this.endDateInput = Dom.get(this.options.endDateHtmlId + "-cntrl-date");
				}
				if (this.options.unlimitedHtmlId != null) {
					this.unlimitedCheckbox = Dom.get(this.options.unlimitedHtmlId + "-entry");
				}
				Event.on(this.options.unlimitedHtmlId + "-entry", "change", this.onChangeUnlimited, this, true);
				this.onChangeUnlimited();
			},

			onChangeUnlimited: function() {
				if (this.unlimitedCheckbox != null) {
					if (this.endDateHidden != null && this.unlimitedCheckbox.checked) {
						this.endDateHidden.value = "";
					}
					if (this.endDateInput != null) {
						if (this.unlimitedCheckbox.checked) {
							this.endDateInput.value = "";
						}
						this.endDateInput.disabled = this.unlimitedCheckbox.checked;
					}
				}
			}
		});
})();