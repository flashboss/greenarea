<%-- 
    Document   : AddMissionStep2
    Created on : 27-feb-2012, 13.51.40
    Author     : MacRed
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ include file="/jstl.inc"%>
<!DOCTYPE html>
<html>
<head>
<meta name="viewport"
	content="width=device-width, minimum-scale=1.0, maximum-scale=1.0" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link rel="stylesheet" type="text/css" href="cityLog.css" />
</head>
<body>
	<div id="container">
		<h1>Info Fascia Oraria</h1>
		<table align="center">
			<html:form action="findVehicle" method="GET"
				enctype="multipart/form-data">
				<tr align="center">
					<td><html:select property="idV" onclick="setAction(this.form)">
							<c:set var="i" value="0" />
							<c:forEach begin="0" end="${sizeVe}" step="1">
								<html:option value="${liVe[i].id}">${liVe[i].makeV} - ${liVe[i].modelV} - ${liVe[i].fuelV}</html:option>
								<c:set var="i" value="${i+1}" />
							</c:forEach>
						</html:select></td>
				</tr>
				<tr align="center">
					<td><html:submit value="Seleziona il tuo Veicolo" /></td>
				</tr>
			</html:form>
		</table>

		<html:form action="addMission" method="POST"
			enctype="multipart/form-data">
			<table align="center">
				<tr>
					<td>Invia Richiesta</td>
				</tr>
				<tr>
					<td>Nome</td>
					<td><html:text property="name" /></td>
				</tr>
				<tr>
					<td>Compagnia</td>
					<td><html:text property="company" /></td>
				</tr>
				<tr>
					<td>Targa</td>
					<td><html:text property="idVehicle" /></td>
				</tr>
				<tr>
					<td>Data Missione</td>
					<td><html:select property="dateMiss">
							<c:set var="i" value="0" />
							<c:forEach begin="0" end="${liScheSi}" step="1">
								<html:option value="${liSche[i].timeSlot}">${liSche[i].timeSlot}</html:option>
								<c:set var="i" value="${i+1}" />
							</c:forEach>
						</html:select></td>
				</tr>
			</table>
			<table border=1 bordercolor="black" align="center">
				<tr align="center">
					<td>Parametro</td>
					<td>Max / Min</td>
				</tr>
				<c:forEach var="par" items="${liPar}">
					<c:choose>
						<c:when test="${par.namePG eq 'euro'}">
							<tr align="center">
								<td>${par.namePG}</td>
								<td>${par.maxVal}/ ${liPar[1].minVal}</td>
							</tr>
							<tr align="center">
								<td>${ve.EURO}</td>
								<td><html:text property="euro" value="${ve.emissionV}" /></td>
							</tr>
						</c:when>
						<c:when test="${par.namePG eq 'peso'}">
							<tr align="center">
								<td>${par.namePG}</td>
								<td>${par.maxVal}/ ${liPar[1].minVal}</td>
							</tr>
							<tr align="center">
								<td>Valore:</td>
								<td><html:text property="peso" value="${ve.weightV}" /></td>
							</tr>
						</c:when>
						<c:when test="${par.namePG eq 'lunghezza'}">
							<tr align="center">
								<td>${par.namePG}</td>
								<td>${par.maxVal}/ ${liPar[1].minVal}</td>
							</tr>
							<tr align="center">
								<td>Valore:</td>
								<td><html:text property="lunghezza" value="${ve.lenghtV}" /></td>
							</tr>
						</c:when>
						<c:otherwise>
							<tr align="center">
								<td>${par.namePG}</td>
								<td>${par.maxVal}/ ${par.minVal}</td>
							</tr>
							<tr align="center">
								<td>Valore:</td>
								<td><html:text property="${par.namePG}" /></td>
							</tr>
						</c:otherwise>
					</c:choose>
				</c:forEach>
				<tr align="center">
					<td COLSPAN="2"><html:submit value="Inserisci Missione" /></td>
				</tr>
			</table>

		</html:form>

	</div>
</body>
</html>
