<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />
<#assign id=args.htmlid/>

<div id="${id}-${set.id}-panel" class="details-panel hidden1">
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
    <span id="${id}-${set.id}-save-draft" class="yui-button yui-push-button">
        <span class="first-child">
            <button type="button">${msg("label.save-draft")}</button>
        </span>
    </span>
</div>
<span id="${id}-${set.id}-expand-panel" class="yui-button yui-push-button">
    <span class="first-child">
        <button type="button">${msg("label.show-details")}</button>
    </span>
</span>

<script type="text/javascript">//<![CDATA[
(function() {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Bubbling = YAHOO.Bubbling;
    var panelId = "${id}-${set.id}-panel",
        expandButtonId = "${id}-${set.id}-expand-panel",
        saveDraftButtonId = "${id}-${set.id}-save-draft";
    var okButton,
        saveDraftYUIButton;

    function init() {
        var expandPanel = function(e) {
            var expandButton = Dom.get(expandButtonId);
            var saveDraftButton = Dom.get(saveDraftButtonId);
            var form = Dom.getAncestorByTagName(panelId, "form");
            var formButtons = Dom.getElementsByClassName("form-buttons", "div", form);

            okButton = Dom.getElementBy(function(el) {
                return Dom.hasClass(el, "yui-submit-button");
            }, "span", formButtons[0]);
            expandButton.parentNode.removeChild(expandButton);
            Bubbling.fire("addSubmitElement", saveDraftYUIButton);
            Dom.insertBefore(saveDraftButton, okButton);
            Dom.setStyle(panelId, "display", "block");
        };
        var saveDraft = function() {
            var hidden = Dom.getElementBy(function(el) {
                return el.name == "prop_lecm-errands_is-short";
            }, 'input', panelId);

            hidden.value = "false";
            okButton.click();
        };

        Alfresco.util.createYUIButton(this, "", expandPanel, {}, expandButtonId);
        saveDraftYUIButton = Alfresco.util.createYUIButton(this, "", saveDraft, {}, saveDraftButtonId);
    }

    Event.onContentReady(panelId, init);
}) ();
//]]></script>