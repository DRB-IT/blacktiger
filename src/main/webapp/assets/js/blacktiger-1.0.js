
/*************************************** MODULE ********************************************/

var blacktiger = angular.module('blacktiger', ['pascalprecht.translate']).
        config(function($locationProvider, $routeProvider) {
    $routeProvider.
            when('/', {controller: ListCtrl, templateUrl: 'assets/views/listParticipants.html'}).
            when('/reports', {controller: ReportCtrl, templateUrl: 'assets/views/reports.html'}).
            otherwise({redirectTo: '/'});
});


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

blacktiger.factory('memPhonebookService', ['$q', 'memParticipantService', 'memReportService', function($q, $participantService, $reportService) {
        return {
            updateEntry: function(phoneNumber, name) {
                var deferred = $q.defer();
                var participants = $participantService.findAll();
                for (var i = 0; i < participants.length; i++) {
                    if (participants[i].phoneNumber == phoneNumber) {
                        participants[i].name = name;
                    }
                }

                var report = $reportService.report;
                for (var i = 0; i < report.length; i++) {
                    if (report[i].phoneNumber == phoneNumber) {
                        report[i].name = name;
                    }
                }
                setTimeout(function() {
                    deferred.resolve();

                }, 10);
                return deferred.promise;
            },
            removeEntry: function() {
                var deferred = $q.defer();
                var participants = $participantService.findAll();
                for (var i = 0; i < participants.length; i++) {
                    if (participants[i].phoneNumber == phoneNumber) {
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
        findOne: function(userid) {
            for (var i = 0; i < this.participants.length; i++) {
                if (this.participants[i].userId == userid) {
                    return this.participants[i];
                }
            }
            return null;
        },
        findAll: function() {
            return this.participants;
        },
        kickParticipant: function(userid) {
            var deferred = $q.defer();
            var kickIndex = -1;
            for (var i = 0; i < this.participants.length; i++) {
                if (this.participants[i].userId == userid) {
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
        muteParticipant: function(userid, muted) {
            var deferred = $q.defer();
            for (var i = 0; i < this.participants.length; i++) {
                if (this.participants[i].userId == userid) {
                    this.participants[i].muted = muted;
                }
            }
            setTimeout(function() {
                deferred.resolve();
            }, 10);
            return deferred.promise;
        },
        waitForChanges: function() {
            //return $http.get(serviceUrl + "rooms/" + roomid + "/changes?" + new Date().getTime());
        }
    }
});

blacktiger.factory('memReportService', function($q) {
    var now = new Date();
    var date1 = new Date(now.getTime());
    var date2 = new Date(now.getTime());
    var date3 = new Date(now.getTime());
    date1.setHours(10);
    date2.setHours(11);
    date3.setHours(17);

    return {
        report: [
            {
                phoneNumber: "+4551923192",
                name: "Michael Krog",
                numberOfCalls: 2,
                totalDuration: 123,
                firstCallTimestamp: date1
            },
            {
                phoneNumber: "+4551923171",
                name: "Hannah Krog",
                numberOfCalls: 4,
                totalDuration: 2343,
                firstCallTimestamp: date2
            },
            {
                phoneNumber: "+4512341234",
                name: "Kasper Dyrvig",
                numberOfCalls: 1,
                totalDuration: 2333,
                firstCallTimestamp: date3
            }
        ],
        findByPeriodAndMinimumDuration: function(dateStart, dateEnd, minDuration) {
            var result = new Array();
            for (var i = 0; i < this.report.length; i++) {
                var entry = this.report[i];
                if (entry.firstCallTimestamp.getTime() > dateStart.getTime() && entry.firstCallTimestamp.getTime() < dateEnd.getTime() &&
                        entry.totalDuration >= minDuration) {
                    result.push(entry);
                }
            }
            return result;
        },
    }
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
        if (participant != null) {
            participant.muted = muted;
            $service.muteParticipant(userId, muted).then($scope.refresh());
        }
    }

    $scope.changeName = function(userId) {
        var participant = $scope.findOne(userId);
        if (participant != null) {
            var newName = window.prompt("Type in new name", participant.name);
            if (newName != null) {
                participant.name = newName;
                $phonebookService.updateEntry(participant.phoneNumber, newName).then($scope.refresh());
            }
        }
    }

    $scope.findOne = function(userId) {
        for (var i = 0; i < $scope.participants.length; i++) {
            if ($scope.participants[i].userId == userId) {
                return $scope.participants[i];
            }
        }
        return null;
    }

    $scope.refresh();

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
        var dateStart = new Date($scope.date.getTime());
        var dateEnd = new Date($scope.date.getTime());
        dateStart.setHours($scope.hourStart);
        dateStart.setMinutes(0);
        dateEnd.setHours($scope.hourEnd);
        dateEnd.setMinutes(0);
        $scope.report = $service.findByPeriodAndMinimumDuration(dateStart, dateEnd, $scope.minDuration * 60);
    }

    $scope.changeName = function(entry) {
        var newName = window.prompt("Type in new name", entry.name);
        if (newName != null) {
            entry.name = newName;
            $phonebookService.updateEntry(entry.phoneNumber, newName).then($scope.refresh());
        }
    }

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

    SongManager.setChangeHandler(function() {
        console.log(SongManager.getState());
        $scope.currentSong = SongManager.getCurrentSong();
        $scope.state = SongManager.getState();
        $scope.updateProgress();
    });

    $scope.currentSong = 1;
}

ListCtrl.$inject = ['$scope', 'memParticipantService', 'memPhonebookService'];
ReportCtrl.$inject = ['$scope', 'memReportService', 'memPhonebookService']
//ListCtrl.$inject = ['$scope','participantService'];

