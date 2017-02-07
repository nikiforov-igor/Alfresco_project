<!DOCTYPE HTML>
<html>
	<head>
		<title>${msg("title.orgstructure.export.import")}</title>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
	</head>
	<body>
		<h2>${msg("title.export")}</h2>
		<a href="${url.context}/proxy/alfresco/lecm/ogstructure/export/positions">${msg("text.staff-positions")} (positions)</a>
		<br>
		<a href="${url.context}/proxy/alfresco/lecm/ogstructure/export/employees">${msg("text.employees")} (employees)</a>
		<br>
		<a href="${url.context}/proxy/alfresco/lecm/ogstructure/export/departments">${msg("text.departments")} (departments)</a>
		<br>
		<a href="${url.context}/proxy/alfresco/lecm/ogstructure/export/staffList">${msg("text.staff-list")} (staffList)</a>
		<br>
		<a href="${url.context}/proxy/alfresco/lecm/ogstructure/export/businessRoles">${msg("text.business-roles")} (businessRoles)</a>
		<br>
		<h2>${msg("title.import")}</h2>
		<form action="${url.context}/proxy/alfresco/lecm/ogstructure/import" method="POST" enctype="multipart/form-data">
			<table border="0">
				<tr>
					<td>${msg("text.staff-positions")} (positions):</td>
					<td><input type="file" name="positionsFile"></td>
				</tr>
				<tr>
					<td>${msg("text.employees")} (employees):</td>
					<td><input type="file" name="employeesFile"></td>
				</tr>
				<tr>
					<td>${msg("text.departments")} (departments):</td>
					<td><input type="file" name="departmentsFile"></td>
				</tr>
				<tr>
					<td>${msg("text.staff-list")} (staffList):</td>
					<td><input type="file" name="staffListFile"></td>
				</tr>
				<tr>
					<td>${msg("text.business-roles")} (businessRoles):</td>
					<td><input type="file" name="businessRolesFile"></td>
				</tr>
			</table>
			<p><input type="submit" value="${msg('title.import')}"> <input type="reset"></p>
		</form>
	</body>
</html>
