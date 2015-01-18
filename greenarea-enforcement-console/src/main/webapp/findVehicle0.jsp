<%-- 
    Document   : AddMissionStep0
    Created on : 27-feb-2012, 13.51.01
    Author     : 
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ include file="/jstl.inc" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width,minimum-scale=1.0, maximum-scale=1.0" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="cityLog.css"/>
        <title>Check Vehicle</title>
    </head>
    <body>
        <div id="container">
        <h1>Pagina Utente</h1>
        <table align="center"> 
        <tr align="center"><td><img src="IMG/logoVige.jpg" border="0" width="180" ></td></tr>
        </table>
        <table table align="center">
            <tr align="center"><td>Veicolo: </td><td>${req.userName}</td></tr>
            <tr align="center"><td>Targa :</td><td>${req.carPlate}</td></tr>
            <tr align="center"><td>Stato :</td><td>${status}</td></tr>
            <tr align="center"><td>Pedaggio :</td><td>${req.price}</td></tr>
            <tr align="center"><td>EcoColor : </td><td>${req.color}</td></tr>
        </table>
       
        
        
        </div>
    </body>
</html>

