<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Projekt</title>
</head>
<style>
body {
	margin: auto;
}

h1 {
	font-family: 'Nobile', Helvetica, Arial, sans-serif;
	color: #2D365F;
	font-size: 40px;
	text-align: center;
	padding: 8px 0px;
}

ul {
	list-style-type: none;
	background-color: #2D365F;
	text-align: center;
	padding-top: 10px 0px;
}

li {
	padding: 10px 0px;
}

li a {
	text-decoration: none;
	color: white;
	padding: 8px 0px;
	font-family: 'Nobile', Helvetica, Arial, sans-serif;
	font-size: 20px;
}
</style>
<body>
	<h1>Projekt iz predmeta Napredne web tehnologije i servisi</h1>
	<ul>
		<li><a
			href="${pageContext.servletContext.contextPath}/mvc/kontrolerPutanje/registracija">Registracija</a></li>

		<li><a
			href="${pageContext.servletContext.contextPath}/mvc/kontrolerPutanje/prijava">Prijava</a></li>

	</ul>
</body>
</html>