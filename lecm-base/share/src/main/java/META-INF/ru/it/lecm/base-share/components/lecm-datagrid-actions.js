/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};


/**
 * LogicECM Base module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Base
 */
LogicECM.module.Base = LogicECM.module.Base || {};


(function()
{
    /**
     * YUI Library aliases
     */
    var Bubbling = YAHOO.Bubbling;

    /**
     * LogicECM.module.Base.DataActions implementation
     */
    LogicECM.module.Base.DataActions = {};
    LogicECM.module.Base.DataActions.prototype =
    {
        /**
         * Delete item(s).
         *
         * @method onActionDelete
         * @param items {Object | Array} Object literal representing the Data Item to be actioned, or an Array thereof
         */
        onActionDelete: function DataGridActions_onActionDelete(p_items, fnDeleteComplete, metadata)
        {
            var me = this,
                items = YAHOO.lang.isArray(p_items) ? p_items : [p_items];

            var fnActionDeleteConfirm = function DataGridActions__onActionDelete_confirm(items)
            {
                var nodeRefs = [];
                for (var i = 0, ii = items.length; i < ii; i++)
                {
                    nodeRefs.push(items[i].nodeRef);
                }
                var query = "";
                if (metadata) {
                    var fullDelete = metadata.fullDelete;
                    var deletedAssocsType = metadata.deletedAssocsType;
                    if (fullDelete != null) {
                        query = query + "full=" + fullDelete;
                    }
                    if (deletedAssocsType != null) {
                        query = query + "&deletedType=" + deletedAssocsType;
                    }
                }
                this.modules.actions.genericAction(
                    {
                        success:
                        {
                            event:
                            {
                                name: "dataItemsDeleted",
                                obj:
                                {
                                    items: items
                                }
                            },
                            message: this.msg("message.delete.success", items.length),
                            callback:{
                                fn: fnDeleteComplete
                            }
                        },
                        failure:
                        {
                            message: this.msg("message.delete.failure")
                        },
                        webscript:
                        {
                            method: Alfresco.util.Ajax.DELETE,
                            name: "delete",
                            queryString:query
                        },
                        config:
                        {
                            requestContentType: Alfresco.util.Ajax.JSON,
                            dataObj:
                            {
                                nodeRefs: nodeRefs
                            }
                        }
                    });
            };

            Alfresco.util.PopupManager.displayPrompt(
                {
                    title: this.msg("message.confirm.delete.title", items.length),
                    text: this.msg("message.confirm.delete.description", items.length),
                    buttons: [
                        {
                            text: this.msg("button.delete"),
                            handler: function DataGridActions__onActionDelete_delete()
                            {
                                this.destroy();
                                fnActionDeleteConfirm.call(me, items);
                            }
                        },
                        {
                            text: this.msg("button.cancel"),
                            handler: function DataGridActions__onActionDelete_cancel()
                            {
                                this.destroy();
                            },
                            isDefault: true
                        }]
                });
        },

        /**
         * Duplicate item(s).
         *
         * @method onActionDuplicate
         * @param items {Object | Array} Object literal representing the Data Item to be actioned, or an Array thereof
         */
        onActionDuplicate: function DataListActions_onActionDuplicate(p_items)
        {
            var items = YAHOO.lang.isArray(p_items) ? p_items : [p_items],
                destinationNodeRef = new Alfresco.util.NodeRef(this.modules.dataGrid.datagridMeta.nodeRef),
                nodeRefs = [];

            for (var i = 0, ii = items.length; i < ii; i++)
            {
                nodeRefs.push(items[i].nodeRef);
            }

            this.modules.actions.genericAction(
                {
                    success:
                    {
                        event:
                        {
                            name: "dataItemsDuplicated",
                            obj:
                            {
                                items: items
                            }
                        },
                        message: this.msg("message.duplicate.success", items.length)
                    },
                    failure:
                    {
                        message: this.msg("message.duplicate.failure")
                    },
                    webscript:
                    {
                        method: Alfresco.util.Ajax.POST,
                        name: "duplicate/node/" + destinationNodeRef.uri
                    },
                    config:
                    {
                        requestContentType: Alfresco.util.Ajax.JSON,
                        dataObj:
                        {
                            nodeRefs: nodeRefs
                        }
                    }
                });
        }
    };
})();

