if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Contracts = LogicECM.module.Contracts|| {};
LogicECM.module.Contracts.dashlet = LogicECM.module.Contracts.dashlet || {};

(function()
{
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Selector = YAHOO.util.Selector;

    /**
     * Preferences
     */
    var PREFERENCES_CONTRACTS = "ru.it.lecm.contracts",
        PREF_FILTER = ".dashlet-contracts-filter",
        PREF_RANGE = ".dashlet-contracts-range";

    LogicECM.module.Contracts.dashlet.Contracts = function Contracts_constructor(htmlId)
    {
        LogicECM.module.Contracts.dashlet.Contracts.superclass.constructor.call(this, "LogicECM.module.Contracts.dashlet.Contracts", htmlId, ["button", "container"]);

        // Preferences service
        this.services.preferences = new Alfresco.service.Preferences();

        return this;
    };

    YAHOO.extend(LogicECM.module.Contracts.dashlet.Contracts, Alfresco.component.Base,
        {
            options:
            {
                activeFilter: "1",

                /**
                 * Component region ID.
                 *
                 * @property regionId
                 * @type string
                 */
                regionId: "",

                itemType:"lecm-contract:document",
                destination: null
            },

            doubleClickLock: false,

            contractsList: null,

            link: "contracts-main",

            onReady: function Contracts_onReady()
            {
                // Create dropdown filter widgets
                this.widgets.range = Alfresco.util.createYUIButton(this, "range", this.onDateFilterChanged,
                    {
                        type: "menu",
                        menu: "range-menu",
                        lazyloadmenu: false
                    });

                this.widgets.user = Alfresco.util.createYUIButton(this, "user", this.onUserFilterChanged,
                    {
                        type: "menu",
                        menu: "user-menu",
                        lazyloadmenu: false
                    });

                // The activity list container
                this.contractsList = Dom.get(this.id + "-contractsList");

                // Load preferences to override default filter and range
                this.widgets.range.set("label", this.msg("filter.7days"));
                this.widgets.range.value = "7";
                this.widgets.user.set("label", this.msg("filter.my"));
                this.widgets.user.value = "my";

                this.services.preferences.request(this.buildPreferences(),
                    {
                        successCallback:
                        {
                            fn: function(p_oResponse)
                            {
                                var rangePreference = Alfresco.util.findValueByDotNotation(p_oResponse.json, this.buildPreferences(PREF_RANGE), "7");
                                if (rangePreference !== null)
                                {
                                    this.widgets.range.value = rangePreference;
                                    // set the correct menu label
                                    var menuItems = this.widgets.range.getMenu().getItems();
                                    for (index in menuItems)
                                    {
                                        if (menuItems.hasOwnProperty(index))
                                        {
                                            if (menuItems[index].value === rangePreference)
                                            {
                                                this.widgets.range.set("label", menuItems[index].cfg.getProperty("text"));
                                                break;
                                            }
                                        }
                                    }
                                }

                                var filterPreference = Alfresco.util.findValueByDotNotation(p_oResponse.json, this.buildPreferences(PREF_FILTER), "my");
                                if (filterPreference !== null)
                                {
                                    this.widgets.user.value = filterPreference;
                                    // set the correct menu label
                                    var menuItems = this.widgets.user.getMenu().getItems();
                                    for (index in menuItems)
                                    {
                                        if (menuItems.hasOwnProperty(index))
                                        {
                                            if (menuItems[index].value === filterPreference)
                                            {
                                                this.widgets.user.set("label", menuItems[index].cfg.getProperty("text"));
                                                break;
                                            }
                                        }
                                    }
                                }
                                // Display the toolbar now that we have selected the filter
                                Dom.removeClass(Selector.query(".toolbar div", this.id, true), "hidden");
                                // Populate the activity list
                                this.populateContractsList(this.widgets.range.value, this.widgets.user.value);
                            },
                            scope: this
                        },
                        failureCallback:
                        {
                            fn: function()
                            {
                                // Display the toolbar now that we have selected the filter
                                Dom.removeClass(Selector.query(".toolbar div", this.id, true), "hidden");
                                // Populate the activity list
                                this.populateContractsList(this.widgets.range.value, this.widgets.user.value);
                            },
                            scope: this
                        }
                    });
            },

            /**
             * @return {string}
             */
            buildPreferences: function Contracts_buildPreferences(suffix)
            {
                var opt = this.options;
                return PREFERENCES_CONTRACTS + "." + opt.regionId + (suffix ? suffix : "");
            },

            /**
             * Populate the activity list via Ajax request
             * @method populateContractsList
             */
            populateContractsList: function Contracts_populateContractsList(dateFilter, userFilter)
            {
                var newId = Alfresco.util.generateDomId();
                // Load the activity list
                Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/contracts/dashlets/list",
                        dataObj:
                        {
                            dateFilter: dateFilter,
                            userFilter: userFilter,
                            htmlid: newId
                        },
                        successCallback:
                        {
                            fn: this.onListLoaded,
                            scope: this,
                            obj: dateFilter
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

            onListLoaded: function Contracts_onListLoaded(p_response, p_obj)
            {
                this.options.activeFilter = p_obj;
                var html = p_response.serverResponse.responseText;
                if (YAHOO.lang.trim(html).length === 0)
                {
                    this.contractsList.innerHTML = Dom.get(this.id + "-empty").innerHTML;
                }
                else
                {
                    this.contractsList.innerHTML = html;
                }
            },

            /**
             * List load failed
             * @method onListLoadFailed
             */
            onListLoadFailed: function Contracts_onListLoadFailed()
            {
                this.contractsList.innerHTML = '<div class="detail-list-item first-item last-item">' + this.msg("label.load-failed") + '</div>';
            },

            onDateFilterChanged: function Contracts_onDateFilterChanged(p_sType, p_aArgs)
            {
                var menuItem = p_aArgs[1];

                if (menuItem)
                {
                    this.widgets.range.set("label", menuItem.cfg.getProperty("text"));
                    this.widgets.range.value = menuItem.value;
                    this.populateContractsList(this.widgets.range.value, this.widgets.user.value);
                    this.services.preferences.set(this.buildPreferences(PREF_RANGE), this.widgets.range.value);
                }
            },

            onUserFilterChanged: function Contracts_onUserFilterChanged(p_sType, p_aArgs)
            {
                var menuItem = p_aArgs[1];

                if (menuItem)
                {
                    this.widgets.user.set("label", menuItem.cfg.getProperty("text"));
                    this.widgets.user.value = menuItem.value;
                    this.populateContractsList(this.widgets.range.value, this.widgets.user.value);
                    this.services.preferences.set(this.buildPreferences(PREF_FILTER), this.widgets.user.value);
                }
            },

            onAddContractClick: function Contracts_onAddContractClick(){
                if (this.doubleClickLock) return;
                this.doubleClickLock = true;
                var destination = this.options.destination,
                    itemType = this.options.itemType;

                // Intercept before dialog show
                var doBeforeDialogShow = function (p_form, p_dialog) {
                    Alfresco.util.populateHTML(
                        [ p_dialog.id + "-form-container_h", this.msg("label.create-row.title") ]
                    );
                    Dom.addClass(p_dialog.id + "-form-container", "metadata-form-edit");
                    this.doubleClickLock = false;
	                p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
                };

                var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
	            var templateRequestParams = {
                        itemKind: "type",
                        itemId: itemType,
                        destination: destination,
                        mode: "create",
                        formId: "",
		            submitType: "json",
		            showCancelButton: true
	            };

                // Using Forms Service, so always create new instance
                var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createContractDetails");
                createDetails.setOptions(
                    {
                        width: "84em",
                        templateUrl: templateUrl,
	                    templateRequestParams: templateRequestParams,
                        actionUrl: null,
                        destroyOnHide: true,
                        doBeforeDialogShow: {
                            fn: doBeforeDialogShow,
                            scope: this
                        },
                        onSuccess: {
                            fn: function DataGrid_onActionCreate_success(response) {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.save.success")
                                    });
                                window.location.href = window.location.protocol + "//" + window.location.host +
                                    Alfresco.constants.URL_PAGECONTEXT + "document?nodeRef=" + response.json.persistedObject;
                                this.doubleClickLock = false;
                            },
                            scope: this
                        },
                        onFailure: {
                            fn: function DataGrid_onActionCreate_failure(response) {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.save.failure")
                                    });
                                this.doubleClickLock = false;
                            },
                            scope: this
                        }
                    }).show();
            }
        });
})();
