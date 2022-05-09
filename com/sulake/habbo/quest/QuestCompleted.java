//com.sulake.habbo.quest.QuestCompleted

package com.sulake.habbo.quest
{
    import com.sulake.core.runtime.IDisposable;
    import com.sulake.core.window.components.IFrameWindow;
    import com.sulake.habbo.communication.messages.incoming.quest.QuestMessageData;
    import com.sulake.habbo.communication.messages.outgoing.quest._SafeStr_27;
    import com.sulake.core.window.events.WindowEvent;
    import com.sulake.core.window.IWindow;
    import com.sulake.habbo.communication.messages.outgoing.quest._SafeStr_35;
    import com.sulake.core.window.components.ITextWindow;

    public class QuestCompleted implements IDisposable 
    {

        private static const _SafeStr_3129:int = 2000;
        private static const TEXT_HEIGHT_SPACING:int = 5;
        private static const MIN_DESC_HEIGHT:int = 31;

        private var _window:IFrameWindow;
        private var _questEngine:HabboQuestEngine;
        private var _SafeStr_3125:QuestMessageData;
        private var _twinkleAnimation:Animation;
        private var _SafeStr_3130:int;

        public function QuestCompleted(_arg_1:HabboQuestEngine)
        {
            this._questEngine = _arg_1;
        }

        public function dispose():void
        {
            this._questEngine = null;
            this._SafeStr_3125 = null;
            if (this._window)
            {
                this._window.dispose();
                this._window = null;
            };
            if (this._twinkleAnimation)
            {
                this._twinkleAnimation.dispose();
                this._twinkleAnimation = null;
            };
        }

        public function get disposed():Boolean
        {
            return (this._window == null);
        }

        public function onQuest(_arg_1:QuestMessageData):void
        {
            this.close();
        }

        public function onQuestCancelled():void
        {
            this.close();
        }

        public function onQuestCompleted(_arg_1:QuestMessageData, _arg_2:Boolean):void
        {
            if (_arg_2)
            {
                this.prepare(_arg_1);
                this._SafeStr_3130 = 2000;
            };
        }

        private function close():void
        {
            if (this._window)
            {
                this._window.visible = false;
            };
        }

        private function onNextQuest(_arg_1:WindowEvent, _arg_2:IWindow):void
        {
            if (_arg_1.type != "WME_CLICK")
            {
                return;
            };
            this._window.visible = false;
            this._questEngine.questController.questDetails.openForNextQuest = this._questEngine.getBoolean("questing.showDetailsForNextQuest");
            this._questEngine.send(new _SafeStr_27());
        }

        private function onMoreQuests(_arg_1:WindowEvent, _arg_2:IWindow):void
        {
            if (_arg_1.type == "WME_CLICK")
            {
                this._window.visible = false;
                this._questEngine.send(new _SafeStr_35());
            };
        }

        public function prepare(_arg_1:QuestMessageData):void
        {
            this._SafeStr_3125 = _arg_1;
            if (this._window == null)
            {
                this._window = IFrameWindow(this._questEngine.getXmlWindow("QuestCompletedDialog"));
                this._window.findChildByTag("close").procedure = this.onNextQuest;
                this._window.findChildByName("next_quest_button").procedure = this.onNextQuest;
                this._window.findChildByName("more_quests_button").procedure = this.onMoreQuests;
                this._window.findChildByName("catalog_link_region").procedure = this.onCatalogLink;
                this._twinkleAnimation = this._questEngine.getTwinkleAnimation(this._window);
            };
            this._window.findChildByName("catalog_link_txt").caption = this._questEngine.localization.getLocalization(("quests.completed.cataloglink." + this._SafeStr_3125.activityPointType));
            var _local_2:String = ("quests.completed.reward." + this._SafeStr_3125.activityPointType);
            this._questEngine.localization.registerParameter(_local_2, "amount", this._SafeStr_3125.rewardCurrencyAmount.toString());
            this._window.findChildByName("reward_txt").caption = this._questEngine.localization.getLocalization(_local_2, _local_2);
            this._window.findChildByName("reward_txt").visible = ((this._SafeStr_3125.activityPointType >= 0) && (this._SafeStr_3125.rewardCurrencyAmount > 0));
            this._window.visible = false;
            this._window.findChildByName("congrats_txt").caption = this._questEngine.localization.getLocalization(((this._SafeStr_3125.lastQuestInCampaign) ? "quests.completed.campaign.caption" : "quests.completed.quest.caption"));
            this._window.findChildByName("more_quests_button").visible = this._SafeStr_3125.lastQuestInCampaign;
            this._window.findChildByName("campaign_reward_icon").visible = this._SafeStr_3125.lastQuestInCampaign;
            this._window.findChildByName("catalog_link_region").visible = ((!(this._SafeStr_3125.lastQuestInCampaign)) && (this._SafeStr_3125.rewardCurrencyAmount > 0));
            this._window.findChildByName("next_quest_button").visible = (!(this._SafeStr_3125.lastQuestInCampaign));
            this._window.findChildByName("reward_icon").visible = (!(this._SafeStr_3125.lastQuestInCampaign));
            this._window.findChildByName("campaign_reward_icon").visible = this._SafeStr_3125.lastQuestInCampaign;
            this._window.findChildByName("campaign_pic_bitmap").visible = this._SafeStr_3125.lastQuestInCampaign;
            this.setWindowTitle(((this._SafeStr_3125.lastQuestInCampaign) ? "quests.completed.campaign.title" : "quests.completed.quest.title"));
            this._questEngine.setupCampaignImage(this._window, _arg_1, this._SafeStr_3125.lastQuestInCampaign);
            var _local_3:ITextWindow = ITextWindow(this._window.findChildByName("desc_txt"));
            var _local_4:int = _local_3.height;
            this.setDesc((this._SafeStr_3125.getQuestLocalizationKey() + ".completed"));
            _local_3.height = Math.max(31, (_local_3.textHeight + 5));
            var _local_5:int = (_local_3.height - _local_4);
            this._window.height = (this._window.height + _local_5);
        }

        private function setWindowTitle(_arg_1:String):void
        {
            this._questEngine.localization.registerParameter(_arg_1, "category", this._questEngine.getCampaignName(this._SafeStr_3125));
            this._window.caption = this._questEngine.localization.getLocalization(_arg_1, _arg_1);
        }

        private function setDesc(_arg_1:String):void
        {
            this._window.findChildByName("desc_txt").caption = this._questEngine.localization.getLocalization(_arg_1, _arg_1);
        }

        private function onCatalogLink(_arg_1:WindowEvent, _arg_2:IWindow=null):void
        {
            if (_arg_1.type == "WME_CLICK")
            {
                this._questEngine.openCatalog(this._SafeStr_3125);
            };
        }

        public function update(_arg_1:uint):void
        {
            if (this._SafeStr_3130 > 0)
            {
                this._SafeStr_3130 = (this._SafeStr_3130 - _arg_1);
                if (this._SafeStr_3130 < 1)
                {
                    this._window.center();
                    this._window.visible = true;
                    this._window.activate();
                    if (this._SafeStr_3125.lastQuestInCampaign)
                    {
                        this._twinkleAnimation.restart();
                    }
                    else
                    {
                        this._twinkleAnimation.stop();
                    };
                };
            };
            if (this._twinkleAnimation != null)
            {
                this._twinkleAnimation.update(_arg_1);
            };
        }


    }
}//package com.sulake.habbo.quest
