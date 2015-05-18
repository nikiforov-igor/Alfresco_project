<div id="${fieldHtmlId}-parent" class="control viewmode">
	<div class="label-div"></div>
	<div class="container">
		<div class="value-div">
			<span id="${fieldHtmlId}-remove-item-button" class="yui-button yui-push-button search-icon">
                <span class="first-child">
                    <button type="button">${msg('label.button.meeting.holding.remove.item')}</button>
                </span>
            </span>
		</div>
	</div>
</div>

<script type="text/javascript">
	(function () {
		function init() {
			var button = YAHOO.util.Dom.get("${fieldHtmlId}-remove-item-button");
			if (button != null) {
				Alfresco.util.createYUIButton({id: "${fieldHtmlId}"}, "remove-item-button", deleteItem);
			}
		}

		function deleteItem() {
			YAHOO.Bubbling.fire("meetingHoldingRemoveItem",
					{
						nodeRef: "${form.arguments.itemId}"
					});
		}

		YAHOO.util.Event.onAvailable("${fieldHtmlId}-remove-item-button", init);
	})();
</script>