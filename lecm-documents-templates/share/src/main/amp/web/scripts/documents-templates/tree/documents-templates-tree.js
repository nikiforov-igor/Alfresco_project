/* global YAHOO, Alfresco */

if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.DocumentsTemplates = LogicECM.module.DocumentsTemplates || {};

(function () {
	var Event = YAHOO.util.Event,
		Bubbling = YAHOO.Bubbling;

	LogicECM.module.DocumentsTemplates.TreeView = function(containerId, options, messages) {
		LogicECM.module.DocumentsTemplates.TreeView.superclass.constructor.call(this, 'LogicECM.module.DocumentsTemplates.TreeView', containerId);
		this.setOptions(options);
		this.setMessages(messages);
		this.treeViewId = containerId + '-tree';
		return this;
	};

	YAHOO.lang.extend(LogicECM.module.DocumentsTemplates.TreeView, Alfresco.component.Base, {

		nodeRef: null,

		xpath: null,

		treeViewId: null,

		options: {
			selectableType: null,
			xpath: null
		},

		_loadTypesData: function(node, fnLoadComplete) {
			/* this == LogicECM.module.DocumentsTemplates.TreeView */
			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.PROXY_URI + 'lecm/documents/types',
				successCallback: {
					scope: this,
					fn: function(successResponse) {
						var types = successResponse.json.data;
						types.forEach(function (type) {
							new YAHOO.widget.TextNode({
								label: type.name,
								value: type.value,
								config: {
									showDelay: 100,
									hideDelay: 100,
									autoDismissDelay: 0,
									// error: null,
									disabled: false,
									// type: 'textBox',
									// nodeRef: null,
									// name: null,
									value: type.value,
									title: 'Создать шаблон',
									itemKind: 'type',
									itemId: 'lecm-template:document-template',
									mode: 'create',
									destination: this.nodeRef,
									formId: ''
								}
							}, node).setDynamicLoad(this.bind(this._loadTemplatesData));
							//TODO: в каждой новой ноде хранить конфиг для Alfresco.util.InsituEditor
						}, this);
						if (YAHOO.lang.isFunction(fnLoadComplete)) {
							fnLoadComplete.call(node);
						}
					}
				},
				failureCallback: {
					scope: this,
					fn: function(failureResponse) {
						Alfresco.util.PopupManager.displayMessage({
							text: this.msg('message.failure')
						});
						if (YAHOO.lang.isFunction(fnLoadComplete)) {
							fnLoadComplete.call(node);
						}
					}
				}
			});
		},

		_loadTemplatesData: function(node, fnLoadComplete) {
			/* this == LogicECM.module.DocumentsTemplates.TreeView */
			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.PROXY_URI + 'lecm/forms/picker/node/children',
				dataObj: {
					selectableType: this.options.selectableType,
					searchTerm: '',
					size: 100,
					sortProp: 'cm:created',
					additionalFilter: '=@lecm\\-template\\:doc\\-type:"' + node.data.value + '"',
					xpath: this.options.xpath
				},
				successCallback: {
					scope: this,
					fn: function(successResponse) {
						debugger;
						// var templates = successResponse.json;
						// templates.forEach(function (template) {
						// 	new YAHOO.widget.TextNode({
						// 		label: '',
						// 		isLeaf: true
						// 	}, node);
						//TODO: в каждой новой ноде хранить конфиг для Alfresco.util.InsituEditor
						// }, this);
						if (YAHOO.lang.isFunction(fnLoadComplete)) {
							fnLoadComplete.call(node);
						}
					}
				},
				failureCallback: {
					scope: this,
					fn: function(failureResponse) {
						Alfresco.util.PopupManager.displayMessage({
							text: this.msg('message.failure')
						});
						if (YAHOO.lang.isFunction(fnLoadComplete)) {
							fnLoadComplete.call(node);
						}
					}
				}
			});
		},

		onExpandComplete: function(node, tree) {
			node.children.forEach(function(childNode) {
				if (childNode.data.config && !childNode.data.insituEditor) {
					childNode.data.config.container = childNode.getContentEl();
					childNode.data.config.context = childNode.getContentEl().parentElement;
					childNode.data.insituEditor = new Alfresco.widget.InsituEditorTemplateCreate(null, childNode.data.config);
				}
			});
		},

		onReady: function() {
			console.log(this.name + '[' + this.id + '] is ready');
			this.widgets.treeView = new YAHOO.widget.TreeView(this.treeViewId);
			this.widgets.treeView.data = {
				documentsTemplates: this
			};
			var root = new YAHOO.widget.TextNode({
				label: 'Шаблоны документов',
				expanded: true
			}, this.widgets.treeView.getRoot());
			root.setDynamicLoad(this.bind(this._loadTypesData));
			this.widgets.treeView.subscribe('expandComplete', this.onExpandComplete, this.widgets.treeView, this);

			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.PROXY_URI + 'lecm/documents-templates/root',
				successCallback: {
					scope: this,
					fn: function(successResponse) {
						this.nodeRef = successResponse.json.nodeRef;
						this.xpath = successResponse.json.xpath;
						this.widgets.treeView.draw();
					}
				},
				failureMessage: this.msg('message.failure')
			});
		}
	}, true);

	Alfresco.widget.InsituEditorTemplateCreate = function(p_editor, p_params) {
		Alfresco.widget.InsituEditorTemplateCreate.superclass.constructor.call(this, p_editor, p_params);
		return this;
	};

	YAHOO.lang.extend(Alfresco.widget.InsituEditorTemplateCreate, Alfresco.widget.InsituEditorIcon, {

		onIconClick: function (e, obj) {
			if (obj.disabled) {
				return;
			}

			Alfresco.util.Ajax.request({
				url: Alfresco.constants.URL_SERVICECONTEXT + 'components/form',
				dataObj: {
					htmlid: Alfresco.util.generateDomId(),
					itemKind: obj.params.itemKind,
					itemId: obj.params.itemId,
					destination: obj.params.destination,
					mode:  obj.params.mode,
					formId: obj.params.formId,
					submitType: 'json',
					showCancelButton: true
				},
				successCallback: {
					scope: this,
					fn: function(successResponse) {
						Bubbling.fire('', {
							bubblingLabel: '',
							form: successResponse.serverResponse.responseText
						});
					}
				},
				failureMessage: Alfresco.util.message('message.failure')
			});

			Event.stopEvent(e);
		}
	}, true);
})();
