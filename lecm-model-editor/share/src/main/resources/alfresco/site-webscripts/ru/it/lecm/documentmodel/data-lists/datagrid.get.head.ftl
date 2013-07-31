<#include "/org/alfresco/components/component.head.inc">

<!-- Advanced Search -->
<@script type="text/javascript" src="${page.url.context}/scripts/lecm-base/components/advsearch.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/form/date-range.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/form/number-range.js"></@script>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/search/search.css" />

<!-- Historic Properties Viewer -->
<@script type="text/javascript" src="${page.url.context}/scripts/lecm-base/components/versions.js"></@script>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/modules/document-details/historic-properties-viewer.css" />

<@script type="text/javascript" src="${page.url.context}/res/components/form/form.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/form/date.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/form/date-picker.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/form/period.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/object-finder/object-finder.js"></@script>
<script type="text/javascript" src="${page.url.context}/res/yui/calendar/calendar-${DEBUG?string("debug", "min")}.js"></script>
<script type="text/javascript" src="${page.url.context}/res/modules/editors/tiny_mce/tiny_mce${DEBUG?string("_src", "")}.js"></script>
<@script type="text/javascript" src="${page.url.context}/res/modules/editors/tiny_mce.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/form/rich-text.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/form/content.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/form/workflow/transitions.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/form/workflow/activiti-transitions.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/form/jmx/operations.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/model-editor/controls/dialog.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/model-editor/controls/input.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/model-editor/controls/select.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/model-editor/model-editor.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/model-editor/model-list.js"></@script>

<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/model-editor/model-editor.css" />

<#if config.global.forms?exists && config.global.forms.dependencies?exists && config.global.forms.dependencies.js?exists>
<#list config.global.forms.dependencies.js as jsFile>
<script type="text/javascript" src="${page.url.context}/res${jsFile}"></script>
</#list>
</#if>