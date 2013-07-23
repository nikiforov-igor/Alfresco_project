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

	YAHOO.extend(LogicECM.dashlet.MyDocuments, Alfresco.dashlet.MyDocuments,
		{
			getWebscriptUrl: function ()
			{
				return Alfresco.constants.PROXY_URI + "lecm/doclib/doclist/lecm-documents/node/alfresco/company/home?max=50";
			},

			renderCellThumbnail: function (elCell, oRecord, oColumn, oData)
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
						nodeRef = new Alfresco.util.NodeRef(record.nodeRef),
						docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "/document?nodeRef=" + nodeRef.toString();

					var imageSrc = Alfresco.constants.URL_RESCONTEXT + "images/lecm-documents/type-icons/" + record.nodeType.replace(":", "_") + ".png";
					var image = '<img src="' + imageSrc + '" alt="' + extn + '" title="' + $html(name) + '" onerror="this.src = \'/share/res/images/lecm-documents/type-icons/default_document.png\';"/>';

					if (this.options.simpleView)
					{
						/**
						 * Simple View
						 */
						desc = '<span class="icon32"><a href="' + docDetailsUrl + '">' + image + '</a></span>';
					}
					else
					{
						/**
						 * Detailed View
						 */
						columnWidth = 100;
						desc = '<span class="thumbnail"><a href="' + docDetailsUrl + '">' + image + '</a></span>';
					}
				}

				oColumn.width = columnWidth;

				Dom.setStyle(elCell, "width", oColumn.width + "px");
				Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

				elCell.innerHTML = desc;
			},

			renderCellDetail: function (elCell, oRecord, oColumn, oData)
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
						docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "/document?nodeRef=" + nodeRef.toString();

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

					if (this.options.simpleView)
					{
						/**
						 * Simple View
						 */
						desc += '<h3 class="filename simple-view"><a class="theme-color-1" href="' + docDetailsUrl + '">' + $html(record.presentString) + '</a></h3>';
						desc += '<div class="detail"><span class="item-simple">' + dateLine + '</span></div>';
					}
					else
					{
						/**
						 * Detailed View
						 */
						desc += '<h3 class="filename"><a class="theme-color-1" href="' + docDetailsUrl + '">' + $html(record.presentString) + '</a>' + version + '</h3>';

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
