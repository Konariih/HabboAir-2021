//com.sulake.habbo.room.messages.RoomObjectAvatarCarryObjectUpdateMessage

package com.sulake.habbo.room.messages
{
    public class RoomObjectAvatarCarryObjectUpdateMessage extends RoomObjectUpdateStateMessage 
    {

        private var _itemType:int;
        private var _itemName:String;

        public function RoomObjectAvatarCarryObjectUpdateMessage(_arg_1:int, _arg_2:String)
        {
            this._itemType = _arg_1;
            this._itemName = _arg_2;
        }

        public function get itemType():int
        {
            return (this._itemType);
        }

        public function get itemName():String
        {
            return (this._itemName);
        }


    }
}//package com.sulake.habbo.room.messages
