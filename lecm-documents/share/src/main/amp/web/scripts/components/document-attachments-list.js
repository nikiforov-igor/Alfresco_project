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
 * DocumentAttachmentsList
 *
 * @namespace LogicECM
 * @class LogicECM.DocumentAttachmentsList
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
     * DocumentAttachmentsList constructor.
     *
     * @param {String} htmlId The HTML id of the parent element
     * @return {LogicECM.DocumentAttachmentsList} The new DocumentAttachmentsList instance
     * @constructor
     */
    LogicECM.DocumentAttachmentsList = function DocumentAttachmentsList_constructor(htmlId) {
        LogicECM.DocumentAttachmentsList.superclass.constructor.call(this, "LogicECM.DocumentAttachmentsList", htmlId);

        this.dataSourceUrl = $combine(Alfresco.constants.URL_SERVICECONTEXT, "lecm/document/data/doclistAttachments/");
        this.renderers = {};
	    this.showingMoreActions = false;
	    this.dragEventRefCount = 0;

	    YAHOO.Bubbling.on("listRefresh", this.onDocListRefresh, this);
	    YAHOO.Bubbling.on("fileRenamed", this.onFileRenamed, this);
	    YAHOO.Bubbling.on("fileDeleted", this.onFileAction, this);
        return this;
    };

    YAHOO.extend(LogicECM.DocumentAttachmentsList, Alfresco.component.Base);

    /**
     * Augment prototype with Actions module
     */
    YAHOO.lang.augmentProto(LogicECM.DocumentAttachmentsList, Alfresco.doclib.Actions);

    /**
     * Generate User Profile link
     *
     * @method generateUserLink
     * @param scope {object} DocumentLibrary instance
     * @param oUser {object} Object literal container user data
     * @return {string} HTML mark-up for user profile link
     */
    LogicECM.DocumentAttachmentsList.generateUserLink = function (scope, oUser)
    {
        if (oUser.isDeleted === true)
        {
            return '<span>' + scope.msg("details.user.deleted", $html(oUser.userName)) + '</span>';
        }
        return $userProfile(oUser.userName, YAHOO.lang.trim(oUser.firstName + " " + oUser.lastName));
    };

    LogicECM.DocumentAttachmentsList.generateLECMUserLink = function (scope, oUser, oUserRef)
    {
        if (oUserRef != null) {
            return "<span><a href='javascript:void(0);'" + " onclick=\"LogicECM.module.Base.Util.viewAttributes({itemId:\'" + oUserRef + "\'})\">" + (oUser.userName ? oUser.userName : oUser) + "</a></span>";
        } else {
            return $userProfile(oUser.userName, YAHOO.lang.trim(oUser.firstName + " " + oUser.lastName));
        }
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
    LogicECM.DocumentAttachmentsList.generateFileFolderLinkMarkup = function (scope, record)
    {
        var html = Alfresco.constants.URL_PAGECONTEXT + "document-attachment?nodeRef=" + record.jsNode.nodeRef;

        if (scope.options.nodeRef.indexOf("base-document-attachments") == 0) {
            return '<a href="' + html + '" target="_blank">';
        } else {
            return '<a  href="' + html + '">';
        }
    };

	/**
	 * Generate URL to thumbnail image
	 *
	 * @method generateThumbnailUrl
	 * @param record {object} File record
	 * @return {string} URL to thumbnail
	 */
	LogicECM.DocumentAttachmentsList.generateThumbnailUrl = function (record)
	{
		var jsNode = record.jsNode,
			nodeRef = jsNode.isLink ? jsNode.linkedNode.nodeRef : jsNode.nodeRef;

		return Alfresco.constants.PROXY_URI + "api/node/" + nodeRef.uri + "/content/thumbnails/doclib?c=queue&ph=true";
	};

    YAHOO.lang.augmentObject(LogicECM.DocumentAttachmentsList.prototype,
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

	            categoryName: null,

	            /**
	             * Метка для bubbling. Используется для отрисовки датагрида. Следует передать в datagridMeta
	             */
	            bubblingLabel: null,

	            showFileFolderLink: false,

	            hasDeleteOwnAttachmentPerm: false,

	            hasAddAttachmentPerm: false,

				uploaderDialogHeaderTemplate: Alfresco.component.Base.prototype.msg("select.category"),

	            showActions: [
		            {
			            id: "document-download",
			            onlyForOwn: false
		            },
		            {
			            id: "document-view-content",
			            onlyForOwn: false
		            },
		            {
			            id: "document-edit-properties",
			            onlyForOwn: false
		            },
		            {
			            id: "document-upload-new-version",
			            onlyForOwn: false
		            },
		            {
			            id: "document-delete",
			            onlyForOwn: false
		            },
                    {
                        id: "move-to-another-category",
                        onlyForOwn: false
                    }
	            ]
            },

            renderers: null,

	        showingMoreActions: null,

	        fileUpload: null,

	        dragEventRefCount: null,

            /**
             * Fired by YUI when parent element is available for scripting.
             * Template initialisation, including instantiation of YUI widgets and event listener binding.
             *
             * @method onReady
             */
            onReady: function DocumentAttachmentsList_onReady() {
	            var me = this;

	            Alfresco.util.createYUIButton(this, "fileUpload-button", this.onFileUpload,
		            {
			            disabled: false,
			            value: "CreateChildren"
		            });

//	            Alfresco.util.createYUIButton(this, "addLink-button", this.onAddLink,
//		            {
//			            disabled: false,
//			            value: "CreateChildren"
//		            });

	            // DocLib Actions module
	            this.modules.actions = new Alfresco.module.DoclibActions();

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
					            Alfresco.logger.error("DocumentAttachmentsList_fnActionHandler", owner.title, e);
				            }
			            }
		            }
		            return true;
	            };
	            YAHOO.Bubbling.addDefaultAction("doc-list-action-link" + (me.options.bubblingLabel ? "-"+ me.options.bubblingLabel : ""), fnActionHandler);
	            YAHOO.Bubbling.addDefaultAction("doc-list-show-more" + (me.options.bubblingLabel ? "-"+ me.options.bubblingLabel : ""), fnActionHandler);

	            this._addDragAndDrop();
            },

	        onFileUpload: function DocumentAttachmentsList_onFileUpload(e, obj)
	        {
				var uploaderDialogTitle = YAHOO.lang.substitute(this.options.uploaderDialogHeaderTemplate, {
					categoryName: this.options.categoryName
				});

		        if (this.fileUpload == null)
		        {
			        this.fileUpload = Alfresco.getFileUploadInstance();
		        }

		        var multiUploadConfig =
		        {
			        destination: this.options.nodeRef,
			        filter: [],
			        mode: this.fileUpload.MODE_MULTI_UPLOAD,
			        thumbnails: "doclib",
			        onFileUploadComplete:
			        {
				        fn: function() {
                            YAHOO.Bubbling.fire("listRefresh",{});
                        },
				        scope: this
			        },
                    suppressRefreshEvent: true
		        };
		        this.fileUpload.show(multiUploadConfig);
				if (this.fileUpload.uploader.titleText) {
					this.fileUpload.uploader.titleText.innerHTML = uploaderDialogTitle;
				} else if (this.fileUpload.uploader.widgets && this.fileUpload.uploader.widgets.panel) {
					this.fileUpload.uploader.widgets.panel.setHeader(uploaderDialogTitle);
				}
		        Event.preventDefault(e);
	        },

	        onAddLink: function() {
		        var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
		        var templateRequestParams = {
			        itemKind: "node",
			        itemId: this.options.nodeRef,
			        mode: "edit",
			        submitType: "json",
			        showCancelButton: true
		        };

		        new Alfresco.module.SimpleDialog(this.id + "-addLink").setOptions({
			        width:"60em",
			        templateUrl:templateUrl,
			        templateRequestParams:templateRequestParams,
			        actionUrl:null,
			        destroyOnHide:true,
			        doBeforeDialogShow:{
				        fn: function(p_form, p_dialog) {
					        var fileSpan = '<span class="light">' + this.msg("title.add_attachment") + '</span>';
					        Alfresco.util.populateHTML(
						        [ p_dialog.id + "-form-container_h", fileSpan]
					        );
					        p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
				        },
				        scope: this
			        }
		        }).show();
	        },

            /**
             * Register a metadata renderer
             *
             * @method registerRenderer
             * @param propertyName {string} Property name to attach this renderer to
             * @param renderer {function} Rendering function
             * @return {boolean} Success status of registration
             */
            registerRenderer: function DocumentAttachmentsList_registerRenderer(propertyName, renderer)
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
            _setupMetadataRenderers: function DocumentAttachmentsList__setupMetadataRenderers()
            {
                /**
                 * Date
                 */
                this.registerRenderer("date", function(record, label)
                {
                    var jsNode = record.jsNode,
                        properties = jsNode.properties;

                    var dateI18N = "modified", dateProperty = properties.modified.iso8601;
                    if (record.workingCopy && record.workingCopy.isWorkingCopy)
                    {
                        dateI18N = "editing-started";
                    }
                    else if (dateProperty === properties.created.iso8601)
                    {
                        dateI18N = "created";
                    }
                    var modifier = properties["lecm-document:modifier"];
                    var modifierRef = properties["lecm-document:modifier-ref"];
                    var userLink = modifier ?
                        LogicECM.DocumentAttachmentsList.generateLECMUserLink(this, modifier, modifierRef) :
                        LogicECM.DocumentAttachmentsList.generateUserLink(this, properties.modifier);
                    html = '<span class="item">' + label + this.msg("details." + dateI18N + "-by", $relTime(dateProperty), userLink) + '</span>';

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
            },

            /**
             * Updates document list by calling data webscript with current site and path
             *
             * @method _updateDocList
             * @param p_obj.filter {object} Optional filter to navigate with
             * @param p_obj.page {string} Optional page to navigate to (defaults to this.currentPage)
             */
            _updateDocList: function DocumentAttachmentsList__updateDocList()
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
                            loadingMessage.destroyWithAnimationsStop();
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

            _buildDocListParams: function DocumentAttachmentsList__buildDocListParams()
            {
                // Build the URI stem
                params = YAHOO.lang.substitute("documents/node/" + this.options.nodeRef.replace(":/", ""));

                // View mode and No-cache
                params += "?view=attachment&noCache=" + new Date().getTime();

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
                            resultsList: "items"
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
            _setupDataTable: function DocumentAttachmentsList__setupDataTable()
            {
                var me = this;

                // DataTable column defintions
                var columnDefinitions =
                    [
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

                this.widgets.dataTable.handleDataReturnPayload = function (oRequest, oResponse, oPayload)
                {
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
	            this.widgets.dataTable.subscribe("rowMouseoverEvent", this.widgets.dataTable.onEventHighlightRow, null, this.widgets.dataTable);
	            this.widgets.dataTable.subscribe("rowMouseoutEvent", this.widgets.dataTable.onEventUnhighlightRow, null, this.widgets.dataTable);
	            this.widgets.dataTable.subscribe("rowHighlightEvent", this.onEventHighlightRow, this, true);
	            this.widgets.dataTable.subscribe("rowUnhighlightEvent", this.onEventUnhighlightRow, this, true);
            },

	        /**
	         * Custom event handler to highlight row.
	         *
	         * @method onEventHighlightRow
             * @param oArgs.el {HTMLElement} The highlighted TR element.
             * @param oArgs.record {YAHOO.widget.Record} The highlighted Record.
	         */
	        onEventHighlightRow: function DocumentAttachmentsList_onEventHighlightRow(oArgs)
	        {
		        // Call through to get the row highlighted by YUI
		        //this.widgets.dataTable.onEventHighlightRow.call(this.widgets.dataTable, oArgs);

		        // elActions is the element id of the active table cell where we'll inject the actions
		        var elActions = Dom.get(this.id + "-actions-" + oArgs.el.id);

		        // Inject the correct action elements into the actionsId element
		        if (elActions && elActions.firstChild === null)
		        {
			        // Retrieve the actionSet for this record
			        //var oRecord = this.widgets.dataTable.getRecord(oArgs.target.id),
			        var oRecord = oArgs.record,
				        record = oRecord.getData(),
				        jsNode = record.jsNode,
				        actions = this.checkActions(record),
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
             * @param oArgs.el {HTMLElement} The highlighted TR element.
             * @param oArgs.record {YAHOO.widget.Record} The highlighted Record.
	         */
	        onEventUnhighlightRow: function DocumentAttachmentsList_onEventUnhighlightRow(oArgs)
	        {
		        // Call through to get the row unhighlighted by YUI
		        //this.widgets.dataTable.onEventUnhighlightRow.call(this.widgets.dataTable, oArgs);

		        var elActions = Dom.get(this.id + "-actions-" + (oArgs.el.id));

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
            fnRenderCellSelected: function DocumentAttachmentsList_fnRenderCellSelected()
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
	        fnRenderCellThumbnail: function DocumentAttachmentsList_fnRenderCellThumbnail()
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
		        return function DocumentAttachmentsList_renderCellThumbnail(elCell, oRecord, oColumn, oData)
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

			        if (scope.options.showFileFolderLink) {
				        elCell.innerHTML = '<span class="thumbnail">' + (isLink ? '<span class="link"></span>' : '') + LogicECM.DocumentAttachmentsList.generateFileFolderLinkMarkup(scope, record) + '<img id="' + imgId + '" src="' + LogicECM.DocumentAttachmentsList.generateThumbnailUrl(record) + '" alt="' + extn + '" title="' + $html(name) + '" /></a></span>';
			        } else {
				        elCell.innerHTML = '<span class="thumbnail">' + (isLink ? '<span class="link"></span>' : '') + '<img id="' + imgId + '" src="' + LogicECM.DocumentAttachmentsList.generateThumbnailUrl(record) + '" alt="' + extn + '" title="' + $html(name) + '" /></span>';
			        }
		        };
	        },

            /**
             * Returns description/detail custom datacell formatter
             *
             * @method fnRenderCellDescription
             */
            fnRenderCellDescription: function DocumentAttachmentsList_fnRenderCellDescription()
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
                        title = "",
                        titleHTML = "",
                        linkImage = "",
                        version = "";

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

                    // Link image
                    if ($isValueSet(record.isInnerAttachment) && record.isInnerAttachment == "false") {
	                    var src =  Alfresco.constants.URL_RESCONTEXT + "images/lecm-documents/link_attachment.png";
	                    var title = scope.msg("title.attachment.isLink");
	                    linkImage += "<img src='" + src + "' title='" + title + "'/>";
                    }

                    // Locked / Working Copy handling
                    if (($isValueSet(properties.lockOwner) || $isValueSet(properties.workingCopyOwner)) && !jsNode.hasAspect("trx:transferred"))
                    {
                        var bannerUser = properties.lockOwner || properties.workingCopyOwner,
                            bannerLink = LogicECM.DocumentAttachmentsList.generateUserLink(scope, bannerUser);

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
	                if (scope.options.showFileFolderLink) {
		                desc += '<h3 class="filename"><span id="' + filenameId + '">'+ LogicECM.DocumentAttachmentsList.generateFileFolderLinkMarkup(scope, record);
		                desc += $html(record.displayName) + '</a></span>' + titleHTML + version + linkImage + '</h3>';
	                } else {
		                desc += '<h3 class="filename"><span id="' + filenameId + '">';
		                desc += $html(record.displayName) + '</span>' + titleHTML + version + linkImage + '</h3>';
	                }

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
	        fnRenderCellActions: function DocumentAttachmentsList_fnRenderCellActions()
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
		        return function DocumentAttachmentsList_renderCellActions(elCell, oRecord, oColumn, oData)
		        {
			        oColumn.width = 200;
			        Dom.setStyle(elCell, "width", oColumn.width + "px");
			        Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
			        Dom.addClass(elCell.parentNode, oRecord.getData("type"));

			        elCell.innerHTML = '<div id="' + scope.id + '-actions-' + oRecord.getId() + '" class="hidden"></div>';
		        };
	        },

	        onActionShowMore: function DocumentAttachmentsList_onActionShowMore(record, elMore)
	        {
		        var me = this;

		        // Fix "More Actions" hover style
		        Dom.addClass(elMore.firstChild, "highlighted");

		        // Get the pop-up div, sibling of the "More Actions" link
		        var elMoreActions = Dom.getNextSibling(elMore);
		        Dom.removeClass(elMoreActions, "hidden");
		        me.showingMoreActions = true;

		        // Hide pop-up timer function
		        var fnHidePopup = function DocumentAttachmentsList_oASM_fnHidePopup()
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
	        checkActions: function DocumentAttachmentsList_checkActions(record) {
		        var result = [];
		        var actions = record.actions;
		        if (actions != null) {
			        for (var i = 0; i < actions.length; i++) {
				        var action = actions[i];
				        var show = false;
				        for (var j = 0; j < this.options.showActions.length; j++) {
					        if (action.id == this.options.showActions[j].id &&
						        (!this.options.showActions[j].onlyForOwn ||
							        (record.node != null && record.node.properties["cm:creator"] != null
								        && record.node.properties["cm:creator"].userName == Alfresco.constants.USERNAME))) {
						        show = true;
					        }
				        }
				        if (show) {
					        result.push(action);
				        }
			        }
		        }
		        return result;
	        },

	        /**
	         * DocList Refresh Required event handler
	         *
	         * @method onDocListRefresh
	         * @param layer {object} Event fired (unused)
	         * @param args {array} Event parameters (unused)
	         */
	        onDocListRefresh: function DocumentAttachmentsList_onDocListRefresh(layer, args)
	        {
		        var obj = args[1];
		        if (obj && obj.highlightFile)
		        {
			        this.options.highlightFile = obj.highlightFile;
		        }
		        this._updateDocList.call(this);
	        },

	        /**
	         * Searches the current recordSet for a record with the given parameter value
	         *
	         * @private
	         * @method _findRecordByParameter
	         * @param p_value {string} Value to find
	         * @param p_parameter {string} Parameter to look for the value in
	         * @return {YAHOO.widget.Record} Successful search result or null
	         */
	        _findRecordByParameter: function DocumentAttachmentsList__findRecordByParameter(p_value, p_parameter)
	        {
		        var oRecordSet = this.widgets.dataTable.getRecordSet(),
			        oRecord, record, i, j;

		        for (i = 0, j = oRecordSet.getLength(); i < j; i++)
		        {
			        oRecord = oRecordSet.getRecord(i);
			        record = oRecord.getData();

			        if (record[p_parameter] === p_value || (record.node && record.node[p_parameter] === p_value))
			        {
				        return oRecord;
			        }
		        }
		        return null;
	        },

	        /**
	         * File or folder renamed event handler
	         *
	         * @method onFileRenamed
	         * @param layer {object} Event fired
	         * @param args {array} Event parameters (depends on event type)
	         */
	        onFileRenamed: function DocumentAttachmentsList_onFileRenamed(layer, args)
	        {
		        var obj = args[1];
		        if (obj && (obj.file !== null))
		        {
			        var recordFound = this._findRecordByParameter(obj.file.node.nodeRef, "nodeRef");
			        if (recordFound !== null)
			        {
				        this.widgets.dataTable.updateRow(recordFound, obj.file);
				        var el = this.widgets.dataTable.getTrEl(recordFound);
				        Alfresco.util.Anim.pulse(el);
			        }
		        }
	        },

	        /**
	         * Generic file action event handler
	         *
	         * @method onFileAction
	         * @param layer {object} Event fired
	         * @param args {array} Event parameters (depends on event type)
	         */
	        onFileAction: function DocumentAttachmentsList_onFileAction(layer, args)
	        {
		        var obj = args[1];
		        if (obj)
		        {
			        if (!obj.multiple)
			        {
				        this._updateDocList.call(this);
			        }
		        }
	        },

	        /**
	         * Adds HTML5 drag and drop listeners to the document list.
	         *
	         * @method _addDragAndDrop
	         */
	        _addDragAndDrop: function DL__addDragAndDrop()
	        {
		        if (this.options.hasAddAttachmentPerm) {
			        // Make the entire DocumentList available for dropping files for uploading onto.
			        try
			        {
				        // Add listeners to the HTML5 drag and drop events fired from the entire doc list
				        var doclist = Dom.get(this.id);
				        Event.addListener(doclist, "dragenter", this.onDocumentListDragEnter, this, true);
				        Event.addListener(doclist, "dragover", this.onDocumentListDragOver, this, true);
				        Event.addListener(doclist, "dragleave", this.onDocumentListDragLeave, this, true);
				        Event.addListener(doclist, "drop", this.onDocumentListDrop, this, true);
			        }
			        catch(exception)
			        {
				        Alfresco.logger.error("_addDragAndDrop: The following exception occurred: ", exception);
			        }
		        }
	        },

	        /**
	         * Fired when an object is dragged into the DocumentList DOM element and then again when dragged into a Folder
	         * icon image DOM element.
	         *
	         * @param e {object} HTML5 drag and drop event
	         * @method onDocumentListDragEnter
	         */
	        onDocumentListDragEnter: function DocumentAttachmentsList_onDocumentListDragEnter(e) {
		        var containerEl = this.widgets.dataTable.getContainerEl();

		        // Firefox is a bit buggy with it's enter/leave event matching
		        this.dragEventRefCount = Math.min(++this.dragEventRefCount, 2);
		        Dom.addClass(containerEl, "dndDocListHighlight");

		        e.stopPropagation();
		        e.preventDefault();
	        },

	        /**
	         * Fired when an object is dragged into the DocumentList DOM element and then again when dragged into a Folder
	         * icon image DOM element.
	         *
	         * @param e {object} HTML5 drag and drop event
	         * @method onDocumentListDragOver
	         */
	        onDocumentListDragOver: function DocumentAttachmentsList_onDocumentListDragOver(e)
	        {
		        // Firefox 3.6 set effectAllowed = "move" for files, however the "copy" effect is more accurate for uploads
		        e.dataTransfer.dropEffect = Math.floor(YAHOO.env.ua.gecko) === 1 ? "move" : "copy";
		        e.stopPropagation();
		        e.preventDefault();
	        },

	        /**
	         * Fired when an object is dragged out of the DocumentList DOM element or the Folder icon image DOM element.
	         *
	         * @param e {object} HTML5 drag and drop event
	         * @method onDocumentListDragLeave
	         */
	        onDocumentListDragLeave: function DocumentAttachmentsList_onDocumentListDragLeave(e)
	        {
		        // Providing that the drop target a <TR> (or a child node of a <TR>) in the DocumentList data table then a record
		        // will be returned from this call. If nothing is returned then we cannot proceed with the file upload operation.
		        if (--this.dragEventRefCount === 0)
		        {
			        Dom.removeClass(this.widgets.dataTable.getContainerEl(), "dndDocListHighlight");
		        }

		        e.stopPropagation();
		        e.preventDefault();
	        },

	        /**
	         * Fired when an object is dropped onto the DocumentList DOM element.
	         * Checks that files are present for upload, determines the target (either the current document list or
	         * a specific folder rendered in the document list and then calls on the DNDUpload singleton component
	         * to perform the upload.
	         *
	         * @param e {object} HTML5 drag and drop event
	         * @method onDocumentListDrop
	         */
	        onDocumentListDrop: function DocumentAttachmentsList_onDocumentListDrop(e)
	        {
		        try
		        {
			        // Only perform a file upload if the user has *actually* dropped some files!
			        if (e.dataTransfer.files !== undefined && e.dataTransfer.files !== null && e.dataTransfer.files.length > 0)
			        {
				        // We need to get the upload progress dialog widget so that we can display it.
				        // The function called has been added to file-upload.js and ensures the dialog is a singleton.
				        var progressDialog = LogicECM.getDNDUploadProgressInstance();

				        var continueWithUpload = false;

				        // Check that at least one file with some data has been dropped...
				        var zeroByteFiles = "", i, j;

				        j = e.dataTransfer.files.length;
				        for (i = 0; i < j; i++)
				        {
					        if (e.dataTransfer.files[i].size > 0)
					        {
						        continueWithUpload = true;
					        }
					        else
					        {
						        zeroByteFiles += '"' + e.dataTransfer.files[i].name + '", ';
					        }
				        }

				        if (!continueWithUpload)
				        {
					        zeroByteFiles = zeroByteFiles.substring(0, zeroByteFiles.lastIndexOf(", "));
					        Alfresco.util.PopupManager.displayMessage(
						        {
							        text: progressDialog.msg("message.zeroByteFiles", zeroByteFiles)
						        });
				        }

				        // Perform some checks on based on the browser and selected files to ensure that we will
				        // support the upload request.
				        if (continueWithUpload && progressDialog.uploadMethod === progressDialog.INMEMORY_UPLOAD)
				        {
					        // Add up the total size of all selected files to see if they exceed the maximum allowed.
					        // If the user has requested to upload too large a file or too many files in one operation
					        // then generate an error dialog and abort the upload...
					        var totalRequestedUploadSize = 0;

					        j = e.dataTransfer.files.length;
					        for (i = 0; i < j; i++)
					        {
						        totalRequestedUploadSize += e.dataTransfer.files[i].size;
					        }
					        if (totalRequestedUploadSize > progressDialog.getInMemoryLimit())
					        {
						        continueWithUpload = false;
						        Alfresco.util.PopupManager.displayPrompt(
							        {
								        text: progressDialog.msg("inmemory.uploadsize.exceeded", Alfresco.util.formatFileSize(progressDialog.getInMemoryLimit()))
							        });
					        }
				        }

				        // If all tests are passed...
				        if (continueWithUpload)
				        {
					        // Initialise the target directory as the current path represented by the current rendering of the DocumentList.
					        // If we determine that the user has actually dropped some files onto the a folder icon (which we're about to check
					        // for) then we'll change this value to be that of the folder targeted...
					        var directoryName = this.options.categoryName,
						        destination = this.options.nodeRef;

					        // Remove all the highlighting
					        Dom.removeClass(this.widgets.dataTable.getContainerEl(), "dndDocListHighlight");

					        // Show uploader for multiple files
					        var multiUploadConfig =
					        {
						        files: e.dataTransfer.files,
						        uploadDirectoryName: directoryName,
						        destination: destination,
						        filter: [],
						        mode: progressDialog.MODE_MULTI_UPLOAD,
						        thumbnails: "doclib",
						        onFileUploadComplete:
						        {
							        fn: function() {},
							        scope: this
						        }
					        };

					        progressDialog.show(multiUploadConfig);
				        }
			        }
			        else
			        {
				        Alfresco.logger.debug("DL_onDocumentListDrop: A drop event was detected, but no files were present for upload: ", e.dataTransfer);
			        }
		        }
		        catch(exception)
		        {
			        Alfresco.logger.error("DL_onDocumentListDrop: The following error occurred when files were dropped onto the Document List: ", exception);
		        }
		        e.stopPropagation();
		        e.preventDefault();
	        },

            onActionUploadNewVersion: function (record)
            {
                var jsNode = record.jsNode,
                    displayName = record.displayName,
                    nodeRef = jsNode.nodeRef,
                    version = record.version;

                if (!this.fileUpload) {
                    this.fileUpload = Alfresco.getFileUploadInstance();
                }

                // Show uploader for multiple files
                var description = this.msg("label.filter-description", displayName),
                    extensions = "*";

                if (displayName && new RegExp(/[^\.]+\.[^\.]+/).exec(displayName))
                {
                    // Only add a filtering extension if filename contains a name and a suffix
                    extensions = "*" + displayName.substring(displayName.lastIndexOf("."));
                }

                if (record.workingCopy && record.workingCopy.workingCopyVersion)
                {
                    version = record.workingCopy.workingCopyVersion;
                }

                var singleUpdateConfig =
                {
                    updateNodeRef: nodeRef.toString(),
                    updateFilename: displayName,
                    updateVersion: version,
                    overwrite: true,
                    filter: [
                        {
                            description: description,
                            extensions: extensions
                        }],
                    mode: this.fileUpload.MODE_SINGLE_UPDATE,
                    onFileUploadComplete:
                    {
                        fn:  function(complete) {
                            this._uploadComplete(complete, "updated");
                            YAHOO.Bubbling.fire("listRefresh",{});
                        },
                        scope: this
                    },
                    suppressRefreshEvent: true
                };
                if ($isValueSet(this.options.siteId))
                {
                    singleUpdateConfig.siteId = this.options.siteId;
                    singleUpdateConfig.containerId = this.options.containerId;
                }
                this.fileUpload.show(singleUpdateConfig);
            },

            onActionMoveToNewCategory: function (record, owner) {
                // Get action & params and start create the config for displayForm
                var action = this.getAction(record, owner),
                    params = action.params,
                    config = {
                        title: this.msg(action.label)
                    },
                    displayName = record.displayName;

                // Make sure we don't pass the function as a form parameter
                delete params["function"];

                config.success = {
                    fn: function (response, request) {
                        if (response.json.status && response.json.status != 200) {
                            var errorText = response.json.message;
                            if (errorText.indexOf("LECM_ERROR:") >= 0) {
                                var startIndex = errorText.indexOf("LECM_ERROR:");
                                response.config.failureMessage = errorText.substring(startIndex + "LECM_ERROR: ".length);
                            }
                        } else {
                            YAHOO.Bubbling.fire("listRefresh", {});
                        }
                    },
                    obj: record,
                    scope: this
                };
                // Add configure success message
                if (params.successMessage) {
                    config.successMessage = this.msg(params.successMessage, displayName);
                    delete params["successMessage"];
                }
                // Add configure success message
                if (params.failureMessage) {
                    config.failureMessage = this.msg(params.failureMessage, displayName);
                    delete params["failureMessage"];
                }

                // Use the remaining properties as form properties
                config.properties = params;

                // Finally display form as dialog
                Alfresco.util.PopupManager.displayForm(config);
            }
        }, true);
})();
