if (typeof LogicECM == "undefined" || !LogicECM) {
   LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Calendar = LogicECM.module.Calendar || {};


(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       Sel = YAHOO.util.Selector,
       $siteURL = Alfresco.util.siteURL,
       fromISO8601 = Alfresco.util.fromISO8601,
       toISO8601 = Alfresco.util.toISO8601,
       dateFormat = Alfresco.thirdparty.dateFormat;

   LogicECM.module.Calendar.View = function (htmlId)
   {
      this.id = htmlId;
      LogicECM.module.Calendar.View.superclass.constructor.call(this, "LogicECM.module.Calendar.View", htmlId, ["calendar", "button", "resize", "datasource", "datatable", "history"]);

      return this;
   };

   YAHOO.extend(LogicECM.module.Calendar.View, Alfresco.component.Base,
       {
          /**
           * Object container for storing YUI widget instances.
           *
           * @property widgets
           * @type object
           */
          widgets: {},

          /**
           * Object container for storing module instances.
           *
           * @property modules
           * @type object
           */
          modules: {},

          /**
           * Object container for storing YUI pop dialog instances.
           *
           * @property popups
           * @type object
           */
          popups: {},

          /**
           * Object container for storing event handlers
           *
           * @property handlers
           * @type object
           */
          handlers: {},

          /**
           * Object container for data
           *
           * @property data
           * @type object
           */
          data: {},

          /**
           * View type - must be overridden by subclasses
           *
           * @property calendarView
           * @type string
           */
          calendarView: '',

          viewDialog: null,

          /**
           * Set multiple initialization options at once.
           *
           * @method setOptions
           * @param obj {object} Object literal specifying a set of options
           * @return {object} returns 'this' for method chaining
           */
          setOptions: function Base_setOptions(obj)
          {
             this.options = YAHOO.lang.merge(this.options, obj);
             if (typeof this.options.startDate == "string")
             {
                this.options.startDate = Alfresco.util.fromISO8601(this.options.startDate);
             }
             if (typeof this.options.endDate == "string")
             {
                this.options.endDate = Alfresco.util.fromISO8601(this.options.endDate);
             }
             if (typeof this.options.titleDate == "string")
             {
                this.options.titleDate = Alfresco.util.fromISO8601(this.options.titleDate);
             }
             return this;
          },

          /**
           * Initialises event handling All events are handled through event
           * delegation via the onInteractionEvent handler
           *
           * @method initEvents
           */
          initEvents: function ()
          {
             Event.on(this.id, 'click', this.onInteractionEvent, this, true);
             Event.on(this.id, 'dblclick', this.onInteractionEvent, this, true);
          },

          /**
           * Retrieves events from server
           *
           * @method getEvents
           *
           */
          getEvents : function ()
          {
             Alfresco.util.Ajax.request(
                 {
                    url: Alfresco.constants.PROXY_URI + "lecm/events/user",
                    dataObj:
                    {
                       from: toISO8601(this.options.startDate).split('T')[0],
                       to: toISO8601(this.options.endDate).split('T')[0],
                       repeating: "all"
                    },
                    //filter out non relevant events for current view
                    successCallback:
                    {
                       fn: this.onEventsLoaded,
                       scope: this
                    },
                    failureMessage: Alfresco.util.message("load.fail", "LogicECM.module.Calendar.View")
                 });
          },

          displayMessage: function (message, name)
          {
             Alfresco.util.PopupManager.displayMessage(
                 {
                    text: Alfresco.util.message(message, name || this.name)
                 });
          },

          /**
           *Returns the Event Data object associated with the element passed in.
           *
           * @param data {object} either the HTML node of the event or the event data
           */

          getEventObj: function (data)
          {
             // If we've got the HTML node, we can get the event object using parseRel
             if (typeof(data.innerHTML) === "string")
             {
                return this.parseRel(data);
             }
             // Otherwise, assume it's the event object, so just send that back.
             else
             {
                return data;
             }
          },

          /**
           * builds up the relationship string to store the event reference in the DOM.
           *
           * @param {Object} data
           */
          getRel: function (data)
          {
             //Just stores the ISO yyyy-mm-dd string and will use href from link to identify data
             return data.from.split("T")[0];
          },

          /**
           *
           * retrieves the event object based on the data in the rel string.
           *
           * @param {HTML element} element with a relationship to an event.
           */
          parseRel: function (element)
          {
             var data = "",
                 date = "",
                 result = false;

             // If the passed in is a datatable container, it won't have a rel, so get the first a tag in it.
             if (Sel.test(element, 'div.yui-dt-liner'))
             {
                element = Dom.getElementsByClassName("summary", "a", element.parentNode.parentNode)[0]
             }

             // check the element has a rel tag supplied.
             if (element.rel !== "" && element.rel !== undefined)
             {
                date = element.rel;
                data = this.widgets.Data[date].events;
                for (var i = 0; i < data.length; i++)
                {
                   //if (data[i].uri === "/calendar/event/" + element.href.split("/calendar/event/")[1]) // element.href needs hostname and port stripping.
                   //{
                   result = data[i];
                   //}
                }
             }
             return result;
          },

          /**
           * Shows/hides the early hours of day (midnight till 7am)
           *
           * @method toggleEarlyTableRows
           *
           */
          toggleEarlyTableRows: function ()
          {

             var triggerEl = YAHOO.util.Dom.get('collapseTrigger');
             this.earlyEls = YAHOO.util.Dom.getElementsByClassName('early', 'tr', triggerEl.parentNode);
             var displayStyle = (YAHOO.env.ua.ie) ? 'block' : 'table-row';
             for (var i = 0; i < this.earlyEls.length; i++)
             {
                var el = this.earlyEls[i];
                YAHOO.util.Dom.setStyle(el, 'display', (this.isShowingEarlyRows) ? 'none' : displayStyle);
             }
             this.isShowingEarlyRows = !this.isShowingEarlyRows;
          },

          /**
           * Handler for event retrieval when events are loaded from the server
           * Used by agenda, day and week (i.e. not month view)
           *
           * @method onEventsLoaded
           */
          onEventsLoaded: function (o)
          {
             var data = YAHOO.lang.JSON.parse(o.serverResponse.responseText).events;
             var events = [];
             var comparisonFn = null;
             var viewStartDate = this.options.startDate;
             var viewEndDate = this.options.endDate;

             // Trigger Mini Calendar's rendering before filtering the events
             YAHOO.Bubbling.fire("eventDataLoad",data);

             // TODO: These take no account of timezone. E.g. day view of 6th June. server time = GMT+1000, event time = 06 JUN 2010 05:00GMT+1000, date returned from server is 05 JUN 2010 19:00GMT+0000. 05 JUN != 06 JUN.
             // TODO: This would be better done on the server in the userevents webscript
             comparisonFn = function()
             {

                return function(eventDate, endDate)
                {
                   // Event can: Start before and finish after display dates
                   var eventSurroundsView = (eventDate <= viewStartDate && viewEndDate <= endDate);
                   // or: start during
                   var startDuring = (eventDate >= viewStartDate && eventDate < viewEndDate);
                   // or: finish during
                   var endDuring = (endDate >= viewStartDate && endDate < viewEndDate);
                   return (eventSurroundsView || startDuring || endDuring);
                };
             }.apply(this);

             for (var i = 0; i < data.length; i++)
             {
                // TODO: Make this format consistent across calendar views and API.
                var ev = data[i];
                var date = fromISO8601(ev.startAt.iso8601);
                var endDate = fromISO8601(ev.endAt.iso8601);
                if (comparisonFn(date, endDate))
                {
                   var datum = {};

                   // Legacy properties (to be factored out or rolled up over time)
                   datum.nodeRef = ev.nodeRef || '';
                   datum.desc = ev.description || '';
                   datum.name = ev.title;
                   datum.where = ev.where;
                   datum.contEl = 'div';
                   datum.from = ev.startAt.iso8601;
                   datum.to = ev.endAt.iso8601;
                   datum.hidden = '';
                   datum.allday = '';
                   datum.isMultiDay = (!LogicECM.module.Calendar.Helper.isSameDay(date, endDate));
                   datum.isAllDay = (ev.allday == "true") ? true : false;
                   datum.el = 'div';

                   datum.key = datum.from.split(":")[0] + ':00';

                   // Merge in standard event properties - allowing legacy values to override standards
                   datum = YAHOO.lang.merge(ev, datum)

                   events.push(datum);
                }
             }

             this.renderEvents(events);
          },

          /**
           * Adds events to view
           *
           * @method add
           * @param {String} id Identifier of event
           * @param {Object} o Event Object
           * @return {Boolean} Status of add operation
           */
          add: function (id, o)
          {
             this.add(id, o);
          },

          /**
           * Removes events from view
           *
           * @method remove
           * @param {String} id Identifier of event
           * @return {Boolean} Status of removal operation
           */
          remove: function (id)
          {
             this.remove(id);
          },

          /**
           * Updates specified event
           *
           * @method update
           *
           * @param {String} id Identifier of event
           * @param {Object} o Event Object
           * @return {Boolean} Status of update operation
           */
          update: function (id, o)
          {
             this.data.update(o);
          },

          /**
           * Filters the array of events for multiday events
           * For each Multiday event, it:
           *    - Creates an event for every day in the period.
           *    - If not All day:
           *       - the first day's display end time is set to: 00:00
           *       - the middle days are marked as multiday
           *       - the last day's start time is: 00:00
           *    - Adds cloned tag.
           *
           * This is only used by the Agenda view
           *
           * @method filterMultiday
           * @param events {Array} Array of event objects
           */
          filterMultiday: function (events)
          {
             var DateMath = YAHOO.widget.DateMath;

             for (var i=0, numEvents=events.length;i<numEvents;i++)
             {
                var event = events[i];
                // check if event is multiday
                if (event.isMultiDay)
                {
                   var from = event.from.split("T"),
                       to = event.to.split("T"),
                       startDay = fromISO8601(from[0]),
                       endDay = fromISO8601(to[0]),
                       iterationDay = new Date(startDay + 86400000);

                   // if not all day event, end time on first day needs to be midnight.
                   if (!event.isAllDay)
                   {
                      event.displayEnd = "00:00";
                   }

                   for (var j=0, iterationDay=DateMath.add(startDay, DateMath.DAY, 1); iterationDay.getTime() <= endDay.getTime(); iterationDay=DateMath.add(iterationDay, DateMath.DAY, 1))
                   {
                      var clonedEvent = YAHOO.lang.merge(event);

                      // Mark as cloned and provide a marker to locate the original
                      clonedEvent.isCloned = true;
                      clonedEvent.clonedFromDate=event.from;

                      // Sort out the display time.
                      if (!event.isAllDay)
                      {
                         // If event is not the last day of the repeating sequence, it lasts all day.
                         if (!LogicECM.module.Calendar.Helper.isSameDay(iterationDay, endDay))
                         {
                            clonedEvent.isAllDay = true;
                         } else
                         {
                            // if it is the same day, we need to set the finish time, by removing the displayEnd time.
                            clonedEvent.displayStart="00:00";
                            delete clonedEvent.displayEnd;
                         }

                      }
                      // set the DisplayDates for the cloned object to the current day of the loop:
                      clonedEvent.displayFrom = toISO8601(iterationDay);
                      events.push(clonedEvent);
                   }
                }
             }

             return events
          },

          /**
           * Gets date from either query string or URL fragment
           * (return it from fragment if both exist)
           *
           * @method getDateFromUrl
           *
           */
          getDateFromUrl: function ()
          {
             var date = Alfresco.util.getQueryStringParameter('date'),
                 hashSplit = window.location.hash.split("date=")
             // Check date is in the Hash and retrieve it if it is.
             if (hashSplit[1])
             {
                date = hashSplit[1].split("&")[0]
             }

             return date;
          },

          /**
           * Displays add dialog
           *
           * @method showAddDialog
           * @param date {Date} Javascript date object containing the start date for the new event.
           *
           */
          showAddDialog: function (date) {
             if (date == null) {
                if (date == null) {
                   date = new Date();
                   date.setHours(0);
                   date.setMinutes(0);
                   date.setSeconds(0);
                   date.setMilliseconds(0);
                }
             }

             var fromDate = new Date(date.getTime()),
                 toDate = new Date(date.getTime());

             if (date.getHours() == 0) {
                fromDate.setHours(12);
                toDate.setHours(13);
             } else {
                toDate.setHours(fromDate.getHours() + 1);
             }

             var params = "documentType=lecm-events:document";
             params += "&prop_lecm-events_from-date=" + Alfresco.util.toISO8601(fromDate);
             params += "&prop_lecm-events_to-date=" + Alfresco.util.toISO8601(toDate);

             window.location.href =
                 Alfresco.constants.URL_PAGECONTEXT + "event-create?documentType=lecm-events:document&" + LogicECM.module.Base.Util.encodeUrlParams(params);
          },

          /**
           * shows edits or add dialog depending on source of event
           *
           * @method showDialog
           * @param e {object} Event object
           * @param elTarget {object} Element in which event occured
           *
           */
          showDialog: function(e, elTarget)
          {
             var event = this.getEventObj(elTarget);
             //window.location = $siteURL("event?nodeRef=" + event.nodeRef);

             var viewFormId = this.id + "-viewForm";

             var obj = {
                htmlid:event.nodeRef.replace("workspace://SpacesStore/","").replace("-",""),
                itemKind:"node",
                itemId:event.nodeRef,
                formId: "popup-form",
                mode:"view"
             };

             var me = this;
             Alfresco.util.Ajax.request(
                 {
                    url:Alfresco.constants.URL_SERVICECONTEXT + "components/form",
                    dataObj: obj,
                    successCallback:{
                       fn:function(response) {
                          Dom.get(viewFormId + "-head").innerHTML = event.typeTitle;

                          var formEl = Dom.get(viewFormId + "-content");
                          formEl.innerHTML = response.serverResponse.responseText;

                          if (me.viewDialog == null) {
                             me.viewDialog = Alfresco.util.createYUIPanel(viewFormId,
                                 {
                                    width: "60em",
                                    modal: false
                                 });
                          }

                          Dom.get(viewFormId + "-action-more").href = $siteURL("event?nodeRef=" + event.nodeRef);
                          Dom.get(viewFormId + "-action-edit").href = $siteURL("event-edit?nodeRef=" + event.nodeRef);

                          me.loadActions(event);
                          Dom.setStyle(viewFormId, "display", "block");
                          me.viewDialog.show();
                       }
                    },
                    failureMessage:"message.failure",
                    execScripts:true
                 });
             return false;
          },

          onCancelDialogClick: function() {
             this.viewDialog.hide();
          },

          loadActions: function loadActions_function(event) {
             var viewFormId = this.id + "-viewForm";
             Dom.setStyle(viewFormId + "-action-accept", "display", "none");
             Dom.setStyle(viewFormId + "-action-reject", "display", "none");
             Dom.setStyle(viewFormId + "-action-delete", "display", "none");
             var me = this;
             var items = [];
             items.push(event.nodeRef);
             Alfresco.util.Ajax.jsonRequest({
                method: "POST",
                url: Alfresco.constants.PROXY_URI + "lecm/groupActions/list",
                dataObj: {
                   items: JSON.stringify(items),
                   group: false
                },
                successCallback: {
                   fn: function (response) {
                      var actions = response.json;
                      var armToolbar = Alfresco.util.ComponentManager.findFirst("LogicECM.module.ARM.DocumentsToolbar");
                      if (armToolbar) {
                         actions.forEach(function (action) {
                            var actionObj = {
                               actionId: action.id,
                               type: action.type,
                               withForm: action.withForm,
                               items: items,
                               workflowId: action.workflowId,
                               label: action.title
                            };

                            if (actionObj.actionId === "Принять приглашение") {
                               YAHOO.util.Event.removeListener(viewFormId + "-action-accept", "click");
                               YAHOO.util.Event.addListener(viewFormId + "-action-accept", "click", function() {
                                  armToolbar.onGroupActionsClick(null, null, actionObj);
                               });
                               Dom.setStyle(viewFormId + "-action-accept", "display", "block");
                            } else if (actionObj.actionId === "Отклонить приглашение") {
                               YAHOO.util.Event.removeListener(viewFormId + "-action-reject", "click");
                               YAHOO.util.Event.addListener(viewFormId + "-action-reject", "click", function() {
                                  armToolbar.onGroupActionsClick(null, null, actionObj);
                               });
                               Dom.setStyle(viewFormId + "-action-reject", "display", "block");
                            } else if (actionObj.label === "Удалить") {
                               YAHOO.util.Event.removeListener(viewFormId + "-action-delete", "click");
                               YAHOO.util.Event.addListener(viewFormId + "-action-delete", "click", function() {
                                  armToolbar.onGroupActionsClick(null, null, actionObj);
                               });
                               Dom.setStyle(viewFormId + "-action-delete", "display", "block");
                            }
                         });
                      }
                   }
                },
                failureMessage:"message.failure",
                execScripts:true
             });
          },

          /**
           * Uses the EventInfo delete method to delete the event after showing a confirmation dialogue.
           *
           * @method deleteDialog
           *
           * @param {Object} e
           * @param {Object} elTarget
           */
          deleteDialog: function(e, elTarget)
          {
             var event = this.getEventObj(elTarget);
             // Set up the dialog box
             this.setUpDialog(e, elTarget, event);

             //call delete function
             this.eventInfoPanel.onDeleteClick();

             Event.preventDefault(e);
          },

          /**
           * Uses the EventInfo edit method to jump straight to the event edit screen.
           *
           * @method editDialog
           *
           * @param {Object} e
           * @param {Object} elTarget
           */
          editDialog: function(e, elTarget)
          {
             var event = this.getEventObj(elTarget);
             // Set up the dialog box
             this.setUpDialog(e, elTarget, event);

             //call edit function
             this.eventInfoPanel.onEditClick();

             Event.preventDefault(e);
          },

          /**
           * Does the grunt work of setting up the dialogue box for info, edit and delete methods.
           *
           * @method setUpDialog
           *
           * @param {Object} e
           * @param {Object} elTarget
           * @param {Object} event
           */
          setUpDialog: function(e, elTarget, event)
          {
             var div = document.createElement('div');

             div.id = 'eventInfoPanel';
             document.body.appendChild(div);
             this.eventInfoPanel = new Alfresco.EventInfo(this.id);
             this.eventInfoPanel.event = event;

             if (!this.eventInfoPanel.isShowing)
             {
                this.eventInfoPanel.setOptions(
                    {
                       siteId: this.options.siteId,
                       eventUri: event.uri.substring(1,event.uri.length), // strip off leading '/'
                       displayDate: this.currentDate,
                       event: event,
                       permitToEditEvents: this.options.permitToCreateEvents
                    });
             }
          },

          /**
           * Tests if event is valid for view must be within startdate and (enddate-1 second) of current view
           *
           * @method isValidDateForView
           * @param date {object} Date to validate
           *
           * @return {Boolean}
           *
           */
          isValidDateForView: function(date)
          {
             return (date.getTime() >= this.options.startDate.getTime()) && (date.getTime() < this.options.endDate.getTime());
          },

          // HANDLERS

          /**
           * Updates date field in dialog when date in selected in popup calendar
           *
           * @method onDateSelected
           * @param e {object} Event object
           * @param args {object} Event argument object
           */
          onDateSelected: function (e, args)
          {
             if (this.currPopUpCalContext)
             {
                // ugly
                for (var i = 1; i < args[0][0].length; i++)
                {
                   args[0][0][i] = LogicECM.module.Calendar.Helper.padZeros(args[0][0][i]);
                }
                Dom.get(this.currPopUpCalContext).value = args[0][0].join('-');
                // add one hour as default
                if (this.currPopUpCalContext === 'dtend')
                {
                   Dom.get(this.currPopUpCalContext + 'time').value = YAHOO.widget.DateMath.add(fromISO8601(Dom.get('dtstart').value + 'T' + Dom.get('dtstarttime').value), YAHOO.widget.DateMath.HOUR, 1).format(dateFormat.masks.isoTime);

                }
             }
          },
          // HANDLERS

          /**
           * Fired by YUI when parent element is available for scripting.
           * Component initialisation, including instantiation of YUI widgets and event listener binding.
           *
           * @method onReady
           */
          onReady: function ()
          {
             this.calendarView = this.options.view;
             this.startDate = (YAHOO.lang.isString(this.options.startDate)) ? fromISO8601(this.options.startDate) : this.options.startDate;
             this.container = Dom.get(this.id);
             this.containerRegion = Dom.getRegion(this.container);
             this.isShowingEarlyRows = true;
             this.titleEl = Dom.get('calTitle');

             // Patch YAHOO.widget.DateMath to support Hours, mins and seconds
             if (!YAHOO.widget.DateMath.HOUR)
             {
                YAHOO.widget.DateMath.add = function()
                {
                   var origAddFunc = YAHOO.widget.DateMath.add;
                   YAHOO.widget.DateMath.HOUR = 'H';
                   YAHOO.widget.DateMath.SECOND = 'S';
                   YAHOO.widget.DateMath.MINUTE = 'Mn';
                   return function(date, field, amount)
                   {

                      switch (field)
                      {
                         case YAHOO.widget.DateMath.MONTH:
                         case YAHOO.widget.DateMath.DAY:
                         case YAHOO.widget.DateMath.YEAR:
                         case YAHOO.widget.DateMath.WEEK:
                            return origAddFunc.apply(YAHOO.widget.DateMath, arguments);
                            break;
                         case YAHOO.widget.DateMath.HOUR:
                            var newHour = date.getHours() + amount;
                            var day = 0;
                            if (newHour < 0)
                            {
                               while (newHour < 0)
                               {
                                  newHour += 24;
                                  day -= 1;

                               }
                               // newHour = 23;
                            }
                            if (newHour > 24)
                            {
                               while (newHour > 24)
                               {
                                  newHour -= 24;
                                  day += 1;

                               }
                            }
                            YAHOO.widget.DateMath._addDays(date, day);
                            date.setHours(newHour);
                            break;
                         case YAHOO.widget.DateMath.MINUTE:
                            date.setMinutes(date.getMinutes() + amount);
                            break;
                         case YAHOO.widget.DateMath.SECOND:
                            date.setMinutes(date.getSeconds() + amount);

                      }
                      return date;
                   };
                }();
             }

             this.render();
          },

          /**
           * Event Delegation handler. Delegates to correct handlers using CSS selectors
           *
           * @method onInteractionEvent
           * @param o{object} DomEvent
           * @param args {array} event arguments
           */
          onInteractionEvent: function (o, args)
          {
             // TODO: refactor this if/else list into an event trigger with listeners.

             var elTarget, e;
             // if loop added for DataTable event trigger which passes event and target as single object in 1st param
             if (typeof(o.event) === "object" && typeof(o.target) === "object")
             {
                e = o.event;
                elTarget = o.target;
             }
             else //old style (non DataTable trigger), event as first object, target not included.
             {
                e = o;
                elTarget = Event.getTarget(e);
             }

             // Check for event type.
             // repeated if loops are now a series of else if loops to prevent all selectors being attempted. Matching of multiple selectors is not recommended.
             if (e.type === 'mouseover')
             {
                if (Sel.test(elTarget, 'div.' + this.dragGroup))
                {
                   Dom.addClass(elTarget, 'highlight');
                   if (this.options.permitToCreateEvents)
                   {
                      if (!Dom.hasClass(elTarget, 'disabled'))
                      {
                         elTarget.appendChild(this.addButton);
                      }
                   }
                }
             }
             else if (e.type === 'mouseout')
             {
                if (Sel.test(elTarget, 'div.' + this.dragGroup))
                {
                   Dom.addClass(elTarget, 'highlight');
                }
             }
             else if (e.type === 'click')
             {
                // Show or hide wee hours?
                if (Sel.test(elTarget, 'a#collapseTriggerLink'))
                {
                   this.toggleEarlyTableRows();
                   Event.preventDefault(e);
                }
                // are we adding a new event?
                else if (Sel.test(elTarget, 'button#addEventButton') || Sel.test(elTarget.offsetParent, 'button#addEventButton') || Sel.test(elTarget, 'a.addEvent'))
                {
                   this.showAddDialog();
                   Event.preventDefault(e);
                }
                // a.summary = a click on the event title. Therefore into Event Info mode.
                else if (Sel.test(elTarget, 'a.summary') || Sel.test(elTarget, 'div.yui-dt-liner') )
                {
                   this.showDialog(e, elTarget);
                }
                // Someone clicked the 'show more events in Month View' link.
                else if (Sel.test(elTarget, 'li.moreEvents a'))
                {
                   this.onShowMore(e, args, elTarget);
                }
                //Agenda View show more
                else if (Sel.test(elTarget, 'a.showMore'))
                {
                   this.expandDescription(elTarget);
                   Event.preventDefault(e);
                }
                else if (Sel.test(elTarget, 'a.showLess'))
                {
                   this.collapseDescription(elTarget);
                   Event.preventDefault(e);
                }
                // Delete this event link in Agenda DataTable
                else if (Sel.test(elTarget, "a.deleteAction"))
                {
                   this.deleteDialog(e, elTarget);
                }
                // Edit event link in Agenda DataTable.
                else if (Sel.test(elTarget, "a.editAction"))
                {
                   this.editDialog(e, elTarget);
                }
             }
          },

          /**
           * Handler for when today button is clicked
           *
           * @method onTodayNav
           *
           */
          onTodayNav: function ()
          {
             var today = new Date();
             var params = Alfresco.util.getQueryStringParameters();
             params.date = today.getFullYear() + '-' + LogicECM.module.Calendar.Helper.padZeros((~ ~ (1 * (today.getMonth()))) + 1) + '-' + LogicECM.module.Calendar.Helper.padZeros(today.getDate());
             window.location = window.location.href.split('?')[0] + Alfresco.util.toQueryString(params);
          },

          /**
           *
           * takes the event list and removes any items that aren't tagged with the currently selected tag.
           *
           * @method tagFilter
           *
           * @param {Object} events
           */
          tagFilter: function (events)
          {
             var filteredEvents = [],
                 tagName = this.options.tag;

             // early exit if there is no selected tagName
             if (!tagName)
             {
                return events;
             } else
             {
                for (var i = 0, l = events.length; i < l; i++)
                {
                   var eventTags = events[i].tags
                   // TODO: Remove this check once we have a consistent event object
                   if (typeof(eventTags) === "string")
                   {
                      eventTags = eventTags.split(",");
                   }
                   if (Alfresco.util.arrayContains(eventTags, tagName))
                   {
                      filteredEvents.push(events[i]);
                   }
                }
                return filteredEvents;
             }
          }
       });
   LogicECM.module.Calendar.View.VIEWTYPE_WEEK = 'week';
   LogicECM.module.Calendar.View.VIEWTYPE_MONTH = 'month';
   LogicECM.module.Calendar.View.VIEWTYPE_DAY = 'day';
   LogicECM.module.Calendar.View.VIEWTYPE_AGENDA = 'agenda';
   LogicECM.module.Calendar.View.VIEWTYPE_SEARCH = 'search';
})();

