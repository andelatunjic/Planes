<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
	<head>
	<meta charset="UTF-8">
	<title>Registracija</title>
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
		.gumb{
			justify-content: center;
			display: block;
			color: white;
			background: #2D365F;
			font-size: 20px;
			cursor: pointer;
		}
		input{
			justify-content: center;
			display: flex;
			margin: 0px auto;
			padding: 10px;
			border: none;
			outline: none;
			border-radius: 5px;
		}
		form{
			background: #6f5fdd;
			padding: 10px 0px;
		}
    </style>
	<body>
		<h1>Registracija novog korisnika</h1>
		
		<a href="${pageContext.servletContext.contextPath}/mvc/kontrolerPutanje/pocetak">
			Početna
		</a><br>
		
		<form method="POST" action="${pageContext.servletContext.contextPath}/mvc/kontrolerRegistracije/registracija">
	    	<input type="text" name="korisnik" placeholder="Korisničko ime" required/></br>
	        <input type="text" name="ime" placeholder="Ime" required/></br>
            <input type="text" name="prezime" placeholder="Prezime" required/></br>
           	<input type="password" name="lozinka" placeholder="Lozinka" required/></br>
           	<input type="email" name="email" placeholder="Email" required/></br>
            <input class="gumb" type="submit" value="Registriraj se" />
	    </form>
	         
	</body>
</html>