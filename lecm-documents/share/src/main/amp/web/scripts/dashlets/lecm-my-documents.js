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
 * @class LogicECM.dashlet
 */
LogicECM.dashlet = LogicECM.dashlet || {};


/**
 * Dashboard MyDocuments component.
 *
 * @namespace Alfresco
 * @class LogicECM.dashlet.MyDocuments
 */
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
		$links = Alfresco.util.activateLinks,
		$userProfile = Alfresco.util.userProfileLink,
		$siteDashboard = Alfresco.util.siteDashboardLink,
		$relTime = Alfresco.util.relativeTime;

	var FAVOURITE_EVENTCLASS = Alfresco.util.generateDomId(null, "favourite"),
		LIKE_EVENTCLASS = Alfresco.util.generateDomId(null, "like");

	/**
	 * Preferences
	 */
	var PREFERENCES_MYDOCUMENTS_DASHLET = "org.alfresco.share.mydocuments.dashlet"
	PREFERENCES_MYDOCUMENTS_DASHLET_FILTER = PREFERENCES_MYDOCUMENTS_DASHLET + ".filter",
		PREFERENCES_MYDOCUMENTS_DASHLET_VIEW = PREFERENCES_MYDOCUMENTS_DASHLET + ".simpleView";

	/**
	 * Dashboard MyDocuments constructor.
	 *
	 * @param {String} htmlId The HTML id of the parent element
	 * @return {LogicECM.dashlet.MyDocuments} The new component instance
	 * @constructor
	 */
	LogicECM.dashlet.MyDocuments = function MyDocuments_constructor(htmlId)
	{
		return LogicECM.dashlet.MyDocuments.superclass.constructor.call(this, htmlId);
	};

	Alfresco.component.SimpleDocList.generateFavourite = function SimpleDocList_generateFavourite(scope, record)
	{
		var i18n = "favourite." + ((record.getData("isFolder") && !record.getData("isLecmDocument")) ? "folder." : "document."),
			html = "";

		if (record.getData("isFavourite"))
		{
			html = '<a class="favourite-action ' + FAVOURITE_EVENTCLASS + ' enabled" title="' + scope.msg(i18n + "remove.tip") + '" tabindex="0"></a>';
		}
		else
		{
			html = '<a class="favourite-action ' + FAVOURITE_EVENTCLASS + '" title="' + scope.msg(i18n + "add.tip") + '" tabindex="0">' + scope.msg(i18n + "add.label") + '</a>';
		}

		return html;
	};

	Alfresco.component.SimpleDocList.generateLikes = function SimpleDocList_generateLikes(scope, record)
	{
		var likes = record.getData("likes"),
			i18n = "like." + ((record.getData("isFolder") && !record.getData("isLecmDocument")) ? "folder." : "document."),
			html = "";

		if (likes.isLiked)
		{
			html = '<a class="like-action ' + LIKE_EVENTCLASS + ' enabled" title="' + scope.msg(i18n + "remove.tip") + '" tabindex="0"></a>';
		}
		else
		{
			html = '<a class="like-action ' + LIKE_EVENTCLASS + '" title="' + scope.msg(i18n + "add.tip") + '" tabindex="0">' + scope.msg(i18n + "add.label") + '</a>';
		}

		html += '<span class="likes-count">' + $html(likes.totalLikes) + '</span>';

		return html;
	};

	Alfresco.component.SimpleDocList.generateComments = function SimpleDocList_generateComments(scope, record)
	{
		var file = record.getData(),
			url = Alfresco.constants.URL_PAGECONTEXT + "site/" + file.location.site + "/" + (file.isFolder ? "folder" : "document") + "-details?nodeRef=" + file.nodeRef + "#comment",
			i18n = "comment." + ((file.isFolder && !file.isLecmDocument) ? "folder." : "document.");

		if (file.isLecmDocument) {
			url = Alfresco.constants.URL_PAGECONTEXT + "document?nodeRef=" + file.nodeRef + "#comment";
		}

		return '<a href="' + url + '" class="comment" title="' + scope.msg(i18n + "tip") + '" tabindex="0">' + scope.msg(i18n + "label") + '</a>';
	};

	YAHOO.extend(LogicECM.dashlet.MyDocuments, Alfresco.dashlet.MyDocuments,
		{
			onReady: function SimpleDocList_onReady()
			{
				var me = this;

				// Hook favourite document events
				var fnFavouriteHandler = function SimpleDocList_fnFavouriteHandler(layer, args)
				{
					var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
					if (owner !== null)
					{
						me.onFavourite.call(me, args[1].target.offsetParent, owner);
					}
					return true;
				};
				YAHOO.Bubbling.addDefaultAction(FAVOURITE_EVENTCLASS, fnFavouriteHandler);

				// Hook like/unlike events
				var fnLikesHandler = function SimpleDocList_fnLikesHandler(layer, args)
				{
					var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
					if (owner !== null)
					{
						me.onLikes.call(me, args[1].target.offsetParent, owner);
					}
					return true;
				};
				YAHOO.Bubbling.addDefaultAction(LIKE_EVENTCLASS, fnLikesHandler);

				LogicECM.dashlet.MyDocuments.superclass.onReady.apply(this, arguments);
			},

			getWebscriptUrl: function ()
			{
				return Alfresco.constants.PROXY_URI + "lecm/doclib/doclist/documents/node/alfresco/sites/home?max=50";
			},

			/**
			 * Thumbnail custom datacell formatter
			 *
			 * @method renderCellThumbnail
			 * @param elCell {object}
			 * @param oRecord {object}
			 * @param oColumn {object}
			 * @param oData {object|string}
			 */
			renderCellThumbnail: function SimpleDocList_renderCellThumbnail(elCell, oRecord, oColumn, oData)
			{
				var columnWidth = 40,
					record = oRecord.getData(),
					desc = "";

				if (record.isInfo)
				{
					columnWidth = 52;
					desc = '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/help-docs-bw-32.png" />';
				}
				else
				{
					var name = record.fileName,
						extn = name.substring(name.lastIndexOf(".")),
						locn = record.location,
						nodeRef = new Alfresco.util.NodeRef(record.nodeRef),
						docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + locn.site + "/document-details?nodeRef=" + nodeRef.toString();

					if (record.isLecmDocument) {
						docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "document?nodeRef=" + nodeRef.toString();
						if (record.extPresentString != null && record.extPresentString.length > 0) {
							name = record.extPresentString;
						}
					}

					if (this.options.simpleView)
					{
						/**
						 * Simple View
						 */

						var lecmDocumentImageSrc = Alfresco.constants.URL_RESCONTEXT + "images/lecm-documents/type-icons/" + record.nodeType.replace(":", "_") + "-32.png";
						var lecmDocumentImage = '<img src="' + lecmDocumentImageSrc + '" alt="' + extn + '" title="' + $html(name) + '" onerror="this.src = \'' + Alfresco.constants.URL_RESCONTEXT + 'images/lecm-documents/type-icons/default_document-32.png\';"/>';

						if (record.isLecmDocument) {
							desc = '<span class="icon32"><a href="' + docDetailsUrl + '">' + lecmDocumentImage + '</a></span>';
						} else {
							var id = this.id + '-preview-' + oRecord.getId();
							desc = '<span id="' + id + '" class="icon32"><a href="' + docDetailsUrl + '"><img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/filetypes/' + Alfresco.util.getFileIcon(name) + '" alt="' + extn + '" title="' + $html(name) + '" /></a></span>';

							// Preview tooltip
							this.previewTooltips.push(id);
						}
					}
					else
					{
						/**
						 * Detailed View
						 */

						lecmDocumentImageSrc = Alfresco.constants.URL_RESCONTEXT + "images/lecm-documents/type-icons/" + record.nodeType.replace(":", "_") + ".png";
						lecmDocumentImage = '<img src="' + lecmDocumentImageSrc + '" alt="' + extn + '" title="' + $html(name) + '" onerror="this.src = \'' + Alfresco.constants.URL_RESCONTEXT + 'images/lecm-documents/type-icons/default_document.png\';"/>';

						columnWidth = 100;
						if (record.isLecmDocument) {
							desc = '<span class="thumbnail"><a href="' + docDetailsUrl + '">' + lecmDocumentImage + '</a></span>';
						} else {
							desc = '<span class="thumbnail"><a href="' + docDetailsUrl + '"><img src="' + Alfresco.constants.PROXY_URI + 'api/node/' + nodeRef.uri + '/content/thumbnails/doclib?c=queue&ph=true" alt="' + extn + '" title="' + $html(name) + '" /></a></span>';
						}
					}
				}

				oColumn.width = columnWidth;

				Dom.setStyle(elCell, "width", oColumn.width + "px");
				Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

				elCell.innerHTML = desc;
			},

			/**
			 * Detail custom datacell formatter
			 *
			 * @method renderCellDetail
			 * @param elCell {object}
			 * @param oRecord {object}
			 * @param oColumn {object}
			 * @param oData {object|string}
			 */
			renderCellDetail: function SimpleDocList_renderCellDetail(elCell, oRecord, oColumn, oData)
			{
				var record = oRecord.getData(),
					desc = "";

				if (record.isInfo)
				{
					desc += '<div class="empty"><h3>' + record.title + '</h3>';
					desc += '<span>' + record.description + '</span></div>';
				}
				else
				{
					var id = this.id + '-metadata-' + oRecord.getId(),
						version = "",
						description = '<span class="faded">' + this.msg("details.description.none") + '</span>',
						dateLine = "",
						canComment = record.permissions.userAccess.create,
						locn = record.location,
						nodeRef = new Alfresco.util.NodeRef(record.nodeRef),
						docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + locn.site + "/document-details?nodeRef=" + nodeRef.toString();

					if (record.isLecmDocument) {
						docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "document?nodeRef=" + nodeRef.toString();
					}

					// Description non-blank?
					if (record.description && record.description !== "")
					{
						description = $links($html(record.description));
					}

					// Version display
					if (record.version && record.version !== "")
					{
						version = '<span class="document-version">' + $html(record.version) + '</span>';
					}

					// Date line
					var dateI18N = "modified", dateProperty = record.modifiedOn;
					if (record.custom && record.custom.isWorkingCopy)
					{
						dateI18N = "editing-started";
					}
					else if (record.modifiedOn === record.createdOn)
					{
						dateI18N = "created";
						dateProperty = record.createdOn;
					}
					if (Alfresco.constants.SITE === "")
					{
						dateLine = this.msg("details." + dateI18N + "-in-site", $relTime(dateProperty), $siteDashboard(locn.site, locn.siteTitle, 'class="site-link theme-color-1" id="' + id + '"'));
					}
					else
					{
						dateLine = this.msg("details." + dateI18N + "-by", $relTime(dateProperty), $userProfile(record.modifiedByUser, record.modifiedBy, 'class="theme-color-1"'));
					}

					var fileName = record.displayName;
					if (record.extPresentString != null && record.extPresentString.length > 0) {
						fileName = record.extPresentString;
					}

					if (this.options.simpleView)
					{
						desc += '<h3 class="filename simple-view"><a class="theme-color-1" href="' + docDetailsUrl + '">' + $html(fileName) + '</a></h3>';
						desc += '<div class="detail"><span class="item-simple">' + dateLine + '</span></div>';
					}
					else
					{
						desc += '<h3 class="filename"><a class="theme-color-1" href="' + docDetailsUrl + '">' + $html(fileName) + '</a>' + version + '</h3>';

						desc += '<div class="detail">';
						desc +=    '<span class="item">' + dateLine + '</span>';
						if (this.options.showFileSize)
						{
							desc +=    '<span class="item">' + Alfresco.util.formatFileSize(record.size) + '</span>';
						}
						desc += '</div>';
						desc += '<div class="detail"><span class="item">' + description + '</span></div>';

						/* Favourite / Likes / Comments */
						desc += '<div class="detail detail-social">';
						desc +=    '<span class="item item-social">' + Alfresco.component.SimpleDocList.generateFavourite(this, oRecord) + '</span>';
						desc +=    '<span class="item item-social item-separator">' + Alfresco.component.SimpleDocList.generateLikes(this, oRecord) + '</span>';
						if (canComment)
						{
							desc +=    '<span class="item item-social item-separator">' + Alfresco.component.SimpleDocList.generateComments(this, oRecord) + '</span>';
						}
						desc += '</div>';
					}

					// Metadata tooltip
					this.metadataTooltips.push(id);
				}

				elCell.innerHTML = desc;
			}
		});
})();
