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
    var Dom = YAHOO.util.Dom,
        $siteURL = Alfresco.util.siteURL;

    LogicECM.module.ExecutionTreeControl = function (htmlId) {
        LogicECM.module.ExecutionTreeControl.superclass.constructor.call(this, "LogicECM.module.ExecutionTreeControl", htmlId, ["button", "container"]);
        return this;
    };

    YAHOO.extend(LogicECM.module.ExecutionTreeControl, Alfresco.component.Base,
        {
            options: {
                documentNodeRef: null,
                formId: null,
                fieldId: null,
                showFirstLevel: null
            },

            folerUrl: Alfresco.constants.PROXY_URI + "/lecm/eds/tree/execution/datasource?documentNodeRef={documentNodeRef}",
            receivedItems: {},

            onReady: function () {
                if (this.options.documentNodeRef) {
                    this.receivedItems[this.options.documentNodeRef] = 0;
                    this.layerByLayer(YAHOO.lang.substitute(this.folerUrl + "&showFirstLevel={showFirstLevel}", {
                        documentNodeRef: this.options.documentNodeRef,
                        showFirstLevel: this.options.showFirstLevel.toString()
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
                                    '   <img class="document-type" src=' + Alfresco.constants.URL_RESCONTEXT + '"images/lecm-documents/type-icons/{docTypeIcon}.png" ' +
                                    '       onerror="this.src = \'' + Alfresco.constants.URL_RESCONTEXT + 'images/lecm-documents/type-icons/default_document.png\';"/>' +
                                    '   <span class="link-span{classNotHaveAccess}">' +
                                    '       <a target="_blank" href="{documentUrl}">{documentName}</a>' +
                                    '       <div>' +
                                    '           <span class="connectionType">{documentStatus}</span>' +
                                    '           <a id="{expandShowId}" href="javascript:void(0);" class="{showExpandClass}">{expandShowMessage}</a>' +
                                    '           <a id="{expandHideId}" href="javascript:void(0);" class="hidden1">{expandHideMessage}</a>' +
                                    '       </div>' +
                                    '       <div id="{expandedDivId}" class="hidden1"></div>' +
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
                myDataTable.set("MSG_EMPTY", Alfresco.util.message("execution.tree.no_errands_and_resolutions"));
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
                if (container && !container.innerHTML.length) {
                    Alfresco.util.Ajax.jsonGet({
                        url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/errands/getExecutorReport',
                        dataObj: {
                            nodeRef: nodeRef
                        },
                        successCallback: {
                            scope: this,
                            fn: function (response) {
                                if (response.json) {
                                    var content = "";
                                    content += "<table class='execution-expanded-content type-errand'>";
                                    content += "    <tr>";
                                    content += "        <td class='label-column'>{reportTextLabel}</td>";
                                    content += "        <td>{reportTextContent}</td>";
                                    content += "    </tr>";
                                    content += "    <tr>";
                                    content += "        <td class='label-column'>{connectionsLabel}</td>";
                                    content += "        <td>{connectionsContent}</td>";
                                    content += "    </tr>";
                                    content += "    <tr>";
                                    content += "        <td class='label-column'>{attachmentLabel}</td>";
                                    content += "        <td>{attachmentContent}</td>";
                                    content += "    </tr>";
                                    content += "</table>";

                                    var connectedDocsList = [];
                                    if (response.json.connectedDocuments) {
                                        response.json.connectedDocuments.forEach(function (doc) {
                                            connectedDocsList.push("<a href='" + $siteURL(doc.viewUrl + "?nodeRef=" + doc.nodeRef) + "' target='_blank'>" + doc.presentString + "</a>");
                                        });
                                    }
                                    var attachmentsList = [];
                                    if (response.json.attachments) {
                                        response.json.attachments.forEach(function (attachment) {
                                            attachmentsList.push("<a onclick=\"LogicECM.module.Base.Util.showAttachmentsModalForm('" + nodeRef + "', '" + attachment.nodeRef + "')\" target=\"_blank\">" + attachment.name + "</a>");
                                        });
                                    }

                                    container.innerHTML = YAHOO.lang.substitute(content, {
                                        reportTextLabel: this.msg("msg.errand.report.text.label"),
                                        connectionsLabel: this.msg("msg.errand.report.connections.label"),
                                        attachmentLabel: this.msg("msg.errand.report.attachment.label"),
                                        reportTextContent: response.json.text,
                                        connectionsContent: this.getTableListLayout(connectedDocsList),
                                        attachmentContent: this.getTableListLayout(attachmentsList)
                                    });
                                }
                            }
                        },
                        failureMessage: this.msg('message.failure')
                    });
                }
            },

            expandResolutionContent: function (nodeRef) {
                var container = Dom.get("expanded-block-" + nodeRef.replace('workspace://SpacesStore/', ''));
                if (container && !container.innerHTML.length) {
                    Alfresco.util.Ajax.jsonPost({
                        url: Alfresco.constants.PROXY_URI + "lecm/substitude/format/node",
                        dataObj: {
                            nodeRef: nodeRef,
                            substituteString: "{lecm-eds-aspect:execution-statistics}#{lecm-review-aspects:related-review-statistics}"
                        },
                        successCallback: {
                            fn: function (response) {
                                if (response && response.json.formatString) {
                                    var data = response.json.formatString.split("#");
                                    var executionStatistics = data[0];
                                    var reviewStatistics = data[1];

                                    var content = "";
                                    content += "<table class='execution-expanded-content type-resolution'>";
                                    content += "    <tr>";
                                    content += "        <td class='label-column'>{errandsStatisticLabel}</td>";
                                    content += "        <td>{errandsStatisticList}";
                                    content += "    </tr>";
                                    content += "    <tr>";
                                    content += "        <td class='label-column'>{reviewStatisticLabel}</td>";
                                    content += "        <td>{reviewStatisticList}";
                                    content += "        </td>";
                                    content += "    </tr>";
                                    content += "</table>";

                                    var executionList = [];
                                    if (executionStatistics && executionStatistics.length) {
                                        executionList = JSON.parse(executionStatistics).filter(function (item) {
                                            return item.count > 0;
                                        }).map(function(item) {
                                            return item.state + ": " + item.count;
                                        });
                                    }

                                    var reviewList = [];
                                    if (reviewStatistics && reviewStatistics.length) {
                                        reviewList = JSON.parse(reviewStatistics).filter(function (item) {
                                            return item.count > 0;
                                        }).map(function(item) {
                                            return item.state + ": " + item.count;
                                        });
                                    }

                                    container.innerHTML = YAHOO.lang.substitute(content, {
                                        errandsStatisticLabel: this.msg("msg.resolution.errands.statistic.label"),
                                        reviewStatisticLabel: this.msg("msg.resolution.review.statistic.label"),
                                        errandsStatisticList: executionList.length ? this.getTableListLayout(executionList) : this.msg("msg.resolution.errands.statistic.empty.text"),
                                        reviewStatisticList: reviewList.length ? this.getTableListLayout(reviewList) : this.msg("msg.resolution.review.statistic.empty.text")
                                    });
                                }
                            },
                            scope: this
                        },
                        failureMessage: Alfresco.util.message("message.details.failure"),
                        scope: this
                    });
                }
            },

            getTableListLayout: function (values) {
                var result = "<ul>";
                if (values) {
                    values.forEach(function (value) {
                        result += "<li>" + value + "</li>";
                    });
                }
                result += "</ul>";
                return result;
            }
        });
})();
