if (typeof IT == "undefined" || !IT) {
	var IT = {};
}

IT.component = IT.component || {};

(function() {
	IT.component.DataSource = function DataSource_Constructor(config) {
		this.createEvent("cacheRequestEvent");
	};
})();

YAHOO.lang.augmentObject(IT.component.DataSource, {});

YAHOO.lang.augmentProto(IT.component.DataSource, YAHOO.util.EventProvider);