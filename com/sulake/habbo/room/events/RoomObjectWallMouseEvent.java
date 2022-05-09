//com.sulake.habbo.room.events.RoomObjectWallMouseEvent

package com.sulake.habbo.room.events
{
    import com.sulake.room.events.RoomObjectMouseEvent;
    import com.sulake.room.utils.Vector3d;
    import com.sulake.room.object.IRoomObject;
    import com.sulake.room.utils.IVector3d;

    public class RoomObjectWallMouseEvent extends RoomObjectMouseEvent 
    {

        private var _x:Number;
        private var _y:Number;
        private var _direction:Number;
        private var _wallLocation:Vector3d = null;
        private var _wallWidth:Vector3d = null;
        private var _wallHeight:Vector3d = null;

        public function RoomObjectWallMouseEvent(_arg_1:String, _arg_2:IRoomObject, _arg_3:String, _arg_4:IVector3d, _arg_5:IVector3d, _arg_6:IVector3d, _arg_7:Number, _arg_8:Number, _arg_9:Number, _arg_10:Boolean=false, _arg_11:Boolean=false, _arg_12:Boolean=false, _arg_13:Boolean=false, _arg_14:Boolean=false, _arg_15:Boolean=false)
        {
            super(_arg_1, _arg_2, _arg_3, _arg_10, _arg_11, _arg_12, _arg_13, _arg_14, _arg_15);
            this._wallLocation = new Vector3d();
            this._wallLocation.assign(_arg_4);
            this._wallWidth = new Vector3d();
            this._wallWidth.assign(_arg_5);
            this._wallHeight = new Vector3d();
            this._wallHeight.assign(_arg_6);
            this._x = _arg_7;
            this._y = _arg_8;
            this._direction = _arg_9;
        }

        public function get wallLocation():IVector3d
        {
            return (this._wallLocation);
        }

        public function get wallWidth():IVector3d
        {
            return (this._wallWidth);
        }

        public function get wallHeight():IVector3d
        {
            return (this._wallHeight);
        }

        public function get x():Number
        {
            return (this._x);
        }

        public function get y():Number
        {
            return (this._y);
        }

        public function get direction():Number
        {
            return (this._direction);
        }


    }
}//package com.sulake.habbo.room.events
