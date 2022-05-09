
//------------------------------------------------------------
//com.sulake.habbo.quest.seasonalcalendar.Calendar

package com.sulake.habbo.quest.seasonalcalendar
{
    import com.sulake.core.runtime.IDisposable;
    import com.sulake.core.runtime.IUpdateReceiver;
    import com.sulake.habbo.quest.HabboQuestEngine;
    import __AS3__.vec.Vector;
    import flash.display.BitmapData;
    import com.sulake.core.utils.Map;
    import com.sulake.core.window.IWindowContainer;
    import com.sulake.core.window.components.IBitmapWrapperWindow;
    import flash.utils.Timer;
    import com.sulake.habbo.communication.messages.incoming.quest.QuestMessageData;
    import com.sulake.core.assets.IAsset;
    import flash.events.TimerEvent;
    import com.sulake.core.window.components.IFrameWindow;
    import com.sulake.core.window.IWindow;
    import com.sulake.core.window.components._SafeStr_124;
    import com.sulake.core.window.components.ITextWindow;
    import flash.net.URLRequest;
    import com.sulake.core.assets.AssetLoaderStruct;
    import com.sulake.core.assets.loaders.AssetLoaderEvent;
    import com.sulake.core.window.events.WindowEvent;
    import __AS3__.vec.*;

    public class Calendar implements IDisposable, IUpdateReceiver 
    {

        private static const BG_IMAGE_PREFIX:String = "background_";
        private static const ENTITY_IMAGE_PREFIX:String = "day";
        private static const ENTITY_IMAGE_UNCOMPLETE_POSTFIX:String = "_uncomplete";
        private static const ENTITY_IMAGE_COMPLETED_POSTFIX:String = "_completed";
        private static const SHOW_FUTURE_INACTIVE_ENTITIES_COUNT:int = 2;
        private static const _SafeStr_3058:int = 3;
        private static const ENTITY_SPACING:int = 80;
        private static const ENTITIES_LEFT_MARGIN:int = 37;
        private static const _SafeStr_3059:int = 7;
        private static const DAILY_REFRESH_DELAY_MINUTES:int = 5;
        private static const FLASH_PULSE_LENGHT_IN_MS:int = 2000;
        private static const FLASH_MAX_BRIGHTNESS:int = 100;

        private var _questEngine:HabboQuestEngine;
        private var _SafeStr_2554:MainWindow;
        private var _SafeStr_3060:Array;
        private var _backgroundImageCache:Vector.<BitmapData>;
        private var _graphicEntityCache:Vector.<BitmapData>;
        private var _SafeStr_3061:Map;
        private var _bgAssetNameArray:Array;
        private var _SafeStr_3062:String;
        private var _SafeStr_3063:CalendarBackgroundRenderer;
        private var _entityWindows:Vector.<IWindowContainer>;
        private var _states:Array;
        private var _SafeStr_3064:CalendarArrowButton;
        private var _SafeStr_3065:CalendarArrowButton;
        private var _SafeStr_3066:IWindowContainer;
        private var _SafeStr_3067:IWindowContainer;
        private var _SafeStr_3068:IBitmapWrapperWindow;
        private var _SafeStr_3069:int = -1;
        private var _SafeStr_3070:int = -1;
        private var _highestAvailableQuestIndex:int = -1;
        private var _SafeStr_3071:int = 42;
        private var _SafeStr_3072:Timer;
        private var _scrollOffset:int = 0;
        private var _SafeStr_3073:int = 0;
        private var _scrollBgStartOffset:int = 0;
        private var _SafeStr_3074:int = -1;
        private var _SafeStr_3075:int;
        private var _SafeStr_3076:int = -1;
        private var _SafeStr_3077:Boolean = false;
        private var _SafeStr_3078:Boolean = false;
        private var _SafeStr_3079:Timer;
        private var _SafeStr_3080:int = -1;

        public function Calendar(_arg_1:HabboQuestEngine, _arg_2:MainWindow)
        {
            this._questEngine = _arg_1;
            this._SafeStr_2554 = _arg_2;
        }

        private static function adjustBrightness(_arg_1:uint, _arg_2:int):uint
        {
            var _local_3:int = Math.min(0xFF, Math.max(0, (((_arg_1 >> 16) & 0xFF) + _arg_2)));
            var _local_4:int = Math.min(0xFF, Math.max(0, (((_arg_1 >> 8) & 0xFF) + _arg_2)));
            var _local_5:int = Math.min(0xFF, Math.max(0, ((_arg_1 & 0xFF) + _arg_2)));
            return ((((_local_3 & 0xFF) << 16) + ((_local_4 & 0xFF) << 8)) + (_local_5 & 0xFF));
        }


        private function getImageGalleryHost():String
        {
            return (this._SafeStr_3062);
        }

        public function dispose():void
        {
            if (!this.disposed)
            {
                this._questEngine.removeUpdateReceiver(this);
                this.cleanUpEntityWindows();
                if (this._SafeStr_3063 != null)
                {
                    this._SafeStr_3063.dispose();
                    this._SafeStr_3063 = null;
                };
                if (this._SafeStr_3064 != null)
                {
                    this._SafeStr_3064.dispose();
                    this._SafeStr_3064 = null;
                };
                if (this._SafeStr_3065 != null)
                {
                    this._SafeStr_3065.dispose();
                    this._SafeStr_3065 = null;
                };
                if (this._SafeStr_3072 != null)
                {
                    this._SafeStr_3072.stop();
                    this._SafeStr_3072 = null;
                };
                if (this._SafeStr_3079 != null)
                {
                    this._SafeStr_3079.stop();
                    this._SafeStr_3079 = null;
                };
                this._backgroundImageCache = null;
                this._graphicEntityCache = null;
                this._states = null;
                this._SafeStr_3061 = null;
                this._bgAssetNameArray = null;
                this._questEngine = null;
            };
        }

        public function get disposed():Boolean
        {
            return (this._questEngine == null);
        }

        public function onQuests(_arg_1:Array):void
        {
            var _local_4:QuestMessageData;
            var _local_2:Date = new Date();
            this._SafeStr_3080 = _local_2.getDate();
            var _local_3:int = this._SafeStr_3070;
            this._SafeStr_3060 = [];
            this._highestAvailableQuestIndex = 0;
            for each (_local_4 in _arg_1)
            {
                if (this._questEngine.isSeasonalQuest(_local_4))
                {
                    this._SafeStr_3060.push(_local_4);
                    if (this._highestAvailableQuestIndex < (_local_4.sortOrder - 1))
                    {
                        this._highestAvailableQuestIndex = (_local_4.sortOrder - 1);
                    };
                };
            };
            this._SafeStr_3060.sortOn(["sortOrder"]);
            this._SafeStr_3071 = int(this._questEngine.configuration.getProperty("seasonalQuestCalendar.maximum.entities"));
            this._SafeStr_3070 = Math.min(this._SafeStr_3071, ((this._highestAvailableQuestIndex + 1) + 2));
            if (((!(_local_3 == -1)) && (this._SafeStr_3070 > _local_3)))
            {
                this.prepareImages();
            };
        }

        public function prepare(_arg_1:IFrameWindow):void
        {
            var _local_2:IBitmapWrapperWindow;
            this._SafeStr_3062 = this._SafeStr_2554.getCalendarImageGalleryHost();
            this._SafeStr_3066 = IWindowContainer(_arg_1.findChildByName("calendar_cont"));
            this._SafeStr_3068 = IBitmapWrapperWindow(_arg_1.findChildByName("background_slice"));
            this._SafeStr_3067 = IWindowContainer(_arg_1.findChildByName("entity_template"));
            this._SafeStr_3067.visible = false;
            this._SafeStr_3063 = new CalendarBackgroundRenderer();
            this._SafeStr_3064 = new CalendarArrowButton(this._questEngine.assets, IBitmapWrapperWindow(_arg_1.findChildByName("button_left")), 0, this.scrollArrowProcedure);
            this._SafeStr_3065 = new CalendarArrowButton(this._questEngine.assets, IBitmapWrapperWindow(_arg_1.findChildByName("button_right")), 1, this.scrollArrowProcedure);
            _local_2 = IBitmapWrapperWindow(_arg_1.findChildByName("stripe_mask_left"));
            _local_2.bitmap = BitmapData(IAsset(this._questEngine.assets.getAssetByName("stripe_mask_L")).content);
            _local_2 = IBitmapWrapperWindow(_arg_1.findChildByName("stripe_mask_right"));
            _local_2.bitmap = BitmapData(IAsset(this._questEngine.assets.getAssetByName("stripe_mask_R")).content);
            if (this._SafeStr_3069 == -1)
            {
                this.goToDay(this._SafeStr_2554.currentDay);
            };
            this.prepareImages();
            var _local_3:Date = new Date();
            this._SafeStr_3080 = _local_3.getDate();
            this._SafeStr_3079 = new Timer((60000 * 5));
            this._SafeStr_3079.addEventListener("timer", this.onDateRefreshTimer);
            this._SafeStr_3079.start();
            this.onDateRefreshTimer(new TimerEvent("timer"));
            this._questEngine.registerUpdateReceiver(this, 1);
            this._SafeStr_3072 = new Timer(10, 10);
        }

        public function close():void
        {
            this.cleanUpEntityWindows();
            if (this._SafeStr_3063 != null)
            {
                this._SafeStr_3063.initializeImageChain(new Vector.<BitmapData>());
            };
        }

        public function refresh():void
        {
            var _local_1:int;
            var _local_2:int;
            var _local_3:QuestMessageData;
            for each (_local_3 in this._SafeStr_3060)
            {
                _local_1 = (_local_3.sortOrder - 1);
                _local_2 = ((_local_3.completedCampaign) ? 2 : this._states[_local_1]);
                if (_local_2 != this._states[_local_1])
                {
                    this.retrieveEntityImageAsset(_local_3.sortOrder, _local_2);
                    this.updateEntityIndicatorPanel(_local_1, false);
                    if (((_local_2 == 2) && (this._SafeStr_3074 == _local_1)))
                    {
                        this.stopFlashing();
                    };
                };
            };
            this.initializeBackgroundRendererIfAllImagesInCache();
            this.initializeEntitiesIfAllImagesInCache();
        }

        public function goToDay(_arg_1:int):void
        {
            this.scrollToIndex(Math.max(0, Math.min((_arg_1 - 3), this.maxScrollRightIndex)));
        }

        private function prepareImages():void
        {
            var _local_1:int;
            var _local_2:int;
            var _local_3:int;
            var _local_4:Boolean;
            var _local_5:int;
            var _local_6:int;
            var _local_9:QuestMessageData;
            var _local_7:int = int(int((Math.ceil((this._SafeStr_3070 / 7)) + 1)));
            this._bgAssetNameArray = new Array(_local_7);
            this._backgroundImageCache = new Vector.<BitmapData>(_local_7);
            this._graphicEntityCache = new Vector.<BitmapData>(this._SafeStr_3070);
            this._states = new Array(this._SafeStr_3070);
            var _local_8:Vector.<BitmapData> = new Vector.<BitmapData>();
            _local_1 = 0;
            while (_local_1 < _local_7)
            {
                _local_8.push(new BitmapData(640, 320, false, 0xFFFFFF));
                _local_1++;
            };
            this._SafeStr_3063.initializeImageChain(_local_8);
            _local_2 = this.firstBgIndex;
            while (_local_2 <= this.lastBgIndex)
            {
                this.retrieveBackgroundImageAsset(_local_2);
                _local_2++;
            };
            this._SafeStr_3061 = new Map();
            for each (_local_9 in this._SafeStr_3060)
            {
                if (_local_9.sortOrder <= this._SafeStr_3071)
                {
                    _local_3 = ((_local_9.completedCampaign) ? 2 : 0);
                    _local_4 = (((_local_9.sortOrder - 1) >= this.firstVisibleIndex) && ((_local_9.sortOrder - 1) <= this.lastVisibleIndex));
                    this.retrieveEntityImageAsset(_local_9.sortOrder, _local_3, (!(_local_4)));
                };
            };
            if (this._SafeStr_3060.length < this._SafeStr_3070)
            {
                _local_5 = (this._highestAvailableQuestIndex + 1);
                while (_local_5 < this._SafeStr_3070)
                {
                    this.retrieveEntityImageAsset((_local_5 + 1), 1, (_local_5 > this.lastVisibleIndex));
                    _local_5++;
                };
            };
            _local_6 = 0;
            while (_local_6 < this._SafeStr_3070)
            {
                if (this._states[_local_6] == null)
                {
                    this.retrieveEntityImageAsset((_local_6 + 1), 3, ((_local_6 < this.firstVisibleIndex) || (_local_6 > this.lastVisibleIndex)));
                };
                _local_6++;
            };
        }

        private function initializeBackgroundRendererIfAllImagesInCache():void
        {
            var _local_1:int;
            var _local_2:BitmapData;
            var _local_5:int;
            if (!this.areViewableBackgroundBitmapsInitialized())
            {
                return;
            };
            var _local_3:Array = [];
            var _local_4:Vector.<BitmapData> = new Vector.<BitmapData>();
            _local_1 = 0;
            while (_local_1 < this._backgroundImageCache.length)
            {
                _local_2 = this._backgroundImageCache[_local_1];
                if (_local_2 != null)
                {
                    _local_4.push(_local_2);
                }
                else
                {
                    _local_4.push(new BitmapData(640, 320, false, 0xFFFFFF));
                    _local_3.push(_local_1);
                };
                _local_1++;
            };
            this._SafeStr_3063.initializeImageChain(_local_4);
            this.assignCurrentBackgroundSlice();
            for each (_local_5 in _local_3)
            {
                this.retrieveBackgroundImageAsset(_local_5);
            };
        }

        private function cleanUpEntityWindows():void
        {
            var _local_1:IWindow;
            if (this._entityWindows == null)
            {
                return;
            };
            for each (_local_1 in this._entityWindows)
            {
                this._SafeStr_3066.removeChild(_local_1);
                _local_1.dispose();
            };
            this._entityWindows = null;
        }

        private function initializeEntitiesIfAllImagesInCache():void
        {
            var _local_1:IWindowContainer;
            var _local_2:int;
            var _local_3:IBitmapWrapperWindow;
            var _local_4:IWindow;
            var _local_5:IWindow;
            var _local_6:IWindow;
            var _local_8:BitmapData;
            var _local_9:int;
            if (!this.areViewableEntityBitmapsInitialized())
            {
                return;
            };
            this.cleanUpEntityWindows();
            if (this._entityWindows == null)
            {
                this._entityWindows = new Vector.<IWindowContainer>();
            };
            var _local_7:Array = [];
            for each (_local_8 in this._graphicEntityCache)
            {
                _local_1 = IWindowContainer(this._SafeStr_3067.clone());
                _local_2 = this._entityWindows.length;
                if (_local_8 != null)
                {
                    _local_3 = (_local_1.findChildByName("entity_bitmap") as IBitmapWrapperWindow);
                    _local_3.width = _local_8.width;
                    _local_3.height = _local_8.height;
                    _local_3.bitmap = _local_8.clone();
                }
                else
                {
                    _local_7.push(_local_2);
                };
                _local_4 = _local_1.findChildByName("entity_mouse_region");
                _local_4.procedure = this.entityMouseRegionWindowProcedure;
                if ((((this._states[_local_2] == 1) || (this._states[_local_2] == 2)) || (this._states[_local_2] == 3)))
                {
                    _local_4.visible = false;
                };
                _local_1.visible = true;
                this._SafeStr_3066.addChild(_local_1);
                this._entityWindows.push(_local_1);
                this.updateEntityIndicatorPanel(_local_2, false);
            };
            this.repositionEntityWrappers();
            this.updateEntityVisibilities();
            _local_5 = this._SafeStr_3066.findChildByName("stripe_mask_left");
            this._SafeStr_3066.setChildIndex(_local_5, (this._SafeStr_3066.numChildren - 1));
            _local_5 = this._SafeStr_3066.findChildByName("stripe_mask_right");
            this._SafeStr_3066.setChildIndex(_local_5, (this._SafeStr_3066.numChildren - 1));
            _local_6 = this._SafeStr_3066.findChildByName("button_left");
            this._SafeStr_3066.setChildIndex(_local_6, (this._SafeStr_3066.numChildren - 1));
            _local_6 = this._SafeStr_3066.findChildByName("button_right");
            this._SafeStr_3066.setChildIndex(_local_6, (this._SafeStr_3066.numChildren - 1));
            for each (_local_9 in _local_7)
            {
                this.retrieveEntityImageAsset((_local_9 + 1), this._states[_local_9]);
            };
            if (this._states[(this._SafeStr_2554.currentDay - 1)] == 0)
            {
                this.startFlashingAtIndex((this._SafeStr_2554.currentDay - 1));
            };
        }

        private function get firstVisibleIndex():int
        {
            var _local_1:int = (this._SafeStr_3069 - 1);
            return ((_local_1 < 0) ? 0 : _local_1);
        }

        private function get lastVisibleIndex():int
        {
            var _local_1:int = ((this._SafeStr_3069 + 7) + 1);
            var _local_2:int = (this._SafeStr_3070 - 1);
            return ((_local_1 > _local_2) ? _local_2 : _local_1);
        }

        private function areViewableEntityBitmapsInitialized():Boolean
        {
            var _local_1:int;
            if (this._graphicEntityCache == null)
            {
                return (false);
            };
            _local_1 = this.firstVisibleIndex;
            while (_local_1 <= this.lastVisibleIndex)
            {
                if (this._graphicEntityCache[_local_1] == null)
                {
                    return (false);
                };
                _local_1++;
            };
            return (true);
        }

        private function get firstBgIndex():int
        {
            var _local_1:int = this.getBackgroundSliceOffset(this._SafeStr_3069);
            var _local_2:int = this._SafeStr_3063.getImageIndexForOffset(_local_1);
            return ((_local_2 < 0) ? 0 : _local_2);
        }

        private function get lastBgIndex():int
        {
            var _local_1:int = this.getBackgroundSliceOffset(this._SafeStr_3069);
            return (this._SafeStr_3063.getImageIndexForOffset((_local_1 + 640)));
        }

        private function areViewableBackgroundBitmapsInitialized():Boolean
        {
            var _local_1:int;
            if (this._backgroundImageCache == null)
            {
                return (false);
            };
            var _local_2:int = this.getBackgroundSliceOffset(this._SafeStr_3069);
            _local_1 = this.firstBgIndex;
            while (_local_1 <= this.lastBgIndex)
            {
                if (this._backgroundImageCache[_local_1] == null)
                {
                    return (false);
                };
                _local_1++;
            };
            return (true);
        }

        private function updateEntityIndicatorPanel(_arg_1:int, _arg_2:Boolean):void
        {
            var _local_3:BitmapData;
            var _local_4:String;
            if (((this._entityWindows == null) || (this._entityWindows.length < (_arg_1 - 1))))
            {
                return;
            };
            var _local_5:_SafeStr_124 = _SafeStr_124(this._entityWindows[_arg_1].findChildByName("entity_indicator"));
            var _local_6:uint = CalendarEntityStateEnums.INDICATOR_COLOR[this._states[_arg_1]];
            if (_arg_2)
            {
                _local_6 = (_local_6 + 0x202020);
            };
            if (this._SafeStr_3074 != _arg_1)
            {
                _local_5.color = _local_6;
            };
            var _local_7:IBitmapWrapperWindow = IBitmapWrapperWindow(this._entityWindows[_arg_1].findChildByName("entity_indicator_status"));
            if (this._states[_arg_1] == 2)
            {
                _local_3 = BitmapData(this._questEngine.assets.getAssetByName("calendar_quest_complete").content);
                _local_7.width = _local_3.width;
                _local_7.height = _local_3.height;
                _local_7.bitmap = _local_3.clone();
            }
            else
            {
                _local_7.bitmap = null;
            };
            var _local_8:ITextWindow = (_local_5.findChildByName("entity_indicator_text") as ITextWindow);
            var _local_9:QuestMessageData = this.getQuestByEntityWindowIndex(_arg_1);
            if (_local_9 != null)
            {
                _local_8.text = this._questEngine.getCampaignName(_local_9);
            }
            else
            {
                _local_4 = QuestMessageData.getCampaignLocalizationKeyForCode(((this._questEngine.getSeasonalCampaignCodePrefix() + "_") + (_arg_1 + 1)));
                _local_8.text = this._questEngine.getCampaignNameByCode(_local_4);
            };
        }

        private function retrieveEntityImageAsset(_arg_1:int, _arg_2:int, _arg_3:Boolean=false):void
        {
            var _local_4:String = ("day" + _arg_1);
            switch (_arg_2)
            {
                case 0:
                case 1:
                case 3:
                    _local_4 = (_local_4 + "_uncomplete");
                    break;
                case 2:
                    _local_4 = (_local_4 + "_completed");
            };
            this._states[(_arg_1 - 1)] = _arg_2;
            this._SafeStr_3061[_local_4] = (_arg_1 - 1);
            var _local_5:IAsset = this._questEngine.assets.getAssetByName(_local_4);
            if (_local_5 != null)
            {
                this.assignEntityBitmapToCacheByAssetName(_local_4);
                this.initializeEntitiesIfAllImagesInCache();
            }
            else
            {
                if (!_arg_3)
                {
                    this.loadAssetFromImageGallery(_local_4, this.onEntityImageAssetDownloaded);
                };
            };
        }

        private function retrieveBackgroundImageAsset(_arg_1:int):void
        {
            var _local_2:String = ("background_" + (_arg_1 + 1));
            this._bgAssetNameArray[_arg_1] = _local_2;
            var _local_3:IAsset = this._questEngine.assets.getAssetByName(_local_2);
            if (_local_3 != null)
            {
                this.assignBackgroundBitmapToCacheByAssetName(_local_2);
                this.initializeBackgroundRendererIfAllImagesInCache();
            }
            else
            {
                this.loadAssetFromImageGallery(_local_2, this.onBackgroundImageAssetDownloaded);
            };
        }

        private function loadAssetFromImageGallery(_arg_1:String, _arg_2:Function):void
        {
            var _local_3:* = ((this.getImageGalleryHost() + _arg_1) + ".png");
            var _local_4:URLRequest = new URLRequest(_local_3);
            var _local_5:AssetLoaderStruct = this._questEngine.assets.loadAssetFromFile(_arg_1, _local_4, "image/png");
            if (((_local_5) && (!(_local_5.disposed))))
            {
                _local_5.addEventListener("AssetLoaderEventComplete", _arg_2);
                _local_5.addEventListener("AssetLoaderEventError", _arg_2);
            };
        }

        private function onBackgroundImageAssetDownloaded(_arg_1:AssetLoaderEvent):void
        {
            var _local_2:AssetLoaderStruct = (_arg_1.target as AssetLoaderStruct);
            if (_local_2 != null)
            {
                this.assignBackgroundBitmapToCacheByAssetName(_local_2.assetName);
            };
            this.initializeBackgroundRendererIfAllImagesInCache();
        }

        private function onEntityImageAssetDownloaded(_arg_1:AssetLoaderEvent):void
        {
            var _local_2:AssetLoaderStruct = (_arg_1.target as AssetLoaderStruct);
            if (_local_2 != null)
            {
                this.assignEntityBitmapToCacheByAssetName(_local_2.assetName);
            };
            this.initializeEntitiesIfAllImagesInCache();
        }

        private function assignBackgroundBitmapToCacheByAssetName(_arg_1:String):void
        {
            var _local_2:int = this._bgAssetNameArray.indexOf(_arg_1);
            if (_local_2 == -1)
            {
                return;
            };
            var _local_3:IAsset = this._questEngine.assets.getAssetByName(_arg_1);
            this._backgroundImageCache[_local_2] = ((_local_3 != null) ? (_local_3.content as BitmapData) : new BitmapData(640, 320));
        }

        private function assignEntityBitmapToCacheByAssetName(_arg_1:String):void
        {
            var _local_2:IAsset = this._questEngine.assets.getAssetByName(_arg_1);
            var _local_3:int = this._SafeStr_3061[_arg_1];
            if (((_local_3 == -1) || (_local_3 >= this._graphicEntityCache.length)))
            {
                return;
            };
            this._graphicEntityCache[_local_3] = ((_local_2 != null) ? (_local_2.content as BitmapData) : new BitmapData(1, 1, true, 0));
        }

        private function repositionEntityWrappers():void
        {
            var _local_1:int;
            if (this._entityWindows == null)
            {
                return;
            };
            _local_1 = 0;
            while (_local_1 < this._entityWindows.length)
            {
                this._entityWindows[_local_1].x = ((((_local_1 - this._SafeStr_3069) * 80) + this._scrollOffset) + 37);
                _local_1++;
            };
        }

        private function getBackgroundSliceOffset(_arg_1:int):int
        {
            return (_arg_1 * 80);
        }

        private function assignCurrentBackgroundSlice():void
        {
            var _local_1:BitmapData = this._SafeStr_3063.getSlice(this.getBackgroundSliceOffset(this._SafeStr_3069), this._SafeStr_3066.width);
            this._SafeStr_3068.x = 0;
            this._SafeStr_3068.width = _local_1.width;
            this._SafeStr_3068.height = _local_1.height;
            this._SafeStr_3068.bitmap = _local_1.clone();
        }

        private function assignScrollableBackgroundSlice(_arg_1:int):void
        {
            var _local_2:BitmapData;
            var _local_3:int;
            var _local_4:int;
            var _local_5:int;
            var _local_6:int;
            if (_arg_1 < this._SafeStr_3069)
            {
                _local_3 = (this._SafeStr_3069 - _arg_1);
                _local_4 = this.getBackgroundSliceOffset(_arg_1);
                _local_2 = this._SafeStr_3063.getSlice(_local_4, (this._SafeStr_3066.width + (80 * _local_3)));
                this._scrollBgStartOffset = -(80 * _local_3);
            }
            else
            {
                _local_5 = (_arg_1 - this._SafeStr_3069);
                _local_6 = ((80 * _local_5) + this._SafeStr_3066.width);
                _local_2 = this._SafeStr_3063.getSlice(this.getBackgroundSliceOffset(this._SafeStr_3069), _local_6);
                this._scrollBgStartOffset = 0;
            };
            this._SafeStr_3068.x = this._scrollBgStartOffset;
            if (_local_2 != null)
            {
                this._SafeStr_3068.width = _local_2.width;
                this._SafeStr_3068.height = _local_2.height;
                this._SafeStr_3068.bitmap = _local_2.clone();
            };
        }

        private function repositionBackgroundSlice():void
        {
            this._SafeStr_3068.x = (this._scrollBgStartOffset + this._scrollOffset);
        }

        private function scrollToIndex(_arg_1:int):void
        {
            if (((_arg_1 < 0) || (_arg_1 >= this._SafeStr_3070)))
            {
                return;
            };
            if (((!(this._SafeStr_3072 == null)) && (this._SafeStr_3072.running)))
            {
                return;
            };
            if (!this.areViewableEntityBitmapsInitialized())
            {
                this._SafeStr_3069 = _arg_1;
                this.enableScrollArrowsByViewIndex();
                return;
            };
            var _local_2:int = this._SafeStr_3069;
            this._SafeStr_3069 = _arg_1;
            if (this.areViewableBackgroundBitmapsInitialized())
            {
                this._SafeStr_3069 = _local_2;
                this.assignScrollableBackgroundSlice(_arg_1);
                this.updateEntityVisibilities(true, (_arg_1 - this._SafeStr_3069));
                this._SafeStr_3073 = (-(80 * (_arg_1 - this._SafeStr_3069)) / 10);
                this._SafeStr_3072 = new Timer(10, 10);
                this._SafeStr_3072.addEventListener("timer", this.onAnimateScroll);
                this._SafeStr_3072.addEventListener("timerComplete", this.onAnimateScroll);
                this._SafeStr_3072.start();
            }
            else
            {
                this._SafeStr_3069 = _local_2;
            };
        }

        private function get maxScrollRightIndex():int
        {
            return (this._SafeStr_3071 - 7);
        }

        private function enableScrollArrowsByViewIndex():void
        {
            if (this._SafeStr_3069 > 0)
            {
                this._SafeStr_3064.activate();
            }
            else
            {
                this._SafeStr_3064.deactivate();
            };
            if (this._SafeStr_3069 < Math.min(((this._SafeStr_3070 - 3) - 1), this.maxScrollRightIndex))
            {
                this._SafeStr_3065.activate();
            }
            else
            {
                this._SafeStr_3065.deactivate();
            };
        }

        private function updateEntityVisibilities(_arg_1:Boolean=false, _arg_2:int=0):void
        {
            var _local_3:int;
            var _local_4:int;
            var _local_5:int;
            if (this._entityWindows != null)
            {
                _local_3 = (this._SafeStr_3069 - 1);
                if (((_arg_1) && (_arg_2 < 0)))
                {
                    _local_3 = (_local_3 + _arg_2);
                };
                _local_4 = ((this._SafeStr_3069 + 7) + 1);
                if (((_arg_1) && (_arg_2 > 0)))
                {
                    _local_4 = (_local_4 + _arg_2);
                };
                _local_5 = 0;
                while (_local_5 < this._entityWindows.length)
                {
                    if (((_local_5 < _local_3) || (_local_5 > _local_4)))
                    {
                        this._entityWindows[_local_5].visible = false;
                    }
                    else
                    {
                        this._entityWindows[_local_5].visible = true;
                        if (((_local_5 == _local_3) || (_local_5 == _local_4)))
                        {
                            this._entityWindows[_local_5].getChildByName("entity_mouse_region").visible = false;
                        }
                        else
                        {
                            if (this._states[_local_5] == 0)
                            {
                                this._entityWindows[_local_5].getChildByName("entity_mouse_region").visible = true;
                            };
                        };
                    };
                    _local_5++;
                };
            };
        }

        private function onAnimateScroll(_arg_1:TimerEvent):void
        {
            switch (_arg_1.type)
            {
                case "timer":
                    this._scrollOffset = (this._scrollOffset + this._SafeStr_3073);
                    this.repositionBackgroundSlice();
                    this.repositionEntityWrappers();
                    return;
                case "timerComplete":
                    this._scrollOffset = 0;
                    if (this._SafeStr_3073 > 0)
                    {
                        this._SafeStr_3069 = (this._SafeStr_3069 - 1);
                    }
                    else
                    {
                        this._SafeStr_3069 = (this._SafeStr_3069 + 1);
                    };
                    this.assignCurrentBackgroundSlice();
                    this.repositionEntityWrappers();
                    this.enableScrollArrowsByViewIndex();
                    this.updateEntityVisibilities();
                    this._SafeStr_3072.removeEventListener("timer", this.onAnimateScroll);
                    this._SafeStr_3072.removeEventListener("timerComplete", this.onAnimateScroll);
                    return;
            };
        }

        private function scrollArrowProcedure(_arg_1:WindowEvent, _arg_2:IWindow):void
        {
            if (_arg_1.type == "WME_DOWN")
            {
                switch (_arg_2.name)
                {
                    case "button_left":
                        this._SafeStr_3077 = true;
                        break;
                    case "button_right":
                        this._SafeStr_3078 = true;
                };
            };
            if (((_arg_1.type == "WME_UP") || (_arg_1.type == "WME_UP_OUTSIDE")))
            {
                this._SafeStr_3077 = false;
                this._SafeStr_3078 = false;
            };
        }

        private function entityMouseRegionWindowProcedure(_arg_1:WindowEvent, _arg_2:IWindow):void
        {
            var _local_3:int;
            var _local_4:QuestMessageData;
            if (_arg_2.name == "entity_mouse_region")
            {
                _local_3 = this._entityWindows.indexOf((_arg_2.parent as IWindowContainer));
                if (_arg_1.type == "WME_CLICK")
                {
                    _local_4 = this.getQuestByEntityWindowIndex(_local_3);
                    if (_local_4 != null)
                    {
                        this._questEngine.questController.questDetails.openDetails(_local_4, true);
                    };
                };
                if (_arg_1.type == "WME_OVER")
                {
                    this.updateEntityIndicatorPanel(_local_3, true);
                    this._SafeStr_3076 = _local_3;
                };
                if (_arg_1.type == "WME_OUT")
                {
                    this.updateEntityIndicatorPanel(_local_3, false);
                    this._SafeStr_3076 = -1;
                };
            };
        }

        private function getQuestByEntityWindowIndex(_arg_1:int):QuestMessageData
        {
            var _local_2:QuestMessageData;
            for each (_local_2 in this._SafeStr_3060)
            {
                if ((_local_2.sortOrder - 1) == _arg_1)
                {
                    return (_local_2);
                };
            };
            return (null);
        }

        public function update(_arg_1:uint):void
        {
            var _local_2:int;
            var _local_3:Number;
            var _local_4:_SafeStr_124;
            var _local_5:Number;
            if (((!(this._entityWindows == null)) && (!(this._SafeStr_3074 == -1))))
            {
                _local_2 = CalendarEntityStateEnums.INDICATOR_COLOR[this._states[this._SafeStr_3074]];
                _local_3 = ((this._SafeStr_3075 % 2000) / 2000);
                _local_3 = Math.abs((2 * ((_local_3 > 0.5) ? --_local_3 : _local_3)));
                _local_4 = _SafeStr_124(this._entityWindows[this._SafeStr_3074].findChildByName("entity_indicator"));
                if (_local_4)
                {
                    _local_5 = (_local_3 * 100);
                    if (this._SafeStr_3076 == this._SafeStr_3074)
                    {
                        _local_5 = (_local_5 + 20);
                    };
                    _local_4.color = adjustBrightness(_local_2, _local_5);
                };
                this._SafeStr_3075 = (this._SafeStr_3075 + _arg_1);
            };
            if (this._SafeStr_3072 != null)
            {
                if ((((this._SafeStr_3077) && (!(this._SafeStr_3072.running))) && (this._scrollOffset == 0)))
                {
                    if (((this._SafeStr_3069 > 0) && (!(this._SafeStr_3064.isInactive()))))
                    {
                        this.scrollToIndex((this._SafeStr_3069 - 1));
                    };
                };
                if ((((this._SafeStr_3078) && (!(this._SafeStr_3072.running))) && (this._scrollOffset == 0)))
                {
                    if (((this._SafeStr_3069 < this._highestAvailableQuestIndex) && (!(this._SafeStr_3065.isInactive()))))
                    {
                        this.scrollToIndex((this._SafeStr_3069 + 1));
                    };
                };
            };
        }

        private function startFlashingAtIndex(_arg_1:int):void
        {
            if (((_arg_1 < 0) || (_arg_1 >= this._SafeStr_3070)))
            {
                return;
            };
            this._SafeStr_3074 = _arg_1;
            this._SafeStr_3075 = 0;
        }

        private function stopFlashing():void
        {
            this._SafeStr_3074 = -1;
        }

        private function onDateRefreshTimer(_arg_1:TimerEvent):void
        {
            var _local_2:Date = new Date();
            if (this._SafeStr_3080 != _local_2.getDate())
            {
                this._questEngine.requestSeasonalQuests();
            };
            this._SafeStr_3080 = _local_2.getDate();
        }


    }
}//package com.sulake.habbo.quest.seasonalcalendar