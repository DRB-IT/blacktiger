<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html lang="<c:out value="${pageContext.request.locale.language}"/>">
    <head>
        <title><spring:message code="general.log_in"/> - TeleSal</title>
        <%@include file="inc/headtags.jsp" %>
    </head>
    <body onload='document.f.j_username.focus();'>
        <div class="container">
            <div class="row" id="ContentContainer">

                <div class="span5 offset3" style="padding-top: 30px;">
                    <h3><spring:message code="log_in.title"/></h3>
                    <br />
                    <c:set var="error" value="${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}"/>
                    <c:if test="${not empty error}">
                        <div class="alert">
                            <spring:message code="log_in.error.text1"/><br />
                            <spring:message code="log_in.error.text2" arguments="${error}"/>
                        </div>
                    </c:if>
                    <form name="f" class="form-horizontal" action="<c:url value="/j_spring_security_check"/>" method="POST">
                        <div class="control-group">
                            <label class="control-label" for="inputEmail"><spring:message code="general.username"/></label>
                            <div class="controls">
                                <input type="text" id="inputEmail" placeholder="<spring:message code="general.username"/>" name='j_username' value='' />
                            </div>
                        </div>
                        <div class="control-group">
                            <label class="control-label" for="inputPassword"><spring:message code="general.password"/></label>
                            <div class="controls">
                                <input type="password" id="inputPassword" placeholder="<spring:message code="general.password"/>" name='j_password' />
                            </div>
                        </div>
                        <div class="control-group">
                            <div class="controls">
                                <label class="checkbox">
                                    <input type="checkbox" name="_spring_security_remember_me"> Remember me
                                </label>
                                <button type="submit" name="submit" class="btn"><spring:message code="general.log_in"/></button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
            <div class="row">
                <div class="span12" id="FooterContainer">&copy; <spring:message code="system.copyrightholder"/></div>
            </div>
        </div>
        <%@include file="inc/scripts.jsp" %>
    </body>
</html>