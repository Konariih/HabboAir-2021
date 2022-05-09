
//------------------------------------------------------------
//com.sulake.habbo.quest.seasonalcalendar.CalendarBackgroundRenderer

package com.sulake.habbo.quest.seasonalcalendar
{
    import com.sulake.core.runtime.IDisposable;
    import __AS3__.vec.Vector;
    import flash.display.BitmapData;
    import flash.geom.Rectangle;
    import flash.geom.Point;

    public class CalendarBackgroundRenderer implements IDisposable 
    {

        private var _SafeStr_1274:Vector.<BitmapData>;
        private var _disposed:Boolean;


        public function get disposed():Boolean
        {
            return (this._disposed);
        }

        public function dispose():void
        {
            if (!this._disposed)
            {
                this._SafeStr_1274 = null;
            };
        }

        public function initializeImageChain(_arg_1:Vector.<BitmapData>):void
        {
            this._SafeStr_1274 = _arg_1;
        }

        public function getSlice(_arg_1:int, _arg_2:int):BitmapData
        {
            var _local_3:int;
            var _local_4:int;
            var _local_5:BitmapData;
            var _local_6:int;
            var _local_8:int;
            if ((((this._disposed) || (this._SafeStr_1274 == null)) || (this._SafeStr_1274.length == 0)))
            {
                return (new BitmapData(1, 1));
            };
            var _local_7:BitmapData = new BitmapData(_arg_2, this._SafeStr_1274[0].height, false, 0);
            while (_local_8 < _arg_2)
            {
                _local_3 = (_arg_1 + _local_8);
                _local_4 = this.getImageIndexForOffset(_local_3);
                if (_local_4 < 0)
                {
                    _local_8 = (_local_8 + -(_arg_1));
                    if (_arg_1 >= 0)
                    {
                        return (new BitmapData(1, 1));
                    };
                }
                else
                {
                    _local_5 = this._SafeStr_1274[_local_4];
                    _local_6 = this.getRelativeXForOffset(_local_3);
                    if (_local_5.width > ((_local_6 + _arg_2) - _local_8))
                    {
                        _local_7.copyPixels(_local_5, new Rectangle(_local_6, 0, (_arg_2 - _local_8), _local_5.height), new Point(_local_8, 0));
                        _local_8 = (_local_8 + (_arg_2 - _local_8));
                    }
                    else
                    {
                        _local_7.copyPixels(_local_5, new Rectangle(_local_6, 0, (_local_5.width - _local_6), _local_5.height), new Point(_local_8, 0));
                        _local_8 = (_local_8 + (_local_5.width - _local_6));
                    };
                };
            };
            return (_local_7);
        }

        public function getImageIndexForOffset(_arg_1:int):int
        {
            var _local_2:int;
            var _local_3:int;
            _local_2 = 0;
            while (_local_2 < this._SafeStr_1274.length)
            {
                if (((_local_3 <= _arg_1) && (_arg_1 < (_local_3 + this._SafeStr_1274[_local_2].width))))
                {
                    return (_local_2);
                };
                _local_3 = (_local_3 + this._SafeStr_1274[_local_2].width);
                _local_2++;
            };
            return (-1);
        }

        private function getRelativeXForOffset(_arg_1:int):int
        {
            var _local_2:int;
            var _local_3:int;
            _local_2 = 0;
            while (_local_2 < this._SafeStr_1274.length)
            {
                if (((_local_3 <= _arg_1) && (_arg_1 < (_local_3 + this._SafeStr_1274[_local_2].width))))
                {
                    return (_arg_1 - _local_3);
                };
                _local_3 = (_local_3 + this._SafeStr_1274[_local_2].width);
                _local_2++;
            };
            return (-1);
        }


    }
}//package com.sulake.habbo.quest.seasonalcalendar

//------------------------------------------------------------
//com.sulake.habbo.quest.seasonalcalendar.CalendarEntityStateEnums

package com.sulake.habbo.quest.seasonalcalendar
{
    public class CalendarEntityStateEnums 
    {

        public static const ACTIVE:int = 0;
        public static const INACTIVE:int = 1;
        public static const COMPLETED:int = 2;
        public static const _SafeStr_3085:int = 3;
        public static const INDICATOR_COLOR:Array = new Array(2134301, 12439506, 0x999999, 0x999999);


    }
}//package com.sulake.habbo.quest.seasonalcalendar
