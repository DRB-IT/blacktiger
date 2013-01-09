<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Room</title>
        <script src="<c:url value="/js/jquery-1.8.3.min.js"/>"></script>
        <script src="<c:url value="/js/blacktiger.js"/>"></script>
    </head>
    <body>
        <h1>Room</h1>
        <audio class="aud">
		<p>Oops, looks like your browser doesn't support HTML 5 audio.</p>
	</audio>
        
        <table id="participant-table">
            <tbody>
                <c:forEach var="p" items="${participants}">
                    <tr>
                        <td>${p.userId}</td>
                        <td>${p.muted}</td>
                        <td>${p.phoneNumber}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>  

        <script>
            function updateList() {
                BlackTiger.listParticipants("09991", function(data) {
                    var html = "";
                    for(var i=0;i<data.length;i++) {
                        var p = data[i];
                        html += "<tr><td>" + p.userId + "</td><td>" + p.muted + "</td><td>" + p.phoneNumber + "</td></tr>";
                    }
                    $('#participant-table tbody').html(html);
                });
            }
            
            function handleChanges() {
                BlackTiger.waitForChanges("09991", function() {
                    setTimeout(function() {
                        updateList();
                        handleChanges();
                    }, 500);
                });
            }
            
            function init() {
                BlackTiger.init("0999", "1132", "<c:url value="/data"/>");
                handleChanges();
            }
            
            $(document).ready(init);
            BlackTiger.play();

        </script>
    </body>
</html>
