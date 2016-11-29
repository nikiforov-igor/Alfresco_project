<#if field.control.params.rows??><#assign rows=field.control.params.rows><#else><#assign rows=2></#if>
<#if field.control.params.columns??><#assign columns=field.control.params.columns><#else><#assign columns=60></#if>


<#assign defaultValue = "">
<#if form.arguments[field.name]?has_content>
    <#assign defaultValue = form.arguments[field.name]>
</#if>

<#assign value = field.value>
<#if value == "" && defaultValue != "">
    <#assign value = defaultValue>
</#if>

<#if form.mode == "view" || field.disabled>
    <script type="text/javascript">
        (function() {
            function init() {
                LogicECM.module.Base.Util.loadCSS([
                    'css/lecm-base/components/lecm-rich-text.css'
                ]);
            }
            YAHOO.util.Event.onDOMReady(init);
        })();
    </script>
    <div class="control richtext viewmode">
        <div class="label-div">
            <#if field.mandatory && value == "">
            <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}"/><span>
            </#if>
            <label>${field.label?html}:</label>
        </div>
        <div class="container">
            <div class="value-div">
                <#if value == "">${msg("form.control.novalue")}<#else>${value}</#if>
            </div>
        </div>
    </div>
<#else>
    <script type="text/javascript">//<![CDATA[
    (function() {
        var Dom = YAHOO.util.Dom;

    	function init() {
            LogicECM.module.Base.Util.loadScripts([
                'scripts/lecm-base/components/lecm-rich-text.js'
			], createRichText);
		}
		function createRichText() {
	        new LogicECM.RichTextControl("${fieldHtmlId}").setOptions(
	            {
	                <#if form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>disabled: true,</#if>
	                currentValue: "${value?js_string}",
	                mandatory: ${field.mandatory?string},
		            editorParameters:{
			            width: "100%",
			            inline_styles: false,
			            convert_fonts_to_spans: false,
			            theme: 'advanced',
			            theme_advanced_toolbar_location: "top",
			            theme_advanced_toolbar_align: "left",
			            theme_advanced_statusbar_location: "bottom",
			            theme_advanced_path: false,
			            language: "${locale?substring(0, 2)?js_string}",
			            plugins: "fullscreen,table,paste",
                        paste_remove_styles_if_webkit: false,
			            theme_advanced_buttons1: "bold,italic,underline,strikethrough,separator,fontselect,fontsizeselect, separator, fullscreen",
			            theme_advanced_buttons2: "justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,table,separator,undo,redo,separator,forecolor,backcolor",
			            theme_advanced_buttons3: null,
                        init_instance_callback : setTabIndex
		            }
	            }).setMessages(${messages});
		}
        function setTabIndex (editor) {
            var editorId = editor.id;

            Dom.setAttribute(editorId + '_ifr', "tabindex", Dom.getAttribute(editorId, "tabindex"));
            Dom.setAttribute(editorId, "tabindex", '0');
        }
        YAHOO.util.Event.onContentReady("${fieldHtmlId}", init, true);
    })();
    //]]></script>

    <div class="control richtext editmode">
        <div class="label-div">
            <label for="${fieldHtmlId}">
                ${field.label?html}:
                <#if field.mandatory>
                    <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
                </#if>
            </label>
        </div>
        <div class="container">
            <div class="value-div">
                <textarea id="${fieldHtmlId}" name="${field.name}" rows="${rows}" columns="${columns}" tabindex="0"
                          <#if field.description??>title="${field.description}"</#if>
                          <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
                          <#if field.control.params.style??>style="${field.control.params.style}"</#if>
                          <#if field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true")>disabled="true"</#if>>${value?html}</textarea>
            </div>
        </div>
    </div>
</#if>
<div class="clear"></div>
