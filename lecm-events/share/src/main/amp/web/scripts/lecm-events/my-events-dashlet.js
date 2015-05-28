/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.dashlet
 */
LogicECM.dashlet = LogicECM.dashlet || {};


/**
 * Dashboard TasksSubordinates component.
 *
 * @namespace LogicECM.dashlet
 * @class LogicECM.dashlet.TasksSubordinates
 */
(function()
{
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    /**
     * Alfresco Slingshot aliases
     */
    var $html = Alfresco.util.encodeHTML,
        $siteURL = Alfresco.util.siteURL;
    /**
     * Preferences
     */
    var PREFERENCES_EVENTS_DASHLET_FILTER = "ru.it.lecm.share.events.dashlet.filter";

    /**
     * Dashboard TasksSubordinates constructor.
     *
     * @param {String} htmlId The HTML id of the parent element
     * @return {LogicECM.dashlet.TasksSubordinates} The new component instance
     * @constructor
     */
    LogicECM.dashlet.MyEvents = function (htmlId)
    {
        LogicECM.dashlet.MyEvents.superclass.constructor.call(this, "LogicECM.dashlet.MyEvents", htmlId, ["button", "container", "datasource", "datatable", "paginator", "history", "animation"]);

        // Services
        this.services.preferences = new Alfresco.service.Preferences();

        return this;
    };

    /**
     * Extend from Alfresco.component.Base
     */
    YAHOO.extend(LogicECM.dashlet.MyEvents, Alfresco.component.Base);
    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.dashlet.MyEvents.prototype,
        {
            maxItems: "5",

            menuButton: null,
            /**
             * Fired by YUI when parent element is available for scripting
             * @method onReady
             */
            onReady: function () {

                var maxItems = LogicECM.module.Base.Util.getCookie(this._buildPreferencesKey());
                if (maxItems !== null) {
                    this.maxItems = maxItems;
                } else {
                    this.maxItems = "today";
                }

                var menuItems = [
                    { value: "today", text: this.msg("relative.today"), onclick: { fn: this.onMenuItemClick.bind(this)} },
                    { value: "5", text: this.msg("label.dashlet.next_count") +  " " + 5, onclick: { fn: this.onMenuItemClick.bind(this)} },
                    { value: "10", text: this.msg("label.dashlet.next_count") +  " " + 10, onclick: { fn: this.onMenuItemClick.bind(this)} }
                ];

                var label = ""
                if (this.maxItems === "today") {
                    label = this.msg("relative.today")
                } else {
                    label = this.msg("label.dashlet.next_count") +  " " + this.maxItems
                }

                this.menuButton = new YAHOO.widget.Button(this.id + '-filters', {type: "menu",
                    label: label,
                    menu: menuItems});

                this.menuButton.set("label", label);
                this.loadItems();
            },

            loadItems: function loadItems_function() {
                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.PROXY_URI  + "lecm/events/nearest",
                        dataObj:{
                            maxItems: this.maxItems
                        },
                        successCallback:
                        {
                            fn: this.buildList,
                            scope: this
                        },
                        failureCallback:
                        {
                            fn: this.onListLoadFailed,
                            scope: this
                        },
                        scope: this,
                        execScripts: true
                    });
            },

            buildList: function buildList_function(response) {
                var container = Dom.get(this.id + '-events');
                container.innerHTML = '';
                var events = response.json;
                if (events.length > 0) {
                    events.forEach(function(event) {
                        var header = document.createElement("h3");
                        header.className = 'header';
                        var a = document.createElement("a");
                        a.href = Alfresco.constants.URL_PAGECONTEXT + "event?nodeRef=" + event.nodeRef;
                        a.innerHTML = event.title;
                        header.appendChild(a);
                        container.appendChild(header);
                        var datetime = document.createElement('div');
                        datetime.className = 'location';
                        var date = "";
                        if (event.allDay) {
                            date = Alfresco.util.formatDate(new Date(event.fromDate), this.msg("lecm.date-format.defaultDateOnly")) + " " + this.msg("label.all-day");
                        } else {
                            var fromDay = Alfresco.util.formatDate(new Date(event.fromDate), this.msg("lecm.date-format.defaultDateOnly"));
                            var toDay = Alfresco.util.formatDate(new Date(event.toDate), this.msg("lecm.date-format.defaultDateOnly"));
                            if (fromDay === toDay) {
                                date = fromDay + ' ' + Alfresco.util.formatDate(new Date(event.fromDate), this.msg("lecm.date-format.time")) + ' - ' + Alfresco.util.formatDate(new Date(event.toDate), this.msg("lecm.date-format.time"));
                            } else {
                                date = Alfresco.util.formatDate(new Date(event.fromDate), this.msg("lecm.date-format.datetime")) + ' - ' + Alfresco.util.formatDate(new Date(event.toDate), this.msg("lecm.date-format.datetime"));
                            }
                        }
                        datetime.innerHTML = event.location + ', ' + date;
                        container.appendChild(datetime);

                        var delim = document.createElement('div');
                        delim.className = 'delim';
                        delim.innerHTML = '&nbsp;';
                        container.appendChild(delim);

                    }.bind(this));
                } else {
                    container.innerHTML = "<div class='default-text'>" + this.msg("label.not-record") + "</div>";
                }
            },

            onListLoadFailed: function Contracts_onListLoadFailed()
            {
                this.errandsList.innerHTML = this.msg("label.not-record");
            },

            onMenuItemClick: function onMenuItemClick_function(p_sType, p_aArgs, p_oItem) {
                var sText = p_oItem.cfg.getProperty("text");
                this.menuButton.set("label", sText);
                var date = new Date;
                date.setDate(date.getDate() + 30);
                var expiresDate = date;
                LogicECM.module.Base.Util.setCookie(this._buildPreferencesKey(), p_oItem.value, {expires: expiresDate});
                this.maxItems = p_oItem.value;
                this.loadItems();
            },

            _buildPreferencesKey: function () {
                return this.PREFERENCES_EVENTS_DASHLET_FILTER + "." + LogicECM.currentUser;
            }
        });
})();
