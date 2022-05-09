//com.sulake.habbo.quest.ProgressBar

package com.sulake.habbo.quest
{
    import com.sulake.core.runtime.IDisposable;
    import com.sulake.core.window.IWindowContainer;
    import flash.geom.Point;
    import com.sulake.core.window.IWindow;

    public class ProgressBar implements IDisposable 
    {

        private static const PROGRESS_TEXT_X_OFFSET:int = 3;
        private static const CONTAINER_SPACING:int = 10;

        private var _questEngine:HabboQuestEngine;
        private var _window:IWindowContainer;
        private var _progressBarWidth:int;
        private var _progressKey:String;
        private var _hasFrame:Boolean;
        private var _currentAmount:int;
        private var _maxAmount:int;
        private var _SafeStr_3126:int;
        private var _SafeStr_3127:int;
        private var _startProgressWidth:int;
        private var _currentProgressWidth:int;
        private var _SafeStr_3128:Boolean;

        public function ProgressBar(_arg_1:HabboQuestEngine, _arg_2:IWindowContainer, _arg_3:int, _arg_4:String, _arg_5:Boolean, _arg_6:Point)
        {
            this._questEngine = _arg_1;
            this._window = _arg_2;
            this._progressBarWidth = _arg_3;
            this._progressKey = _arg_4;
            this._hasFrame = _arg_5;
            var _local_7:IWindowContainer = IWindowContainer(this._window.findChildByName("progress_bar_cont"));
            if (_local_7 == null)
            {
                _local_7 = IWindowContainer(this._questEngine.getXmlWindow("ProgressBar"));
                this._window.addChild(_local_7);
                _local_7.x = _arg_6.x;
                _local_7.y = _arg_6.y;
                _local_7.width = (this._progressBarWidth + 10);
            };
        }

        public function refresh(_arg_1:int, _arg_2:int, _arg_3:int, _arg_4:int):void
        {
            var _local_5:Boolean = ((!(_arg_3 == this._SafeStr_3127)) || (!(_arg_2 == this._maxAmount)));
            this._maxAmount = _arg_2;
            this._currentAmount = _arg_1;
            this._startProgressWidth = this._currentProgressWidth;
            this._SafeStr_3127 = _arg_3;
            this._SafeStr_3126 = _arg_4;
            if (((_local_5) || ((this._currentAmount == 0) && (this._currentProgressWidth > 0))))
            {
                this._currentProgressWidth = this.getProgressWidth(this._currentAmount);
            };
            this._SafeStr_3128 = true;
            this.updateView();
        }

        public function set visible(_arg_1:Boolean):void
        {
            var _local_2:IWindowContainer = IWindowContainer(this._window.findChildByName("progress_bar_cont"));
            if (_local_2 != null)
            {
                _local_2.visible = _arg_1;
            };
        }

        public function updateView():void
        {
            var _local_1:int;
            if (!this._SafeStr_3128)
            {
                return;
            };
            var _local_2:IWindow = this._window.findChildByName("bar_a_bkg");
            var _local_3:IWindow = this._window.findChildByName("bar_a_c");
            var _local_4:IWindow = this._window.findChildByName("bar_a_r");
            var _local_5:IWindow = this._window.findChildByName("bar_l");
            var _local_6:IWindow = this._window.findChildByName("bar_c");
            var _local_7:IWindow = this._window.findChildByName("bar_r");
            _local_5.visible = this._hasFrame;
            _local_6.visible = this._hasFrame;
            _local_7.visible = this._hasFrame;
            if (this._hasFrame)
            {
                _local_6.width = this._progressBarWidth;
                _local_7.x = (this._progressBarWidth + _local_3.x);
            };
            var _local_8:int = this.getProgressWidth(this._currentAmount);
            if (this._currentProgressWidth < _local_8)
            {
                _local_1 = Math.min(Math.abs((this._currentProgressWidth - _local_8)), Math.abs((this._startProgressWidth - _local_8)));
                this._currentProgressWidth = Math.min(_local_8, (this._currentProgressWidth + Math.max(1, Math.round(Math.sqrt(_local_1)))));
            };
            var _local_9:* = (this._currentProgressWidth > 0);
            _local_2.visible = _local_9;
            _local_3.visible = _local_9;
            _local_4.visible = _local_9;
            if (_local_9)
            {
                _local_3.blend = (1 - ((_local_8 - this._currentProgressWidth) / (_local_8 - this._startProgressWidth)));
                _local_3.width = this._currentProgressWidth;
                _local_4.x = (this._currentProgressWidth + _local_3.x);
                _local_2.width = (_local_4.right - _local_3.left);
            };
            this._SafeStr_3128 = (this._currentProgressWidth < _local_8);
            var _local_10:IWindow = this._window.findChildByName("progress_txt");
            var _local_11:int = ((this._SafeStr_3128) ? int(Math.round(((this._currentProgressWidth / this._progressBarWidth) * this._maxAmount))) : this._currentAmount);
            this._questEngine.localization.registerParameter(this._progressKey, "progress", ("" + (_local_11 + this._SafeStr_3126)));
            this._questEngine.localization.registerParameter(this._progressKey, "limit", ("" + (this._maxAmount + this._SafeStr_3126)));
            _local_10.caption = this._questEngine.localization.getLocalization(this._progressKey, this._progressKey);
            _local_10.x = ((3 + _local_3.x) + ((this._progressBarWidth - _local_10.width) / 2));
        }

        public function dispose():void
        {
            this._questEngine = null;
            this._window = null;
        }

        public function get disposed():Boolean
        {
            return (this._questEngine == null);
        }

        private function getProgressWidth(_arg_1:int):int
        {
            return (Math.max(0, Math.round(((this._progressBarWidth * _arg_1) / this._maxAmount))));
        }


    }
}//package com.sulake.habbo.quest
