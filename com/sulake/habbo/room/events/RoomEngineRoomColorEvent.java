//com.sulake.habbo.room.events.RoomEngineRoomColorEvent

package com.sulake.habbo.room.events
{
    public class RoomEngineRoomColorEvent extends RoomEngineEvent 
    {

        public static const ROOM_COLOR:String = "REE_ROOM_COLOR";

        private var _color:uint;
        private var _brightness:uint;
        private var _bgOnly:Boolean;

        public function RoomEngineRoomColorEvent(_arg_1:int, _arg_2:uint, _arg_3:uint, _arg_4:Boolean, _arg_5:Boolean=false, _arg_6:Boolean=false)
        {
            super("REE_ROOM_COLOR", _arg_1, _arg_5, _arg_6);
            this._color = _arg_2;
            this._brightness = _arg_3;
            this._bgOnly = _arg_4;
        }

        public function get color():uint
        {
            return (this._color);
        }

        public function get brightness():uint
        {
            return (this._brightness);
        }

        public function get bgOnly():Boolean
        {
            return (this._bgOnly);
        }


    }
}//package com.sulake.habbo.room.events
