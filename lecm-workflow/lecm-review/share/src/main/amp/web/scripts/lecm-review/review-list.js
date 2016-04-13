if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.OrgStructure = LogicECM.module.OrgStructure || {};

(function() {
	var Bubbling = YAHOO.Bubbling;

	LogicECM.module.Review.ReviewList = function(containerId, options, datagridMeta, messages) {
		LogicECM.module.Review.ReviewList.superclass.constructor.call(this, containerId);
		this.name = 'LogicECM.module.Review.ReviewList';
		this.setMessages(messages);
		this.setOptions(options);
		this.datagridMeta = datagridMeta;
		Bubbling.on('initDatagrid', this._initReviewListDatagrid, this);
		return this;
	};

	YAHOO.lang.extend(LogicECM.module.Review.ReviewList, LogicECM.module.Base.DataGrid, {
		_initReviewListDatagrid: function(layer, args) {
			if (this.options.bubblingLabel == args[1].datagrid.options.bubblingLabel) {
				Bubbling.unsubscribe(layer, this._initReviewListDatagrid);
				Bubbling.fire('activeGridChanged', {
					bubblingLabel: this.options.bubblingLabel,
					datagridMeta: this.datagridMeta
				});
			}
		}
	}, true);
})();
