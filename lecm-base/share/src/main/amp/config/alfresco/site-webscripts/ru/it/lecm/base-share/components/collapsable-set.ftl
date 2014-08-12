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
    <div class="form-buttons">
        <span id="${id}-${set.id}-save-draft" class="yui-button yui-push-button">
            <span class="first-child">
                <button type="button">${msg("label.save-draft")}</button>
            </span>
        </span>
    </div>
</div>
<div class="form-buttons">
    <span id="${id}-${set.id}-expand-panel" class="yui-button yui-push-button">
        <span class="first-child">
            <button type="button">${msg("label.show-details")}</button>
        </span>
    </span>
</div>

<script type="text/javascript">//<![CDATA[
(function() {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Selector = YAHOO.util.Selector;
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

            okButton = Selector.query(".form-buttons .yui-submit-button", form, true);
            expandButton.parentNode.removeChild(expandButton);
            Dom.insertBefore(saveDraftButton, okButton);
            Bubbling.fire("addSubmitElement", saveDraftYUIButton);
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