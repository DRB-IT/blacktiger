var BlackTiger = new function() {
    
    var serviceUrl = "api";
    
    this.init = function(url) {
        if(url != null) {
            serviceUrl = url;
        }
    }
    
    this.updatePhonebookEntry = function(phoneNumber, name, callback) {
        $.ajax({
            url: serviceUrl + "/phonebook/" + phoneNumber,
            headers: { 
                Accept : "application/json"
            },
            data: name,
            type:"POST",
            contentType:"plain/text"
        }).fail(function(xhr, textStatus) {
            if(xhr.status == 404) {
                callback(0);
            }
        }).done(function (data) {
            callback(data);
        });
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
        this.callParticipantMethod(roomid, userid, "kick", callback)
    }
    
    this.setParticipantMuteness = function(roomid, userid, muted, callback) {
        var method = muted == true ? "mute" : "unmute";
        this.callParticipantMethod(roomid, userid, method, callback)
    }
    
    this.callParticipantMethod = function(roomid, userid, method, callback) {
        $.ajax({
            url: serviceUrl + "/rooms/" + roomid + "/" + userid + "/" + method,
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
    
    this.waitForChanges = function(roomid, callback) {
        $.ajax({
            url: serviceUrl + "/rooms/" + roomid + "/changes?" + new Date().getTime()   ,
            headers: { 
                Accept : "application/json"
            }
        }).always(function (data) {
            callback(data);
        });
    }

}