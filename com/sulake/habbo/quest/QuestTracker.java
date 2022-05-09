//com.sulake.habbo.quest.QuestTracker

package com.sulake.habbo.quest
{
    import com.sulake.core.runtime.IDisposable;
    import flash.geom.Point;
    import com.sulake.habbo.communication.messages.incoming.quest.QuestMessageData;
    import com.sulake.core.window.IWindowContainer;
    import flash.utils.Timer;
    import com.sulake.core.window.IWindow;
    import com.sulake.core.window.events.WindowEvent;
    import com.sulake.habbo.communication.messages.outgoing.quest._SafeStr_27;
    import com.sulake.habbo.communication.messages.outgoing.quest.StartCampaignMessageComposer;
    import flash.events.TimerEvent;
    import com.sulake.core.window.components.IDesktopWindow;
    import com.sulake.core.window.components.IFrameWindow;

    public class QuestTracker implements IDisposable 
    {

        private static const TRACKER_ANIMATION_STATUS_NONE:int = 0;
        private static const TRACKER_ANIMATION_STATUS_SLIDE_IN:int = 1;
        private static const TRACKER_ANIMATION_STATUS_SLIDE_OUT:int = 2;
        private static const TRACKER_ANIMATION_STATUS_COMPLETED_ANIMATION:int = 3;
        private static const TRACKER_ANIMATION_STATUS_PROGRESS_NUDGE:int = 4;
        private static const TRACKER_ANIMATION_STATUS_CLOSE_WAIT:int = 5;
        private static const TRACKER_ANIMATION_STATUS_PROMPT_ANIMATION:int = 6;
        private static const NUDGE_OFFSETS:Array = [-2, -3, -2, 0, 2, 3, 2, 0, 2, 1, 0, 1];
        private static const _SafeStr_3138:Array = [1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 4];
        private static const MAX_SUCCESS_FRAME:int = 6;
        private static const PROMPT_SEQUENCE_REPEATS:int = 4;
        private static const PROMPT_SEQUENCE_REPEATS_QUEST_OPEN:int = 2;
        private static const PROMPT_FRAME_LENGTH_IN_MSECS:int = 200;
        private static const PROMPT_FRAMES:Array = ["a", "b", "c", "d"];
        private static const PROMPT_DELAY_IN_MSECS:int = 10000;
        private static const PROMPT_DELAY_ON_QUEST_OPEN_IN_MSECS:int = 0;
        private static const _SafeStr_3139:int = -1;
        private static const PROGRESS_BAR_LOC:Point = new Point(10, 87);
        private static const PROGRESS_BAR_WIDTH:int = 162;
        private static const TRACKER_SLIDE_IN_SPEED:Number = 0.01;
        private static const TRACKER_SLIDE_OUT_SPEED:Number = 100;
        private static const COMPLETION_CLOSE_DELAY_IN_MSECS:int = 1000;
        private static const TOOLBAR_EXTENSION_ID:String = "quest_tracker";
        private static const _SafeStr_3140:int = 10;

        private var _questEngine:HabboQuestEngine;
        private var _SafeStr_3141:QuestMessageData;
        private var _window:IWindowContainer;
        private var _SafeStr_3142:Timer;
        private var _SafeStr_3143:ProgressBar;
        private var _trackerAnimationStatus:int = 0;
        private var _SafeStr_3144:int = 0;
        private var _SafeStr_3145:int = 0;
        private var _successFrame:int = -1;
        private var _msecsUntilPrompt:int = -1;
        private var _promptFrame:int = -1;
        private var _msecsUntilNextPromptFrame:int;
        private var _SafeStr_3146:int;
        private var _SafeStr_3147:Boolean;
        private var _SafeStr_3148:Boolean;
        private var _SafeStr_3149:Boolean = false;

        public function QuestTracker(_arg_1:HabboQuestEngine)
        {
            this._questEngine = _arg_1;
        }

        public function dispose():void
        {
            if (this._questEngine)
            {
                this._questEngine.toolbar.extensionView.detachExtension("quest_tracker");
            };
            this._questEngine = null;
            this._SafeStr_3141 = null;
            if (this._window)
            {
                this._window.dispose();
                this._window = null;
            };
            if (this._SafeStr_3142)
            {
                this._SafeStr_3142.stop();
                this._SafeStr_3142 = null;
            };
            if (this._SafeStr_3143)
            {
                this._SafeStr_3143.dispose();
                this._SafeStr_3143 = null;
            };
        }

        public function get disposed():Boolean
        {
            return (this._questEngine == null);
        }

        public function onQuestCompleted(_arg_1:QuestMessageData, _arg_2:Boolean):void
        {
            if (this._window)
            {
                this.clearPrompt();
                this._SafeStr_3141 = _arg_1;
                this._SafeStr_3144 = 0;
                this.refreshTrackerDetails();
                this._successFrame = 0;
                this._trackerAnimationStatus = 3;
                this._SafeStr_3148 = (!(_arg_2));
            };
        }

        public function onQuestCancelled():void
        {
            this._SafeStr_3141 = null;
            if (this._window)
            {
                this.clearPrompt();
                this._SafeStr_3143.refresh(0, 100, -1, 0);
                this._trackerAnimationStatus = 2;
            };
        }

        public function onRoomEnter():void
        {
            var _local_1:int;
            var _local_2:* = (this._questEngine.getInteger("new.identity", 0) > 0);
            if (!_local_2)
            {
                return;
            };
            var _local_3:String = this.getDefaultCampaign();
            if ((((this._SafeStr_3142 == null) && (_local_2)) && (!(_local_3 == ""))))
            {
                _local_1 = this._questEngine.getInteger("questing.startQuestDelayInSeconds", 30);
                this._SafeStr_3142 = new Timer((_local_1 * 1000), 1);
                this._SafeStr_3142.addEventListener("timer", this.onStartQuestTimer);
                this._SafeStr_3142.start();
                Logger.log(("Initialized start quest timer with period: " + _local_1));
            };
        }

        public function onRoomExit():void
        {
            if (((!(this._window == null)) && (this._window.visible)))
            {
                this._window.findChildByName("more_info_txt").visible = false;
                this._window.findChildByName("more_info_region").visible = false;
            };
        }

        public function onQuest(_arg_1:QuestMessageData):void
        {
            if (this._SafeStr_3142 != null)
            {
                this._SafeStr_3142.stop();
            };
            var _local_2:Boolean = ((this._window) && (this._window.visible));
            if (_arg_1.waitPeriodSeconds > 0)
            {
                if (_local_2)
                {
                    this.setWindowVisible(false);
                };
                return;
            };
            this._SafeStr_3141 = _arg_1;
            this.prepareTrackerWindow();
            this.refreshTrackerDetails();
            this.refreshPromptFrames();
            this.setWindowVisible(true);
            this.hideSuccessFrames();
            if (_local_2)
            {
                if (this._trackerAnimationStatus == 2)
                {
                    this._trackerAnimationStatus = 1;
                };
                this.setupPrompt(this._msecsUntilPrompt, 4, false);
            }
            else
            {
                this._window.x = this.getOutScreenLocationX();
                this._trackerAnimationStatus = 1;
                this.setupPrompt(0, 2, false);
            };
        }

        private function refreshPromptFrames():void
        {
            var _local_1:int;
            if (!this._questEngine.isQuestWithPrompts(this._SafeStr_3141))
            {
                return;
            };
            _local_1 = 0;
            while (_local_1 < PROMPT_FRAMES.length)
            {
                this._questEngine.setupPromptFrameImage(this._window, this._SafeStr_3141, PROMPT_FRAMES[_local_1]);
                _local_1++;
            };
        }

        private function prepareTrackerWindow():void
        {
            if (this._window != null)
            {
                return;
            };
            this._window = IWindowContainer(this._questEngine.getXmlWindow("QuestTracker"));
            this._window.findChildByName("more_info_region").procedure = this.onMoreInfo;
            this.hideSuccessFrames();
            this._SafeStr_3143 = new ProgressBar(this._questEngine, IWindowContainer(this._window.findChildByName("content_cont")), 162, "quests.tracker.progress", false, PROGRESS_BAR_LOC);
        }

        private function hideSuccessFrames():void
        {
            var _local_1:int;
            _local_1 = 1;
            while (_local_1 <= 6)
            {
                this.getSuccessFrame(_local_1).visible = false;
                _local_1++;
            };
        }

        private function hidePromptFrames():void
        {
            var _local_1:int;
            _local_1 = 0;
            while (_local_1 < PROMPT_FRAMES.length)
            {
                this.getPromptFrame(PROMPT_FRAMES[_local_1]).visible = false;
                _local_1++;
            };
        }

        private function getSuccessFrame(_arg_1:int):IWindow
        {
            return (this._window.findChildByName(("success_pic_" + _arg_1)));
        }

        private function getPromptFrame(_arg_1:String):IWindow
        {
            return (this._window.findChildByName(("prompt_pic_" + _arg_1)));
        }

        private function refreshTrackerDetails():void
        {
            this._questEngine.localization.registerParameter("quests.tracker.caption", "quest_name", this._questEngine.getQuestName(this._SafeStr_3141));
            this._window.findChildByName("desc_txt").caption = this._questEngine.getQuestDesc(this._SafeStr_3141);
            this._window.findChildByName("more_info_txt").visible = this._questEngine.currentlyInRoom;
            this._window.findChildByName("more_info_region").visible = this._questEngine.currentlyInRoom;
            var _local_1:int = int(int(Math.ceil(((100 * this._SafeStr_3141.completedSteps) / this._SafeStr_3141.totalSteps))));
            this._SafeStr_3143.refresh(_local_1, 100, this._SafeStr_3141.id, 0);
            this._questEngine.setupQuestImage(this._window, this._SafeStr_3141);
        }

        private function onMoreInfo(_arg_1:WindowEvent, _arg_2:IWindow):void
        {
            if (_arg_1.type == "WME_CLICK")
            {
                this._questEngine.questController.questDetails.showDetails(this._SafeStr_3141);
            };
        }

        public function forceWindowCloseAfterAnimationsFinished():void
        {
            if (this._trackerAnimationStatus == 0)
            {
                this.setWindowVisible(false);
                this._SafeStr_3149 = false;
            }
            else
            {
                this._SafeStr_3149 = true;
            };
        }

        public function update(_arg_1:uint):void
        {
            var _local_2:int;
            var _local_3:int;
            var _local_4:int;
            if (this._window == null)
            {
                return;
            };
            this._SafeStr_3143.updateView();
            switch (this._trackerAnimationStatus)
            {
                case 1:
                    _local_2 = this.getDefaultLocationX();
                    _local_3 = (this._window.x - _local_2);
                    if (_local_3 > 0)
                    {
                        _local_4 = Math.max(1, Math.round(((_local_3 * _arg_1) * 0.01)));
                        this._window.x = (this._window.x - _local_4);
                    }
                    else
                    {
                        this._trackerAnimationStatus = 0;
                        this._window.x = _local_2;
                    };
                    return;
                case 2:
                    _local_2 = this.getOutScreenLocationX();
                    _local_3 = (this._window.width - this._window.x);
                    if (_local_3 > 0)
                    {
                        _local_4 = int(int(Math.max(1, Math.round(((_arg_1 * 100) / _local_3)))));
                        this._window.x = (this._window.x + _local_4);
                    }
                    else
                    {
                        this._trackerAnimationStatus = 0;
                        this._window.x = _local_2;
                        this.setWindowVisible(false);
                    };
                    return;
                case 3:
                    this.hideSuccessFrames();
                    this.getSuccessFrame(_SafeStr_3138[this._successFrame]).visible = true;
                    this._successFrame++;
                    if (this._successFrame >= _SafeStr_3138.length)
                    {
                        this._trackerAnimationStatus = 5;
                        this._SafeStr_3145 = 1000;
                    };
                    return;
                case 6:
                    this.setQuestImageVisible(false);
                    this.hidePromptFrames();
                    this._msecsUntilNextPromptFrame = (this._msecsUntilNextPromptFrame - _arg_1);
                    this.getPromptFrame(PROMPT_FRAMES[this._promptFrame]).visible = true;
                    if (this._msecsUntilNextPromptFrame < 0)
                    {
                        this._msecsUntilNextPromptFrame = 200;
                        this._promptFrame++;
                        if (this._promptFrame >= PROMPT_FRAMES.length)
                        {
                            this._promptFrame = 0;
                            this._SafeStr_3146--;
                            if (this._SafeStr_3146 < 1)
                            {
                                this.setupPrompt(10000, 2, true);
                                this._trackerAnimationStatus = 0;
                            };
                        };
                    };
                    return;
                case 4:
                    if (this._SafeStr_3144 >= (NUDGE_OFFSETS.length - 1))
                    {
                        this._window.x = this.getDefaultLocationX();
                        this._trackerAnimationStatus = 0;
                        this.setupPrompt(10000, 2, false);
                    }
                    else
                    {
                        this._window.x = (this.getDefaultLocationX() + NUDGE_OFFSETS[this._SafeStr_3144]);
                        this._SafeStr_3144++;
                    };
                    return;
                case 5:
                    this._SafeStr_3145 = (this._SafeStr_3145 - _arg_1);
                    if (this._SafeStr_3145 < 0)
                    {
                        this._trackerAnimationStatus = 0;
                        if (((this._SafeStr_3148) && (!(this._SafeStr_3149))))
                        {
                            this._questEngine.send(new _SafeStr_27());
                        }
                        else
                        {
                            this.setWindowVisible(false);
                            this._SafeStr_3149 = false;
                        };
                    };
                    return;
                case 0:
                    if (this._msecsUntilPrompt != -1)
                    {
                        this._msecsUntilPrompt = (this._msecsUntilPrompt - _arg_1);
                        if (this._msecsUntilPrompt < 0)
                        {
                            this._msecsUntilPrompt = -1;
                            if (((!(this._SafeStr_3141 == null)) && (this._questEngine.isQuestWithPrompts(this._SafeStr_3141))))
                            {
                                if (this._SafeStr_3147)
                                {
                                    this.startNudge();
                                }
                                else
                                {
                                    this._trackerAnimationStatus = 6;
                                    this._promptFrame = 0;
                                    this._msecsUntilNextPromptFrame = 200;
                                };
                            };
                        };
                    };
            };
        }

        private function getDefaultLocationX():int
        {
            return (0);
        }

        private function getOutScreenLocationX():int
        {
            return (this._window.width + 10);
        }

        public function isVisible():Boolean
        {
            return ((this._window) && (this._window.visible));
        }

        private function onStartQuestTimer(_arg_1:TimerEvent):void
        {
            if (this.hasBlockingWindow())
            {
                Logger.log("Quest start blocked. Waiting some more");
                this._SafeStr_3142.reset();
                this._SafeStr_3142.start();
            }
            else
            {
                this._questEngine.questController.questDetails.openForNextQuest = this._questEngine.getBoolean("questing.showDetailsForNextQuest");
                this._questEngine.send(new StartCampaignMessageComposer(this.getDefaultCampaign()));
            };
        }

        private function getDefaultCampaign():String
        {
            var _local_1:String = this._questEngine.getProperty("questing.defaultCampaign");
            return ((_local_1 == null) ? "" : _local_1);
        }

        private function hasBlockingWindow():Boolean
        {
            var _local_1:int;
            var _local_2:IDesktopWindow;
            _local_1 = 0;
            while (_local_1 <= 2)
            {
                _local_2 = this._questEngine.windowManager.getDesktop(_local_1);
                if (((!(_local_2 == null)) && (this.hasBlockingWindowInLayer(_local_2))))
                {
                    return (true);
                };
                _local_1++;
            };
            return (false);
        }

        private function hasBlockingWindowInLayer(_arg_1:IWindowContainer):Boolean
        {
            var _local_2:int;
            var _local_3:IWindow;
            while (_local_2 < _arg_1.numChildren)
            {
                _local_3 = _arg_1.getChildAt(_local_2);
                if (((!(_local_3 == null)) && (_local_3.visible)))
                {
                    if ((_local_3 as IFrameWindow) != null)
                    {
                        if (((!(_local_3.name == "mod_start_panel")) && (!(_local_3.name == "_frame"))))
                        {
                            return (true);
                        };
                    }
                    else
                    {
                        if (_local_3.name == "welcome_screen")
                        {
                            return (true);
                        };
                    };
                };
                _local_2++;
            };
            return (false);
        }

        private function setQuestImageVisible(_arg_1:Boolean):void
        {
            this._window.findChildByName("quest_pic_bitmap").visible = _arg_1;
        }

        private function clearPrompt():void
        {
            this.setupPrompt(-1, 0, false);
        }

        private function setupPrompt(_arg_1:int, _arg_2:int, _arg_3:Boolean):void
        {
            this.setQuestImageVisible(true);
            this.hidePromptFrames();
            this._msecsUntilPrompt = _arg_1;
            this._SafeStr_3146 = _arg_2;
            this._SafeStr_3147 = _arg_3;
        }

        private function startNudge():void
        {
            this._SafeStr_3144 = 0;
            this._trackerAnimationStatus = 4;
        }

        private function setWindowVisible(_arg_1:Boolean):void
        {
            this._window.visible = _arg_1;
            if (!_arg_1)
            {
                this._questEngine.toolbar.extensionView.detachExtension("quest_tracker");
            }
            else
            {
                this._questEngine.toolbar.extensionView.attachExtension("quest_tracker", this._window);
            };
        }


    }
}//package com.sulake.habbo.quest
