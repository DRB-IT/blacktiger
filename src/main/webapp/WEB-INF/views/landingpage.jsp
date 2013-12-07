<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html lang="<c:out value="${pageContext.request.locale.language}"/>">
  <head>
    <meta charset="utf-8" />
    <meta name="robots" content="noindex, nofollow" />
    <title>TeleSal</title>
    <link href="<c:url value="/assets/css/bootstrap.min.css"/>" rel="stylesheet" type="text/css" />
    <link href="<c:url value="/assets/css/basic.css"/>" rel="stylesheet" type="text/css" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="shortcut icon" href="<c:url value="assets/img/favicon.ico"/>" />
    <link rel="apple-touch-icon-precomposed" sizes="144x144" href="<c:url value="assets/img/tslogo144.png"/>" />
    <link rel="icon" href="<c:url value="assets/img/icon_favicon.gif"/>" />
    <meta name="msapplication-TileImage" content="<c:url value="assets/img/tslogo144-ms.png"/>" />
    <meta name="msapplication-TileColor" content="#f89406" />
  </head>
  <body>
    <div class="intro" style="-moz-user-select: none;-webkit-user-select: none;" onselectstart="return false;">
        <img src="<c:url value="/img/tslogo114.png"/>" alt="TeleSal logo" style="margin-bottom: 10px;" />
    </div>
  </body>
</html>