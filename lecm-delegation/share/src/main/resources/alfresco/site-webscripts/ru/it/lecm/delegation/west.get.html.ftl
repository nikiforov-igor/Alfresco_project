<script type="text/javascript"> //<![CDATA[
	(function () {
		var delegationWest = new LogicECM.module.Delegation.West ("west");
		delegationWest.setMessages(${messages});
//		delegationWest.initListeners("buttons", ["testButtonId", "mybutton"]);
	})();
//]]>
</script>

<div id="west">
	<div>${myItem}</div>
	<div id="buttons">
		<div id="buttons-myButton"></div>
	</div>
</div>
<!--
<div id="buttons">
	<input type="button" id="testButtonId" name="testButton" label="йа кнопко" title="йа кнопочко">

	<span id="mybutton" class="yui-button">
		<span class="first-child">
			<button type="button">Click Me</button>
		</span>
	</span>
</div>
-->
