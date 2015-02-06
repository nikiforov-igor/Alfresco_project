<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign aDateTime = .now>
<#assign datagridId = fieldHtmlId + "-container-" + aDateTime?iso_utc>
<#assign toolbarId = fieldHtmlId + "-toolbar-" + aDateTime?iso_utc>
<#assign addFieldsFormId = toolbarId + "-addFieldsForm">

<#if field.control.params.bubblingLabel??>
	<#assign bubblingId = field.control.params.bubblingLabel + aDateTime?iso_utc/>
<#else>
	<#assign bubblingId = "custom"/>
</#if>

<div class="form-field with-grid form-attribute-table">
	<div id="${toolbarId}" class="subscribe">
		<@comp.baseToolbar toolbarId true false false>
			<div class="new-row">
				<span id="${toolbarId}-newRowButton" class="yui-button yui-push-button">
					<span class="first-child">
						<button type="button" title="${msg("label.form.attributeTable.fields.add.title")}">${msg("label.form.attributeTable.fields.add.title")}</button>
					</span>
				</span>
			</div>
			<div class="new-row">
				<span id="${toolbarId}-newFakeRowButton" class="yui-button yui-push-button">
					<span class="first-child">
						<button type="button" title="${msg("label.form.attributeTable.fake.field.add.title")}">${msg("label.form.attributeTable.fake.field.add.title")}</button>
					</span>
				</span>
			</div>
		</@comp.baseToolbar>
	</div>

	<div id="${datagridId}" class="subscribe">
		<@grid.datagrid datagridId false/>
	</div>

	<div id="${addFieldsFormId}" class="yui-panel">
		<div id="${addFieldsFormId}-head" class="hd">${msg("logicecm.view")}</div>
		<div id="${addFieldsFormId}-body" class="bd">
			<div id="${addFieldsFormId}-content" class="fields-table-content"></div>
			<div class="bdft">
				<span id="${addFieldsFormId}-add" class="yui-button yui-push-button">
					<span class="first-child">
						<button type="button" tabindex="1">${msg("button.add")}</button>
					</span>
				</span>
				<span id="${addFieldsFormId}-cancel" class="yui-button yui-push-button">
					<span class="first-child">
						<button type="button" tabindex="0">${msg("button.close")}</button>
					</span>
				</span>
			</div>
		</div>
	</div>
</div>
<@inlineScript>
(function() {

	function init() {
		new LogicECM.module.FormsEditor.AttributesTableToolbar('${toolbarId}').setMessages(${messages}).setOptions({
			bubblingLabel: '${bubblingId}',
			itemNodeRef: '${form.arguments.itemId}',
			datasourceUri: '${field.control.params.datasourceUri!""}'
		});

		new LogicECM.module.FormsEditor.AttributesDatagrid('${datagridId}').setOptions({
			usePagination: false,
			actions: [{
					type: 'datagrid-action-link-${bubblingId}',
					id: 'onActionEdit',
					permission: 'edit',
					label: '${msg("actions.edit")}'
				},{
					type: 'datagrid-action-link-${bubblingId}',
					id: 'onActionDelete',
					permission: 'delete',
					label: '${msg("actions.delete-row")}'
				}
			],
			datagridMeta:{
				sort: 'lecm-forms-editor:attr-index|true',
				itemType: 'lecm-forms-editor:attr',
				useChildQuery: true,
				nodeRef: '${form.arguments.itemId}',
				actionsConfig:{
					fullDelete:true
				}
			},
			bubblingLabel: '${bubblingId}',
			showCheckboxColumn: false
		}).setMessages(${messages}).draw();
	}

	var js = ['scripts/lecm-base/components/lecm-toolbar.js',
				'scripts/lecm-base/components/advsearch.js',
				'scripts/lecm-base/components/lecm-datagrid.js',
				'scripts/lecm-forms-editor/lecm-forms-attributes-table-toolbar.js',
				'scripts/lecm-forms-editor/lecm-forms-attribute-datagrid.js'];
	var css = ['css/lecm-forms-editor/lecm-forms-attributes-table-toolbar.css'];
	LogicECM.module.Base.Util.loadResources(js, css, init);
})();
</@>
