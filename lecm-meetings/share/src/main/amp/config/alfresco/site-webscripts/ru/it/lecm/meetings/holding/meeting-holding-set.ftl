<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />
<#assign id=args.htmlid/>

<#if item??>
    <#assign thisSet = item />
<#else>
    <#assign thisSet = set />
</#if >

<@renderSet set=thisSet />

<#macro renderSet set>
    <div class="meeting-holding-set">
        <#list set.children as child>
            <#if child.kind == "set">
                <@formLib.renderSet set=child />
            <#else>
                <@formLib.renderField field=form.fields[child.id] />
            </#if>
        </#list>
    </div>
</#macro>

<script type="text/javascript">//<![CDATA[
(function() {
	function loadSources() {
		LogicECM.module.Base.Util.loadResources([], [
			'css/lecm-meetings/meeting-holding-set.css'
		]);
	}

	YAHOO.util.Event.onDOMReady(loadSources);
}) ();
//]]></script>