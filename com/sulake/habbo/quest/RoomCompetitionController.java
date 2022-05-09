//com.sulake.habbo.quest.RoomCompetitionController

package com.sulake.habbo.quest
{
    import com.sulake.core.runtime.IDisposable;
    import com.sulake.habbo.room.IGetImageListener;
    import com.sulake.core.window.IWindowContainer;
    import flash.utils.Timer;
    import com.sulake.core.utils.Map;
    import com.sulake.core.window.IWindow;
    import com.sulake.habbo.communication.messages.parser.competition.CompetitionVotingInfoMessageEvent;
    import com.sulake.habbo.communication.messages.parser.competition.CompetitionEntrySubmitResultMessageEvent;
    import flash.geom.Rectangle;
    import com.sulake.habbo.room._SafeStr_147;
    import com.sulake.room.utils.Vector3d;
    import flash.display.BitmapData;
    import com.sulake.core.window.components.IBitmapWrapperWindow;
    import flash.geom.Point;
    import com.sulake.habbo.communication.messages.parser.room.engine.RoomEntryInfoMessageParser;
    import com.sulake.habbo.communication.messages.outgoing.competition._SafeStr_49;
    import com.sulake.habbo.communication.messages.incoming.room.engine.RoomEntryInfoMessageEvent;
    import com.sulake.habbo.communication.messages.outgoing.competition.SubmitRoomToCompetitionMessageComposer;
    import com.sulake.core.window.events.WindowEvent;
    import com.sulake.habbo.toolbar.events.HabboToolbarEvent;
    import com.sulake.habbo.communication.messages.outgoing.talent.GetTalentTrackMessageComposer;
    import com.sulake.habbo.communication.messages.outgoing.competition.VoteForRoomMessageComposer;
    import com.sulake.core.window.components.ITextWindow;
    import com.sulake.core.window.components.IItemGridWindow;
    import flash.events.TimerEvent;

    public class RoomCompetitionController implements IDisposable, IGetImageListener 
    {

        private static const INDENT_LEFT:int = 270;
        private static const INDENT_RIGHT:int = 200;
        private static const INDENT_TOP:int = 4;

        private var _window:IWindowContainer;
        private var _questEngine:HabboQuestEngine;
        private var _SafeStr_2343:String;
        private var _SafeStr_3150:int;
        private var _SafeStr_3151:int;
        private var _submit:Boolean;
        private var _dontShowAgain:Boolean;
        private var _hideTimer:Timer = new Timer(3000, 1);
        private var _SafeStr_834:int;
        private var _SafeStr_3152:Map = new Map();

        public function RoomCompetitionController(_arg_1:HabboQuestEngine)
        {
            this._questEngine = _arg_1;
            this._hideTimer.addEventListener("timer", this.onHideTimer);
        }

        public function dispose():void
        {
            this._questEngine = null;
            if (this._window)
            {
                this._window.dispose();
                this._window = null;
            };
            if (this._hideTimer)
            {
                this._hideTimer.removeEventListener("timer", this.onHideTimer);
                this._hideTimer.reset();
                this._hideTimer = null;
            };
            if (this._SafeStr_3152)
            {
                this._SafeStr_3152.dispose();
                this._SafeStr_3152 = null;
            };
        }

        public function get disposed():Boolean
        {
            return (this._window == null);
        }

        private function setText(_arg_1:IWindow, _arg_2:String, _arg_3:String):void
        {
            var _local_4:String = ((_arg_2 + ".") + _arg_3);
            var _local_5:String = this._questEngine.localization.getLocalization(_local_4, "");
            if (_local_5 == "")
            {
                _local_4 = _arg_2;
                _local_5 = this._questEngine.localization.getLocalization(_local_4, "");
            };
            if (_local_5 == "")
            {
                _arg_1.visible = false;
            }
            else
            {
                _arg_1.visible = true;
                this._questEngine.localization.registerParameter(_local_4, "competition_name", this.getCompetitionName());
                this._questEngine.localization.registerParameter(_local_4, "votes", ("" + this._SafeStr_3151));
                _arg_1.caption = (("${" + _local_4) + "}");
            };
        }

        public function onCompetitionVotingInfo(_arg_1:CompetitionVotingInfoMessageEvent):void
        {
            this._SafeStr_3151 = _arg_1.getParser().votesRemaining;
            var _local_2:Boolean = _arg_1.getParser().isVotingAllowedForUser;
            var _local_3:int = _arg_1.getParser().resultCode;
            this.refreshContent(_arg_1.getParser().goalId, false, _arg_1.getParser().goalCode, _local_3.toString());
            this.setInfoRegionProc(((_local_3 == 1) ? this.onTalents : this.onSeeParticipants));
            this.getActionButton().procedure = this.onVote;
            this.getActionButton().visible = ((this._SafeStr_3151 > 0) && (_local_2));
            this.getButtonInfoText().visible = _local_2;
        }

        public function onCompetitionEntrySubmitResult(_arg_1:CompetitionEntrySubmitResultMessageEvent):void
        {
            if (_arg_1.getParser().result == 5)
            {
                return;
            };
            this.refreshContent(_arg_1.getParser().goalId, true, _arg_1.getParser().goalCode, ("" + _arg_1.getParser().result));
            this._SafeStr_834 = _arg_1.getParser().result;
            if (this._SafeStr_834 == 2)
            {
                this.setInfoRegionProc(null);
                this.getActionButton().procedure = this.onConfirm;
            }
            else
            {
                if (this._SafeStr_834 == 6)
                {
                    this.setInfoRegionProc(this.onGoToHotelView);
                    this.getActionButton().procedure = this.onAccept;
                }
                else
                {
                    if (this._SafeStr_834 == 1)
                    {
                        this.setInfoRegionProc(this.onGoToHotelView);
                        this.getActionButton().procedure = this.onSubmit;
                    }
                    else
                    {
                        if (this._SafeStr_834 == 3)
                        {
                            this.setInfoRegionProc(this.onCatalogLink);
                            this.getActionButton().visible = false;
                            this.refreshRequiredFurnis(_arg_1);
                            this.getRequiredFurnisWindow().visible = true;
                        }
                        else
                        {
                            if (this._SafeStr_834 == 0)
                            {
                                this.setInfoRegionProc(this.onGoToHotelView);
                                this.getActionButton().procedure = this.onClose;
                            }
                            else
                            {
                                if (this._SafeStr_834 == 4)
                                {
                                    this.setInfoRegionProc(null);
                                    this.getActionButton().procedure = null;
                                    this.getActionButton().visible = false;
                                }
                                else
                                {
                                    if (this._SafeStr_834 == 5)
                                    {
                                        this.setInfoRegionProc(null);
                                        this.getActionButton().procedure = this.onOpenNavigator;
                                        this.getActionButton().visible = true;
                                    };
                                };
                            };
                        };
                    };
                };
            };
        }

        private function setInfoRegionProc(_arg_1:Function):void
        {
            this.getInfoRegion().procedure = _arg_1;
            this.getInfoRegion().setParamFlag(1, (!(_arg_1 == null)));
        }

        public function refreshContent(_arg_1:int, _arg_2:Boolean, _arg_3:String, _arg_4:String):void
        {
            this._SafeStr_3150 = _arg_1;
            this._SafeStr_2343 = _arg_3;
            this._submit = _arg_2;
            this.prepare();
            this.setTexts(((_arg_2) ? "submit" : "vote"), _arg_4);
            this.getActionButton().visible = true;
            this.setPromoImage();
            this.showAndPositionWindow();
            this.getRequiredFurnisWindow().visible = false;
            this._window.findChildByName("dont_show_again_container").visible = false;
            this._window.findChildByName("normal_container").visible = true;
        }

        private function setPromoImage():void
        {
            this.getVoteImage().visible = (!(this._submit));
            this.getSubmitImage().visible = this._submit;
        }

        private function showAndPositionWindow():void
        {
            this._window.visible = true;
            var _local_1:Rectangle = this._window.desktop.rectangle;
            this._window.x = 270;
            this._window.y = 4;
            this._window.width = ((_local_1.width - 270) - 200);
            this._window.activate();
        }

        private function refreshRequiredFurnis(_arg_1:CompetitionEntrySubmitResultMessageEvent):void
        {
            var _local_2:int;
            var _local_3:String;
            var _local_4:Array;
            var _local_5:String;
            var _local_6:String;
            var _local_7:IWindowContainer;
            var _local_8:_SafeStr_147;
            var _local_9:Array = _arg_1.getParser().requiredFurnis;
            _local_2 = 0;
            while (_local_2 < _local_9.length)
            {
                _local_3 = _local_9[_local_2];
                _local_4 = _local_3.split("*");
                _local_5 = _local_4[0];
                _local_6 = ((_local_4.length > 1) ? _local_4[1] : "");
                _local_7 = this.getRequiredFurniWindow((_local_2 + 1));
                if (_local_3 == null)
                {
                    _local_7.visible = false;
                }
                else
                {
                    _local_7.visible = true;
                    _local_7.findChildByName("tick_icon").visible = (!(_arg_1.getParser().isMissing(_local_3)));
                    _local_8 = this._questEngine.roomEngine.getGenericRoomObjectImage(_local_5, _local_6, new Vector3d(180, 0, 0), 1, this);
                    if (_local_8.id != 0)
                    {
                        this._SafeStr_3152.add(_local_8.id, _local_2);
                    };
                    this.setRequiredFurniImage(_local_2, _local_8.data);
                };
                _local_2++;
            };
        }

        public function imageReady(_arg_1:int, _arg_2:BitmapData):void
        {
            if (this._SafeStr_3152.getValue(_arg_1) != null)
            {
                this.setRequiredFurniImage(this._SafeStr_3152.getValue(_arg_1), _arg_2);
                this._SafeStr_3152.remove(_arg_1);
            };
        }

        public function imageFailed(_arg_1:int):void
        {
        }

        private function setRequiredFurniImage(_arg_1:int, _arg_2:BitmapData):void
        {
            var _local_3:IWindowContainer = this.getRequiredFurniWindow((_arg_1 + 1));
            var _local_4:IBitmapWrapperWindow = IBitmapWrapperWindow(_local_3.findChildByName("furni_icon"));
            var _local_5:BitmapData = new BitmapData(_local_4.width, _local_4.height, true, 0);
            if (_arg_2 != null)
            {
                _local_5.copyPixels(_arg_2, _arg_2.rect, new Point(((_local_5.width - _arg_2.width) / 2), ((_local_5.height - _arg_2.height) / 2)));
                _local_4.bitmap = _local_5;
            };
        }

        private function getCompetitionName():String
        {
            var _local_1:* = (("roomcompetition." + this._SafeStr_2343) + ".name");
            return (this._questEngine.localization.getLocalization(_local_1, _local_1));
        }

        private function setTexts(_arg_1:String, _arg_2:String):void
        {
            this.setText(this.getCaption(), ("roomcompetition.caption." + _arg_1), _arg_2);
            this.setText(this.getInfoText(), ("roomcompetition.info." + _arg_1), _arg_2);
            this.setText(this.getActionButton(), ("roomcompetition.button." + _arg_1), _arg_2);
            this.setText(this.getButtonInfoText(), ("roomcompetition.buttoninfo." + _arg_1), _arg_2);
            this.onResize();
        }

        private function onResize():void
        {
            this.getInfoRegion().y = ((this.getCaption().y + this.getCaption().textHeight) + 5);
        }

        public function onRoomExit():void
        {
            this.close();
        }

        public function onRoomEnter(_arg_1:RoomEntryInfoMessageEvent):void
        {
            this.close();
            var _local_2:RoomEntryInfoMessageParser = _arg_1.getParser();
            var _local_3:Boolean = ((this._questEngine.getInteger("new.identity", 0) == 0) || (!(this._questEngine.getBoolean("new.identity.hide.ui"))));
            if (((!(this._dontShowAgain)) && (_local_3)))
            {
                this._submit = _local_2.owner;
                this._questEngine.send(new _SafeStr_49());
            };
        }

        public function sendRoomCompetitionInit():void
        {
            this._questEngine.send(new _SafeStr_49());
        }

        public function onContextChanged():void
        {
            if ((((!(this._window == null)) && (this._window.visible)) && (this._submit)))
            {
                this._questEngine.send(new SubmitRoomToCompetitionMessageComposer(this._SafeStr_2343, 0));
            };
        }

        private function close():void
        {
            if (this._window)
            {
                this._window.visible = false;
            };
            this._SafeStr_2343 = "";
        }

        private function prepare():void
        {
            var _local_1:int;
            if (this._window == null)
            {
                _local_1 = 1;
                this._window = IWindowContainer(this._questEngine.getXmlWindow("RoomCompetition", _local_1));
                this._window.findChildByName("close_region").procedure = this.onClose;
                this._questEngine.windowManager.getWindowContext(_local_1).getDesktopWindow().addEventListener("WE_RESIZED", this.onDesktopResized);
                this._window.findChildByName("dont_show_again_region").procedure = this.onDontShowAgain;
            };
        }

        private function onCatalogLink(_arg_1:WindowEvent, _arg_2:IWindow=null):void
        {
            if (_arg_1.type == "WME_CLICK")
            {
                this._questEngine.catalog.openCatalogPage(this._questEngine.getProperty((("competition." + this._SafeStr_2343) + ".catalogPage")));
            };
        }

        private function onOpenNavigator(_arg_1:WindowEvent, _arg_2:IWindow=null):void
        {
            if (_arg_1.type == "WME_CLICK")
            {
                this._questEngine.navigator.open();
            };
        }

        private function onGoToHotelView(_arg_1:WindowEvent, _arg_2:IWindow=null):void
        {
            var _local_3:HabboToolbarEvent;
            if (_arg_1.type == "WME_CLICK")
            {
                _local_3 = new HabboToolbarEvent("HTE_TOOLBAR_CLICK");
                _local_3.iconId = "HTIE_ICON_RECEPTION";
                this._questEngine.toolbar.events.dispatchEvent(_local_3);
            };
        }

        private function onSeeParticipants(_arg_1:WindowEvent, _arg_2:IWindow=null):void
        {
        }

        private function onTalents(_arg_1:WindowEvent, _arg_2:IWindow=null):void
        {
            if (_arg_1.type == "WME_CLICK")
            {
                this._questEngine.tracking.trackTalentTrackOpen(this._questEngine.sessionDataManager.currentTalentTrack, "roomcompetition");
                this._questEngine.send(new GetTalentTrackMessageComposer(this._questEngine.sessionDataManager.currentTalentTrack));
            };
        }

        private function onAccept(_arg_1:WindowEvent, _arg_2:IWindow=null):void
        {
            if (_arg_1.type == "WME_CLICK")
            {
                this._questEngine.send(new SubmitRoomToCompetitionMessageComposer(this._SafeStr_2343, 1));
            };
        }

        private function onSubmit(_arg_1:WindowEvent, _arg_2:IWindow=null):void
        {
            if (_arg_1.type == "WME_CLICK")
            {
                this._questEngine.send(new SubmitRoomToCompetitionMessageComposer(this._SafeStr_2343, 2));
            };
        }

        private function onConfirm(_arg_1:WindowEvent, _arg_2:IWindow=null):void
        {
            if (_arg_1.type == "WME_CLICK")
            {
                this._questEngine.send(new SubmitRoomToCompetitionMessageComposer(this._SafeStr_2343, 3));
            };
        }

        private function onVote(_arg_1:WindowEvent, _arg_2:IWindow=null):void
        {
            if (_arg_1.type == "WME_CLICK")
            {
                this._questEngine.send(new VoteForRoomMessageComposer(this._SafeStr_2343));
            };
        }

        private function onClose(_arg_1:WindowEvent, _arg_2:IWindow=null):void
        {
            var _local_3:String;
            if (_arg_1.type == "WME_CLICK")
            {
                if (((this._submit) && (this._SafeStr_834 == 0)))
                {
                    this.close();
                    return;
                };
                _local_3 = ("roomcompetition.dontshowagain.info." + ((this._submit) ? "submit" : "vote"));
                this._window.findChildByName("dont_show_info_txt").caption = this._questEngine.localization.getLocalization(_local_3, _local_3);
                this._window.findChildByName("dont_show_again_container").visible = true;
                this._window.findChildByName("normal_container").visible = false;
                this._hideTimer.reset();
                this._hideTimer.start();
            };
        }

        private function onDontShowAgain(_arg_1:WindowEvent, _arg_2:IWindow=null):void
        {
            if (_arg_1.type == "WME_CLICK")
            {
                this._dontShowAgain = true;
                this.close();
            };
        }

        private function getInfoRegion():IWindow
        {
            return (this._window.findChildByName("info_region"));
        }

        private function getInfoText():IWindow
        {
            return (this._window.findChildByName("info_txt"));
        }

        private function getButtonInfoText():IWindow
        {
            return (this._window.findChildByName("button_info_txt"));
        }

        private function getActionButton():IWindow
        {
            return (this._window.findChildByName("action_button"));
        }

        private function getCaption():ITextWindow
        {
            return (ITextWindow(this._window.findChildByName("caption_txt")));
        }

        private function getRequiredFurnisWindow():IWindow
        {
            return (this._window.findChildByName("required_furnis_itemgrid"));
        }

        private function getVoteImage():IWindow
        {
            return (this._window.findChildByName("vote_image"));
        }

        private function getSubmitImage():IWindow
        {
            return (this._window.findChildByName("submit_image"));
        }

        private function getRequiredFurniWindow(_arg_1:int):IWindowContainer
        {
            var _local_2:int;
            var _local_3:IItemGridWindow = IItemGridWindow(this._window.findChildByName("required_furnis_itemgrid"));
            var _local_4:IWindowContainer = IWindowContainer(_local_3.getGridItemAt(0));
            if (_local_3.numGridItems < _arg_1)
            {
                _local_2 = 0;
                while (_local_2 < (_arg_1 - _local_3.numGridItems))
                {
                    _local_3.addGridItem(_local_4.clone());
                    _local_2++;
                };
            };
            return (IWindowContainer(_local_3.getGridItemAt((_arg_1 - 1))));
        }

        private function onDesktopResized(_arg_1:WindowEvent):void
        {
            if (((!(this._window == null)) && (this._window.visible)))
            {
                this.onResize();
            };
        }

        public function set dontShowAgain(_arg_1:Boolean):void
        {
            this._dontShowAgain = _arg_1;
        }

        private function onHideTimer(_arg_1:TimerEvent):void
        {
            this.close();
        }


    }
}//package com.sulake.habbo.quest
