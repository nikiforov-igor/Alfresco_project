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
 * DocumentAttachmentsListTable
 *
 * @namespace LogicECM
 * @class LogicECM.DocumentAttachmentsListTable
 */
(function () {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    /**
     * Alfresco Slingshot aliases
     */
    var $html = Alfresco.util.encodeHTML,
        $links = Alfresco.util.activateLinks,
        $combine = Alfresco.util.combinePaths,
        $userProfile = Alfresco.util.userProfileLink,
        $siteURL = Alfresco.util.siteURL,
        $date = function $date(date, format) { return Alfresco.util.formatDate(Alfresco.util.fromISO8601(date), format); },
        $relTime = Alfresco.util.relativeTime,
        $isValueSet = Alfresco.util.isValueSet;

    /**
     * DocumentAttachmentsListTable constructor.
     *
     * @param {String} htmlId The HTML id of the parent element
     * @return {LogicECM.DocumentAttachmentsListTable} The new DocumentAttachmentsListTable instance
     * @constructor
     */
    LogicECM.DocumentAttachmentsListTable = function DocumentAttachmentsList_constructor(htmlId) {
        LogicECM.DocumentAttachmentsListTable.superclass.constructor.call(this, "LogicECM.DocumentAttachmentsListTable", htmlId);

        this.dataSourceUrl = $combine(Alfresco.constants.URL_SERVICECONTEXT, "components/documentlibrary/data/doclist/");
        this.renderers = {};
	    this.showingMoreActions = false;
        return this;
    };

    YAHOO.extend(LogicECM.DocumentAttachmentsListTable, Alfresco.component.Base);

    /**
     * Augment prototype with Actions module
     */
    YAHOO.lang.augmentProto(LogicECM.DocumentAttachmentsListTable, Alfresco.doclib.Actions);

    /**
     * Generate "changeFilter" event mark-up suitable for element attribute.
     *
     * @method generateFilterMarkup
     * @param filter {object} Object literal containing new filter parameters
     * @return {string} Mark-up for use in node attribute
     */
    LogicECM.DocumentAttachmentsListTable.generateFilterMarkup = function (filter)
    {
        var filterObj = Alfresco.util.cleanBubblingObject(filter);
        return YAHOO.lang.substitute("{filterOwner}|{filterId}|{filterData}|{filterDisplay}", filterObj, function(p_key, p_value, p_meta)
        {
            return typeof p_value === "undefined" ? "" : window.escape(p_value);
        });
    };

    /**
     * Generate "changeFilter" event mark-up specifically for category changes
     *
     * @method generateCategoryMarkup
     * @param category {Array} category[0] is name, category[1] is qnamePath
     * @return {string} Mark-up for use in node attribute
     */
    LogicECM.DocumentAttachmentsListTable.generateCategoryMarkup = function (category)
    {
        return LogicECM.DocumentAttachmentsListTable.generateFilterMarkup(
            {
                filterId: "category",
                filterData: $combine(category[1], category[0])
            });
    };

    /**
     * Generate User Profile link
     *
     * @method generateUserLink
     * @param scope {object} DocumentLibrary instance
     * @param oUser {object} Object literal container user data
     * @return {string} HTML mark-up for user profile link
     */
    LogicECM.DocumentAttachmentsListTable.generateUserLink = function (scope, oUser)
    {
        if (oUser.isDeleted === true)
        {
            return '<span>' + scope.msg("details.user.deleted", $html(oUser.userName)) + '</span>';
        }
        return $userProfile(oUser.userName, YAHOO.lang.trim(oUser.firstName + " " + oUser.lastName));
    };

    /**
     * Generate URL for a file- or folder-link that may be located within a different Site
     *
     * @method generateFileFolderLinkMarkup
     * @param record {object} Item record
     * @return {string} Mark-up for use in node attribute
     * <pre>
     *       Folders: Navigate into the folder (ajax)
     *       Documents: Navigate to the details page (page)
     *    Links: Same site (or Repository mode)
     *       Links to folders: Navigate into the folder (ajax)
     *       Links to documents: Navigate to the details page (page)
     *    Links: Different site
     *       Links to folders: Navigate into the site & folder (page)
     *       Links to documents: Navigate to the details page within the site (page)
     * </pre>
     */
    LogicECM.DocumentAttachmentsListTable.generateFileFolderLinkMarkup = function (scope, record)
    {
        var html = scope.getActionUrls(record).documentDetailsUrl;
        return '<a href="' + html + '">';
    };

	/**
	 * Generate URL to thumbnail image
	 *
	 * @method generateThumbnailUrl
	 * @param record {object} File record
	 * @return {string} URL to thumbnail
	 */
	LogicECM.DocumentAttachmentsListTable.generateThumbnailUrl = function (record)
	{
		var jsNode = record.jsNode,
			nodeRef = jsNode.isLink ? jsNode.linkedNode.nodeRef : jsNode.nodeRef;

		return Alfresco.constants.PROXY_URI + "api/node/" + nodeRef.uri + "/content/thumbnails/doclib?c=queue&ph=true";
	};

    YAHOO.lang.augmentObject(LogicECM.DocumentAttachmentsListTable.prototype,
        {
            /**
             * Object container for initialization options
             *
             * @property options
             * @type object
             */
            options: {

	            /**
	             * Delay time value for "More Actions" popup, in milliseconds
	             *
	             * @property actionsPopupTimeout
	             * @type int
	             * @default 500
	             */
	            actionsPopupTimeout: 700,

	            /**
	             * Delay before showing "loading" message for slow data requests
	             *
	             * @property loadingMessageDelay
	             * @type int
	             * @default 1000
	             */
	            loadingMessageDelay: 1000,

                nodeRef: null,

                path: null,

	            /**
	             * Метка для bubbling. Используется для отрисовки датагрида. Следует передать в datagridMeta
	             */
	            bubblingLabel: null,

	            ignoreActions: [
		            "document-edit-offline",
		            "document-copy-to",
		            "document-move-to",
		            "document-assign-workflow",
		            "document-publish"
	            ]
            },

            renderers: null,

	        showingMoreActions: null,

            /**
             * Fired by YUI when parent element is available for scripting.
             * Template initialisation, including instantiation of YUI widgets and event listener binding.
             *
             * @method onReady
             */
            onReady: function DocumentAttachmentsList_onReady() {
	            var me = this;

                // Set-up default metadata renderers
                this._setupMetadataRenderers();

                // DataSource set-up and event registration
                this._setupDataSource();

                // DataTable set-up and event registration
                this._setupDataTable();

                this._updateDocList();

	            // Hook action events
	            var fnActionHandler = function (layer, args)
	            {
		            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
		            if (owner !== null)
		            {
			            if (typeof me[owner.title] === "function")
			            {
				            args[1].stop = true;
				            var record = me.widgets.dataTable.getRecord(args[1].target.offsetParent).getData();
				            try
				            {
					            me[owner.title].call(me, record, owner);
				            }
				            catch (e)
				            {
					            Alfresco.logger.error("DL_fnActionHandler", owner.title, e);
				            }
			            }
		            }
		            return true;
	            };
	            YAHOO.Bubbling.addDefaultAction("doc-list-action-link" + (me.options.bubblingLabel ? "-"+ me.options.bubblingLabel : ""), fnActionHandler);
	            YAHOO.Bubbling.addDefaultAction("doc-list-show-more" + (me.options.bubblingLabel ? "-"+ me.options.bubblingLabel : ""), fnActionHandler);
            },

            /**
             * Register a metadata renderer
             *
             * @method registerRenderer
             * @param propertyName {string} Property name to attach this renderer to
             * @param renderer {function} Rendering function
             * @return {boolean} Success status of registration
             */
            registerRenderer: function (propertyName, renderer)
            {
                if ($isValueSet(propertyName) && $isValueSet(renderer))
                {
                    this.renderers[propertyName] = renderer;
                    return true;
                }
                return false;
            },

            /**
             * Configure standard metadata renderers
             *
             * @method _setupMetadataRenderers
             */
            _setupMetadataRenderers: function ()
            {
                /**
                 * Date
                 */
                this.registerRenderer("date", function(record, label)
                {
                    var jsNode = record.jsNode,
                        properties = jsNode.properties,
                        html = "";

                    var dateI18N = "modified", dateProperty = properties.modified.iso8601;
                    if (record.workingCopy && record.workingCopy.isWorkingCopy)
                    {
                        dateI18N = "editing-started";
                    }
                    else if (dateProperty === properties.created.iso8601)
                    {
                        dateI18N = "created";
                    }

                    html = '<span class="item">' + label + this.msg("details." + dateI18N + "-by", $relTime(dateProperty), LogicECM.DocumentAttachmentsListTable.generateUserLink(this, properties.modifier)) + '</span>';

                    return html;
                });

                /**
                 * File size
                 */
                this.registerRenderer("size", function(record, label)
                {
                    var jsNode = record.jsNode,
                        properties = jsNode.properties,
                        html = "";

                    if (!jsNode.isContainer && !jsNode.isLink)
                    {
                        html += '<span class="item">' + label + Alfresco.util.formatFileSize(jsNode.size) + '</span>';
                    }

                    return html;
                });

                /**
                 * Description
                 */
                this.registerRenderer("description", function(record, label)
                {
                    var jsNode = record.jsNode,
                        properties = jsNode.properties,
                        id = Alfresco.util.generateDomId(),
                        html = '<span id="' + id + '" class="faded">' + label + this.msg("details.description.none") + '</span>';

                    // Description non-blank?
                    if (properties.description && properties.description !== "")
                    {
                        html = '<span id="' + id + '" class="item">' + label + $links($html(properties.description)) + '</span>';
                    }

                    return html;
                });

                /**
                 * Tags
                 */
                this.registerRenderer("tags", function(record, label)
                {
                    var jsNode = record.jsNode,
                        properties = jsNode.properties,
                        id = Alfresco.util.generateDomId(),
                        html = "";

                    var tags = jsNode.tags, tag;
                    if (jsNode.hasAspect("cm:taggable") && tags.length > 0)
                    {
                        for (var i = 0, j = tags.length; i < j; i++)
                        {
                            tag = $html(tags[i]);
                            html += '<span class="tag"><a href="#" class="tag-link" rel="' + tag + '">' + tag + '</a></span>';
                        }
                    }
                    else
                    {
                        html += '<span class="faded">' + label + this.msg("details.tags.none") + '</span>';
                    }

                    if (jsNode.hasPermission("Write") && !jsNode.isLocked)
                    {
                        // Add the tags insitu editor
                        this.insituEditors.push(
                            {
                                context: id,
                                params:
                                {
                                    type: "tagEditor",
                                    nodeRef: jsNode.nodeRef.toString(),
                                    name: "prop_cm_taggable",
                                    value: record.node.properties["cm:taggable"],
                                    validations: [
                                        {
                                            type: Alfresco.forms.validation.nodeName,
                                            when: "keyup",
                                            message: this.msg("validation-hint.nodeName")
                                        }
                                    ],
                                    title: this.msg("tip.insitu-tag"),
                                    errorMessage: this.msg("message.insitu-edit.tag.failure")
                                },
                                callback:
                                {
                                    fn: this._insituCallback,
                                    scope: this,
                                    obj: record
                                }
                            });
                    }

                    return '<span id="' + id + '" class="item">' + label + html + '</span>';
                });

                /**
                 * Categories
                 */
                this.registerRenderer("categories", function(record, label)
                {
                    var jsNode = record.jsNode,
                        properties = jsNode.properties,
                        html = "";

                    if (jsNode.hasAspect("cm:generalclassifiable"))
                    {
                        var categories = jsNode.categories, category;
                        html += '<span class="category-item item">&nbsp;</span><span class="item">' + label;
                        if (categories.length > 0)
                        {
                            for (var i = 0, j = categories.length; i < j; i++)
                            {
                                category = categories[i];
                                html += '<span class="category"><a href="#" class="filter-change" rel="' + LogicECM.DocumentAttachmentsListTable.generateCategoryMarkup(category) + '">' + $html(category[0]) + '</a></span>' + (j - i > 1 ? ", " : "");
                            }
                        }
                        else
                        {
                            html += '<span class="faded">' + label + this.msg("details.categories.none") + '</span>';
                        }
                        html += '</span>';
                    }

                    return html;
                });
            },

            /**
             * Updates document list by calling data webscript with current site and path
             *
             * @method _updateDocList
             * @param p_obj.filter {object} Optional filter to navigate with
             * @param p_obj.page {string} Optional page to navigate to (defaults to this.currentPage)
             */
            _updateDocList: function ()
            {
                var loadingMessage = null,
                    timerShowLoadingMessage = null,
                    me = this;

                // Clear the current document list if the data webscript is taking too long
                var fnShowLoadingMessage = function ()
                {
                    // Check the timer still exists. This is to prevent IE firing the event after we cancelled it. Which is "useful".
                    if (timerShowLoadingMessage)
                    {
                        loadingMessage = Alfresco.util.PopupManager.displayMessage(
                            {
                                displayTime: 0,
                                text: '<span class="wait">' + $html(this.msg("message.loading")) + '</span>',
                                noEscape: true
                            });

                        if (YAHOO.env.ua.ie > 0)
                        {
                            this.loadingMessageShowing = true;
                        }
                        else
                        {
                            loadingMessage.showEvent.subscribe(function()
                            {
                                this.loadingMessageShowing = true;
                            }, this, true);
                        }
                    }
                };

                // Reset the custom error messages
                this._setDefaultDataTableErrors(this.widgets.dataTable);

                // Reset preview tooltips array
                this.previewTooltips = [];

                this.insituEditors = [];

                // More Actions menu no longer relevant
                this.showingMoreActions = false;

                // Slow data webscript message
                this.loadingMessageShowing = false;

                var destroyLoaderMessage = function ()
                {
                    if (timerShowLoadingMessage)
                    {
                        // Stop the "slow loading" timed function
                        timerShowLoadingMessage.cancel();
                        timerShowLoadingMessage = null;
                    }

                    if (loadingMessage)
                    {
                        if (this.loadingMessageShowing)
                        {
                            // Safe to destroy
                            loadingMessage.destroy();
                            loadingMessage = null;
                        }
                        else
                        {
                            // Wait and try again later. Scope doesn't get set correctly with "this"
                            YAHOO.lang.later(100, me, destroyLoaderMessage);
                        }
                    }
                };

                destroyLoaderMessage();
                timerShowLoadingMessage = YAHOO.lang.later(this.options.loadingMessageDelay, this, fnShowLoadingMessage);

                var successHandler = function (sRequest, oResponse, oPayload)
                {
                    destroyLoaderMessage();

                     this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
                };

                var failureHandler = function (sRequest, oResponse)
                {
                    destroyLoaderMessage();
                    // Clear out deferred functions
                    this.afterDocListUpdate = [];

                    if (oResponse.status === 401)
                    {
                        // Our session has likely timed-out, so refresh to offer the login page
                        window.location.reload(true);
                    }
                    else
                    {
                        try
                        {
                            var fnAfterFailedUpdate = function (responseMsg)
                            {
                                return function ()
                                {
                                    this.widgets.paginator.setState(
                                        {
                                            totalRecords: 0
                                        });
                                    this.widgets.paginator.render();
                                    this.widgets.dataTable.set("MSG_ERROR", responseMsg);
                                    this.widgets.dataTable.showTableMessage(responseMsg, YAHOO.widget.DataTable.CLASS_ERROR);
                                };
                            };

                            this.afterDocListUpdate.push(fnAfterFailedUpdate(YAHOO.lang.JSON.parse(oResponse.responseText).message));
                            this.widgets.dataTable.initializeTable();
                            this.widgets.dataTable.render();
                        }
                        catch(e)
                        {
                            Alfresco.logger.error(e);
                            this._setDefaultDataTableErrors(this.widgets.dataTable);
                        }
                    }
                };

                // Update the DataSource
                var requestParams = this._buildDocListParams();
                Alfresco.logger.debug("DataSource requestParams: ", requestParams);
                this.widgets.dataSource.sendRequest(requestParams,
                    {
                        success: successHandler,
                        failure: failureHandler,
                        scope: this
                    });
            },

            /**
             * Build URI parameter string for doclist JSON data webscript
             *
             * @method _buildDocListParams
             * @param p_obj.page {string} Page number
             * @param p_obj.pageSize {string} Number of items per page
             * @param p_obj.path {string} Path to query
             * @param p_obj.type {string} Filetype to filter: "all", "documents", "folders"
             * @param p_obj.site {string} Current site
             * @param p_obj.container {string} Current container
             * @param p_obj.filter {string} Current filter
             */
            _buildDocListParams: function ()
            {
                // Essential defaults
                var obj =
                {
                    path: this.options.path,
                    type: "documents",
                    site: "",
                    container: "documentLibrary",
                    filter: {
	                    filterId: "path",
	                    filterData: this.options.path,
	                    filterOwner: "Alfresco.DocListTree"
                    }
                };

                // Build the URI stem
                var uriPart =  "node/alfresco/company/home",
                    params = YAHOO.lang.substitute("{type}/" + uriPart + (obj.filter.filterId === "path" ? "{path}" : ""),
                        {
                            type: encodeURIComponent(obj.type),
                            site: encodeURIComponent(obj.site),
                            container: encodeURIComponent(obj.container),
                            path: $combine("/", Alfresco.util.encodeURIPath(obj.path).replace(/%25/g,"%2525"))
                        });

                // Filter parameters
                params += "?filter=" + encodeURIComponent(obj.filter.filterId);
                if (obj.filter.filterData && obj.filter.filterId !== "path")
                {
                    params += "&filterData=" + encodeURIComponent(obj.filter.filterData);
                }

                // Sort parameters
                params += "&sortAsc=true&sortField=" + encodeURIComponent("cm:name");

                // View mode and No-cache
                params += "&view=browse&noCache=" + new Date().getTime();

                return params;
            },

            /**
             * DataSource set-up and event registration
             *
             * @method _setupDataSource
             * @protected
             */
            _setupDataSource: function DocumentAttachmentsList__setupDataSource()
            {
                var me = this;

                // DataSource definition
                this.widgets.dataSource = new YAHOO.util.DataSource($combine(this.dataSourceUrl),
                    {
                        responseType: YAHOO.util.DataSource.TYPE_JSON,
                        responseSchema:
                        {
                            resultsList: "items",
                            metaFields:
                            {
                                paginationRecordOffset: "startIndex",
                                totalRecords: "totalRecords",
                                totalRecordsUpper : "totalRecordsUpper" // if null then totalRecords is accurate else totalRecords is lower estimate (if -1 upper estimate is unknown)
                            }
                        }
                    });

                // Intercept data returned from data webscript to extract custom metadata
                this.widgets.dataSource.doBeforeCallback = function (oRequest, oFullResponse, oParsedResponse)
                {

                    // In documentlist.lib.ftl there are a number of DOM structures that are not displayed, these can
                    // cloned to display the relevant information to the user based on content, display options, site
                    // ownership and access rights. All of theses DOM "snippets" need to be added to a main container
                    // which controls the overall display (of borders, etc).
                    var template = Dom.get(me.id + "-main-template"),
                        main = template.cloneNode(true),
                        container = Dom.getFirstChild(main);

                    // Add a node in with a style of "clear" set to both to ensure that the main div is given
                    // a height to accomodate the floated content...
                    var clearingNode = document.createElement("div");
                    Dom.setStyle(clearingNode, "clear", "both");
                    container.appendChild(clearingNode);

                    // Finally set the innerHTML of the main node as the text string of the YUI datatable
                    me.widgets.dataTable.set("MSG_EMPTY", main.innerHTML);

                    return oParsedResponse;
                };
            },

            /**
             * DataTable set-up and event registration
             *
             * @method _setupDataTable
             * @protected
             */
            _setupDataTable: function ()
            {
                var me = this;

                // DataTable column defintions
                var columnDefinitions =
                    [
                        { key: "nodeRef", label: "Select", sortable: false, formatter: this.fnRenderCellSelected(), width: 16 },
                        { key: "thumbnail", label: "Preview", sortable: false, formatter: this.fnRenderCellThumbnail(), width: 100 },
                        { key: "fileName", label: "Description", sortable: false, formatter: this.fnRenderCellDescription() },
                        { key: "actions", label: "Actions", sortable: false, formatter: this.fnRenderCellActions(), width: 200 }
                ];

                // DataTable definition
                this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-documents", columnDefinitions, this.widgets.dataSource,
                    {
                        renderLoopSize: Alfresco.util.RENDERLOOPSIZE,
                        initialLoad: false,
                        dynamicData: true,
                        MSG_EMPTY: this.msg("message.loading")
                    });

                // Update totalRecords on the fly with value from server
                this.widgets.dataTable.handleDataReturnPayload = function (oRequest, oResponse, oPayload)
                {
                    me.totalRecords = oResponse.meta.totalRecords;
                    me.totalRecordsUpper = oResponse.meta.totalRecordsUpper;
                    return oResponse.meta;
                };

                // Custom error messages
                this._setDefaultDataTableErrors(this.widgets.dataTable);

                // Override abstract function within DataTable to set custom error message
                this.widgets.dataTable.doBeforeLoadData = function (sRequest, oResponse, oPayload)
                {
                    // Clear any existing error
                    this.hideTableMessage();

                    if (oResponse.error)
                    {
                        try
                        {
                            var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                            this.set("MSG_ERROR", response.message);
                        }
                        catch(e)
                        {
                            me._setDefaultDataTableErrors(this);
                        }
                    }
                    else if (oResponse.results.length === 0)
                    {
                        // We don't get an renderEvent for an empty recordSet, but we'd like one anyway
                        this.fireEvent("renderEvent",
                            {
                                type: "renderEvent"
                            });
                    }
                    else
                    {
                        // Add an Alfresco.util.Node instance to each result
                        for (var i = 0, ii = oResponse.results.length; i < ii; i++)
                        {
                            oResponse.results[i].jsNode = new Alfresco.util.Node(oResponse.results[i].node);
                        }
                    }

                    // Must return true to have the "Loading..." message replaced by the error message
                    return true;
                };

	            // Enable row highlighting
	            this.widgets.dataTable.subscribe("rowMouseoverEvent", this.onEventHighlightRow, this, true);
	            this.widgets.dataTable.subscribe("rowMouseoutEvent", this.onEventUnhighlightRow, this, true);
            },

	        /**
	         * Custom event handler to highlight row.
	         *
	         * @method onEventHighlightRow
	         * @param oArgs.event {HTMLEvent} Event object.
	         * @param oArgs.target {HTMLElement} Target element.
	         */
	        onEventHighlightRow: function DL_onEventHighlightRow(oArgs)
	        {
		        // Call through to get the row highlighted by YUI
		        this.widgets.dataTable.onEventHighlightRow.call(this.widgets.dataTable, oArgs);

		        // elActions is the element id of the active table cell where we'll inject the actions
		        var elActions = Dom.get(this.id + "-actions-" + oArgs.target.id);

		        // Inject the correct action elements into the actionsId element
		        if (elActions && elActions.firstChild === null)
		        {
			        // Retrieve the actionSet for this record
			        var oRecord = this.widgets.dataTable.getRecord(oArgs.target.id),
				        record = oRecord.getData(),
				        jsNode = record.jsNode,
				        actions = this.checkActions(record.actions),
				        actionsEl = document.createElement("div"),
				        actionHTML = "",
				        actionsSel;

			        record.actionParams = {};
			        for (var i = 0, ii = actions.length; i < ii; i++)
			        {
				        actionHTML += this.customRenderAction(actions[i], record);
			        }

			        // Token replacement - action Urls
			        actionsEl.innerHTML = YAHOO.lang.substitute(actionHTML, this.getActionUrls(record));

			        // Simple or detailed view
			        Dom.addClass(actionsEl, "action-set");
			        Dom.addClass(actionsEl, "detailed");

			        // Need the "More >" container?
			        var splitAt = jsNode.isContainer ? 2 : 3;
			        actionsSel = YAHOO.util.Selector.query("div", actionsEl);
			        if (actionsSel.length > splitAt + 1)
			        {
				        var moreContainer = Dom.get(this.id + "-moreActions").cloneNode(true),
					        containerDivs = YAHOO.util.Selector.query("div", moreContainer);

				        // Insert the two necessary DIVs before the third action item
				        Dom.insertBefore(containerDivs[0], actionsSel[splitAt]);
				        Dom.insertBefore(containerDivs[1], actionsSel[splitAt]);

				        // Now make action items three onwards children of the 2nd DIV
				        var index, moreActions = actionsSel.slice(splitAt);
				        for (index in moreActions)
				        {
					        if (moreActions.hasOwnProperty(index))
					        {
						        containerDivs[1].appendChild(moreActions[index]);
					        }
				        }
			        }

			        elActions.appendChild(actionsEl);
		        }

		        if (this.showingMoreActions)
		        {
			        this.deferredActionsMenu = elActions;
		        }
		        else if (!Dom.hasClass(document.body, "masked"))
		        {
			        this.currentActionsMenu = elActions;
			        // Show the actions
			        Dom.removeClass(elActions, "hidden");
			        this.deferredActionsMenu = null;
		        }
	        },

	        /**
	         * Custom event handler to unhighlight row.
	         *
	         * @method onEventUnhighlightRow
	         * @param oArgs.event {HTMLEvent} Event object.
	         * @param oArgs.target {HTMLElement} Target element.
	         */
	        onEventUnhighlightRow: function DL_onEventUnhighlightRow(oArgs)
	        {
		        // Call through to get the row unhighlighted by YUI
		        this.widgets.dataTable.onEventUnhighlightRow.call(this.widgets.dataTable, oArgs);

		        var elActions = Dom.get(this.id + "-actions-" + (oArgs.target.id));

		        // Don't hide unless the More Actions drop-down is showing, or a dialog mask is present
		        if ((elActions && !this.showingMoreActions) || Dom.hasClass(document.body, "masked"))
		        {
			        // Just hide the action links, rather than removing them from the DOM
			        Dom.addClass(elActions, "hidden");
			        this.deferredActionsMenu = null;
		        }
	        },

            /**
             * Resets the YUI DataTable errors to our custom messages
             * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
             *
             * @method _setDefaultDataTableErrors
             * @param dataTable {object} Instance of the DataTable
             */
            _setDefaultDataTableErrors: function (dataTable)
            {
                var msg = Alfresco.util.message;
                dataTable.set("MSG_EMPTY", msg("message.empty", "Alfresco.DocumentList"));
                dataTable.set("MSG_ERROR", msg("message.error", "Alfresco.DocumentList"));
            },

            /**
             * DataTable Cell Renderers
             */

            /**
             * Returns selector custom datacell formatter
             *
             * @method fnRenderCellSelected
             */
            fnRenderCellSelected: function ()
            {
                var scope = this;

                /**
                 * Selector custom datacell formatter
                 *
                 * @method renderCellSelected
                 * @param elCell {object}
                 * @param oRecord {object}
                 * @param oColumn {object}
                 * @param oData {object|string}
                 */
                return function (elCell, oRecord, oColumn, oData)
                {
                    Dom.setStyle(elCell, "width", oColumn.width + "px");
                    Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

                    var jsNode = oRecord.getData("jsNode"),
                        nodeRef = jsNode.nodeRef;

                    elCell.innerHTML = '<input id="checkbox-' + oRecord.getId() + '" type="checkbox" name="fileChecked" value="'+ nodeRef + '">';
                };
            },

	        /**
	         * Returns thumbnail custom datacell formatter
	         *
	         * @method fnRenderCellThumbnail
	         */
	        fnRenderCellThumbnail: function DL_fnRenderCellThumbnail()
	        {
		        var scope = this;

		        /**
		         * Thumbnail custom datacell formatter
		         *
		         * @method renderCellThumbnail
		         * @param elCell {object}
		         * @param oRecord {object}
		         * @param oColumn {object}
		         * @param oData {object|string}
		         */
		        return function DL_renderCellThumbnail(elCell, oRecord, oColumn, oData)
		        {
			        var record = oRecord.getData(),
				        node = record.jsNode,
				        name = record.displayName,
				        isLink = node.isLink,
				        extn = name.substring(name.lastIndexOf(".")),
				        imgId = node.nodeRef.nodeRef; // DD added

			        /**
			         * Detailed View
			         */
			        oColumn.width = 100;
			        Dom.setStyle(elCell, "width", oColumn.width + "px");
			        Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

			        elCell.innerHTML = '<span class="thumbnail">' + (isLink ? '<span class="link"></span>' : '') + LogicECM.DocumentAttachmentsListTable.generateFileFolderLinkMarkup(scope, record) + '<img id="' + imgId + '" src="' + LogicECM.DocumentAttachmentsListTable.generateThumbnailUrl(record) + '" alt="' + extn + '" title="' + $html(name) + '" /></a></span>';
		        };
	        },

            /**
             * Returns description/detail custom datacell formatter
             *
             * @method fnRenderCellDescription
             */
            fnRenderCellDescription: function ()
            {
                var scope = this;

                /**
                 * Description/detail custom datacell formatter
                 *
                 * @method renderCellDescription
                 * @param elCell {object}
                 * @param oRecord {object}
                 * @param oColumn {object}
                 * @param oData {object|string}
                 */
                return function (elCell, oRecord, oColumn, oData)
                {
                    var desc = "", i, j,
                        record = oRecord.getData(),
                        jsNode = record.jsNode,
                        properties = jsNode.properties,
                        isContainer = jsNode.isContainer,
                        isLink = jsNode.isLink,
                        title = "",
                        titleHTML = "",
                        version = "",
                        canComment = jsNode.permissions.user.CreateChildren;

                    if (jsNode.isLink)
                    {
                        // Link handling
                        oRecord.setData("displayName", scope.msg("details.link-to", record.location.file));
                    }
                    else if (properties.title && properties.title !== record.displayName)
                    {
                        // Use title property if it's available. Supressed for links.
                        titleHTML = '<span class="title">(' + $html(properties.title) + ')</span>';
                    }

                    // Version display
                    if ($isValueSet(record.version) && !jsNode.isContainer && !jsNode.isLink)
                    {
                        version = '<span class="document-version">' + $html(record.version) + '</span>';
                    }

                    // Locked / Working Copy handling
                    if (($isValueSet(properties.lockOwner) || $isValueSet(properties.workingCopyOwner)) && !jsNode.hasAspect("trx:transferred"))
                    {
                        var bannerUser = properties.lockOwner || properties.workingCopyOwner,
                            bannerLink = LogicECM.DocumentAttachmentsListTable.generateUserLink(scope, bannerUser);

                        /* Google Docs Integration */
                        if (record.workingCopy)
                        {
                            if ($isValueSet(record.workingCopy.googleDocUrl))
                            {
                                if (bannerUser.userName === Alfresco.constants.USERNAME)
                                {
                                    desc += '<div class="info-banner">' + scope.msg("details.banner.google-docs-owner", '<a href="' + record.workingCopy.googleDocUrl + '" target="_blank">' + scope.msg("details.banner.google-docs.link") + '</a>') + '</div>';
                                }
                                else
                                {
                                    desc += '<div class="info-banner">' + scope.msg("details.banner.google-docs-locked", bannerLink, '<a href="' + record.workingCopy.googleDocUrl + '" target="_blank">' + scope.msg("details.banner.google-docs.link") + '</a>') + '</div>';
                                }
                            }
                            else
                            {
                                /* Regular Working Copy handling */
                                if (bannerUser.userName === Alfresco.constants.USERNAME)
                                {
                                    desc += '<div class="info-banner">' + scope.msg("details.banner." + (record.workingCopy.isWorkingCopy ? "editing" : "lock-owner")) + '</div>';
                                }
                                else
                                {
                                    desc += '<div class="info-banner">' + scope.msg("details.banner.locked", bannerLink) + '</div>';
                                }
                            }
                        }
                        else
                        {
                            /* Regular Locked handling */
                            if (bannerUser.userName === Alfresco.constants.USERNAME)
                            {
                                desc += '<div class="info-banner">' + scope.msg("details.banner.lock-owner") + '</div>';
                            }
                            else
                            {
                                desc += '<div class="info-banner">' + scope.msg("details.banner.locked", bannerLink) + '</div>';
                            }
                        }
                    }

                    // Insitu editing for title (filename)
                    var filenameId = Alfresco.util.generateDomId();
                    if (jsNode.hasPermission("Write") && !jsNode.isLocked)
                    {
                        scope.insituEditors.push(
                            {
                                context: filenameId,
                                params:
                                {
                                    type: "textBox",
                                    nodeRef: jsNode.nodeRef.toString(),
                                    name: "prop_cm_name",
                                    value: record.fileName,
                                    fnSelect: function fnSelect(elInput, value)
                                    {
                                        // If the file has an extension, omit it from the edit selection
                                        var extnPos = value.lastIndexOf(Alfresco.util.getFileExtension(value)) - 1;
                                        if (extnPos > 0)
                                        {
                                            Alfresco.util.selectText(elInput, 0, extnPos);
                                        }
                                        else
                                        {
                                            elInput.select();
                                        }
                                    },
                                    validations: [
                                        {
                                            type: Alfresco.forms.validation.nodeName,
                                            when: "keyup",
                                            message: scope.msg("validation-hint.nodeName")
                                        },
                                        {
                                            type: Alfresco.forms.validation.length,
                                            args: { min: 1, max: 255, crop: true },
                                            when: "keyup",
                                            message: scope.msg("validation-hint.length.min.max", 1, 255)
                                        }],
                                    title: scope.msg("tip.insitu-rename"),
                                    errorMessage: scope.msg("message.insitu-edit.name.failure")
                                },
                                callback:
                                {
                                    fn: scope._insituCallback,
                                    scope: scope,
                                    obj: record
                                }
                            });
                    }

                    /* Title */
                    desc += '<h3 class="filename"><span id="' + filenameId + '">'+ LogicECM.DocumentAttachmentsListTable.generateFileFolderLinkMarkup(scope, record);
                    desc += $html(record.displayName) + '</a></span>' + titleHTML + version + '</h3>';

                    /**
                     *  Render using metadata template
                     */
                    var metadataTemplate = record.metadataTemplate;
                    if (metadataTemplate && YAHOO.lang.isArray(metadataTemplate.lines))
                    {
                        var fnRenderTemplate = function fnRenderTemplate_substitute(p_key, p_value, p_meta)
                        {
                            var label = (p_meta !== null ? '<em>' + scope.msg(p_meta) + '</em>: ': ''),
                                value = "";

                            // render value from properties or custom renderer
                            if (scope.renderers.hasOwnProperty(p_key) && typeof scope.renderers[p_key] === "function")
                            {
                                value = scope.renderers[p_key].call(scope, record, label);
                            }
                            else
                            {
                                value = '<span class="item">' + label + $html(jsNode.properties[p_key]) + '</span>';
                            }

                            return value;
                        };

                        var html, line;
                        for (i = 0, j = metadataTemplate.lines.length; i < j; i++)
                        {
                            line = metadataTemplate.lines[i];
                            if (line.template != "{social}" && (!$isValueSet(line.view) || line.view == "detailed"))
                            {
                                html = YAHOO.lang.substitute(line.template, scope.renderers, fnRenderTemplate);
                                if ($isValueSet(html))
                                {
                                    desc += '<div class="detail">' + html + '</div>';
                                }
                            }
                        }
                    }

                    elCell.innerHTML = desc;
                };
            },

	        /**
	         * Returns actions custom datacell formatter
	         *
	         * @method fnRenderCellActions
	         */
	        fnRenderCellActions: function DL_fnRenderCellActions()
	        {
		        var scope = this;

		        /**
		         * Actions custom datacell formatter
		         *
		         * @method renderCellActions
		         * @param elCell {object}
		         * @param oRecord {object}
		         * @param oColumn {object}
		         * @param oData {object|string}
		         */
		        return function DL_renderCellActions(elCell, oRecord, oColumn, oData)
		        {
			        oColumn.width = 200;
			        Dom.setStyle(elCell, "width", oColumn.width + "px");
			        Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
			        Dom.addClass(elCell.parentNode, oRecord.getData("type"));

			        elCell.innerHTML = '<div id="' + scope.id + '-actions-' + oRecord.getId() + '" class="hidden"></div>';
		        };
	        },

	        onActionShowMore: function DL_onActionShowMore(record, elMore)
	        {
		        var me = this;

		        // Fix "More Actions" hover style
		        Dom.addClass(elMore.firstChild, "highlighted");

		        // Get the pop-up div, sibling of the "More Actions" link
		        var elMoreActions = Dom.getNextSibling(elMore);
		        Dom.removeClass(elMoreActions, "hidden");
		        me.showingMoreActions = true;

		        // Hide pop-up timer function
		        var fnHidePopup = function DL_oASM_fnHidePopup()
		        {
			        // Need to rely on the "elMoreActions" enclosed variable, as MSIE doesn't support
			        // parameter passing for timer functions.
			        Event.removeListener(elMoreActions, "mouseover");
			        Event.removeListener(elMoreActions, "mouseout");
			        Dom.removeClass(elMore.firstChild, "highlighted");
			        Dom.addClass(elMoreActions, "hidden");
			        me.showingMoreActions = false;
			        if (me.deferredActionsMenu !== null)
			        {
				        Dom.addClass(me.currentActionsMenu, "hidden");
				        me.currentActionsMenu = me.deferredActionsMenu;
				        me.deferredActionsMenu = null;
				        Dom.removeClass(me.currentActionsMenu, "hidden");
			        }
		        };

		        // Initial after-click hide timer - 4x the mouseOut timer delay
		        if (elMoreActions.hideTimerId)
		        {
			        window.clearTimeout(elMoreActions.hideTimerId);
		        }
		        elMoreActions.hideTimerId = window.setTimeout(fnHidePopup, me.options.actionsPopupTimeout * 4);

		        // Mouse over handler
		        var onMouseOver = function DLSM_onMouseOver(e, obj)
		        {
			        // Clear any existing hide timer
			        if (obj.hideTimerId)
			        {
				        window.clearTimeout(obj.hideTimerId);
				        obj.hideTimerId = null;
			        }
		        };

		        // Mouse out handler
		        var onMouseOut = function DLSM_onMouseOut(e, obj)
		        {
			        var elTarget = Event.getTarget(e);
			        var related = elTarget.relatedTarget;

			        // In some cases we should ignore this mouseout event
			        if ((related !== obj) && (!Dom.isAncestor(obj, related)))
			        {
				        if (obj.hideTimerId)
				        {
					        window.clearTimeout(obj.hideTimerId);
				        }
				        obj.hideTimerId = window.setTimeout(fnHidePopup, me.options.actionsPopupTimeout);
			        }
		        };

		        Event.on(elMoreActions, "mouseover", onMouseOver, elMoreActions);
		        Event.on(elMoreActions, "mouseout", onMouseOut, elMoreActions);
	        },

	        /**
	         * Renders a single action for a given record.
	         * Callers should then use
	         * <pre>
	         *    YAHOO.lang.substitute(actionHTML, this.getActionUrls(record))
	         * </pre>
	         * on the final concatenated HTML for multiple actions to populate placeholder URLs.
	         *
	         * @method renderAction
	         * @param p_action {object} Object literal representing the node
	         * @param p_record {string} Optional siteId override for site-based locations
	         * @return {string} HTML containing action markup
	         */
	        customRenderAction: function dlA_renderAction(p_action, p_record)
	        {
		        var urlContext = Alfresco.constants.URL_RESCONTEXT + "components/documentlibrary/actions/",
			        iconStyle = 'style="background-image:url(' + urlContext + '{icon}-16.png)" ',
			        actionTypeMarkup =
			        {
				        "link": '<div class="{id}"><a title="{label}" class="simple-link" href="{href}" ' + iconStyle + '{target}><span>{label}</span></a></div>',
				        "pagelink": '<div class="{id}"><a title="{label}" class="simple-link" href="{pageUrl}" ' + iconStyle + '><span>{label}</span></a></div>',
				        "javascript": '<div class="{id}" title="{jsfunction}"><a title="{label}" class="doc-list-action-link doc-list-action-link' + (this.options.bubblingLabel ? "-"+ this.options.bubblingLabel : "") + '" href="#"' + iconStyle + '><span>{label}</span></a></div>'
			        };

		        // Store quick look-up for client-side actions
		        p_record.actionParams[p_action.id] = p_action.params;

		        var markupParams =
		        {
			        "id": p_action.id,
			        "icon": p_action.icon,
			        "label": Alfresco.util.substituteDotNotation(this.msg(p_action.label), p_record)
		        };

		        // Parameter substitution for each action type
		        if (p_action.type === "link")
		        {
			        if (p_action.params.href)
			        {
				        markupParams.href = Alfresco.util.substituteDotNotation(p_action.params.href, p_record);
				        markupParams.target = p_action.params.target ? "target=\"" + p_action.params.target + "\"" : "";
			        }
			        else
			        {
				        Alfresco.logger.warn("Action configuration error: Missing 'href' parameter for actionId: ", p_action.id);
			        }
		        }
		        else if (p_action.type === "pagelink")
		        {
			        if (p_action.params.page)
			        {
				        var recordSiteName = $isValueSet(p_record.location.site) ? p_record.location.site.name : null;
				        markupParams.pageUrl = $siteURL(Alfresco.util.substituteDotNotation(p_action.params.page, p_record),
					        {
						        site: recordSiteName
					        });
			        }
			        else
			        {
				        Alfresco.logger.warn("Action configuration error: Missing 'page' parameter for actionId: ", p_action.id);
			        }
		        }
		        else if (p_action.type === "javascript")
		        {
			        if (p_action.params["function"])
			        {
				        markupParams.jsfunction = p_action.params["function"];
			        }
			        else
			        {
				        Alfresco.logger.warn("Action configuration error: Missing 'function' parameter for actionId: ", p_action.id);
			        }
		        }

		        return YAHOO.lang.substitute(actionTypeMarkup[p_action.type], markupParams);
	        },
	        checkActions: function(actions) {
		        var result = [];
		        if (actions != null) {
			        for (var i = 0; i < actions.length; i++) {
				        var action = actions[i];
				        var show = true;
				        for (var j = 0; j < this.options.ignoreActions.length; j++) {
					        if (action.id == this.options.ignoreActions[j]) {
						        show = false;
					        }
				        }
				        if (show) {
					        result.push(action);
				        }
			        }
		        }
		        return result;
	        }
        }, true);
})();