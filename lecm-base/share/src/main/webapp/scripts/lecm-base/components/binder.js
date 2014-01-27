/* global Alfresco,
          console,
          YAHOO
*/
function Binder(options) {
	'use strict';

	function throwException(paramName) {
		//console.log('Binder: не был задан параметр при создании экземпляра (%s)', paramName);
		throw 'Binder: при создании экземпляра не был задан параметр (' + paramName + ')';
	}

	this.bubblingLayer = options.bubblingLayer || throwException('bubblingLayer');
	this.components    = options.components    || throwException('components');
	this.dialog        = null;
	this.dialogId      = options.dialogId      || throwException('dialogId');
	this.getIdFn       = options.getIdFn       || this._getIdDefaultFn;
	this.handlers      = options.handlers      || throwException('handlers');

	this._onEventFired = function(type, args, obj) { // obj === Binder Instance (this)
		var currentId;

		try {
			currentId = obj.getIdFn.call(obj, args[1]);
		} catch (e) {
			console.log('Binder: не удалось разрешить ID компонента (' + currentId + ')');
			return false;
		}

		if(obj.handlers[currentId]) {
			console.log('Binder: изменилось состояние компонента пучка (' + currentId + ')');
			obj.handlers[currentId].call(obj, type, args);
		}

		return true;
	};

	this._init();
}

Binder.prototype._init = function() {
	'use strict';
	this._initComponents();
	this._bubbSubscribe();
	this._hookDialog();
};

Binder.prototype._getIdDefaultFn = function(obj) {
	'use strict';
	return obj.eventGroup.id;
};

Binder.prototype._initComponents = function() {
	'use strict';

	var i, cmpnt, query;

	var components = this.components;
	var componentsLg = components.length;

	var ComponentManager = Alfresco.util.ComponentManager;

	for(i = 0; i < componentsLg; i++) {
		query = components[i];
		cmpnt = ComponentManager.find(query)[0];

		if(cmpnt) {
			components[i] = cmpnt;
		} else {
			throw 'Binder: не найден компонент (' + query.id + ')';
		}
	}
};

Binder.prototype._bubbSubscribe = function() {
	'use strict';
	YAHOO.Bubbling.subscribe(this.bubblingLayer, this._onEventFired, this);
};

Binder.prototype._bubbUnsubscribe = function() {
	'use strict';
	YAHOO.Bubbling.unsubscribe(this.bubblingLayer, this._onEventFired, this);
};

Binder.prototype._hookDialog = function() {
	'use strict';

	function onAfterFormRuntimeInit() {
		var dialog;
		var query = { id: binder.dialogId, name: 'Alfresco.module.SimpleDialog' };
		var queryResult = ComponentManager.find(query);

		// В коллекции ComponentManager может сущестсовать несколько компонентов с одним и тем же id, так как они
		// добавляются последовательно, то берём последний.
		queryResult = queryResult[queryResult.length - 1];

		YAHOO.Bubbling.unsubscribe(bubbLayer, onAfterFormRuntimeInit); // Once Event

		binder.dialog = dialog = queryResult.dialog;
		dialog.beforeHideEvent.subscribe(binder._bubbUnsubscribe, binder, true);
	}

	var binder = this; // The Closure vs...

	var bubbLayer = 'afterFormRuntimeInit';

	var Bubbling = YAHOO.Bubbling;
	var ComponentManager = Alfresco.util.ComponentManager;

	Bubbling.subscribe(bubbLayer, onAfterFormRuntimeInit, binder); // ... the Context
};