<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />
<#assign id=args.htmlid/>

<div id="${id}_create-event-set" class="create-event-set">
<#list set.children as item>
    <#if item_index == 0>
        <div class="event-create-right">
            <div class="event-create-actions">
                <h2 class="alfresco-twister alfresco-twister-open">
                    ${msg("label.events.actions.onCreate")}
                </h2>
                <div>
                    <ul>
                        <li class="event-save"><a id="${id}-event-action-save" href="#">${msg("button.create")}</a></li>
                        <li class="event-cancel"><a id="${id}-event-action-cancel" href="#">${msg("button.cancel")}</a></li>
                    </ul>
                </div>
            </div>
<#--            <div class="event-create-attachments">
                <h2 class="alfresco-twister alfresco-twister-open">
                    ${msg("label.events.attachments")}
                </h2>
                <@formLib.renderField field=form.fields[item.id] />
            </div>
-->
        </div>
        <div class="event-create-center">
    </#if>
    <@formLib.renderField field=form.fields[item.id] />
</#list>
    </div>
</div>

<script type="text/javascript">//<![CDATA[
(function() {
    var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event,
            Selector = YAHOO.util.Selector;
    var setId = "${id}_create-event-set";

    function loadSources() {
        LogicECM.module.Base.Util.loadResources([], [
            'css/lecm-events/event-create-set.css'
        ], init);
    }
    function init() {
        var expandedClass = "alfresco-twister-open",
            collapsedClass = "alfresco-twister-closed";
        var h2s = Selector.query(".event-create-right h2", setId);

        if (h2s && h2s.length > 0) {
            Event.addListener(h2s, "click", function() {
                var el = this;

                if (Dom.hasClass(el, collapsedClass)) {
                    Dom.replaceClass(el, collapsedClass, expandedClass);
                } else {
                    Dom.replaceClass(el, expandedClass, collapsedClass);
                }
            });
        }
    }

    Event.onDOMReady(loadSources);
}) ();
//]]></script>