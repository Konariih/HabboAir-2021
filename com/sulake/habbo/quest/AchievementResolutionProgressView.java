//com.sulake.habbo.quest.AchievementResolutionProgressView

package com.sulake.habbo.quest
{
    import com.sulake.core.runtime.IDisposable;
    import com.sulake.core.window.components.IFrameWindow;
    import com.sulake.core.window.components.IWidgetWindow;
    import com.sulake.habbo.window.widgets.IBadgeImageWidget;
    import com.sulake.core.window.components.IStaticBitmapWrapperWindow;
    import com.sulake.core.window.IWindowContainer;
    import com.sulake.habbo.window.widgets.ICountdownWidget;
    import com.sulake.core.window.events.WindowEvent;
    import com.sulake.core.window.IWindow;

    public class AchievementResolutionProgressView implements IDisposable 
    {

        private static const PROGRESSBAR_LEFT:String = "achieved_left";
        private static const PROGRESSBAR_MID:String = "achieved_mid";
        private static const PROGRESSBAR_RIGHT:String = "achieved_right";

        private var _SafeStr_3117:int;
        private var _SafeStr_1284:AchievementsResolutionController;
        private var _window:IFrameWindow;
        private var _stuffId:int;
        private var _achievementId:int;
        private var _SafeStr_3115:String;

        public function AchievementResolutionProgressView(_arg_1:AchievementsResolutionController)
        {
            this._SafeStr_1284 = _arg_1;
        }

        public function dispose():void
        {
            if (this._window)
            {
                this._window.dispose();
                this._window = null;
            };
            this._SafeStr_1284 = null;
        }

        public function get disposed():Boolean
        {
            return (this._SafeStr_1284 == null);
        }

        public function get achievementId():int
        {
            return (this._achievementId);
        }

        public function get stuffId():int
        {
            return (this._stuffId);
        }

        public function get visible():Boolean
        {
            if (!this._window)
            {
                return (false);
            };
            return (this._window.visible);
        }

        public function show(_arg_1:int, _arg_2:int, _arg_3:String, _arg_4:int, _arg_5:int, _arg_6:int):void
        {
            if (this._window == null)
            {
                this.createWindow();
            };
            if (_arg_2 != this._achievementId)
            {
                this.initializeWindow();
                this._window.center();
            };
            this._stuffId = _arg_1;
            this._achievementId = _arg_2;
            this._SafeStr_3115 = _arg_3;
            this.setProgress(_arg_4, _arg_5);
            this.setBadge(this._SafeStr_3115);
            this.setLocalizations();
            this.setCountdown(_arg_6);
            this._window.visible = true;
        }

        private function setProgress(_arg_1:int, _arg_2:int):void
        {
            var _local_3:Number = Math.min(1, (_arg_1 / _arg_2));
            if (_local_3 > 0)
            {
                this._window.setVisibleChildren(true, ["achieved_left", "achieved_mid"]);
                this._window.findChildByName("achieved_right").visible = (_local_3 == 1);
            };
            this._window.findChildByName("achieved_mid").width = (this._SafeStr_3117 * _local_3);
            this._SafeStr_1284.questEngine.localization.registerParameter("resolution.progress.progress", "progress", _arg_1.toString());
            this._SafeStr_1284.questEngine.localization.registerParameter("resolution.progress.progress", "total", _arg_2.toString());
        }

        private function setBadge(_arg_1:String):void
        {
            var _local_2:IWidgetWindow = (this._window.findChildByName("achievement_badge") as IWidgetWindow);
            var _local_3:IBadgeImageWidget = (_local_2.widget as IBadgeImageWidget);
            IStaticBitmapWrapperWindow(IWindowContainer(_local_2.rootWindow).findChildByName("bitmap")).assetUri = "common_loading_icon";
            _local_3.badgeId = _arg_1;
            _local_2.visible = true;
        }

        private function setLocalizations():void
        {
            this._window.findChildByName("achievement.name").caption = this._SafeStr_1284.questEngine.localization.getBadgeName(this._SafeStr_3115);
            this._window.findChildByName("achievement.desc").caption = this._SafeStr_1284.questEngine.localization.getBadgeDesc(this._SafeStr_3115);
        }

        private function setCountdown(_arg_1:int):void
        {
            var _local_2:IWidgetWindow = IWidgetWindow(this._window.findChildByName("time_left_widget"));
            var _local_3:ICountdownWidget = ICountdownWidget(_local_2.widget);
            _local_3.seconds = _arg_1;
            _local_3.running = true;
        }

        private function createWindow():void
        {
            this._window = IFrameWindow(this._SafeStr_1284.questEngine.getXmlWindow("AchievementResolutionProgress"));
            this._window.findChildByTag("close").procedure = this.onWindowClose;
            this._window.findChildByName("reset_button").procedure = this.onResetButton;
            this._SafeStr_3117 = this._window.findChildByName("achieved_mid").width;
        }

        private function initializeWindow():void
        {
            this._window.center();
            this._window.setVisibleChildren(false, ["achieved_left", "achieved_mid", "achieved_right"]);
        }

        public function close():void
        {
            if (this._window)
            {
                this._window.visible = false;
            };
        }

        private function onWindowClose(_arg_1:WindowEvent, _arg_2:IWindow):void
        {
            if (_arg_1.type == "WME_CLICK")
            {
                this.close();
            };
        }

        private function onResetButton(_arg_1:WindowEvent, _arg_2:IWindow):void
        {
            if (_arg_1.type == "WME_CLICK")
            {
                this._SafeStr_1284.resetResolution(this._stuffId);
                this.close();
            };
        }


    }
}//package com.sulake.habbo.quest
