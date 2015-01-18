<%-- 
    Document   : ViewRequest1
    Created on : 28-feb-2012, 14.43.14
    Author     : MacRed
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ include file="/jstl.inc" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        
        <link rel="stylesheet" type="text/css" href="cityLog.css"/>
        <title>JSP Page</title>
    </head>
    <body>
        <div id="container">
        <h1>Ciao ${request.userName}</h1>
        <h3>Missione ${request.idMission} Processata </h3>
        <table align="center">
            <tr align="center"><td>Pagamento : </td><td>${request.price} €</td></tr>
            <tr align="center"><td>Eco Color :</td><td>${request.color}</td></tr>
            <tr align="center"><td>Targa :</td><td>${request.carPlate}</td></tr>
            
        </table>
             <html:link page="/AddMissionStep0.jsp">HOME</html:link>
            
        </div>
    </body>
</html>
