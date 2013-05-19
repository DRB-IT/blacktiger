<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<table class="table table-striped table-hover">
    <thead>
        <tr>
            <td class="span2"><spring:message code="reporttable.number"/></td>
            <td><spring:message code="reporttable.name"/></td>
            <td class="span2"><spring:message code="reporttable.first_call"/></td>
            <td class="span1"><spring:message code="reporttable.calls"/></td>
            <td class="span1"><spring:message code="reporttable.minutes"/></td>
            <td class="span1"></td>
        </tr>
    </thead>
    <tbody>
        <c:set var="durationSum" value="0"/>
        <c:set var="callSum" value="0"/>

        <c:forEach var="callinfo" items="${callInfos}">
            <c:set var="durationSum" value="${durationSum + callinfo.totalDuration}"/>
            <c:set var="callSum" value="${callSum + callinfo.numberOfCalls}"/>
            <c:set var="name" value="${callinfo.name}"/>
            <c:if test="${empty name}"><spring:message var="name" code="participantstable.unknown"/></c:if>
            <tr>
                <td class="participantNumber">${callinfo.phoneNumber}</td>
                <td class="participantName">
                    <span contenteditable="false" data-type="callinfo-name" data-number="${callinfo.phoneNumber}">${name}</span>
                    <span data-type="callinfo-name-controls" data-number="${callinfo.phoneNumber}" class="hide">
                        <button data-type="callinfo-name-save" data-number="${callinfo.phoneNumber}" class="btn btn-small" title="<spring:message code="general.save"/>"><i class="icon-ok"></i></button> 
                        <button data-type="callinfo-name-cancel" data-number="${callinfo.phoneNumber}" class="btn btn-small" title="<spring:message code="general.cancel"/>"><i class="icon-remove"></i></button>
                    </span>
                </td>
                <td><fmt:formatDate type="both" dateStyle="short" timeStyle="short" value="${callinfo.firstCallTimestamp}" /></td>
                <td>${callinfo.numberOfCalls}</td>
                <td><fmt:formatNumber type="number" maxFractionDigits="2" value="${callinfo.totalDuration/60}" /></td>
                <td class="participantOptions"><button class="btn btn-small" data-type="edit-callinfo" data-number="${callinfo.phoneNumber}"><i class="icon-pencil"></i></button></td>
            </tr>
        </c:forEach>

    </tbody>
    <tfoot>
        <tr style="font-weight: bold;">
            <td colspan="3"><spring:message code="reporttable.total"/>: ${callInfos.size()}</td>
            <td>${callSum}</td>
            <td><fmt:formatNumber type="number" maxFractionDigits="2" value="${durationSum/60}" /></td>
            <td></td>
        </tr>
    </tfoot>
</table>
