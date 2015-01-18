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
        <title>AddMission</title>
    </head>
    <body>
        <div id="container">
        <h1>Pagina Utente</h1>
            
        <table align="center"> 
            <tr><td><img src="IMG/logoVige.jpg" border="0" width="180" ></td></tr>
        <tr><td> <html:link action="findTimeSlot" ><img src="IMG/inviaRichiesta.gif" border="0"></html:link></td></tr>
        <tr><td><html:link forward="insertIdMission" >Info Ticket</html:link></td></tr>
        </table>
        </div>
    </body>
</html>
