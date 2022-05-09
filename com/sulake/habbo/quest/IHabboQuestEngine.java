//com.sulake.habbo.quest.IHabboQuestEngine

package com.sulake.habbo.quest
{
    import com.sulake.core.runtime.IUnknown;
    import flash.events.IEventDispatcher;

    public interface IHabboQuestEngine extends IUnknown 
    {

        function isTrackerVisible():Boolean;
        function ensureAchievementsInitialized():void;
        function showAchievements():void;
        function showQuests():void;
        function getAchievementLevel(_arg_1:String, _arg_2:String):int;
        function reenableRoomCompetitionWindow():void;
        function requestSeasonalQuests():void;
        function requestQuests():void;
        function get events():IEventDispatcher;
        function activateQuest(_arg_1:int):void;
        function goToQuestRooms():void;

    }
}//package com.sulake.habbo.quest

//------------------------------------------------------------
//com.sulake.habbo.quest.IncomingMessages

package com.sulake.habbo.quest
{
    import com.sulake.core.runtime.IDisposable;
    import com.sulake.habbo.window.utils.IAlertDialog;
    import com.sulake.habbo.communication.IHabboCommunicationManager;
    import com.sulake.habbo.communication.messages.parser.competition.CompetitionEntrySubmitResultMessageEvent;
    import com.sulake.habbo.communication.messages.incoming.quest.QuestCancelledMessageEvent;
    import com.sulake.habbo.communication.messages.parser.game.lobby.AchievementResolutionCompletedMessageEvent;
    import com.sulake.habbo.communication.messages.incoming.notifications.HabboAchievementNotificationMessageEvent;
    import com.sulake.habbo.communication.messages.incoming.notifications.ActivityPointsMessageEvent;
    import com.sulake.habbo.communication.messages.parser.game.lobby.AchievementResolutionsMessageEvent;
    import com.sulake.habbo.communication.messages.incoming.inventory.achievements.AchievementEvent;
    import com.sulake.habbo.communication.messages.incoming.quest.SeasonalQuestsMessageEvent;
    import com.sulake.habbo.communication.messages.incoming.quest.QuestCompletedMessageEvent;
    import com.sulake.habbo.communication.messages.incoming.inventory.achievements.AchievementsScoreEvent;
    import com.sulake.habbo.communication.messages.incoming.notifications.HabboActivityPointNotificationMessageEvent;
    import com.sulake.habbo.communication.messages.incoming.handshake.IsFirstLoginOfDayEvent;
    import com.sulake.habbo.communication.messages.parser.competition.CompetitionVotingInfoMessageEvent;
    import com.sulake.habbo.communication.messages.incoming.room.engine.RoomEntryInfoMessageEvent;
    import com.sulake.habbo.communication.messages.parser.room.session.CloseConnectionMessageEvent;
    import com.sulake.habbo.communication.messages.incoming.users.ScrSendUserInfoEvent;
    import com.sulake.habbo.communication.messages.incoming.quest.QuestMessageEvent;
    import com.sulake.habbo.communication.messages.incoming.room.engine.ObjectAddMessageEvent;
    import com.sulake.habbo.communication.messages.parser.game.lobby.AchievementResolutionProgressMessageEvent;
    import com.sulake.habbo.communication.messages.incoming.quest.QuestsMessageEvent;
    import com.sulake.habbo.communication.messages.incoming.room.engine.ObjectRemoveMessageEvent;
    import com.sulake.habbo.communication.messages.incoming.inventory.achievements.AchievementsEvent;
    import com.sulake.habbo.communication.messages.incoming.roomsettings.RoomSettingsSavedEvent;
    import com.sulake.habbo.communication.messages.parser.quest.QuestCompletedMessageParser;
    import com.sulake.habbo.quest.events.QuestCompletedEvent;
    import com.sulake.core.communication.messages.IMessageEvent;
    import com.sulake.habbo.communication.messages.parser.quest.QuestsMessageParser;
    import com.sulake.habbo.quest.events.QuestsListEvent;
    import com.sulake.habbo.communication.messages.parser.quest.SeasonalQuestsMessageParser;
    import com.sulake.habbo.communication.messages.parser.quest.QuestMessageParser;
    import com.sulake.habbo.communication.messages.parser.inventory.achievements.AchievementsMessageParser;
    import com.sulake.habbo.communication.messages.parser.game.lobby.AchievementResolutionsMessageParser;
    import com.sulake.habbo.communication.messages.parser.game.lobby.AchievementResolutionProgressMessageParser;
    import com.sulake.habbo.communication.messages.parser.game.lobby.AchievementResolutionCompletedMessageParser;
    import com.sulake.habbo.communication.messages.parser.inventory.achievements.AchievementMessageParser;
    import com.sulake.habbo.communication.messages.parser.inventory.achievements.AchievementsScoreMessageParser;
    import com.sulake.habbo.communication.messages.parser.notifications.HabboAchievementNotificationMessageParser;
    import com.sulake.habbo.communication.messages.outgoing.tracking.EventLogMessageComposer;
    import com.sulake.habbo.communication.messages.parser.handshake.IsFirstLoginOfDayParser;
    import flash.utils.Dictionary;
    import com.sulake.habbo.catalog.purse._SafeStr_139;

    public class IncomingMessages implements IDisposable 
    {

        private var _questEngine:HabboQuestEngine;
        private var _SafeStr_3124:IAlertDialog;
        private var _disposed:Boolean = false;

        public function IncomingMessages(_arg_1:HabboQuestEngine)
        {
            this._questEngine = _arg_1;
            var _local_2:IHabboCommunicationManager = this._questEngine.communication;
            _local_2.addHabboConnectionMessageEvent(new CompetitionEntrySubmitResultMessageEvent(this.onCompetitionEntrySubmitResult));
            _local_2.addHabboConnectionMessageEvent(new QuestCancelledMessageEvent(this.onQuestCancelled));
            _local_2.addHabboConnectionMessageEvent(new AchievementResolutionCompletedMessageEvent(this.onAchievementResolutionCompleted));
            _local_2.addHabboConnectionMessageEvent(new HabboAchievementNotificationMessageEvent(this.onLevelUp));
            _local_2.addHabboConnectionMessageEvent(new ActivityPointsMessageEvent(this.onActivityPoints));
            _local_2.addHabboConnectionMessageEvent(new AchievementResolutionsMessageEvent(this.onAchievementResolutions));
            _local_2.addHabboConnectionMessageEvent(new AchievementEvent(this.onAchievement));
            _local_2.addHabboConnectionMessageEvent(new SeasonalQuestsMessageEvent(this.onSeasonalQuests));
            _local_2.addHabboConnectionMessageEvent(new QuestCompletedMessageEvent(this.onQuestCompleted));
            _local_2.addHabboConnectionMessageEvent(new AchievementsScoreEvent(this.onAchievementsScore));
            _local_2.addHabboConnectionMessageEvent(new HabboActivityPointNotificationMessageEvent(this.onActivityPointsNotification));
            _local_2.addHabboConnectionMessageEvent(new IsFirstLoginOfDayEvent(this.onIsFirstLoginOfDay));
            _local_2.addHabboConnectionMessageEvent(new CompetitionVotingInfoMessageEvent(this.onCompetitionVotingInfo));
            _local_2.addHabboConnectionMessageEvent(new RoomEntryInfoMessageEvent(this.onRoomEnter));
            _local_2.addHabboConnectionMessageEvent(new CloseConnectionMessageEvent(this.onRoomExit));
            _local_2.addHabboConnectionMessageEvent(new ScrSendUserInfoEvent(this.onSubscriptionUserInfoEvent));
            _local_2.addHabboConnectionMessageEvent(new QuestMessageEvent(this.onQuest));
            _local_2.addHabboConnectionMessageEvent(new ObjectAddMessageEvent(this.onFurnisChanged));
            _local_2.addHabboConnectionMessageEvent(new AchievementResolutionProgressMessageEvent(this.onAchievementResolutionProgress));
            _local_2.addHabboConnectionMessageEvent(new QuestsMessageEvent(this.onQuests));
            _local_2.addHabboConnectionMessageEvent(new ObjectRemoveMessageEvent(this.onFurnisChanged));
            _local_2.addHabboConnectionMessageEvent(new AchievementsEvent(this.onAchievements));
            _local_2.addHabboConnectionMessageEvent(new RoomSettingsSavedEvent(this.onRoomSettingsSaved));
        }

        public function get disposed():Boolean
        {
            return (this._disposed);
        }

        private function onQuestCompleted(_arg_1:IMessageEvent):void
        {
            var _local_2:QuestCompletedMessageParser = (_arg_1 as QuestCompletedMessageEvent).getParser();
            Logger.log(((("Quest Completed: " + _local_2.questData.campaignCode) + " quest: ") + _local_2.questData.id));
            this._questEngine.questController.onQuestCompleted(_local_2.questData, _local_2.showDialog);
            if (this._questEngine.isSeasonalQuest(_local_2.questData))
            {
                this._questEngine.events.dispatchEvent(new QuestCompletedEvent("qce_seasonal", _local_2.questData));
            };
        }

        private function onQuestCancelled(_arg_1:IMessageEvent):void
        {
            Logger.log("Quest Cancelled: ");
            this._questEngine.questController.onQuestCancelled();
            if (QuestCancelledMessageEvent(_arg_1).getParser().expired)
            {
                this._questEngine.windowManager.alert("${quests.expired.title}", "${quests.expired.body}", 0, null);
            };
        }

        private function onQuests(_arg_1:IMessageEvent):void
        {
            var _local_2:QuestsMessageParser = (_arg_1 as QuestsMessageEvent).getParser();
            Logger.log(((("Got Quests: " + _local_2.quests) + ", ") + _local_2.openWindow));
            this._questEngine.events.dispatchEvent(new QuestsListEvent("qu_quests", _local_2.quests, _local_2.openWindow));
        }

        private function onSeasonalQuests(_arg_1:IMessageEvent):void
        {
            var _local_2:SeasonalQuestsMessageParser = (_arg_1 as SeasonalQuestsMessageEvent).getParser();
            Logger.log(("Got seasonal Quests: " + _local_2.quests));
            this._questEngine.events.dispatchEvent(new QuestsListEvent("qe_quests_seasonal", _local_2.quests, true));
        }

        private function onQuest(_arg_1:IMessageEvent):void
        {
            var _local_2:QuestMessageParser = (_arg_1 as QuestMessageEvent).getParser();
            Logger.log(("Got Quest: " + _local_2.quest));
            this._questEngine.questController.onQuest(_local_2.quest);
        }

        public function dispose():void
        {
            if (this._SafeStr_3124)
            {
                this._SafeStr_3124.dispose();
                this._SafeStr_3124 = null;
            };
            this._disposed = true;
        }

        private function onRoomEnter(_arg_1:RoomEntryInfoMessageEvent):void
        {
            this._questEngine.roomCompetitionController.onRoomEnter(_arg_1);
            this._questEngine.currentlyInRoom = true;
        }

        private function onRoomExit(_arg_1:IMessageEvent):void
        {
            this._questEngine.questController.onRoomExit();
            this._questEngine.achievementController.onRoomExit();
            this._questEngine.roomCompetitionController.onRoomExit();
            this._questEngine.currentlyInRoom = false;
        }

        private function onFurnisChanged(_arg_1:IMessageEvent):void
        {
            this._questEngine.roomCompetitionController.onContextChanged();
        }

        private function onRoomSettingsSaved(_arg_1:IMessageEvent):void
        {
            this._questEngine.roomCompetitionController.onContextChanged();
        }

        private function onAchievements(_arg_1:IMessageEvent):void
        {
            var _local_2:AchievementsEvent = (_arg_1 as AchievementsEvent);
            var _local_3:AchievementsMessageParser = (_local_2.getParser() as AchievementsMessageParser);
            this._questEngine.achievementController.onAchievements(_local_3.achievements, _local_3.defaultCategory);
        }

        private function onAchievementResolutions(_arg_1:AchievementResolutionsMessageEvent):void
        {
            var _local_2:AchievementResolutionsMessageParser = _arg_1.getParser();
            this._questEngine.achievementsResolutionController.onResolutionAchievements(_local_2.stuffId, _local_2.achievements, _local_2.endTime);
        }

        private function onAchievementResolutionProgress(_arg_1:AchievementResolutionProgressMessageEvent):void
        {
            var _local_2:AchievementResolutionProgressMessageParser = _arg_1.getParser();
            this._questEngine.achievementsResolutionController.onResolutionProgress(_local_2.stuffId, _local_2.achievementId, _local_2.requiredLevelBadgeCode, _local_2.userProgress, _local_2.totalProgress, _local_2.endTime);
        }

        private function onAchievementResolutionCompleted(_arg_1:AchievementResolutionCompletedMessageEvent):void
        {
            var _local_2:AchievementResolutionCompletedMessageParser = _arg_1.getParser();
            this._questEngine.achievementsResolutionController.onResolutionCompleted(_local_2.badgeCode, _local_2.stuffCode);
        }

        private function onAchievement(_arg_1:IMessageEvent):void
        {
            var _local_2:AchievementEvent = (_arg_1 as AchievementEvent);
            var _local_3:AchievementMessageParser = (_local_2.getParser() as AchievementMessageParser);
            this._questEngine.achievementController.onAchievement(_local_3.achievement);
            this._questEngine.achievementsResolutionController.onAchievement(_local_3.achievement);
        }

        private function onAchievementsScore(_arg_1:IMessageEvent):void
        {
            var _local_2:AchievementsScoreEvent = (_arg_1 as AchievementsScoreEvent);
            var _local_3:AchievementsScoreMessageParser = (_local_2.getParser() as AchievementsScoreMessageParser);
            this._questEngine.localization.registerParameter("achievements.categories.score", "score", _local_3.score.toString());
        }

        private function onLevelUp(_arg_1:IMessageEvent):void
        {
            var _local_2:HabboAchievementNotificationMessageEvent = (_arg_1 as HabboAchievementNotificationMessageEvent);
            var _local_3:HabboAchievementNotificationMessageParser = _local_2.getParser();
            var _local_4:String = this._questEngine.localization.getBadgeBaseName(_local_3.data.badgeCode);
            this._questEngine.send(new EventLogMessageComposer("Achievements", _local_4, "Leveled", "", _local_3.data.level));
            this._questEngine.achievementsResolutionController.onLevelUp(_local_3.data);
        }

        private function onIsFirstLoginOfDay(_arg_1:IMessageEvent):void
        {
            var _local_2:IsFirstLoginOfDayParser = (_arg_1 as IsFirstLoginOfDayEvent).getParser();
            this._questEngine.setIsFirstLoginOfDay(_local_2.isFirstLoginOfDay);
        }

        private function onCompetitionEntrySubmitResult(_arg_1:CompetitionEntrySubmitResultMessageEvent):void
        {
            this._questEngine.roomCompetitionController.onCompetitionEntrySubmitResult(_arg_1);
        }

        private function onCompetitionVotingInfo(_arg_1:CompetitionVotingInfoMessageEvent):void
        {
            this._questEngine.roomCompetitionController.onCompetitionVotingInfo(_arg_1);
        }

        private function onSubscriptionUserInfoEvent(_arg_1:ScrSendUserInfoEvent):void
        {
            if (((_arg_1.getParser().isVIP) && (_arg_1.getParser().responseType == 2)))
            {
                this._questEngine.roomCompetitionController.sendRoomCompetitionInit();
            };
        }

        private function onActivityPoints(_arg_1:IMessageEvent):void
        {
            var _local_3:int;
            var _local_4:Object;
            var _local_2:Dictionary = ActivityPointsMessageEvent(_arg_1).points;
            for each (_local_3 in _SafeStr_139.values())
            {
                this._questEngine.questController.onActivityPoints(_local_3, 0);
            };
            for (_local_4 in _local_2)
            {
                this._questEngine.questController.onActivityPoints(int(_local_4), _local_2[_local_4]);
            };
        }

        private function onActivityPointsNotification(_arg_1:HabboActivityPointNotificationMessageEvent):void
        {
            this._questEngine.questController.onActivityPoints(_arg_1.type, _arg_1.amount);
        }


    }
}//package com.sulake.habbo.quest
