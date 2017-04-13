<#assign dialogId = args.htmlid>

<script type='application/javascript'>
(function() {

	var commentControl_id = '${dialogId}_prop_bpm_comment';
	var subscribed = false;

	YAHOO.Bubbling.on('mandatoryControlValueUpdated', function() {
		if (!subscribed) {
			YAHOO.Bubbling.fire('registerValidationHandler', {
				message: '${msg("signing.message.specify.comment")}',
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
		var status = form.getFormData()['prop_lecmSign2_decision'];
		var commentContainer = field.parentNode.parentNode.parentNode;
		if (status === 'REJECTED') {
			YAHOO.util.Dom.removeClass(commentContainer, "hidden");
			valid = form.getFormData()['prop_bpm_comment'].trim() != '';
		} else {
			YAHOO.util.Dom.addClass(commentContainer, "hidden");
			valid = true;
		}
		return valid;
	}

})();
</script>
