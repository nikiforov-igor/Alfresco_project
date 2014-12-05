<#assign dialogId = args.htmlid>

<script type='application/javascript'>
(function() {

	function onResultChanged(type, args) {
		var dicTypeSelectControl = args[1];
		var dicTypeSelect = document.getElementById(dicTypeSelectControl.id);
		var dicPlaneHidden = document.getElementById('${dialogId}_prop_lecm-dic_plane-added');
		var dicType = dicTypeSelect.value;
		var isPlane = dicTypeSelect[dicTypeSelect.selectedIndex]['data-isPlane'];

		if (dicPlaneHidden) {
			dicPlaneHidden.value = isPlane;
		}

		LogicECM.module.Base.Util.reInitializeControl('${dialogId}', 'lecm-dic:attributeForShow', {
			webscript: 'lecm/dictionary/attributes?dataType=' + dicType
		});
	}

	function createBinder() {
		var binder = new Binder({
			dialogId: '${dialogId}',
			bubblingLayer: 'mandatoryControlValueUpdated',
			components: [
				{ id: 'lecm-dic_type' },
				{ id: 'lecm-dic_attributeForShow' }
			],
			handlers: {
				'lecm-dic_type': onResultChanged
			},
			getIdFn: function(obj) {
				return obj.id;
			}
		});
	}

	LogicECM.module.Base.Util.loadScripts([
		'scripts/lecm-base/components/lecm-selectone.js',
		'scripts/lecm-base/components/binder.js'
	], createBinder);
})();
</script>
