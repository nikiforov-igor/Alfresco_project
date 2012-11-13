<div class="new-row">
<input id="structure" type="radio" name="type" value="structure"/>
<label for="structure">${msg("structure.btn")}</label>
<input id="staffLists" type="radio" name="type" value="staffLists"/>
<label for="staffLists">${msg("stafflist.btn")}</label>
<input id="employees" type="radio" name="type" value="employees"/>
<label for="employees">${msg("employees.btn")}</label>
<input id="workGroups" type="radio" name="type" value="workGroups"/>
<label for="workGroups">${msg("workgroups.btn")}</label>
</div>
<script type="text/javascript">//<![CDATA[
(function () {
	function init() {
		var menu = new LogicECM.module.OrgStructure.Menu();
		menu.draw();
	}
	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>
