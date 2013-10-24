var SongManager = new function() {
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
    
    this.isRandom = function() {
        return random;
    }   
    
    this.setRandom = function(value) {
        random = value;
        loop = value;
        fireChange();
    }   
    
    this.setChangeHandler = function(handler) {
        changeHandler = handler;
    }   
    
    this.getNoOfSongs = function() {
        return maxSongNumber;
    }
    
    this.getTitle = function(number) {
        return "Unknown";//titles[index];
    }
    
    this.getCurrentSong = function() {
        return currentSongNumber;
    }
    
    this.setCurrentSong = function(number) {
        SongManager.stop();
        currentSongNumber = number;
        fireChange();
    }
    
    this.getProgressPercent = function() {
        if(state == 'stopped' || !audio) {
            return 0;
        } else {
            return audio.currentTime / audio.duration * 100;
        }
        
    }
    
    this.getState = function() {
        return state;
    }
    
    this.getFileFormat = function() {
        return fileFormat;
    }
    
    this.setFileFormat = function(format) {
        fileFormat = format;
    }
    
    this.play = function() {
        if(state == 'playing') return;
        
        if(random) {
            SongManager.setCurrentSong(randomSongNumber());
        } else if(isNaN(currentSongNumber)) {
            return;
        }
        
        var url = buildUrlForSong(currentSongNumber);
        audio = new Audio(url);
        audio.addEventListener('ended', handleSongEnded, false);
        audio.addEventListener('error', handleError, false);
        
        if(audio) {
            audio.play();
            state = "playing";
            fireChange();
        }
    }
    
    this.pause = function() {
        if(state == 'paused') return;
        if(audio) {
            audio.pause();
            state = "paused";
            fireChange();
        }
    }
    
    this.stop = function() {
        if(state == 'stopped') return;
        if(audio) {
            audio.pause();
            //audio.currentTime = 0;
            audio = null;
            state = "stopped";
            fireChange();
        }
    }
    
    this.isSupported = function() {
        return Audio ? true : false;
    }
    
    var handleSongEnded = function() {
        SongManager.stop();
        fireChange();
        if(loop) {
            var nextNumber;
            if(random) {
                nextNumber = randomSongNumber();
            } else {    
                nextNumber = currentSongNumber + 1;
                if(nextNumber > maxSongNumber) {
                    nextNumber = 1;
                }
            }
            SongManager.setCurrentSong(nextNumber);
            SongManager.play();
        }
    }
    
    var handleError = function() {
        SongManager.stop();
        fireChange();
    }
    
    var randomSongNumber = function() {
        return Math.floor(Math.random()*maxSongNumber) + 1;
    }
    
    var fireChange = function() {
        if(changeHandler) changeHandler();
    }
    
    var buildUrlForSong = function(number) {
        number = lpad(number, 3, '0');
        var songName = baseSongName.replace(replacePattern, "\$1" + number + "." + fileFormat);
        return baseUrl + songName;
    }
    
    var lpad = function(s, width, char) {
        return (s.length >= width) ? s : (new Array(width).join(char) + s).slice(-width);
    }
}

