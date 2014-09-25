<#assign dialogId = args.htmlid>

<script type='application/javascript'>
if (typeof LogicECM == 'undefined' || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Approval = LogicECM.module.Approval || {};

(function() {

	var commentControl_id = '${dialogId}_prop_bpm_comment';
	var subscribed = false;

	YAHOO.Bubbling.on('mandatoryControlValueUpdated', function() {
		debugger;
		if (!subscribed) {
			YAHOO.Bubbling.fire('registerValidationHandler', {
				message: 'Необходимо указать комментарий',
				fieldId: commentControl_id,
				handler: commentValidation,
				when: 'keyup'
			});
			subscribed = true;
		}
	}, false);


	// Валидатор для поля 'Комментарий'
	function commentValidation (field, args, event, form) {
		var valid = false;
		var status = form.getFormData()['prop_lecmApprove3_decision'];
		if (status === 'REJECTED' || status == 'APPROVED_WITH_REMARK') {
			valid = form.getFormData()['prop_bpm_comment'] != '';
		} else {
			valid = true;
		}
		return valid;
	}

})();
</script>
