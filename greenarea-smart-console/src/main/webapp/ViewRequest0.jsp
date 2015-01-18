<%-- 
    Document   : viewRequest
    Created on : 28-feb-2012, 14.23.44
    Author     : MacRed
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ include file="/jstl.inc" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="cilo.css"/>
    </head>
    <body>
        <div id="container">
        <h1>Inserisci Ticket</h1>
        <html:form action="viewMission" method="POST" enctype="multipart/form-data">
            <table>
                <tr><html:text property="idMission"/></tr>
                <tr><html:submit value="Visualizza Info"/></tr>
            </table>
        </html:form>
        </div>
    </body>
</html>
