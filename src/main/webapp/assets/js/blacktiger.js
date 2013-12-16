angular.module('blacktiger', []
        ).provider('$blacktiger', function() {
    var serviceUrl = "";
    this.setServiceUrl = function(url) {
        serviceUrl = url;
    }

    this.$get = function() {
        return {
            getServiceUrl: function() {
                return serviceUrl;
            }
        }
    }
}).factory('$room', function($q, $blacktiger, $timeout, $http, $rootScope) {
    var roomIds = null;
    var current = null;
    return {
        getRoomIds: function() {
            var deferred = $q.defer();
            if (roomIds === null) {
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
        setCurrent: function(roomId) {
            current = roomId;
            $rootScope.$broadcast("roomChanged", {roomId: roomId});
        },
        getCurrent: function() {
            return current;
        }
    }
}).factory('$participant', function($http, $q, $room, $blacktiger) {
    return {
        findOne: function(userid) {
            return $http.get($blacktiger.getServiceUrl() + "rooms/" + $room.getCurrent() + "/" + userid).then(function(request) {
                return request.data;
            });
        },
        findAll: function() {
            return $q.defer().promise;
            /*return $http.get($blacktiger.getServiceUrl() + "rooms/" + $room.getCurrent()).then(function(request) {
                return request.data;
            });*/
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
}).factory('$phonebook', function($http, $q, $room, $blacktiger) {
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
}).factory('$report', function($http, $q, $room, $blacktiger) {
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
}).factory('$dbStorage', function($q, $timeout) {
    var indexedDB = window.indexedDB || window.webkitIndexedDB || window.mozIndexedDB || window.OIndexedDB || window.msIndexedDB,
            IDBTransaction = window.IDBTransaction || window.webkitIDBTransaction || window.OIDBTransaction || window.msIDBTransaction,
            dbVersion = 1, database = null, dbName = "files", storeName = "songs" ;



    var onUpgrade = function(ev) {
        var database = ev.target.result;
        // Create an objectStore
        console.log("Creating objectStore")
        database.createObjectStore(storeName);
    }


    return {
        init: function() {
            var deferred = $q.defer();

            if (database === null) {
                var onOpen = function(event) {
                    database = event.target.result;
                    console.log("Success creating/accessing IndexedDB database");
                    
                    database.onerror = function(event) {
                        console.log("Error creating/accessing IndexedDB database");
                    };

                    deferred.resolve();
                }

                var handleError = function() {
                    deferred.reject();
                }

                var request = indexedDB.open(dbName, dbVersion);
                request.onsuccess = onOpen;
                request.onerror = handleError;
                request.onupgradeneeded = onUpgrade;
            } else {
                $timeout(function() {
                    deferred.resolve();
                }, 1);
            }
            return deferred.promise;
        },
        isSupported: function() {
            return indexedDB !== null && navigator.userAgent.indexOf('Chrome') < 0;
        },
        hasBlobs: function(names) {
            var deferred = $q.defer();
            var transaction = database.transaction([storeName], "readonly");
            var store = transaction.objectStore(storeName);
            store.openCursor().onsuccess = function(event) {
                var cursor = event.target.result;
                if (cursor) {
                    console.log(cursor.key);
                    var index = names.indexOf(cursor.key);
                    if (index >= 0) {
                        names.splice(index, 1);
                    }
                    cursor.continue();
                }
                else {
                    if (names.length === 0) {
                        deferred.resolve();
                    } else {
                        deferred.reject(names);
                    }
                }
            }
            return deferred.promise;
        },
        readBlob: function(name) {
            var deferred = $q.defer();
            var transaction = database.transaction([storeName], "readonly");
            var request = transaction.objectStore(storeName).get(name);
            request.onsuccess = function(event) {
                console.log("Got file:" + name);
                var blob = event.target.result;
                deferred.resolve(blob);
            };
            request.onerror = function(event) {
                console.log("Error retreiving file:" + name);
                deferred.reject(event);
            }
            return deferred.promise;
        },
        writeBlob: function(name, blob) {
            var deferred = $q.defer();

            var transaction = database.transaction([storeName], "readwrite"); 
            var request = transaction.objectStore(storeName).put(blob, name);
            request.onsuccess = function() {
                console.log("File persisted:" + name);
                deferred.resolve();
            };
            request.onerror = function(event) {
                console.log("Error persisting file:" + name);
                deferred.reject(event);
            };

            return deferred.promise;
        }
    }
}).factory('$fileStorage', function($q, $timeout) {
    window.requestFileSystem = window.requestFileSystem || window.webkitRequestFileSystem;
    var fileSystem = null;



    var onFailFs = function(code) {
        var msg = '';

        switch (code) {
            case FileError.QUOTA_EXCEEDED_ERR:
                msg = 'QUOTA_EXCEEDED_ERR';
                break;
            case FileError.NOT_FOUND_ERR:
                msg = 'NOT_FOUND_ERR';
                break;
            case FileError.SECURITY_ERR:
                msg = 'SECURITY_ERR';
                break;
            case FileError.INVALID_MODIFICATION_ERR:
                msg = 'INVALID_MODIFICATION_ERR';
                break;
            case FileError.INVALID_STATE_ERR:
                msg = 'INVALID_STATE_ERR';
                break;
            default:
                msg = 'Unknown Error';
                break;
        }
        ;

        console.log('Error: ' + msg);
    }




    return {
        init: function() {
            var deferred = $q.defer();

            if (fileSystem === null) {
                var size = 800000000;

                var onQuotaGranted = function() {
                    window.requestFileSystem(window.PERSISTENT, size, onInitFs, handleError);
                }
                
                var onInitFs = function(fs) {
                    console.log('Opened file system: ' + fs.name);
                    fileSystem = fs;
                    deferred.resolve();
                }

                var handleError = function(e) {
                    onFailFs(e.code);
                    deferred.reject();
                }

                //window.webkitStorageInfo.queryUsageAndQuota()
                navigator.webkitPersistentStorage.requestQuota(size, onQuotaGranted, handleError);
            } else {
                $timeout(function() {
                    deferred.resolve();
                }, 1);
            }
            return deferred.promise;
        },
        isSupported: function() {
            return window.requestFileSystem !== undefined;
        },
        hasBlobs: function(names) {
            var deferred = $q.defer();
            var dirReader = fileSystem.root.createReader();
            dirReader.readEntries(function(results) {
                for (var i = 0; i < results.length; i++) {
                    var index = names.indexOf(results[i].name);
                    if (index >= 0) {
                        names.splice(index, 1);
                    }
                }

                if (names.length === 0) {
                    deferred.resolve();
                } else {
                    deferred.reject(names);
                }
            }, function() {
                deferred.reject();
            });

            return deferred.promise;
        },
        readBlob: function(name) {
            var deferred = $q.defer();
            fileSystem.root.getFile(name, {}, function(fileEntry) {
                fileEntry.file(function(file) {
                    var reader = new FileReader();

                    reader.onloadend = function(e) {
                        deferred.resolve(new Blob([reader.result]));
                    };

                    reader.onerror = function(e) {
                        deferred.reject();
                    };

                    reader.readAsArrayBuffer(file);
                }, onFailFs);

            }, onFailFs);
            return deferred.promise;
        },
        writeBlob: function(name, blob) {
            var deferred = $q.defer();

            fileSystem.root.getFile(name, {create: true}, function(fileEntry) {
                fileEntry.createWriter(function(fileWriter) {
                    fileWriter.onwriteend = function(e) {
                        deferred.resolve();
                    };
                    fileWriter.onerror = function(e) {
                        deferred.reject();
                    };
                    fileWriter.write(blob);
                }, onFailFs);
            }, onFailFs);

            return deferred.promise;
        }
    }
}).factory('$remoteSongs', function($q, $http) {
    var baseUrl = "http://telesal.s3.amazonaws.com/music/";
    var baseSongName = "iasn_E_000";
    var replacePattern = /(iasn_E_)([0]{3})/;
    var lpad = function(s, width, char) {
        return (s.length >= width) ? s : (new Array(width).join(char) + s).slice(-width);
    }

    return {
        getNumberOfSongs: function() {
            return 135;
        },
        readBlob: function(number) {
            var deferred = $q.defer(),
                    number = lpad(number, 3, '0'),
                    songName = baseSongName.replace(replacePattern, "\$1" + number.toString() + ".mp3"),
                    url = baseUrl + songName;

            $http({method: 'GET', url: url, responseType: 'blob'}).success(function(data, status, headers, config) {
                deferred.resolve(data);
            }).error(function(data, status, headers, config) {
                deferred.reject();
            });
            return deferred.promise;
        }

    }
}).factory('$storage', function($fileStorage, $dbStorage) {
    var storage = null;

    if ($dbStorage.isSupported()) {
        storage = $dbStorage;
    } else if ($fileStorage.isSupported()) {
        storage = $fileStorage;
    }

    return {
        init: function() {
            return storage.init();
        },
        hasBlobs: function(names) {
            return storage.hasBlobs(names);
        },
        readBlob: function(name) {
            return storage.readBlob(name);
        },
        writeBlob: function(name, blob) {
            return storage.writeBlob(name, blob);
        }
    }
}).factory('$audioplayer', function($rootScope) {
    var url;
    var state = 'stopped';
    
    return {
        setUrl: function(value) {
            url = value;
        },
        getProgressPercent: function() {
            if (state === 'stopped' || !audio) {
                return 0;
            } else {
                return audio.currentTime / audio.duration * 100;
            }

        },
        getState: function() {
            return state;
        },
        play: function() {
            if (state === 'playing')
                return;

            audio = new Audio(url);
            
            var self = this;
            audio.addEventListener('ended', this.stop, false);
            audio.addEventListener('error', function(ev) {
                self.stop();
                $rootScope.$emit('audioplayer.error', ev);
            }, false);

            if (audio) {
                audio.play();
                state = "playing";
                $rootScope.$emit('audioplayer.playing');
            }
        },
        pause: function() {
            if (state === 'paused')
                return;
            if (audio) {
                audio.pause();
                state = "paused";
                $rootScope.$emit('audioplayer.paused');
            }
        },
        stop: function() {
            if (state === 'stopped')
                return;
            if (audio) {
                audio.pause();
                //audio.currentTime = 0;
                audio = null;
                state = "stopped";
                $rootScope.$emit('audioplayer.stopped');
            }
        },
        isSupported: function() {
            return Audio ? true : false;
        }
    }
});


