<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<c:set var="area" value="report"/>
<html lang="<c:out value="${pageContext.request.locale.language}"/>">
    <head>
        <title><spring:message code="system.name"/></title>
        <%@include file="inc/headtags.jsp" %>
    </head>
    <body>
        <div class="container">
            <%@include file="inc/navigation.jsp" %>
            <div class="row" id="ContentContainer">
                <div class="span10 offset1">
                    <h3><spring:message code="report.title"/></h3>
                    <form>
                        <table class="table">
                            <tr>
                                <td><spring:message code="report.call_commenced"/></td>
                                <td><spring:message code="report.after_time"/></td>
                                <td><spring:message code="report.before_time"/></td>
                                <td><spring:message code="report.minimum_duration"/></td>
                                <td></td>
                            </tr>
                            <tr>
                                <td><input type="text" value="<fmt:formatDate value="${reportDate}" type="date" dateStyle="short"/>" disabled class="input-small"></b></td>
                                <td>
                                    <select name="hourStart" class="input-small">
                                        <c:forEach var="entry" items="0,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20">
                                            <option <c:if test="${entry==reportHourStart}">selected</c:if> value="${entry}">${entry}:00</option>
                                        </c:forEach>
                                    </select>
                                </td>
                                <td>
                                    <select name="hourEnd" class="input-small">
                                        <c:forEach var="entry" items="8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23">
                                            <option <c:if test="${entry==reportHourEnd}">selected</c:if> value="${entry}">${entry}:00</option>
                                        </c:forEach>
                                    </select>
                                </td>
                                <td><input type="number" name="duration" class="input-small" value="${reportMinimumDuration}" ></td>
                                <td><button class="btn"><spring:message code="general.ok"/></button></td>
                            </tr>
                        </table>
                    </form>
                    <%@include file="inc/reporttable.jsp" %>
                </div>
            </div>
            <%@include file="inc/foot.jsp" %>
        </div>
        <%@include file="inc/scripts.jsp" %>
        <script>

            $('#ContentContainer').on('click', 'button[data-type="edit-callinfo"]', function() {
                var number = $(this).attr('data-number');
                setNameEditable(number, true);
            });

            $('#ContentContainer').on('keypress', 'span[data-type="callinfo-name"]', function(event) {
                var keycode = event.keyCode ? event.keyCode : event.which;
                if (keycode == 13) {
                    var number = $(this).attr('data-number');
                    setNameEditable(number, false);
                    updatePhonebookEntryFromElement(number);
                    event.preventDefault();
                    return false;
                }
            });

            $('#ContentContainer').on('click', 'button[data-type="callinfo-name-save"]', function() {
                var number = $(this).attr('data-number');
                setNameEditable(number, false);

                updatePhonebookEntryFromElement(number);

            });
            
            $('#ContentContainer').on('click', 'button[data-type="callinfo-name-cancel"]', function() {
                var number = $(this).attr('data-number');
                setNameEditable(number, false);

                window.location.reload();

            });

            function setNameEditable(number, editable) {
                $('span[data-type="callinfo-name"][data-number="' + number + '"]').attr('contenteditable', editable);

                if (editable) {
                    $('span[data-type="callinfo-name-controls"][data-number="' + number + '"]').removeClass('hide');
                } else {
                    $('span[data-type="callinfo-name-controls"][data-number="' + number + '"]').addClass('hide');
                }
            }

            function updatePhonebookEntryFromElement(number) {
                var name = $('span[data-type="callinfo-name"][data-number="' + number + '"]').text();
                
                var callback = function() {
                    window.location.reload();
                }
                
                if("" === name) {
                    BlackTiger.removePhonebookEntry(number, callback);
                } else {
                    BlackTiger.updatePhonebookEntry(number, name, callback);
                }
                
            }
            
            BlackTiger.init("<c:url value="/"/>");
                
        </script>
    </body>
</html>
