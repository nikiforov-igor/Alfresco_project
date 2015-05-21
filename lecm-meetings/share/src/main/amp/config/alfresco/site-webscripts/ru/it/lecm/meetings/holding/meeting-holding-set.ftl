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
            <#if child_index == 1>
                <div class="meeting-items-left">
                    <@formLib.renderField field=form.fields[child.id] />
            <#elseif child_index == 3>
                </div>
                <div class="meeting-items-right">
                    <@formLib.renderField field=form.fields[child.id] />
                </div>
            <#elseif child_index == 5>
                <div class="meeting-items-bottom">
                    <@formLib.renderField field=form.fields[child.id] />
            <#else>
                <@formLib.renderField field=form.fields[child.id] />
            </#if>
        </#list>
        </div>
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