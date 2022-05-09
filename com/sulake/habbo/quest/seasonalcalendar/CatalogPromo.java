
//------------------------------------------------------------
//com.sulake.habbo.quest.seasonalcalendar.CatalogPromo

package com.sulake.habbo.quest.seasonalcalendar
{
    import com.sulake.core.runtime.IDisposable;
    import com.sulake.habbo.room.IGetImageListener;
    import com.sulake.habbo.session.product.IProductDataListener;
    import com.sulake.habbo.quest.HabboQuestEngine;
    import com.sulake.core.window.IWindowContainer;
    import com.sulake.core.communication.connection.IConnection;
    import com.sulake.habbo.communication.messages.incoming.catalog.CatalogPageMessageProductData;
    import com.sulake.habbo.communication.messages.incoming.catalog.SeasonalCalendarDailyOfferMessageEvent;
    import com.sulake.core.communication.messages.IMessageEvent;
    import com.sulake.habbo.communication.messages.incoming.catalog.CatalogPublishedMessageEvent;
    import com.sulake.habbo.communication.messages.outgoing.catalog._SafeStr_21;
    import com.sulake.core.window.components.IFrameWindow;
    import com.sulake.habbo.room._SafeStr_147;
    import com.sulake.core.window.components.ITextWindow;
    import com.sulake.habbo.roomevents.Util;
    import com.sulake.core.window.IWindow;
    import com.sulake.room.utils.Vector3d;
    import flash.display.BitmapData;
    import com.sulake.core.window.events.WindowEvent;
    import com.sulake.habbo.session.product.IProductData;
    import com.sulake.core.window.components.IBitmapWrapperWindow;
    import flash.geom.Rectangle;
    import flash.geom.Point;

    public class CatalogPromo implements IDisposable, IGetImageListener, IProductDataListener 
    {

        private var _questEngine:HabboQuestEngine;
        private var _window:IWindowContainer;
        private var _connection:IConnection;
        private var _SafeStr_2554:MainWindow;
        private var _SafeStr_1476:CatalogPageMessageProductData = null;
        private var _offerId:int = -1;
        private var _SafeStr_1425:int = -1;
        private var _SafeStr_3086:SeasonalCalendarDailyOfferMessageEvent = null;
        private var _SafeStr_3087:IMessageEvent = null;
        private var _SafeStr_3088:IMessageEvent = null;

        public function CatalogPromo(_arg_1:HabboQuestEngine, _arg_2:MainWindow)
        {
            this._questEngine = _arg_1;
            this._SafeStr_2554 = _arg_2;
        }

        public function dispose():void
        {
            if (this._connection)
            {
                if (this._SafeStr_3087)
                {
                    this._connection.removeMessageEvent(this._SafeStr_3087);
                    this._SafeStr_3087 = null;
                };
                if (this._SafeStr_3088)
                {
                    this._connection.removeMessageEvent(this._SafeStr_3088);
                    this._SafeStr_3088 = null;
                };
                this._connection = null;
            };
            this._questEngine = null;
        }

        public function get disposed():Boolean
        {
            return (this._questEngine == null);
        }

        public function onActivityPoints(_arg_1:int, _arg_2:int):void
        {
            if (_arg_1 == this.getActivityPointType())
            {
                this._questEngine.localization.registerParameter("quests.seasonalcalendar.promo.balance", "amount", ("" + _arg_2));
                if (this._window != null)
                {
                    this.refresh();
                };
            };
        }

        private function getActivityPointType():int
        {
            var _local_1:String = this._questEngine.configuration.getProperty("seasonalQuestCalendar.currency");
            return ((isNaN(Number(_local_1))) ? 0 : int(_local_1));
        }

        public function prepare(_arg_1:IFrameWindow):void
        {
            this._window = IWindowContainer(_arg_1.findChildByName("catalog_promo_cont"));
            this._window.findChildByName("buy_button").disable();
            this._window.findChildByName("buy_button").procedure = this.onBuyButton;
            this._connection = this._questEngine.communication.connection;
            if (this._connection != null)
            {
                this._SafeStr_3087 = new SeasonalCalendarDailyOfferMessageEvent(this.onDailyOfferMessage);
                this._SafeStr_3088 = new CatalogPublishedMessageEvent(this.onCatalogPublished);
                this._connection.addMessageEvent(this._SafeStr_3087);
                this._connection.addMessageEvent(this._SafeStr_3088);
                this._connection.send(new _SafeStr_21());
            };
        }

        public function refresh():void
        {
            var _local_1:String;
            var _local_2:_SafeStr_147;
            var _local_3:ITextWindow = ITextWindow(this._window.findChildByName("your_balance_txt"));
            var _local_4:IWindowContainer = IWindowContainer(this._window.findChildByName("currency_icon_cont"));
            _local_4.x = (_local_3.x + _local_3.width);
            Util.hideChildren(_local_4);
            var _local_5:IWindow = _local_4.findChildByName(("currency_icon_" + this.getActivityPointType()));
            if (_local_5 != null)
            {
                _local_5.visible = true;
            };
            if (this._SafeStr_1476 != null)
            {
                _local_1 = null;
                _local_2 = null;
                if (this._SafeStr_1476.productType == "i")
                {
                    _local_2 = this._questEngine.roomEngine.getWallItemImage(this._SafeStr_1476.furniClassId, new Vector3d(90, 0, 0), 64, this, 0, this._SafeStr_1476.extraParam);
                }
                else
                {
                    if (this._SafeStr_1476.productType == "s")
                    {
                        _local_2 = this._questEngine.roomEngine.getFurnitureImage(this._SafeStr_1476.furniClassId, new Vector3d(90, 0, 0), 64, this);
                    };
                };
                if (((!(_local_2 == null)) && (!(_local_2.data == null))))
                {
                    this.setPromoFurniImage(_local_2.data);
                };
            };
        }

        public function imageReady(_arg_1:int, _arg_2:BitmapData):void
        {
            this.setPromoFurniImage(_arg_2);
        }

        public function imageFailed(_arg_1:int):void
        {
        }

        private function onBuyButton(_arg_1:WindowEvent, _arg_2:IWindow):void
        {
            if (_arg_1.type == "WME_CLICK")
            {
                Logger.log("Buy button clicked");
                if (this._offerId != -1)
                {
                    this._questEngine.catalog.openCatalogPageById(this._SafeStr_1425, this._offerId, "NORMAL");
                };
            };
        }

        private function onDailyOfferMessage(_arg_1:SeasonalCalendarDailyOfferMessageEvent):void
        {
            this._window.findChildByName("buy_button").enable();
            var _local_2:IProductData = this._questEngine.sessionDataManager.getProductData(_arg_1.offer.localizationId);
            if (_local_2 != null)
            {
                ITextWindow(this._window.findChildByName("promo_info")).text = _local_2.name;
                this._SafeStr_1425 = _arg_1.pageId;
                this._offerId = _arg_1.offer.offerId;
                if (_arg_1.offer.products.length > 0)
                {
                    this._SafeStr_1476 = CatalogPageMessageProductData(_arg_1.offer.products[0]);
                    this.refresh();
                };
            }
            else
            {
                if (this._SafeStr_3086 == null)
                {
                    this._SafeStr_3086 = _arg_1;
                    this._questEngine.sessionDataManager.addProductsReadyEventListener(this);
                };
            };
        }

        public function productDataReady():void
        {
            this.onDailyOfferMessage(this._SafeStr_3086);
        }

        private function setPromoFurniImage(_arg_1:BitmapData):void
        {
            var _local_2:IBitmapWrapperWindow = IBitmapWrapperWindow(this._window.findChildByName("furni_preview"));
            var _local_3:BitmapData = new BitmapData(_local_2.width, _local_2.height, true, 0);
            var _local_4:Rectangle = _arg_1.rect;
            if (_local_4.width > _local_3.rect.width)
            {
                _local_4.x = ((_local_4.width - _local_3.rect.width) / 2);
                _local_4.width = _local_3.rect.width;
            };
            if (_local_4.height > _local_3.rect.height)
            {
                _local_4.y = ((_local_4.height - _local_3.rect.height) / 2);
                _local_4.height = _local_3.rect.height;
            };
            var _local_5:Point = new Point(0, 0);
            if (_local_3.rect.width > _local_4.width)
            {
                _local_5.x = ((_local_3.rect.width - _local_4.width) / 2);
            };
            if (_local_3.rect.height > _local_4.height)
            {
                _local_5.y = ((_local_3.rect.height - _local_4.height) / 2);
            };
            _local_3.copyPixels(_arg_1, _local_4, _local_5);
            _local_2.bitmap = _local_3;
        }

        private function onCatalogPublished(_arg_1:IMessageEvent):void
        {
            if (this._connection != null)
            {
                this._connection.send(new _SafeStr_21());
            };
        }


    }
}//package com.sulake.habbo.quest.seasonalcalendar