LogicECM.module.Calendar.Helper = (function ()
{
   var fromISO8601 = Alfresco.util.fromISO8601;

   return {

      /**
       * Pads specified value with zeros if value is less than 10
       *
       * @method padZeros
       *
       * @param value {Object} value to pad
       * @return {String} padded value
       */
      padZeros: function (value)
      {
         return (value < 10) ? '0' + value : value;
      },

      /**
       * Checks to see if the two dates are the same
       *
       * @method isSameDay
       * @param {Date|string} dateOne (either JS Date Object or ISO8601 date string)
       * @param {Date|string} dateTwo
       *
       * @return {Boolean} flag indicating if the dates are the same or not
       */
      isSameDay: function (dateOne, dateTwo)
      {
         if (typeof(dateOne) === "string")
         {
            dateOne = fromISO8601(dateOne);
         }
         if (typeof(dateTwo) === "string")
         {
            dateTwo = fromISO8601(dateTwo);
         }
         return (dateOne.getDate() === dateTwo.getDate() && dateOne.getMonth() === dateTwo.getMonth() && dateOne.getFullYear() === dateTwo.getFullYear());
      },

      /**
       * @method isAllDay
       * @param {Object} eventData event data object
       *
       * @return {Boolean} flag indicating whether event is a timed event or not
       */
      isAllDay: function (eventData)
      {
         var isSameDay = this.isSameDay(eventData.from, eventData.to);
         var isMidnight = (eventData.end == eventData.start && "00:00") ? true : false;
         return (!isSameDay && isMidnight);
      }
   };
})();