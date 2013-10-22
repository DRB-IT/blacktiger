
/*************************************** MODULE ********************************************/

var blacktiger = angular.module('blacktiger',[]).
  config(function($locationProvider,$routeProvider) {
    //$locationProvider.html5Mode(true); 
    //$locationProvider.hashPrefix('!');
    $routeProvider.
      when('/', {controller:ListCtrl, templateUrl:'listParticipants.html'}).
      when('/reports', {controller:ReportCtrl, templateUrl:'reports.html'}).
      otherwise({redirectTo:'/'});
  });

/*************************************** SERVICES ********************************************/
/*
blacktiger.factory('restParticipantService', function() {
    return {
        findOne:function(roomid, userid) {
            return $http.get(serviceUrl + "rooms/" + roomid + "/" + userid).then(
                function(result) {
                    return result.data;
                }
            ); 
        },
        findAll:function(roomid) {
            return $http.get(serviceUrl + "rooms/" + roomid).then(
                function(result) {
                    return result.data;
                }
            );
        },
        kickParticipant:function(roomid, userid) {
            return $http({method: 'POST', url: serviceUrl + "rooms/" + roomid + "/" + userid + "/kick"});
        },
        muteParticipant:function(roomid, userid) {
            return $http({method: 'POST', url: serviceUrl + "rooms/" + roomid + "/" + userid + "/mute"});
        },
        unmuteParticipant:function(roomid, userid) {
            return $http({method: 'POST', url: serviceUrl + "rooms/" + roomid + "/" + userid + "/unmute"});
        },
        waitForChanges:function(roomid) {
            return $http.get(serviceUrl + "rooms/" + roomid + "/changes?" + new Date().getTime());
        }
  }
});*/

blacktiger.factory('memPhonebookService', ['$q','memParticipantService', function($q, $participantService) {
    return {
        updateEntry:function(phoneNumber, name) {
            var deferred = $q.defer();
            var participants = $participantService.findAll();
            for(var i=0;i<participants.length;i++) {
                if(participants[i].phoneNumber == phoneNumber) {
                    participants[i].name = name;
                }
            }
            setTimeout(function() {
                deferred.resolve();
                
            }, 10);
            return deferred.promise;
        },
        removeEntry:function() {
            var deferred = $q.defer();
            var participants = $participantService.findAll();
            for(var i=0;i<participants.length;i++) {
                if(participants[i].phoneNumber == phoneNumber) {
                    participants[i].name = null;
                }
            }
            setTimeout(function() {
                deferred.resolve();
                
            }, 10);
            return deferred.promise;
        }
    }
}]);
                   
blacktiger.factory('memParticipantService', function($q) {
    return {
        participants: [
                {
                    "userId": "1",
                    "muted": true,
                    "host": false,
                    "phoneNumber": "+4551923192",
                    "dateJoined": 1382383383744,
                    "name": "Michael Krog"
                },
                {
                    "userId": "2",
                    "muted": false,
                    "host": true,
                    "phoneNumber": "IP-0999",
                    "dateJoined": 1382383401553,
                    "name": "Test-rigssal"
                },
                {
                    "userId": "3",
                    "muted": true,
                    "host": false,
                    "phoneNumber": "+4551923171",
                    "dateJoined": 1382383401553,
                    "name": "Hannah Krog"
                },
                {
                    "userId": "4",
                    "muted": true,
                    "host": false,
                    "phoneNumber": "+4512341234",
                    "dateJoined": 1382383401553,
                    "name": "Kasper Dyrvig"
                }
            
        ],
        findOne:function(userid) {
            for(var i=0;i<this.participants.length;i++) {
                if(this.participants[i].userId == userid) {
                    return this.participants[i];
                }
            }
            return null;
        },
        findAll:function() {
            return this.participants;
        },
        kickParticipant:function(userid) {
            var deferred = $q.defer();
            var kickIndex = -1;
            for(var i=0;i<this.participants.length;i++) {
                if(this.participants[i].userId == userid) {
                    kickIndex = i;
                }
            }
            if (kickIndex > -1) {
               this.participants.splice(kickIndex, 1);
            }
            setTimeout(function() {
                deferred.resolve();
                
            }, 10);
            return deferred.promise;
        },
        muteParticipant:function(userid, muted) {
            var deferred = $q.defer();
            for(var i=0;i<this.participants.length;i++) {
                if(this.participants[i].userId == userid) {
                    this.participants[i].muted=muted;
                }
            }
            setTimeout(function() {
                deferred.resolve();
            }, 10);
            return deferred.promise;
        },
        waitForChanges:function() {
            //return $http.get(serviceUrl + "rooms/" + roomid + "/changes?" + new Date().getTime());
        }
  }
});

/*************************************** CONTROLLERS ********************************************/

function MenuCtrl($scope, $location){
    $scope.location = $location;
    $scope.links = [
         { url: "#/", name: "Lyttere nu"},
         { url: "#/reports", name: "Lytterrapport"},
         { url: "http://telesal.dk/wiki", name: "Hjælp"}
    ];
}

function ListCtrl($scope, $service, $phonebookService) {
    $scope.participants = [];
    
    $scope.refresh = function() {
        $scope.participants = $service.findAll();
    }
 
    $scope.waitForChanges = function() {
        $service.waitForChanges().
            success(function(data, status, headers, config) {
                $scope.refresh();
                window.setTimeout(function() {
                    $scope.waitForChanges();
                }, 500);
            }).
            error(function(data, status, headers, config) {
                window.setTimeout(function() {
                    $scope.waitForChanges();
                }, 10000);
            });
    }
    
    $scope.kickParticipant = function(userId) {
        $service.kickParticipant(userId).then($scope.refresh());
    }
    
    $scope.muteParticipant = function(userId, muted) {
        var participant = $scope.findOne(userId);
        if(participant != null) {
            participant.muted=muted;
            $service.muteParticipant(userId, muted).then($scope.refresh());
        }
    }
    
    $scope.changeName = function(userId) {
        var participant = $scope.findOne(userId);
        if (participant != null) {
            var newName = window.prompt("Type in new name", participant.name);
            if(newName != null) {
                participant.name = newName; 
                $phonebookService.updateEntry(participant.phoneNumber, newName).then($scope.refresh());
            }
        }
    }
                    
    $scope.findOne = function(userId) {
        for(var i=0;i<$scope.participants.length;i++) {
            if($scope.participants[i].userId == userId) {
                return $scope.participants[i];
            }
        }
        return null;
    }
    
    $scope.refresh();
    
}

function ReportCtrl($scope) {
  $scope.reports = [
    {meetingStart:new Date(), meetingEnd:new Date(),
        participants:[
            {number:'+4551923192',name:'Michael Krog',calls:3,totalDuration:114},
            {number:'+4551923171',name:'Hannah Krog',calls:3,totalDuration:114},
            {number:'+4512341234',name:'Kasper Dyrvig',calls:3,totalDuration:114}
        ]
    }
  ];
}

ListCtrl.$inject = ['$scope','memParticipantService','memPhonebookService'];
//ListCtrl.$inject = ['$scope','participantService'];

