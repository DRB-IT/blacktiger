/*************************************** SERVICES ********************************************/
blacktiger.factory('roomIdService', function($q, $timeout, $http, $rootScope) {
    var roomIds = ['09991'];
    var current = null;
    return {
        getRoomIds:function() {
            var deferred = $q.defer();
            $timeout(function() {
                deferred.resolve(roomIds);
            }, 0);
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

blacktiger.factory('phonebookService', ['$q', 'participantService', 'reportService', function($q, $participantService, $reportService) {
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

blacktiger.factory('participantService', function($q, $timeout) {
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
            var deferred = $q.defer();
            var result = this.participants;
            $timeout(function() {
                deferred.resolve(result);
            }, 50);
            return deferred.promise;
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
            $timeout(function() {
                deferred.resolve();
            }, 10);
            return deferred.promise;
        },
        waitForChanges: function() {
            var deferred = $q.defer();
            $timeout(function() {
                deferred.resolve();
            }, 30000);
            return deferred.promise;
        }
    }
});

blacktiger.factory('reportService', function($q, $timeout) {
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
        findByPeriodAndMinimumDuration: function(hourStart, hourEnd, minDuration) {
            var deferred = $q.defer();
            var report = this.report;
            var now = new Date();
            var start = new Date(now.getTime());
            var end = new Date(now.getTime());
            start.setHours(hourStart);
            end.setHours(hourEnd);
            
            $timeout(function() {
                var result = new Array();
                for (var i = 0; i < report.length; i++) {
                    var entry = report[i];
                    if (entry.firstCallTimestamp.getTime() > start.getTime() && entry.firstCallTimestamp.getTime() < end.getTime() &&
                            entry.totalDuration >= minDuration) {
                        result.push(entry);
                    }
                }
                deferred.resolve(result);
           }, 50);
            return deferred.promise;
        },
    }
});