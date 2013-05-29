var SongManager = new function() {
    var audio = null;
    var currentSongIndex = -1;
    
    var urls = [];
    var titles = [];
    var changerHandler = null;
    
    this.loop = false;
    this.random = false;
    
    
            
urls[001-1] = "http://download.jw.org/files/media_music/bd/iasn_D_001.mp3";
titles[001-1] = "Jehovas egenskaber";
urls[002-1] = "http://download.jw.org/files/media_music/c6/iasn_D_002.mp3";
titles[002-1] = "Vi takker dig, Jehova";
urls[003-1] = "http://download.jw.org/files/media_music/f0/iasn_D_003.mp3";
titles[003-1] = "„Gud er kærlighed“";
urls[004-1] = "http://download.jw.org/files/media_music/7c/iasn_D_004.mp3";
titles[004-1] = "Et godt navn hos Gud";
urls[005-1] = "http://download.jw.org/files/media_music/00/iasn_D_005.mp3";
titles[005-1] = "Kristus, vort forbillede";
urls[006-1] = "http://download.jw.org/files/media_music/49/iasn_D_006.mp3";
titles[006-1] = "Guds tjeners bøn";
urls[007-1] = "http://download.jw.org/files/media_music/2c/iasn_D_007.mp3";
titles[007-1] = "Kristen indvielse";
urls[008-1] = "http://download.jw.org/files/media_music/0c/iasn_D_008.mp3";
titles[008-1] = "Herrens aftensmåltid";
urls[009-1] = "http://download.jw.org/files/media_music/35/iasn_D_009.mp3";
titles[009-1] = "Pris Jehova, vor Gud!";
urls[010-1] = "http://download.jw.org/files/media_music/3f/iasn_D_010.mp3";
titles[010-1] = "„Her er jeg! Send mig“";
urls[011-1] = "http://download.jw.org/files/media_music/85/iasn_D_011.mp3";
titles[011-1] = "Vi kan glæde Jehovas hjerte";
urls[012-1] = "http://download.jw.org/files/media_music/8c/iasn_D_012.mp3";
titles[012-1] = "Løftet om evigt liv";
urls[013-1] = "http://download.jw.org/files/media_music/15/iasn_D_013.mp3";
titles[013-1] = "En takkebøn";
urls[014-1] = "http://download.jw.org/files/media_music/4e/iasn_D_014.mp3";
titles[014-1] = "„Jeg gør alting nyt“";
urls[015-1] = "http://download.jw.org/files/media_music/c9/iasn_D_015.mp3";
titles[015-1] = "Skaberværket vidner om Jehovas herlighed";
urls[016-1] = "http://download.jw.org/files/media_music/fe/iasn_D_016.mp3";
titles[016-1] = "Flygt til Guds rige!";
urls[017-1] = "http://download.jw.org/files/media_music/ae/iasn_D_017.mp3";
titles[017-1] = "Fremad, I vidner!";
urls[018-1] = "http://download.jw.org/files/media_music/e7/iasn_D_018.mp3";
titles[018-1] = "Guds loyale kærlighed";
urls[019-1] = "http://download.jw.org/files/media_music/f8/iasn_D_019.mp3";
titles[019-1] = "Guds løfte om et paradis";
urls[020-1] = "http://download.jw.org/files/media_music/7f/iasn_D_020.mp3";
titles[020-1] = "Velsign vort samvær";
urls[021-1] = "http://download.jw.org/files/media_music/87/iasn_D_021.mp3";
titles[021-1] = "Lykkelige er de barmhjertige!";
urls[022-1] = "http://download.jw.org/files/media_music/28/iasn_D_022.mp3";
titles[022-1] = "„Jehova er min hyrde“";
urls[023-1] = "http://download.jw.org/files/media_music/67/iasn_D_023.mp3";
titles[023-1] = "Jehova er vor styrke";
urls[024-1] = "http://download.jw.org/files/media_music/5e/iasn_D_024.mp3";
titles[024-1] = "Hold dig sejrsprisen for øje!";
urls[025-1] = "http://download.jw.org/files/media_music/ce/iasn_D_025.mp3";
titles[025-1] = "Kristi disciples kendetegn";
urls[026-1] = "http://download.jw.org/files/media_music/c3/iasn_D_026.mp3";
titles[026-1] = "Du må vandre med Gud!";
urls[027-1] = "http://download.jw.org/files/media_music/57/iasn_D_027.mp3";
titles[027-1] = "Stil dig på Jehovas side!";
urls[028-1] = "http://download.jw.org/files/media_music/5a/iasn_D_028.mp3";
titles[028-1] = "Den nye sang";
urls[029-1] = "http://download.jw.org/files/media_music/29/iasn_D_029.mp3";
titles[029-1] = "Jeg vil vandre i uangribelighed";
urls[030-1] = "http://download.jw.org/files/media_music/ab/iasn_D_030.mp3";
titles[030-1] = "Jehovas herredømme er begyndt";
urls[031-1] = "http://download.jw.org/files/media_music/26/iasn_D_031.mp3";
titles[031-1] = "Vidner for den sande Gud";
urls[032-1] = "http://download.jw.org/files/media_music/38/iasn_D_032.mp3";
titles[032-1] = "Vær faste, urokkelige!";
urls[033-1] = "http://download.jw.org/files/media_music/77/iasn_D_033.mp3";
titles[033-1] = "Frygt ikke for dem!";
urls[034-1] = "http://download.jw.org/files/media_music/16/iasn_D_034.mp3";
titles[034-1] = "Måtte vi bære vort navn til Guds pris";
urls[035-1] = "http://download.jw.org/files/media_music/34/iasn_D_035.mp3";
titles[035-1] = "Tak til Gud for hans tålmodighed";
urls[036-1] = "http://download.jw.org/files/media_music/68/iasn_D_036.mp3";
titles[036-1] = "’Hvad Gud har føjet sammen’";
urls[037-1] = "http://download.jw.org/files/media_music/26/iasn_D_037.mp3";
titles[037-1] = "Bibelen — inspireret af Gud";
urls[038-1] = "http://download.jw.org/files/media_music/4a/iasn_D_038.mp3";
titles[038-1] = "Kast din byrde på Jehova";
urls[039-1] = "http://download.jw.org/files/media_music/57/iasn_D_039.mp3";
titles[039-1] = "Vor kristne fred";
urls[040-1] = "http://download.jw.org/files/media_music/f4/iasn_D_040.mp3";
titles[040-1] = "Bliv ved med at søge Guds rige først";
urls[041-1] = "http://download.jw.org/files/media_music/bd/iasn_D_041.mp3";
titles[041-1] = "Tilbed Jehova endnu mens du er ung";
urls[042-1] = "http://download.jw.org/files/media_music/a0/iasn_D_042.mp3";
titles[042-1] = "’Hjælp dem som er svage’";
urls[043-1] = "http://download.jw.org/files/media_music/b6/iasn_D_043.mp3";
titles[043-1] = "Hold dig vågen, vær standhaftig!";
urls[044-1] = "http://download.jw.org/files/media_music/8c/iasn_D_044.mp3";
titles[044-1] = "Tag med glæde del i høsten";
urls[045-1] = "http://download.jw.org/files/media_music/a1/iasn_D_045.mp3";
titles[045-1] = "Gør fremskridt!";
urls[046-1] = "http://download.jw.org/files/media_music/d2/iasn_D_046.mp3";
titles[046-1] = "Jehova er vor Konge!";
urls[047-1] = "http://download.jw.org/files/media_music/f7/iasn_D_047.mp3";
titles[047-1] = "Forkynd den gode nyhed";
urls[048-1] = "http://download.jw.org/files/media_music/9c/iasn_D_048.mp3";
titles[048-1] = "Vi vil vandre med Jehova dag for dag";
urls[049-1] = "http://download.jw.org/files/media_music/4d/iasn_D_049.mp3";
titles[049-1] = "Jehova er vor tilflugt";
urls[050-1] = "http://download.jw.org/files/media_music/28/iasn_D_050.mp3";
titles[050-1] = "Det guddommelige kærlighedsmønster";
urls[051-1] = "http://download.jw.org/files/media_music/b1/iasn_D_051.mp3";
titles[051-1] = "Vi holder os nær til Jehova";
urls[052-1] = "http://download.jw.org/files/media_music/b9/iasn_D_052.mp3";
titles[052-1] = "Vogt dit hjerte";
urls[053-1] = "http://download.jw.org/files/media_music/86/iasn_D_053.mp3";
titles[053-1] = "Vor kristne enhed";
urls[054-1] = "http://download.jw.org/files/media_music/6a/iasn_D_054.mp3";
titles[054-1] = "Vi må eje troen";
urls[055-1] = "http://download.jw.org/files/media_music/66/iasn_D_055.mp3";
titles[055-1] = "Se den dag Gud giver evigt liv!";
urls[056-1] = "http://download.jw.org/files/media_music/97/iasn_D_056.mp3";
titles[056-1] = "Lyt til min bøn!";
urls[057-1] = "http://download.jw.org/files/media_music/da/iasn_D_057.mp3";
titles[057-1] = "Mit hjertes tanker";
urls[058-1] = "http://download.jw.org/files/media_music/80/iasn_D_058.mp3";
titles[058-1] = "Min indvielse til Gud i bøn";
urls[059-1] = "http://download.jw.org/files/media_music/57/iasn_D_059.mp3";
titles[059-1] = "Til Gud har vi viet alt";
urls[060-1] = "http://download.jw.org/files/media_music/99/iasn_D_060.mp3";
titles[060-1] = "Han vil gøre dig stærk";
urls[061-1] = "http://download.jw.org/files/media_music/ba/iasn_D_061.mp3";
titles[061-1] = "’Hvilken slags menneske jeg bør være’";
urls[062-1] = "http://download.jw.org/files/media_music/4b/iasn_D_062.mp3";
titles[062-1] = "Hvis ejendom er du?";
urls[063-1] = "http://download.jw.org/files/media_music/4e/iasn_D_063.mp3";
titles[063-1] = "Loyale i ét og alt";
urls[064-1] = "http://download.jw.org/files/media_music/4d/iasn_D_064.mp3";
titles[064-1] = "Lev dit liv med Gud";
urls[065-1] = "http://download.jw.org/files/media_music/ae/iasn_D_065.mp3";
titles[065-1] = "„Dette er vejen“";
urls[066-1] = "http://download.jw.org/files/media_music/1f/iasn_D_066.mp3";
titles[066-1] = "Jeg vil tjene Jehova af hele min sjæl";
urls[067-1] = "http://download.jw.org/files/media_music/8b/iasn_D_067.mp3";
titles[067-1] = "Bed til Jehova hver dag";
urls[068-1] = "http://download.jw.org/files/media_music/e8/iasn_D_068.mp3";
titles[068-1] = "Den nedtryktes bøn";
urls[069-1] = "http://download.jw.org/files/media_music/2a/iasn_D_069.mp3";
titles[069-1] = "Lad mig kende dine veje";
urls[070-1] = "http://download.jw.org/files/media_music/18/iasn_D_070.mp3";
titles[070-1] = "’Forvis jer om de mere vigtige ting’";
urls[071-1] = "http://download.jw.org/files/media_music/30/iasn_D_071.mp3";
titles[071-1] = "En bøn om hellig ånd fra Gud";
urls[072-1] = "http://download.jw.org/files/media_music/c0/iasn_D_072.mp3";
titles[072-1] = "Lad os vokse i kærlighed";
urls[073-1] = "http://download.jw.org/files/media_music/49/iasn_D_073.mp3";
titles[073-1] = "„Elsk hinanden inderligt af hjertet“";
urls[074-1] = "http://download.jw.org/files/media_music/23/iasn_D_074.mp3";
titles[074-1] = "Jehovas glæde er vores fæstning";
urls[075-1] = "http://download.jw.org/files/media_music/49/iasn_D_075.mp3";
titles[075-1] = "Vore mange grunde til glæde";
urls[076-1] = "http://download.jw.org/files/media_music/9b/iasn_D_076.mp3";
titles[076-1] = "Jehova, fredens Gud";
urls[077-1] = "http://download.jw.org/files/media_music/22/iasn_D_077.mp3";
titles[077-1] = "Vær tilgivende";
urls[078-1] = "http://download.jw.org/files/media_music/d7/iasn_D_078.mp3";
titles[078-1] = "Langmodighed";
urls[079-1] = "http://download.jw.org/files/media_music/4e/iasn_D_079.mp3";
titles[079-1] = "Den kærlige godheds magt";
urls[080-1] = "http://download.jw.org/files/media_music/56/iasn_D_080.mp3";
titles[080-1] = "Efterlign Jehovas godhed";
urls[081-1] = "http://download.jw.org/files/media_music/04/iasn_D_081.mp3";
titles[081-1] = "„Giv os mere tro“";
urls[082-1] = "http://download.jw.org/files/media_music/d1/iasn_D_082.mp3";
titles[082-1] = "Efterlign Kristi milde sind";
urls[083-1] = "http://download.jw.org/files/media_music/cf/iasn_D_083.mp3";
titles[083-1] = "Vi må beherske os";
urls[084-1] = "http://download.jw.org/files/media_music/a3/iasn_D_084.mp3";
titles[084-1] = "’Det vil jeg’";
urls[085-1] = "http://download.jw.org/files/media_music/23/iasn_D_085.mp3";
titles[085-1] = "Jehova giver sin løn";
urls[086-1] = "http://download.jw.org/files/media_music/62/iasn_D_086.mp3";
titles[086-1] = "Trofaste kvinder, kristne søstre";
urls[087-1] = "http://download.jw.org/files/media_music/a2/iasn_D_087.mp3";
titles[087-1] = "Nu er vi ét";
urls[088-1] = "http://download.jw.org/files/media_music/f2/iasn_D_088.mp3";
titles[088-1] = "En gave Gud betror forældre";
urls[89-1] = "http://download.jw.org/files/media_music/3d/iasn_D_089.mp3";
titles[89-1] = "Jehovas kærlige opfordring: „Vær vís, min søn“!";
urls[090-1] = "http://download.jw.org/files/media_music/f7/iasn_D_090.mp3";
titles[090-1] = "Skønheden i de grå hår";
urls[091-1] = "http://download.jw.org/files/media_music/e6/iasn_D_091.mp3";
titles[091-1] = "Min Fader, min Gud, min Ven";
urls[092-1] = "http://download.jw.org/files/media_music/73/iasn_D_092.mp3";
titles[092-1] = "Tal Guds ord!";
urls[093-1] = "http://download.jw.org/files/media_music/52/iasn_D_093.mp3";
titles[093-1] = "’Lad jeres lys skinne’";
urls[094-1] = "http://download.jw.org/files/media_music/2a/iasn_D_094.mp3";
titles[094-1] = "Tilfredshed med Guds gode gaver";
urls[095-1] = "http://download.jw.org/files/media_music/8f/iasn_D_095.mp3";
titles[095-1] = "„Smag og se at Jehova er god“";
urls[096-1] = "http://download.jw.org/files/media_music/00/iasn_D_096.mp3";
titles[096-1] = "Find frem til dem der fortjener det";
urls[097-1] = "http://download.jw.org/files/media_music/52/iasn_D_097.mp3";
titles[097-1] = "Gå fremad, I Rigets forkyndere!";
urls[098-1] = "http://download.jw.org/files/media_music/21/iasn_D_098.mp3";
titles[098-1] = "Så Rigets sæd";
urls[099-1] = "http://download.jw.org/files/media_music/e0/iasn_D_099.mp3";
titles[099-1] = "Pris jordens nye konge!";
urls[100-1] = "http://download.jw.org/files/media_music/52/iasn_D_100.mp3";
titles[100-1] = "Vi er Jehovas stridsmænd!";
urls[101-1] = "http://download.jw.org/files/media_music/a0/iasn_D_101.mp3";
titles[101-1] = "Gør sandheden om Riget kendt";
urls[102-1] = "http://download.jw.org/files/media_music/1f/iasn_D_102.mp3";
titles[102-1] = "Syng med på sangen om Riget!";
urls[103-1] = "http://download.jw.org/files/media_music/a9/iasn_D_103.mp3";
titles[103-1] = "„Fra hus til hus“";
urls[104-1] = "http://download.jw.org/files/media_music/56/iasn_D_104.mp3";
titles[104-1] = "Pris Jah med mig!";
urls[105-1] = "http://download.jw.org/files/media_music/91/iasn_D_105.mp3";
titles[105-1] = "Himlen forkynder Guds ære";
urls[106-1] = "http://download.jw.org/files/media_music/6e/iasn_D_106.mp3";
titles[106-1] = "Venskab med Jehova";
urls[107-1] = "http://download.jw.org/files/media_music/22/iasn_D_107.mp3";
titles[107-1] = "Kom med til Jehovas bjerg!";
urls[108-1] = "http://download.jw.org/files/media_music/2b/iasn_D_108.mp3";
titles[108-1] = "Tak til Jehova for hans salvede Konge";
urls[109-1] = "http://download.jw.org/files/media_music/80/iasn_D_109.mp3";
titles[109-1] = "Hyld Jehovas førstefødte!";
urls[110-1] = "http://download.jw.org/files/media_music/5f/iasn_D_110.mp3";
titles[110-1] = "Guds underfulde værk";
urls[111-1] = "http://download.jw.org/files/media_music/9e/iasn_D_111.mp3";
titles[111-1] = "Han vil kalde";
urls[112-1] = "http://download.jw.org/files/media_music/4d/iasn_D_112.mp3";
titles[112-1] = "Jehova, vor store Gud";
urls[113-1] = "http://download.jw.org/files/media_music/c4/iasn_D_113.mp3";
titles[113-1] = "Vores taknemmelighed for Guds ord";
urls[114-1] = "http://download.jw.org/files/media_music/f7/iasn_D_114.mp3";
titles[114-1] = "Guds egen bog — en kostelig skat";
urls[115-1] = "http://download.jw.org/files/media_music/75/iasn_D_115.mp3";
titles[115-1] = "Hav lykken med dig på din vej!";
urls[116-1] = "http://download.jw.org/files/media_music/0f/iasn_D_116.mp3";
titles[116-1] = "Lyset vokser!";
urls[117-1] = "http://download.jw.org/files/media_music/2c/iasn_D_117.mp3";
titles[117-1] = "Undervist af Jehova";
urls[118-1] = "http://download.jw.org/files/media_music/59/iasn_D_118.mp3";
titles[118-1] = "En velkomst";
urls[119-1] = "http://download.jw.org/files/media_music/3f/iasn_D_119.mp3";
titles[119-1] = "Kom og bliv opmuntret!";
urls[120-1] = "http://download.jw.org/files/media_music/e6/iasn_D_120.mp3";
titles[120-1] = "Lyt, adlyd — og bliv velsignet";
urls[121-1] = "http://download.jw.org/files/media_music/f8/iasn_D_121.mp3";
titles[121-1] = "Lad os opmuntre hinanden";
urls[122-1] = "http://download.jw.org/files/media_music/fd/iasn_D_122.mp3";
titles[122-1] = "Brødre i tusindtal";
urls[123-1] = "http://download.jw.org/files/media_music/28/iasn_D_123.mp3";
titles[123-1] = "Hyrder er gaver";
urls[124-1] = "http://download.jw.org/files/media_music/3b/iasn_D_124.mp3";
titles[124-1] = "Tag gæstfrit imod dem";
urls[125-1] = "http://download.jw.org/files/media_music/b4/iasn_D_125.mp3";
titles[125-1] = "Loyal lydighed under teokratisk orden";
urls[126-1] = "http://download.jw.org/files/media_music/40/iasn_D_126.mp3";
titles[126-1] = "Givet i kærlighed";
urls[127-1] = "http://download.jw.org/files/media_music/bf/iasn_D_127.mp3";
titles[127-1] = "Et sted til ære for dit navn";
urls[128-1] = "http://download.jw.org/files/media_music/47/iasn_D_128.mp3";
titles[128-1] = "Denne verdens scene skifter";
urls[129-1] = "http://download.jw.org/files/media_music/c1/iasn_D_129.mp3";
titles[129-1] = "Vort sikre og faste håb";
urls[130-1] = "http://download.jw.org/files/media_music/1d/iasn_D_130.mp3";
titles[130-1] = "Vort underfulde liv";
urls[131-1] = "http://download.jw.org/files/media_music/4d/iasn_D_131.mp3";
titles[131-1] = "Du er min redning, Gud";
urls[132-1] = "http://download.jw.org/files/media_music/0a/iasn_D_132.mp3";
titles[132-1] = "En sejrssang";
urls[133-1] = "http://download.jw.org/files/media_music/2b/iasn_D_133.mp3";
titles[133-1] = "Søg Gud! Han vil beskytte dig";
urls[134-1] = "http://download.jw.org/files/media_music/48/iasn_D_134.mp3";
titles[134-1] = "Se det for dig — alt er nyt";
urls[135-1] = "http://download.jw.org/files/media_music/b2/iasn_D_135.mp3";
titles[135-1] = "Hold ud til enden!";
            
    this.setChangeHandler = function(handler) {
        changeHandler = handler;
    }   
    
    this.getNoOfSongs = function() {
        return urls.length;
    }
    
    this.getTitle = function(index) {
        return titles[index];
    }
    
    this.getCurrentSong = function() {
        return currentSongIndex;
    }
    
    this.setCurrentSong = function(index) {
        SongManager.stop();
        audio = new Audio(urls[index]);
        audio.addEventListener('ended', handleSongEnded, false);
        currentSongIndex = index;
        if(changeHandler) changeHandler();
    }
    
    this.play = function() {
        if(audio) {
            audio.play();
        }
    }
    
    this.pause = function() {
        if(audio) {
            audio.pause();
        }
    }
    
    this.stop = function() {
        if(audio) {
            audio.pause();
            audio.currentTime = 0;
        }
    }
    
    this.isSupported = function() {
        return Audio ? true : false;
    }
    
    var handleSongEnded = function() {
        if(SongManager.loop) {
            var nextNumber;
            if(SongManager.random) {
                nextNumber = Math.floor(Math.random()*urls.length);
            } else {    
                nextNumber = currentSongIndex + 1;
                if(nextNumber >= urls.length) {
                    nextNumber = 0;
                }
            }
            SongManager.setCurrentSong(nextNumber);
            SongManager.play();
        }
    }
    
}

