<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />
<#assign id=args.htmlid/>

<div id="${id}-${set.id}-panel" class="details-panel">
    <#list set.children as item>
        <#if item.kind == "set">
            <#if item.template??>
                <#include "${item.template}" />
            <#else>
                <@formLib.renderSet set=item />
            </#if>
        <#else>
            <@formLib.renderField field=form.fields[item.id] />
        </#if>
    </#list>
</div>
<span id="${id}-${set.id}-title" class="collapse-details">
    ${msg("label.hide-details")}
</span>
<span></span><#-- Do not remove, it's useful line -->

<script type="text/javascript">//<![CDATA[
(function() {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;
    var hideDetails = "${msg("label.hide-details")}",
        showDetails = "${msg("label.show-details")}";
    var panelId = "${id}-${set.id}-panel";
    var titleId = "${id}-${set.id}-title";

    function init() {
        var title = Dom.get(titleId);
        var hidden = Dom.getElementBy(function(el) {
            return el.name == "prop_lecm-errands_is-short";
        }, 'input', panelId);

        Alfresco.util.createTwister(titleId, "", {
            panel: panelId
        });
        Event.addListener(titleId, "click", function(p_event, p_obj) {
            // Only expand/collapse if actual twister element is clicked (not for inner elements, i.e. twister actions)
            if (p_event.target == p_event.currentTarget) {
                if(Dom.hasClass(titleId, "alfresco-twister-open")){ //opened
                    title.innerHTML = hideDetails;
                    hidden.value = "false";
                } else {
                    title.innerHTML = showDetails;
                    hidden.value = "true";
                }
            }
        }, {});
        title.click(); // Потому что по умолчанию форма должна быть свернута
    }

    Event.onContentReady(panelId, init);
}) ();
//]]></script>