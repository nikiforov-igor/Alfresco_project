/* global YAHOO, Alfresco */

if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.DocumentsTemplates = LogicECM.module.DocumentsTemplates || {};

(function () {
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		Bubbling = YAHOO.Bubbling;

	LogicECM.module.DocumentsTemplates.TreeView = function (containerId, options, messages) {
		LogicECM.module.DocumentsTemplates.TreeView.superclass.constructor.call(this, 'LogicECM.module.DocumentsTemplates.TreeView', containerId);
		this.setOptions(options);
		this.setMessages(messages);
		this.treeViewId = containerId + '-tree';
		Bubbling.on('createTemplate', this.onCreateTemplate, this);
		Bubbling.on('editTemplate', this.onEditTemplate, this);
		Bubbling.on('deleteTemplate', this.onDeleteTemplate, this);
		Bubbling.on('createNode', this.onCreateNode, this);
		Bubbling.on('editNode', this.onEditNode, this);
		Bubbling.on('deleteNode', this.onDeleteNode, this);
		return this;
	};

	YAHOO.lang.extend(LogicECM.module.DocumentsTemplates.TreeView, Alfresco.component.Base, {

		nodeRef: null,

		xpath: null,

		treeViewId: null,

		currentParentNode: null,

		currentEditNode: null,

		currentDeleteNode: null,

		options: {
			bubblingLabel: null
		},

		_hasEventInterest: function (obj) {
			if (!this.options.bubblingLabel || !obj || !obj.bubblingLabel) {
				return true;
			} else {
				return this.options.bubblingLabel === obj.bubblingLabel;
			}
		},

		_createInsituEditors: function (node) {
			node.children.forEach(function (childNode) {
				if (childNode.data.config && !childNode.data.insituEditor) {
					childNode.data.config.treeNode = childNode;
					childNode.data.config.container = childNode.getContentEl();
					childNode.data.config.context = childNode.getContentEl().parentElement;
					switch(childNode.data.config.itemKind) {
						case 'type':
							childNode.data.insituEditor = new Alfresco.widget.InsituEditorTemplateCreate(null, childNode.data.config);
							break;
						case 'node':
						childNode.data.insituEditor = new Alfresco.widget.InsituEditorTemplateDelete(null, childNode.data.config);
							break;
					}
				}
				if (childNode.expanded && !childNode.isLeaf) {
					this._createInsituEditors(childNode);
				}
			}, this);
		},

		_deleteInsituEditors: function (node) {
			node.children.forEach(function (childNode) {
				if (childNode.data.insituEditor) {
					delete childNode.data.insituEditor;
				}
				if (childNode.expanded && !childNode.isLeaf) {
					this._deleteInsituEditors(childNode);
				}
			}, this);
		},

		_loadTypesData: function (node, fnLoadComplete) {
			/* this == LogicECM.module.DocumentsTemplates.TreeView */
			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.PROXY_URI + 'lecm/documents/types',
				successCallback: {
					scope: this,
					fn: function (successResponse) {
						var types = successResponse.json.data;
						types.forEach(function (type) {
							new YAHOO.widget.TextNode({
								label: type.name,
								value: type.value,
								config: {
									showDelay: 100,
									hideDelay: 200,
									autoDismissDelay: 0,
									disabled: false,
									value: type.value,
									title: this.msg('template-tree-action-create.title'),
									itemKind: 'type',
									itemId: 'lecm-template:document-template',
									mode: 'create',
									destination: this.nodeRef,
									formId: ''
								}
							}, node).setDynamicLoad(this.bind(this._loadTemplatesData));
						}, this);
						if (YAHOO.lang.isFunction(fnLoadComplete)) {
							fnLoadComplete.call(node);
						}
					}
				},
				failureCallback: {
					scope: this,
					fn: function (failureResponse) {
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

		_loadTemplatesData: function (node, fnLoadComplete) {
			/* this == LogicECM.module.DocumentsTemplates.TreeView */
			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.PROXY_URI + 'lecm/forms/picker/node/children',
				dataObj: {
					selectableType: 'lecm-template:document-template',
					searchTerm: '',
					size: 100,
					sortProp: 'cm:created',
					additionalFilter: '@lecm\\-template\\:doc\\-type:"' + node.data.value + '"',
					xpath: this.xpath,
					nameSubstituteString: '{cm:title}',
					titleNameSubstituteString: '{cm:title}'
				},
				successCallback: {
					scope: this,
					fn: function (successResponse) {
						var templates = successResponse.json.data.items;
						templates.forEach(function (template) {
							new YAHOO.widget.TextNode({
								label: template.title,
								value: template.nodeRef,
								isLeaf: true,
								config: {
									showDelay: 100,
									hideDelay: 200,
									autoDismissDelay: 0,
									disabled: false,
									value: template.nodeRef,
									title: this.msg('template-tree-action-delete.title'),
									itemKind: 'node',
									itemId: template.nodeRef,
									mode: 'edit',
									formId: ''
								}
							}, node);
						}, this);
						if (YAHOO.lang.isFunction(fnLoadComplete)) {
							fnLoadComplete.call(node);
						}
					}
				},
				failureCallback: {
					scope: this,
					fn: function (failureResponse) {
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

		onExpandComplete: function (node, tree) {
			this._createInsituEditors(node);
		},

		onTreeNodeClicked: function (obj, tree) {
			if (obj.node.isLeaf) {
				this.currentEditNode = obj.node;
				Bubbling.fire('editTemplate', {
					bubblingLabel: 'documentsTemplatesTreeView',
					params: obj.node.data.config
				});
			}
			return tree.onEventToggleHighlight(obj);
		},

		onCreateTemplate: function (layer, args) {
			var params;
			if (this._hasEventInterest(args[1])) {
				Bubbling.fire('beforeTemplate', {
					bubblingLabel: 'documentsTemplatesDetailsView'
				});
				params = args[1].params;
				this.currentParentNode = params.treeNode;
				Alfresco.util.Ajax.request({
					url: Alfresco.constants.URL_SERVICECONTEXT + 'lecm/components/form',
					dataObj: {
						htmlid: Alfresco.util.generateDomId(),
						itemKind: params.itemKind,
						itemId: params.itemId,
						destination: params.destination,
						mode: params.mode,
						formId: params.formId,
						submitType: 'json',
						showSubmitButton: false,
						initFields: JSON.stringify({
							"lecm-template:doc-type": params.value
						}),
						showCaption: false
					},
					successCallback: {
						scope: this,
						fn: function (successResponse) {
							Bubbling.fire('templateCreated', {
								bubblingLabel: 'documentsTemplatesDetailsView',
								html: successResponse.serverResponse.responseText,
								htmlid: successResponse.config.dataObj.htmlid
							});
						}
					},
					failureMessage: Alfresco.util.message('message.failure'),
					execScripts: true
				});
			}
		},

		onEditTemplate: function (layer, args) {
			var params;
			if (this._hasEventInterest(args[1])) {
				Bubbling.fire('beforeTemplate', {
					bubblingLabel: 'documentsTemplatesDetailsView'
				});
				params = args[1].params;
				Alfresco.util.Ajax.request({
					url: Alfresco.constants.URL_SERVICECONTEXT + 'lecm/components/form',
					dataObj: {
						htmlid: Alfresco.util.generateDomId(),
						itemKind: params.itemKind,
						itemId: params.itemId,
						mode: params.mode,
						formId: params.formId,
						submitType: 'json',
						showSubmitButton: false,
						showCaption: false
					},
					successCallback: {
						scope: this,
						fn: function (successResponse) {
							Bubbling.fire('templateEdited', {
								bubblingLabel: 'documentsTemplatesDetailsView',
								html: successResponse.serverResponse.responseText,
								htmlid: successResponse.config.dataObj.htmlid
							});
						}
					},
					failureMessage: Alfresco.util.message('message.failure'),
					execScripts: true
				});
			}
		},

		onDeleteTemplate: function (layer, args) {
			var params;
			if (this._hasEventInterest(args[1])) {
				params = args[1].params;
				this.currentDeleteNode = params.treeNode;
				Alfresco.util.Ajax.jsonPost({
					url: Alfresco.constants.PROXY_URI + 'lecm/base/action/delete?alf_method=delete&full=true&trash=false',
					dataObj: {
						nodeRefs: [this.currentDeleteNode.data.value]
					},
					successCallback: {
						scope: this,
						fn: function (successResponse) {
							//если что-то удалилось
							var result;
							if (successResponse.json.totalResults > 0) {
								result = successResponse.json.results[0];
								if (result.success) {
									// если мы удаляем ту же самую ноду, карточка которой открыта справа
									if (this.currentEditNode && result.nodeRef === this.currentEditNode.data.value) {
										this.currentEditNode = null;
										Bubbling.fire('beforeTemplate', {
											bubblingLabel: 'documentsTemplatesDetailsView'
										});
									}
									// удаляем ноду из дерева
									Bubbling.fire('deleteNode', {
										bubblingLabel: 'documentsTemplatesTreeView',
										deleteResult: result
									});
									Alfresco.util.PopupManager.displayMessage({
										text: this.msg('template-tree-successfull-template-deletion.title')
									});
								}
							}
						}
					},
					failureMessage: Alfresco.util.message('message.failure')
				});
			}
		},

		onCreateNode: function (layer, args) {
			var nodeRef,
				formData;
			if (this._hasEventInterest(args[1]) && this.currentParentNode) {
				nodeRef = args[1].nodeRef;
				formData = args[1].formData;
				if (this.currentParentNode.data.value === formData['prop_lecm-template_doc-type']) {
					this.currentEditNode = new YAHOO.widget.TextNode({
						label: formData.prop_cm_title,
						value: nodeRef,
						isLeaf: true,
						config: {
							showDelay: 100,
							hideDelay: 200,
							autoDismissDelay: 0,
							disabled: false,
							value: nodeRef,
							title: this.msg('template-tree-action-edit.title'),
							itemKind: 'node',
							itemId: nodeRef,
							mode: 'edit',
							formId: ''
						}
					}, this.currentParentNode);
					if (this.currentParentNode.expanded) {
						this._deleteInsituEditors(this.currentParentNode);
						this.currentParentNode.refresh();
						this._createInsituEditors(this.currentParentNode);
						this.currentEditNode.highlight();
					}
				}
			}
		},

		onEditNode: function (layer, args) {
			var nodeRef,
				formData;
			if (this._hasEventInterest(args[1]) && this.currentEditNode) {
				nodeRef = args[1].nodeRef;
				formData = args[1].formData;
				if (this.currentEditNode.data.value === nodeRef) {
					this.currentEditNode.label = formData.prop_cm_title;
					this.currentEditNode.getLabelEl().innerHTML = formData.prop_cm_title;
				}
			}
		},

		onDeleteNode: function (layer, args) {
			if (this._hasEventInterest(args[1]) && this.currentDeleteNode) {
				this._deleteInsituEditors(this.widgets.treeRoot);
				this.widgets.treeView.removeNode(this.currentDeleteNode, true);
				delete this.currentDeleteNode;
				this._createInsituEditors(this.widgets.treeRoot);
			}
		},

		onReady: function () {
			console.log(this.name + '[' + this.id + '] is ready');
			this.widgets.treeView = new YAHOO.widget.TreeView(this.treeViewId);
			this.widgets.treeView.singleNodeHighlight = true;
			this.widgets.treeView.data = {
				documentsTemplates: this
			};
			this.widgets.treeRoot = new YAHOO.widget.TextNode({
				label: this.msg('template-tree-root.title'),
				expanded: true
			}, this.widgets.treeView.getRoot());
			this.widgets.treeRoot.setDynamicLoad(this.bind(this._loadTypesData));
			this.widgets.treeView.subscribe('expandComplete', this.onExpandComplete, this.widgets.treeView, this);
			this.widgets.treeView.subscribe('clickEvent', this.onTreeNodeClicked, this.widgets.treeView, this);

			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.PROXY_URI + 'lecm/documents-templates/root',
				successCallback: {
					scope: this,
					fn: function (successResponse) {
						this.nodeRef = successResponse.json.nodeRef;
						this.xpath = successResponse.json.xpath;
						this.widgets.treeView.draw();
					}
				},
				failureMessage: this.msg('message.failure')
			});
		}
	}, true);

	Alfresco.widget.InsituEditorTemplateCreate = function (p_editor, p_params) {
		var span = document.createElement('span');
		Alfresco.widget.InsituEditorTemplateCreate.superclass.constructor.call(this, p_editor, p_params);
		Dom.removeClass(this.editIcon, 'insitu-edit');
		Dom.addClass(this.editIcon, 'insitu-create');
		span.innerHTML = '&nbsp;&nbsp;&nbsp;';
		this.params.container.appendChild(span);
		return this;
	};

	YAHOO.lang.extend(Alfresco.widget.InsituEditorTemplateCreate, Alfresco.widget.InsituEditorIcon, {

		onIconClick: function (e, obj) {
			if (obj.disabled) {
				return;
			}

			Bubbling.fire('createTemplate', {
				bubblingLabel: 'documentsTemplatesTreeView',
				params: obj.params
			});

			Event.stopEvent(e);
		}
	}, true);

	Alfresco.widget.InsituEditorTemplateDelete = function (p_editor, p_params) {
		var span = document.createElement('span');
		Alfresco.widget.InsituEditorTemplateDelete.superclass.constructor.call(this, p_editor, p_params);
		Dom.removeClass(this.editIcon, 'insitu-edit');
		Dom.addClass(this.editIcon, 'insitu-delete');
		span.innerHTML = '&nbsp;&nbsp;&nbsp;';
		this.params.container.appendChild(span);
		return this;
	};

	YAHOO.lang.extend(Alfresco.widget.InsituEditorTemplateDelete, Alfresco.widget.InsituEditorIcon, {

		onIconClick: function (e, obj) {
			if (obj.disabled) {
				return;
			}

			Alfresco.util.PopupManager.displayPrompt({
				title: obj.params.treeNode.tree.data.documentsTemplates.msg('template-tree-template-deletion.title'),
				text: obj.params.treeNode.tree.data.documentsTemplates.msg('template-tree-template-deletion-text.title'),
				modal: true,
				buttons: [{
					text: obj.params.treeNode.tree.data.documentsTemplates.msg('template-tree-template-deletion-yes.title'),
					handler: function () {
						Bubbling.fire('deleteTemplate', {
							bubblingLabel: 'documentsTemplatesTreeView',
							params: obj.params
						});
						this.destroy();
					},
					isDefault: true
				}, {
					text: obj.params.treeNode.tree.data.documentsTemplates.msg('template-tree-template-deletion-no.title'),
					handler: function () {
						this.destroy();
					}
				}]
			});
			Event.stopEvent(e);
		}
	}, true);
})();
