<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />

<#assign htmlid = args.htmlid?js_string/>
<#assign startDateHtmlId = ""/>
<#assign endDateHtmlId = ""/>
<#assign unlimitHtmlId = ""/>
<@renderDateRange set=set />

<#macro renderDateRange set>
	<div class="daterange-unlimited">
        <#if set.children[2]??>
            <#assign unlimited = set.children[2]/>
            <#assign unlimitHtmlId = (htmlid + "_" + unlimited.id)/>
            <@formLib.renderField field=form.fields[unlimited.id] />
        </#if>
	</div>
	<div id="${htmlid}" class="daterange-set two-columns">
        <div class="column">
            <div class="daterange-start-date">
                <#assign start = set.children[0]/>
               	<#assign startDateHtmlId = (htmlid + "_" + start.id)/>
                <@formLib.renderField field=form.fields[start.id] />
            </div>
        </div>
        <div class="column last">
            <div class="daterange-end-date">
                <#assign end = set.children[1]/>
                <#assign endDateHtmlId = (htmlid + "_" + end.id)/>
                <@formLib.renderField field=form.fields[end.id] />
            </div>
        </div>
        <div class="clear"></div>
	</div>
</#macro>

<script type="text/javascript">
(function() {
	function init() {
        (function() {
            LogicECM.module.Base.Util.loadCSS([
                'css/lecm-base/components/daterange.css'
            ]);
        })();
        LogicECM.module.Base.Util.loadScripts([
            'scripts/lecm-base/components/lecm-datarange.js',
		    'scripts/lecm-base/components/lecm-date-picker.js'
		], createDateRange);
	}
	function createDateRange(){
		new LogicECM.DateRange("${htmlid}").setOptions({
			startDateHtmlId: "${startDateHtmlId}",
			endDateHtmlId: "${endDateHtmlId}",
			unlimitedHtmlId: "${unlimitHtmlId}"
		}).setMessages(${messages});
	}
	YAHOO.util.Event.onDOMReady(init);
})();
</script>
