//com.sulake.habbo.communication.messages.incoming.inventory.avatareffect.AvatarEffect

package com.sulake.habbo.communication.messages.incoming.inventory.avatareffect
{
    public class AvatarEffect 
    {

        private var _type:int;
        private var _subType:int;
        private var _duration:int;
        private var _inactiveEffectsInInventory:int;
        private var _secondsLeftIfActive:int;
        private var _isPermanent:Boolean;


        public function get type():int
        {
            return (this._type);
        }

        public function set type(_arg_1:int):void
        {
            this._type = _arg_1;
        }

        public function get subType():int
        {
            return (this._subType);
        }

        public function set subType(_arg_1:int):void
        {
            this._subType = _arg_1;
        }

        public function get duration():int
        {
            return (this._duration);
        }

        public function set duration(_arg_1:int):void
        {
            this._duration = _arg_1;
        }

        public function get inactiveEffectsInInventory():int
        {
            return (this._inactiveEffectsInInventory);
        }

        public function set inactiveEffectsInInventory(_arg_1:int):void
        {
            this._inactiveEffectsInInventory = _arg_1;
        }

        public function get secondsLeftIfActive():int
        {
            return (this._secondsLeftIfActive);
        }

        public function set secondsLeftIfActive(_arg_1:int):void
        {
            this._secondsLeftIfActive = _arg_1;
        }

        public function get isPermanent():Boolean
        {
            return (this._isPermanent);
        }

        public function set isPermanent(_arg_1:Boolean):void
        {
            this._isPermanent = _arg_1;
        }


    }
}//package com.sulake.habbo.communication.messages.incoming.inventory.avatareffect
