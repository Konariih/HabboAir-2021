//com.sulake.habbo.room.events.RoomEngineZoomEvent

package com.sulake.habbo.room.events
{
    public class RoomEngineZoomEvent extends RoomEngineEvent 
    {

        public static const ROOM_ZOOM:String = "REE_ROOM_ZOOM";

        private var _level:Number = 1;
        private var _isFlipForced:Boolean = false;

        public function RoomEngineZoomEvent(_arg_1:int, _arg_2:Number, _arg_3:*=false, _arg_4:Boolean=false, _arg_5:Boolean=false)
        {
            super("REE_ROOM_ZOOM", _arg_1, _arg_4, _arg_5);
            this._level = _arg_2;
            this._isFlipForced = _arg_3;
        }

        public function get level():Number
        {
            return (this._level);
        }

        public function get isFlipForced():Boolean
        {
            return (this._isFlipForced);
        }


    }
}//package com.sulake.habbo.room.events
