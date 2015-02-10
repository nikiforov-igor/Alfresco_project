<#assign employees = result?keys>

<#if (employees?size > 0)>

	<#assign month = calendarHeader?keys?sort>
	<#assign curMonth = curMonthConst>
	<#assign reasonsNames = reasons?keys>
	<#assign curYear = curYearConst>

	<div id="summary-table-container">
		<table class="reasons-legend">
			<tbody>
				<tr>
					<th width="20%">${msg("lecm.calendar.absence.color")}</th>
					<th width="80%">${msg("lecm.calendar.absence.absence.reason")}</th>
				</tr>
				<#list reasonsNames?sort as reasonName>
				<tr>
					<td style="background-color: ${reasons[reasonName].color}; border-color: #000000" ></td>
					<td>${reasonName}</td>
				</tr>
				</#list>
			</tbody>
		</table>

		<table class="summary-table">
			 <tr>
				<td>
				   <table class="summary-table-header" >
						<tr cellpadding="1" class="calendar-header">
							<td class="row-id">№</td>
							<td class="employee-header employee-name">${msg("lecm.calendar.absence.employee")}</td>
							<td>
								<table>
									<tr>
									<#list month as yearAndMonth>
										<#assign monthNumber = yearAndMonth?substring(yearAndMonth?index_of("m"))>
										<td colspan="${calendarHeader[yearAndMonth]?size}" >
											<span style="max-width: ${calendarHeader[yearAndMonth]?size*21}px;">
												${monthNames[monthNumber]}
											</span>
										</td>
									</#list>
									</tr>
									<tr class="diagramm-row">
									<#list month as yearAndMonth>
										<#list calendarHeader[yearAndMonth] as day>
										<td>${day?string("00")}</td>
										</#list>
									</#list>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
					<div class="summary-table-body-wrapper">
						<table cellpadding="1" class="summary-table-body">
						<#list employees?sort as employee>
						<#assign prevMonthSize = 0>
						<#assign curMonth = curMonthConst>
						<#assign curYear = curYearConst>
							<tr <#if (employee_index % 2 == 1) >class="yui-dt-odd" </#if> >
								<td class="row-id">${employee_index + 1}</td>
								<td class="employee-name">${employee}</td>
								<td>
									<table cellspacing="0" width="100%">
										<tr class="diagramm-row">
										<#list result[employee] as day>
											<#assign curMonthStr = "y" + curYear + "m" + curMonth?string>
											<td <#if (day?length > 0)>style="background-color: ${day}; color: ${day}; border-color: ${day};"</#if>>&nbsp;</td>
											<#if (day_index == (calendarHeader[curMonthStr]?size + prevMonthSize - 1)) >
												<#assign curMonth = curMonth + 1>
												<#assign prevMonthSize =  prevMonthSize + calendarHeader[curMonthStr]?size>
												<#if curMonth == 12>
													<#assign curMonth = 0>
													<#assign curYear = curYear + 1>
												</#if>
											</#if>
										</#list>
										</tr>
									</table>
								</td>
							</tr>
						</#list>
						</table>
					</div>
				</td>
			</tr>
		</table>
	</div>
</#if>