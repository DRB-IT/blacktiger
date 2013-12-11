angular.module('blacktiger', [])
.provider('$blacktiger', function() {
    var serviceUrl = "";
    this.setServiceUrl=function(url) {
        serviceUrl = url;
    }
    
    
    this.$get = function() {
        return {
            getServiceUrl: function() {
                return serviceUrl;
            }
        }

    }
})
/*************************************** SERVICES ********************************************/
.factory('$room', function($q, $blacktiger, $timeout, $http, $rootScope) {
    var roomIds = null;
    var current = null;
    return {
        getRoomIds:function() {
            var deferred = $q.defer();
            if(roomIds == null) {
                $http({method: 'GET', url: $blacktiger.getServiceUrl() + "rooms"}).success(function(data) {
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
})
       
.factory('$participant', function($http, $q, $room, $blacktiger) {
    return {
        findOne: function(userid) {
            return $http.get($blacktiger.getServiceUrl() + "rooms/" + $room.getCurrent() + "/" + userid).then(function(request) {
               return request.data; 
            });
        },
        findAll: function() {
            return $http.get($blacktiger.getServiceUrl() + "rooms/" + $room.getCurrent()).then(function(request) {
               return request.data; 
            });
        },
        kickParticipant: function(userid) {
            var deferred = $q.defer();
            $http({method: 'POST', url: $blacktiger.getServiceUrl() + "rooms/" + $room.getCurrent() + "/" + userid + "/kick"}).success(function() {
                deferred.resolve();
            });
            return deferred.promise;
        },
        muteParticipant: function(userid) {
            var deferred = $q.defer();
            $http({method: 'POST', url: $blacktiger.getServiceUrl() + "rooms/" + $room.getCurrent() + "/" + userid + "/mute"}).success(function() {
                deferred.resolve();
            });
            return deferred.promise;
        },
        unmuteParticipant: function(userid) {
            var deferred = $q.defer();
            $http({method: 'POST', url: $blacktiger.getServiceUrl() + "rooms/" + $room.getCurrent() + "/" + userid + "/unmute"}).success(function() {
                deferred.resolve();
            });
            return deferred.promise;
        },
        waitForChanges: function() {
            var deferred = $q.defer();
            $http.get($blacktiger.getServiceUrl() + "rooms/" + $room.getCurrent() + "/changes?" + new Date().getTime()).success(function() {
                deferred.resolve();
            });
            return deferred.promise;
        }
    }
})
        
.factory('$phonebook', function($http, $q, $room, $blacktiger) {
    return {
        updateEntry: function(phoneNumber, name) {
            var deferred = $q.defer();
            $http.post($blacktiger.getServiceUrl() + 'phonebook/' + phoneNumber, name).success(function() {
                deferred.resolve();
            });
            return deferred.promise;
        },
        removeEntry: function() {
            var deferred = $q.defer();
            $http.delete($blacktiger.getServiceUrl() + 'phonebook/' + phoneNumber).success(function() {
                deferred.resolve();
            });
            return deferred.promise;
        }
    }
})
.factory('$report', function($http, $q, $room, $blacktiger) {
    return {
        report: [],
        findByPeriodAndMinimumDuration: function(hourStart, hourEnd, minDuration) {
            return $http.get($blacktiger.getServiceUrl() + "reports/" + $room.getCurrent() + '?hourStart=' + hourStart + '&hourEnd=' + hourEnd + '&duration=' + minDuration).
                    then(function(request) {
                        return request.data;
                    }
            );
        },
    }
});

