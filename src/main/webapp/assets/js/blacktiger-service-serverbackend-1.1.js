
    var roomid = "09991";
    
/*************************************** SERVICES ********************************************/

blacktiger.factory('participantService', function($http, $resource, $q) {
    var serviceUrl = "../";
    var Participant = $resource(serviceUrl + 'rooms/:roomId/:pId', {roomId:roomid, pId:'@pId'}, {
     kick: {method:'POST', url:serviceUrl + 'rooms/:roomId/:pId/kick'},
     mute: {method:'POST', url:serviceUrl + 'rooms/:roomId/:pId/mute'},
     unmute: {method:'POST', url:serviceUrl + 'rooms/:roomId/:pId/unmute'}
    });
    return {
        findOne: function(userid) {
            var deferred = $q.defer();
            Participant.get({pId:userid}, function(data) {
                deferred.resolve(data);
            });
            return deferred.promise;
        },
        findAll: function() {
            var deferred = $q.defer();
            Participant.query(function(data) {
                deferred.resolve(data);
            });
            return deferred.promise;
        },
        kickParticipant: function(userid) {
            var deferred = $q.defer();
            $http({method: 'POST', url: serviceUrl + "rooms/" + roomid + "/" + userid + "/kick"}).success(function() {
                deferred.resolve();
            });
            return deferred.promise;
        },
        muteParticipant: function(userid) {
            var deferred = $q.defer();
            $http({method: 'POST', url: serviceUrl + "rooms/" + roomid + "/" + userid + "/mute"}).success(function() {
                deferred.resolve();
            });
            return deferred.promise;
        },
        unmuteParticipant: function(userid) {
            var deferred = $q.defer();
            $http({method: 'POST', url: serviceUrl + "rooms/" + roomid + "/" + userid + "/unmute"}).success(function() {
                deferred.resolve();
            });
            return deferred.promise;
        },
        waitForChanges: function() {
            var deferred = $q.defer();
            $http.get(serviceUrl + "rooms/" + roomid + "/changes?" + new Date().getTime()).success(function() {
                deferred.resolve();
            });
            return deferred.promise;
        }
    }
});

blacktiger.factory('phonebookService', function($http, $q) {
    var serviceUrl = "../";
    return {
        updateEntry: function(phoneNumber, name) {
            var deferred = $q.defer();
            $http.post(serviceUrl + 'phonebook/' + phoneNumber, name).success(function() {
                deferred.resolve();
            });
            return deferred.promise;
        },
        removeEntry: function() {
            var deferred = $q.defer();
            $http.delete(serviceUrl + 'phonebook/' + phoneNumber).success(function() {
                deferred.resolve();
            });
            return deferred.promise;
        }
    }
});



blacktiger.factory('reportService', function($http, $q) {
    
    var serviceUrl = "../";
    
    return {
        report: [],
        findByPeriodAndMinimumDuration: function(hourStart, hourEnd, minDuration) {
            var deferred = $q.defer();
            $http.get(serviceUrl + "reports/" + roomid + '?hourStart=' + hourStart + '&hourEnd=' + hourEnd + '&duration=' + minDuration).success(function(data) {
                deferred.resolve(data);
            });
            return deferred.promise;
        },
    }
});