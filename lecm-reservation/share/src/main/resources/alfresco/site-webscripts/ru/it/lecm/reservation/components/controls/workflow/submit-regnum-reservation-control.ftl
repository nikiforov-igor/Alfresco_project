<#if form.mode == "edit">

<script type="text/javascript">//<![CDATA[

(function()
{
   var filename = "";
   var fileRef = "";

   var actTrans = new Alfresco.ActivitiTransitions("${fieldHtmlId}");
   actTrans.setOptions(
   {
      currentValue: "${field.control.params.options?js_string}",
      hiddenFieldName: "${field.name}"
   }).setMessages(
      ${messages}
   );

   actTrans.onClick = function ActivitiTransitions_onClick(e, p_obj){
        // determine what button was pressed by it's id
        var buttonId = p_obj.get("id");

        var transitionId = buttonId.substring(this.id.length+1);

		// get the hidden field
        var hiddenField = this._getHiddenField();

        // set the hidden field value
        Dom.setAttribute(hiddenField, "value", transitionId);

        if (Alfresco.logger.isDebugEnabled())
           Alfresco.logger.debug("Set transitions hidden field to: " + transitionId);

        // generate the hidden transitions field
        this._generateTransitionsHiddenField();

		if (transitionId === this.options.transitions[1].id){
			var taskCommentDialog = new Alfresco.util.createYUIPanel("reservation-reject-comment-dialog",
			{ width:"400px",
			  height:"170px",
			  fixedcenter:true,
			  close: false,
			  modal:true,
			  buttons: [
						 {
						   text: this.msg("button.ok"),
						   isDefault: true,
						   handler:
						   {
							 fn: function onComment()
								 {
									 var comment = document.getElementById("reservation-reject-comment-string").value;
									 var commentEl = Dom.get("${args.htmlid}_prop_reservationWf_comment-added");
									 commentEl.value = comment;
									 taskCommentDialog.hide();
									 Alfresco.util.submitForm(p_obj.getForm());

								 },
							 scope: this
						   }
						 },
						 {
						   text: this.msg("button.cancel"),
						   handler:
						   {
							 fn: function onCancel()
								 {
								   taskCommentDialog.hide();
								 },
							 scope: this
						   }
						 }
					   ]
			});
			taskCommentDialog.setHeader("${msg('ru.it.reservation.title.rejectReason')}");
			taskCommentDialog.show();
		}
		else{
			Alfresco.util.submitForm(p_obj.getForm());
		}
   };
})();
//]]></script>

<div class="form-field suggested-actions" id="${fieldHtmlId}">
   <div id="${fieldHtmlId}-buttons">
   </div>
</div>

<div id="reservation-reject-comment-dialog" class="yui-pe-content" style=" width:1px; height:1px; visibility: hidden;" >
	<div class="hd" id="dialog-header"></div>
	<div class="bd">
		<table width="100%" class="form-table">
			<tr>
				<td align="center">
					<div class="yui-dt-liner">
					   <textarea id="reservation-reject-comment-string" style="resize:none; width:90%; height:70px;" placeholder="${msg('ru.it.reservation.field.comment')}"></textarea>
					</div>
				</td>
			</tr>
		</table>
	</div>
</div>

</#if>