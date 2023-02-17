<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
	<head>
	<meta charset="UTF-8">
	<title>Obavijest</title>
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
		<h1>Obavijest o provedenom zahtjevu</h1>
		<a href="${pageContext.servletContext.contextPath}/mvc/kontrolerPutanje/pocetak">
			Početna stranica
		</a><br>
		<p>${requestScope.odgovor}</p>
	</body>
</html>