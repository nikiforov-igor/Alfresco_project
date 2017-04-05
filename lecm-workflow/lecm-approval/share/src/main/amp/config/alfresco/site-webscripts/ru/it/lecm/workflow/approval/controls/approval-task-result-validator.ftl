<#assign dialogId = args.htmlid>

<script type='application/javascript'>
(function() {

	var commentControl_id = '${dialogId}_prop_bpm_comment';
	var subscribed = false;

	YAHOO.Bubbling.on('mandatoryControlValueUpdated', function() {
		if (!subscribed) {
			YAHOO.Bubbling.fire('registerValidationHandler', {
				message: '${msg("message.need.comment")}',
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
			valid = form.getFormData()['prop_bpm_comment'].trim() != '';
		} else {
			valid = true;
		}
		return valid;
	}

})();
</script>
