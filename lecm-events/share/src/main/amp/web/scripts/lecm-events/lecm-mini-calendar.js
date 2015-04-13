if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};


(function()
{
	/**
	 * YUI Library aliases
	 */
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		Selector = YAHOO.util.Selector,
		Element = YAHOO.util.Element,
		toISO8601 = Alfresco.util.toISO8601,
		fromISO8601 = Alfresco.util.fromISO8601;

	/**
	 * Alfresco Slingshot aliases
	 */
	var $html = Alfresco.util.encodeHTML;

	LogicECM.module.MiniCalendar = function(htmlId) {
		this.name = "LogicECM.module.MiniCalendar";
		this.id = htmlId;

		/* Register this component */
		Alfresco.util.ComponentManager.register(this);

		/* Load YUI Components */
		Alfresco.util.YUILoaderHelper.require(["calendar", "button", "treeview"], this.onComponentsLoaded, this);

		return this;
	};

	LogicECM.module.MiniCalendar.prototype = {
		/**
		 * AddEvent module instance.
		 *
		 * @property eventDialog
		 * @type Alfresco.module.AddEvent
		 */
		eventDialog: null,

		/**
		 * A reference to the YAHOO calendar component.
		 *
		 * @property calendar
		 * @type YAHOO.widget.Calendar
		 */
		calendar: null,

		/**
		 * Set messages for this component
		 *
		 * @method setMessages
		 * @param obj {object} Object literal specifying a set of messages
		 */
		setMessages: function(obj)
		{
			Alfresco.util.addMessages(obj, this.name);
			return this;
		},

		/**
		 * Fired by YUILoaderHelper when required component script files have
		 * been loaded into the browser.
		 *
		 * @method onComponentsLoaded
		 */
		onComponentsLoaded: function()
		{
			Event.onContentReady(this.id, this.init, this, true);
		},

		/**
		 * Fired by YUI when parent element is available for scripting.
		 * Initialises components, including YUI widgets.
		 *
		 * @method init
		 */
		init: function()
		{
			/* Add Event Button */
			Alfresco.util.createYUIButton(this, "thisMonth-button", this.onThisMonthClick);

			// Separate the (initial) rendering of the calendar from the data loading.
			// If for some reason the data fails to load, the calendar will still display.
			this.calendar = new YAHOO.widget.Calendar("calendar", { navigator:true });
			// Set localised properties
			Alfresco.util.calI18nParams(this.calendar);
			this.calendar.render();
			this.calendar.selectEvent.subscribe(this.onDateSelected, this, true);
			this.calendar.changePageEvent.subscribe(this.loadEvents, this, true);

			// Register for changes to the calendar data
			YAHOO.Bubbling.on("eventDataLoad", this.onEventDataLoad, this);
			YAHOO.Bubbling.on("eventSaved", this.onEventSaved, this);

			//load events for this month
			var date = new Date();
			date.setDate(1);
			date.setHours(0,0,0);
			this.loadEvents(null, [null, new Date(date)]);
		},

		/**
		 * Calendar date selected event handler
		 *
		 * @method onDateSelected
		 * @param p_type {string} Event type
		 * @param p_args {array} Event arguments
		 * @param p_obj {object} Object passed back from subscribe method
		 */
		onDateSelected: function (p_type, p_args, p_obj)
		{
			var selected = p_args[0];
			var selDate = this.calendar.toDate(selected[0]);
			YAHOO.Bubbling.fire("dateChanged",
				{
					date: selDate
				})
		},

        loadEvents: function (p_type, p_args, p_obj) {
			var fromDate = p_args[1];
			var toDate = new Date(new Date(fromDate).setMonth(fromDate.getMonth()+1));

            //console.log("loadEvents: from " + fromDate + " to " + toDate);
			if (fromDate != null && toDate != null) {
				Alfresco.util.Ajax.request(
					{
						url: Alfresco.constants.PROXY_URI + "lecm/events/user",
						dataObj:
						{
							from: toISO8601(fromDate).split('T')[0],
							to: toISO8601(toDate).split('T')[0]
						},
						//filter out non relevant events for current view
						successCallback:
						{
							fn: this.onEventsLoaded,
							scope: this
						},
						failureMessage: Alfresco.util.message("load.fail", "LogicECM.module.Calendar.View")
					});
			}
		},

		onEventsLoaded: function (o)
		{
			var data = YAHOO.lang.JSON.parse(o.serverResponse.responseText).events;

			for (var i = 0; i < data.length; i++) {
				var ev = data[i];
				var date = fromISO8601(ev.startAt.iso8601);
				var endDate = fromISO8601(ev.endAt.iso8601);
			 	if (date != null && endDate != null) {
                    var cellIndex = this.calendar.getCellIndex(date);

                    if (cellIndex > -1) {
                        //todo: может есть какой-то свой метод в календаре, чтоб взять ячейку
                        // пока через dom
                        var tds = Selector.query("#" + this.calendar.id + " td");

                        if (tds && tds.length > 0) {
                            Dom.addClass(tds[cellIndex], "with-events");
                        }
                    }
			    }
			}
		},

		/*
		 * This method is called when the "eventSaved" event is fired; this
		 * usually occurs when an event is successfully created. The calendar
		 * updates its view to hightlight the date of the event.
		 *
		 * @method onEventSaved
		 * @param e {object} Event fired
		 * @param args {array} Event parameters (depends on event type)
		 */
		onEventSaved: function(e, args)
		{
			var params = args[1];
			if (params && params.from)
			{
				var from = params.from;
				var selectedDates = this.calendar.getSelectedDates();

				dates.push(Alfresco.util.formatDate(Alfresco.thirdparty.fromISO8601(from), "mm/dd/yyyy"));

				this.calendar.cfg.setProperty("selected", dates.join(","));
				this.calendar.render();

				Alfresco.util.PopupManager.displayMessage(
					{
						text: this._msg("message.add.success", params.name)
					});
			}
		},

		/*
		 * This method is called when the "eventDataLoad" event is fired; this
		 * usually occurs when the page first loads. The calendar data is retrieved
		 * and is used to update the view with the corresponding events.
		 *
		 * @method onEventDataLoad
		 * @param e {object} Event fired
		 * @param args {array} Event parameters (depends on event type)
		 */
		onEventDataLoad: function(e, args)
		{
			var events = args[1];
			if (events)
			{

				var selectedDates = [];

				for (var i=0;i<events.length;i++)
				{
					var event = events[i];
					if (event)
					{
						//var from = event.startAt.iso8601 || event.from || event.dtstart || event.when; todo: сейчас в selected вообще ничего не кладется, только сегодня
						var from = event.from || event.dtstart || event.when;

						selectedDates.push(Alfresco.util.formatDate(Alfresco.thirdparty.fromISO8601(from),"mm/dd/yyyy"));
					}
				}
				// Get the data and refresh the view
				this.calendar.cfg.setProperty("selected", selectedDates.join(','));
				this.calendar.render();
			}
		},

		/**
		 * Fired when the "This Month" button is clicked.
		 *
		 * @method  onThisMonthClick
		 * @param e {object} DomEvent
		 * @param obj {object} Object passed back from addListener method
		 */
		onThisMonthClick: function(e, oValue)
		{
			var today = new Date();
			this.calendar.cfg.setProperty("pagedate", today.getMonth() + 1 + "/" + today.getFullYear());
			this.calendar.render();
			Event.preventDefault(e);
		},


		/**
		 * PRIVATE FUNCTIONS
		 */

		/**
		 * Gets a custom message
		 *
		 * @method _msg
		 * @param messageId {string} The messageId to retrieve
		 * @return {string} The custom message
		 * @private
		 */
		_msg: function (messageId)
		{
			return Alfresco.util.message.call(this, messageId, this.name, Array.prototype.slice.call(arguments).slice(1));
		}
	};
})();
