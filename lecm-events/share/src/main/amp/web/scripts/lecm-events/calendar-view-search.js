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
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		$combine = Alfresco.util.combinePaths,
		fromISO8601 = Alfresco.util.fromISO8601;


	LogicECM.module.Calendar.SearchView = function (htmlId) {
		this.id = htmlId;
		LogicECM.module.Calendar.SearchView.superclass.constructor.call(this, htmlId);

		return this;
	};

	YAHOO.extend(LogicECM.module.Calendar.SearchView, LogicECM.module.Calendar.AgendaView, {});

	YAHOO.lang.augmentObject(LogicECM.module.Calendar.SearchView.prototype, {

		searchProperties: [],

		render: function () {
			this.loadSearchProperties();
			this.initEvents();

			this.initToolbar();

			var view = Alfresco.util.getQueryStringParameter('view');
			this.onUpdateView(view);

			YAHOO.Bubbling.on("viewChanged", function (e, args) {
				var view = Alfresco.util.ComponentManager.findFirst("LogicECM.module.Calendar.Toolbar").enabledViews[args[1].activeView];
				this.onUpdateView(view);
			}, this);
		},

		loadSearchProperties: function () {
			Alfresco.util.Ajax.jsonGet(
				{
					url: $combine(Alfresco.constants.URL_SERVICECONTEXT, "/lecm/components/datagrid/config/columns?formId=searchColumns&itemType=" + encodeURIComponent("lecm-events:document")),
					successCallback:
					{
						fn: function (response) {
							var columns = response.json.columns;
							for (var i = 0; i < columns.length; i++) {
								var column = columns[i];
								if (column.dataType == "text" || column.dataType == "mltext") {
									this.searchProperties[column.name] = column.name;
								}
							}
						},
						scope: this
					},
					failureCallback:
					{
						obj:
						{
							title: this.msg("message.error.columns.title"),
							text: this.msg("message.error.columns.description")
						},
						scope: this
					}
				});
		},

		onUpdateView: function(view) {
			if (view === LogicECM.module.Calendar.View.VIEWTYPE_SEARCH) {
				Dom.setStyle(this.id, "display", "block");
			} else {
				Dom.setStyle(this.id, "display", "none");
			}
		},

		initToolbar: function() {
			var toolbarId = this.id + "-toolbar";
			Dom.setStyle(toolbarId + "-body", "visibility", "visible");

			Alfresco.util.createYUIButton(this, "toolbar-searchButton", this.onSearchClick);
			Alfresco.util.createYUIButton(this, "toolbar-extendSearchButton", this.onExSearchClick);
			Event.on(this.id + "-toolbar-clearSearchInput", "click", this.onClearSearch, null, this);
			Event.on(this.id + "-toolbar-full-text-search", "keyup", this.checkShowClearSearch, null, this);

			new YAHOO.util.KeyListener(Dom.get(this.id + "-toolbar-full-text-search"),
				{
					keys: 13
				},
				{
					fn: this.onSearchClick,
					scope: this,
					correctScope: true
				}, "keydown").enable();

			this.checkShowClearSearch();

		},

		checkShowClearSearch: function () {
			if (Dom.get(this.id + "-toolbar-full-text-search").value.length > 0) {
				Dom.setStyle(this.id + "-toolbar-clearSearchInput", "visibility", "visible");
			} else {
				Dom.setStyle(this.id + "-toolbar-clearSearchInput", "visibility", "hidden");
			}
		},

		onSearchClick: function() {
			var searchTerm = Dom.get(this.id + "-toolbar-full-text-search").value;
			if (searchTerm.length > 0) {
				this.getEvents(searchTerm);
			}
		},

		onExSearchClick: function() {
			alert("ext-search");
		},

		onClearSearch: function Toolbar_onSearch() {
			Dom.get(this.id + "-toolbar-full-text-search").value = "";
			this.checkShowClearSearch();
		},

		getEvents : function (searchTerm) {
			var searchData = "";
			for (var column in this.searchProperties) {
				searchData += column + ":" + decodeURIComponent(searchTerm) + "#";
			}
			if (searchData != "") {
				searchData = searchData.substring(0, (searchData.length) - 1);
			} else {
				searchData = "cm:name" + ":" + decodeURIComponent(searchTerm);
			}

			Alfresco.util.Ajax.request(
				{
					url: Alfresco.constants.PROXY_URI + "lecm/events/search",
					dataObj: {
						searchTerm: searchData
					},
					//filter out non relevant events for current view
					successCallback: {
						fn: this.onEventsLoaded,
						scope: this
					},
					failureMessage: Alfresco.util.message("load.fail", "LogicECM.module.Calendar.View")
				});
		},

		onEventsLoaded: function (o)
		{
			var data = YAHOO.lang.JSON.parse(o.serverResponse.responseText).events;
			var events = [];
			var comparisonFn = null;
			var viewStartDate = this.options.startDate;
			var viewEndDate = this.options.endDate;

			for (var i = 0; i < data.length; i++)
			{
				// TODO: Make this format consistent across calendar views and API.
				var ev = data[i];
				var date = fromISO8601(ev.startAt.iso8601);
				var endDate = fromISO8601(ev.endAt.iso8601);

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

			this.renderEvents(events);
		},

		isValidDateForView: function(date) {
			return true;
		}
	}, true);
})();