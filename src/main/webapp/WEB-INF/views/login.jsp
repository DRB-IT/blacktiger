<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html lang="<c:out value="${pageContext.request.locale.language}"/>">
    <head>
        <title>TeleSal - <spring:message code="general.log_in"/></title>
        <%@include file="inc/headtags.jsp" %>
    </head>
    <body onload='document.f.j_username.focus();'>
        <div class="container-fluid-header">
            <span class="brand"><img src="assets/img/tslogo50.png" alt="TeleSal logo" /><span><spring:message code="system.name"/></span></span>
        </div>
        <div class="container-fluid-content">
            <div class="row">
                <div class="col-md-12" id="MusicContainer">
                    [music player here]
                </div>
            </div>
            <div class="row">
                <div class="col-sm-10 col-sm-offset-1" id="ContentContainer">
                    <div class="alert alert-info">
                        <strong>You must login</strong> to see status for the transmission. You <i>don't need</i> to login in order to play music.
                    </div>
                    <div class="row">
                        <div class="col-md-4 col-md-offset-4 col-sm-6 col-sm-offset-3">
                            <c:set var="error" value="${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}"/>
                            <c:if test="${not empty error}">
                                <div class="alert">
                                    <spring:message code="log_in.error.text1"/><br />
                                    <spring:message code="log_in.error.text2" arguments="${error}"/>
                                </div>
                            </c:if>
                            <form name="f" action="<c:url value="/j_spring_security_check"/>" method="POST">
                                <div class="form-group">
                                    <label for="inputEmail"><spring:message code="general.username"/></label>
                                    <input type="text" class="form-control" id="inputEmail" placeholder="<spring:message code="general.username"/>" name='j_username' value='' />
                                </div>
                                <div class="form-group">
                                    <label for="inputPassword"><spring:message code="general.password"/></label>
                                    <input type="password" class="form-control" id="inputPassword" placeholder="<spring:message code="general.password"/>" name='j_password' />
                                </div>
                                <div class="checkbox">
                                    <label class="checkbox">
                                        <input type="checkbox" name="_spring_security_remember_me"> Remember me
                                    </label>
                                </div>
                                <button type="submit" name="submit" class="btn btn-default"><spring:message code="general.log_in"/></button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row" id="FooterContainer">
                <div class="col-md-12 text-right">
                    <small class="text-muted"><spring:message code="system.copyrightholder"/></small>
                </div>
            </div>
        </div>
        <%@include file="inc/scripts.jsp" %>
    </body>
</html>