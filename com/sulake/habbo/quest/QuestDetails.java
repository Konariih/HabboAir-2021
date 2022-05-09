//com.sulake.habbo.quest.QuestDetails

package com.sulake.habbo.quest
{
    import com.sulake.core.runtime.IDisposable;
    import flash.geom.Point;
    import com.sulake.core.window.components.IFrameWindow;
    import com.sulake.habbo.communication.messages.incoming.quest.QuestMessageData;
    import com.sulake.core.window.IWindowContainer;
    import com.sulake.core.window.components.ITextWindow;
    import com.sulake.core.window.components.IRegionWindow;
    import com.sulake.core.window.events.WindowEvent;
    import com.sulake.core.window.IWindow;
    import com.sulake.habbo.communication.messages.outgoing.quest.AcceptQuestMessageComposer;
    import com.sulake.habbo.communication.messages.outgoing.quest.ActivateQuestMessageComposer;
    import com.sulake.habbo.communication.messages.outgoing.quest._SafeStr_51;

    public class QuestDetails implements IDisposable 
    {

        private static const _SafeStr_3098:int = 56;
        private static const SPACING:int = 5;
        private static const TEXT_HEIGHT_SPACING:int = 5;
        private static const _SafeStr_3133:Point = new Point(8, 8);
        private static const _SafeStr_3134:Array = ["PLACE_ITEM", "PLACE_FLOOR", "PLACE_WALLPAPER", "PET_DRINK", "PET_EAT"];

        private var _questEngine:HabboQuestEngine;
        private var _window:IFrameWindow;
        private var _SafeStr_3135:Boolean;
        private var _SafeStr_3125:QuestMessageData;
        private var _msecsToRefresh:int;
        private var _SafeStr_3136:Boolean = false;

        public function QuestDetails(_arg_1:HabboQuestEngine)
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
        }

        public function get disposed():Boolean
        {
            return (this._questEngine == null);
        }

        public function onQuest(_arg_1:QuestMessageData):void
        {
            if (this._SafeStr_3135)
            {
                this._SafeStr_3135 = false;
                this.openDetails(_arg_1);
            }
            else
            {
                if (((this._SafeStr_3125 == null) || (!(this._SafeStr_3125.id == _arg_1.id))))
                {
                    this.close();
                };
            };
        }

        public function onQuestCompleted(_arg_1:QuestMessageData):void
        {
            this.close();
        }

        public function onQuestCancelled():void
        {
            this.close();
        }

        public function onRoomExit():void
        {
            this.close();
        }

        private function close():void
        {
            if (this._window)
            {
                this._window.visible = false;
            };
        }

        public function showDetails(_arg_1:QuestMessageData):void
        {
            if (((this._window) && (this._window.visible)))
            {
                this._window.visible = false;
                return;
            };
            this.openDetails(_arg_1);
        }

        public function openDetails(_arg_1:QuestMessageData, _arg_2:Boolean=false):void
        {
            var _local_3:IWindowContainer;
            this._SafeStr_3125 = _arg_1;
            if (_arg_1 == null)
            {
                return;
            };
            this._SafeStr_3136 = _arg_2;
            if (this._window == null)
            {
                this._window = IFrameWindow(this._questEngine.getXmlWindow("QuestDetails"));
                this._window.findChildByTag("close").procedure = this.onDetailsWindowClose;
                this._window.center();
                _local_3 = this._questEngine.questController.questsList.createListEntry(this.onAcceptQuest, this.onCancelQuest);
                _local_3.x = _SafeStr_3133.x;
                _local_3.y = _SafeStr_3133.y;
                this._window.content.addChild(_local_3);
                this._window.findChildByName("link_region").procedure = this.onLinkProc;
            };
            _local_3 = IWindowContainer(this._window.findChildByName("entry_container"));
            this._questEngine.questController.questsList.refreshEntryDetails(_local_3, _arg_1);
            var _local_4:* = (this._SafeStr_3125.waitPeriodSeconds > 0);
            var _local_5:ITextWindow = ITextWindow(_local_3.findChildByName("hint_txt"));
            var _local_6:int = this.getTextHeight(_local_5);
            if (!_local_4)
            {
                _local_5.caption = this._questEngine.getQuestHint(_arg_1);
                _local_5.height = (_local_5.textHeight + 5);
            };
            _local_5.visible = (!(_local_4));
            var _local_7:int = (this.getTextHeight(_local_5) - _local_6);
            var _local_8:int = this.setupLink("link_region", ((_local_5.y + _local_5.height) + 5));
            var _local_9:IWindowContainer = IWindowContainer(_local_3.findChildByName("quest_container"));
            _local_9.height = (_local_9.height + (_local_7 + _local_8));
            this._questEngine.questController.questsList.setEntryHeight(_local_3);
            this._window.height = (_local_3.height + 56);
            this._window.visible = true;
            this._window.activate();
        }

        private function setupLink(_arg_1:String, _arg_2:int):int
        {
            var _local_8:int;
            var _local_3:Boolean = this.hasCatalogLink();
            var _local_4:Boolean = ((!(_local_3)) && (this.hasNavigatorLink()));
            var _local_5:Boolean = (((!(_local_3)) && (!(_local_4))) && (this.hasRoomLink()));
            var _local_6:Boolean = (((_local_3) || (_local_4)) || (_local_5));
            var _local_7:IRegionWindow = IRegionWindow(this._window.findChildByName(_arg_1));
            _local_7.y = _arg_2;
            if (((_local_6) && (!(_local_7.visible))))
            {
                _local_8 = (5 + _local_7.height);
            };
            if (((!(_local_6)) && (_local_7.visible)))
            {
                _local_8 = (-(5) - _local_7.height);
            };
            _local_7.visible = _local_6;
            _local_7.findChildByName("link_catalog").visible = _local_3;
            _local_7.findChildByName("link_navigator").visible = _local_4;
            _local_7.findChildByName("link_room").visible = _local_5;
            return (_local_8);
        }

        private function hasCatalogLink():Boolean
        {
            return ((this._SafeStr_3125.waitPeriodSeconds < 1) && (_SafeStr_3134.indexOf(this._SafeStr_3125.type) > -1));
        }

        private function hasNavigatorLink():Boolean
        {
            var _local_1:Boolean = this._questEngine.hasLocalizedValue((this._SafeStr_3125.getCampaignLocalizationKey() + ".searchtag"));
            var _local_2:Boolean = this._questEngine.hasLocalizedValue((this._SafeStr_3125.getCampaignLocalizationKey() + ".searchtag"));
            return ((this._SafeStr_3125.waitPeriodSeconds < 1) && ((_local_1) || (_local_2)));
        }

        private function hasRoomLink():Boolean
        {
            return (((this._SafeStr_3125.waitPeriodSeconds < 1) && (this._questEngine.isSeasonalQuest(this._SafeStr_3125))) && (this._questEngine.hasQuestRoomsIds()));
        }

        private function getTextHeight(_arg_1:ITextWindow):int
        {
            return ((_arg_1.visible) ? _arg_1.height : 0);
        }

        private function onDetailsWindowClose(_arg_1:WindowEvent, _arg_2:IWindow):void
        {
            if (_arg_1.type == "WME_CLICK")
            {
                this._window.visible = false;
            };
        }

        public function set openForNextQuest(_arg_1:Boolean):void
        {
            this._SafeStr_3135 = _arg_1;
        }

        private function onLinkProc(_arg_1:WindowEvent, _arg_2:IWindow=null):void
        {
            if (_arg_1.type == "WME_CLICK")
            {
                if (this.hasCatalogLink())
                {
                    this._questEngine.openCatalog(this._SafeStr_3125);
                }
                else
                {
                    if (this.hasNavigatorLink())
                    {
                        this._questEngine.openNavigator(this._SafeStr_3125);
                    }
                    else
                    {
                        this._questEngine.goToQuestRooms();
                    };
                };
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
            var _local_2:Boolean = this._questEngine.questController.questsList.refreshDelay(this._window, this._SafeStr_3125);
            if (_local_2)
            {
                this.openDetails(this._SafeStr_3125, this._SafeStr_3136);
            };
        }

        private function onAcceptQuest(_arg_1:WindowEvent, _arg_2:IWindow):void
        {
            if (_arg_1.type == "WME_CLICK")
            {
                if (this._questEngine.currentlyInRoom)
                {
                    this._questEngine.send(new AcceptQuestMessageComposer(this._SafeStr_3125.id));
                }
                else
                {
                    this._questEngine.send(new ActivateQuestMessageComposer(this._SafeStr_3125.id));
                };
                this._window.visible = false;
                this._questEngine.questController.seasonalCalendarWindow.close();
                if (((this._SafeStr_3136) && (this._questEngine.isSeasonalQuest(this._SafeStr_3125))))
                {
                    this._questEngine.goToQuestRooms();
                };
            };
        }

        private function onCancelQuest(_arg_1:WindowEvent, _arg_2:IWindow):void
        {
            if (_arg_1.type == "WME_CLICK")
            {
                this._questEngine.send(new _SafeStr_51());
            };
        }


    }
}//package com.sulake.habbo.quest
