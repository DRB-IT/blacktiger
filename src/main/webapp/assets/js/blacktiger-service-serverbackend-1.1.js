
/*************************************** SERVICES ********************************************/
blacktiger.factory('roomIdService', function($q, serviceUrl, $timeout, $http, $rootScope) {
    var roomIds = null;
    var current = null;
    return {
        getRoomIds:function() {
            var deferred = $q.defer();
            if(roomIds == null) {
                $http({method: 'GET', url: serviceUrl + "rooms"}).success(function(data) {
                    roomIds = data;
                    deferred.resolve(data);
                });
            } else {
                $timeout(function() {
                    deferred.resolve(roomIds);
                }, 0);
            }
            return deferred.promise;
        },
        setCurrent:function(roomId) {
            current=roomId;
            $rootScope.$broadcast("roomChanged", {roomId:roomId});
        },
        getCurrent:function() {
            return current;
        }
    }
});

blacktiger.factory('participantService', function($http, $q, roomIdService, serviceUrl) {
    return {
        findOne: function(userid) {
            return $http.get(serviceUrl + "rooms/" + roomIdService.getCurrent() + "/" + userid).then(function(request) {
               return request.data; 
            });
        },
        findAll: function() {
            return $http.get(serviceUrl + "rooms/" + roomIdService.getCurrent()).then(function(request) {
               return request.data; 
            });
        },
        kickParticipant: function(userid) {
            var deferred = $q.defer();
            $http({method: 'POST', url: serviceUrl + "rooms/" + roomIdService.getCurrent() + "/" + userid + "/kick"}).success(function() {
                deferred.resolve();
            });
            return deferred.promise;
        },
        muteParticipant: function(userid) {
            var deferred = $q.defer();
            $http({method: 'POST', url: serviceUrl + "rooms/" + roomIdService.getCurrent() + "/" + userid + "/mute"}).success(function() {
                deferred.resolve();
            });
            return deferred.promise;
        },
        unmuteParticipant: function(userid) {
            var deferred = $q.defer();
            $http({method: 'POST', url: serviceUrl + "rooms/" + roomIdService.getCurrent() + "/" + userid + "/unmute"}).success(function() {
                deferred.resolve();
            });
            return deferred.promise;
        },
        waitForChanges: function() {
            var deferred = $q.defer();
            $http.get(serviceUrl + "rooms/" + roomIdService.getCurrent() + "/changes?" + new Date().getTime()).success(function() {
                deferred.resolve();
            });
            return deferred.promise;
        }
    }
});

blacktiger.factory('phonebookService', function($http, $q, roomIdService, serviceUrl) {
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



blacktiger.factory('reportService', function($http, $q, roomIdService, serviceUrl) {
    return {
        report: [],
        findByPeriodAndMinimumDuration: function(hourStart, hourEnd, minDuration) {
            return $http.get(serviceUrl + "reports/" + roomIdService.getCurrent() + '?hourStart=' + hourStart + '&hourEnd=' + hourEnd + '&duration=' + minDuration).
                    then(function(request) {
                        return request.data;
                    }
            );
        },
    }
});