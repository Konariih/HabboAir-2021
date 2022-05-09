//com.sulake.habbo.quest.AchievementCategories

package com.sulake.habbo.quest
{
    import flash.utils.Dictionary;
    import __AS3__.vec.Vector;
    import com.sulake.habbo.communication.messages.incoming.inventory.achievements.AchievementData;
    import __AS3__.vec.*;

    public class AchievementCategories 
    {

        private static const ACHIEVEMENT_DISABLED:int = 0;
        private static const ACHIEVEMENT_ENABLED:int = 1;
        private static const ACHIEVEMENT_ARCHIVED:int = 2;
        private static const ACHIEVEMENT_OFF_SEASON:int = 3;
        private static const ACHIEVEMENT_CATEGORY_ARCHIVED:String = "archive";

        private var _SafeStr_3093:Dictionary = new Dictionary();
        private var _categoryList:Vector.<AchievementCategory> = new Vector.<AchievementCategory>(0);

        public function AchievementCategories(_arg_1:Array)
        {
            var _local_5:AchievementData;
            super();
            var _local_2:AchievementCategory;
            var _local_3:AchievementCategory;
            var _local_4:AchievementCategory = new AchievementCategory("archive");
            this._SafeStr_3093["archive"] = _local_4;
            for each (_local_5 in _arg_1)
            {
                if (_local_5.category != "")
                {
                    if (_local_5.state == 2)
                    {
                        _local_3 = this._SafeStr_3093["archive"];
                    }
                    else
                    {
                        _local_3 = this._SafeStr_3093[_local_5.category];
                    };
                    if (_local_3 == null)
                    {
                        _local_3 = new AchievementCategory(_local_5.category);
                        this._SafeStr_3093[_local_5.category] = _local_3;
                        if (_local_5.category != "misc")
                        {
                            this._categoryList.push(_local_3);
                        }
                        else
                        {
                            _local_2 = _local_3;
                        };
                    };
                    _local_3.add(_local_5);
                };
            };
            if (_local_2 != null)
            {
                this._categoryList.push(_local_2);
            };
            this._categoryList.push(_local_4);
        }

        public function update(_arg_1:AchievementData):void
        {
            if (_arg_1.category == "")
            {
                return;
            };
            var _local_2:AchievementCategory = this._SafeStr_3093[_arg_1.category];
            _local_2.update(_arg_1);
        }

        public function get categoryList():Vector.<AchievementCategory>
        {
            return (this._categoryList);
        }

        public function getMaxProgress():int
        {
            var _local_1:int;
            var _local_2:AchievementCategory;
            for each (_local_2 in this._categoryList)
            {
                _local_1 = (_local_1 + _local_2.getMaxProgress());
            };
            return (_local_1);
        }

        public function getProgress():int
        {
            var _local_1:int;
            var _local_2:AchievementCategory;
            for each (_local_2 in this._categoryList)
            {
                _local_1 = (_local_1 + _local_2.getProgress());
            };
            return (_local_1);
        }

        public function getCategoryByCode(_arg_1:String):AchievementCategory
        {
            var _local_2:AchievementCategory;
            for each (_local_2 in this._categoryList)
            {
                if (_local_2.code == _arg_1)
                {
                    return (_local_2);
                };
            };
            return (null);
        }


    }
}