var BlackTiger = new function() {
    
    var serviceUrl = "api";
    var username = null;
    var password = null;
    
    this.init = function(user, pass, url) {
        username = user;
        password = pass;
        if(url != null) {
            serviceUrl = url;
        }
    }
    
    this.listParticipants = function(roomid, callback) {
        $.ajax({
            url: serviceUrl + "/rooms/" + roomid,
            username: username,
            password: password,
            headers: { 
                Accept : "application/json"
            }
        }).fail(function(xhr, textStatus) {
            if(xhr.status == 404) {
                callback(new Array());
            }
        }).done(function (data) {
            callback(data);
        });
    }
    
    this.getParticipant = function(roomid, userid, callback) {
        $.ajax({
            url: serviceUrl + "/rooms/" + roomid + "/" + userid,
            username: username,
            password: password,
            headers: { 
                Accept : "application/json"
            }
        }).fail(function(xhr, textStatus) {
            if(xhr.status == 404) {
                if (!(typeof callback === 'undefined')) {
                    callback(new Array());
                }
            }
        }).done(function (data) {
            if (!(typeof callback === 'undefined')) {
                callback(data);
            }
        });
    }
    
    this.kickParticipant = function(roomid, userid, callback) {
        $.ajax({
            url: serviceUrl + "/rooms/" + roomid + "/" + userid + "/kick",
            type: "POST",
            username: username,
            password: password,
            headers: { 
                Accept : "application/json"
            }
        }).done(function (data) {
            if (!(typeof callback === 'undefined')) {
                callback();
            }
        });
    }
    
    this.setParticipantMuteness = function(roomid, userid, muted, callback) {
        muted = muted == true;
        $.ajax({
            url: serviceUrl + "/rooms/" + roomid + "/" + userid,
            type: "POST",
            dataType: 'json',
            data: '{"muted": ' + muted + '}',
            username: username,
            password: password,
            headers: { 
                Accept : "application/json"
            }
        }).done(function (data) {
            if (!(typeof callback === 'undefined')) {
                callback(data);
            }
        });
    }
    
    this.waitForChanges = function(roomid, callback) {
        $.ajax({
            url: serviceUrl + "/rooms/" + roomid + "/changes",
            username: username,
            password: password,
            headers: { 
                Accept : "application/json"
            }
        }).done(function (data) {
            callback(data);
        });
    }
}