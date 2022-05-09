//com.sulake.habbo.quest.QuestController

package com.sulake.habbo.quest
{
    import com.sulake.core.runtime.IDisposable;
    import com.sulake.core.runtime.IUpdateReceiver;
    import com.sulake.habbo.quest.seasonalcalendar.MainWindow;
    import com.sulake.habbo.communication.messages.incoming.quest.QuestMessageData;

    public class QuestController implements IDisposable, IUpdateReceiver 
    {

        private var _questEngine:HabboQuestEngine;
        private var _questsList:QuestsList;
        private var _questDetails:QuestDetails;
        private var _SafeStr_3131:QuestCompleted;
        private var _questTracker:QuestTracker;
        private var _SafeStr_3132:NextQuestTimer;
        private var _seasonalCalendarWindow:MainWindow;

        public function QuestController(_arg_1:HabboQuestEngine)
        {
            this._questEngine = _arg_1;
            this._questTracker = new QuestTracker(this._questEngine);
            this._questsList = new QuestsList(this._questEngine);
            this._questDetails = new QuestDetails(this._questEngine);
            this._SafeStr_3131 = new QuestCompleted(this._questEngine);
            this._SafeStr_3132 = new NextQuestTimer(this._questEngine);
            this._seasonalCalendarWindow = new MainWindow(this._questEngine);
        }

        public function onToolbarClick():void
        {
            if (this._questEngine.isSeasonalCalendarEnabled())
            {
                this._seasonalCalendarWindow.onToolbarClick();
                this._questsList.close();
            }
            else
            {
                this._questsList.onToolbarClick();
            };
        }

        public function onQuest(_arg_1:QuestMessageData):void
        {
            this._questTracker.onQuest(_arg_1);
            this._questDetails.onQuest(_arg_1);
            this._SafeStr_3131.onQuest(_arg_1);
            this._SafeStr_3132.onQuest(_arg_1);
        }

        public function onQuestCompleted(_arg_1:QuestMessageData, _arg_2:Boolean):void
        {
            this._questTracker.onQuestCompleted(_arg_1, _arg_2);
            this._questDetails.onQuestCompleted(_arg_1);
            this._SafeStr_3131.onQuestCompleted(_arg_1, _arg_2);
        }

        public function onQuestCancelled():void
        {
            this._questTracker.onQuestCancelled();
            this._questDetails.onQuestCancelled();
            this._SafeStr_3131.onQuestCancelled();
            this._SafeStr_3132.onQuestCancelled();
        }

        public function onRoomEnter():void
        {
            this._questTracker.onRoomEnter();
        }

        public function onRoomExit():void
        {
            this._questsList.onRoomExit();
            this._seasonalCalendarWindow.onRoomExit();
            this._questTracker.onRoomExit();
            this._questDetails.onRoomExit();
            this._SafeStr_3132.onRoomExit();
        }

        public function update(_arg_1:uint):void
        {
            this._SafeStr_3131.update(_arg_1);
            this._questTracker.update(_arg_1);
            this._SafeStr_3132.update(_arg_1);
            this._questsList.update(_arg_1);
            this._questDetails.update(_arg_1);
            this._seasonalCalendarWindow.update(_arg_1);
        }

        public function dispose():void
        {
            this._questEngine = null;
            if (this._questsList)
            {
                this._questsList.dispose();
                this._questsList = null;
            };
            if (this._questTracker)
            {
                this._questTracker.dispose();
                this._questTracker = null;
            };
            if (this._questDetails)
            {
                this._questDetails.dispose();
                this._questDetails = null;
            };
            if (this._SafeStr_3131)
            {
                this._SafeStr_3131.dispose();
                this._SafeStr_3131 = null;
            };
            if (this._SafeStr_3132)
            {
                this._SafeStr_3132.dispose();
                this._SafeStr_3132 = null;
            };
            if (this._seasonalCalendarWindow)
            {
                this._seasonalCalendarWindow.dispose();
                this._seasonalCalendarWindow = null;
            };
        }

        public function get disposed():Boolean
        {
            return (this._questEngine == null);
        }

        public function get questsList():QuestsList
        {
            return (this._questsList);
        }

        public function get questDetails():QuestDetails
        {
            return (this._questDetails);
        }

        public function get questTracker():QuestTracker
        {
            return (this._questTracker);
        }

        public function get seasonalCalendarWindow():MainWindow
        {
            return (this._seasonalCalendarWindow);
        }

        public function onActivityPoints(_arg_1:int, _arg_2:int):void
        {
            if (this._seasonalCalendarWindow)
            {
                this._seasonalCalendarWindow.onActivityPoints(_arg_1, _arg_2);
            };
        }


    }
}//package com.sulake.habbo.quest
