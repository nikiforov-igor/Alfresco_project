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

(function () {
    var Dom = YAHOO.util.Dom;

    LogicECM.module.ExecutionTreeControl = function (htmlId) {
        LogicECM.module.ExecutionTreeControl.superclass.constructor.call(this, "LogicECM.module.ExecutionTreeControl", htmlId, ["button", "container"]);
        return this;
    };

    YAHOO.extend(LogicECM.module.ExecutionTreeControl, Alfresco.component.Base,
        {
            options: {
                documentNodeRef: null,
                formId: null,
                fieldId: null
            },

            folerUrl: Alfresco.constants.PROXY_URI + "/lecm/eds/tree/execution/datasource?documentNodeRef={documentNodeRef}",
            receivedItems: {},

            onReady: function () {
                if (this.options.documentNodeRef) {
                    this.receivedItems[this.options.documentNodeRef] = 0;
                    this.layerByLayer(YAHOO.lang.substitute(this.folerUrl, {
                        documentNodeRef: this.options.documentNodeRef
                    }), this.id + "-expandable-table");
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

                                var template =
                                    '<div class="item-description">' +
                                    '   <img class="document-type" src="/share/res/images/lecm-documents/type-icons/{docTypeIcon}.png" ' +
                                    '       onerror="this.src = \'/share/res/images/lecm-documents/type-icons/default_document.png\';"/>' +
                                    '       <span class="link-span{classNotHaveAccess}">' +
                                    '           <a target="_blank" href="{documentUrl}">{documentName}</a>' +
                                    '           <div>' +
                                    '               <span class="connectionType">{documentStatus}</span>' +
                                    '               <a id="{expandShowId}" href="javascript:void(0);" class="{showExpandClass}">{expandShowMessage}</a>' +
                                    '               <a id="{expandHideId}" href="javascript:void(0);" class="hidden1">{expandHideMessage}</a>' +
                                    '           </div>' +
                                    '           <div id="{expandedDivId}" class="hidden1"/>' +
                                    '   </span>' +
                                    '</div>';

                                var expandShowId = "expand-show-" + nodeRef.replace('workspace://SpacesStore/', '');
                                var expandHideId = "expand-hide-" + nodeRef.replace('workspace://SpacesStore/', '');
                                var showExpandLink = false;
                                if ((oRecord._oData.docType == 'lecm-errands:document' && oRecord._oData.status == "Исполнено")
                                    || (oRecord._oData.docType == 'lecm-resolutions:document' && (oRecord._oData.status == "На исполнении" || oRecord._oData.status == "Завершено"))) {
                                    showExpandLink = true;
                                }

                                el.innerHTML = YAHOO.lang.substitute(template, {
                                    docTypeIcon: oRecord._oData.docType.replace(':', '_'),
                                    classNotHaveAccess: !oRecord._oData.hasAccess ? ' dont-have-access' : '',
                                    documentUrl: Alfresco.constants.URL_PAGECONTEXT + 'document?nodeRef=' + nodeRef,
                                    documentName: oData,
                                    documentStatus: Alfresco.util.message('msg.status') + ': ' + oRecord._oData.status,
                                    expandShowId: expandShowId,
                                    expandHideId: expandHideId,
                                    expandShowMessage: Alfresco.util.message(oRecord._oData.docType == 'lecm-errands:document' ? 'msg.errand.report.show' : 'msg.resolution.statistic.show'),
                                    expandHideMessage: Alfresco.util.message(oRecord._oData.docType == 'lecm-errands:document' ? 'msg.errand.report.hide' : 'msg.resolution.statistic.hide'),
                                    showExpandClass: showExpandLink ? '' : 'hidden1',
                                    expandedDivId: "expanded-block-" + nodeRef.replace('workspace://SpacesStore/', '')
                                });
                                if (previousDocRef) {
                                    if (!me.receivedItems[nodeRef])
                                        me.receivedItems[nodeRef] = me.receivedItems[previousDocRef] + 1;
                                } else {
                                    me.receivedItems[nodeRef] = 1;
                                }

                                YAHOO.util.Event.on(expandShowId, "click", me.expandedBlockShow, {nodeRef: nodeRef, type: oRecord._oData.docType}, me);
                                YAHOO.util.Event.on(expandHideId, "click", me.expandedBlockHide, nodeRef, me);
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

                            YAHOO.util.Event.onContentReady(tabId, function () {
                                me.layerByLayer(YAHOO.lang.substitute(me.folerUrl, {
                                    documentNodeRef: nodeRef
                                }), tabId, pagId)
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
            },

            expandedBlockShow: function (e, args) {
                Dom.addClass("expand-show-" + args.nodeRef.replace('workspace://SpacesStore/', ''), "hidden1");
                Dom.removeClass("expand-hide-" + args.nodeRef.replace('workspace://SpacesStore/', ''), "hidden1");
                Dom.removeClass("expanded-block-" + args.nodeRef.replace('workspace://SpacesStore/', ''), "hidden1");

                if (args.type == 'lecm-errands:document') {
                    this.expandErrandContent(args.nodeRef)
                } else if (args.type == 'lecm-resolutions:document') {
                    this.expandResolutionContent(args.nodeRef)
                }
            },

            expandedBlockHide: function (e, nodeRef) {
                Dom.addClass("expand-hide-" + nodeRef.replace('workspace://SpacesStore/', ''), "hidden1");
                Dom.addClass("expanded-block-" + nodeRef.replace('workspace://SpacesStore/', ''), "hidden1");
                Dom.removeClass("expand-show-" + nodeRef.replace('workspace://SpacesStore/', ''), "hidden1");
            },

            expandErrandContent: function (nodeRef) {
                var container = Dom.get("expanded-block-" + nodeRef.replace('workspace://SpacesStore/', ''));
                if (container) {
                    container.innerHTML = "Errand " + nodeRef;
                }
            },

            expandResolutionContent: function (nodeRef) {
                var container = Dom.get("expanded-block-" + nodeRef.replace('workspace://SpacesStore/', ''));
                if (container) {
                    container.innerHTML = "Resolution " + nodeRef;
                }
            }
        });
})();
