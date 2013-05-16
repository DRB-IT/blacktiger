<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
                                <td><input type="text" value="2013-05-10" disabled class="input-small"></b></td>
                                <td>
                                    <select name="hourStart" class="input-small">
                                        <option selected value="0">00:00</option>
                                        <option value="6">06:00</option>
                                        <option value="7">07:00</option>
                                        <option value="8">08:00</option>
                                        <option value="9">09:00</option>
                                        <option value="10">10:00</option>
                                        <option value="11">11:00</option>
                                        <option value="12">12:00</option>
                                        <option value="13">13:00</option>
                                        <option value="14">14:00</option>
                                        <option value="15">15:00</option>
                                        <option value="16">16:00</option>
                                        <option value="17">17:00</option>
                                        <option value="18">18:00</option>
                                        <option value="19">19:00</option>
                                        <option value="20">20:00</option>
                                    </select>
                                </td>
                                <td>
                                    <select name="hourEnd" class="input-small">
                                        <option value="8">07:59</option>
                                        <option value="9">08:59</option>
                                        <option value="10">09:59</option>
                                        <option value="11">10:59</option>
                                        <option value="12">11:59</option>
                                        <option value="13">12:59</option>
                                        <option value="14">13:59</option>
                                        <option value="15">14:59</option>
                                        <option value="16">15:59</option>
                                        <option value="17">16:59</option>
                                        <option value="18">17:59</option>
                                        <option value="19">18:59</option>
                                        <option value="20">19:59</option>
                                        <option value="21">20:59</option>
                                        <option value="22">21:59</option>
                                        <option value="23">22:59</option>
                                        <option value="24" selected>23:59</option>
                                    </select>
                                </td>
                                <td><input type="number" name="duration" class="input-small" value="0" ></td>
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
                BlackTiger.updatePhonebookEntry(number, name, function() {

                });
            }
            
            BlackTiger.init("<c:url value="/"/>");
                
        </script>
    </body>
</html>
