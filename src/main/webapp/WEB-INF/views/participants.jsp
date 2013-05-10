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
                                <li class="active"><a href="<c:url value="/"/>">Lyttere nu</a></li>
                                <!--li><a href="/play">Musik</a></li-->
                                <li><a href="<c:url value="/reports"/>">Lytterrapport</a></li>
                                <li><a href="/wiki">Hjælp</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row" id="ContentContainer">
                <%@include file="inc/participantscontent.jsp" %>
            </div>
            <div class="row">
                <div class="span12" id="FooterContainer">&copy; DRB</div>
            </div>
        </div>
        <script src="<c:url value="/js/jquery-1.8.3.min.js"/>"></script>
        <script src="<c:url value="/js/bootstrap.min.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/js/blacktiger.js"/>"></script>
        <script>
            var roomid = "${roomNo}";

            function buildMuteButton(participant) {
                if (participant.muted) {
                    return "<button class='btn button-unmute-user' data-userid='" + participant.userId + "'><i class='icon-comment'></i></button>";
                } else {
                    return "<button class='btn button-mute-user' data-userid='" + participant.userId + "'><i class='icon-volume-off'></i></button>";
                }
            }

            function listUsers() {
                $.ajax({
                    url: "<c:url value="/rooms/"/>" + roomid + "?mode=sliced"
                }).fail(function(xhr, textStatus) {
                    showError("Request for list of users returned " + xhr.status + " " + xhr.statusText);
                }).done(function(data) {
                    $("#ContentContainer").html(data);
                });
            }

            function kick(userid) {
                BlackTiger.kickParticipant(roomid, userid, listUsers);
            }

            function mute(userid) {
                BlackTiger.setParticipantMuteness(roomid, userid, true);
            }

            function unMute(userid) {
                BlackTiger.setParticipantMuteness(roomid, userid, false);
            }

            function handleChanges() {
                BlackTiger.waitForChanges(roomid, function() {
                    setTimeout(function() {
                        listUsers();
                        handleChanges();
                    }, 500);
                });
            }

            function showError(message) {
                $('#error-dialog').text(message);
                $('#error-dialog').removeClass('hide');

            }

            function init() {
                $('#testtest').on('click', function() {
                    alert();
                });
                
                $('#ContentContainer').on('click', '#participant-table-wrapper button[data-type="edit-participant"]', function() {
                    var id = $(this).attr('data-id');
                    setNameEditable(id, true);
                });

                $('#ContentContainer').on('keypress', '#participant-table-wrapper span[data-type="participant-name"]', function(event) {
                    var keycode = event.keyCode ? event.keyCode : event.which;
                    if (keycode == 13) {
                        var id = $(this).attr('data-id');
                        setNameEditable(id, false);
                        updatePhonebookEntryFromElement(id);
                        event.preventDefault();
                        return false;
                    }
                });

                $('#ContentContainer').on('click', '#participant-table-wrapper button[data-type="participant-name-save"]', function() {
                    var id = $(this).attr('data-id');
                    setNameEditable(id, false);

                    updatePhonebookEntryFromElement(id);

                });

                $('#ContentContainer').on('click', '#participant-table-wrapper button[data-type="participant-name-cancel"]', function() {
                    var id = $(this).attr('data-id');
                    setNameEditable(id, false);
                    listUsers();
                });

                $('#ContentContainer').on('click', '#host-table-wrapper button[data-type="kick-participant"]', function() {
                    kick($(this).attr('data-id'));
                });
                
                $('#ContentContainer').on('click', '#participant-table-wrapper button[data-type="kick-participant"]', function() {
                    kick($(this).attr('data-id'));
                });

                $('#ContentContainer').on('click', '#participant-table-wrapper button[data-type="mute-participant"]', function() {
                    mute($(this).attr('data-id'));
                    setTimeout(listUsers, 100);
                });

                $('#ContentContainer').on('click', '#participant-table-wrapper button[data-type="unmute-participant"]', function() {
                    unMute($(this).attr('data-id'));
                    setTimeout(listUsers, 100);
                });

                BlackTiger.init("<c:url value="/"/>");
                handleChanges();
                
                log("Initalized");
                
            }

            function setNameEditable(userId, editable) {
                $('span[data-type="participant-name"][data-id="' + userId + '"]').attr('contenteditable', editable);

                if (editable) {
                    $('span[data-type="participant-name-controls"][data-id="' + userId + '"]').removeClass('hide');
                } else {
                    $('span[data-type="participant-name-controls"][data-id="' + userId + '"]').addClass('hide');
                }
            }

            function updatePhonebookEntryFromElement(id) {
                var phone = $('*[data-type="participant-number"][data-id="' + id + '"]').attr('data-number');
                var name = $('span[data-type="participant-name"][data-id="' + id + '"]').text();
                BlackTiger.updatePhonebookEntry(phone, name, function() {
                    listUsers();
                });
            }
            
            function log(message) {
                //console.log(message);
            }

            $(document).ready(init);
        </script>
    </body>
</html>

