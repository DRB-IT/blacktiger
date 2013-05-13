<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="da">
    <head>
        <title>TeleSal</title>
        <meta name="robots" content="none" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <link href="<c:url value="/css/bootstrap.min.css"/>" rel="stylesheet" />
        <link href="<c:url value="/css/bootstrap-responsive.min.css"/>" rel="stylesheet" />
        <link href="<c:url value="/css/Basic.css"/>" rel="stylesheet" />
    </head>
    <body>
        <div class="container">
            <div class="row" id="menuMain">
                <div class="span12">
                    <div class="navbar navbar-static-top navbar-inverse">
                        <div class="navbar-inner">
                            <span class="brand">TELESAL</span>
                            <ul class="nav">
                                <li><a href="<c:url value="/"/>">Lyttere nu</a></li>
                                <li class="active"><a href="<c:url value="/reports"/>">Lytterrapport</a></li>
                                <li><a href="/help">Hjælp</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row" id="ContentContainer">
                <div class="span10 offset1">
                    <h3>Lytterrapport</h3>
                    <form>
                        <table class="table">
                            <tr>
                                <td>Opkald påbegyndt</td>
                                <td>Efter kl.</td>
                                <td>Før kl.</td>
                                <td>Min. varighed</td>
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
                                <td><button class="btn">OK</button></td>
                            </tr>
                        </table>
                    </form>
                    <%@include file="inc/reporttable.jsp" %>
                </div>
            </div>
            <div class="row">
                <div class="span12" id="FooterContainer">&copy; DRB</div>
            </div>
        </div>
        <script src="<c:url value="/js/jquery-1.8.3.min.js"/>"></script>
        <script src="<c:url value="/js/bootstrap.min.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/js/blacktiger.js"/>"></script>

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
