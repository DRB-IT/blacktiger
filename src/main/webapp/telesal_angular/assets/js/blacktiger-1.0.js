var blacktiger = angular.module('blacktiger',[]).
  config(function($locationProvider,$routeProvider) {
    //$locationProvider.html5Mode(true); 
    //$locationProvider.hashPrefix('!');
    $routeProvider.
      when('/', {controller:ListCtrl, templateUrl:'listParticipants.html'}).
      when('/reports', {controller:ReportCtrl, templateUrl:'reports.html'}).
      otherwise({redirectTo:'/'});
  });


blacktiger.factory('participantService', function() {
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
});

blacktiger.factory('dummyService', function() {
    return {
        findOne:function(roomid, userid) {
            return {
                    "userId": "1",
                    "muted": true,
                    "host": false,
                    "phoneNumber": "+4551923192",
                    "dateJoined": 1382383383744,
                    "name": "Michael Krog"
                }
        },
        findAll:function(roomid) {
            return [
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
                    "phoneNumber": "0999",
                    "dateJoined": 1382383401553,
                    "name": "Test-rigssal"
                }
            ];
        },
  }
});


function MenuCtrl($scope, $location){
    $scope.location = $location;
     $scope.links = [
         { url: "#/", name: "Lyttere nu"},
         { url: "#/reports", name: "Lytterrapport"},
         { url: "http://telesal.dk/wiki", name: "Hjælp"}
    ];
}

function ListCtrl($scope, $service) {
  $scope.participants = [];
    
    $scope.refresh = function(roomid) {
        $scope.participants = $service.findAll(roomid);
    }
 
    $scope.waitForChanges = function(roomid) {
        $service.waitForChanges(roomid).
            success(function(data, status, headers, config) {
                $scope.refresh();
                window.setTimeout(function() {
                    $scope.waitForChanges(roomid);
                }, 500);
            }).
            error(function(data, status, headers, config) {
                window.setTimeout(function() {
                    $scope.waitForChanges(roomid);
                }, 10000);
            });
                    

    }
    $scope.refresh();
    
}

function ReportCtrl($scope) {
  $scope.reports = [
    {meetingStart:new Date(), meetingEnd:new Date(),
        participants:[
            {number:'+4551923192',name:'Michael Krog',calls:3,totalDuration:114}
        ]
    }
  ];
}

ListCtrl.$inject = ['$scope','dummyService'];
//ListCtrl.$inject = ['$scope','participantService'];

var BlackTiger = new function() {
    
    var serviceUrl = "api";
    var currentRequest = null;
    
    this.init = function(url) {
        if(url != null) {
            serviceUrl = url;
        }
    }
    
    this.updatePhonebookEntry = function(phoneNumber, name, callback) {
        $.ajax({
            url: serviceUrl + "phonebook/" + phoneNumber,
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
    
    this.removePhonebookEntry = function(phoneNumber, callback) {
        $.ajax({
            url: serviceUrl + "phonebook/" + phoneNumber + "?_method=DELETE",
            headers: { 
                Accept : "application/json"
            },
            type:"POST"
        }).fail(function(xhr, textStatus) {
            if(xhr.status == 404) {
                callback(0);
            }
        }).done(function (data) {
            callback(data);
        });
    }
    


    this.destroy = function() {
        if(currentRequest != null) {
            currentRequest.abort();
        }
    }

}