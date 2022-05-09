//com.sulake.habbo.quest.HabboQuestEngine

package com.sulake.habbo.quest
{
    import com.sulake.core.runtime.Component;
    import com.sulake.core.runtime.IUpdateReceiver;
    import com.sulake.core.runtime.events.ILinkEventTracker;
    import com.sulake.habbo.window.IHabboWindowManager;
    import com.sulake.habbo.communication.IHabboCommunicationManager;
    import com.sulake.habbo.localization.IHabboLocalizationManager;
    import com.sulake.core.runtime.ICoreConfiguration;
    import com.sulake.habbo.toolbar.IHabboToolbar;
    import com.sulake.habbo.catalog.IHabboCatalog;
    import com.sulake.habbo.navigator.IHabboNewNavigator;
    import com.sulake.habbo.notifications.IHabboNotifications;
    import com.sulake.habbo.session.ISessionDataManager;
    import com.sulake.habbo.help.IHabboHelp;
    import com.sulake.habbo.tracking.IHabboTracking;
    import com.sulake.habbo.room.IRoomEngine;
    import com.sulake.iid.IIDHabboCommunicationManager;
    import com.sulake.iid.IIDHabboWindowManager;
    import com.sulake.iid.IIDHabboLocalizationManager;
    import com.sulake.iid.IIDHabboConfigurationManager;
    import com.sulake.iid.IIDHabboToolbar;
    import com.sulake.iid.IIDHabboCatalog;
    import com.sulake.iid.IIDHabboNotifications;
    import com.sulake.iid.IIDHabboHelp;
    import com.sulake.iid.IIDHabboNewNavigator;
    import com.sulake.iid.IIDSessionDataManager;
    import com.sulake.iid.IIDRoomEngine;
    import com.sulake.iid.IIDHabboTracking;
    import com.sulake.core.runtime.IContext;
    import com.sulake.core.assets.IAssetLibrary;
    import com.sulake.core.window.IWindow;
    import com.sulake.core.window.IWindowContainer;
    import com.sulake.core.assets.IAsset;
    import com.sulake.core.assets.XmlAsset;
    import com.sulake.core.runtime.IID;
    import com.sulake.core.runtime.IUnknown;
    import com.sulake.habbo.communication.messages.incoming.quest.QuestMessageData;
    import com.sulake.habbo.toolbar.events.HabboToolbarEvent;
    import com.sulake.core.communication.messages.IMessageComposer;
    import com.sulake.core.window.components.IStaticBitmapWrapperWindow;
    import com.sulake.habbo.catalog.purse._SafeStr_139;
    import com.sulake.core.window.components.IBitmapWrapperWindow;
    import com.sulake.habbo.communication.messages.outgoing.quest._SafeStr_28;
    import com.sulake.habbo.communication.messages.outgoing.quest._SafeStr_35;
    import com.sulake.habbo.communication.messages.outgoing.quest.ActivateQuestMessageComposer;

    public class HabboQuestEngine extends Component implements IHabboQuestEngine, IUpdateReceiver, ILinkEventTracker 
    {

        private static const _SafeStr_3122:int = 5;
        private static const TWINKLE_ANIMATION_START_TIME:int = 800;
        private static const TWINKLE_ANIMATION_OBJECT_COUNT:int = 15;
        private static const DELAY_BETWEEN_TWINKLE_STARTS:int = 300;
        private static const _SafeStr_602:Array = ["MOVEITEM", "ENTEROTHERSROOM", "CHANGEFIGURE", "FINDLIFEGUARDTOWER", "SCRATCHAPET"];

        private var _windowManager:IHabboWindowManager;
        private var _communication:IHabboCommunicationManager;
        private var _localization:IHabboLocalizationManager;
        private var _configuration:ICoreConfiguration;
        private var _SafeStr_457:IncomingMessages;
        private var _questController:QuestController;
        private var _achievementController:AchievementController;
        private var _roomCompetitionController:RoomCompetitionController;
        private var _toolbar:IHabboToolbar;
        private var _catalog:IHabboCatalog;
        private var _navigator:IHabboNewNavigator;
        private var _notifications:IHabboNotifications;
        private var _sessionDataManager:ISessionDataManager;
        private var _habboHelp:IHabboHelp;
        private var _tracking:IHabboTracking;
        private var _SafeStr_601:TwinkleImages;
        private var _currentlyInRoom:Boolean = false;
        private var _roomEngine:IRoomEngine;
        private var _SafeStr_3123:Boolean = false;
        private var _achievementsResolutionController:AchievementsResolutionController;

        public function HabboQuestEngine(_arg_1:IContext, _arg_2:uint=0, _arg_3:IAssetLibrary=null)
        {
            super(_arg_1, _arg_2, _arg_3);
            this._questController = new QuestController(this);
            this._achievementController = new AchievementController(this);
            this._achievementsResolutionController = new AchievementsResolutionController(this);
            this._roomCompetitionController = new RoomCompetitionController(this);
            queueInterface(new IIDHabboCommunicationManager(), this.onCommunicationComponentInit);
            queueInterface(new IIDHabboWindowManager(), this.onWindowManagerReady);
            queueInterface(new IIDHabboLocalizationManager(), this.onLocalizationReady);
            queueInterface(new IIDHabboConfigurationManager(), this.onConfigurationReady);
            queueInterface(new IIDHabboToolbar(), this.onToolbarReady);
            queueInterface(new IIDHabboCatalog(), this.onCatalogReady);
            queueInterface(new IIDHabboNotifications(), this.onNotificationsReady);
            queueInterface(new IIDHabboHelp(), this.onHabboHelpReady);
            queueInterface(new IIDHabboNewNavigator(), this.onHabboNavigatorReady);
            queueInterface(new IIDSessionDataManager(), this.onSessionDataManagerReady);
            queueInterface(new IIDRoomEngine(), this.onRoomEngineReady);
            queueInterface(new IIDHabboTracking(), this.onTrackingReady);
            _arg_1.addLinkEventTracker(this);
            registerUpdateReceiver(this, 5);
        }

        public static function moveChildrenToRow(_arg_1:IWindowContainer, _arg_2:Array, _arg_3:int, _arg_4:int):void
        {
            var _local_5:IWindow;
            var _local_6:String;
            for each (_local_6 in _arg_2)
            {
                _local_5 = _arg_1.getChildByName(_local_6);
                if (((!(_local_5 == null)) && (_local_5.visible)))
                {
                    _local_5.x = _arg_3;
                    _arg_3 = (_arg_3 + (_local_5.width + _arg_4));
                };
            };
        }


        override public function dispose():void
        {
            removeUpdateReceiver(this);
            context.removeLinkEventTracker(this);
            if (this._toolbar)
            {
                this._toolbar.release(new IIDHabboToolbar());
                this._toolbar = null;
            };
            if (this._catalog != null)
            {
                this._catalog.release(new IIDHabboCatalog());
                this._catalog = null;
            };
            if (this._notifications != null)
            {
                this._notifications.release(new IIDHabboNotifications());
                this._notifications = null;
            };
            if (this._windowManager != null)
            {
                this._windowManager.release(new IIDHabboWindowManager());
                this._windowManager = null;
            };
            if (this._localization != null)
            {
                this._localization.release(new IIDHabboLocalizationManager());
                this._localization = null;
            };
            if (this._configuration != null)
            {
                this._configuration.release(new IIDHabboConfigurationManager());
                this._configuration = null;
            };
            if (this._communication != null)
            {
                this._communication.release(new IIDHabboCommunicationManager());
                this._communication = null;
            };
            if (this._sessionDataManager != null)
            {
                this._sessionDataManager.events.removeEventListener("BIRE_BADGE_IMAGE_READY", this._achievementController.onBadgeImageReady);
                this._sessionDataManager.release(new IIDSessionDataManager());
                this._sessionDataManager = null;
            };
            if (this._SafeStr_457)
            {
                this._SafeStr_457.dispose();
            };
            if (this._habboHelp != null)
            {
                this._habboHelp.release(new IIDHabboHelp());
                this._habboHelp = null;
            };
            if (this._navigator != null)
            {
                this._navigator.release(new IIDHabboNewNavigator());
                this._navigator = null;
            };
            if (this._tracking != null)
            {
                this._tracking.release(new IIDHabboTracking());
                this._tracking = null;
            };
            if (this._SafeStr_601)
            {
                this._SafeStr_601.dispose();
                this._SafeStr_601 = null;
            };
            if (this._roomEngine)
            {
                this._roomEngine.release(new IIDRoomEngine());
                this._roomEngine = null;
            };
            if (this._achievementsResolutionController)
            {
                this._achievementsResolutionController.dispose();
                this._achievementsResolutionController = null;
            };
            super.dispose();
        }

        public function getXmlWindow(_arg_1:String, _arg_2:int=1):IWindow
        {
            var _local_3:IAsset;
            var _local_4:XmlAsset;
            var _local_5:IWindow;
            try
            {
                _local_3 = assets.getAssetByName(_arg_1);
                _local_4 = XmlAsset(_local_3);
                _local_5 = this._windowManager.buildFromXML(XML(_local_4.content), _arg_2);
            }
            catch(e:Error)
            {
            };
            return (_local_5);
        }

        private function onCommunicationComponentInit(_arg_1:IID=null, _arg_2:IUnknown=null):void
        {
            this._communication = IHabboCommunicationManager(_arg_2);
            this._SafeStr_457 = new IncomingMessages(this);
        }

        private function onWindowManagerReady(_arg_1:IID=null, _arg_2:IUnknown=null):void
        {
            this._windowManager = IHabboWindowManager(_arg_2);
        }

        private function onLocalizationReady(_arg_1:IID=null, _arg_2:IUnknown=null):void
        {
            this._localization = IHabboLocalizationManager(_arg_2);
        }

        private function onConfigurationReady(_arg_1:IID, _arg_2:IUnknown):void
        {
            if (_arg_2 == null)
            {
                return;
            };
            this._configuration = (_arg_2 as ICoreConfiguration);
        }

        private function onCatalogReady(_arg_1:IID=null, _arg_2:IUnknown=null):void
        {
            if (disposed)
            {
                return;
            };
            this._catalog = (_arg_2 as IHabboCatalog);
        }

        private function onNotificationsReady(_arg_1:IID=null, _arg_2:IUnknown=null):void
        {
            if (disposed)
            {
                return;
            };
            this._notifications = (_arg_2 as IHabboNotifications);
        }

        private function onSessionDataManagerReady(_arg_1:IID=null, _arg_2:IUnknown=null):void
        {
            if (disposed)
            {
                return;
            };
            this._sessionDataManager = (_arg_2 as ISessionDataManager);
            this._sessionDataManager.events.addEventListener("BIRE_BADGE_IMAGE_READY", this._achievementController.onBadgeImageReady);
        }

        private function onHabboHelpReady(_arg_1:IID=null, _arg_2:IUnknown=null):void
        {
            if (disposed)
            {
                return;
            };
            this._habboHelp = (_arg_2 as IHabboHelp);
        }

        private function onHabboNavigatorReady(_arg_1:IID=null, _arg_2:IUnknown=null):void
        {
            if (disposed)
            {
                return;
            };
            this._navigator = (_arg_2 as IHabboNewNavigator);
        }

        private function onRoomEngineReady(_arg_1:IID=null, _arg_2:IUnknown=null):void
        {
            if (disposed)
            {
                return;
            };
            this._roomEngine = (_arg_2 as IRoomEngine);
        }

        private function onTrackingReady(_arg_1:IID=null, _arg_2:IUnknown=null):void
        {
            if (disposed)
            {
                return;
            };
            this._tracking = (_arg_2 as IHabboTracking);
        }

        public function get communication():IHabboCommunicationManager
        {
            return (this._communication);
        }

        public function get habboHelp():IHabboHelp
        {
            return (this._habboHelp);
        }

        public function get windowManager():IHabboWindowManager
        {
            return (this._windowManager);
        }

        public function get localization():IHabboLocalizationManager
        {
            return (this._localization);
        }

        public function get questController():QuestController
        {
            return (this._questController);
        }

        public function get roomCompetitionController():RoomCompetitionController
        {
            return (this._roomCompetitionController);
        }

        public function get achievementController():AchievementController
        {
            return (this._achievementController);
        }

        public function get achievementsResolutionController():AchievementsResolutionController
        {
            return (this._achievementsResolutionController);
        }

        public function get toolbar():IHabboToolbar
        {
            return (this._toolbar);
        }

        public function get roomEngine():IRoomEngine
        {
            return (this._roomEngine);
        }

        public function get catalog():IHabboCatalog
        {
            return (this._catalog);
        }

        public function get tracking():IHabboTracking
        {
            return (this._tracking);
        }

        public function openCatalog(_arg_1:QuestMessageData):void
        {
            var _local_2:String = _arg_1.catalogPageName;
            if (_local_2 != "")
            {
                Logger.log(("Questing->Open Catalog: " + _local_2));
                this._catalog.openCatalogPage(_local_2);
            }
            else
            {
                Logger.log("Questing->Open Catalog: Quest Catalog page name not defined");
                this._catalog.openCatalog();
            };
        }

        public function openNavigator(_arg_1:QuestMessageData):void
        {
            var _local_2:String;
            var _local_3:Boolean = this.hasLocalizedValue((_arg_1.getQuestLocalizationKey() + ".searchtag"));
            if (_local_3)
            {
                _local_2 = (_arg_1.getQuestLocalizationKey() + ".searchtag");
            }
            else
            {
                _local_2 = (_arg_1.getCampaignLocalizationKey() + ".searchtag");
            };
            var _local_4:String = this._localization.getLocalization(_local_2);
            Logger.log(("Questing->Open Navigator: " + _local_4));
            this._navigator.performTagSearch(_local_4);
        }

        public function hasQuestRoomsIds():Boolean
        {
            var _local_1:String = this.getQuestRoomIds();
            return ((!(_local_1 == null)) && (!(_local_1 == "")));
        }

        private function getQuestRoomIds():String
        {
            return (this._localization.getLocalization((("quests." + this.getSeasonalCampaignCodePrefix()) + ".roomids")));
        }

        public function goToQuestRooms():void
        {
            if (!this.hasQuestRoomsIds())
            {
                return;
            };
            var _local_1:String = this.getQuestRoomIds();
            var _local_2:Array = _local_1.split(",");
            if (_local_2.length == 0)
            {
                return;
            };
            var _local_3:int = Math.max(0, Math.min((_local_2.length - 1), Math.floor((Math.random() * _local_2.length))));
            var _local_4:String = _local_2[_local_3];
            var _local_5:int = int(_local_4);
            Logger.log(("Forwarding to a guest room: " + _local_5));
            this._navigator.goToRoom(_local_5);
        }

        private function onToolbarReady(_arg_1:IID=null, _arg_2:IUnknown=null):void
        {
            this._toolbar = (IHabboToolbar(_arg_2) as IHabboToolbar);
            this._toolbar.events.addEventListener("HTE_TOOLBAR_CLICK", this.onHabboToolbarEvent);
        }

        private function onHabboToolbarEvent(_arg_1:HabboToolbarEvent):void
        {
            if (_arg_1.type == "HTE_TOOLBAR_CLICK")
            {
                if (_arg_1.iconId == "HTIE_ICON_QUESTS")
                {
                    this._questController.onToolbarClick();
                };
                if (_arg_1.iconId == "HTIE_ICON_ACHIEVEMENTS")
                {
                    this._achievementController.onToolbarClick();
                };
            };
        }

        public function ensureAchievementsInitialized():void
        {
            if (this._achievementController != null)
            {
                this._achievementController.ensureAchievementsInitialized();
            };
        }

        public function showAchievements():void
        {
            if (this._achievementController != null)
            {
                this._achievementController.show();
            };
        }

        public function showQuests():void
        {
        }

        public function getAchievementLevel(_arg_1:String, _arg_2:String):int
        {
            if (this._achievementController != null)
            {
                return (this._achievementController.getAchievementLevel(_arg_1, _arg_2));
            };
            return (0);
        }

        public function reenableRoomCompetitionWindow():void
        {
            this._roomCompetitionController.dontShowAgain = false;
        }

        public function get notifications():IHabboNotifications
        {
            return (this._notifications);
        }

        public function get sessionDataManager():ISessionDataManager
        {
            return (this._sessionDataManager);
        }

        public function send(_arg_1:IMessageComposer):void
        {
            this.communication.connection.send(_arg_1);
        }

        public function isTrackerVisible():Boolean
        {
            return (this._questController.questTracker.isVisible());
        }

        public function getQuestRowTitle(_arg_1:QuestMessageData):String
        {
            var _local_2:String = ((_arg_1.waitPeriodSeconds < 1) ? (_arg_1.getQuestLocalizationKey() + ".name") : "quests.list.questdelayed");
            return (this._localization.getLocalization(_local_2, _local_2));
        }

        public function getQuestName(_arg_1:QuestMessageData):String
        {
            var _local_2:* = (_arg_1.getQuestLocalizationKey() + ".name");
            return (this._localization.getLocalization(_local_2, _local_2));
        }

        public function getQuestDesc(_arg_1:QuestMessageData):String
        {
            var _local_2:* = (_arg_1.getQuestLocalizationKey() + ".desc");
            return (this._localization.getLocalization(_local_2, _local_2));
        }

        public function getQuestHint(_arg_1:QuestMessageData):String
        {
            var _local_2:* = (_arg_1.getQuestLocalizationKey() + ".hint");
            return (this._localization.getLocalization(_local_2, _local_2));
        }

        public function getActivityPointName(_arg_1:int):String
        {
            var _local_2:String = ("achievements.activitypoint." + _arg_1);
            return (this._localization.getLocalization(_local_2, _local_2));
        }

        public function getCampaignNameByCode(_arg_1:String):String
        {
            var _local_2:* = (_arg_1 + ".name");
            return (this._localization.getLocalization(_local_2, _local_2));
        }

        public function getCampaignName(_arg_1:QuestMessageData):String
        {
            return (this.getCampaignNameByCode(_arg_1.getCampaignLocalizationKey()));
        }

        public function getAchievementCategoryName(_arg_1:String):String
        {
            var _local_2:* = (("quests." + _arg_1) + ".name");
            return (this._localization.getLocalization(_local_2, _local_2));
        }

        public function setupQuestImage(_arg_1:IWindowContainer, _arg_2:QuestMessageData):void
        {
            var _local_3:IStaticBitmapWrapperWindow = (_arg_1.findChildByName("quest_pic_bitmap") as IStaticBitmapWrapperWindow);
            var _local_4:String = ((_arg_2.waitPeriodSeconds > 0) ? "quest_timer_questionmark" : ((((_arg_2.campaignCode + "_") + _arg_2.localizationCode) + _arg_2.imageVersion) + ((this.isQuestWithPrompts(_arg_2)) ? "_a" : "")).toLowerCase());
            _local_3.assetUri = (("${image.library.questing.url}" + _local_4) + ".png");
        }

        public function setupPromptFrameImage(_arg_1:IWindowContainer, _arg_2:QuestMessageData, _arg_3:String):void
        {
            var _local_4:IStaticBitmapWrapperWindow = (_arg_1.findChildByName(("prompt_pic_" + _arg_3)) as IStaticBitmapWrapperWindow);
            _local_4.assetUri = (("${image.library.questing.url}" + (((((_arg_2.campaignCode + "_") + _arg_2.localizationCode) + _arg_2.imageVersion) + "_") + _arg_3).toLowerCase()) + ".png");
        }

        public function setupRewardImage(_arg_1:IWindowContainer, _arg_2:int):void
        {
            var _local_3:IWindow = _arg_1.findChildByName("currency_icon");
            _local_3.style = _SafeStr_139.getIconStyleFor(_arg_2, this, true);
        }

        public function setupCampaignImage(_arg_1:IWindowContainer, _arg_2:QuestMessageData, _arg_3:Boolean):void
        {
            var _local_4:IStaticBitmapWrapperWindow = (_arg_1.findChildByName("campaign_pic_bitmap") as IStaticBitmapWrapperWindow);
            if (!_arg_3)
            {
                _local_4.visible = false;
                return;
            };
            _local_4.visible = true;
            var _local_5:String = _arg_2.campaignCode;
            if (this.isSeasonalQuest(_arg_2))
            {
                _local_5 = (this.getSeasonalCampaignCodePrefix() + "_campaign_icon");
            };
            _local_4.assetUri = (("${image.library.questing.url}" + _local_5) + ".png");
        }

        public function setupAchievementCategoryImage(_arg_1:IWindowContainer, _arg_2:AchievementCategory, _arg_3:Boolean):void
        {
            var _local_4:IStaticBitmapWrapperWindow = (_arg_1.findChildByName("category_pic_bitmap") as IStaticBitmapWrapperWindow);
            _local_4.assetUri = (("${image.library.questing.url}" + ((_arg_3) ? ("ach_category_" + _arg_2.code) : ("achicon_" + _arg_2.code))) + ".png");
        }

        public function isQuestWithPrompts(_arg_1:QuestMessageData):Boolean
        {
            return (_SafeStr_602.indexOf(_arg_1.localizationCode) > -1);
        }

        public function refreshReward(_arg_1:Boolean, _arg_2:IWindowContainer, _arg_3:int, _arg_4:int):void
        {
            _arg_1 = (((_arg_3 < 0) || (_arg_4 < 1)) ? false : _arg_1);
            var _local_5:IWindow = _arg_2.findChildByName("reward_caption_txt");
            var _local_6:IWindow = _arg_2.findChildByName("reward_amount_txt");
            var _local_7:IWindow = _arg_2.findChildByName("currency_icon");
            _local_6.visible = _arg_1;
            _local_5.visible = _arg_1;
            _local_7.visible = _arg_1;
            if (!_arg_1)
            {
                return;
            };
            _local_6.caption = ("" + _arg_4);
            moveChildrenToRow(_arg_2, ["reward_caption_txt", "reward_amount_txt", "currency_icon"], _local_5.x, 3);
            this.setupRewardImage(_arg_2, _arg_3);
        }

        public function update(_arg_1:uint):void
        {
            this._questController.update(_arg_1);
            this._achievementController.update(_arg_1);
        }

        public function getTwinkleAnimation(_arg_1:IWindowContainer):Animation
        {
            var _local_2:int;
            if (this._SafeStr_601 == null)
            {
                this._SafeStr_601 = new TwinkleImages(this);
            };
            var _local_3:* = 800;
            var _local_4:Animation = new Animation(IBitmapWrapperWindow(_arg_1.findChildByName("twinkle_bitmap")));
            _local_2 = 0;
            while (_local_2 < 15)
            {
                _local_4.addObject(new Twinkle(this._SafeStr_601, _local_3));
                _local_3 = (_local_3 + 300);
                _local_2++;
            };
            return (_local_4);
        }

        public function get currentlyInRoom():Boolean
        {
            return (this._currentlyInRoom);
        }

        public function set currentlyInRoom(_arg_1:Boolean):void
        {
            this._currentlyInRoom = _arg_1;
        }

        public function isSeasonalCalendarEnabled():Boolean
        {
            return (this._configuration.getBoolean("seasonalQuestCalendar.enabled"));
        }

        public function isSeasonalQuest(_arg_1:QuestMessageData):Boolean
        {
            var _local_2:String = this.getSeasonalCampaignCodePrefix();
            return ((!(_local_2 == "")) && (_arg_1.campaignCode.indexOf(_local_2) == 0));
        }

        public function getSeasonalCampaignCodePrefix():String
        {
            return (getProperty("seasonalQuestCalendar.campaignPrefix"));
        }

        public function setIsFirstLoginOfDay(_arg_1:Boolean):void
        {
            this._SafeStr_3123 = _arg_1;
        }

        public function get isFirstLoginOfDay():Boolean
        {
            return (this._SafeStr_3123);
        }

        public function get configuration():ICoreConfiguration
        {
            return (this._configuration);
        }

        public function hasLocalizedValue(_arg_1:String):Boolean
        {
            return (!(this._localization.getLocalization(_arg_1, "") == ""));
        }

        public function get navigator():IHabboNewNavigator
        {
            return (this._navigator);
        }

        public function requestSeasonalQuests():void
        {
            this.send(new _SafeStr_28());
        }

        public function requestQuests():void
        {
            this.send(new _SafeStr_35());
        }

        public function activateQuest(_arg_1:int):void
        {
            this.send(new ActivateQuestMessageComposer(_arg_1));
        }

        public function get linkPattern():String
        {
            return ("questengine/");
        }

        public function linkReceived(_arg_1:String):void
        {
            var _local_2:Array = _arg_1.split("/");
            if (_local_2.length < 2)
            {
                return;
            };
            switch (_local_2[1])
            {
                case "gotorooms":
                    this.goToQuestRooms();
                    return;
                case "achievements":
                    if (_local_2.length == 3)
                    {
                        this._achievementController.show();
                        this._achievementController.selectCategoryInternalLink(_local_2[2]);
                    }
                    else
                    {
                        this.showAchievements();
                    };
                    return;
                case "calendar":
                    this._questController.seasonalCalendarWindow.onToolbarClick();
                    return;
                case "quests":
                    this._questController.onToolbarClick();
                    return;
                default:
                    Logger.log(("QuestEngine unknown link-type received: " + _local_2[1]));
                    return;
            };
        }


    }
}//package com.sulake.habbo.quest
