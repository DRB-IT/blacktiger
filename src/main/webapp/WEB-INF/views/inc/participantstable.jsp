<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<table class="table table-striped table-hover participantsTable">
    <tbody>
        <c:forEach var="participant" items="${participants}">
            <c:if test="${not participant.host}">
                <c:set var="participantsDetected" value="true"/>
                <c:set var="name" value="${participant.name}"/>
                <c:if test="${empty name}"><c:set var="name" value="Ukendt"/></c:if>
                <tr <c:if test="${not participant.muted}">class="warning"</c:if>>
                    <td class="span2 participantNumber" data-type="participant-number" data-id="${participant.userId}" data-number="${participant.phoneNumber}">${participant.phoneNumber}</td>
                    <td class="participantName">
                        <span data-type="participant-name" data-id="${participant.userId}" contenteditable="false">${name}</span> 
                        <span data-type="participant-name-controls" data-id="${participant.userId}" class="hide">
                            <button data-type="participant-name-save" data-id="${participant.userId}" class="btn btn-small" title="<spring:message code="general.save"/>"><i class="icon-ok"></i></button> 
                            <button data-type="participant-name-cancel" data-id="${participant.userId}" class="btn btn-small" title="<spring:message code="general.cancel"/>"><i class="icon-remove"></i></button>
                        </span>
                        <c:if test="${not participant.muted}"><span class="label label-warning"><spring:message code="participantstable.info.unmuted"/></span></c:if></td>
                        <td class="span3 participantOptions">
                            <div class="btn-group">
                                <button class="btn btn-small" title="<spring:message code="participantstable.edit"/>" data-type="edit-participant" data-id="${participant.userId}"><i class="icon-pencil"></i></button>
                                <c:choose>
                                    <c:when test="${participant.muted}">
                                    <button class="btn btn-small" title="<spring:message code="participantstable.unmute"/>" data-type="unmute-participant" data-id="${participant.userId}"><i class="icon-comment"></i></button>
                                    </c:when>
                                    <c:otherwise>
                                    <button class="btn btn-small btn-warning" title="<spring:message code="participantstable.mute"/>" data-type="mute-participant" data-id="${participant.userId}"><i class="icon-volume-off"></i></button>
                                    </c:otherwise>
                                </c:choose>
                            <button class="btn btn-small" title="<spring:message code="participantstable.kick"/>" data-type="kick-participant" data-id="${participant.userId}"><i class="icon-remove"></i></button>
                        </div>
                    </td>
                </tr>
            </c:if>
        </c:forEach>

        <c:if test="${not participantsDetected}">
            <tr>
                <td>
                    <spring:message code="participantstable.info.no_participants"/>
                </td>
            </tr>
        </c:if>

        <!--tr class="error">
            <td class="span2 participantNumber">22736623</td>
            <td class="participantName">Ukendt lytter <span class="label label-important">Tag stilling!</span></td>
            <td class="span3 participantOptions">
                <div class="btn-group">
                    <button class="btn btn-small" title="Rediger"><i class="icon-pencil"></i></button>
                    <button class="btn btn-small disabled" title="Åben svarmikrofon"><i class="icon-comment"></i></button>
                    <button class="btn btn-small" title="Afbryd lytter"><i class="icon-remove"></i></button>
                </div>
            </td>
        </tr--> 
    </tbody>
</table>