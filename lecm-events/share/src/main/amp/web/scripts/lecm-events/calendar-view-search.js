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
		YAHOO.Bubbling.on("beforeFormRuntimeInit", this.onBeforeFormRuntimeInit, this);

		return this;
	};

	YAHOO.extend(LogicECM.module.Calendar.SearchView, LogicECM.module.Calendar.AgendaView, {});

	YAHOO.lang.augmentObject(LogicECM.module.Calendar.SearchView.prototype, {

		searchProperties: [],

		currentForm: null,

		searchFormId: "searchBlock-forms",

		attrSearchApplied: false,
		fullTextSearchApplied: false,

		render: function () {
			this.loadSearchProperties();
			this.initEvents();

			this.initToolbar();

			this.initFiltersToolbar();

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

			Alfresco.util.createYUIButton(this, "toolbar-searchButton", this.onFullTextSearchClick);
			Alfresco.util.createYUIButton(this, "toolbar-extendSearchButton", this.onExtSearchClick);
			Event.on(this.id + "-toolbar-clearSearchInput", "click", this.onFullTextClearSearch, true, this);
			Event.on(this.id + "-toolbar-full-text-search", "keyup", this.checkShowClearSearch, null, this);

			new YAHOO.util.KeyListener(Dom.get(this.id + "-toolbar-full-text-search"),
				{
					keys: 13
				},
				{
					fn: this.onFullTextSearchClick,
					scope: this,
					correctScope: true
				}, "keydown").enable();

			this.checkShowClearSearch();

		},

		initFiltersToolbar: function() {
			this.updateCurrentFiltersFormView();
			var filtersBlockId = this.id + "-filters-toolbar";
			YAHOO.util.Dom.setStyle(filtersBlockId + "-body", "visibility", "inherit");

			YAHOO.util.Event.on(filtersBlockId + "-delete-all-link", 'click', this.deleteAllFilters, null, this);
		},

		checkShowClearSearch: function () {
			if (Dom.get(this.id + "-toolbar-full-text-search").value.length > 0) {
				Dom.setStyle(this.id + "-toolbar-clearSearchInput", "visibility", "visible");
			} else {
				Dom.setStyle(this.id + "-toolbar-clearSearchInput", "visibility", "hidden");
			}
		},

		onFullTextSearchClick: function() {
			this.getEvents();
			this.fullTextSearchApplied = true;
			this.updateCurrentFiltersFormView();
		},

		onFullTextClearSearch: function (loadEvents) {
			Dom.get(this.id + "-toolbar-full-text-search").value = "";
			this.checkShowClearSearch();
			this.fullTextSearchApplied = false;
			this.updateCurrentFiltersFormView();
			if (loadEvents) {
				this.getEvents();
			}
		},

		onExtSearchClick: function() {
			var defaultForm = {};
			defaultForm.id = "search";
			defaultForm.type = "lecm-events:document";

			if (this.searchDialog == null){
				// Если SearchBlock уже есть в разметке в body (остался с предыдущей "страницы")
				// удаляем его
				// Это актуально для раздела "Администрирование"
				var searchBlockInBody = Selector.query("body > div > #searchBlock", null, true);
				if (searchBlockInBody) {
					searchBlockInBody.parentNode.removeChild(searchBlockInBody);
				}
				// создаем диалог
				this.searchDialog = Alfresco.util.createYUIPanel("searchBlock",
					{
						width:"800px"
					});
				// создаем кнопки
				this.widgets.searchButton = Alfresco.util.createYUIButton(this, "searchBlock-search-button", this.onExtSearch, {}, Dom.get("searchBlock-search-button"));
				this.widgets.clearSearchButton = Alfresco.util.createYUIButton(this, "searchBlock-clearSearch-button", this.onClearExtSearch, true, Dom.get("searchBlock-clearSearch-button"));
			}

			if(!this.currentForm || !this.currentForm.htmlid) { // форма ещё создана или не проинициализирована
				// создаем форму
				this.renderExtFormTemplate(defaultForm);
			} else {
				if (this.searchDialog != null) {
					this.searchDialog.show();
				}
			}
		},

		renderExtFormTemplate: function (form, isClearSearch, loadEvent) {
			if (!isClearSearch) {
				isClearSearch = false;
			}
			// update current form state
			this.currentForm = form;

			if (this.currentForm) {
				var formDiv = Dom.get(this.searchFormId); // элемент в который будет отрисовываться форма
				form.htmlid = this.searchFormId + "-" + form.type.split(":").join("_");

				// load the form component for the appropriate type
				var formData = {
					htmlid: form.htmlid,
					itemKind: "type",
					itemId: form.type,
					formId: form.id,
					mode: "edit",
					showSubmitButton: false,
					showCancelButton: false,
					showCaption: false
				};
				Alfresco.util.Ajax.request(
					{
						url: Alfresco.constants.URL_SERVICECONTEXT + "/components/form",
						dataObj: formData,
						successCallback: {
							fn: function (response) {
								formDiv.innerHTML = response.serverResponse.responseText;
								if (this.searchDialog) {
									if (isClearSearch && loadEvent) {
										this.getEvents();
									} else {
										this.searchDialog.show();
									}
								}
							},
							scope: this
						},
						failureMessage: "Could not load form component",
						scope: this,
						execScripts: true
					});
			}
		},

		onBeforeFormRuntimeInit:function (layer, args) {
			// extract the current form runtime - so we can reference it later
			if (this.currentForm && args[1].runtime.formId == (this.searchFormId + "-" + this.currentForm.type.split(":").join("_") + "-form")) {
				this.currentForm.runtime = args[1].runtime;
			}
		},

		onExtSearch: function() {
	   	    this.getEvents();
			this.searchDialog.hide();
			this.attrSearchApplied = true;
			this.updateCurrentFiltersFormView();
		},

		onClearExtSearch: function(loadEvent) {
			this.renderExtFormTemplate(this.currentForm, true, loadEvent);
			this.attrSearchApplied = false;
			this.updateCurrentFiltersFormView();
		},

		getEvents : function () {
			var searchTerm = Dom.get(this.id + "-toolbar-full-text-search").value;
			var searchData = "";
			if (searchTerm.length > 0) {
				for (var column in this.searchProperties) {
					searchData += column + ":" + decodeURIComponent(searchTerm) + "#";
				}
				if (searchData != "") {
					searchData = searchData.substring(0, (searchData.length) - 1);
				} else {
					searchData = "cm:name" + ":" + decodeURIComponent(searchTerm);
				}
			}

			var extSearchData = "";
			if (this.currentForm != null && this.currentForm.runtime != null) {
				extSearchData = YAHOO.lang.JSON.stringify(this.currentForm.runtime.getFormData());
			}

			Alfresco.util.Ajax.request(
				{
					url: Alfresco.constants.PROXY_URI + "lecm/events/search",
					dataObj: {
						searchTerm: searchData,
						extSearchData: extSearchData,
                        timeZoneOffset: encodeURIComponent(new Date().getTimezoneOffset())
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
		},

		updateCurrentFiltersFormView: function() {
			var filtersExist = this.fullTextSearchApplied || this.attrSearchApplied;
			var filtersBlockId = this.id + "-filters-toolbar";
			Dom.setStyle(filtersBlockId, "display", filtersExist ? "" : "none");

			if (filtersExist) {
				var currentFiltersConteiner = Dom.get(filtersBlockId + "-current-filters");
				if (currentFiltersConteiner != null) {
					var filtersHTML = "";

					if (this.attrSearchApplied) {
						filtersHTML += "<span class='arm-filter-item' title='" + Alfresco.util.message('lecm.arm.lbl.by.attrs') + "'>";
						filtersHTML += Alfresco.util.message('lecm.arm.lbl.by.attrs');
						filtersHTML += this.getRemoveAttrFilterButton();
						filtersHTML += "</span>";
					}

					if (this.fullTextSearchApplied) {
						filtersHTML += "<span class='arm-filter-item' title='" + Alfresco.util.message('lecm.arm.lbl.by.text') + "'>";
						filtersHTML += Alfresco.util.message('lecm.arm.lbl.by.text');
						filtersHTML += this.getRemoveFullTextFilterButton();
						filtersHTML += "</span>";
					}

					currentFiltersConteiner.innerHTML = filtersHTML;
				}
			}
		},

		getRemoveAttrFilterButton: function () {
			var id = Dom.generateId();
			var result = "<span id='" + id + "' class='arm-filter-remove'>✕</span>";
			YAHOO.util.Event.onAvailable(id, function () {
				YAHOO.util.Event.on(id, 'click', this.onClearExtSearch, true, this);
			}, null, this);
			return result;
		},

		getRemoveFullTextFilterButton: function () {
			var id = Dom.generateId();
			var result = "<span id='" + id + "' class='arm-filter-remove'>✕</span>";
			YAHOO.util.Event.onAvailable(id, function () {
				YAHOO.util.Event.on(id, 'click', this.onFullTextClearSearch, true, this);
			}, null, this);
			return result;
		},

		deleteAllFilters: function() {
			this.onClearExtSearch(false);
			this.onFullTextClearSearch(false);

			this.renderEvents([]);
		}
	}, true);
})();