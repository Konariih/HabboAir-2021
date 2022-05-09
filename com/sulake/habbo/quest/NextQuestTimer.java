//com.sulake.habbo.quest.NextQuestTimer

package com.sulake.habbo.quest
{
    import com.sulake.core.runtime.IDisposable;
    import com.sulake.habbo.communication.messages.incoming.quest.QuestMessageData;
    import com.sulake.core.window.IWindowContainer;
    import com.sulake.habbo.utils.FriendlyTime;
    import com.sulake.core.window.events.WindowEvent;
    import com.sulake.core.window.IWindow;

    public class NextQuestTimer implements IDisposable 
    {

        public static const REFRESH_PERIOD_IN_MSECS:int = 1000;
        private static const TOOLBAR_EXTENSION_ID:String = "next_quest";

        private var _questEngine:HabboQuestEngine;
        private var _SafeStr_3125:QuestMessageData;
        private var _window:IWindowContainer;
        private var _expanded:Boolean = false;
        private var _msecsToRefresh:int;

        public function NextQuestTimer(_arg_1:HabboQuestEngine)
        {
            this._questEngine = _arg_1;
        }

        public function dispose():void
        {
            if (this._questEngine)
            {
                this._questEngine.toolbar.extensionView.detachExtension("next_quest");
            };
            this._questEngine = null;
            this._SafeStr_3125 = null;
            if (this._window)
            {
                this._window.dispose();
                this._window = null;
            };
        }

        public function get disposed():Boolean
        {
            return (this._questEngine == null);
        }

        public function onQuestCancelled():void
        {
            this._SafeStr_3125 = null;
            this.close();
        }

        public function onRoomExit():void
        {
            if (((!(this._window == null)) && (this._window.visible)))
            {
                this._window.findChildByName("more_info_region").visible = false;
                this._window.findChildByName("more_info_txt").visible = false;
            };
        }

        public function onQuest(_arg_1:QuestMessageData):void
        {
            if (_arg_1.waitPeriodSeconds < 1)
            {
                this.close();
                return;
            };
            this._SafeStr_3125 = _arg_1;
            this.prepareWindow();
            this.refreshVisibility();
            this._window.visible = true;
            this._questEngine.toolbar.extensionView.attachExtension("next_quest", this._window);
        }

        private function prepareWindow():void
        {
            if (this._window != null)
            {
                return;
            };
            this._window = IWindowContainer(this._questEngine.getXmlWindow("NextQuestTimer"));
            this._window.x = 0;
            this._window.y = 0;
            this._window.findChildByName("more_info_region").procedure = this.onMoreInfo;
            this._window.findChildByName("quest_timer_expanded").procedure = this.onToggleExpanded;
            this._window.findChildByName("quest_timer_contracted").procedure = this.onToggleExpanded;
            this.refreshVisibility();
        }

        private function refresh():void
        {
            var _local_1:int = this._SafeStr_3125.waitPeriodSeconds;
            if (_local_1 < 1)
            {
                this.close();
                this._SafeStr_3125.waitPeriodSeconds = 0;
                this._questEngine.questController.onQuest(this._SafeStr_3125);
            };
            var _local_2:String = FriendlyTime.getFriendlyTime(this._questEngine.localization, _local_1);
            var _local_3:* = (this._SafeStr_3125.getCampaignLocalizationKey() + ".delayedmsg");
            this._questEngine.localization.registerParameter("quests.nextquesttimer.caption.contracted", "time", _local_2);
            this._questEngine.localization.registerParameter(_local_3, "time", _local_2);
            this._window.findChildByName("quest_header_txt").caption = this._questEngine.localization.getLocalization(("quests.nextquesttimer.caption." + ((this._expanded) ? "expanded" : "contracted")));
            this._window.findChildByName("desc_txt").caption = this._questEngine.localization.getLocalization(_local_3, _local_3);
        }

        private function refreshVisibility():void
        {
            this._window.findChildByName("quest_timer_expanded").visible = this._expanded;
            this._window.findChildByName("quest_timer_contracted").visible = (!(this._expanded));
            this._window.findChildByName("more_info_txt").visible = ((this._expanded) && (this._questEngine.currentlyInRoom));
            this._window.findChildByName("more_info_region").visible = ((this._expanded) && (this._questEngine.currentlyInRoom));
            this._window.findChildByName("quest_pic_bitmap").visible = this._expanded;
            this._window.findChildByName("desc_txt").visible = this._expanded;
            this.refresh();
        }

        private function onMoreInfo(_arg_1:WindowEvent, _arg_2:IWindow):void
        {
            if (_arg_1.type == "WME_CLICK")
            {
                this._questEngine.questController.questDetails.showDetails(this._SafeStr_3125);
            };
        }

        private function onToggleExpanded(_arg_1:WindowEvent, _arg_2:IWindow):void
        {
            if (_arg_1.type == "WME_CLICK")
            {
                this._expanded = (!(this._expanded));
                this.refreshVisibility();
            };
        }

        public function update(_arg_1:uint):void
        {
            if (((this._window == null) || (!(this._window.visible))))
            {
                return;
            };
            this._msecsToRefresh = (this._msecsToRefresh - _arg_1);
            if (this._msecsToRefresh > 0)
            {
                return;
            };
            this._msecsToRefresh = 1000;
            this.refresh();
        }

        private function getDefaultLocationX():int
        {
            return (0);
        }

        public function isVisible():Boolean
        {
            return ((this._window) && (this._window.visible));
        }

        public function close():void
        {
            if (((!(this._window == null)) && (this._window.visible)))
            {
                this._window.visible = false;
                this._questEngine.toolbar.extensionView.detachExtension("next_quest");
            };
        }

        private function setQuestImageVisible(_arg_1:Boolean):void
        {
            this._window.findChildByName("quest_pic_bitmap").visible = _arg_1;
        }


    }
}//package com.sulake.habbo.quest
