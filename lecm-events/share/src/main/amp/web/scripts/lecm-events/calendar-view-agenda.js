if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Calendar = LogicECM.module.Calendar || {};

(function()
{
	/**
	 * Alfresco Slingshot aliases
	 */
	var $html = Alfresco.util.encodeHTML,
		Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		Selector = YAHOO.util.Selector,
		fromISO8601 = Alfresco.util.fromISO8601,
		$siteURL = Alfresco.util.siteURL,
		toISO8601 = Alfresco.util.toISO8601,
		formatDate = Alfresco.util.formatDate,
		dateFormat = Alfresco.thirdparty.dateFormat,
		DateMath = YAHOO.widget.DateMath,
		collapsedClass = "collapsed",
		expandedClass = "expanded";

	LogicECM.module.Calendar.AgendaView = function (htmlId)
	{
		this.id = htmlId;
		LogicECM.module.Calendar.FullView.superclass.constructor.call(this, htmlId);

		return this;
	};

	YAHOO.extend(LogicECM.module.Calendar.AgendaView, LogicECM.module.Calendar.View, {});

	YAHOO.lang.augmentObject(LogicECM.module.Calendar.AgendaView.prototype, {

		initStartDate: null,
		initEndDate: null,

		render: function () {
			this.initEvents();

			this.initStartDate = this.options.startDate;
			this.initEndDate = this.options.endDate;

			this.getEvents(dateFormat(this.options.startDate, 'yyyy-mm-dd'));

			var hash = window.location.hash;
			var view = hash.substring(hash.indexOf("view=") + 5).split("&")[0] || Alfresco.util.getQueryStringParameter('view') || this.options.view;
			this.onUpdateView(view);

			YAHOO.Bubbling.on("viewChanged", function (e, args) {
				var view = Alfresco.util.ComponentManager.findFirst("LogicECM.module.Calendar.Toolbar").enabledViews[args[1].activeView];
				this.onUpdateView(view);
			}, this);
		},

		onUpdateView: function(view) {
			if (view === LogicECM.module.Calendar.View.VIEWTYPE_AGENDA) {
				Dom.setStyle(this.id, "display", "block");
			} else {
				Dom.setStyle(this.id, "display", "none");
			}
		},

		/**
		 * INIT
		 */

		/**
		 * Triggered after events have loaded - bind necessary Agenda specific events.
		 */
		initAgendaEvents: function () {
			YAHOO.Bubbling.on("nextAgendaNav", function (e, args) {
				this.onLoadEvents(false);
			}, this);
			YAHOO.Bubbling.on("prevAgendaNav", function (e, args) {
				this.onLoadEvents(true);
			}, this);
			YAHOO.Bubbling.on("todayAgendaNav", function (e, args) {
				this.options.startDate = this.initStartDate;
				this.options.endDate = this.initEndDate;
				this.getEvents();
			}, this);
			Event.addListener(this.id + "_expand_all", "click", this.bind(this.expandAllEvents));
			Event.addListener(this.id + "_collapse_all", "click", this.bind(this.collapseAllEvents));
		},

		collapseAllEvents: function() {
			var events = Dom.getElementsByClassName("event-name " + expandedClass, "div", this.options.id);

			if (events && events.length > 0) {
				Dom.replaceClass(events, expandedClass, collapsedClass);
			}
		},

		expandAllEvents: function() {
			var events = Dom.getElementsByClassName("event-name " + collapsedClass, "div", this.options.id);

			if (events && events.length > 0) {
				Dom.replaceClass(events, collapsedClass, expandedClass);
			}
		},

		/**
		 *  CELL RENDERERS
		 */

		/**
		 * Called by the DataTable to render the 'start' cell, which contains the event time and icon..
		 *
		 * @method renderCellStart
		 * @param elCell {object}
		 * @param oRecord {object}
		 * @param oColumn {object}
		 * @param oData {object|string}
		 *
		 */
		renderCellStart : function (elCell, oRecord, oColumn, oData)
		{
			var data = oRecord.getData(),
				html = "",
				start = formatDate(data.startAt.iso8601, this.msg("date-format.shortTime")),
				end = formatDate(data.endAt.iso8601, this.msg("date-format.shortTime"));

			// build up cell content
			if (data.isAllDay)
			{
				html = this.msg("label.all-day")
			} else
			{
				var startDate = new Date(data.startAt.iso8601.split('T')[0]),
					endDate = new Date(data.endAt.iso8601.split('T')[0]),
					displayDate = new Date(data.renderDate);
				if (LogicECM.module.Calendar.Helper.isSameDay(startDate, displayDate) && (startDate < endDate))
				{
					endDate.setHours(23,59,59,999);
					end = formatDate(endDate.toISOString(), this.msg("date-format.shortTime"));
				} else
				if (LogicECM.module.Calendar.Helper.isSameDay(endDate, displayDate) && (startDate < endDate))
				{
					startDate.setHours(0,0,1,1);
					start = formatDate(startDate.toISOString(), this.msg("date-format.shortTime"));
				}
				html = start + " - " + end
			}
			// write to DOM
			elCell.innerHTML = html;
		},

		/**
		 * Called by the DataTable to render the 'start' cell, which contains the event name (what) and link to more info/edit box.
		 *
		 * @method renderCellName
		 * @param elCell {object}
		 * @param oRecord {object}
		 * @param oColumn {object}
		 * @param oData {object|string}
		 *
		 */
		renderCellName : function (elCell, oRecord, oColumn, oData)
		{
			var data = oRecord.getData(),
				rel = this.getRel(data),
				html = "";

			// build up cell content
			//html = '<a href="' + $siteURL("event?nodeRef=" + data.nodeRef) + '" rel="'+ rel + '" class="summary">' + data.name + '</a>';

			var nameEl = document.createElement('div');
			var id = this.options.id + "-event-name-" + data.renderDate + "_" + data.nodeRef;
			nameEl.id = id;
			nameEl.innerHTML = data.name;
			Dom.addClass(nameEl, "event-name " + collapsedClass);
			YAHOO.util.Event.addListener(id, "click", function() {
				var nameEl = this;
				if (Dom.hasClass(nameEl, collapsedClass)) {
					Dom.replaceClass(nameEl, collapsedClass, expandedClass);
				} else {
					Dom.replaceClass(nameEl, expandedClass, collapsedClass);
				}
			});

			html += nameEl.outerHTML;
			html += '<div class="event-info-container">';

			var members = data.members;
			var membersString = this.msg("label.events.members") + ": ";
			if (members != null) {
				for (var i = 0; i < members.length; i++) {
					var member = members[i];
					membersString += '<a href="' + $siteURL('view-metadata?nodeRef=' + member.nodeRef) + '">' + member.name.trim() + '</a>';
					if (i < members.length - 1) {
						membersString += ", ";
					}
				}
			}
			html += '<div>' + membersString + "</div>";

			var invitedMembers = data.invitedMembers;
			var invitedMembersString = this.msg("label.events.invitedMembers") + ": ";
			if (invitedMembers != null) {
				for (i = 0; i < invitedMembers.length; i++) {
					var invitedMember = invitedMembers[i];
					invitedMembersString += '<a href="' + $siteURL('view-metadata?nodeRef=' + invitedMember.nodeRef) + '">' + invitedMember.name.trim() + '</a>';
					if (i < invitedMembers.length - 1) {
						invitedMembersString += ", ";
					}
				}
			}
			html += '<div>' + invitedMembersString + "</div>";
			if (data.description !== "") {
				html += '<div>' + this.msg("label.events.description") + ":</div>";
				html += data.description;
			}
			html += '</div>';

			// write to DOM
			elCell.innerHTML += html;
		},

		/**
		 * Called by the DataTable to render the 'description' cell, which contains the event description (notes).
		 *
		 * @method renderCellDescription
		 * @param elCell {object}
		 * @param oRecord {object}
		 * @param oColumn {object}
		 * @param oData {object|string}
		 *
		 */
		renderCellDescription : function (elCell, oRecord, oColumn, oData)
		{
			var data = oRecord.getData(),
				html = this.truncate(data); //run truncation

			// write to DOM
			elCell.innerHTML = html;
		},

		/**
		 * Called by the DataTable to render the 'location' cell, which contains the event location (where) and icon.
		 *
		 * @method renderCellLocation
		 * @param elCell {object}
		 * @param oRecord {object}
		 * @param oColumn {object}
		 * @param oData {object|string}
		 *
		 */
		renderCellLocation : function (elCell, oRecord, oColumn, oData)
		{
			var data = oRecord.getData(),
				html = "";

			// build up cell content
			html = '<span class="agendaLocation">'+ data.where + '</span>'
			if (data.where === "")
			{
				Dom.addClass(elCell, "empty");
			}
			// write to DOM
			elCell.innerHTML = html;
		},

		/**
		 * Called by the DataTable to render the 'actions' cell, which contains the action links.
		 *
		 * @method renderCellActions
		 * @param elCell {object}
		 * @param oRecord {object}
		 * @param oColumn {object}
		 * @param oData {object|string}
		 *
		 */
		renderCellActions : function (elCell, oRecord, oColumn, oData)
		{
			var data = oRecord.getData(),
				html = "",
				actions = [],
				rel = this.getRel(data),
				template = '<a href="{url}" class="{type}" title="{tooltip}" rel="' + rel + '"><span>{label}</span></a>',
				write = false,
				isEdit = false,
				me = this;

			// build up cell content
			write = this.options.permitToCreateEvents;
			isEdit = data.permissions.isEdit;
			isDelete = data.permissions.isDelete;

			// NOTE: DOM order (Delete, Edit, Info) is reverse of display order (Info, Edit, Delete), due to right float.
			if (write) {
				// Edit
				if (isEdit) {
					actions.push(YAHOO.lang.substitute(template,
						{
							type:"editAction",
							url: $siteURL("event-edit?nodeRef=" + data.nodeRef),
							label: me.msg("agenda.action.edit.label"),
							tooltip: me.msg("agenda.action.edit.tooltip")
						}));
				}
			}

			// Info
			actions.push(YAHOO.lang.substitute(template,
				{
					type:"infoAction summary",
					url: $siteURL("event?nodeRef=" + data.nodeRef),
					label: me.msg("agenda.action.info.label"),
					tooltip: me.msg("agenda.action.info.tooltip")
				}));



			html = actions.join(" ");

			// write to DOM
			elCell.innerHTML = html;
			this.renderCellActionsExt(data.nodeRef, elCell, actions)
		},

		renderCellActionsExt: function renderCellActionsExt(nodeRef, cell, cellActions) {
			var template = '<a id="{id}" href="#" class="{type}" title="{tooltip}"><span>{label}</span></a>';

			var me = this;
			var items = [];
			items.push(nodeRef);
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
						var acceptId = null;
						var acceptActionObject = null;
						var rejectId = null;
						var rejectActionObject = null;
						var deleteId = null;
						var deleteActionObject = null;
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
									acceptId = Alfresco.util.generateDomId();
									cellActions.push(YAHOO.lang.substitute(template,
										{
											id: acceptId,
											type: "acceptAction",
											label: me.msg("agenda.action.accept.label"),
											tooltip: me.msg("agenda.action.accept.tooltip")
										}));
									acceptActionObject = actionObj;
								} else if (actionObj.actionId === "Отклонить приглашение") {
									rejectId = Alfresco.util.generateDomId();
									cellActions.push(YAHOO.lang.substitute(template,
										{
											id: rejectId,
											type: "rejectAction",
											label: me.msg("agenda.action.reject.label"),
											tooltip: me.msg("agenda.action.reject.tooltip")
										}));
									rejectActionObject = actionObj;
								} else if (actionObj.label === "Удалить") {
									deleteId = Alfresco.util.generateDomId();
									cellActions.push(YAHOO.lang.substitute(template,
										{
											id: deleteId,
											type: "deleteAction",
											label: me.msg("agenda.action.delete.label"),
											tooltip: me.msg("agenda.action.delete.tooltip")
										}));
									deleteActionObject = actionObj;
								}
							});

							if (acceptId != null) {
								YAHOO.util.Event.addListener(acceptId, "click", function() {
									armToolbar.onGroupActionsClick(null, null, acceptActionObject);
								});
							}
							if (rejectId != null) {
								YAHOO.util.Event.addListener(rejectId, "click", function() {
									armToolbar.onGroupActionsClick(null, null, rejectActionObject);
								});
							}
							if (deleteId != null) {
								YAHOO.util.Event.addListener(deleteId, "click", function() {
									armToolbar.onGroupActionsClick(null, null, deleteActionObject);
								});
							}
							if (acceptId != null || rejectId != null || deleteId != null) {
								var html = cellActions.join(" ");
								cell.innerHTML = html;
							}
						}
					}
				},
				failureMessage:"message.failure",
				execScripts:true
			});			// Accept/reject actions
		},

		/**
		 * Render events to DOM
		 *
		 *  Called by getEvents. Runs every time an event has been modified.
		 *  Delegates actual rendering to other functions. (e.g. renderDay & DataTable cell renderers)
		 *
		 * @method addEvents
		 * @param {Object} events - processed array of view relevant events from getEvents()
		 */
		renderEvents : function (events)
		{
			// using this.bind for inline function calls to ensure access to parent object.
			var numEvents = 0, // number of events for this view (dates already validated server side),
				grandParentEl = this.getCalendarContainer(), // this contains all the DataTables
				data = {}, // alias
				tag = this.options.tag || null, // which (if any) tag is selected?
				sortedEvents = {}, //temporary array used for comparing the new events with and existing events
				modifiedDates = [], // this array contains updates days which will be used by the renderer.
				linkStart = '<a href="" class="addEvent">', // used in template below to wrap link text
				linkEnd = '</a>', // start/close tags seperated from template for i18n flexibility
				noEventsId = this.options.id + "-noEvent",
				noEventsNoEditTemplate ='<div id="' + noEventsId + '" class="noEvent">'+
					'<p class="instructionTitle">{noevents}</p>'+
					'</div>',
				noEventsEditTemplate = '<div id="' + noEventsId + '" class="noEvent">'+
					'<p class="instructionTitle">{noevents}</p>'+
					'<span>{link}</span>'+
					'</div>';

			// set up Data
			this.widgets.Data = this.widgets.Data || {}; // check object instantiation
			data = this.widgets.Data; // alias the data

			// check events arg was supplied (if it was, update memory), else read from memory
			if (events)
			{
				// if it was passed in, filter it for multiday events
				events = this.filterMultiday(events);
				this.events = events;
			} else
			{
				events = this.events;
			}

			// filter events for selected tag:
			events = this.tagFilter(events);

			numEvents = events.length;

			// Set View Title.
			this.updateTitle();

			// sort the events from the passed parameter into the data object according to date (in big endian ISO format YYYY-MM-DD)
			for (var i=0;i<numEvents;i++)
			{
				var event = events[i],
					date = event.displayFrom || event.from;

				date = date.split('T')[0];
				sortedEvents[date] = sortedEvents[date] || {events: []};
				sortedEvents[date].events.push(event);
			}

			// Check for days that are no longer needed.
			for (date in data)
			{
				// if the date exists in the current object, but not the new one, remove it
				if(!sortedEvents[date])
				{
					// remove from DOM
					this.removeDay(date);
					// remove from Data
					delete data[date];
				}
			}

			if (numEvents > 0)
			{
				// Remove the default text/noEvent text if it exists (both live in same div)
				var noEventsEl = Dom.get(noEventsId);
				if(noEventsEl)
				{
					noEventsEl.parentNode.removeChild(noEventsEl);
				}

				// loop through each of the sorted events and write to Data store & render if new data has been supplied.
				for (date in sortedEvents)
				{
					var render = false;

					// check to see if data exists (and whether it was rendered) and if it does (and has), only update it if it has changed.
					// Some dates have data but aren't rendered initially (e.g. multiday events that start before the view starts), but may
					// need rendering this time. Rendering necessitates the dataTable object - hence the check.
					if (data[date] && data[date].dataTable)
					{
						// Converting Objects to JSON strings to enable comparison
						if (YAHOO.lang.JSON.stringify(data[date].events) != YAHOO.lang.JSON.stringify(sortedEvents[date].events))
						{
							render = true; // day exists, but event data modified
							data[date].events = sortedEvents[date].events;
						}
					}
					else // day is new.
					{
						render = true;
						data[date] = sortedEvents[date]
					}

					// if it is new or has changed, update data and render.
					if (render)
					{
						this.renderDay(date); // each day has a different data table to make it easier to render the day header & manage additional day/removing empty days.
					}
				}
			} else
			{
				// Display noEvent text
				var noEventsTemplate = (this.options.permitToCreateEvents)? noEventsEditTemplate : noEventsNoEditTemplate; // show different help text if the user can't create an event.

				grandParentEl.innerHTML = YAHOO.lang.substitute(noEventsTemplate,
					{
						link: this.msg("agenda.add-events", linkStart, linkEnd),
						noevents: this.msg("agenda.no-events")
					});
			}

			// These two functions need to be called the first time this is run only.
			if (!this.eventsInitialised)
			{
				this.initAgendaEvents();
				this.eventsInitialised = true;
			}
		},

		/**
		 * Renders the DataTable and Title for each day.
		 *
		 * @method renderDay
		 * @param {String} date - the ISO Formatted (yyyy-mm-dd) string for the date to render
		 */
		renderDay: function (date)
		{
			var data = this.widgets.Data[date],
				schema =
					[
						{key: "start", formatter:this.bind(this.renderCellStart)}, // both the start and end times.
						{key: "name", formatter:this.bind(this.renderCellName)},
						//{key: "description", formatter:this.bind(this.renderCellDescription)},
						{key: "where", formatter:this.bind(this.renderCellLocation)},
						{key: "actions", formatter:this.bind(this.renderCellActions)}
					],
				grandParentEl = this.getCalendarContainer();

			// Check we have the data to work with, and that the date to be rendered is valid for the view.
			// The events have already been filtered to ensure that a portion of them is valid for the current view,
			// but some multiday events may extend (or begin) out side of the view parameters, so this filtering needs to occur again.
			if (!data || !this.isValidDateForView(fromISO8601(date)))
			{
				return false;
			}

			for (var i=0; i<data.events.length; i++)
			{
				data.events[i].renderDate = date;
			}

			// instantiate or update DataSource:
			data.dataSource = new YAHOO.util.LocalDataSource(data.events);

			// does day already have a DataTable?
			if (data.dataTable)
			{
				// already exists, so remove and recreate
				// TODO - there must be a better way to update an existing data table than this?
				this.removeDay(date);
			}

			var containerEl = document.createElement('div'), // the container for each day with title.
				parentEl = document.createElement('div'), // the container for each day.
				titleEl = document.createElement('div'), // the day heading.
				kids = Dom.getChildren(grandParentEl), // all the elements containing DataTables
				dateTime = fromISO8601(date).getTime(), // makes date comparisons easier
				insertBeforeThisEl,
				today = Alfresco.util.toISO8601(new Date()).split("T")[0];

			containerEl.id = this.options.id + "-cont-" + date;
			Dom.addClass(containerEl, "dayContainer");

			parentEl.id = this.options.id + "-dt-" + date;
			Dom.addClass(parentEl, "dayContent");

			titleEl.id = this.options.id + "-head-" + date;
			Dom.addClass(titleEl, "dayTitle");
			titleEl.innerHTML = Alfresco.util.relativeDate(fromISO8601(date), this.msg("date-format.dayDateMonth"), {limit: true});
			Dom.setAttribute(titleEl, "title", formatDate(fromISO8601(date), this.msg("date-format.fullDate")));

			// Add highlighting on today's element.
			if (date === today)
			{
				Dom.addClass(containerEl, "is-today");
			}

			// Write elements to DOM
			// Magic insert location finding code.
			// returns an HTML element of the first heading after the date we're looking for. If blank, append to grandParent. else insert before match.
			for (i in kids)
			{
				// the iso date is on the end of the element's id, compare this with current date
				if (fromISO8601(kids[i].id.slice(-10)).getTime() > dateTime)
				{
					insertBeforeThisEl = kids[i];
					break;
				}
			}

			containerEl.appendChild(titleEl);
			containerEl.appendChild(parentEl);

			if (insertBeforeThisEl) {
				Dom.insertBefore(containerEl, insertBeforeThisEl);
			}
			else { // looks like this is the last date we've currently got, so stick it on the end.
				grandParentEl.appendChild(containerEl);
			}

			// instantiate the dataTable.
			data.dataTable = new YAHOO.widget.DataTable(parentEl, schema, data.dataSource,
				{
					onEventHighlightRow: function(event, target){Dom.addClass(target, "yui-dt-highlight")}
				});

			// Note Event bindings occur automagically, so action links do not need to be bound to anything.

			//Add row hover effects.
			data.dataTable.subscribe("rowMouseoverEvent", data.dataTable.onEventHighlightRow);
			data.dataTable.subscribe("rowMouseoutEvent", data.dataTable.onEventUnhighlightRow);
		},

		/**
		 * Removes All HTML elements associated with a given date
		 * @param {string} date
		 */
		removeDay: function (date)
		{
			var elements = Selector.query("[id$="+date+"]");
			for (i in elements) {
				var rmEl = Dom.get(elements[i]);
				rmEl.parentNode.removeChild(rmEl);
			}
		},

		/**
		 *  ACTION HANDLERS
		 */

		/**
		 * Triggered when the previous/next links are clicked.
		 */
		onLoadEvents: function (previus) {
			var step = 30, // number of days to add each time
				dayInMS = 24 * 60 * 60 * 1000; // milliseconds in one day

			// Update the start or end date as appropriate
			if (previus) {
				this.options.startDate = new Date(this.options.startDate.getTime() - (step * dayInMS));
				this.options.endDate = new Date(this.options.endDate.getTime() - (step * dayInMS));
			} else {
				this.options.startDate = new Date(this.options.startDate.getTime() + (step * dayInMS));
				this.options.endDate = new Date(this.options.endDate.getTime() + (step * dayInMS));
			}

			// get a fresh list of events from server, this calls the render functions on success
			this.getEvents();
		},


		/**
		 * UTIL METHODS
		 */

		/**
		 * Returns the root element for the calendar DataTables & titles.
		 *
		 * @method getCalendarContainer
		 * @return {HTML Element}
		 */
		getCalendarContainer: function ()
		{
			return Dom.get(this.options.id);
		},

		/**
		 * Updates the Agenda title with the new date and tags (if any)
		 *
		 * @method updateTitle
		 */
		updateTitle: function ()
		{

			var startDate = this.options.startDate,
				endDate = this.options.endDate,
				startDateString = "",
				withYear = this.msg("date-format.longDate"),
				noYear = this.msg("date-format.longDateNoYear"),
				endDateString = formatDate(endDate, withYear);

			// convert date objects to strings
			// only show year in start date if it differs to end date.
			if (startDate.getFullYear() === endDate.getFullYear())
			{
				startDateString = formatDate(startDate, noYear);
			} else
			{
				startDateString = formatDate(startDate, withYear)
			}

			this.titleEl.innerHTML = this.msg("title.agenda", startDateString, endDateString);
		},

		/**
		 * Truncates the text after a set number of characters and adds the show more link
		 *
		 * Note: Breaks on previous word boundary and will not increase the visible string length
		 * - if the show more string added to the truncated text is greater than the original
		 * string, the original is used.
		 *
		 * @method truncate
		 * @param {string} text - the text to truncate
		 * @param {int} length - the number of characters to show before truncating.
		 *
		 */
		truncate : function (event, length)
		{
			var showMore = this.msg("agenda.truncate.show-more"),
				ellipsis = this.msg("agenda.truncate.ellipsis"),
				truncateTo = parseInt(length) || parseInt(this.options.truncateLength) || 100, // use default and ensure int.
				text = event.description,
				result = text,
				resultReplace = "";

			// don't truncate unless we need to.
			// if we do truncate, we want to ensure that the overhead (showMore text and ellipsis) doesn't result in an actual
			// increase in the string's length.
			if (text.length > truncateTo + showMore.length + ellipsis.length)
			{
				result = text.substring(0,truncateTo);
				// truncate to previous one.
				resultReplace = result.replace(/\w+$/, '');
				// but ensure we don't remove the whole string if there are no word boundaries.
				result = (resultReplace.length > 0)? resultReplace : result ;

				// add in ellipsis and the html wrapped show more string
				result = '<span class="truncatedText">' + result + ellipsis + " " + '<a href="javascript:void(0);" rel="'+ this.getRel(event) +'" class="showMore">' + showMore + '</a>.'
			}
			return result;
		},

		expandDescription: function (el)
		{
			var event = this.getEventObj(el),
				containerEl = el.parentNode,
				text = $html(event.description),
				showLess = '<a href="javascript:void(0);" rel="' + el.rel + '" class="showLess">' + this.msg("agenda.truncate.show-less") + '</a>';

			containerEl.innerHTML = text + " " + showLess;
		},

		collapseDescription: function (el)
		{
			var event = this.getEventObj(el),
				containerEl = el.parentNode

			containerEl.innerHTML = this.truncate(event);
		}
	}, true);
})();