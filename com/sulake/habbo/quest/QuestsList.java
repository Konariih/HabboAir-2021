//com.sulake.habbo.quest.QuestsList

package com.sulake.habbo.quest
{
    import com.sulake.core.runtime.IDisposable;
    import com.sulake.core.window.components.IFrameWindow;
    import com.sulake.core.window.components.IItemListWindow;
    import com.sulake.core.window.components.IScrollbarWindow;
    import com.sulake.habbo.utils.WindowToggle;
    import com.sulake.habbo.quest.events.QuestsListEvent;
    import com.sulake.habbo.communication.messages.incoming.quest.QuestMessageData;
    import com.sulake.core.window.IWindowContainer;
    import com.sulake.core.window.IWindow;
    import com.sulake.core.window.components.ITextWindow;
    import com.sulake.habbo.utils.FriendlyTime;
    import com.sulake.core.window.events.WindowEvent;
    import com.sulake.habbo.communication.messages.outgoing.quest.AcceptQuestMessageComposer;
    import com.sulake.habbo.communication.messages.outgoing.quest._SafeStr_51;
    import com.sulake.habbo.window.utils.IAlertDialog;

    public class QuestsList implements IDisposable 
    {

        private static const COL_SPACING:int = 5;
        private static const QUEST_LIST_SPACING:int = 10;
        private static const CANCEL_LINK_OFFSET_FROM_RIGHT:int = 10;
        private static const COMPLETION_TEXT_OFFSET_FROM_BOTTOM:int = 30;

        private var _questEngine:HabboQuestEngine;
        private var _window:IFrameWindow;
        private var _SafeStr_853:IItemListWindow;
        private var _SafeStr_951:IScrollbarWindow;
        private var _showToolbarNotification:Boolean = true;
        private var _SafeStr_3137:Boolean;
        private var _SafeStr_2929:WindowToggle;
        private var _SafeStr_3060:Array = [];
        private var _msecsToRefresh:int = 1000;

        public function QuestsList(_arg_1:HabboQuestEngine)
        {
            this._questEngine = _arg_1;
            this._questEngine.events.addEventListener("qu_quests", this.onQuestsEvent);
        }

        public function dispose():void
        {
            if (this._questEngine)
            {
                this._questEngine.events.removeEventListener("qu_quests", this.onQuestsEvent);
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
            this._SafeStr_853 = null;
            this._SafeStr_951 = null;
            this._SafeStr_3060 = null;
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
                this._questEngine.requestQuests();
                return;
            };
            if (((!(this._SafeStr_2929)) || (this._SafeStr_2929.disposed)))
            {
                this._SafeStr_2929 = new WindowToggle(this._window, this._window.desktop, this._questEngine.requestQuests, this.close);
            };
            this._SafeStr_2929.toggle();
            this._showToolbarNotification = false;
        }

        private function onQuestsEvent(_arg_1:QuestsListEvent):void
        {
            this.onQuests(_arg_1.quests, true);
        }

        private function onQuests(_arg_1:Array, _arg_2:Boolean):void
        {
            var _local_3:QuestMessageData;
            this._SafeStr_3060 = [];
            for each (_local_3 in _arg_1)
            {
                if (!this._questEngine.isSeasonalQuest(_local_3))
                {
                    this._SafeStr_3060.push(_local_3);
                };
            };
            if (((!(this.isVisible())) && (!(_arg_2))))
            {
                return;
            };
            this.refresh(false);
            this._window.visible = true;
            this._window.activate();
            this._SafeStr_3137 = false;
            for each (_local_3 in _arg_1)
            {
                if (_local_3.accepted)
                {
                    this._SafeStr_3137 = true;
                };
            };
        }

        private function refresh(_arg_1:Boolean):void
        {
            var _local_2:int;
            var _local_3:Boolean;
            this.prepareWindow();
            this._SafeStr_853.autoArrangeItems = false;
            _local_2 = 0;
            while (true)
            {
                if (_local_2 < this._SafeStr_3060.length)
                {
                    this.refreshEntry(true, _local_2, this._SafeStr_3060[_local_2], _arg_1);
                }
                else
                {
                    _local_3 = this.refreshEntry(false, _local_2, null, _arg_1);
                    if (_local_3) break;
                };
                _local_2++;
            };
            this._SafeStr_853.autoArrangeItems = true;
        }

        private function prepareWindow():void
        {
            if (this._window != null)
            {
                return;
            };
            this._window = IFrameWindow(this._questEngine.getXmlWindow("Quests"));
            this._window.findChildByTag("close").procedure = this.onWindowClose;
            this._SafeStr_853 = IItemListWindow(this._window.findChildByName("quest_list"));
            this._SafeStr_951 = IScrollbarWindow(this._window.findChildByName("scroller"));
            this._window.center();
            this._SafeStr_853.spacing = 10;
        }

        private function refreshEntry(_arg_1:Boolean, _arg_2:int, _arg_3:QuestMessageData, _arg_4:Boolean):Boolean
        {
            var _local_6:Boolean;
            var _local_5:IWindowContainer = IWindowContainer(this._SafeStr_853.getListItemAt(_arg_2));
            if (_local_5 == null)
            {
                if (!_arg_1)
                {
                    return (true);
                };
                _local_5 = this.createListEntry(this.onAcceptQuest, this.onCancelQuest);
                this._SafeStr_853.addListItem(_local_5);
                _local_6 = true;
            };
            if (_arg_1)
            {
                if (_arg_4)
                {
                    this.refreshDelay(_local_5, _arg_3);
                }
                else
                {
                    this.refreshEntryDetails(_local_5, _arg_3);
                };
                _local_5.visible = true;
            }
            else
            {
                _local_5.visible = false;
            };
            return (false);
        }

        public function createListEntry(_arg_1:Function, _arg_2:Function):IWindowContainer
        {
            var _local_3:IWindowContainer = IWindowContainer(this._questEngine.getXmlWindow("QuestEntry"));
            var _local_4:IWindowContainer = IWindowContainer(this._questEngine.getXmlWindow("Campaign"));
            var _local_5:IWindowContainer = IWindowContainer(this._questEngine.getXmlWindow("Quest"));
            var _local_6:IWindowContainer = IWindowContainer(this._questEngine.getXmlWindow("EntryArrows"));
            var _local_7:IWindowContainer = IWindowContainer(this._questEngine.getXmlWindow("CampaignCompleted"));
            _local_3.addChild(_local_4);
            _local_3.addChild(_local_5);
            _local_3.addChild(_local_7);
            _local_3.addChild(_local_6);
            _local_5.findChildByName("accept_button").procedure = _arg_1;
            _local_5.findChildByName("cancel_region").procedure = _arg_2;
            _local_3.findChildByName("hint_txt").visible = false;
            _local_3.findChildByName("link_region").visible = false;
            var _local_8:IWindow = _local_3.findChildByName("cancel_region");
            var _local_9:IWindow = _local_3.findChildByName("cancel_txt");
            _local_8.width = _local_9.width;
            _local_8.x = ((_local_5.width - _local_8.width) - 10);
            _local_5.x = ((_local_4.x + _local_4.width) + 5);
            _local_3.width = (_local_5.x + _local_5.width);
            _local_7.x = _local_5.x;
            this.setEntryHeight(_local_3);
            return (_local_3);
        }

        public function setEntryHeight(_arg_1:IWindowContainer):void
        {
            var _local_2:IWindowContainer = IWindowContainer(_arg_1.findChildByName("campaign_container"));
            var _local_3:IWindowContainer = IWindowContainer(_arg_1.findChildByName("quest_container"));
            var _local_4:IWindowContainer = IWindowContainer(_arg_1.findChildByName("entry_arrows_cont"));
            _local_2.height = _local_3.height;
            _arg_1.height = _local_3.height;
            _local_4.x = ((_local_2.x + _local_2.width) - 2);
            _local_4.y = (Math.floor(((_local_2.height - _local_4.height) / 2)) + 1);
            _local_2.findChildByName("completion_txt").y = (_local_2.height - 30);
            var _local_5:int = 2;
            var _local_6:IWindow = _local_2.findChildByName("bg_bottom");
            _local_6.height = Math.floor(((_local_2.height - (2 * _local_5)) / 2));
            _local_6.y = (_local_5 + _local_6.height);
        }

        public function refreshEntryDetails(_arg_1:IWindowContainer, _arg_2:QuestMessageData):void
        {
            _arg_1.findChildByName("campaign_header_txt").caption = this._questEngine.getCampaignName(_arg_2);
            _arg_1.findChildByName("completion_txt").caption = ((_arg_2.completedQuestsInCampaign + "/") + _arg_2.questCountInCampaign);
            this._questEngine.setupCampaignImage(_arg_1, _arg_2, true);
            this.setColor(_arg_1, "bg", _arg_2.accepted, 4290944315, 4284769380);
            this.setColor(_arg_1, "bg_top", _arg_2.accepted, 0xFFFFD788, 4290427578);
            this.setColor(_arg_1, "bg_bottom", _arg_2.accepted, 0xFFFFC758, 4289440683);
            _arg_1.findChildByName("completion_bg_red_bitmap").visible = ((!(_arg_2.completedCampaign)) && (_arg_2.completedQuestsInCampaign < 1));
            _arg_1.findChildByName("completion_bg_blue_bitmap").visible = ((!(_arg_2.completedCampaign)) && (_arg_2.completedQuestsInCampaign > 0));
            _arg_1.findChildByName("completion_bg_green_bitmap").visible = _arg_2.completedCampaign;
            _arg_1.findChildByName("arrow_0").visible = (!(_arg_2.accepted));
            _arg_1.findChildByName("arrow_1").visible = _arg_2.accepted;
            _arg_1.findChildByName("quest_container").visible = (!(_arg_2.completedCampaign));
            _arg_1.findChildByName("campaign_completed_container").visible = _arg_2.completedCampaign;
            if (!_arg_2.completedCampaign)
            {
                this.refreshEntryQuestDetails(IWindowContainer(_arg_1.findChildByName("quest_container")), _arg_2);
                this.refreshDelay(_arg_1, _arg_2);
            };
        }

        private function refreshEntryQuestDetails(_arg_1:IWindowContainer, _arg_2:QuestMessageData):void
        {
            _arg_1.findChildByName("quest_header_txt").caption = this._questEngine.getQuestRowTitle(_arg_2);
            _arg_1.findChildByName("desc_txt").caption = this._questEngine.getQuestDesc(_arg_2);
            _arg_1.findChildByName("cancel_txt").visible = _arg_2.accepted;
            _arg_1.findChildByName("cancel_region").visible = _arg_2.accepted;
            _arg_1.findChildByName("accept_button").visible = (!(_arg_2.accepted));
            _arg_1.findChildByName("accept_button").id = _arg_2.id;
            this.setColor(_arg_1, null, _arg_2.accepted, 15982264, 0xC8C8C8);
            this.setColor(_arg_1, "quest_header", _arg_2.accepted, 15577658, 0x8D8D8D);
            ITextWindow(_arg_1.findChildByName("quest_header_txt")).textColor = ((_arg_2.accepted) ? 0xFFFFFFFF : 4281808695);
            this._questEngine.setupQuestImage(_arg_1, _arg_2);
            this._questEngine.refreshReward((_arg_2.waitPeriodSeconds < 1), _arg_1, _arg_2.activityPointType, _arg_2.rewardCurrencyAmount);
            _arg_1.findChildByName("delay_desc_txt").visible = (_arg_2.waitPeriodSeconds > 0);
            _arg_1.findChildByName("delay_txt").visible = (_arg_2.waitPeriodSeconds > 0);
            _arg_1.findChildByName("desc_txt").visible = (_arg_2.waitPeriodSeconds < 1);
        }

        public function refreshDelay(_arg_1:IWindowContainer, _arg_2:QuestMessageData):Boolean
        {
            var _local_3:int;
            var _local_4:String;
            if (_arg_1.findChildByName("delay_desc_txt").visible)
            {
                _local_3 = _arg_2.waitPeriodSeconds;
                if (_local_3 > 0)
                {
                    _local_4 = FriendlyTime.getFriendlyTime(this._questEngine.localization, _local_3);
                    _arg_1.findChildByName("delay_txt").caption = _local_4;
                }
                else
                {
                    this.refreshEntryQuestDetails(_arg_1, _arg_2);
                    return (true);
                };
            };
            return (false);
        }

        private function onWindowClose(_arg_1:WindowEvent, _arg_2:IWindow):void
        {
            var _local_3:Boolean;
            if (_arg_1.type == "WME_CLICK")
            {
                this.close();
                _local_3 = (this._questEngine.getInteger("new.identity", 0) > 0);
                if ((((_local_3) && (this._showToolbarNotification)) && (!(this._SafeStr_3137))))
                {
                    this._showToolbarNotification = false;
                    this._questEngine.habboHelp.showWelcomeScreen("HTIE_ICON_QUESTS", "quests.rejectnotification", 0);
                };
            };
        }

        private function onAcceptQuest(_arg_1:WindowEvent, _arg_2:IWindow):void
        {
            if (_arg_1.type != "WME_CLICK")
            {
                return;
            };
            var _local_3:int = _arg_2.id;
            Logger.log(("Accept quest: " + _local_3));
            this._questEngine.send(new AcceptQuestMessageComposer(_local_3));
            this._window.visible = false;
        }

        private function onCancelQuest(_arg_1:WindowEvent, _arg_2:IWindow):void
        {
            if (_arg_1.type != "WME_CLICK")
            {
                return;
            };
            Logger.log("Reject quest");
            this._questEngine.send(new _SafeStr_51());
        }

        private function setColor(_arg_1:IWindowContainer, _arg_2:String, _arg_3:Boolean, _arg_4:uint, _arg_5:uint):void
        {
            ((_arg_2 == null) ? _arg_1 : _arg_1.findChildByName(_arg_2)).color = ((_arg_3) ? _arg_4 : _arg_5);
        }

        public function onAlert(_arg_1:IAlertDialog, _arg_2:WindowEvent):void
        {
            if (((_arg_2.type == "WE_OK") || (_arg_2.type == "WE_CANCEL")))
            {
                _arg_1.dispose();
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
            this.refresh(true);
        }


    }
}//package com.sulake.habbo.quest
