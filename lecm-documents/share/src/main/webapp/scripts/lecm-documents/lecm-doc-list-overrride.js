YAHOO.util.Event.onContentReady("alf-hd", function () {
    var $html = Alfresco.util.encodeHTML,
        $links = Alfresco.util.activateLinks,
        $isValueSet = Alfresco.util.isValueSet;
        $lecmUserProfile = function (oUser, fullName, linkAttr, disableLink) {
            var userName = oUser ? oUser.userName : "";
            if (!YAHOO.lang.isString(userName) || userName.length === 0) {
                return "";
            }

            var html = Alfresco.util.encodeHTML(YAHOO.lang.isString(fullName) && fullName.length > 0 ? fullName : userName),
                template = "view-metadata?nodeRef={nodeRef}";

            // If the "userprofilepage" template doesn't exist or is empty, or we're in portlet mode we'll just return the user's fullName || userName
            if (disableLink || Alfresco.constants.PORTLET || !oUser.nodeRef) {
                return '<span>' + html + '</span>';
            }

            // Generate the link
            var url = YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + template,
                {
                    nodeRef: oUser.nodeRef
                });

            return '<a href="' + url + '" ' + (linkAttr || "") + '>' + html + '</a>';
        };

    if (Alfresco.DocumentList) {

        Alfresco.DocumentList.generateFileFolderLinkMarkup = function DL_generateFileFolderLinkMarkup(scope, record) {
            var jsNode = record.jsNode,
                type = jsNode.type,
                recordSiteName = $isValueSet(record.location.site) ? record.location.site.name : null,
                html;

            // Test for: handling a link and the link's target does not live within this site
            if (jsNode.isLink && recordSiteName !== scope.options.siteId) {
                if (jsNode.isContainer) {
                    // Create a URL to either the Repository or Site document library where the link target lives
                    html = $siteURL((recordSiteName === null ? "repository" : "documentlibrary") + "?path=" + encodeURIComponent($combine(record.location.path, record.location.file)),
                        {
                            site: recordSiteName
                        });
                }
                else {
                    // Create a URL to the document details page for the link target
                    html = scope.getActionUrls(record, recordSiteName).documentDetailsUrl;
                }
            }
            else {
                var nodeRef = jsNode.isLink ? jsNode.linkedNode.nodeRef : jsNode.nodeRef,
                    strNodeRef = nodeRef.toString();

                if (jsNode.isContainer) {
                    if (type.indexOf("lecm") >= 0 && type.toLowerCase().indexOf("document") >= 0) {
                        html = Alfresco.util.siteURL("document?nodeRef=" + strNodeRef,
                            {
                                site: null
                            });
                    } else {
                        // Create path-change filter markup
                        html = '#" class="filter-change" rel="' + Alfresco.DocumentList.generatePathMarkup(record.location);
                    }
                }
                else {
                    // Create a URL to the document details page
                    html = scope.getActionUrls(record).documentDetailsUrl;
                }
            }

            return '<a href="' + html + '">';
        };

        Alfresco.DocumentList.prototype.fnRenderCellThumbnail = function () {
            var scope = this;
            return function (elCell, oRecord, oColumn, oData) {
                var record = oRecord.getData(),
                    node = record.jsNode,
                    properties = node.properties,
                    name = record.displayName,
                    isContainer = node.isContainer,
                    type = node.type,
                    isLink = node.isLink,
                    extn = name.substring(name.lastIndexOf(".")),
                    imgId = node.nodeRef.nodeRef; // DD added

                var containerTarget; // This will only get set if thumbnail represents a container
                if (scope.options.simpleView) {
                    /**
                     * Simple View
                     */
                    oColumn.width = 40;
                    Dom.setStyle(elCell, "width", oColumn.width + "px");
                    Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

                    if (isContainer) {
                        var icon = "components/documentlibrary/images/folder-32.png";
                        if (type.indexOf("lecm") >= 0 && type.toLowerCase().indexOf("document") >= 0) {
                            icon = "images/lecm-documents/type-icons/" + type.replace(":", "_") + "-32.png";
                        }
                        elCell.innerHTML = '<span class="folder-small">' + (isLink ? '<span class="link"></span>' : '') + (scope.dragAndDropEnabled ? '<span class="droppable"></span>' : '') + Alfresco.DocumentList.generateFileFolderLinkMarkup(scope, record) + '<img id="' + imgId + '" src="' + Alfresco.constants.URL_RESCONTEXT + icon + '" /></a>';
                        containerTarget = new YAHOO.util.DDTarget(imgId); // Make the folder a target
                    }
                    else {
                        var id = scope.id + '-preview-' + oRecord.getId();
                        elCell.innerHTML = '<span id="' + id + '" class="icon32">' + (isLink ? '<span class="link"></span>' : '') + Alfresco.DocumentList.generateFileFolderLinkMarkup(scope, record) + '<img id="' + imgId + '" src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/filetypes/' + Alfresco.util.getFileIcon(name) + '" alt="' + extn + '" title="' + $html(name) + '" /></a></span>';
                        // Preview tooltip
                        scope.previewTooltips.push(id);
                    }
                }
                else {
                    /**
                     * Detailed View
                     */
                    oColumn.width = 100;
                    Dom.setStyle(elCell, "width", oColumn.width + "px");
                    Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

                    if (isContainer) {
                        var icon = "components/documentlibrary/images/folder-64.png";
                        if (type.indexOf("lecm") >= 0 && type.toLowerCase().indexOf("document") >= 0) {
                            icon = "/images/lecm-documents/type-icons/" + type.replace(":", "_") + ".png";
                        }
                        elCell.innerHTML = '<span class="folder-small">' + (isLink ? '<span class="link"></span>' : '') + (scope.dragAndDropEnabled ? '<span class="droppable"></span>' : '') + Alfresco.DocumentList.generateFileFolderLinkMarkup(scope, record) + '<img id="' + imgId + '" src="' + Alfresco.constants.URL_RESCONTEXT + icon + '" /></a>';
                        containerTarget = new YAHOO.util.DDTarget(imgId); // Make the folder a target
                    }
                    else {
                        elCell.innerHTML = '<span class="thumbnail">' + (isLink ? '<span class="link"></span>' : '') + Alfresco.DocumentList.generateFileFolderLinkMarkup(scope, record) + '<img id="' + imgId + '" src="' + Alfresco.DocumentList.generateThumbnailUrl(record) + '" alt="' + extn + '" title="' + $html(name) + '" /></a></span>';
                    }
                }

                var dnd = new Alfresco.DnD(imgId, scope);
            };
        };

        /*Alfresco.DocumentList.prototype.fnRenderCellActions = function () {
            var scope = this;
            return function (elCell, oRecord, oColumn, oData) {
                var record = oRecord.getData(),
                    node = record.jsNode,
                    type = node.type;

                if (type.toLowerCase().indexOf("lecm") < 0) {
                    if (scope.options.simpleView) {
                        oColumn.width = 80;
                    }
                    else {
                        oColumn.width = 200;
                    }
                    Dom.setStyle(elCell, "width", oColumn.width + "px");
                    Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
                    Dom.addClass(elCell.parentNode, oRecord.getData("type"));

                    elCell.innerHTML = '<div id="' + scope.id + '-actions-' + oRecord.getId() + '" class="hidden"></div>';
                } else {
                    elCell.innerHTML = '<div id="' + scope.id + '-actions-hidden-' + oRecord.getId() + '" class="hidden"></div>';
                }
            };
        };*/

        Alfresco.DocumentList.generateUserLink = function DL_generateUserLink(scope, oUser) {
            if (oUser.isDeleted === true) {
                return '<span>' + scope.msg("details.user.deleted", $html(oUser.userName)) + '</span>';
            }
            return $lecmUserProfile(oUser, YAHOO.lang.trim(oUser.displayName));
        };

        // Reference to Data Grid component
        var list = Alfresco.util.ComponentManager.findFirst("Alfresco.DocumentList");
        if (list != null) {
            list._setupDataSource();
            // DataTable set-up and event registration
            list._setupDataTable();
        }
    }
});
