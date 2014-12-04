<#assign dialogId = args.htmlid>

<script type='application/javascript'>
(function() {

	function onResultChanged(type, args) {
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
		'scripts/lecm-base/components/binder.js'
	], createBinder, ['scripts/lecm-base/components/lecm-selectone.js']);
})();
</script>
