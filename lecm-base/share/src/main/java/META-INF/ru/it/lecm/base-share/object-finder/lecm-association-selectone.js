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

(function()
{
    var Dom = YAHOO.util.Dom;

    var $combine = Alfresco.util.combinePaths;

    LogicECM.module.AssociationSelectOne = function LogicECM_module_AssociationSelectOne(fieldHtmlId)
    {
        LogicECM.module.AssociationSelectOne.superclass.constructor.call(this, "LogicECM.module.AssociationSelectOne", fieldHtmlId, [ "container", "resize", "datasource"]);
        this.selectItemId = fieldHtmlId + "-added";
        this.currentDisplayValueId = fieldHtmlId + "-currentValueDisplay";

        return this;
    };

    YAHOO.extend(LogicECM.module.AssociationSelectOne, Alfresco.component.Base,
        {
            options:
            {
                parentNodeRef: "",

                itemType: "cm:content",

                itemFamily: "node",

                selectedValueNodeRef: "",

                maxSearchResults: 1000,

                nameSubstituteString: "{cm:name}",

                openSubstituteSymbol: "{",

                closeSubstituteSymbol: "}"
            },

            selectItemId: null,

            currentDisplayValueId: null,

            selectItem: null,

            currentDisplayValueElement: null,

            dataSource: null,

            setMessages: function AssociationSelectOne_setMessages(obj)
            {
                LogicECM.module.AssociationSelectOne.superclass.setMessages.call(this, obj);
                return this;
            },

            onReady: function AssociationSelectOne_onReady()
            {
                this.selectItem = Dom.get(this.selectItemId);
                if (this.selectItem) {
                    this.populateSelect();
                }
                this.currentDisplayValueElement = Dom.get(this.currentDisplayValueId);
                if (this.currentDisplayValueElement) {
                    this.populateCurrentValue();
                }
            },

            destroy: function AssociationSelectOne_destroy()
            {
                LogicECM.module.AssociationSelectOne.superclass.destroy.call(this);
            },

            populateSelect: function AssociationSelectOne_populateSelect() {
                this._createDataSource();

                var successHandler = function (sRequest, oResponse, oPayload)
                {
                    var results = oResponse.results;
                    for (var i = 0; i < results.length; i++) {
                        var node = results[i];
                        var opt = document.createElement('option');
                        opt.innerHTML = node.name;
                        opt.value = node.nodeRef;
                        if (node.nodeRef == this.options.selectedValueNodeRef) {
                            opt.selected = true;
                        }
                        this.selectItem.appendChild(opt);
                    }
                }.bind(this);

                var failureHandler = function (sRequest, oResponse)
                {
                    if (oResponse.status == 401)
                    {
                        // Our session has likely timed-out, so refresh to offer the login page
                        window.location.reload();
                    }
                    else
                    {
                        //todo show failure message
                    }
                }.bind(this);

                var url = this._generateChildrenUrlPath(this.options.parentNodeRef) + this._generateChildrenUrlParams("");

                this.dataSource.sendRequest(url,
                    {
                        success: successHandler,
                        failure: failureHandler,
                        scope: this
                    });
            },

            populateCurrentValue: function AssociationSelectOne_populateCurrentValue() {
                this._createDataSource();

                var successHandler = function (sRequest, oResponse, oPayload)
                {
                    var results = oResponse.results;
                    for (var i = 0; i < results.length; i++) {
                        var node = results[i];
                        if (node.nodeRef == this.options.selectedValueNodeRef) {
                            this.currentDisplayValueElement.innerHTML = node.name;
                        }
                    }
                }.bind(this);

                var failureHandler = function (sRequest, oResponse)
                {
                    if (oResponse.status == 401)
                    {
                        // Our session has likely timed-out, so refresh to offer the login page
                        window.location.reload();
                    }
                    else
                    {
                        //todo show failure message
                    }
                }.bind(this);

                var url = this._generateChildrenUrlPath(this.options.parentNodeRef) + this._generateChildrenUrlParams("");

                this.dataSource.sendRequest(url,
                    {
                        success: successHandler,
                        failure: failureHandler,
                        scope: this
                    });
            },

            _createDataSource: function AssociationSelectOne__createDataSource() {
                var me = this;

                var pickerChildrenUrl = Alfresco.constants.PROXY_URI + "lecm/forms/picker/" + this.options.itemFamily;
                this.dataSource = new YAHOO.util.DataSource(pickerChildrenUrl,
                    {
                        responseType: YAHOO.util.DataSource.TYPE_JSON,
                        connXhrMode: "queueRequests",
                        responseSchema:
                        {
                            resultsList: "items",
                            metaFields:
                            {
                                parent: "parent"
                            }
                        }
                    });

                this.dataSource.doBeforeParseData = function (oRequest, oFullResponse)
                {
                    var updatedResponse = oFullResponse;

                    if (oFullResponse)
                    {
                        var items = oFullResponse.data.items;

                        if (me.options.maxSearchResults > -1 && items.length > me.options.maxSearchResults)
                        {
                            items = items.slice(0, me.options.maxSearchResults-1);
                        }

                        var index, item;
                        for (index in items)
                        {
                            if (items.hasOwnProperty(index))
                            {
                                item = items[index];
                                if (item.type == "cm:category" && item.displayPath.indexOf("/categories/Tags") !== -1)
                                {
                                    item.type = "tag";
                                    oFullResponse.data.parent.type = "tag";
                                }
                            }
                        }

                        updatedResponse =
                        {
                            parent: oFullResponse.data.parent,
                            items: items
                        };
                    }

                    return updatedResponse;
                };
            },

            _generateChildrenUrlPath: function AssociationSelectOne__generateChildrenUrlPath(nodeRef)
            {
                return $combine("/", nodeRef.replace("://", "/"), "children");
            },

            _generateChildrenUrlParams: function AssociationSelectOne__generateChildrenUrlParams(searchTerm)
            {
                return "?selectableType=" + this.options.itemType + "&searchTerm=" + encodeURIComponent(searchTerm) +
                    "&size=" + this.options.maxSearchResults + "&nameSubstituteString=" + encodeURIComponent(this.options.nameSubstituteString) +
                    "&openSubstituteSymbol=" + encodeURIComponent(this.options.openSubstituteSymbol) +
                    "&closeSubstituteSymbol=" + encodeURIComponent(this.options.closeSubstituteSymbol);
            }
         });
})();