<%-- 
    Document   : AddMissionStep3
    Created on : 27-feb-2012, 13.52.03
    Author     : MacRed
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ include file="/jstl.inc" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width,minimum-scale=1.0, maximum-scale=1.0" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        
        <link rel="stylesheet" type="text/css" href="cityLog.css"/>
    </head>
    <body>
        <div id="container">
        <h1>Missione Aggiunta</h1>
        <p>Importante!!! : Conservare il ticket</p>
        <table>
            <tr><td>Ticket</td><td>${mission.id}</td></tr>
            <tr><td></td><td></td></tr>
            <tr><td></td><td></td></tr>
            <tr><td></td><td></td></tr>
            <tr><td></td><td></td></tr>
            <tr><td></td><td></td></tr>
        </table>
            <html:link page="/AddMissionStep0.jsp">HOME</html:link>
      </div>  
    </body>
</html>
