<script type="text/javascript">//<![CDATA[
(function () {
	var delegationView = new LogicECM.module.Delegation.View ("someId");
	delegationView.setMessages(${messages});
	delegationView.init();
	delegationView.initListeners("buttons", ["testButtonId", "mybutton"]);
})();
//]]></script>
<div id="view">${myItem}</div>
<div id="buttons">
	<input type="button" id="testButtonId" name="testButton" label="йа кнопко" title="йа кнопочко">

	<span id="mybutton" class="yui-button">
		<span class="first-child">
			<button type="button">Click Me</button>
		</span>
	</span>

</div>