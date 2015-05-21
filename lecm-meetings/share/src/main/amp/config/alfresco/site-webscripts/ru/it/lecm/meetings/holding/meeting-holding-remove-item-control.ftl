<div id="${fieldHtmlId}-parent" class="control meeting-remove-item viewmode">
    <span id="${fieldHtmlId}-remove-item-button" class="yui-button yui-push-button">
        <span class="first-child">
            <button type="button" title="${msg('label.button.meeting.holding.remove.item')}"></button>
        </span>
    </span>
</div>

<script type="text/javascript">
	(function () {
        function loadSources() {
            LogicECM.module.Base.Util.loadResources([], [
                'css/lecm-meetings/meeting-holding-remove-item-control.css'
            ], init);
        }

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

		YAHOO.util.Event.onAvailable("${fieldHtmlId}-remove-item-button", loadSources);
    })();
</script>