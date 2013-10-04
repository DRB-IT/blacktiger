<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<c:set var="area" value="participants"/>
<html lang="<c:out value="${pageContext.request.locale.language}"/>">
    <head>
        <title><spring:message code="system.name"/></title>
        <%@include file="inc/headtags.jsp" %>
    </head>
    <body>
        <div id="songplayer" class="navbar navbar-fixed-bottom">
            <div class="navbar-inner" style="padding-left:20px;vertical-align: middle">
                <a class="brand" href="#">Musik</a>
                <div class="pull-left input-append" style="margin:0px;margin-top:5px">
                    <input id="songplayer-number" type="number" pattern="[0-9]{1,3}" value="1" min="1" max="135" class="input-mini" style="text-align: right">
                    <button id="songplayer-play" class="btn"><i class="icon-play"></i></button>
                    <button id="songplayer-stop" class="btn"><i class="icon-stop"></i></button>
                    <button id="songplayer-random" class="btn"><i class="icon-random"></i></button>
                </div>
                <div id="songplayer-progress" class="pull-left progress progress-warning" style="margin:10px 20px;border:1px solid #bfbfbf;width:50px">
                    <div class="bar" style="width: 0%;"></div>
                </div>
                <div id="songplayer-title" class="lead muted pull-left" style="margin:0px;margin-top:5px"></div> 
            </div>
        </div>
        <div class="container">
            <%@include file="inc/navigation.jsp" %>

            <div class="row" id="ContentContainer">

                <%@include file="inc/participantscontent.jsp" %>
            </div>
            <%@include file="inc/foot.jsp" %>
        </div>
        <%@include file="inc/scripts.jsp" %>
        <script>
            var roomid = "${roomNo}";
            var leavingPage = false;

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
                BlackTiger.waitForChanges(roomid, function(data) {
                    if (data == true || "true" == data) {
                        setTimeout(function() {
                            listUsers();
                            handleChanges();
                        }, 500);
                    } else {
                        setTimeout(function() {
                            handleChanges();
                        }, 500);
                    }
                });
            }

            function updateHud() {
                var number = SongManager.getCurrentSong();
                var state = SongManager.getState();
                var progress = SongManager.getProgressPercent();

                //$('#songplayer-title').text(SongManager.getTitle(number));
                $('#songplayer-number').val(number);
                $('#songplayer-play').prop('disabled', state == 'playing');
                $('#songplayer-stop').prop('disabled', state == 'stopped');
                $('#songplayer-progress .bar').css("width", progress + '%');

                if (SongManager.isRandom()) {
                    $('#songplayer-random').addClass('disabled');
                } else {
                    $('#songplayer-random').removeClass('disabled');
                }

                if (state == 'playing') {
                    setTimeout(updateHud, 200);
                }

            }


            function startPlayer() {
                var number = $('#songplayer-number').val();
                SongManager.setCurrentSong(number);
                SongManager.play();
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

                /** SONGMANAGER INIT **/
                if (SongManager.isSupported()) {
                    if (Modernizr.audio.mp3) {
                        SongManager.setFileFormat("mp3");
                    } else if (Modernizr.audio.ogg) {
                        SongManager.setFileFormat("ogg");
                    } else {
                        $("#songplayer").hide();
                    }

                    $('#songplayer-number').attr('max', SongManager.getNoOfSongs());
                    $('#songplayer-number').change(function() {
                        SongManager.setCurrentSong($(this).val());
                    });
                    $('#songplayer-play').click(startPlayer);
                    $('#songplayer-stop').click(SongManager.stop);
                    $('#songplayer-random').click(function() {
                        SongManager.setRandom(!SongManager.isRandom());
                    });

                    SongManager.setChangeHandler(function() {
                        log(SongManager.getState());
                        updateHud();
                    });
                    SongManager.setCurrentSong(1);

                }

                log("Initialized");

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

                if ("" === name) {
                    BlackTiger.removePhonebookEntry(phone, listUsers);
                } else {
                    BlackTiger.updatePhonebookEntry(phone, name, listUsers);
                }
            }

            function log(message) {
                //console.log(message);
            }

            $(document).ajaxError(function(event, request, settings) {
                window.setTimeout(function() {
                    if(!leavingPage) {
                        showError("Error requesting page " + settings.url);
                    }
                }, 500);
                
            });

            $(window).on('beforeunload', function() {
                if(SongManager.getState() == 'playing') {
                    return 'Leaving the page will stop the music.';
                }
            });
            
            $(window).on('unload', function() {
                leavingPage = true;
                BlackTiger.destroy();
            });
            
            $(document).ready(init);
        </script>
    </body>
</html>

