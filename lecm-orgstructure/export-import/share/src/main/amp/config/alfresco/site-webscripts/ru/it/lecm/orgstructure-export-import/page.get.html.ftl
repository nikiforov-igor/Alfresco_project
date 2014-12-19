<!DOCTYPE HTML>
<html>
	<head>
		<title>Экспорт/импорт оргструктуры</title>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
	</head>
	<body>
		<h2>Экспорт</h2>
		<a href="/share/proxy/alfresco/lecm/ogstructure/export/positions">Штатные позиции (positions)</a>
		<br>
		<a href="/share/proxy/alfresco/lecm/ogstructure/export/employees">Сотрудники (employees)</a>
		<br>
		<a href="/share/proxy/alfresco/lecm/ogstructure/export/departments">Подразделения (departments)</a>
		<br>
		<a href="/share/proxy/alfresco/lecm/ogstructure/export/staffList">Штатное расписание (staffList)</a>
		<br>
		<a href="/share/proxy/alfresco/lecm/ogstructure/export/businessRoles">Бизнес-роли (businessRoles)</a>
		<br>
		<h2>Импорт</h2>
		<form action="/share/proxy/alfresco/lecm/ogstructure/import" method="POST" enctype="multipart/form-data">
			<table border="0">
				<tr>
					<td>Штатные позиции (positions):</td>
					<td><input type="file" name="positionsFile"></td>
				</tr>
				<tr>
					<td>Сотрудники (employees):</td>
					<td><input type="file" name="employeesFile"></td>
				</tr>
				<tr>
					<td>Подразделения (departments):</td>
					<td><input type="file" name="departmentsFile"></td>
				</tr>
				<tr>
					<td>Штатное расписание (staffList):</td>
					<td><input type="file" name="staffListFile"></td>
				</tr>
				<tr>
					<td>Бизнес-роли (businessRoles):</td>
					<td><input type="file" name="businessRolesFile"></td>
				</tr>
			</table>
			<p><input type="submit" value="Импорт"> <input type="reset"></p>
		</form>
	</body>
</html>
