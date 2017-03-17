( function()
{

   /**
    * Alfresco Slingshot aliases
    */
   // Uses jQuery and FullCalendar functions for FullCalendar functions and YUI ones for Alfresco data
   var fc = $.fullCalendar,
       parseISO8601 = fc.parseISO8601,
       $html = Alfresco.util.encodeHTML,
       fromISO8601 = Alfresco.util.fromISO8601,
       toISO8601 = Alfresco.util.toISO8601,
       dateFormat = Alfresco.thirdparty.dateFormat,
       $jCalendar = ""; // Cache the jQuery selector. Populated in renderEvents after DOM has rendered.

   LogicECM.module.Calendar.FullView = function (htmlId)
   {
      this.id = htmlId;
      LogicECM.module.Calendar.FullView.superclass.constructor.call(this, htmlId);

      return this;
   };

   YAHOO.extend(LogicECM.module.Calendar.FullView, LogicECM.module.Calendar.View);


   YAHOO.lang.augmentObject(LogicECM.module.Calendar.FullView.prototype,
       {

          PREFERENCE_KEY: "ru.it.lecm.calendar.state.",

          /**
           * Converts Alfresco view type into FullCalendar view type
           *
           * Note: FullCalendar refers to views that include time slots as agenda views, Alfresco terminology for these views is "day" and "week"
           * The FullCalendar agenda views are not to be confused with the Alfresco agenda view which is an event overview page.
           *
           * @method getFullCalendarViewType
           * @param AlfrescoViewType {String} [day|week|month]
           */
          getFullCalendarViewType: function (AlfrescoViewType)
          {
             switch(AlfrescoViewType){
                case LogicECM.module.Calendar.View.VIEWTYPE_DAY:
                   return this.options.fcOpts.dayView
                case LogicECM.module.Calendar.View.VIEWTYPE_WEEK:
                   return this.options.fcOpts.weekView
                case LogicECM.module.Calendar.View.VIEWTYPE_MONTH:
                   return this.options.fcOpts.monthView
             }
          },

          render: function () {
             var hash = window.location.hash;
             var view = hash.substring(hash.indexOf("view=") + 5).split("&")[0] || Alfresco.util.getQueryStringParameter('view') || this.options.view;
             this.onUpdateView(view);

             this.renderEvents();
          },

          renderEvents: function () {
             // Prevent unnecessary DOM lookups and cache the jQuery object for the calendar container.
             $jCalendar = $('#' + this.options.id);

             // invoke Full Calendar
             this.initFullCalendar();

             // Nav events.
             YAHOO.Bubbling.on("todayNav", function ()
             {
                $jCalendar.fullCalendar("today");
             }, this);

             YAHOO.Bubbling.on("nextNav", function ()
             {
                $jCalendar.fullCalendar("next");
             }, this);

             YAHOO.Bubbling.on("prevNav", function ()
             {
                $jCalendar.fullCalendar("prev");
             }, this);

             YAHOO.Bubbling.on("toggleWorkHours", this.onToggleWorkHours, this);

             YAHOO.Bubbling.on("viewChanged", function (e, args)
             {
                // gets the view changed to from the index of the button in the event object of the passed in parameters
                var view = Alfresco.util.ComponentManager.findFirst("LogicECM.module.Calendar.Toolbar").enabledViews[args[1].activeView];
                this.onViewNav(view);
                this.onUpdateView(view);
                $jCalendar.fullCalendar("render");
             }, this);

             // Mini Calendar
             YAHOO.Bubbling.on("dateChanged", function (e, args) {
                this.onDateNav(args[1].date);

                $jCalendar.fullCalendar('select', args[1].date);
             }, this);

             // Override Resizer callback function (while keeping the old one):
             var oldResizerFn = LogicECM.module.Base.Resizer.prototype.onResizeNotification;
             LogicECM.module.Base.Resizer.prototype.onResizeNotification = function () {
                oldResizerFn();
                $jCalendar.fullCalendar("render");
             }
          },

          onUpdateView: function(view) {
             if (view === LogicECM.module.Calendar.View.VIEWTYPE_AGENDA || view === LogicECM.module.Calendar.View.VIEWTYPE_SEARCH) {
                //this.onViewChanged.apply(this, arguments);
                Dom.setStyle(this.id, "display", "none");
             } else {
                Dom.setStyle(this.id, "display", "block");
             }
          },

          /**
           * Invokes the jQuery FullCalendar initialisation and rendering
           *
           * @method initFullCalendar
           *
           */
          initFullCalendar: function ()
          {
             var date = new Date(),
                 view = this.options.view;

             // jQuery resets context so 'this' becomes the HTML element passed in, but we need a handle to the CalendarFullCalendar component:
             var me = this;

             // Standard jQuery wrapper maintained for clarity
             $(document).ready(function()
             {
                if (me.options.permitToCreateEvents)
                {
                   $jCalendar.addClass("calendar-editable");
                }
                // FullCalendar init, inc. inline function declarations.
                $jCalendar.fullCalendar(
                    {
                       // Take view & date from settings:
                       defaultView: me.getFullCalendarViewType(view),
                       date: date.getDate(),
                       month: date.getMonth(),
                       year: date.getFullYear(),

                       // Config:
                       weekMode: me.options.fcOpts.weekMode,
                       weekends: me.options.fcOpts.weekends,
                       allDaySlot: me.options.fcOpts.allDaySlot,
                       firstDay: parseInt(Alfresco.util.message("calendar.widget_config.start_weekday")),
                       firstHour: me.options.fcOpts.firstHour,
                       minTime: (me.options.fcOpts.showWorkHours) ? me.options.fcOpts.minTimeWorkHours : me.options.fcOpts.minTimeToggle,
                       maxTime: (me.options.fcOpts.showWorkHours) ? me.options.fcOpts.maxTimeWorkHours : me.options.fcOpts.maxTimeToggle,
                       aspectRatio: me.options.fcOpts.aspectRatio,
                       slotMinutes: me.options.fcOpts.slotMinutes,
                       disableDragging: true,
                       disableResizing: true,

                       // This is treated as a maximum height for day and week views - if it's set too small, you'll get an internal scrollbar.
                       height: 2000,

                       // Why would you want to ignoreTimezones?
                       ignoreTimezone: false,

                       // Internationalisation:
                       monthNames: me.msg("months.long").split(","),
                       monthNamesShort: me.msg("months.short").split(","),
                       dayNames: me.msg("days.long").split(","),
                       dayNamesShort: me.msg("days.short").split(","),
                       buttonText:
                       {
                          month: me.msg("label.month"),
                          week: me.msg("label.week"),
                          day: me.msg("label.day")
                       },
                       allDayText: me.msg("label.all-day"),
                       timeFormat:
                       {
                          month: me.msg("fullCalendar.timeFormat.month"),
                          week: me.msg("fullCalendar.timeFormat.week"),
                          day: me.msg("fullCalendar.timeFormat.day")
                       },
                       columnFormat:
                       {
                          month: me.msg("fullCalendar.columnFormat.month"),
                          week: me.msg("fullCalendar.columnFormat.week"),
                          day: me.msg("fullCalendar.columnFormat.day")
                       },
                       titleFormat:
                       {
                          month: me.msg("fullCalendar.titleFormat.month"),
                          week: me.msg("fullCalendar.titleFormat.week") + " - {" + me.msg("fullCalendar.titleFormat.week") + "}",
                          day: me.msg("fullCalendar.titleFormat.day")
                       },
                       axisFormat: me.msg("fullCalendar.axisFormat"),

                       // Remove header navigation and just show the title. Nav is handled by toolbar.js to remain consistent across views.
                       header:
                       {
                          left: 'title',
                          center: '',
                          right: ''
                       },

                       // Has the user got permissions to create events?
                       editable: me.options.permitToCreateEvents,

                       // Define the event source as the Alfresco Calendar Event API
                       eventSources:[me.getEventSources()],

                       // Trigger the Event Info Dialogue
                       eventClick: function (calEvent, jsEvent, view)
                       {
                          me.showDialog(jsEvent, calEvent);
                          return false;
                       },

                       viewDisplay: function (view)
                       {
                          // reset height for month view to ensure use of aspect ratio so we get square boxes.
                          if (view.name === me.options.fcOpts.monthView)
                          {
                             $jCalendar.fullCalendar("option", "height", null);
                          }
                          me.updateNonWorkingDays();
                       },

                       dayClick: function (date, allDay, jsEvent, view)
                       {
                          // Only show add dialogue if user has create events permission.
                          if (me.options.permitToCreateEvents)
                          {
                             me.showAddDialog(date);
                          }
                       },

                       loading: function (notLoad) {
                          if (!notLoad) {
                             YAHOO.Bubbling.fire("calendarReady");
                          }
                       },
                       eventRender: function(event, element) {
                           element.attr('title', event.title);
                       }
                    });

                $jCalendar.limitEvents(3);

             });
          },

          getEventSources: function () {
             var me = this;
             var url = Alfresco.constants.PROXY_URI + "lecm/events/user?repeating=all&mode=full&timeZoneOffset=" + encodeURIComponent(new Date().getTimezoneOffset());
             var lastCreatedString = "";
             var lastCreated = this.getLastCreatedDocuments();
             if (lastCreated != null) {
                for (var i = 0; i < lastCreated.length; i++) {
                   if (lastCreatedString.length > 0) {
                      lastCreatedString += ",";
                   }
                   lastCreatedString += lastCreated[i];
                }
             }

             if (lastCreatedString.length > 0) {
                url += "&lastCreated=" + encodeURIComponent(lastCreatedString);
             }

             return {
                url: url,
                startParam: "from",
                startParamFn: function(rangeStart)
                {
                   return toISO8601(rangeStart).split('T')[0];
                },
                endParam: "to",
                endParamFn: function(rangeEnd)
                {
                   return toISO8601(rangeEnd).split('T')[0];
                },
                success: function(data)
                {
                   var parsedEvents = [];
                   if (data.events) {
                      var filteredEvents = me.tagFilter(data.events);

                      // trigger Mini Calendar's rendering:
                      YAHOO.Bubbling.fire("eventDataLoad", filteredEvents);

                      $.each(filteredEvents, function(i, event)
                      {
                         var className = "";
                         if (event.userMemberStatus == "CONFIRMED") {
                            className = "event-accepted";
                         } else if (event.userMemberStatus == "DECLINED") {
                            className = "event-rejected";
                         } else if (event.userMemberStatus == "REQUEST_NEW_TIME") {
                            className = "event-another-time";
                         } else if (event.userIsInitiator) {
                            className = "event-initiator"
                         }

                         // Map Alfresco Event object to FullCalendar Event Object (ensuring that existing properties are still present)
                         // Parse user input strings for XSS
                         parsedEvents.push(YAHOO.lang.augmentObject(
                             {
                                id: $html(event.name),
                                start: parseISO8601(event.startAt.iso8601),
                                end: parseISO8601(event.endAt.iso8601),
                                allDay: (event.allday === "true") ? true : false,
                                location: $html(event.where),
                                uri: "event?nodeRef=" + event.nodeRef,
                                description: $html(event.description),
                                title: event.title,
                                where: $html(event.where),
                                url: Alfresco.constants.URL_CONTEXT + "event?nodeRef=" + event.nodeRef,
                                className: className
                             }, event));
                      });
                   }
                   me.nonWorking = data.nonWorkingDays;
                   me.updateNonWorkingDays();
                   return parsedEvents;
                }
             }
          },

          saveLastView: function saveLastView(view) {
             var date = new Date();
             date.setDate(date.getDate() + 30);
             LogicECM.module.Base.Util.setCookie(this.PREFERENCE_KEY  + encodeURIComponent(LogicECM.currentUser), view, {expires: date});
          },

          updateNonWorkingDays: function() {
             var nonWorkingElements = Dom.getElementsByClassName("non-working", "td", this.id);
             if (nonWorkingElements != null) {
                for (var i = 0; i < nonWorkingElements.length; i++) {
                   Dom.removeClass(nonWorkingElements[i], "non-working");
                }
             }

             var view = $jCalendar.fullCalendar("getView");
             if (this.nonWorking) {
                var startCalendar = view.visStart;
                if (startCalendar != null) {
                   for (i = 0; i < this.nonWorking.length; i++) {
                      var nw = this.nonWorking[i];
                      if (nw != null) {
                         var date = fromISO8601(nw);
                         var dayDiff = (date.getTime() - startCalendar.getTime())/(1000*60*60*24);

                         if (view.name === this.options.fcOpts.monthView) {
                            var className = "fc-day" + dayDiff;
                            var elements = Dom.getElementsByClassName(className, "td");
                            if (elements != null && elements.length > 0) {
                               Dom.addClass(elements[0], "non-working");
                            }
                         } else if (view.name === this.options.fcOpts.weekView) {
                            className = "fc-col" + dayDiff;
                            elements = Dom.getElementsByClassName(className, "th");
                            if (elements != null && elements.length > 0) {
                               Dom.addClass(elements[0], "non-working");
                            }
                         }
                      }
                   }
                }
             }
          },

          /**
           * Retrieves the events and refreshes the calendar
           *
           * @method getEvents
           */
          getEvents: function () {
             $jCalendar.fullCalendar("refetchEvents");
          },

          /**
           * Triggered when the date gets changed
           * this is usually when the mini calendar is clicked on
           *
           * @method onDateNav
           * @param date {Date Object|ISO8601 compatible string}
           */
          onDateNav: function (date)
          {
             if (typeof(date) === "string")
             {
                date =  fromISO8601(date);
             }
             $jCalendar.fullCalendar("gotoDate", date);
          },

          /**
           * Triggered when the view changes
           * this is usually when the toolbar has been clicked on
           *
           * @method onViewNav
           * @param view {string} ["day"|"week"|"month"]
           */
          onViewNav: function (viewPar)
          {
             var view = this.getFullCalendarViewType(viewPar);
             if (view != null) {
                $jCalendar.fullCalendar("changeView", view);
                this.saveLastView(viewPar);
             } else {
                if (viewPar == LogicECM.module.Calendar.View.VIEWTYPE_AGENDA) {
                   this.saveLastView("agenda");
                } else if (viewPar == LogicECM.module.Calendar.View.VIEWTYPE_SEARCH) {
                   this.saveLastView("search");
                }
             }
             this.updateNonWorkingDays();
          },

          /**
           *
           * Toggles the display of Work Hours or not
           *
           * @method onToggleWorkHours
           */
          onToggleWorkHours: function ()
          {
             // Remove the Calendar
             $jCalendar.fullCalendar("destroy");
             // Reverse the state of the showWorkHours option.
             this.options.fcOpts.showWorkHours = (this.options.fcOpts.showWorkHours) ? false : true ;
             // Rerender the Calendar
             this.initFullCalendar();
          }
       },
       true);
})();
