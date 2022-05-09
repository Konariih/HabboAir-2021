//com.sulake.habbo.quest.AchievementCategory

package com.sulake.habbo.quest
{
    import __AS3__.vec.Vector;
    import com.sulake.habbo.communication.messages.incoming.inventory.achievements.AchievementData;
    import __AS3__.vec.*;

    public class AchievementCategory 
    {

        private var _code:String;
        private var _achievements:Vector.<AchievementData> = new Vector.<AchievementData>(0);

        public function AchievementCategory(_arg_1:String)
        {
            this._code = _arg_1;
        }

        public function add(_arg_1:AchievementData):void
        {
            this._achievements.push(_arg_1);
        }

        public function update(_arg_1:AchievementData):void
        {
            var _local_2:int;
            var _local_3:AchievementData;
            _local_2 = 0;
            while (_local_2 < this._achievements.length)
            {
                _local_3 = this._achievements[_local_2];
                if (_local_3.achievementId == _arg_1.achievementId)
                {
                    this._achievements[_local_2] = _arg_1;
                };
                _local_2++;
            };
        }

        public function getProgress():int
        {
            var _local_1:int;
            var _local_2:AchievementData;
            for each (_local_2 in this._achievements)
            {
                _local_1 = (_local_1 + ((_local_2.finalLevel) ? _local_2.level : (_local_2.level - 1)));
            };
            return (_local_1);
        }

        public function getMaxProgress():int
        {
            var _local_1:int;
            var _local_2:AchievementData;
            for each (_local_2 in this._achievements)
            {
                _local_1 = (_local_1 + _local_2.levelCount);
            };
            return (_local_1);
        }

        public function get code():String
        {
            return (this._code);
        }

        public function get achievements():Vector.<AchievementData>
        {
            return (this._achievements);
        }


    }
}//package com.sulake.habbo.quest
