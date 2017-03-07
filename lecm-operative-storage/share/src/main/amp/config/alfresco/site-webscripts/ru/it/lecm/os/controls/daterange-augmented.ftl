<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />

<#assign htmlid = args.htmlid?js_string/>
<#assign startDateHtmlId = ""/>
<#assign endDateHtmlId = ""/>
<#assign unlimitHtmlId = ""/>
<#assign controlId = fieldHtmlId + "-cntrl">
<@renderDateRange set=set />

<#macro renderDateRange set>
	<div class="daterange-unlimited">
        <#if set.children[2]??>
            <#assign unlimited = set.children[2]/>
            <#assign unlimitHtmlId = (htmlid + "_" + unlimited.id)/>
            <@formLib.renderField field=form.fields[unlimited.id] />
        </#if>
	</div>
	<div id="${htmlid}" class="yui-g two-column">
        <div class="yui-u first">
			<#assign start = set.children[0]/>
			<#assign startReadyId = htmlid + "_" + start.id?replace("prop_", "")?replace("_", ":") + "_" + "componentReady"/>
			<#assign startDateHtmlId = (htmlid + "_" + start.id)/>
			<@formLib.renderField field=form.fields[start.id] />
        </div>
        <div class="yui-u">
			<#assign end = set.children[1]/>
			<#assign endReadyId = htmlid + "_" + end.id?replace("prop_", "")?replace("_", ":") + "_" + "componentReady"/>
			<#assign endDateHtmlId = (htmlid + "_" + end.id)/>
			<@formLib.renderField field=form.fields[end.id] />
        </div>
        <div class="clear"></div>
	</div>
</#macro>

<script type="text/javascript">
(function() {
	function init() {
        (function() {
            LogicECM.module.Base.Util.loadCSS([
            	'css/lecm-base/components/lecm-date-picker.css',
                'css/lecm-base/components/daterange.css'
            ]);
        })();
        LogicECM.module.Base.Util.loadScripts([
            'scripts/lecm-os/controls/daterange-augmented.js',
		    'scripts/lecm-base/components/lecm-date-picker.js'
		], createDateRange, ["button", "calendar"]);
	}
	function createDateRange(){
		new LogicECM.DateRangeAugmented("${controlId}").setOptions({
			startDateHtmlId: "${startDateHtmlId}",
			endDateHtmlId: "${endDateHtmlId}",
			unlimitedHtmlId: "${unlimitHtmlId}"
		}).setMessages(${messages});
	}


	YAHOO.util.Event.onAvailable('${startReadyId}', function() {
		YAHOO.util.Event.onAvailable('${endReadyId}', init);
	});
})();
</script>
