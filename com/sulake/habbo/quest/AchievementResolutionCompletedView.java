//com.sulake.habbo.quest.AchievementResolutionCompletedView

package com.sulake.habbo.quest
{
    import com.sulake.core.runtime.IDisposable;
    import com.sulake.core.window.components.IFrameWindow;
    import com.sulake.core.window.IWindow;
    import com.sulake.core.window.events.WindowMouseEvent;
    import com.sulake.core.window.components.IWidgetWindow;
    import com.sulake.habbo.window.widgets.IBadgeImageWidget;
    import com.sulake.core.window.components.IStaticBitmapWrapperWindow;
    import com.sulake.core.window.IWindowContainer;

    public class AchievementResolutionCompletedView implements IDisposable 
    {

        private static const _SafeStr_3113:String = "header_button_close";
        private static const _SafeStr_3114:String = "cancel_button";

        private var _SafeStr_1284:AchievementsResolutionController;
        private var _window:IFrameWindow;
        private var _SafeStr_3115:String;
        private var _SafeStr_3116:String;

        public function AchievementResolutionCompletedView(_arg_1:AchievementsResolutionController)
        {
            this._SafeStr_1284 = _arg_1;
        }

        public function dispose():void
        {
            this._SafeStr_1284 = null;
            if (this._window)
            {
                this._window.dispose();
                this._window = null;
            };
        }

        public function get disposed():Boolean
        {
            return (!(this._SafeStr_1284 == null));
        }

        public function get visible():Boolean
        {
            if (!this._window)
            {
                return (false);
            };
            return (this._window.visible);
        }

        public function show(_arg_1:String, _arg_2:String):void
        {
            if (this._window == null)
            {
                this.createWindow();
            };
            this.initializeWindow();
            this._SafeStr_3116 = _arg_1;
            this._SafeStr_3115 = _arg_2;
            this.setBadge(this._SafeStr_3115);
            this._window.visible = true;
        }

        private function createWindow():void
        {
            this._window = IFrameWindow(this._SafeStr_1284.questEngine.getXmlWindow("AchievementResolutionCompleted"));
            this.addClickListener("header_button_close");
            this.addClickListener("cancel_button");
        }

        private function addClickListener(_arg_1:String):void
        {
            var _local_2:IWindow = this._window.findChildByName(_arg_1);
            if (_local_2 != null)
            {
                _local_2.addEventListener("WME_CLICK", this.onMouseClick);
            };
        }

        private function onMouseClick(_arg_1:WindowMouseEvent):void
        {
            switch (_arg_1.target.name)
            {
                case "header_button_close":
                case "cancel_button":
                    this.close();
                    return;
            };
        }

        private function initializeWindow():void
        {
            this._window.center();
        }

        private function setBadge(_arg_1:String):void
        {
            var _local_2:IWidgetWindow = (this._window.findChildByName("achievement_badge") as IWidgetWindow);
            var _local_3:IBadgeImageWidget = (_local_2.widget as IBadgeImageWidget);
            IStaticBitmapWrapperWindow(IWindowContainer(_local_2.rootWindow).findChildByName("bitmap")).assetUri = "common_loading_icon";
            _local_3.badgeId = _arg_1;
            _local_2.visible = true;
        }

        public function close():void
        {
            if (this._window)
            {
                this._window.visible = false;
            };
        }


    }
}//package com.sulake.habbo.quest
