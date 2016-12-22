/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
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
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};

/**
 * LogicECM Base module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Base
 */

LogicECM.module.Errands = LogicECM.module.Errands || {};

(function () {
    var Dom = YAHOO.util.Dom;

    LogicECM.module.Errands.TreeControl = function (htmlId) {
        LogicECM.module.Errands.TreeControl.superclass.constructor.call(this, "LogicECM.module.Errands.TreeControl", htmlId, ["button", "container"]);
        return this;
    };

    YAHOO.extend(LogicECM.module.Errands.TreeControl, Alfresco.component.Base,
        {
            options: {
                documentNodeRef: null,
                formId: null,
                fieldId: null
            },

            receivedItems: {},

            onReady: function () {
                if (this.options.documentNodeRef) {
                    this.receivedItems[this.options.documentNodeRef] = 0;
                    var folerUrl = Alfresco.constants.PROXY_URI + "/lecm/document/connections/api/tree-datasource?documentNodeRef="
                        + this.options.documentNodeRef + "&linkedDocTypes=lecm-errands:document&onlyDirect=true&isErrandCard=true&isFirstLayer=true";
                    this.layerByLayer(folerUrl, this.id + "-expandable-table");
                }

                LogicECM.module.Base.Util.createComponentReadyElementId(this.id, this.options.formId, this.options.fieldId);
            },

            layerByLayer: function (folerUrl, tableId) {
                var me = this;
                var myDataSource = new YAHOO.util.XHRDataSource(folerUrl);

                myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
                myDataSource.connXhrMode = "queueRequests";
                myDataSource.responseSchema = {
                    resultsList: "items"
                };

                var myDataTable = new YAHOO.widget.RowExpansionDataTable(
                    tableId,
                    [
                        {
                            label: "",
                            formatter: function (el, oRecord, oColumn, oData) {
                                var div = document.createElement("div"),
                                    nodeRef = oRecord._oData.nodeRef,
                                    previousDocRef = oRecord._oData.previosDocRef;

                                if (!me.receivedItems[nodeRef]) {
                                    YAHOO.widget.RowExpansionDataTable.formatRowExpansion(el, oRecord, oColumn, oData);
                                }

                                if (me.receivedItems[previousDocRef]) {
                                    el.parentElement.setAttribute("style", "position: relative;");
                                    el.parentElement.appendChild(div);
                                }
                            }
                        },
                        {
                            key: "title",
                            label: "",
                            resizeable: true,
                            sortable: false,
                            width: '200px',
                            formatter: function (el, oRecord, oColumn, oData) {
                                var nodeRef = oRecord._oData.nodeRef;
                                var previousDocRef = oRecord._oData.previosDocRef;

                                var docTypeIcon = '<img class="document-type" ' +
                                    'src="/share/res/images/lecm-documents/type-icons/' + (oRecord._oData.docType || '').replace(':', '_') + '.png"' +
                                    'onerror="this.src = \'/share/res/images/lecm-documents/type-icons/default_document.png\';"> </img>';

                                var linkBlock = '<span class="link-span' + (!oRecord._oData.hasAccess ? ' dont-have-access' : '') + '">';
                                var directionIconSpan = '<span class="connection-direction ' + (oRecord._oData.direction || '') + '"> </span>';
                                linkBlock += (oRecord._oData.connectionType ? '<p class="connectionType">' + oRecord._oData.connectionType + '</p>' : '');
                                linkBlock += '<a target="_blank" href="' + Alfresco.constants.URL_PAGECONTEXT + 'document?nodeRef=' + nodeRef + '" >' + oData + '</a>';
                                linkBlock += (oRecord._oData.status ? '<p class="connectionType">' + Alfresco.util.message('msg.status') + ': ' + oRecord._oData.status + '</p>' : '') + '</span>';

                                var descrBlock = '<div class="item-description">' + directionIconSpan + docTypeIcon + linkBlock + '</div>';
                                el.innerHTML = descrBlock || '--[ No description ]--';
                                if (previousDocRef) {
                                    if (!me.receivedItems[nodeRef])
                                        me.receivedItems[nodeRef] = me.receivedItems[previousDocRef] + 1;
                                } else {
                                    me.receivedItems[nodeRef] = 1;
                                }
                            }
                        }
                    ],
                    myDataSource,
                    {
                        /**
                         * The "rowExpansionTemplate" property is passed a string. This is passed
                         * through YAHOO.lang.substitute which can match tokens (represented with brackets),
                         * which contain keys from the DataTables data.
                         **/
                        rowExpansionTemplate: function (oData) {
                            var nodeRef = oData.data._oData.nodeRef;
                            var linerEl = oData.liner_element;
                            var tabId = 'tab-' + nodeRef;
                            linerEl.innerHTML = '<div class="yui-inner-div" id="' + tabId + '"></div>';
                            var pagId = 'pag-' + nodeRef;
                            var fUrl = Alfresco.constants.PROXY_URI + "/lecm/document/connections/api/tree-datasource?documentNodeRef=" + nodeRef + "&isErrandCard=true"
                                + "&exclErrands=true&onlySystem=true&connectionTypes=onBasis"
                                + ((oData.data._oData.previosDocRef) ? "&previosDocRef=" + oData.data._oData.previosDocRef : "");

                            YAHOO.util.Event.onContentReady(tabId, function () {
                                me.layerByLayer(fUrl, tabId, pagId)
                            }, true);
                        }
                    }
                );

                /**
                 *
                 * Subscribe to the "cellClickEvent" which will yui-dt-expandablerow-trigger the expansion
                 * when the user clicks on the yui-dt-expandablerow-trigger column
                 *
                 **/
                myDataTable.on('renderEvent', this.colorRows);
                myDataTable.subscribe('cellClickEvent', myDataTable.onEventToggleRowExpansion);
                myDataTable.set("MSG_EMPTY", Alfresco.util.message("errands.tree.no_errands"));
                myDataTable.set("MSG_ERROR", Alfresco.util.message("msg.forbiden"));

                myDataTable.nativeCollapseRow = myDataTable.collapseRow;
                myDataTable.collapseRow = function (recordId) {
                    var rowData = this.getRecord(recordId);
                    var nodeRef = rowData._oData.nodeRef;
                    var level = me.receivedItems[nodeRef];
                    if (level) {
                        var itemRefs = Object.keys(me.receivedItems);
                        var newMap = {};
                        for (var i = 0, itemRef, itemLevel; i < itemRefs.length; i++) {
                            itemRef = itemRefs[i];
                            itemLevel = me.receivedItems[itemRef];
                            if (itemLevel && itemLevel <= level) {
                                newMap[itemRef] = itemLevel;
                            }
                        }
                        me.receivedItems = newMap;
                    }
                    myDataTable.nativeCollapseRow(recordId);
                    me.colorRows();
                };
            },
            colorRows: function () {
                var rows = Dom.getElementsByClassName('yui-dt-rec');
                for (var i = 0; i < rows.length; i++) {
                    YAHOO.util.Dom.removeClass(rows[i], 'yui-dt-odd');
                    YAHOO.util.Dom.removeClass(rows[i], 'yui-dt-even');
                    if (i % 2 === 0) {
                        YAHOO.util.Dom.addClass(rows[i], 'yui-dt-even');
                    } else {
                        YAHOO.util.Dom.addClass(rows[i], 'yui-dt-odd');
                    }
                }
            }
        });
})();
