<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign aDateTime = .now>
<#assign controlId = fieldHtmlId + "-cntrl-" + aDateTime?iso_utc>


<div class="control number viewmode">
	<div class="label-div">
		<#if field.mandatory && !(field.value?is_number) && field.value == "">
			<span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png"
		                                      title="${msg("form.field.incomplete")}"/><span>
		</#if>
		<label>${field.label?html}:</label>
	</div>
	<div class="container">
		<div class="value-div">
			<div id="${controlId}"></div>
			<select id="${fieldHtmlId}" name="${field.name}" tabindex="0" size="7">
			</select>
		</div>
	</div>
</div>
<div class="clear"></div>

<@formLib.renderFieldHelp field=field />
    <script type="text/javascript">//<![CDATA[
    (function () {
	    var nodeRef;

	    function init() {
	    LogicECM.module.Base.Util.loadScripts([
	            'scripts/lecm-contracts/contracts-based-on-document.js'
			], createControls);
		}
		function createControls(){
			new LogicECM.module.Contracts.BasedOnDocumentSelection("${fieldHtmlId}").setOptions({
	            controlId: "${controlId}",
				nodeRef: nodeRef
	    }).setMessages(${messages});
		}
		
	    function loadControl(layer, args) {
		    nodeRef = args[1].items;
		    init();
	    }

	    YAHOO.Bubbling.on("afterSetItems", loadControl);
    })();
    //]]></script>