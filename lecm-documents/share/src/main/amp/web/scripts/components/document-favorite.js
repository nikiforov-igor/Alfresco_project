/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
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
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event;


	LogicECM.module.DocumentFavourite = function (fieldHtmlId)
	{
		LogicECM.module.DocumentFavourite.superclass.constructor.call(this, "LogicECM.module.DocumentFavourite", fieldHtmlId, [ "container", "datasource"]);
		this.services.preferences = new Alfresco.service.Preferences();
		this.controlId = fieldHtmlId + "-cntrl";
		return this;
	};

	YAHOO.extend(LogicECM.module.DocumentFavourite, Alfresco.component.Base,
		{
			options: {
				documentRef: null,
				isFavourite: false
			},

			controlId: null,
			favoriteButton: null,

			onReady: function()
			{
				this.favoriteButton = Alfresco.util.createYUIButton(this, this.controlId + "-favourite-button", this.onFavorite,{},
					Dom.get(this.controlId + "-favourite-button"));

				this.render();
			},

			render: function() {
				if (this.options.isFavourite) {
					Dom.get(this.controlId + "-favourite-button").title =  this.msg("button.remove.favourite");
					Dom.addClass(this.controlId + "-favourite", "enabled");
				} else {
					Dom.get(this.controlId + "-favourite-button").title =  this.msg("button.add.favourite");
					Dom.removeClass(this.controlId + "-favourite", "enabled");
				}

			},

			onFavorite: function()
			{
				var me = this;
				var orgValues =
				{
					isFavourite: this.options.isFavourite
				};

				this.options.isFavourite = !this.options.isFavourite;

				var responseConfig =
				{
					failureCallback:
					{
						fn: function Favourite_favourite_failure(event)
						{
							me.options.isFavourite = orgValues.isFavourite;
							me.render();
							Alfresco.util.PopupManager.displayPrompt(
								{
									text: this.msg("favourite.message.failure")
								});
						},
						scope: this
					}
				};

				var action = this.options.isFavourite ? "add" : "remove";
				var key = Alfresco.service.Preferences.FAVOURITE_DOCUMENTS;
				this.services.preferences[action].call(this.services.preferences, key, this.options.documentRef, responseConfig);

				this.render();
			}
		});
})();