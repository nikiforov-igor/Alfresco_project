<#assign dialogId = args.htmlid>

<script type='application/javascript'>
(function() {

	var commentControl_id = '${dialogId}_prop_bpm_comment';

	// YAHOO.util.Event.onContentReady("${dialogId}_prop_bpm_comment", function(){
	// 	var	commentFormField = getControlContainer(document.getElementById(commentControl_id));
	// 	commentFormField.style.display='none';
	// }, false);

	function createBinder() {

		var binder = new Binder({
			bubblingLayer: 'mandatoryControlValueUpdated',
			components: [{
							id: 'lecmApprove3_decision'
						}
						],
			dialogId: '${dialogId}',
			handlers: {
				'lecmApprove3_decision': onResultChanged
			},
			getIdFn: function(obj) {
				return obj.id;
			}
		});

	}

	var loader = new YAHOO.util.YUILoader({
		require: [
			'binder'
		],
		skin: {}
	});

	loader.addModule({
		name: 'binder',
		type: 'js',
		fullpath: Alfresco.constants.URL_RESCONTEXT + 'scripts/lecm-base/components/binder.js'
	});

	loader.onSuccess = createBinder;
	loader.insert(null, 'js');

	// Функция для получения самого верхнего элемента с классом control для данного элемента
	function getControlContainer(el){
		if(el.parentNode.className.match(/\bcontrol\b/)) return el.parentNode;
		return getControlContainer(el.parentNode);
	}

	// Валидатор для поля "Комментарий"
	function commentValidation(field, args, event, form) {
		var valid = false;
		var status = form.getFormData()['prop_lecmApprove3_decision'];
		if (status === 'REJECTED' || status == 'APPROVED_WITH_REMARK') {
			valid = form.getFormData()['prop_bpm_comment'] != '';
		} else {
			valid = true;
		}
		return valid;
	}

	function onResultChanged(type, args) {

		var resultValue = document.getElementById(args[1].options.hiddenFieldId).value;
		var	commentFormField = getControlContainer(document.getElementById(commentControl_id));

		YAHOO.Bubbling.fire("registerValidationHandler",
		{
			message: 'Необходимо указать комментарий',
			fieldId: commentControl_id,
			handler: commentValidation,
			when: "keyup"
		});

		// if (resultValue === '' || resultValue === 'SIGNED') {
		// 	commentFormField.style.display='none';
		// } else if (resultValue === 'REJECTED') {
		// 	commentFormField.style.display='block';
		// } else {
		// 	commentFormField.style.display='none';
		// }
	}

})();
</script>
