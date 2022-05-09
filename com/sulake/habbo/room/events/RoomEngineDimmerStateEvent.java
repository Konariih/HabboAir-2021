//com.sulake.habbo.room.events.RoomEngineDimmerStateEvent

package com.sulake.habbo.room.events
{
    public class RoomEngineDimmerStateEvent extends RoomEngineEvent 
    {

        public static const _SafeStr_3155:String = "REDSE_ROOM_COLOR";

        private var _state:int;
        private var _presetId:int;
        private var _effectId:int;
        private var _color:uint;
        private var _brightness:int;

        public function RoomEngineDimmerStateEvent(_arg_1:int, _arg_2:int, _arg_3:int, _arg_4:int, _arg_5:uint, _arg_6:uint, _arg_7:Boolean=false, _arg_8:Boolean=false)
        {
            super("REDSE_ROOM_COLOR", _arg_1, _arg_7, _arg_8);
            this._state = _arg_2;
            this._presetId = _arg_3;
            this._effectId = _arg_4;
            this._color = _arg_5;
            this._brightness = _arg_6;
        }

        public function get state():int
        {
            return (this._state);
        }

        public function get presetId():int
        {
            return (this._presetId);
        }

        public function get effectId():int
        {
            return (this._effectId);
        }

        public function get color():uint
        {
            return (this._color);
        }

        public function get brightness():uint
        {
            return (this._brightness);
        }


    }
}//package com.sulake.habbo.room.events
