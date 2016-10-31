if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Absence = LogicECM.module.WCalendar.Absence || {};

(function() {

	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event;

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
			var me =this;

			if (!this.options.currentValue) {
				// MNT-2214 fix, check for prevously entered value
				var iso8601DateString = Dom.get(this.currentValueHtmlId).value;
				this.options.currentValue = Alfresco.util.formatDate(iso8601DateString,this._msg("lecm.date-format.default"));
			}
			// calculate current date
			if (this.options.currentValue) {
				theDate = Date.parse(this.options.currentValue);
			}
			else {
				theDate = new Date();
			}

			var page = (theDate.getMonth() + 1) + "/" + theDate.getFullYear();
			var selected = (theDate.getMonth() + 1) + "/" + theDate.getDate() + "/" + theDate.getFullYear();
			var dateEntry = theDate.toString(this._msg("form.control.date-picker.entry.date.format"));
			var timeEntry = theDate.toString(this._msg("form.control.date-picker.entry.time.format"));

			// populate the input fields
			if (this.options.currentValue)
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

			if (this.options.minLimit) {
				this.widgets.calendar.cfg.setProperty("mindate", this.options.minLimit);

			}
			if (this.options.maxLimit) {
				this.widgets.calendar.cfg.setProperty("maxdate", this.options.maxLimit);
			}

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


			// Hide Calendar if we click anywhere in the document other than the calendar
			Event.on(document, "click", function(e) {
				var inputEl = Dom.get(me.id + "-date");
				var iconEl = Dom.get(me.id + "-icon");
				var el = Event.getTarget(e);
				if (me.widgets.calendar) {
					var dialogEl = me.widgets.calendar.oDomContainer;

					if (el && el != dialogEl && !Dom.isAncestor(dialogEl, el) && el != iconEl) {
						me._hidePicker();
					}
				}
			});

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
			if (this.options.currentValue)
			{
			this._handleFieldChange();
			}
    };
})();
