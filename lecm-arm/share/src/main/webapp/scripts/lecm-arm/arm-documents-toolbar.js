(function()
{

	LogicECM.module.ARM.DocumentsToolbar = function(htmlId)
	{
		LogicECM.module.ARM.DocumentsToolbar.superclass.constructor.call(this, "LogicECM.module.ARM.DocumentsToolbar", htmlId);
		return this;
	};

	YAHOO.extend(LogicECM.module.ARM.DocumentsToolbar, LogicECM.module.Base.Toolbar);

	YAHOO.lang.augmentObject(LogicECM.module.ARM.DocumentsToolbar.prototype,
		{
			_initButtons: function () {

			}
		}, true);
})();