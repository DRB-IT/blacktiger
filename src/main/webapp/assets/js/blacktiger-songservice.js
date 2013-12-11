blacktiger.factory('fileStorageService', function($q) {
    var indexedDB = window.indexedDB || window.webkitIndexedDB || window.mozIndexedDB || window.OIndexedDB || window.msIndexedDB,
            IDBTransaction = window.IDBTransaction || window.webkitIDBTransaction || window.OIDBTransaction || window.msIDBTransaction,
            dbVersion = 1.0;
    var request = indexedDB.open("songFiles", dbVersion);
    var createObjectStore = function(dataBase) {
        // Create an objectStore
        console.log("Creating objectStore")
        dataBase.createObjectStore("song");
    }

    request.onsuccess = function(event) {
        console.log("Success creating/accessing IndexedDB database");
        db = request.result;

        db.onerror = function(event) {
            console.log("Error creating/accessing IndexedDB database");
        };

    }

    request.onupgradeneeded = function(event) {
        createObjectStore(event.target.result);
    };

    return {
        isSupported: function() {
            return indexedDB != null;
        },
        hasBlobs: function(names) {
            var deferred = $q.defer();
            var transaction = db.transaction(["songs"], IDBTransaction.READ);

            transaction.objectStore("songs").openCursor().onsuccess = function(event) {
                var cursor = event.target.result;
                if (cursor) {
                    var index = names.indexOf(cursor.name);
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
            var transaction = db.transaction(["songs"], IDBTransaction.READ);
            var request = transaction.objectStore("songs").get(name);
            request.onsuccess = function(event) {
                console.log("Got file:" + name);
                var imgFile = event.target.result;
                deferred.resolve(imgFile.value);
            };
            request.onerror = function(event) {
                console.log("Error retreiving file:" + name);
                deferred.reject(event);
            }
            return deferred.promise;
        },
        writeBlob: function(name, blob) {
            var deferred = $q.defer();

            var transaction = db.transaction(["songs"], IDBTransaction.READ_WRITE);
            var request = transaction.objectStore("songs").put(blob, name);
            request.onsuccess(function() {
                console.log("File persisted:" + name);
                deferred.resolve();
            });
            request.onerror(function(event) {
                console.log("Error persisting file:" + name);
                deferred.reject(event);
            });

            return deferred.promise;
        }
    }
});

blacktiger.factory('songService', function($q, fileStorage) {
    var audio = null;
    var currentSongNumber = 1;
    var maxSongNumber = 135;
    var state = "stopped";
    var baseUrl = "http://telesal.s3.amazonaws.com/music/";
    var changerHandler = null;
    var baseSongName = "iasn_E_000";
    var fileFormat = "mp3";
    var replacePattern = /(iasn_E_)([0]{3})/;
    var loop = false;
    var random = false;

    // Detect format
    var testAudio = document.createElement('audio');
    if (testAudio.canPlayType) {

        var canPlayMp3 = !!testAudio.canPlayType && "probably" === testAudio.canPlayType('audio/mpeg');
        var canPlayOgg = !!testAudio.canPlayType && "probably" === testAudio.canPlayType('audio/ogg; codecs="vorbis"');
        if (!canPlayMp3 && canPlayOgg) {
            fileFormat = "ogg";
        }
    }

    var buildUrlForSong = function(number) {
        number = lpad(number, 3, '0');
        var songName = baseSongName.replace(replacePattern, "\$1" + number + "." + fileFormat);
        return baseUrl + songName;
    }

    var lpad = function(s, width, char) {
        return (s.length >= width) ? s : (new Array(width).join(char) + s).slice(-width);
    }

    var handleSongEnded = function() {
        stop();
        fireChange();
        if (loop) {
            var nextNumber;
            if (random) {
                nextNumber = randomSongNumber();
            } else {
                nextNumber = currentSongNumber + 1;
                if (nextNumber > maxSongNumber) {
                    nextNumber = 1;
                }
            }
            setCurrentSong(nextNumber);
            play();
        }
    }

    var handleError = function() {
        stop();
        fireChange();
    }

    var randomSongNumber = function() {
        return Math.floor(Math.random() * maxSongNumber) + 1;
    }

    var fireChange = function() {
        if (changeHandler)
            changeHandler();
    }

    return {
        isRandom: function() {
            return random;
        },
        setRandom: function(value) {
            random = value;
            loop = value;
            fireChange();
        },
        setChangeHandler: function(handler) {
            changeHandler = handler;
        },
        getNoOfSongs: function() {
            return maxSongNumber;
        },
        getTitle: function(number) {
            return "Unknown";//titles[index];
        },
        getCurrentSong: function() {
            return currentSongNumber;
        },
        setCurrentSong: function(number) {
            stop();
            currentSongNumber = number;
            fireChange();
        },
        getProgressPercent: function() {
            if (state == 'stopped' || !audio) {
                return 0;
            } else {
                return audio.currentTime / audio.duration * 100;
            }

        },
        getState: function() {
            return state;
        },
        getFileFormat: function() {
            return fileFormat;
        },
        play: function() {
            if (state == 'playing')
                return;

            if (random) {
                setCurrentSong(randomSongNumber());
            } else if (isNaN(currentSongNumber)) {
                return;
            }

            var url = buildUrlForSong(currentSongNumber);
            audio = new Audio(url);
            audio.addEventListener('ended', handleSongEnded, false);
            audio.addEventListener('error', handleError, false);

            if (audio) {
                audio.play();
                state = "playing";
                fireChange();
            }
        },
        pause: function() {
            if (state == 'paused')
                return;
            if (audio) {
                audio.pause();
                state = "paused";
                fireChange();
            }
        },
        stop: function() {
            if (state == 'stopped')
                return;
            if (audio) {
                audio.pause();
                //audio.currentTime = 0;
                audio = null;
                state = "stopped";
                fireChange();
            }
        },
        isSupported: function() {
            return Audio && fileStorage.isSupported() ? true : false;
        }
    }
});




