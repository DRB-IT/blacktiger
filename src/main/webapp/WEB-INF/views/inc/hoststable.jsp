<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<table class="table table-striped table-hover participantsTable">
    <tbody>
        <c:forEach var="participant" items="${participants}">
            <c:if test="${participant.host}">
                <c:set var="hostsDetected" value="true"/>    
                <tr>
                    <td class="span2 participantNumber">${participant.phoneNumber}</td>
                    <td class="participantName">${participant.name}</td>
                    <td class="span3 participantOptions">
                        <button class="btn btn-danger btn-small" data-type="kick-participant" data-id="${participant.userId}" title="Afbryd transmissionen"><i class="icon-off icon-white"></i></button>
                    </td>
                </tr>
            </c:if>
        </c:forEach>

        <c:if test="${not hostsDetected}">
            <tr>
                <td>
                    Der er pt. ingen transmission fra rigssalen.
                </td>
            </tr>
        </c:if>
    </tbody>
</table>