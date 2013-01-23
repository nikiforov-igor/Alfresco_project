<#include "/org/alfresco/components/form/form.lib.ftl">
<#if formUI == "true">
	<@formLib.renderFormsRuntime formId=formId />
</#if>


<div>
<@formLib.renderFormContainer formId=formId>
	<#list form.structure as item>
		<#if item.kind == "set">
			<@formLib.renderSet set=item />
		<#else>
			<@formLib.renderField field=form.fields[item.id] />
		</#if>
	</#list>
	<div class="form-field">
		<label> </label>
		<input type="radio" name="reiteration-type" value="week-days" checked=true> ${msg("label.shedule.form.week-days-type")}
	</div>
	<div class="form-field">
		<div>
			<p>
				<input type="checkbox" name="w1" value=true> Понедельник<br/>
				<input type="checkbox" name="w2" value=true> Вторник<br/>
				<input type="checkbox" name="w3" value=true> Среда<br/>
				<input type="checkbox" name="w4" value=true> Четверг<br/>
				<input type="checkbox" name="w5" value=true> Пятница<br/>
				<input type="checkbox" name="w6" value=true> Суббота<br/>
				<input type="checkbox" name="w7" value=true> Воскресенье<br/>
			</p>
		</div>
	</div>
	<div class="form-field">
		<label> </label>
		<input type="radio" name="reiteration-type" value="month-days"> ${msg("label.shedule.form.month-days-type")} (через запятую, напр: 1,3,5,6)
	</div>
	<div class="form-field">
		<label>${msg("label.shedule.form.month-days")}<span class="mandatory-indicator">*</span></label>
		<input name="month-days" tabindex="0" type="text" value="">
	</div>
	<div class="form-field">
		<input type="radio" name="reiteration-type" value="shift-work"> ${msg("label.shedule.form.shift-work-type")}
	</div>
	<div class="form-field">
		<label>${msg("label.shedule.form.workind-days-amount")}:<span class="mandatory-indicator">*</span></label>
		<input name="working-days-amount" tabindex="0" type="text" value="">
	</div>
	<div class="form-field">
		<label>${msg("label.shedule.form.workind-days-interval")}<span class="mandatory-indicator">*</span></label>
		<input name="working-days-interval" tabindex="0" type="text" value="">
	</div>
</@>
</div>

