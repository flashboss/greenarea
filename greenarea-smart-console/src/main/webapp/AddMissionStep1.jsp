<%-- 
    Document   : AddMissionStep1
    Created on : 27-feb-2012, 13.51.30
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
        <title>Fasce Orarie Disponibili</title>
    </head>
    <body>
        <div id="container">
        <h1>Seleziona una Fascia Oraria</h1>
  
            <html:form action="selectTimeSlot" method="POST" enctype="multipart/form-data">
                <html:select property="idTimeSlot">
             <c:set var="i" value="0" />
            <c:forEach begin="0" end="${size}" step="1">
              <html:option value="${liTiSlo[i].idTS}">${liTiSlo[i].startTS} <-> ${liTiSlo[i].finishTS}</html:option>
             <c:set var="i" value="${i+1}" />
            </c:forEach>
           
            </html:select>
            <html:submit value="Inserisci"/>
            </html:form>
        </div>
 </body>
</html>
