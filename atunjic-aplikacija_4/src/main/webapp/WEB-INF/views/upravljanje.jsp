<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
	<head>
	<meta charset="UTF-8">
	<title>Upravljanje poslužiteljem</title>
	</head>
    <style>
    	body{
    		margin: auto;
    	}
        h1{
        	font-family: 'Nobile', Helvetica, Arial, sans-serif;
    		color: #2D365F;
    		font-size: 40px;
    		text-align: center;
    		padding:8px 0px;
        }
		a{
			background-color: #2D365F;
    	  	text-align: center;
			text-decoration:none; 
			color:white; 
			padding:10px 0px; 
			font-family:'Nobile', Helvetica, Arial, sans-serif;
		  	font-size: 20px;
		  	display: flow-root;
		}
    </style>
    <body>
		<h1>Upravljanje poslužiteljem</h1>
		<a href="${pageContext.servletContext.contextPath}/mvc/kontrolerPutanje/pocetak">
			Početna stranica
		</a><br>
		<p>Status poslužitelja (0:hibernira, 1:inicijaliziran, 2:aktivan): ${requestScope.status}</p>  
		
		<form method="POST" action="${pageContext.servletContext.contextPath}/mvc/kontrolerUpravljanje/upravljanjee?zeton=${requestScope.zeton}&korisnik=${requestScope.korisnik}">
		  <select name="naredba">
		    <option value="INIT">Inicijalizacija poslužitelja</option>
		    <option value="QUIT">Prekid rada poslužitelja</option>
		    <option value="LOAD">Učitavanje podataka</option>
		    <option value="CLEAR">Brisanje podataka</option>
		  </select>
		  <input type="submit" value="Pošalji">
		  
		</form>
		<p>Odgovor poslužitelja: ${requestScope.odgovor}</p>
	</body>
</html>