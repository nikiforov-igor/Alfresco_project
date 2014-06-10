<#assign dialogId = args.htmlid>

<#assign bubblingLayer = field.control.params.bubblingLayer>
<#assign components    = field.control.params.components?replace(" ", "")?split(",")>
<#assign componentIds  = field.control.params.componentIds?replace(" ", "")?split(",")>
<#assign inCreateOrEditMode = form.mode == "create" || form.mode == "edit">

<#if inCreateOrEditMode>
<script type='application/javascript'>
(function() {
	function onContractorChange(type, args) {
		function onAjaxSuccess(response) {
			function nodeRef(obj) { return obj.nodeRef; }
			function isPrimary(obj) { return obj.isPrimary; }

			var representatives = response.json.representatives;
			var allowedNodes = representatives.map(nodeRef);
			var primary = representatives.filter(isPrimary)[0];

			rprTree.options.allowedNodes = allowedNodes;
			rprTree._updateItems(rprTree.options.rootNodeRef, '');

			autoComp.options.allowedNodes = allowedNodes;
			autoComp.dataArray.length = 0;
			autoComp.populateData();

			if(primary) {
				rprTree.addSelectedItem(primary.nodeRef);
			}
		}

		var i, k, rprTree, autoComp,
			rprTreeQuery = {
				id: 'lecm-contract_representative-assoc',
				name: 'AssociationTreeViewer'
			},
			autoCompQuery = {
				id: 'lecm-contract_representative-assoc',
				name: 'LogicECM.module.AssociationAutoComplete'
			};


			rprTree = this.components.filter(function(v) {
			return (v.id.indexOf(rprTreeQuery.id) >= 0) && (v.name.indexOf(rprTreeQuery.name) >= 0);
			})[0];

			autoComp = this.components.filter(function(v) {
			return (v.id.indexOf(autoCompQuery.id) >= 0) && (v.name.indexOf(autoCompQuery.name) >= 0);
			})[0];


		var cntSelected = args[1].selectedItems;
		var rprSelected = rprTree.selectedItems;
		var autoSelected = autoComp.selectedItems;

		var Ajax = Alfresco.util.Ajax;

		if(cntSelected.length == 0) {
			rprTree.options.allowedNodes.length = 0;
			autoComp.options.allowedNodes.length = 0;
			autoComp.dataArray.length = 0;
			autoComp.populateData();

			for(k in rprSelected) { delete rprSelected[k]; }
			for(k in autoSelected) { delete autoSelected[k]; }

			rprTree._updateItems(rprTree.options.rootNodeRef, '');

			rprTree.updateFormFields(true /* clearCurrentDisplayValue */);
			autoComp.updateFormFields(true /* clearCurrentDisplayValue */);

			return;
		}

		Ajax.jsonRequest({
			method: 'POST',
			url: Alfresco.constants.PROXY_URI_RELATIVE + '/lecm/contractors/getrepresentatives',
			dataObj: { targetContractor: cntSelected[0] },
			successCallback: {
				fn: onAjaxSuccess,
				scope: this
			}
		});
	}

	function init() {
		LogicECM.module.Base.Util.loadScripts([
			'scripts/lecm-base/components/binder.js'
		], createBinder, ['scripts/lecm-base/components/association-tree/association-tree-view.js',
						  'scripts/lecm-base/components/association-tree/association-tree-view.js',
						  'scripts/lecm-base/components/lecm-association-autocomplete.js']);
	}

	function createBinder() {
		var binder = new Binder({
			bubblingLayer: '${bubblingLayer}',
			components: [<#list components as cmpnt>${cmpnt}<#if cmpnt_has_next>,</#if></#list>],
			dialogId: '${dialogId}',
			handlers: {
			'lecm-contract_partner-assoc': onContractorChange
			}
		});
	}

	var deferred = new Alfresco.util.Deferred([<#list componentIds as id>'${id}'<#if id_has_next>,</#if></#list>], {
		scope: this,
		fn: init
	});

	<#list componentIds as id>
	YAHOO.util.Event.onContentReady('${dialogId}_${id}', function() {
		deferred.fulfil('${id}');
	});
	</#list>
})();
</script>
</#if>