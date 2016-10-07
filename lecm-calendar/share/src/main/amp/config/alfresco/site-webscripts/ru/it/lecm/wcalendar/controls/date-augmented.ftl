<#if field.control.params.errorContainerID??>
	<#assign errorContainerID="error-message-container-${field.control.params.errorContainerID?string}">
<#else>
	<#assign errorContainerID="error-message-container">
</#if>

<#if field.control.params.validateHandler??>
   <#assign validateHandler=field.control.params.validateHandler>
</#if>

<#if field.control.params.messageHandler??>
   <#assign messageHandler=field.control.params.messageHandler>
</#if>

<#if field.control.params.messageText??>
   <#assign messageText=field.control.params.messageText>
</#if>

<#if messageHandler??>
   <#assign errorMessage=messageHandler>
<#elseif messageText??>
   <#assign errorMessage=messageText>
</#if>

<#if field.control.params.hideDateFormat?? && field.control.params.hideDateFormat == "true">
	<#assign hideDateFormat=true>
<#else>
	<#assign hideDateFormat=false>
</#if>

<#if field.control.params.showTime?? && field.control.params.showTime == "true">
	<#assign showTime=true>
<#else>
	<#assign showTime=false>
</#if>

<#if showTime>
	<#assign viewFormat>${msg("form.control.date-picker.view.time.format")}</#assign>
<#else>
	<#assign viewFormat>${msg("form.control.date-picker.view.date.format")}</#assign>
</#if>

<#assign disabled=field.disabled>
<#if field.control.params.forceEditable?? && field.control.params.forceEditable == "true">
   <#assign disabled=false>
</#if>

<#assign multiValued=false>
<#if field.value != "" && field.value?index_of(",") != -1>
   <#assign multiValued=true>
</#if>

<#assign currentValue = "" />
<#assign minLimit = "" />
<#assign maxLimit = "" />
<#if field.control.params.currentValueArg?? && args[field.control.params.currentValueArg]??>
	<#assign currentValue = args[field.control.params.currentValueArg] />
</#if>

<#if field.control.params.maxLimitArg?? && args[field.control.params.maxLimitArg]??>
	<#assign maxLimit = args[field.control.params.maxLimitArg] />
</#if>

<#if field.control.params.minLimitArg?? && args[field.control.params.minLimitArg]??>
	<#assign minLimit = args[field.control.params.minLimitArg] />
</#if>

<#if form.capabilities?? && form.capabilities.javascript?? && form.capabilities.javascript == false><#assign jsDisabled=true><#else><#assign jsDisabled=false></#if>

<#if form.mode == "view">
	<div class="control date-argumented viewmode">
		<div class="label-div">
			<#if field.mandatory && field.value == "">
			<span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png"
			                                      title="${msg("form.field.incomplete")}"/><span>
			</#if>
			<label>${field.label?html}:</label>
		</div>
		<div class="container">
			<div class="value-div">
				<#if field.value == "">
					${msg("form.control.novalue")}
				<#elseif !multiValued>
					${xmldate(field.value)?string(viewFormat)}
				<#else>
					<#list field.value?split(",") as dateEl>
					${xmldate(dateEl)?string(viewFormat)}<#if dateEl_has_next>,</#if>
					</#list>
				</#if>
			</div>
		</div>
	</div>
<#elseif !multiValued>
  <#if jsDisabled>
	  <div class="control date-argumented editmode">
		  <div class="label-div">
			  <label for="${fieldHtmlId}">
			  ${field.label?html}:
				  <#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if>
			  </label>
		  </div>
		  <div class="container">
			  <div class="value-div">
				  <input id="${fieldHtmlId}" name="${field.name}" type="text" value="${field.value?html}"
				         <#if field.description??>title="${field.description}"</#if> <#if disabled>disabled="true"
				         <#else>tabindex="0"</#if> />
				  <div>
					  <span class="date-format <#if hideDateFormat>hidden1</#if>">${msg("form.control.date-picker.entry.datetime.format.nojs")}</span>
				  </div>
			  </div>
		  </div>
	  </div>
  <#else>
     <#assign controlId = fieldHtmlId + "-cntrl">

     <script type="text/javascript">//<![CDATA[
     (function()
     {

        function init() {
            LogicECM.module.Base.Util.loadScripts([
                'scripts/lecm-calendar/absence/date-interval-validation.js',
                'scripts/lecm-base/components/lecm-date-picker.js',
                'scripts/lecm-calendar/absence/absence-date-picker.js',
                'components/form/date.js'
           ], createControl, ["button", "calendar"]);
        }

        function createControl() {
           var picker = new LogicECM.module.WCalendar.Absence.DatePicker("${controlId}", "${fieldHtmlId}").setOptions(
           {
              <#if form.mode == "view" || disabled>disabled: true,</#if>
			  currentValue:"${currentValue}",
		      minLimit: "${minLimit}",
			  maxLimit: "${maxLimit}",
              showTime: ${showTime?string},
              mandatory: ${field.mandatory?string},
              validateHandler: <#if validateHandler??>${validateHandler}<#else>null</#if>,
              message: <#if errorMessage??>${errorMessage}<#else>"${msg('lecm.absence.msg.wrong.value')}"</#if>

           }).setMessages(
              ${messages}
           );
           picker.draw();
        }
	     YAHOO.util.Event.onContentReady("${controlId}", init);;
     })();
     //]]></script>

	  <div class="control date date-argumented editmode">
		  <div class="label-div">
			  <label for="${controlId}-date">
			  ${field.label?html}:
				  <#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if>
			  </label>
		  </div>
		  <div class="container">
			  <#if disabled == false>
				  <div class="buttons-div">
                      <a id="${controlId}-icon" class="calendar-icon"></a>
                      <@formLib.renderFieldHelp field=field />
                  </div>
				  <div id="${controlId}" class="datepicker"></div>
			  </#if>
			  <div class="value-div">
				  <input id="${fieldHtmlId}" type="hidden" name="${field.name}" value="${field.value?html}"/>
				  <input id="${controlId}-date" name="-" type="text" class="mandatory-highlightable"
				         <#if field.description??>title="${field.description}"</#if> <#if disabled>disabled="true"
				         <#else>tabindex="0"</#if> />
				  <div>
					  <span class="date-format <#if hideDateFormat>hidden1</#if>">${msg("form.control.date-picker.display.date.format")}</span>
				  </div>

				  <#if showTime>
					  <input id="${controlId}-time" name="-" type="text" class="time-entry"
					         <#if field.description??>title="${field.description}"</#if> <#if disabled>disabled="true"
					         <#else>tabindex="0"</#if> />
					  <div>
						  <span class="time-format<#if disabled>-disabled</#if>">${msg("form.control.date-picker.display.time.format")}</span>
					  </div>
				  </#if>
				  <div id="${errorContainerID}"></div>
			  </div>
		  </div>
	  </div>
  </#if>
</#if>
<div class="clear"></div>
