<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html lang="<c:out value="${pageContext.request.locale.language}"/>">
  <head>
    <meta charset="utf-8" />
    <title>TeleSal</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta name="robots" content="none"/>
    <link href="<c:url value="/assets/css/Basic.css"/>" rel="stylesheet" type="text/css" />
    <link href="<c:url value="/assets/css/fontello-embedded.css"/>" rel="stylesheet" type="text/css" />
  </head>
  <body>
    <div class="intro" style="-moz-user-select: none;-webkit-user-select: none;" onselectstart="return false;">
        <table>
            <tr>
                <td>
                    <img src="<c:url value="/assets/img/tslogo57.png"/>" alt="TeleSal logo"/>
                </td>
                <td>
                    <h1><spring:message code="system.name"/></h1>
                    <h2><spring:message code="landingpage.title"/></h2>
                    <p><a href="<c:url value="/login"/>"><spring:message code="general.log_in"/></a></p>
                    <small>&copy; <spring:message code="system.copyrightholder"/></small>
                </td>
            </tr>
        </table>
    </div>
  </body>
</html>