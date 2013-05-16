<%@page import="java.net.URL"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<div class="row" id="menuMain">
    <div class="span12">
        <div class="navbar navbar-static-top navbar-inverse">
            <div class="navbar-inner">
                <span class="brand">TELESAL</span>
                <ul class="nav">
                    <li <c:if test="${area=='participants'}">class="active"</c:if>><a href="<c:url value="/"/>"><spring:message code="navigation.participants"/></a></li>
                    <li <c:if test="${area=='report'}">class="active"</c:if>><a href="<c:url value="/reports"/>"><spring:message code="navigation.report"/></a></li>
                    <li><a href="/wiki"><spring:message code="navigation.help"/>${pageContext.request.requestURI}</a></li>
                </ul>
            </div>
        </div>
    </div>
</div>
