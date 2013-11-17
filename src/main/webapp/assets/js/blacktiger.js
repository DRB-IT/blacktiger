
/*************************************** MODULE ********************************************/

var blacktiger = angular.module('blacktiger', ['pascalprecht.translate', 'ngResource']).
        config(function($locationProvider, $routeProvider) {
    $routeProvider.
            when('/', {controller: ListCtrl, templateUrl: 'assets/views/listParticipants.html'}).
            when('/reports', {controller: ReportCtrl, templateUrl: 'assets/views/reports.html'}).
            otherwise({redirectTo: '/'});
});


/*************************************** TRANSLATION ********************************************/

blacktiger.config(function($translateProvider) {
    $translateProvider.useStaticFilesLoader({
        prefix: 'assets/js/blacktiger-locale-',
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

function RoomCtrl($scope, roomIdService) {
    $scope.rooms = null;
    $scope.currentRoom = null;
    
    $scope.$watch('currentRoom', function() {
        if (roomIdService.getCurrent() != $scope.currentRoom) {
            roomIdService.setCurrent($scope.currentRoom);
        }
    });
    
    $scope.$watch('rooms', function() {
        if($scope.rooms != null && $scope.rooms.length>0 && $scope.currentRoom == null) {
            $scope.currentRoom = $scope.rooms[0];
        }
    });
    
    $scope.$on("roomChanged", function(event, args) {
        $scope.currentRoom = roomIdService.getCurrent();
    });
    
    roomIdService.getRoomIds().then(function(data) {
       $scope.rooms = data; 
    });
}

function ListCtrl($scope, $q, $service, $phonebookService, roomIdService) {
    $scope.participants = [];
    $scope.currentRoom = roomIdService.getCurrent();
    $scope.translationData = {
        phoneNumber:$scope.currentRoom
    };

    $scope.refresh = function() {
        var deferred = $q.defer();
        var promise = $service.findAll();
        promise.then(function(data) {
            $scope.participants = data;
            deferred.resolve();
        });
        return deferred.promise;
    }

    $scope.waitForChanges = function() {
        $service.waitForChanges().then(function(data, status, headers, config) {
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
        $service.kickParticipant(userId).then(function(data) {
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
            $service.muteParticipant(userId, muted).then(function(data) {
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
                $phonebookService.updateEntry(participant.phoneNumber, newName);
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
        $scope.currentRoom = roomIdService.getCurrent();
    });

    $scope.refresh();
    $scope.waitForChanges();
}

function ReportCtrl($scope, $service, $phonebookService) {
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
        $service.findByPeriodAndMinimumDuration($scope.hourStart, $scope.hourEnd, $scope.minDuration * 60).then(function(data) {
           $scope.report = data; 
        });
    }

    $scope.changeName = function(entry) {
        var newName = window.prompt("Type in new name", entry.name);
        if (newName != null) {
            entry.name = newName;
            $phonebookService.updateEntry(entry.phoneNumber, newName);
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

ListCtrl.$inject = ['$scope', '$q', 'participantService', 'phonebookService', 'roomIdService'];
ReportCtrl.$inject = ['$scope', 'reportService', 'phonebookService'];
RoomCtrl.$inject = ['$scope', 'roomIdService'];

