//com.sulake.habbo.quest.Twinkle

package com.sulake.habbo.quest
{
    import com.sulake.core.runtime.IDisposable;
    import flash.geom.Point;
    import flash.display.BitmapData;

    public class Twinkle implements AnimationObject, IDisposable 
    {

        private static const FRAME_DURATION_IN_MSECS:int = 100;
        private static const FRAME_SEQUENCE:Array = [1, 2, 3, 4, 5, 6, 5, 4, 3, 2, 1];
        private static const FRAME_NOT_STARTED:int = -1;
        private static const FRAME_FINISHED:int = -2;
        private static const _SafeStr_3153:Point = new Point(44, 44);

        private var _SafeStr_601:TwinkleImages;
        private var _SafeStr_3154:int;
        private var _position:Point;

        public function Twinkle(_arg_1:TwinkleImages, _arg_2:int)
        {
            this._SafeStr_601 = _arg_1;
            this._SafeStr_3154 = _arg_2;
        }

        public function dispose():void
        {
            this._SafeStr_601 = null;
            this._position = null;
        }

        public function get disposed():Boolean
        {
            return (this._SafeStr_601 == null);
        }

        public function onAnimationStart():void
        {
            this._position = new Point(Math.round((Math.random() * _SafeStr_3153.x)), Math.round((Math.random() * _SafeStr_3153.y)));
        }

        public function getPosition(_arg_1:int):Point
        {
            return (this._position);
        }

        public function isFinished(_arg_1:int):Boolean
        {
            return (this.getFrame(_arg_1) == -2);
        }

        public function getBitmap(_arg_1:int):BitmapData
        {
            var _local_2:int = this.getFrame(_arg_1);
            return (this._SafeStr_601.getImage(FRAME_SEQUENCE[_local_2]));
        }

        private function getFrame(_arg_1:int):int
        {
            var _local_2:int = (_arg_1 - this._SafeStr_3154);
            if (_local_2 < 0)
            {
                return (-1);
            };
            var _local_3:int = int(int(Math.floor((_local_2 / 100))));
            if (_local_3 >= FRAME_SEQUENCE.length)
            {
                return (-2);
            };
            return (_local_3);
        }


    }
}//package com.sulake.habbo.quest

//------------------------------------------------------------
//com.sulake.habbo.quest.TwinkleImages

package com.sulake.habbo.quest
{
    import com.sulake.core.runtime.IDisposable;
    import com.sulake.core.assets.IAsset;
    import com.sulake.core.runtime.Component;
    import com.sulake.core.assets.BitmapDataAsset;
    import flash.display.BitmapData;

    public class TwinkleImages implements IDisposable 
    {

        private static const IMAGE_COUNT:int = 6;

        private var _questEngine:HabboQuestEngine;

        public function TwinkleImages(_arg_1:HabboQuestEngine)
        {
            var _local_2:int;
            super();
            this._questEngine = _arg_1;
            _local_2 = 1;
            while (_local_2 <= 6)
            {
                this._questEngine.windowManager.resourceManager.retrieveAsset(getImageUri(_local_2), null);
                _local_2++;
            };
        }

        private static function getImageUri(_arg_1:int):String
        {
            return (("${image.library.questing.url}ach_twinkle" + _arg_1) + ".png");
        }


        public function getImage(_arg_1:int):BitmapData
        {
            var _local_2:IAsset;
            if (this._questEngine != null)
            {
                _local_2 = Component(this._questEngine.windowManager).assets.getAssetByName(this._questEngine.interpolate(getImageUri(_arg_1)));
                if (((!(_local_2 == null)) && (_local_2 is BitmapDataAsset)))
                {
                    return (_local_2.content as BitmapData);
                };
            };
            return (null);
        }

        public function dispose():void
        {
            this._questEngine = null;
        }

        public function get disposed():Boolean
        {
            return (!(this._questEngine == null));
        }


    }
}//package com.sulake.habbo.quest
