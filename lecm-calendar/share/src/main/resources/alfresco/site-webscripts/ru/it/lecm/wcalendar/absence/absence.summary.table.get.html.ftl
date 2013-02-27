<#assign employees = result?keys>
<!-- ${debug} -->
<#if (employees?size > 0)>

	<#assign month = calendarHeader?keys>
	<#assign curMonth = curMonthConst>
	<#assign reasonsNames = reasons?keys>

	<div id="summary-table-container">
		<table class="reasons-legend">
			<tbody>
				<tr>
					<th width="20%">Цвет</th>
					<th width="80%">Причина отсутствия</th>
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
							<td class="employee-header employee-name">Сотрудник</td>
							<td>
								<table>
									<tr>
									<#list month as m>
										<td colspan="${calendarHeader[m]?size}" >
											<span style="display: inline-block; overflow: hidden; white-space: nowrap; max-width: ${calendarHeader[m]?size*21}px;">
												${monthNames[m]}
											</span>
										</td>
									</#list>
									</tr>
									<tr class="diagramm-row">
									<#list month as m>
										<#list calendarHeader[m] as day>
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
							<tr <#if (employee_index % 2 == 1) >class="yui-dt-odd" </#if> >
								<td class="row-id">${employee_index + 1}</td>
								<td class="employee-name">${employee}</td>
								<td>
									<table cellspacing="0" width="100%">
										<tr class="diagramm-row">
										<#list result[employee] as day>
											<#assign curMonthStr = "m" + curMonth?string>
											<td <#if (day?length > 0)>style="background-color: ${day}; color: ${day}; border-color: ${day};" </#if> >&nbsp;</td>
											<#if (day_index == (calendarHeader[curMonthStr]?size + prevMonthSize - 1)) >
												<#assign curMonth = curMonth + 1>
												<#assign prevMonthSize =  prevMonthSize + calendarHeader[curMonthStr]?size>
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