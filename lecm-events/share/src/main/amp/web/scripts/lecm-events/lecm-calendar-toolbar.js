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
		Element = YAHOO.util.Element;

	LogicECM.module.Calendar.Toolbar = function(containerId, enabledViews, defaultView) {
		this.name = "LogicECM.module.Calendar.Toolbar";
		this.id = containerId;
		this.enabledViews = enabledViews;
		this.defaultView = defaultView;

		this.navButtonGroup = null;
		this.nextButton = null;
		this.prevButton = null;
		this.todayButton = null;

		this.isAgenda = false;

		/* Load YUI Components */
		Alfresco.util.YUILoaderHelper.require(["button", "container", "connection"], this.onComponentsLoaded, this);

		Alfresco.util.ComponentManager.register(this);
		YAHOO.Bubbling.on("calendarReady", this.onCalendarReady, this);
		return this;
	};

	LogicECM.module.Calendar.Toolbar.prototype =
	{
		PREFERENCE_KEY: "ru.it.lecm.calendar.state.",
		/**
		 * Set messages for this component.
		 *
		 * @method setMessages
		 * @param obj {object} Object literal specifying a set of messages
		 * @return {Alfresco.DocListTree} returns 'this' for method chaining
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
		init: function() {
			this.nextButton = Alfresco.util.createYUIButton(this, "next-button", this.onNextNav);
			this.nextButton.addClass("next-button");
			this.prevButton = Alfresco.util.createYUIButton(this, "prev-button", this.onPrevNav);
			this.prevButton.addClass("prev-button");
			this.todayButton = Alfresco.util.createYUIButton(this, "today-button", this.onTodayNav);

			this.navButtonGroup = new YAHOO.widget.ButtonGroup(this.id + "-navigation");
			if (typeof(this.navButtonGroup) != "undefined" && this.navButtonGroup._buttons != null ) // Will be undefined / null if navigation is hidden serverside (e.g. only one view enabled)
			{

				// The view will either be the booked marked value (from the hash with the "view=" stripped off), or on the query params, or the default.
				var hash = window.location.hash,
					view = hash.substring(hash.indexOf("view=") + 5).split("&")[0] || Alfresco.util.getQueryStringParameter('view') || this.defaultView;

				for (var i = 0; i < this.navButtonGroup._buttons.length; i++)
				{
					if (this.navButtonGroup._buttons[i]._button.id.match(view))
					{
						this.navButtonGroup.check(i);
						this.updateButtons(i);
						break;
					}
				}
				this.navButtonGroup.on("checkedButtonChange", this.onNavigation, this.navButtonGroup, this);
			}
		},

		onCalendarReady: function onCalendarReady() {
			var calendarPref = LogicECM.module.Base.Util.getCookie(this.PREFERENCE_KEY  + encodeURIComponent(LogicECM.currentUser));
			if (calendarPref !== null) {
				for (var i = 0; i < this.navButtonGroup._buttons.length; i++) {
					if (this.navButtonGroup._buttons[i]._button.id.match(calendarPref)) {
						this.navButtonGroup.check(i);
						this.updateButtons(i);
						break;
					}
				}
			}
		},

		onNextNav: function(e) {
			if (this.isAgenda) {
				this._fireEvent("nextAgendaNav");
			} else {
				this._fireEvent("nextNav");
			}
		},

		onPrevNav: function(e) {
			if (this.isAgenda) {
				this._fireEvent("prevAgendaNav");
			} else {
				this._fireEvent("prevNav");
			}
		},

		onTodayNav: function(e) {
			if (this.isAgenda) {
				this._fireEvent("todayAgendaNav");
			} else {
				this._fireEvent("todayNav");
			}
		},

		onNavigation: function(e)
		{
			this.updateButtons(e.newValue.index);

			YAHOO.Bubbling.fire("viewChanged",
				{
					activeView: e.newValue.index
				});
		},
		updateButtons : function(butIndex)
		{
			var selectedButton = this.navButtonGroup.getButtons()[butIndex];
			if (this.todayButton != null) // Note: Today button will be null if elements are hidden serverside
			{
				// Disable Nav for Agenda view which uses a different navigation model
				if (this.endWidth(selectedButton.get("id"), LogicECM.module.Calendar.View.VIEWTYPE_AGENDA)) {
					this.isAgenda = true;
				} else {
					this.isAgenda = false;
				}
			}
		},
		endWidth: function(text, postfix) {
			return text.indexOf(postfix) == (text.length - postfix.length);
		},

		_fireEvent: function(type) {
			YAHOO.Bubbling.fire(type,
				{
					source: this
				});
		}
	};
})();