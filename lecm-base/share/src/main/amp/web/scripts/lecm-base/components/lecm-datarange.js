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
			startDatePicker: null,
			startDateHidden: null,
			startDateInput: null,
			endDatePicker: null,
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
				if (this.options.startDateHtmlId != null) {
					this.startDateHidden = Dom.get(this.options.startDateHtmlId);
					this.startDateInput = Dom.get(this.options.startDateHtmlId + "-cntrl-date");
					if (this.startDateInput != null && !this.startDateInput.disabled) {
						Event.on(this.options.startDateHtmlId + "-cntrl-date", "change", this.onChangeDates, this, true);
						this.startDatePicker = Alfresco.util.ComponentManager.get(this.options.startDateHtmlId + "-cntrl");
						if (this.startDatePicker != null && this.startDatePicker.widgets != null) {
							this.startDatePicker.widgets.calendar.selectEvent.subscribe(this.onChangeDates, this, true);
						}
					}
				}
				if (this.options.endDateHtmlId != null) {
					this.endDateHidden = Dom.get(this.options.endDateHtmlId);
					this.endDateInput = Dom.get(this.options.endDateHtmlId + "-cntrl-date");
					if (this.endDateInput != null && !this.endDateInput.disabled) {
						Event.on(this.options.endDateHtmlId + "-cntrl-date", "change", this.onChangeDates, this, true);
						this.endDatePicker = Alfresco.util.ComponentManager.get(this.options.endDateHtmlId + "-cntrl");
						if (this.endDatePicker != null && this.endDatePicker.widgets != null) {
							this.endDatePicker.widgets.calendar.selectEvent.subscribe(this.onChangeDates, this, true);
						}
					}
				}
				if (this.options.unlimitedHtmlId != null) {
					this.unlimitedCheckbox = Dom.get(this.options.unlimitedHtmlId + "-entry");
					if (this.unlimitedCheckbox != null && !this.unlimitedCheckbox.disabled && this.endDateInput != null && !this.endDateInput.disabled) {
						Event.on(this.options.unlimitedHtmlId + "-entry", "change", this.onChangeUnlimited, this, true);
						this.onChangeUnlimited();
					}
				}
			},

			onChangeUnlimited: function() {
				if (this.endDateHidden != null && this.unlimitedCheckbox.checked) {
					this.endDateHidden.value = "";
					this.startDatePicker._handleFieldChange();
					Dom.removeClass(this.endDatePicker.id + "-date", "invalid");
					YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this.endDatePicker);
				}
				if (this.endDateInput != null) {
					if (this.unlimitedCheckbox.checked) {
						this.endDateInput.value = "";
					}
					this.endDateInput.disabled = this.unlimitedCheckbox.checked;
				}
				Dom.setStyle(this.options.endDateHtmlId + "-cntrl-icon", "visibility", this.unlimitedCheckbox.checked ? "hidden" : "visible");
			},

			onChangeDates: function() {
				if (this.startDatePicker != null && this.endDatePicker != null) {
					var startDate = Date.parseExact(this.startDateInput.value, this.msg("form.control.date-picker.entry.date.format"));
					var endDate = Date.parseExact(this.endDateInput.value, this.msg("form.control.date-picker.entry.date.format"));
					if (startDate != null && endDate != null) {
						if (startDate > endDate) {
							Dom.addClass(this.startDatePicker.id + "-date", "invalid");
							Dom.addClass(this.endDatePicker.id + "-date", "invalid");
							YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this.startDatePicker);
							YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this.endDatePicker);
						} else {
							Dom.removeClass(this.startDatePicker.id + "-date", "invalid");
							Dom.removeClass(this.endDatePicker.id + "-date", "invalid");
							YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this.startDatePicker);
							YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this.endDatePicker);
						}
					}
				}
			}
		});
})();