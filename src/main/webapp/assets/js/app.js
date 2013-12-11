
/*************************************** MODULE ********************************************/

var blacktigerApp = angular.module('blacktiger-app', ['ngRoute','pascalprecht.translate','blacktiger']).
    config(function($locationProvider, $routeProvider, $translateProvider) {
        $routeProvider.
            when('/', {controller: ListCtrl, templateUrl: 'assets/views/listParticipants.html'}).
            when('/reports', {controller: ReportCtrl, templateUrl: 'assets/views/reports.html'}).
            otherwise({redirectTo: '/'});
    
        $translateProvider.useStaticFilesLoader({
            prefix: 'assets/js/i18n/blacktiger-locale-',
            suffix: '.json'
        });
        
        var language = window.navigator.browserLanguage || window.navigator.language;
        var langData = language.split("-");
        $translateProvider.preferredLanguage(langData[0]);
        $translateProvider.fallbackLanguage('en'); 
        
    });

/*************************************** CONTROLLERS ********************************************/

function MenuCtrl($scope, $location) {
    $scope.location = $location;
    $scope.links = [
        {url: "#/", name: 'NAVIGATION.PARTICIPANTS'},
        {url: "#/reports", name: 'NAVIGATION.REPORT'},
        {url: "http://telesal.dk/wiki", name: 'NAVIGATION.HELP'}
    ];
}

function RoomCtrl($scope, $room) {
    $scope.rooms = null;
    $scope.currentRoom = null;
    
    $scope.$watch('currentRoom', function() {
        if ($room.getCurrent() != $scope.currentRoom) {
            $room.setCurrent($scope.currentRoom);
        }
    });
    
    $scope.$watch('rooms', function() {
        if($scope.rooms != null && $scope.rooms.length>0 && $scope.currentRoom == null) {
            $scope.currentRoom = $scope.rooms[0];
        }
    });
    
    $scope.$on("roomChanged", function(event, args) {
        $scope.currentRoom = $room.getCurrent();
    });
    
    $room.getRoomIds().then(function(data) {
       $scope.rooms = data; 
    });
}

function ListCtrl($scope, $q, $participant, $phonebook, $room) {
    $scope.participants = [];
    $scope.currentRoom = $room.getCurrent();
    $scope.translationData = {
        phoneNumber:$scope.currentRoom
    };

    $scope.refresh = function() {
        var deferred = $q.defer();
        var promise = $participant.findAll();
        promise.then(function(data) {
            $scope.participants = data;
            deferred.resolve();
        });
        return deferred.promise;
    }

    $scope.waitForChanges = function() {
        $participant.waitForChanges().then(function(data, status, headers, config) {
            $scope.refresh().then(function(){
                $scope.waitForChanges();
            });
        }, function(data, status, headers, config) {
            window.setTimeout(function() {
                $scope.$apply(function() {
                    $scope.waitForChanges();
                });
            }, 10000);
        });
    }

    $scope.kickParticipant = function(userId) {
        $participant.kickParticipant(userId).then(function(data) {
            var index = $scope.getIndexForUserId(userId);
            if(index>=0) {
                $scope.participants.splice(index, 1);
            }
        });
    }

    $scope.muteParticipant = function(userId, muted) {
        var participant = $scope.findOne(userId);
        if (participant != null) {
            participant.muted = muted;
            $participant.muteParticipant(userId, muted).then(function(data) {
                var index = $scope.getIndexForUserId(userId);
                if(index>=0) {
                    $scope.participants[index].muted=muted;
                }
            });
        }
    }

    $scope.changeName = function(userId) {
        var participant = $scope.findOne(userId);
        if (participant != null) {
            var newName = window.prompt("Type in new name", participant.name);
            if (newName != null) {
                participant.name = newName;
                $phonebook.updateEntry(participant.phoneNumber, newName);
            }
        }
    }

    $scope.findOne = function(userId) {
        for (var i = 0; i < $scope.participants.length; i++) {
            if ($scope.participants[i].userId === userId) {
                return $scope.participants[i];
            }
        }
        return null;
    }
    
    $scope.getIndexForUserId=function(userId) {
        var index = -1;
        for(var i=0;i<$scope.participants.length;i++) {
            if($scope.participants[i].userId === userId) {
                index = i;
                break;
            }
        }
        
        return index;
    }

    $scope.$on("roomChanged", function(event, args) {
        $scope.refresh();
        $scope.waitForChanges();
        $scope.currentRoom = $room.getCurrent();
    });

    $scope.refresh();
    $scope.waitForChanges();
}

function ReportCtrl($scope, $repoert, $phonebook) {
    $scope.hourStart = 6;
    $scope.hourEnd = 23;
    $scope.minDuration = 0;
    $scope.date = new Date();
    $scope.report = [];

    $scope.getTotalCalls = function() {
        var sum = 0;
        for (var i = 0; i < $scope.report.length; i++) {
            sum += $scope.report[i].numberOfCalls;
        }
        return sum;
    }

    $scope.getTotalDuration = function() {
        var sum = 0;
        for (var i = 0; i < $scope.report.length; i++) {
            sum += $scope.report[i].totalDuration;
        }
        return sum;
    }

    $scope.refresh = function() {
        $report.findByPeriodAndMinimumDuration($scope.hourStart, $scope.hourEnd, $scope.minDuration * 60).then(function(data) {
           $scope.report = data; 
        });
    }

    $scope.changeName = function(entry) {
        var newName = window.prompt("Type in new name", entry.name);
        if (newName != null) {
            entry.name = newName;
            $phonebook.updateEntry(entry.phoneNumber, newName);
        }
    }

    $scope.$on("roomChanged", function(event, args) {
       $scope.refresh(); 
    });
    $scope.refresh(); 
}

function MusicCtrl($scope) {
    $scope.currentSong = 0;
    $scope.progress = 0;
    $scope.state = SongManager.getState();

    $scope.getProgressStyle = function() {
        return {
            width:$scope.progress + '%'
        }
    }
    
    $scope.play = function() {
        SongManager.play();
    }

    $scope.stop = function() {
        SongManager.setRandom(false);
        SongManager.stop();
    }

    $scope.toggleRandom = function() {
        SongManager.setRandom(!SongManager.isRandom());
    }

    $scope.isRandom = function() {
        return SongManager.isRandom();
    }

    $scope.$watch('currentSong', function() {
        if (SongManager.getCurrentSong() != $scope.currentSong) {
            SongManager.setCurrentSong($scope.currentSong);
        }
    });

    $scope.updateProgress = function() {
        $scope.progress = SongManager.getProgressPercent();
        if ($scope.state == 'playing') {
            window.setTimeout(function() {
                $scope.$apply(function() {
                    $scope.updateProgress(); 
                });
                
                
            }, 100);
        }
    }
    
    $scope.isSupported = function() {
        return SongManager.isSupported();
    }

    SongManager.setChangeHandler(function() {
        console.log(SongManager.getState());
        $scope.currentSong = SongManager.getCurrentSong();
        $scope.state = SongManager.getState();
        $scope.updateProgress();
    });

    $scope.currentSong = 1;
}

angular.module('blacktiger-app-mocked', ['blacktiger-app', 'ngMockE2E'])
.run(function($httpBackend) {
  participants= [
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

        ];
  
  $httpBackend.whenGET('rooms').respond(["09991"]);
  $httpBackend.whenGET('rooms/09991').respond(participants);
 
  $httpBackend.whenGET(/^rooms\/09991\/changes.?/).respond();
 
  $httpBackend.whenGET(/^assets\/.?/).passThrough();
  
});
