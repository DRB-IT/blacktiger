<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="da">
    <head>
        <meta charset="utf-8" />
        <title>TeleSal</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <link href="<c:url value="/css/bootstrap.min.css"/>" rel="stylesheet" type="text/css" />
        <link href="<c:url value="/css/basic.css"/>" rel="stylesheet" type="text/css" />
        <link rel="shortcut icon" type="image/ico" href="<c:url value="/img/favicon.ico"/>" />
        <link rel="apple-touch-icon" href="<c:url value="/img/touch-57.png"/>" />
        <link rel="apple-touch-icon" sizes="72x72" href="<c:url value="/img/touch-72.png"/>" />
        <link rel="apple-touch-icon" sizes="114x114" href="<c:url value="/img/touch-114.png"/>" />
        <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"></script>
        <script type="text/javascript" src="<c:url value="/js/bootstrap.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/js/blacktiger.js"/>"></script>
        <script>
            var roomid = "09991";
            
            function listUsers() {
                BlackTiger.listParticipants(roomid, function(data) {
                    var html = ""
                    for(var i = 0; i<data.length; i++) {
                        var p = data[i];
                        html += "<tr><td>" + p.userId + "</td><td>" + p.phoneNumber + "</td><td>" + p.muted + "</td><td><button class='btn button-kick-user' data-userid='" + p.userId + "'>Læg p&aring;</button></td></tr>";
                    }
                    $("#participant-table tbody").html(html);
                });
            }
            
            function kick(userid) {
                BlackTiger.kickParticipant(roomid, userid, listUsers);
            }
            
            function mute() {
                BlackTiger.setParticipantMuteness(roomid, userid, true);
            }
            
            function unMute() {
                BlackTiger.setParticipantMuteness(roomid, userid, false);
            }
            
            function handleChanges() {
                BlackTiger.waitForChanges("09991", function() {
                    setTimeout(function() {
                        listUsers();
                        handleChanges();
                    }, 500);
                });
            }
            
            function init() {
                $('#participant-table').on('click',' .button-kick-user', function() {
                    kick($(this).attr('data-userid'));
                });

                BlackTiger.init("<c:url value="/data"/>");
                
                handleChanges();
                

            }

        </script>
    </head>
    <body>
        <div class="container">
            <!--[if lt IE 8]> <div class='alert'><b>SERVICEMEDDELELSE:</b> Du bruger en forældet version af Internet Explorer. <a href="http://windows.microsoft.com/da-DK/internet-explorer/products/ie/home" traget="_blank">Opgradér til den nyeste version</a>.</div> <![endif]-->
            <header>
                <div class="row" id="headerContent">
                    <div class="span3" id="headerLogo"><span>TeleSal</span></div>
                    <div class="span9 hidden-phone" id="headerSlogan">Transmission af møder</div>
                </div>
            </header>
            <div class="row">
                <div class="span12" id="menuContent">
                    <nav>
                        <ul class="mainMenu">
                            <li><a href="../lyttere-nu" class="selected">Lyttere nu</a></li>
                            <li><a href="../rapport">Lytterrapport</a></li>
                            <li><a href="/wiki">Hjælp</a></li>
                        </ul>
                    </nav>
                </div>
            </div>
            <div class="row" id="mainContent">
                <article>
                    <div class="span10 offset1">
                        <table id="participant-table" class="table table-bordered">
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Nummer</th>
                                    <th>Muted</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="p" items="${participants}">
                                    <tr>
                                        <td>${p.userId}</td>
                                        <td>${p.phoneNumber}</td>
                                        <td>${p.muted}</td>
                                        <td>
                                            <div class="btn-group">
                                                <button class="btn button-mute-user" data-userid='${p.userId}'>Mute</button>
                                                <button class='btn button-kick-user' data-userid='${p.userId}'>Læg p&aring;</button>

                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </article>
            </div>
            <div class="row">
                <div class="span12" id="footerContent">
                    <footer>&copy; Det Regionale Byggeudvalg</footer>
                </div>
            </div>
        </div>
        <script>
            $(document).ready(init);
        </script>
    </body>
</html>
