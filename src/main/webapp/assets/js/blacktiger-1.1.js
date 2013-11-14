
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
    $translateProvider.translations('da', {
        SYSTEM: {
            NAME: 'Telesal',
            COPYRIGHTHOLDER: 'Det Regionale Byggeudvalg',
        },
        NAVIGATION: {
            PARTICIPANTS: 'Lyttere nu',
            REPORT: 'Lytterrapport',
            HELP: 'Hjælp'
        },
        PARTICIPANTS: {
            WARNINGS: {
                NO_PARTICIPANTS_AND_TRANSMITTERS: 'Der er i øjeblikket hverken transmission til, eller deltagere i, telefonmødet.',
                PARTICIPANTS_BUT_NO_TRANSMITTER_TITLE: 'Der sendes ikke lyd til lytterne!',
                PARTICIPANTS_BUT_NO_TRANSMITTER_TEXT1: 'For at transmittere lyd, ring op til nummer <b>{{phoneNumber}}</b> med X-Lite; ring ikke til det 8-cifrede nummer, det er kun til brug for lytterne!',
                PARTICIPANTS_BUT_NO_TRANSMITTER_TEXT2: 'Er mødet slut, tryk <i class="icon-remove"></i> ud for hver lytter, så de ikke betaler unødigt!',
                PARTICIPANTS_BUT_NO_TRANSMITTER_TEXT3: 'Hvis X-Lite ikke vil ringe op, så tryk Exit i X-Lite og start den igen.',
            },
            INFO: {
                NO_PARTICIPANTS: 'Der er pt. ingen lyttere',
                UNMUTED: 'Svarmikrofonen er åben',
                NO_HOST: 'Der er pt. ingen transmission fra rigssalen.'
            },
            UNMUTE: 'Åben svarmikrofon',
            MUTE: 'Luk svarmikrofon',
            KICK: 'Afbryd lytter',
            EDIT: 'Rediger',
            UNKNOWN: 'Ukendt',
            KINGDOM_HALL: 'Rigssal',
            PARTICIPANTS: 'Lyttere',
            ABORT_TRANSMISSION: 'Afbryd transmissionen'
        },
        REPORT: {
            NUMBER: 'Nummer',
            NAME: 'Navn',
            FIRST_CALL: 'Første opkald',
            CALLS: 'Opkald',
            MINUTES: 'Minutter',
            TOTAL: 'I alt',
            TITLE: 'Lytterrapport',
            CALL_COMMENCED: 'Opkald påbegyndt',
            AFTER_TIME: 'Efter kl.',
            BEFORE_TIME: 'Før kl.',
            MINIMUM_DURATION: 'Min. varighed'
        },
        GENERAL: {
            OK: 'Ok'
        }

    });

    $translateProvider.preferredLanguage('da');
});



/*************************************** CONTROLLERS ********************************************/

function MenuCtrl($scope, $location, $translate) {
    $scope.location = $location;
    $scope.links = [
        {url: "#/", name: $translate('NAVIGATION.PARTICIPANTS')},
        {url: "#/reports", name: $translate('NAVIGATION.REPORT')},
        {url: "http://telesal.dk/wiki", name: $translate('NAVIGATION.HELP')}
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

