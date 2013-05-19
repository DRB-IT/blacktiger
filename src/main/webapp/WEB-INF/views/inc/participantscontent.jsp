<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<div class="span10 offset1">
    <div id="error-dialog" class="alert alert-error hide"></div>

    <c:if test="${empty participants}">
        <div class="alert alert-info">
            <spring:message code="participants.warning.no_participants_and_transmitters.text"/>
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
                <b><spring:message code="participants.warning.participants_but_no_transmitter.title"/></b>
                <ol>
                    <li><spring:message code="participants.warning.participants_but_no_transmitter.text1" arguments="09991"/></li>
                    <li><spring:message code="participants.warning.participants_but_no_transmitter.text2"/></li>
                    <li><spring:message code="participants.warning.participants_but_no_transmitter.text3"/></li>
                </ol>
            </div>
        </c:if>
    </c:if>
    <h3><spring:message code="participants.kingdom_hall"/></h3>
    <div id="host-table-wrapper">
        <%@include file="hoststable.jsp" %>
    </div>
    <h3><spring:message code="participants.participants"/></h3>
    <div id="participant-table-wrapper">
        <%@include file="participantstable.jsp" %>
    </div>
</div>