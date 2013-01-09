var BlackTiger = new function() {
    
    var serviceUrl = "api";
    
    this.init = function(url) {
        if(url != null) {
            serviceUrl = url;
        }
    }
    
    this.listParticipants = function(roomid, callback) {
        $.ajax({
            url: serviceUrl + "/rooms/" + roomid,
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
            url: serviceUrl + "/rooms/" + roomid + "/changes?" + new Date().getTime()   ,
            headers: { 
                Accept : "application/json"
            }
        }).done(function (data) {
            callback(data);
        });
    }

}