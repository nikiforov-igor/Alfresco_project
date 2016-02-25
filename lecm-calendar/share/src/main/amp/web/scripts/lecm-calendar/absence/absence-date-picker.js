if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Absence = LogicECM.module.WCalendar.Absence || {};

(function() {

	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		Bubbling = YAHOO.Bubbling;

	LogicECM.module.WCalendar.Absence.DatePicker = function(htmlId, currentValueHtmlId) {
		// Mandatory properties

		this.name = "LogicECM.module.WCalendar.Absence.DatePicker";
		this.id = htmlId;
		this.currentValueHtmlId = currentValueHtmlId;

		/* Register this component */
		Alfresco.util.ComponentManager.register(this);

		Alfresco.util.YUILoaderHelper.require(["button", "calendar"], this.onComponentsLoaded, this);

		// Initialise prototype properties
		this.widgets = {};

		return this;
	};

	YAHOO.lang.extend(LogicECM.module.WCalendar.Absence.DatePicker, LogicECM.DatePicker);

	LogicECM.module.WCalendar.Absence.DatePicker.prototype.draw = function () {
			var theDate = null;

			if (this.options.currentValue == null || this.options.currentValue === "")
			{
				// MNT-2214 fix, check for prevously entered value
				this.options.currentValue = Dom.get(this.currentValueHtmlId).value;
			}

			// calculate current date
			if (this.options.currentValue !== null && this.options.currentValue !== "")
			{
				theDate = Alfresco.util.fromISO8601(this.options.currentValue);
			}
			else
			{
				theDate = new Date();
			}

			var page = (theDate.getMonth() + 1) + "/" + theDate.getFullYear();
			var selected = (theDate.getMonth() + 1) + "/" + theDate.getDate() + "/" + theDate.getFullYear();
			var dateEntry = theDate.toString(this._msg("form.control.date-picker.entry.date.format"));
			var timeEntry = theDate.toString(this._msg("form.control.date-picker.entry.time.format"));

			// populate the input fields
			if (this.options.currentValue !== "")
			{
				// show the formatted date
				Dom.get(this.id + "-date").value = dateEntry;

				if (this.options.showTime)
				{
					Dom.get(this.id + "-time").value = timeEntry;
				}
			}

			// construct the picker
			this.widgets.calendar = new YAHOO.widget.Calendar(this.id, this.id, {
				title:this._msg("form.control.date-picker.choose"),
				close:true,
				navigator: {
					strings: {
						month: this._msg("lable.calendar-month-label"),
						year: this._msg("lable.calendar-year-label"),
						submit: this._msg("lable.calendar-ok-label"),
						cancel: this._msg("lable.calendar-cancel-label"),
						invalidYear: this._msg("lable.calendar-wrongyear-label")
					}
				}
			});
			this.widgets.calendar.cfg.setProperty("pagedate", page);
			this.widgets.calendar.cfg.setProperty("selected", selected);
			Alfresco.util.calI18nParams(this.widgets.calendar);

			// setup events
			this.widgets.calendar.selectEvent.subscribe(this._handlePickerChange, this, true);
			this.widgets.calendar.hideEvent.subscribe(function()
			{
				// Focus icon after calendar is closed
				Dom.get(this.id + "-icon").focus();
			}, this, true);
			Event.addListener(this.id + "-date", "keyup", this._handleFieldChange, this, true);
			Event.addListener(this.id + "-time", "keyup", this._handleFieldChange, this, true);

			var iconEl = Dom.get(this.id + "-icon");
			if (iconEl)
			{
				// setup keyboard enter events on the image instead of the link to get focus outline displayed
				Alfresco.util.useAsButton(iconEl, this._showPicker, null, this);
				Event.addListener(this.id + "-icon", "click", this._showPicker, this, true);
			}


			// register a validation handler for the date entry field so that the submit
			// button disables when an invalid date is entered
			this.options.validateHandler = this.options.validateHandler || Alfresco.forms.validation.validDateTime;
			YAHOO.Bubbling.fire("registerValidationHandler",
			{
				fieldId: this.id + "-date",
				handler: this.options.validateHandler,
				when: "propertychange",
				message: this.options.message
			});

			// render the calendar control
			this.widgets.calendar.render();

			// If value was set in visible fields, make sure they are validated and put in the hidden field as well
			if (this.options.currentValue !== "")
			{
			this._handleFieldChange(null);
			}
    };

    LogicECM.module.WCalendar.Absence.DatePicker.prototype._handlePickerChange = function DatePicker__handlePickerChange(type, args, obj) {

        var me = this;
        var selected = args[0];
        var selDate = new Date(Date.UTC(selected[0][0], selected[0][1] - 1, selected[0][2]));
        var dateEntry = selDate.toString(me._msg("lecm.form.control.date-picker.entry.date.format"));
        Dom.get(me.id + "-date").value = dateEntry;

        if (selDate != null) {
            Dom.removeClass(me.id + "-date", "invalid");
            if (me.options.showTime) {
                Dom.removeClass(me.id + "-time", "invalid");
            }
            var isoValue = Alfresco.util.toISO8601(selDate, {"milliseconds": true});
            Dom.get(me.currentValueHtmlId).value = isoValue;

            if (Alfresco.logger.isDebugEnabled())
                Alfresco.logger.debug("Hidden field '" + me.currentValueHtmlId + "' updated to '" + isoValue + "'");

            Bubbling.fire("mandatoryControlValueUpdated", me);

            if (me.options.changeFireAction != null) {
                Bubbling.fire(me.options.changeFireAction, {
                    date: isoValue
                });
            }
        }
        else {
            Dom.addClass(me.id + "-date", "invalid");

            if (me.options.showTime) {
                Dom.addClass(me.id + "-time", "invalid");
            }
        }

		this._hidePicker();
    }
})();
