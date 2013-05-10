<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="span10 offset1">
    <div id="error-dialog" class="alert alert-error hide"></div>

    <c:if test="${empty participants}">
        <div class="alert alert-info">
            Der er i øjeblikket hverken transmission til, eller deltagere i, telefonmødet.
        </div>
    </c:if>

    <c:if test="${not empty participants}">
        <c:forEach var="participant" items="${participants}">
            <c:if test="${participant.host}">
                <c:set var="hostDetected" value="true"/>
            </c:if>
        </c:forEach>

        <c:if test="${not hostDetected}">
            <div class="alert alert-error">
                <b>Der sendes ikke lyd til lytterne!</b>
                <ol>
                    <li>For at transmittere lyd, ring op til nummer <b>09991</b> med X-Lite; ring ikke til det 8-cifrede nummer, det er kun til brug for lytterne!</li>
                    <li>Er mødet slut, tryk <i class="icon-remove"></i> ud for hver lytter, så de ikke betaler unødigt!</li>
                    <li>Hvis X-Lite ikke vil ringe op, så tryk Exit i X-Lite og start den igen.</li>
                </ol>
            </div>
        </c:if>
    </c:if>
    <h3>Rigssal</h3>
    <div id="host-table-wrapper">
        <%@include file="hoststable.jsp" %>
    </div>
    <h3>Lyttere</h3>
    <div id="participant-table-wrapper">
        <%@include file="participantstable.jsp" %>
    </div>
</div>