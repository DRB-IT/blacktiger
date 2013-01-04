<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Room ${room.roomNumber}</h1>
        <table
            <c:forEach var="u" items="${room.users}">
                <tr>
                    <td>${u.userNumber}</td>
                    <td>${u.state}</td>
                    <td>${u.muted}</td>
                    <td>${u.channel.callerId.number}</td>
                </tr>
            </c:forEach>
        </table>    
    </body>
</html>
