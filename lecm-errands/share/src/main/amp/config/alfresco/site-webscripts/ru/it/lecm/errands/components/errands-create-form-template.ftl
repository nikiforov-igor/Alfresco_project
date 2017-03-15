<#import "/org/alfresco/components/form/form.lib.ftl" as formLib />

<#assign formId=args.htmlid?js_string?html + "-form"/>
<#assign formUI><#if args.formUI??>${args.formUI}<#else>true</#if></#assign>
<#assign setCount=0/>

<#if formUI == "true">
    <@formLib.renderFormsRuntime formId=formId />
</#if>

<@formLib.renderFormContainer formId=formId>
    <#list form.structure as item>
        <#if item.kind == "set">
            <#assign setCount=setCount+1/>
            <#if setCount % 2 == 0>
                <div class="${formId}-panel hidden">
                    <@formLib.renderSet set=item/>
                </div>
            <#else>
                <@formLib.renderSet set=item/>
            </#if>
        <#else>
            <@formLib.renderField field=form.fields[item.id] />
        </#if>
    </#list>
    <div class="form-buttons ${formId}-panel hidden">
        <span id="${formId}-save-draft" class="yui-button yui-push-button">
            <span class="first-child">
                <button type="button">${msg("label.save-draft")}</button>
            </span>
        </span>
    </div>
    <div class="form-buttons">
        <span id="${formId}-expand-panel" class="yui-button yui-push-button">
            <span class="first-child">
                <button type="button">${msg("label.show-details")}</button>
            </span>
        </span>
    </div>
</@>

<script type="text/javascript">
    (function() {
        var Dom = YAHOO.util.Dom,
                Event = YAHOO.util.Event,
                Selector = YAHOO.util.Selector,
                Bubbling = YAHOO.Bubbling,
                expandButtonId = "${formId}-expand-panel",
                saveDraftButtonId = "${formId}-save-draft",
                okButton,
                saveDraftYUIButton;

        function init() {
            var expandPanel = function(e) {
                var expandButton = Dom.get(expandButtonId);
                var saveDraftButton = Dom.get(saveDraftButtonId);
                var form = Dom.get("${formId}");

                okButton = Selector.query(".form-buttons .yui-submit-button", form, true);
                expandButton.parentNode.removeChild(expandButton);
                Dom.insertBefore(saveDraftButton, okButton);
                Bubbling.fire("addSubmitElement", saveDraftYUIButton);
                Dom.removeClass(Selector.query(".${formId}-panel"), 'hidden');
            };
            var saveDraft = function() {
                var hidden = Dom.getElementBy(function(el) {
                    return el.name == "prop_lecm-errands_is-short";
                }, 'input', "${formId}");

                hidden.value = "false";
                okButton.click();
            };

            Alfresco.util.createYUIButton(this, "", expandPanel, {}, expandButtonId);
            saveDraftYUIButton = Alfresco.util.createYUIButton(this, "", saveDraft, {}, saveDraftButtonId);

            Alfresco.util.Ajax.request({
                url: Alfresco.constants.PROXY_URI + "/lecm/errands/isHideAdditionAttributes",
                successCallback: {
                    fn: function (response) {
                        var oResults = JSON.parse(response.serverResponse.responseText);
                        if (oResults && !oResults.hide) {
                            expandPanel();
                        }
                    }
                },
                failureMessage: "message.failure"
            });

        }

        Event.onContentReady("${formId}", init);
    }) ();
</script>