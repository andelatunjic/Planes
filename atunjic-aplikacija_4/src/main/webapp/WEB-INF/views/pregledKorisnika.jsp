<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Pregled korisnika</title>
<link rel="stylesheet" type="text/css"
	href="https://cdn.datatables.net/v/dt/dt-1.11.5/datatables.min.css" />
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

.pocetna {
	background-color: #2D365F;
	text-align: center;
	text-decoration: none;
	color: white;
	padding: 10px 0px;
	font-family: 'Nobile', Helvetica, Arial, sans-serif;
	font-size: 20px;
	display: flow-root;
}

td {
	text-align: center;
}
</style>
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
	<h1>Pregled korisnika i brisanje tokena</h1>

	<ul>
		<li><a
			href="${pageContext.servletContext.contextPath}/mvc/kontrolerPutanje/pocetak">Početna
				stranica</a></li>
		<li><a
			href="${pageContext.servletContext.contextPath}/mvc/kontrolerUpravljanje/upravljanje?zeton=${requestScope.zeton}&korisnik=${requestScope.korisnik}">
				Upravljanje poslužiteljem</a></li>
	</ul>
	<br>

	<a
		href="${pageContext.servletContext.contextPath}/mvc/kontrolerKorisnici/obrisi?zeton=${requestScope.zeton}&korisnik=${requestScope.korisnik}&lozinka=${requestScope.lozinka}">
		Obriši svoj aktivni token: </a>
	<p>${requestScope.zeton}</p>

	<table id="korisnici" class="display" style="width: 100%">
		<thead>
			<tr>
				<th>Korisnik</th>
				<th>Ime</th>
				<th>Prezime</th>
				<th>Lozinka</th>
				<th>E-mail</th>
				<th>Brisanje</th>
			</tr>
		<thead>
		<tbody>

			<c:forEach var="k" items="${requestScope.korisnici}">
				<tr>
					<td>${k.korIme}</td>
					<td>${k.ime}</td>
					<td>${k.prezime}</td>
					<td>${k.lozinka}</td>
					<td>${k.email}</td>
					<c:if test="${requestScope.provjera == true}">

						<td><a
							href="${pageContext.servletContext.contextPath}/mvc/kontrolerKorisnici/obrisiTokene?korIme=${k.korIme}&korisnik=${requestScope.korisnik}&lozinka=${requestScope.lozinka}">

								Obriši tokene ovog korisnika</a></td>
					</c:if>
					<c:if test="${requestScope.provjera == false}">

						<td>Nemate ovlasti za brisanje.</a>
						</td>
					</c:if>
				</tr>
			</c:forEach>

		</tbody>
	</table>

	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
	<script type="text/javascript"
		src="https://cdn.datatables.net/v/dt/dt-1.11.5/datatables.min.js"></script>
	<script>
		$(document).ready(function() {
			$('#korisnici').DataTable();
		});
	</script>
</body>
</html>