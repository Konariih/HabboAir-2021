//com.sulake.habbo.quest.Animation

package com.sulake.habbo.quest
{
    import com.sulake.core.runtime.IDisposable;
    import com.sulake.core.window.components.IBitmapWrapperWindow;
    import flash.display.BitmapData;

    public class Animation implements IDisposable 
    {

        private var _SafeStr_1267:IBitmapWrapperWindow;
        private var _SafeStr_3120:int;
        private var _SafeStr_3121:Boolean;
        private var _SafeStr_1387:Array = [];

        public function Animation(_arg_1:IBitmapWrapperWindow)
        {
            this._SafeStr_1267 = _arg_1;
            this._SafeStr_1267.visible = false;
            if (_arg_1.bitmap == null)
            {
                _arg_1.bitmap = new BitmapData(_arg_1.width, _arg_1.height, true, 0);
            };
        }

        public function dispose():void
        {
            var _local_1:AnimationObject;
            this._SafeStr_1267 = null;
            if (this._SafeStr_1387)
            {
                for each (_local_1 in this._SafeStr_1387)
                {
                    _local_1.dispose();
                };
                this._SafeStr_1387 = null;
            };
        }

        public function get disposed():Boolean
        {
            return (this._SafeStr_1267 == null);
        }

        public function addObject(_arg_1:AnimationObject):void
        {
            this._SafeStr_1387.push(_arg_1);
        }

        public function stop():void
        {
            this._SafeStr_3121 = false;
            this._SafeStr_1267.visible = false;
        }

        public function restart():void
        {
            var _local_1:AnimationObject;
            this._SafeStr_3120 = 0;
            this._SafeStr_3121 = true;
            for each (_local_1 in this._SafeStr_1387)
            {
                _local_1.onAnimationStart();
            };
            this.draw();
            this._SafeStr_1267.visible = true;
        }

        public function update(_arg_1:uint):void
        {
            if (this._SafeStr_3121)
            {
                this._SafeStr_3120 = (this._SafeStr_3120 + _arg_1);
                this.draw();
            };
        }

        private function draw():void
        {
            var _local_1:Boolean;
            var _local_2:BitmapData;
            var _local_3:AnimationObject;
            this._SafeStr_1267.bitmap.fillRect(this._SafeStr_1267.bitmap.rect, 0);
            if (this._SafeStr_3121)
            {
                _local_1 = false;
                for each (_local_3 in this._SafeStr_1387)
                {
                    if (!_local_3.isFinished(this._SafeStr_3120))
                    {
                        _local_1 = true;
                        _local_2 = _local_3.getBitmap(this._SafeStr_3120);
                        if (_local_2 != null)
                        {
                            this._SafeStr_1267.bitmap.copyPixels(_local_2, _local_2.rect, _local_3.getPosition(this._SafeStr_3120));
                        };
                    };
                };
            };
            this._SafeStr_1267.invalidate();
            this._SafeStr_3121 = _local_1;
        }


    }
}//package com.sulake.habbo.quest
