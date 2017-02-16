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

		// Register this component
		Alfresco.util.ComponentManager.register(this);

		Alfresco.util.YUILoaderHelper.require(["button", "calendar"], this.onComponentsLoaded, this);

		// Initialise prototype properties
		this.widgets = {};

		return this;
	};

	YAHOO.lang.extend(LogicECM.module.WCalendar.Absence.DatePicker, LogicECM.DatePicker, {
		_getDateByKey: function (key) {
			var date = new Date();
			if (key != null && key != "NOW") {
				if (key == "START_YEAR") {
					date.setMonth(0);
					date.setDate(1);
				} else if (key == "NEXT_MONTH") {
					date.setMonth(date.getMonth() + 1);
				} else if (key == "LAST_MONTH"){
					date.setMonth(date.getMonth() - 1);
				} else if (key == "TOMORROW"){
					date.setDate(date.getDate() + 1);
				}
			}
			return date;
		}
	});

	LogicECM.module.WCalendar.Absence.DatePicker.prototype.options.dateDefault = ""; //NOW, NEXT_MONTH, START_YEAR, LAST_MONTH, NOW, TOMORROW

	LogicECM.module.WCalendar.Absence.DatePicker.prototype.draw = function () {
		if (!this.options.currentValue) {
			// MNT-2214 fix, check for prevously entered value
			var iso8601DateString = Dom.get(this.currentValueHtmlId).value;
			if (iso8601DateString) {
				this.options.currentValue = Alfresco.util.fromISO8601(iso8601DateString);
			} else if (this.options.dateDefault && this.options.dateDefault.length) {
				this.options.currentValue = this._getDateByKey(this.options.dateDefault);
			}
		}
		// Calculate current date
		var theDate = this.options.currentValue ? this.options.currentValue : new Date();

		var page = (theDate.getMonth() + 1) + "/" + theDate.getFullYear();
		var selected = (theDate.getMonth() + 1) + "/" + theDate.getDate() + "/" + theDate.getFullYear();
		
		// Populate the input fields
		if (this.options.currentValue) {
			// show the formatted date
			Dom.get(this.id + "-date").value = theDate.toLocaleDateString();

			if (this.options.showTime) {
				Dom.get(this.id + "-time").value = theDate.toLocaleTimeString();
			}
		}

		// Construct the picker
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

		// Setup events
		this.widgets.calendar.selectEvent.subscribe(this._handlePickerChange, this, true);
		this.widgets.calendar.hideEvent.subscribe(function() {
			// Focus icon after calendar is closed
			Dom.get(this.id + "-icon").focus();
		}, this, true);
		Event.addListener(this.id + "-date", "keyup", this._handleFieldChange, this, true);
		Event.addListener(this.id + "-time", "keyup", this._handleFieldChange, this, true);

		var iconEl = Dom.get(this.id + "-icon");
		if (iconEl) {
			// Setup keyboard enter events on the image instead of the link to get focus outline displayed
			Alfresco.util.useAsButton(iconEl, this._showPicker, null, this);
			Event.addListener(this.id + "-icon", "click", this._showPicker, this, true);
		}


		// Hide Calendar if we click anywhere in the document other than the calendar
		Event.on(document, "click", function(e) {
			var inputEl = Dom.get(this.id + "-date");
			var iconEl = Dom.get(this.id + "-icon");
			var el = Event.getTarget(e);
			if (this.widgets.calendar) {
				var dialogEl = this.widgets.calendar.oDomContainer;

				if (el && el != dialogEl && !Dom.isAncestor(dialogEl, el) && el != iconEl) {
					this._hidePicker();
				}
			}
		}, null, this);

		// Register a validation handler for the date entry field so that the submit
		// button disables when an invalid date is entered
		this.options.validateHandler = this.options.validateHandler || Alfresco.forms.validation.validDateTime;
		YAHOO.Bubbling.fire("registerValidationHandler", {
			fieldId: this.id + "-date",
			handler: this.options.validateHandler,
			when: "propertychange",
			message: this.options.message
		});

		// Render the calendar control
		this.widgets.calendar.render();

		// If value was set in visible fields, make sure they are validated and put in the hidden field as well
		if (this.options.currentValue) {
			this._handleFieldChange();
		}
    };
})();