(function()
{
    LogicECM.module.Base.Actions = function()
    {
        this.name = "LogicECM.module.Base.Actions";

        /* Load YUI Components */
        Alfresco.util.YUILoaderHelper.require(["json"], this.onComponentsLoaded, this);

        return this;
    };

    LogicECM.module.Base.Actions.prototype =
    {
        /**
         * Flag indicating whether module is ready to be used.
         * Flag is set when all YUI component dependencies have loaded.
         *
         * @property isReady
         * @type boolean
         */
        isReady: false,

        /**
         * Object literal for default AJAX request configuration
         *
         * @property defaultConfig
         * @type object
         */
        defaultConfig:
        {
            method: "POST",
            urlStem: Alfresco.constants.PROXY_URI + "lecm/base/action/",
            dataObj: null,
            successCallback: null,
            successMessage: null,
            failureCallback: null,
            failureMessage: null,
            object: null
        },

        /**
         * Fired by YUILoaderHelper when required component script files have
         * been loaded into the browser.
         *
         * @method onComponentsLoaded
         */
        onComponentsLoaded: function DLA_onComponentsLoaded()
        {
            this.isReady = true;
        },

        /**
         * Make AJAX request to data webscript
         *
         * @method _runAction
         * @private
         * @return {boolean} false: module not ready for use
         */
        _runAction: function DLA__runAction(config, obj)
        {
            // Check components loaded
            if (!this.isReady)
            {
                return false;
            }

            // Merge-in any supplied object
            if (typeof obj == "object")
            {
                config = YAHOO.lang.merge(config, obj);
            }

            if (config.method == Alfresco.util.Ajax.DELETE)
            {
                if (config.dataObj !== null)
                {
                    // Change this request into a POST with the alf_method override
                    config.method = Alfresco.util.Ajax.POST;
                    if (config.url.indexOf("alf_method") < 1)
                    {
                        config.url += (config.url.indexOf("?") < 0 ? "?" : "&") + "alf_method=delete";
                    }
                    Alfresco.util.Ajax.jsonRequest(config);
                }
                else
                {
                    Alfresco.util.Ajax.request(config);
                }
            }
            else
            {
                Alfresco.util.Ajax.jsonRequest(config);
            }
        },


        /**
         * ACTION: Generic action.
         * Generic DataList action based on passed-in parameters
         *
         * @method genericAction
         * @param action.success.event.name {string} Bubbling event to fire on success
         * @param action.success.event.obj {object} Bubbling event success parameter object
         * @param action.success.message {string} Timed message to display on success
         * @param action.success.callback.fn {object} Callback function to call on success.
         * <pre>function(data, obj) where data is an object literal containing config, json, serverResponse</pre>
         * @param action.success.callback.scope {object} Success callback function scope
         * @param action.success.callback.obj {object} Success callback function object passed to callback
         * @param action.failure.event.name {string} Bubbling event to fire on failure
         * @param action.failure.event.obj {object} Bubbling event failure parameter object
         * @param action.failure.message {string} Timed message to display on failure
         * @param action.failure.callback.fn {object} Callback function to call on failure.
         * <pre>function(data, obj) where data is an object literal containing config, json, serverResponse</pre>
         * @param action.failure.callback.scope {object} Failure callback function scope
         * @param action.failure.callback.obj {object} Failure callback function object passed to callback
         * @param action.webscript.stem {string} optional webscript URL stem
         * <pre>default: Alfresco.constants.PROXY_URI + "slingshot/datalists/action/"</pre>
         * @param action.webscript.name {string} data webscript URL name
         * @param action.webscript.method {string} HTTP method to call the data webscript on
         * @param action.webscript.queryString {string} Optional queryString to append to the webscript URL
         * @param action.webscript.params.nodeRef {string} nodeRef of target item
         * @param action.wait.message {string} if set, show a Please wait-style message during the operation
         * @param action.config {object} optional additional request configuration overrides
         * @return {boolean} false: module not ready
         */
        genericAction: function DataGridActions_genericAction(action)
        {
            var success = action.success,
                failure = action.failure,
                webscript = action.webscript,
                params = action.params ? action.params : action.webscript.params,
                overrideConfig = action.config,
                wait = action.wait,
                configObj = null;

            var fnCallback = function DataGridActions_genericAction_callback(data, obj)
            {
                // Check for notification event
                if (obj)
                {
                    // Event(s) specified?
                    if (obj.event && obj.event.name)
                    {
                        YAHOO.Bubbling.fire(obj.event.name, obj.event.obj);
                    }
                    if (YAHOO.lang.isArray(obj.events))
                    {
                        for (var i = 0, ii = obj.events.length; i < ii; i++)
                        {
                            YAHOO.Bubbling.fire(obj.events[i].name, obj.events[i].obj);
                        }
                    }

                    // Please wait pop-up active?
                    if (obj.popup)
                    {
                        obj.popup.destroy();
                    }
                    // Message?
                    if (obj.message)
                    {
                        Alfresco.util.PopupManager.displayMessage(
                            {
                                text: obj.message
                            });
                    }
                    // Callback function specified?
                    if (obj.callback && obj.callback.fn)
                    {
                        obj.callback.fn.call((typeof obj.callback.scope == "object" ? obj.callback.scope : this),
                            {
                                config: data.config,
                                json: data.json,
                                serverResponse: data.serverResponse
                            }, obj.callback.obj);
                    }
                }
            };

            // Please Wait... message pop-up?
            if (wait && wait.message)
            {
                if (typeof success != "object")
                {
                    success = {};
                }
                if (typeof failure != "object")
                {
                    failure = {};
                }

                success.popup = Alfresco.util.PopupManager.displayMessage(
                    {
                        modal: true,
                        displayTime: 0,
                        text: wait.message,
                        effect: null
                    });
                failure.popup = success.popup;
            }

            var url;
            if (webscript.stem)
            {
                url = webscript.stem + webscript.name;
            }
            else
            {
                url = this.defaultConfig.urlStem + webscript.name;
            }

            if (params)
            {
                url = YAHOO.lang.substitute(url, params);
                configObj = params;
            }
            if (webscript.queryString && webscript.queryString != "")
            {
                url += "?" + webscript.queryString;
            }

            var config = YAHOO.lang.merge(this.defaultConfig,
                {
                    successCallback:
                    {
                        fn: fnCallback,
                        scope: this,
                        obj: success
                    },
                    successMessage: null,
                    failureCallback:
                    {
                        fn: fnCallback,
                        scope: this,
                        obj: failure
                    },
                    failureMessage: null,
                    url: url,
                    method: webscript.method,
                    responseContentType: Alfresco.util.Ajax.JSON,
                    object: configObj
                });

            return this._runAction(config, overrideConfig);
        }
    };

    /* Dummy instance to load optional YUI components early */
    var dummyInstance = new LogicECM.module.Base.Actions();
})();
