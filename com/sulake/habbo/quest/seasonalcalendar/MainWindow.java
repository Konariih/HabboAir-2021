
//------------------------------------------------------------
//com.sulake.habbo.quest.seasonalcalendar.MainWindow

package com.sulake.habbo.quest.seasonalcalendar
{
    import com.sulake.core.runtime.IDisposable;
    import com.sulake.habbo.quest.HabboQuestEngine;
    import com.sulake.core.window.components.IFrameWindow;
    import com.sulake.habbo.utils.WindowToggle;
    import com.sulake.habbo.quest.events.QuestsListEvent;
    import com.sulake.habbo.quest.events.QuestCompletedEvent;
    import com.sulake.habbo.communication.messages.incoming.quest.QuestMessageData;
    import com.sulake.core.window.events.WindowEvent;
    import com.sulake.core.window.IWindow;

    public class MainWindow implements IDisposable 
    {

        private var _questEngine:HabboQuestEngine;
        private var _window:IFrameWindow;
        private var _SafeStr_2929:WindowToggle;
        private var _calendar:Calendar;
        private var _catalogPromo:CatalogPromo;
        private var _SafeStr_3089:RareTeaser;
        private var _SafeStr_3090:Boolean = false;
        private var _currentDay:int;

        public function MainWindow(_arg_1:HabboQuestEngine)
        {
            this._questEngine = _arg_1;
            this._calendar = new Calendar(this._questEngine, this);
            this._catalogPromo = new CatalogPromo(this._questEngine, this);
            this._SafeStr_3089 = new RareTeaser(this._questEngine);
            this._questEngine.events.addEventListener("qe_quests_seasonal", this.onSeasonalQuests);
            this._questEngine.events.addEventListener("qce_seasonal", this.onSeasonalQuestCompleted);
        }

        public function dispose():void
        {
            if (this._questEngine)
            {
                this._questEngine.events.removeEventListener("qe_quests_seasonal", this.onSeasonalQuests);
                this._questEngine.events.removeEventListener("qce_seasonal", this.onSeasonalQuestCompleted);
                this._questEngine = null;
            };
            if (this._window)
            {
                this._window.dispose();
                this._window = null;
            };
            if (this._SafeStr_2929)
            {
                this._SafeStr_2929.dispose();
                this._SafeStr_2929 = null;
            };
            if (this._calendar)
            {
                this._calendar.close();
                this._calendar.dispose();
                this._calendar = null;
            };
            if (this._catalogPromo)
            {
                this._catalogPromo.dispose();
                this._catalogPromo = null;
            };
            if (this._SafeStr_3089)
            {
                this._SafeStr_3089.dispose();
                this._SafeStr_3089 = null;
            };
        }

        public function get disposed():Boolean
        {
            return (this._questEngine == null);
        }

        public function isVisible():Boolean
        {
            return ((this._window) && (this._window.visible));
        }

        public function close():void
        {
            if (this._calendar)
            {
                this._calendar.close();
            };
            if (this._window)
            {
                this._window.visible = false;
            };
        }

        public function onRoomExit():void
        {
            this.close();
        }

        public function onToolbarClick():void
        {
            if (!this._window)
            {
                this._questEngine.requestSeasonalQuests();
                return;
            };
            if (((!(this._SafeStr_2929)) || (this._SafeStr_2929.disposed)))
            {
                this._SafeStr_2929 = new WindowToggle(this._window, this._window.desktop, this._questEngine.requestSeasonalQuests, this.close);
            };
            this._SafeStr_2929.toggle();
        }

        public function getCalendarImageGalleryHost():String
        {
            var _local_1:String = this._questEngine.getSeasonalCampaignCodePrefix();
            return ((this._questEngine.configuration.getProperty("image.library.url") + _local_1) + "_quest_calendar/");
        }

        public function onQuests(_arg_1:Array, _arg_2:Boolean):void
        {
            if (((!(this.isVisible())) && (!(_arg_2))))
            {
                return;
            };
            this._currentDay = this.resolveCurrentDay(_arg_1);
            this._calendar.onQuests(_arg_1);
            this.refresh();
            if (_arg_2)
            {
                this._window.visible = true;
                this._window.activate();
            };
        }

        private function onSeasonalQuests(_arg_1:QuestsListEvent):void
        {
            this.onQuests(_arg_1.quests, true);
        }

        private function onSeasonalQuestCompleted(_arg_1:QuestCompletedEvent):void
        {
            this._questEngine.questController.questTracker.forceWindowCloseAfterAnimationsFinished();
            this._questEngine.requestSeasonalQuests();
        }

        public function onActivityPoints(_arg_1:int, _arg_2:int):void
        {
            this._catalogPromo.onActivityPoints(_arg_1, _arg_2);
        }

        private function resolveCurrentDay(_arg_1:Array):int
        {
            var _local_2:int;
            var _local_3:QuestMessageData;
            for each (_local_3 in _arg_1)
            {
                if (this._questEngine.isSeasonalQuest(_local_3))
                {
                    _local_2 = Math.max(_local_2, _local_3.sortOrder);
                };
            };
            return (_local_2);
        }

        private function refresh():void
        {
            this.prepareWindow();
            this._calendar.refresh();
            this._catalogPromo.refresh();
            this._SafeStr_3089.refresh();
        }

        private function prepareWindow():void
        {
            if (this._window != null)
            {
                return;
            };
            this._window = IFrameWindow(this._questEngine.getXmlWindow("SeasonalCalendar"));
            var _local_1:* = (("quests." + this._questEngine.getSeasonalCampaignCodePrefix()) + ".title");
            this._window.caption = this._questEngine.localization.getLocalizationWithParams(_local_1, _local_1);
            this._window.findChildByTag("close").procedure = this.onWindowClose;
            this._calendar.prepare(this._window);
            this._catalogPromo.prepare(this._window);
            this._SafeStr_3089.prepare(this._window);
            this._window.center();
        }

        private function onWindowClose(_arg_1:WindowEvent, _arg_2:IWindow):void
        {
            if (_arg_1.type == "WME_CLICK")
            {
                this.close();
            };
        }

        public function get currentDay():int
        {
            return (this._currentDay);
        }

        public function get catalogPromo():CatalogPromo
        {
            return (this._catalogPromo);
        }

        public function update(_arg_1:uint):void
        {
            if (((((!(this._questEngine.configuration == null)) && (this._questEngine.isFirstLoginOfDay)) && (!(this._SafeStr_3090))) && (this._questEngine.isSeasonalCalendarEnabled())))
            {
                this._questEngine.requestSeasonalQuests();
                this._SafeStr_3090 = true;
            };
        }


    }
}//package com.sulake.habbo.quest.seasonalcalendar
