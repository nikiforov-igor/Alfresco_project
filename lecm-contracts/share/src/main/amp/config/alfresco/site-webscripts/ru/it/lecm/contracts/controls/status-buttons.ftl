<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />
<#if form.mode == "edit">
<#assign id=args.htmlid/>
    <span id="${fieldHtmlId}-start-job" class="yui-button yui-push-button left1">
        <span class="first-child">
            <button type="button">${msg("label.contracts.start-stage")}</button>
        </span>
    </span>
    <span id="${fieldHtmlId}-end-job" class="yui-button yui-push-button left1">
        <span class="first-child">
            <button type="button">${msg("label.contracts.end-stage")}</button>
        </span>
    </span>

<script type="text/javascript">//<![CDATA[
(function() {
    var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event,
            Bubbling = YAHOO.Bubbling;
    var panelId = "${id}-form-container",
        startButtonId = "${fieldHtmlId}-start-job",
        endButtonId = "${fieldHtmlId}-end-job";

    function init() {
        var okButton = Dom.get("${id}-form-submit");

        var startButtonClick = function() {
            var hidden = Dom.getElementBy(function(el) {
                return el.name == "prop_lecm-contract-table-structure_stage-status";
            }, 'select', panelId);
            hidden.value = "${msg('label.in-work')}";
            var okButton = Dom.get("${id}-form-submit");
            okButton.click();
        };
        var button = Alfresco.util.createYUIButton(this, "start", startButtonClick, {}, startButtonId);
        Bubbling.fire("addSubmitElement", button);
        Dom.insertBefore(Dom.get(startButtonId), okButton);

        var endButtonClick = function() {
            var hidden = Dom.getElementBy(function(el) {
                return el.name == "prop_lecm-contract-table-structure_stage-status";
            }, 'select', panelId);
            hidden.value = "${msg('label.closed')}";
            var okButton = Dom.get("${id}-form-submit");
            okButton.click();
        };
        button = Alfresco.util.createYUIButton(this, "end", endButtonClick, {}, endButtonId);
        Bubbling.fire("addSubmitElement", button);
        Dom.insertBefore(Dom.get(endButtonId), okButton);

        Dom.setStyle(panelId, "display", "block");
    }

    Event.onContentReady(panelId, init);
}) ();
//]]></script>
</#if>