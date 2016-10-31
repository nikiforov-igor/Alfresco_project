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
 * @class LogicECM..module
 */
LogicECM.module = LogicECM.module || {};

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
        $hasEventInterest = Alfresco.util.hasEventInterest;

    LogicECM.module.PackageItemsFinder = function LECM_PackageItemsFinder(htmlId, currentValueHtmlId)
    {
        LogicECM.module.PackageItemsFinder.superclass.constructor.call(this, htmlId, currentValueHtmlId);
        this.resultLists = {};
        return this;
    };

    YAHOO.extend(LogicECM.module.PackageItemsFinder, LogicECM.module.ObjectFinder,
        {

            resultLists: null,

            _createSelectedItemsControls: function ObjectFinder__createSelectedItemsControls()
            {
                var doBeforeParseDataFunction = function ObjectFinder__createSelectedItemsControls_doBeforeParseData(oRequest, oFullResponse)
                {
                    var updatedResponse = oFullResponse;

                    if (oFullResponse && oFullResponse.length > 0)
                    {
                        var items = oFullResponse.data.items;

                        // Special case for tags, which we want to render differently to categories
                        var index, item;
                        for (index in items)
                        {
                            if (items.hasOwnProperty(index))
                            {
                                item = items[index];
                                if (item.type == "cm:category" && item.displayPath.indexOf("/categories/Tags") !== -1)
                                {
                                    item.type = "tag";
                                }
                            }
                        }

                        // we need to wrap the array inside a JSON object so the DataTable is happy
                        updatedResponse =
                        {
                            items: items
                        };
                    }

                    return updatedResponse;
                };

                var me = this;

                if (this.options.disabled === false)
                {

                    // Setup a DataSource for the selected items list
                    this.widgets.dataSource = new YAHOO.util.DataSource([],
                        {
                            responseType: YAHOO.util.DataSource.TYPE_JSARRAY,
                            doBeforeParseData: doBeforeParseDataFunction
                        });

                    // Picker DataTable definition
                    var columnDefinitions =
                        [
                            { key: "nodeRef", label: "Icon", sortable: false, formatter: this.fnRenderCellIcon(), width: this.options.compactMode ? 16 : 32 },
                            { key: "name", label: "Item", sortable: false, formatter: this.fnRenderCellName() },
                            { key: "remove", label: "Remove", sortable: false, formatter: this.fnRenderCellRemove(), width: 16 }
                        ];

                    this.widgets.dataTable = new YAHOO.widget.DataTable(this.pickerId + "-selectedItems", columnDefinitions, this.widgets.dataSource,
                        {
                            MSG_EMPTY: this.msg("form.control.object-picker.selected-items.empty")
                        });

                    // Hook remove item action click events
                    var fnRemoveItemHandler = function ObjectFinder__createSelectedItemsControls_fnRemoveItemHandler(layer, args)
                    {
                        var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
                        if (owner !== null)
                        {
                            var target, rowId, record;

                            target = args[1].target;
                            rowId = target.offsetParent;
                            record = me.widgets.dataTable.getRecord(rowId);
                            if (record)
                            {
                                me.widgets.dataTable.deleteRow(rowId);
                                YAHOO.Bubbling.fire("selectedItemRemoved",
                                    {
                                        eventGroup: me,
                                        item: record.getData()
                                    });
                            }
                        }
                        return true;
                    };
                    YAHOO.Bubbling.addDefaultAction("remove-" + this.eventGroup, fnRemoveItemHandler, true);
                }

                // Add displayMode as class so we can separate the styling of the currentValue element
                var currentValueEl = Dom.get(this.id + "-currentValueDisplay");
                Dom.addClass(currentValueEl, "object-finder-" + this.options.displayMode);

                if (this.options.displayMode == "list")
                {
                    // Setup a DataSource for the selected items list
                    var ds = new YAHOO.util.DataSource([],
                        {
                            responseType: YAHOO.util.DataSource.TYPE_JSARRAY,
                            doBeforeParseData: doBeforeParseDataFunction
                        });

                    // Current values DataTable definition
                    var currentValuesColumnDefinitions =
                        [
                            { key: "nodeRef", label: "Icon", sortable: false, formatter: this.fnRenderCellGenericIcon(), width: 50 },
                            { key: "name", label: "Item", sortable: false, formatter: this.fnRenderCellListItemName() }
                        ];
                    if (this.options.showActions) {
                        currentValuesColumnDefinitions.push({ key: "action",
                            label: "Actions",
                            sortable: false,
                            formatter: this.fnRenderCellListItemActions(),
                            width: 200 });

                    }
                    // Make sure the currentValues container is a div rather than a span to make sure it may become a datatable
                    var currentValueId = this.id + "-currentValueDisplay";
                    currentValueEl = Dom.get(currentValueId);
                    if (currentValueEl.tagName.toLowerCase() == "span")
                    {
                        var currentValueDiv = document.createElement("div");
                        currentValueDiv.setAttribute("class", currentValueEl.getAttribute("class"));
                        currentValueEl.parentNode.appendChild(currentValueDiv);
                        currentValueEl.parentNode.removeChild(currentValueEl);
                        currentValueEl = currentValueDiv;
                    }
                    this.widgets.currentValuesDataTable = new YAHOO.widget.DataTable(currentValueEl, currentValuesColumnDefinitions, ds,
                        {
                            MSG_EMPTY: this.msg("form.control.object-picker.selected-items.empty")
                        });
                    this.widgets.currentValuesDataTable.subscribe("rowMouseoverEvent", this.widgets.currentValuesDataTable.onEventHighlightRow);
                    this.widgets.currentValuesDataTable.subscribe("rowMouseoutEvent", this.widgets.currentValuesDataTable.onEventUnhighlightRow);

                    Dom.addClass(currentValueEl, "form-element-border");
                    Dom.addClass(currentValueEl, "form-element-background-color");

                    if (this.options.showActions){
                        // Hook action item click events
                        var fnActionListItemHandler = function ObjectFinder__createSelectedItemsControls_fnActionListItemHandler(layer, args)
                        {
                            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
                            if (owner !== null)
                            {
                                var target, rowId, record;

                                target = args[1].target;
                                rowId = target.offsetParent;
                                record = me.widgets.currentValuesDataTable.getRecord(rowId);
                                if (record)
                                {
                                    var data = record.getData(),
                                        name = YAHOO.util.Dom.getAttribute(args[1].target, "class").split(" ")[0];
                                    for (var i = 0, il = me.options.listItemActions.length; i < il; i++)
                                    {
                                        if (me.options.listItemActions[i].name == name)
                                        {
                                            YAHOO.Bubbling.fire(me.options.listItemActions[i].event,
                                                {
                                                    eventGroup: me,
                                                    value: data,
                                                    rowId: rowId
                                                });
                                            return true;
                                        }
                                    }
                                }
                            }
                            return true;
                        };
                        YAHOO.Bubbling.addDefaultAction("list-action-event-" + this.eventGroup, fnActionListItemHandler, true);
                    }
                }
            },

            fnRenderCellListItemName: function ObjectFinder_fnRenderCellListItemName() {
                var scope = this;
                /**
                 * Action item custom datacell formatter
                 *
                 * @method fnRenderCellListItemName
                 * @param elCell {object}
                 * @param oRecord {object}
                 * @param oColumn {object}
                 * @param oData {object|string}
                 */
                return function ObjectFinder_fnRenderCellListItemName(elCell, oRecord, oColumn, oData, finder) {
                    var item = oRecord.getData(),
                        description = item['lecm-document_list-present-string'] ? item['lecm-document_list-present-string'] : '',
                        modifiedOn = item.modified ? Alfresco.util.formatDate(Alfresco.util.fromISO8601(item.modified)) : null,
                        title = $html(item.name);
                    if (scope.options.showLinkToTarget && scope.options.targetLinkTemplate !== null) {
                        var link;
                        if (YAHOO.lang.isFunction(scope.options.targetLinkTemplate)) {
                            link = scope.options.targetLinkTemplate.call(scope, oRecord.getData());
                        }
                        else {
                            //Discard template, build link from scratch
                            var linkTemplate = (item.site) ? Alfresco.constants.URL_PAGECONTEXT + "site/{site}/document-details?nodeRef={nodeRef}" : Alfresco.constants.URL_PAGECONTEXT + "document-details?nodeRef={nodeRef}";
                            link = YAHOO.lang.substitute(linkTemplate,
                                {
                                    nodeRef: item.nodeRef,
                                    site: item.site
                                });
                        }
                        title = '<a href="' + link + '">' + $html(scope.options.objectRenderer._deactivateLinks(item.name)) + '</a>';
                    }
                    var template = '<h3 class="name">' + title + '</h3>';
                    template += '<div class="description">' + description + '</div>';
                    template += '<div class="viewmode-label">' + scope.msg("form.control.object-picker.modified-on") + ': ' + (modifiedOn ? modifiedOn : scope.msg("label.none")) + '</div>';
                    elCell.innerHTML = template;
                };
            },
            _loadSelectedItems: function ObjectFinder__loadSelectedItems(useOptions)
            {
                var arrItems = "";
                if (this.options.selectedValue)
                {
                    arrItems = this.options.selectedValue;
                }
                else
                {
                    arrItems = this.options.currentValue;
                }

                var onSuccess = function ObjectFinder__loadSelectedItems_onSuccess(response)
                {
                    var items = eval("("+ response.serverResponse.responseText + ")").data.items,
                        item;
                    this.selectedItems = {};
                    this.resultLists = {};
                    //this.singleSelectedItem = null;

                    for (var i = 0, il = items.length; i < il; i++)
                    {
                        item = items[i];
                        if (item.type != this.options.resultListType)  {
                            this.selectedItems[item.nodeRef] = item;
                        } else {
                            this.resultLists[item.nodeRef] = item;
                        }
                    }

                    YAHOO.Bubbling.fire("renderCurrentValue",
                        {
                            eventGroup: this
                        });
                };

                var onFailure = function ObjectFinder__loadSelectedItems_onFailure(response)
                {
                    this.selectedItems = null;
                    this.resultLists = null;
                };

                if (arrItems !== "")
                {
                    Alfresco.util.Ajax.jsonRequest(
                        {
                            url: Alfresco.constants.PROXY_URI + "lecm/forms/picker/items",
                            method: "POST",
                            dataObj:
                            {
                                items: arrItems.split(","),
                                itemValueType: this.options.valueType,
                                itemNameSubstituteString: this.options.nameSubstituteString,
                                substituteParent: this.options.substituteParent != "" ? this.options.substituteParent : "none",
                                itemOpenSubstituteSymbol: this.options.openSubstituteSymbol,
                                itemCloseSubstituteSymbol: this.options.closeSubstituteSymbol,
                                additionalProperties:this.options.additionalProperties ? this.options.additionalProperties : "none"
                            },
                            successCallback:
                            {
                                fn: onSuccess,
                                scope: this
                            },
                            failureCallback:
                            {
                                fn: onFailure,
                                scope: this
                            }
                        });
                }
                else
                {
                    // if disabled show the (None) message
                    if (this.options.disabled && this.options.displayMode == "items")
                    {
                        Dom.get(this.id + "-currentValueDisplay").innerHTML = this.msg("form.control.novalue");
                    }

                    this._enableActions();
                }
            },

            onRenderCurrentValue: function ObjectFinder_onRenderCurrentValue(layer, args) {
                // Check the event is directed towards this instance
                if ($hasEventInterest(this, args)) {
                    var items = this.selectedItems,
                        lists = this.resultLists,
                        displayValue = "";

                    var item, link;
                    if (this.options.displayMode == "list") {
                        var l = this.widgets.currentValuesDataTable.getRecordSet().getLength();
                        if (l > 0) {
                            this.widgets.currentValuesDataTable.deleteRows(0, l);
                        }
                    }

                    for (var key in items) {
                        if (items.hasOwnProperty(key)) {
                            item = items[key];

                            // Special case for tags, which we want to render differently to categories
                            if (item.type == "cm:category" && item.displayPath.indexOf("/categories/Tags") !== -1) {
                                item.type = "tag";
                            }

                            if (this.options.showLinkToTarget && this.options.targetLinkTemplate !== null) {
                                if (this.options.displayMode == "items") {
                                    link = null;
                                    if (YAHOO.lang.isFunction(this.options.targetLinkTemplate)) {
                                        link = this.options.targetLinkTemplate.call(this, item);
                                    }
                                    else {
                                        //Discard template, build link from scratch
                                        var linkTemplate = (item.site) ? Alfresco.constants.URL_PAGECONTEXT + "site/{site}/" + this.options.linkToTarget : Alfresco.constants.URL_PAGECONTEXT + this.options.linkToTarget;
                                        link = YAHOO.lang.substitute(linkTemplate,
                                            {
                                                nodeRef: item.nodeRef,
                                                site: item.site
                                            });
                                    }
                                    if (!this.options.viewOnLinkClick) {
                                        displayValue += this.options.objectRenderer.renderItem(item, 16,
                                            "<div>{icon} <a href='" + link + "'>{name}</a></div>");
                                    } else {
                                        displayValue += this.options.objectRenderer.renderItem(item, 16,
                                            "<div>{icon} <a href='javascript:void(0)'; onclick=\"LogicECM.module.Base.Util.viewAttributes({" +
                                            "formId:\'"+ this.id + "-link\'," +
                                            "itemId:\'" + item.nodeRef + "\'," +
                                            "htmlId: \'LinkMetadata-" + item.nodeRef.replace(/\//g, "_")+ "\'," +
                                            "setId:\'common\'," +
                                            "failureMessage: \'message.object-not-found\'})\">" + "{name}</a></div>");
                                    }
                                }
                                else if (this.options.displayMode == "list") {
                                    this.widgets.currentValuesDataTable.addRow(item);
                                }
                            }
                            else {
                                if (this.options.displayMode == "items") {
                                    if (item.type === "tag") {
                                        displayValue += this.options.objectRenderer.renderItem(item, null, "<div class='itemtype-tag'>{name}</div>");
                                    }
                                    else {
                                        displayValue += this.options.objectRenderer.renderItem(item, 16, "<div class='itemtype-" + $html(item.type) + "'>{icon} {name}</div>");
                                    }
                                }
                                else if (this.options.displayMode == "list") {
                                    this.widgets.currentValuesDataTable.addRow(item);
                                }
                            }
                        }
                    }
                    if (this.options.displayMode == "items") {
                        Dom.get(this.id + "-currentValueDisplay").innerHTML = displayValue;
                    }

                    for (var key in lists) {
                        if (lists.hasOwnProperty(key)) {
                            item = lists[key];
                            Alfresco.util.Ajax.request(
                                {
                                    url:Alfresco.constants.URL_SERVICECONTEXT + "components/form",
                                    dataObj:{
                                        htmlid:"Result-List-" + item.nodeRef,
                                        itemKind:"node",
                                        itemId:item.nodeRef,
                                        mode:"view"
                                    },
                                    successCallback:{
                                        fn:function(response) {
                                            var formEl = Dom.get(this.id +"-currentListValueDisplay");
                                            formEl.innerHTML += response.serverResponse.responseText;
                                        },
                                        scope:this
                                    },
                                    failureMessage:"message.failure",
                                    execScripts:true
                                });
                        }
                    }
                }
            }
        });
})();
