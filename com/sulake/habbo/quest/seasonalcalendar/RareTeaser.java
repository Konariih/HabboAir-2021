//com.sulake.habbo.quest.seasonalcalendar.RareTeaser

package com.sulake.habbo.quest.seasonalcalendar
{
    import com.sulake.core.runtime.IDisposable;
    import com.sulake.habbo.quest.HabboQuestEngine;
    import com.sulake.core.window.IWindowContainer;
    import com.sulake.core.window.components.IFrameWindow;
    import com.sulake.core.window.components.IStaticBitmapWrapperWindow;
    import com.sulake.core.window.IWindow;
    import com.sulake.core.window.events.WindowEvent;

    public class RareTeaser implements IDisposable 
    {

        private var _questEngine:HabboQuestEngine;
        private var _window:IWindowContainer;
        private var _SafeStr_3091:Array;
        private var _SafeStr_3092:Array;
        private var _SafeStr_2716:Array;

        public function RareTeaser(_arg_1:HabboQuestEngine)
        {
            this._questEngine = _arg_1;
        }

        public function dispose():void
        {
            this._questEngine = null;
            this._window = null;
        }

        public function get disposed():Boolean
        {
            return (this._questEngine == null);
        }

        public function prepare(_arg_1:IFrameWindow):void
        {
            var _local_2:int;
            this._SafeStr_3091 = this.parseInts("quests.seasonalcalendar.rareteaser.days");
            this._SafeStr_3092 = this.parseStrings("quests.seasonalcalendar.rareteaser.images");
            this._SafeStr_2716 = this.parseStrings("quests.seasonalcalendar.rareteaser.pages");
            this._window = IWindowContainer(_arg_1.findChildByName("rare_teaser_cont"));
            _local_2 = 1;
            while (_local_2 <= this._SafeStr_3091.length)
            {
                this.getFurniPic(_local_2).assetUri = ((this._questEngine.questController.seasonalCalendarWindow.getCalendarImageGalleryHost() + this._SafeStr_3092[(_local_2 - 1)]) + ".png");
                _local_2++;
            };
            this.getClickRegion(1).procedure = this.onFirstSlot;
            this.getClickRegion(2).procedure = this.onSecondSlot;
            this.getClickRegion(3).procedure = this.onThirdSlot;
        }

        private function parseInts(_arg_1:String):Array
        {
            var _local_5:String;
            var _local_2:String = this._questEngine.localization.getLocalization(_arg_1, "");
            var _local_3:Array = _local_2.split(",");
            var _local_4:Array = [];
            for each (_local_5 in _local_3)
            {
                if (!isNaN(Number(_local_5)))
                {
                    _local_4.push(_local_5);
                };
            };
            return (_local_4);
        }

        private function parseStrings(_arg_1:String):Array
        {
            var _local_5:String;
            var _local_2:String = this._questEngine.localization.getLocalization(_arg_1, "");
            var _local_3:Array = _local_2.split(",");
            var _local_4:Array = [];
            for each (_local_5 in _local_3)
            {
                if (_local_5 != "")
                {
                    _local_4.push(_local_5);
                };
            };
            return (_local_4);
        }

        private function getFurniPic(_arg_1:int):IStaticBitmapWrapperWindow
        {
            return (this.getRare(_arg_1).findChildByName("furni_pic") as IStaticBitmapWrapperWindow);
        }

        private function getLockIcon(_arg_1:int):IWindow
        {
            return (this.getRare(_arg_1).findChildByName("locked_icon"));
        }

        private function getLockedBg(_arg_1:int):IWindow
        {
            return (this.getRare(_arg_1).findChildByName("locked_bg"));
        }

        private function getOpenBg(_arg_1:int):IWindow
        {
            return (this.getRare(_arg_1).findChildByName("open_bg"));
        }

        private function getClickRegion(_arg_1:int):IWindow
        {
            return (this.getRare(_arg_1).findChildByName("click_region"));
        }

        private function getRare(_arg_1:int):IWindowContainer
        {
            return (IWindowContainer(this._window.findChildByName(("rare_cont_" + _arg_1))));
        }

        public function refresh():void
        {
            var _local_1:int;
            var _local_2:Boolean;
            var _local_3:int = this._questEngine.questController.seasonalCalendarWindow.currentDay;
            var _local_4:int = -1;
            _local_1 = 1;
            while (_local_1 <= this._SafeStr_3091.length)
            {
                _local_2 = (this._SafeStr_3091[(_local_1 - 1)] > _local_3);
                this.getFurniPic(_local_1).visible = (!(_local_2));
                this.getLockIcon(_local_1).visible = _local_2;
                this.getOpenBg(_local_1).visible = (!(_local_2));
                this.getLockedBg(_local_1).visible = _local_2;
                this.getClickRegion(_local_1).visible = (!(_local_2));
                if (((_local_2) && (_local_4 == -1)))
                {
                    _local_4 = (this._SafeStr_3091[(_local_1 - 1)] - _local_3);
                };
                _local_1++;
            };
            this._window.findChildByName("teaser_info").visible = (!(_local_4 == -1));
            this._questEngine.localization.registerParameter("quests.seasonalcalendar.rareteaser.info", "days", ("" + _local_4));
        }

        private function onFirstSlot(_arg_1:WindowEvent, _arg_2:IWindow):void
        {
            this.onSlot(_arg_1, 0);
        }

        private function onSecondSlot(_arg_1:WindowEvent, _arg_2:IWindow):void
        {
            this.onSlot(_arg_1, 1);
        }

        private function onThirdSlot(_arg_1:WindowEvent, _arg_2:IWindow):void
        {
            this.onSlot(_arg_1, 2);
        }

        private function onSlot(_arg_1:WindowEvent, _arg_2:int):void
        {
            if (((_arg_1.type == "WME_CLICK") && (!(this._SafeStr_2716[_arg_2] == null))))
            {
                this._questEngine.catalog.openCatalogPage(this._SafeStr_2716[_arg_2]);
            };
        }


    }
}