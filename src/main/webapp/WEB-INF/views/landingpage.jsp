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
      <h1><spring:message code="system.name"/></h1>
      <h2><spring:message code="landingpage.title"/></h2>
      <p>
      <center><a href="<c:url value="/login"/>">Log ind</a> </center>
      </p>
      <p>&copy; <spring:message code="system.copyrightholder"/></p>
    </div>
  </body>
</html>