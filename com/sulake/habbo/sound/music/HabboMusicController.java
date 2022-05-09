//------------------------------------------------------------
//com.sulake.habbo.sound.music.HabboMusicController

package com.sulake.habbo.sound.music
{
    import com.sulake.habbo.sound.IHabboMusicController;
    import com.sulake.core.runtime.IDisposable;
    import com.sulake.habbo.sound.HabboSoundManagerFlash10;
    import com.sulake.core.communication.connection.IConnection;
    import flash.events.IEventDispatcher;
    import com.sulake.core.utils.Map;
    import com.sulake.habbo.sound.IPlayListController;
    import flash.utils.Timer;
    import com.sulake.core.communication.messages.IMessageEvent;
    import com.sulake.habbo.communication.messages.incoming.sound.TraxSongInfoMessageEvent;
    import com.sulake.habbo.communication.messages.incoming.sound.UserSongDisksInventoryMessageEvent;
    import com.sulake.habbo.sound.events.NowPlayingEvent;
    import com.sulake.habbo.sound.events.SoundCompleteEvent;
    import com.sulake.habbo.sound.IHabboSound;
    import com.sulake.habbo.sound.ISongInfo;
    import com.sulake.habbo.communication.messages.outgoing.sound.GetUserSongDisksMessageComposer;
    import com.sulake.habbo.sound.trax.TraxSequencer;
    import com.sulake.habbo.communication.messages.outgoing.sound.GetSongInfoMessageComposer;
    import flash.events.TimerEvent;
    import com.sulake.habbo.communication.messages.incoming.sound.SongInfoEntry;
    import com.sulake.habbo.communication.messages.parser.sound.TraxSongInfoMessageParser;
    import com.sulake.habbo.sound.events.SongInfoReceivedEvent;
    import com.sulake.habbo.sound.events.SongDiskInventoryReceivedEvent;
    import flash.utils.getTimer;
    import com.sulake.habbo.communication.messages.parser.sound.UserSongDisksInventoryMessageParser;
    import flash.events.Event;
    import com.sulake.habbo.communication.messages.outgoing.sound.GetNowPlayingMessageComposer;
    import com.sulake.habbo.sound.*;

    public class HabboMusicController implements IHabboMusicController, IDisposable 
    {

        public static const SKIP_POSITION_SET:int = -1;
        private static const MAXIMUM_NOTIFY_PRIORITY:int = 0;

        private var _soundManager:HabboSoundManagerFlash10;
        private var _connection:IConnection;
        private var _events:IEventDispatcher;
        private var _roomEvents:IEventDispatcher;
        private var _SafeStr_3726:Map = new Map();
        private var _SafeStr_3727:Map = new Map();
        private var _SafeStr_3728:Array = [];
        private var _SafeStr_3729:IPlayListController = null;
        private var _disposed:Boolean = false;
        private var _SafeStr_3724:Array = [];
        private var _SafeStr_3725:Array = [];
        private var _SafeStr_3730:int = -1;
        private var _SafeStr_3731:int = -1;
        private var _SafeStr_3732:int = -1;
        private var _SafeStr_3723:Timer;
        private var _SafeStr_2101:Map = new Map();
        private var _SafeStr_3733:Array = [];
        private var _messageEvents:Array = [];
        private var _SafeStr_3734:int = -1;
        private var _previousNotificationTime:int = -1;

        public function HabboMusicController(_arg_1:HabboSoundManagerFlash10, _arg_2:IEventDispatcher, _arg_3:IEventDispatcher, _arg_4:IConnection)
        {
            var _local_5:int;
            var _local_6:IMessageEvent;
            super();
            this._soundManager = _arg_1;
            this._events = _arg_2;
            this._roomEvents = _arg_3;
            this._connection = _arg_4;
            this._messageEvents.push(new TraxSongInfoMessageEvent(this.onSongInfoMessage));
            this._messageEvents.push(new UserSongDisksInventoryMessageEvent(this.onSongDiskInventoryMessage));
            for each (_local_6 in this._messageEvents)
            {
                this._connection.addMessageEvent(_local_6);
            };
            this._roomEvents.addEventListener("ROSM_JUKEBOX_INIT", this.onJukeboxInit);
            this._roomEvents.addEventListener("ROSM_JUKEBOX_DISPOSE", this.onJukeboxDispose);
            this._roomEvents.addEventListener("ROSM_SOUND_MACHINE_INIT", this.onSoundMachineInit);
            this._roomEvents.addEventListener("ROSM_SOUND_MACHINE_DISPOSE", this.onSoundMachineDispose);
            this._SafeStr_3723 = new Timer(1000);
            this._SafeStr_3723.start();
            this._SafeStr_3723.addEventListener("timer", this.sendNextSongRequestMessage);
            this._events.addEventListener("SCE_TRAX_SONG_COMPLETE", this.onSongFinishedPlayingEvent);
            _local_5 = 0;
            while (_local_5 < 4)
            {
                this._SafeStr_3724[_local_5] = null;
                this._SafeStr_3725[_local_5] = 0;
                _local_5++;
            };
        }

        public function get disposed():Boolean
        {
            return (this._disposed);
        }

        public function get events():IEventDispatcher
        {
            return (this._events);
        }

        protected function onSongFinishedPlayingEvent(_arg_1:SoundCompleteEvent):void
        {
            var _local_2:int;
            Logger.log((("Song " + _arg_1.id) + " finished playing"));
            if (this.getSongIdPlayingAtPriority(this._SafeStr_3730) == _arg_1.id)
            {
                if (((this.getTopRequestPriority() == this._SafeStr_3730) && (this.getSongRequestCountAtPriority(this._SafeStr_3730) == this._SafeStr_3732)))
                {
                    this.resetSongStartRequest(this._SafeStr_3730);
                };
                _local_2 = this._SafeStr_3730;
                this.playSongWithHighestPriority();
                if (_local_2 >= 2)
                {
                    this._events.dispatchEvent(new NowPlayingEvent("NPW_USER_STOP_SONG", _local_2, _arg_1.id, -1));
                };
            };
        }

        public function dispose():void
        {
            var _local_1:int;
            var _local_2:SongDataEntry;
            var _local_3:IHabboSound;
            var _local_4:IMessageEvent;
            if (!this._disposed)
            {
                this._soundManager = null;
                this._SafeStr_3728 = null;
                if (this._connection)
                {
                    for each (_local_4 in this._messageEvents)
                    {
                        this._connection.removeMessageEvent(_local_4);
                        _local_4.dispose();
                    };
                    this._messageEvents = null;
                    this._connection = null;
                };
                if (this._SafeStr_3729)
                {
                    this._SafeStr_3729.dispose();
                    this._SafeStr_3729 = null;
                };
                if (this._SafeStr_3726)
                {
                    _local_1 = 0;
                    while (_local_1 < this._SafeStr_3726.length)
                    {
                        _local_2 = (this._SafeStr_3726.getWithIndex(_local_1) as SongDataEntry);
                        _local_3 = (_local_2.soundObject as IHabboSound);
                        if (_local_3 != null)
                        {
                            _local_3.stop();
                        };
                        _local_2.soundObject = null;
                        _local_1++;
                    };
                    this._SafeStr_3726.dispose();
                    this._SafeStr_3726 = null;
                };
                if (this._SafeStr_3727 != null)
                {
                    this._SafeStr_3727.dispose();
                    this._SafeStr_3727 = null;
                };
                this._SafeStr_3723.stop();
                this._SafeStr_3723 = null;
                if (this._roomEvents)
                {
                    this._roomEvents.removeEventListener("ROSM_JUKEBOX_INIT", this.onJukeboxInit);
                    this._roomEvents.removeEventListener("ROSM_JUKEBOX_DISPOSE", this.onJukeboxDispose);
                    this._roomEvents.removeEventListener("ROSM_SOUND_MACHINE_INIT", this.onSoundMachineInit);
                    this._roomEvents.removeEventListener("ROSM_SOUND_MACHINE_DISPOSE", this.onSoundMachineDispose);
                };
                if (this._SafeStr_2101 != null)
                {
                    this._SafeStr_2101.dispose();
                    this._SafeStr_2101 = null;
                };
                this._disposed = true;
            };
        }

        public function getRoomItemPlaylist(_arg_1:int=-1):IPlayListController
        {
            return (this._SafeStr_3729);
        }

        private function addSongStartRequest(_arg_1:int, _arg_2:int, _arg_3:Number, _arg_4:Number, _arg_5:Number, _arg_6:Number):Boolean
        {
            if (((_arg_1 < 0) || (_arg_1 >= 4)))
            {
                return (false);
            };
            var _local_7:SongStartRequestData = new SongStartRequestData(_arg_2, _arg_3, _arg_4, _arg_5, _arg_6);
            this._SafeStr_3724[_arg_1] = _local_7;
            var _local_8:int = _arg_1;
            var _local_9:int = (this._SafeStr_3725[_local_8] + 1);
            this._SafeStr_3725[_local_8] = _local_9;
            return (true);
        }

        private function getSongStartRequest(_arg_1:int):SongStartRequestData
        {
            return (this._SafeStr_3724[_arg_1]);
        }

        private function getSongIdRequestedAtPriority(_arg_1:int):int
        {
            if (((_arg_1 < 0) || (_arg_1 >= 4)))
            {
                return (-1);
            };
            if (this._SafeStr_3724[_arg_1] == null)
            {
                return (-1);
            };
            var _local_2:SongStartRequestData = this._SafeStr_3724[_arg_1];
            return (_local_2.songId);
        }

        private function getSongRequestCountAtPriority(_arg_1:int):int
        {
            if (((_arg_1 < 0) || (_arg_1 >= 4)))
            {
                return (-1);
            };
            return (this._SafeStr_3725[_arg_1]);
        }

        private function getTopRequestPriority():int
        {
            var _local_1:int;
            _local_1 = (this._SafeStr_3724.length - 1);
            while (_local_1 >= 0)
            {
                if (this._SafeStr_3724[_local_1] != null)
                {
                    return (_local_1);
                };
                _local_1--;
            };
            return (-1);
        }

        private function resetSongStartRequest(_arg_1:int):void
        {
            if (((_arg_1 >= 0) && (_arg_1 < 4)))
            {
                this._SafeStr_3724[_arg_1] = null;
            };
        }

        private function reRequestSongAtPriority(_arg_1:int):void
        {
            var _local_2:int = _arg_1;
            var _local_3:Number = (this._SafeStr_3725[_local_2] + 1);
            this._SafeStr_3725[_local_2] = _local_3;
        }

        private function processSongEntryForPlaying(_arg_1:int, _arg_2:Boolean=true):Boolean
        {
            var _local_3:SongDataEntry = this.getSongDataEntry(_arg_1);
            if (_local_3 == null)
            {
                this.addSongInfoRequest(_arg_1);
                return (false);
            };
            if (_local_3.soundObject == null)
            {
                _local_3.soundObject = this._soundManager.loadTraxSong(_local_3.id, _local_3.songData);
            };
            var _local_4:IHabboSound = _local_3.soundObject;
            if (!_local_4.ready)
            {
                return (false);
            };
            return (true);
        }

        public function playSong(_arg_1:int, _arg_2:int, _arg_3:Number=0, _arg_4:Number=0, _arg_5:Number=0.5, _arg_6:Number=0.5):Boolean
        {
            Logger.log((("Requesting " + _arg_1) + " for playing"));
            if (!this.addSongStartRequest(_arg_2, _arg_1, _arg_3, _arg_4, _arg_5, _arg_6))
            {
                return (false);
            };
            if (!this.processSongEntryForPlaying(_arg_1))
            {
                return (false);
            };
            if (_arg_2 >= this._SafeStr_3730)
            {
                this.playSongObject(_arg_2, _arg_1);
            }
            else
            {
                Logger.log(((("Higher priority song blocked playing. Stored song " + _arg_1) + " for priority ") + _arg_2));
            };
            return (true);
        }

        private function playSongWithHighestPriority():void
        {
            var _local_1:int;
            var _local_2:int;
            this._SafeStr_3730 = -1;
            this._SafeStr_3731 = -1;
            this._SafeStr_3732 = -1;
            var _local_3:int = this.getTopRequestPriority();
            _local_1 = _local_3;
            while (_local_1 >= 0)
            {
                _local_2 = this.getSongIdRequestedAtPriority(_local_1);
                if (((_local_2 >= 0) && (this.playSongObject(_local_1, _local_2))))
                {
                    return;
                };
                _local_1--;
            };
        }

        public function stop(_arg_1:int):void
        {
            var _local_2:* = (_arg_1 == this._SafeStr_3730);
            var _local_3:* = (this.getTopRequestPriority() == _arg_1);
            if (_local_2)
            {
                this.resetSongStartRequest(_arg_1);
                this.stopSongAtPriority(_arg_1);
            }
            else
            {
                this.resetSongStartRequest(_arg_1);
                if (_local_3)
                {
                    this.reRequestSongAtPriority(this._SafeStr_3730);
                };
            };
        }

        private function stopSongAtPriority(_arg_1:int):Boolean
        {
            var _local_2:int;
            var _local_3:SongDataEntry;
            if (((_arg_1 == this._SafeStr_3730) && (this._SafeStr_3730 >= 0)))
            {
                _local_2 = this.getSongIdPlayingAtPriority(_arg_1);
                if (_local_2 >= 0)
                {
                    _local_3 = this.getSongDataEntry(_local_2);
                    this.stopSongDataEntry(_local_3);
                    return (true);
                };
            };
            return (false);
        }

        private function stopSongDataEntry(_arg_1:SongDataEntry):void
        {
            var _local_2:IHabboSound;
            if (_arg_1 != null)
            {
                Logger.log(("Stopping current song " + _arg_1.id));
                _local_2 = _arg_1.soundObject;
                if (_local_2 != null)
                {
                    _local_2.stop();
                };
            };
        }

        private function getSongDataEntry(_arg_1:int):SongDataEntry
        {
            var _local_2:SongDataEntry;
            if (this._SafeStr_3726 != null)
            {
                _local_2 = (this._SafeStr_3726.getValue(_arg_1) as SongDataEntry);
            };
            return (_local_2);
        }

        public function updateVolume(_arg_1:Number):void
        {
            var _local_2:int;
            var _local_3:int;
            var _local_4:SongDataEntry;
            _local_2 = 0;
            while (_local_2 < 4)
            {
                _local_3 = this.getSongIdPlayingAtPriority(_local_2);
                if (_local_3 >= 0)
                {
                    _local_4 = (this.getSongDataEntry(_local_3) as SongDataEntry);
                    if (((!(_local_4 == null)) && (!(_local_4.soundObject == null))))
                    {
                        _local_4.soundObject.volume = _arg_1;
                    };
                };
                _local_2++;
            };
        }

        public function onSongLoaded(_arg_1:int):void
        {
            var _local_2:int;
            Logger.log(("Song loaded : " + _arg_1));
            var _local_3:int = this.getTopRequestPriority();
            if (_local_3 >= 0)
            {
                _local_2 = this.getSongIdRequestedAtPriority(_local_3);
                if (_arg_1 == _local_2)
                {
                    this.playSongObject(_local_3, _arg_1);
                };
            };
        }

        public function addSongInfoRequest(_arg_1:int):void
        {
            this.requestSong(_arg_1, true);
        }

        public function requestSongInfoWithoutSamples(_arg_1:int):void
        {
            this.requestSong(_arg_1, false);
        }

        private function requestSong(_arg_1:int, _arg_2:Boolean):void
        {
            if (this._SafeStr_3727.getValue(_arg_1) == null)
            {
                this._SafeStr_3727.add(_arg_1, _arg_2);
                this._SafeStr_3728.push(_arg_1);
            };
        }

        public function getSongInfo(_arg_1:int):ISongInfo
        {
            var _local_2:SongDataEntry = this.getSongDataEntry(_arg_1);
            if (_local_2 == null)
            {
                this.requestSongInfoWithoutSamples(_arg_1);
            };
            return (_local_2);
        }

        public function requestUserSongDisks():void
        {
            if (this._connection == null)
            {
                return;
            };
            this._connection.send(new GetUserSongDisksMessageComposer());
        }

        public function getSongDiskInventorySize():int
        {
            return (this._SafeStr_2101.length);
        }

        public function getSongDiskInventoryDiskId(_arg_1:int):int
        {
            if (((_arg_1 >= 0) && (_arg_1 < this._SafeStr_2101.length)))
            {
                return (this._SafeStr_2101.getKey(_arg_1));
            };
            return (-1);
        }

        public function getSongDiskInventorySongId(_arg_1:int):int
        {
            if (((_arg_1 >= 0) && (_arg_1 < this._SafeStr_2101.length)))
            {
                return (this._SafeStr_2101.getWithIndex(_arg_1));
            };
            return (-1);
        }

        public function getSongIdPlayingAtPriority(_arg_1:int):int
        {
            if (_arg_1 != this._SafeStr_3730)
            {
                return (-1);
            };
            return (this._SafeStr_3731);
        }

        public function samplesUnloaded(_arg_1:Array):void
        {
            var _local_2:int;
            var _local_3:SongDataEntry;
            var _local_4:TraxSequencer;
            var _local_5:Array;
            var _local_6:int;
            _local_2 = 0;
            while (_local_2 < this._SafeStr_3726.length)
            {
                _local_3 = (this._SafeStr_3726.getWithIndex(_local_2) as SongDataEntry);
                _local_4 = (_local_3.soundObject as TraxSequencer);
                if ((((!(_local_3.id == this._SafeStr_3731)) && (!(_local_4 == null))) && (_local_4.ready)))
                {
                    _local_5 = _local_4.traxData.getSampleIds();
                    _local_6 = 0;
                    while (_local_6 < _arg_1.length)
                    {
                        if (_local_5.indexOf(_arg_1[_local_6]) != -1)
                        {
                            _local_3.soundObject = null;
                            _local_4.dispose();
                            Logger.log(((("Unloaded " + _local_3.name) + " by ") + _local_3.creator));
                        };
                        _local_6++;
                    };
                };
                _local_2++;
            };
        }

        public function get samplesIdsInUse():Array
        {
            var _local_1:int;
            var _local_2:SongStartRequestData;
            var _local_3:SongDataEntry;
            var _local_4:TraxSequencer;
            var _local_5:Array = [];
            _local_1 = 0;
            while (_local_1 < this._SafeStr_3724.length)
            {
                if (this._SafeStr_3724[_local_1] != null)
                {
                    _local_2 = this._SafeStr_3724[_local_1];
                    _local_3 = this._SafeStr_3726.getValue(_local_2.songId);
                    if (_local_3 != null)
                    {
                        _local_4 = (_local_3.soundObject as TraxSequencer);
                        if (_local_4 != null)
                        {
                            _local_5 = _local_5.concat(_local_4.traxData.getSampleIds());
                        };
                    };
                };
                _local_1++;
            };
            return (_local_5);
        }

        private function sendNextSongRequestMessage(_arg_1:TimerEvent):void
        {
            if (this._SafeStr_3728.length < 1)
            {
                return;
            };
            if (this._connection == null)
            {
                return;
            };
            this._connection.send(new GetSongInfoMessageComposer(this._SafeStr_3728));
            Logger.log(("Requested song info's : " + this._SafeStr_3728));
            this._SafeStr_3728 = [];
        }

        private function onSongInfoMessage(_arg_1:IMessageEvent):void
        {
            var _local_2:int;
            var _local_3:SongInfoEntry;
            var _local_4:Boolean;
            var _local_5:Boolean;
            var _local_6:IHabboSound;
            var _local_7:SongDataEntry;
            var _local_8:int;
            var _local_9:int;
            var _local_10:TraxSongInfoMessageEvent = (_arg_1 as TraxSongInfoMessageEvent);
            var _local_11:TraxSongInfoMessageParser = (_local_10.getParser() as TraxSongInfoMessageParser);
            var _local_12:Array = _local_11.songs;
            _local_2 = 0;
            while (_local_2 < _local_12.length)
            {
                _local_3 = _local_12[_local_2];
                _local_4 = (this.getSongDataEntry(_local_3.id) == null);
                _local_5 = this.areSamplesRequested(_local_3.id);
                if (_local_4)
                {
                    _local_6 = null;
                    if (_local_5)
                    {
                        _local_6 = this._soundManager.loadTraxSong(_local_3.id, _local_3.data);
                    };
                    _local_7 = new SongDataEntry(_local_3.id, _local_3.length, _local_3.name, _local_3.creator, _local_6);
                    _local_7.songData = _local_3.data;
                    this._SafeStr_3726.add(_local_3.id, _local_7);
                    _local_8 = this.getTopRequestPriority();
                    _local_9 = this.getSongIdRequestedAtPriority(_local_8);
                    if ((((!(_local_6 == null)) && (_local_6.ready)) && (_local_3.id == _local_9)))
                    {
                        this.playSongObject(_local_8, _local_9);
                    };
                    this._events.dispatchEvent(new SongInfoReceivedEvent("SIR_TRAX_SONG_INFO_RECEIVED", _local_3.id));
                    while (this._SafeStr_3733.indexOf(_local_3.id) != -1)
                    {
                        this._SafeStr_3733.splice(this._SafeStr_3733.indexOf(_local_3.id), 1);
                        if (this._SafeStr_3733.length == 0)
                        {
                            this._events.dispatchEvent(new SongDiskInventoryReceivedEvent("SDIR_SONG_DISK_INVENTORY_RECEIVENT_EVENT"));
                        };
                    };
                    Logger.log(("Received song info : " + _local_3.id));
                };
                _local_2++;
            };
        }

        private function playSongObject(_arg_1:int, _arg_2:int):Boolean
        {
            var _local_3:Boolean;
            if ((((_arg_2 == -1) || (_arg_1 < 0)) || (_arg_1 >= 4)))
            {
                return (false);
            };
            if (this.stopSongAtPriority(this._SafeStr_3730))
            {
                _local_3 = true;
            };
            var _local_4:SongDataEntry = this.getSongDataEntry(_arg_2);
            if (_local_4 == null)
            {
                Logger.log((("WARNING: Unable to find song entry id " + _arg_2) + " that was supposed to be loaded."));
                return (false);
            };
            var _local_5:IHabboSound = _local_4.soundObject;
            if (((_local_5 == null) || (!(_local_5.ready))))
            {
                return (false);
            };
            if (_local_3)
            {
                Logger.log(("Waiting previous song to stop before playing song " + _arg_2));
                return (true);
            };
            _local_5.volume = this._soundManager.traxVolume;
            var _local_6:Number = -1;
            var _local_7:Number = 0;
            var _local_8:Number = 2;
            var _local_9:Number = 1;
            var _local_10:SongStartRequestData = this.getSongStartRequest(_arg_1);
            if (_local_10 != null)
            {
                _local_6 = _local_10.startPos;
                _local_7 = _local_10.playLength;
                _local_8 = _local_10.fadeInSeconds;
                _local_9 = _local_10.fadeOutSeconds;
            };
            if (_local_6 >= (_local_4.length / 1000))
            {
                return (false);
            };
            if (_local_6 == -1)
            {
                _local_6 = 0;
            };
            _local_5.fadeInSeconds = _local_8;
            _local_5.fadeOutSeconds = _local_9;
            _local_5.position = _local_6;
            _local_5.play(_local_7);
            this._SafeStr_3730 = _arg_1;
            this._SafeStr_3732 = this.getSongRequestCountAtPriority(_arg_1);
            this._SafeStr_3731 = _arg_2;
            if (this._SafeStr_3730 <= 0)
            {
                this.notifySongPlaying(_local_4);
            };
            if (_arg_1 > 0)
            {
                this._events.dispatchEvent(new NowPlayingEvent("NPE_USER_PLAY_SONG", _arg_1, _local_4.id, -1));
            };
            Logger.log(((((((((("Started playing song " + _arg_2) + " at position ") + _local_6) + " for ") + _local_7) + " seconds (length ") + (_local_4.length / 1000)) + ") with priority ") + _arg_1));
            return (true);
        }

        private function notifySongPlaying(_arg_1:SongDataEntry):void
        {
            var _local_2:Number = 8000;
            var _local_3:int = getTimer();
            if (((_arg_1.length >= _local_2) && ((!(this._SafeStr_3734 == _arg_1.id)) || (_local_3 > (this._previousNotificationTime + _local_2)))))
            {
                this._soundManager.notifyPlayedSong(_arg_1.name, _arg_1.creator);
                this._SafeStr_3734 = _arg_1.id;
                this._previousNotificationTime = _local_3;
            };
        }

        private function areSamplesRequested(_arg_1:int):Boolean
        {
            if (this._SafeStr_3727.getValue(_arg_1) == null)
            {
                return (false);
            };
            return (this._SafeStr_3727.getValue(_arg_1));
        }

        private function onSongDiskInventoryMessage(_arg_1:IMessageEvent):void
        {
            var _local_2:int;
            var _local_3:int;
            var _local_4:int;
            var _local_5:UserSongDisksInventoryMessageEvent = (_arg_1 as UserSongDisksInventoryMessageEvent);
            var _local_6:UserSongDisksInventoryMessageParser = (_local_5.getParser() as UserSongDisksInventoryMessageParser);
            this._SafeStr_2101.reset();
            _local_2 = 0;
            while (_local_2 < _local_6.songDiskCount)
            {
                _local_3 = _local_6.getDiskId(_local_2);
                _local_4 = _local_6.getSongId(_local_2);
                this._SafeStr_2101.add(_local_3, _local_4);
                if (this._SafeStr_3726.getValue(_local_4) == null)
                {
                    this._SafeStr_3733.push(_local_4);
                    this.requestSongInfoWithoutSamples(_local_4);
                };
                _local_2++;
            };
            if (this._SafeStr_3733.length == 0)
            {
                this._events.dispatchEvent(new SongDiskInventoryReceivedEvent("SDIR_SONG_DISK_INVENTORY_RECEIVENT_EVENT"));
            };
        }

        private function onSoundMachineInit(_arg_1:Event):void
        {
            this.disposeRoomPlaylist();
            this._SafeStr_3729 = (new SoundMachinePlayListController(this._soundManager, this, this._events, this._roomEvents, this._connection) as IPlayListController);
        }

        private function onSoundMachineDispose(_arg_1:Event):void
        {
            this.disposeRoomPlaylist();
        }

        private function onJukeboxInit(_arg_1:Event):void
        {
            this.disposeRoomPlaylist();
            this._SafeStr_3729 = (new JukeboxPlayListController(this._soundManager, this, this._events, this._connection) as IPlayListController);
            this._connection.send(new GetNowPlayingMessageComposer());
        }

        private function onJukeboxDispose(_arg_1:Event):void
        {
            this.disposeRoomPlaylist();
        }

        private function disposeRoomPlaylist():void
        {
            if (this._SafeStr_3729 != null)
            {
                this._SafeStr_3729.dispose();
                this._SafeStr_3729 = null;
            };
        }


    }
}