//com.sulake.habbo.communication.messages.incoming.inventory.achievements.AchievementResolutionData

package com.sulake.habbo.communication.messages.incoming.inventory.achievements
{
    import com.sulake.core.communication.messages.IMessageDataWrapper;

    public class AchievementResolutionData 
    {

        public static const _SafeStr_1786:int = 0;

        private var _achievementId:int;
        private var _level:int;
        private var _badgeId:String;
        private var _requiredLevel:int;
        private var _state:int;

        public function AchievementResolutionData(_arg_1:IMessageDataWrapper)
        {
            this._achievementId = _arg_1.readInteger();
            this._level = _arg_1.readInteger();
            this._badgeId = _arg_1.readString();
            this._requiredLevel = _arg_1.readInteger();
            this._state = _arg_1.readInteger();
        }

        public function dispose():void
        {
            this._achievementId = 0;
            this._level = 0;
            this._badgeId = "";
            this._requiredLevel = 0;
        }

        public function get achievementId():int
        {
            return (this._achievementId);
        }

        public function get level():int
        {
            return (this._level);
        }

        public function get badgeId():String
        {
            return (this._badgeId);
        }

        public function get requiredLevel():int
        {
            return (this._requiredLevel);
        }

        public function get enabled():Boolean
        {
            return (this._state == 0);
        }

        public function get state():int
        {
            return (this._state);
        }


    }
}//package com.sulake.habbo.communication.messages.incoming.inventory.achievements
