//com.sulake.habbo.catalog.vault.VaultView

package com.sulake.habbo.catalog.vault
{
    import com.sulake.core.runtime.IDisposable;
    import flash.geom.Point;
    import com.sulake.core.window.IWindowContainer;
    import com.sulake.core.window.components.ITabContextWindow;
    import com.sulake.habbo.window.IHabboWindowManager;
    import com.sulake.habbo.communication.messages.parser.vault.IncomeReward;
    import com.sulake.core.window.components.ITabButtonWindow;
    import com.sulake.core.window.components.ISelectableWindow;
    import com.sulake.core.window.IWindow;
    import com.sulake.core.window.events.WindowEvent;

    public class VaultView implements IDisposable 
    {

        private static const TAB_EARNINGS:String = "earningsTab";
        private static const TAB_VAULT:String = "vaultTab";

        private const DEFAULT_VIEW_LOCATION:Point = new Point(120, 150);

        private var _SafeStr_1284:VaultController;
        private var _window:IWindowContainer;
        private var _currentTab:String = "earningsTab";
        private var _tabContext:ITabContextWindow;
        private var _SafeStr_1509:Array = ["tutorial", "dailygift", "achievements", "marketplace", "habboclub", "levelprogression", "roombundlesales", "bonusbag", "donation", "surprise", "snowstorm"];

        public function VaultView(_arg_1:VaultController, _arg_2:IHabboWindowManager)
        {
            this._SafeStr_1284 = _arg_1;
            this._window = (_arg_2.buildFromXML(XML(this._SafeStr_1284.assets.getAssetByName("vault_view_xml").content)) as IWindowContainer);
            this._window.position = this.DEFAULT_VIEW_LOCATION;
            this._window.procedure = this.windowProcedure;
            this._window.findChildByName("vaultTab").procedure = this.onTab;
            this._window.findChildByName("earningsTab").procedure = this.onTab;
            this.refresh();
            this.switchToTab("earningsTab");
        }

        private static function getDistinctRewardCategories(_arg_1:Array):Array
        {
            var _local_2:Boolean;
            var _local_4:IncomeReward;
            var _local_5:int;
            var _local_3:Array = [];
            for each (_local_4 in _arg_1)
            {
                _local_2 = false;
                for each (_local_5 in _local_3)
                {
                    if (_local_4.rewardCategory == _local_5)
                    {
                        _local_2 = true;
                        break;
                    };
                };
                if (!_local_2)
                {
                    _local_3.push(_local_4.rewardCategory);
                };
            };
            return (_local_3);
        }


        private function resizeTabs():void
        {
            var _local_1:int;
            var _local_2:ITabButtonWindow;
            var _local_3:int = int(int((this._window.width / this._tabContext.numTabItems)));
            _local_3--;
            _local_1 = 0;
            while (_local_1 < this._tabContext.numTabItems)
            {
                _local_2 = this._tabContext.getTabItemAt(_local_1);
                _local_2.width = (_local_3 - 2);
                _local_1++;
            };
        }

        private function switchToTab(_arg_1:String):void
        {
            this._currentTab = _arg_1;
            this._tabContext.selector.setSelected(ISelectableWindow(this._window.findChildByName(this._currentTab)));
        }

        public function onCreditVaultDataReceived(_arg_1:Boolean, _arg_2:int, _arg_3:int):void
        {
            if (_arg_1)
            {
                this.toggleUnlockedVaultElements(true, _arg_2);
                this.toggleLockedVaultElements(false, _arg_2, _arg_3);
            }
            else
            {
                this.toggleUnlockedVaultElements(false, _arg_2);
                this.toggleLockedVaultElements(true, _arg_2, _arg_3);
            };
        }

        private function toggleUnlockedVaultElements(_arg_1:Boolean, _arg_2:int):void
        {
            var _local_3:IWindow;
            var _local_5:String;
            var _local_6:IWindow;
            var _local_4:Array = ["vaultUnlockedInstructions_container"];
            for each (_local_5 in _local_4)
            {
                _local_3 = this._window.findChildByName(_local_5);
                if (_local_3 != null)
                {
                    _local_3.visible = _arg_1;
                };
            };
            _local_6 = this._window.findChildByName("vaultCreditsPending_amount");
            if (_local_6 != null)
            {
                _local_6.caption = _arg_2.toString();
            };
            var _local_7:IWindow = this._window.findChildByName("vaultWithdrawAll_button");
            if (_local_7 != null)
            {
                if (_arg_2 > 0)
                {
                    _local_7.enable();
                }
                else
                {
                    _local_7.disable();
                };
            };
        }

        private function toggleLockedVaultElements(_arg_1:Boolean, _arg_2:int, _arg_3:int):void
        {
            var _local_4:IWindow;
            var _local_5:IWindow;
            var _local_6:IWindow;
            var _local_8:String;
            var _local_7:Array = ["vaultWithdrawArea", "vaultLockedInstructions_container"];
            for each (_local_8 in _local_7)
            {
                _local_4 = this._window.findChildByName(_local_8);
                if (_local_4 != null)
                {
                    _local_4.visible = _arg_1;
                };
            };
            if (_arg_1)
            {
                _local_5 = this._window.findChildByName("vaultCredits_amount");
                if (_local_5 != null)
                {
                    _local_5.caption = _arg_3.toString();
                    _local_6 = this._window.findChildByName("vaultWithdraw_button");
                    if (_local_6 != null)
                    {
                        if (_arg_3 > 0)
                        {
                            _local_6.enable();
                        }
                        else
                        {
                            _local_6.disable();
                        };
                    };
                };
            };
            var _local_9:IWindow = this._window.findChildByName("vaultCreditsPending_amount");
            if (_local_9 != null)
            {
                _local_9.caption = _arg_2.toString();
            };
        }

        public function onIncomeRewardClaimResponse(_arg_1:int, _arg_2:Boolean):void
        {
            if (_arg_2)
            {
                this.updateRewardsForCategory(_arg_1, 0, 0);
            }
            else
            {
                this.setElementEnabled((this._SafeStr_1509[_arg_1] + "_claim_button"), true);
            };
        }

        public function onIncomeRewardDataReceived(_arg_1:Array):void
        {
            var _local_2:int;
            var _local_3:int;
            var _local_4:int;
            var _local_5:Boolean;
            var _local_6:IWindow;
            var _local_8:int;
            var _local_9:String;
            var _local_10:IncomeReward;
            var _local_11:String;
            var _local_7:Array = [];
            for each (_local_8 in getDistinctRewardCategories(_arg_1))
            {
                _local_2 = 0;
                _local_3 = 0;
                _local_4 = 0;
                for each (_local_10 in _arg_1)
                {
                    if (_local_8 == _local_10.rewardCategory)
                    {
                        if (_local_10.rewardType == 0)
                        {
                            _local_2 = (_local_2 + _local_10.amount);
                        };
                        if (_local_10.rewardType === 1)
                        {
                            _local_3 = (_local_3 + _local_10.amount);
                        };
                        if (_local_10.productCode)
                        {
                            _local_4++;
                        };
                    };
                };
                this.updateRewardsForCategory(_local_8, _local_3, _local_2, _local_4);
                if ((((_local_3 > 0) || (_local_2 > 0)) || (_local_4 > 0)))
                {
                    _local_7.push(this._SafeStr_1509[_local_8]);
                };
            };
            for each (_local_9 in this._SafeStr_1509)
            {
                _local_5 = false;
                for each (_local_11 in _local_7)
                {
                    if (_local_9 == _local_11)
                    {
                        _local_5 = true;
                        break;
                    };
                };
                if (!_local_5)
                {
                    _local_6 = this._window.findChildByName((_local_9 + "_claim_button"));
                    if (_local_6 != null)
                    {
                        _local_6.disable();
                    };
                };
            };
        }

        private function updateRewardsForCategory(_arg_1:int, _arg_2:int, _arg_3:int, _arg_4:int=0):void
        {
            var _local_5:IWindow;
            var _local_6:String = this._SafeStr_1509[_arg_1];
            var _local_7:IWindow = this._window.findChildByName((_local_6 + "CreditValue"));
            if (_local_7 != null)
            {
                _local_7.caption = _arg_2.toString();
            };
            var _local_8:IWindow = this._window.findChildByName((_local_6 + "DucketValue"));
            if (_local_8 != null)
            {
                _local_8.caption = _arg_3.toString();
            };
            if (_arg_4 > 0)
            {
                _local_5 = this._window.findChildByName((_local_6 + "ProductValue"));
                if (_local_5 != null)
                {
                    _local_5.caption = _arg_4.toString();
                };
            };
        }

        private function onTab(_arg_1:WindowEvent, _arg_2:IWindow):void
        {
            if (_arg_1.type != "WME_CLICK")
            {
                return;
            };
            this._currentTab = _arg_2.name;
            this.refresh();
        }

        private function refresh():void
        {
            this._tabContext = ITabContextWindow(this._window.findChildByName("tabs"));
            this._tabContext.selector.setSelected(ISelectableWindow(this._window.findChildByName(this._currentTab)));
            this.resizeTabs();
            if (this._currentTab == "vaultTab")
            {
                this._window.findChildByName("vaultContentArea").visible = true;
                this._window.findChildByName("earningsContentArea").visible = false;
            }
            else
            {
                if (this._currentTab == "earningsTab")
                {
                    this._window.findChildByName("vaultContentArea").visible = false;
                    this._window.findChildByName("earningsContentArea").visible = true;
                };
            };
        }

        private function setElementEnabled(_arg_1:String, _arg_2:Boolean):void
        {
            var _local_3:IWindow = this._window.findChildByName(_arg_1);
            if (_local_3 != null)
            {
                if (_arg_2)
                {
                    _local_3.enable();
                }
                else
                {
                    _local_3.disable();
                };
            };
        }

        private function windowProcedure(_arg_1:WindowEvent, _arg_2:IWindow):void
        {
            if (_arg_1.type != "WME_CLICK")
            {
                return;
            };
            switch (_arg_2.name)
            {
                case "vaultWithdraw_button":
                case "vaultWithdrawAll_button":
                    this._SafeStr_1284.withdrawVaultCredits();
                    return;
                case "vaultOpenShop_button":
                    this._SafeStr_1284.openCatalogue();
                    return;
                case "header_button_close":
                    this.dispose();
                    return;
                case "dailygift_claim_button":
                    this.setElementEnabled("dailygift_claim_button", false);
                    this._SafeStr_1284.claimReward(1);
                    return;
                case "achievements_claim_button":
                    this.setElementEnabled("achievements_claim_button", false);
                    this._SafeStr_1284.claimReward(2);
                    return;
                case "marketplace_claim_button":
                    this.setElementEnabled("marketplace_claim_button", false);
                    this._SafeStr_1284.claimReward(3);
                    return;
                case "habboclub_claim_button":
                    this.setElementEnabled("habboclub_claim_button", false);
                    this._SafeStr_1284.claimReward(4);
                    return;
                case "levelprogression_claim_button":
                    this.setElementEnabled("levelprogression_claim_button", false);
                    this._SafeStr_1284.claimReward(5);
                    return;
                case "bonusbag_claim_button":
                    this.setElementEnabled("bonusbag_claim_button", false);
                    this._SafeStr_1284.claimReward(7);
                    return;
                case "donation_claim_button":
                    this.setElementEnabled("donation_claim_button", false);
                    this._SafeStr_1284.claimReward(8);
                    return;
                case "surprise_claim_button":
                    this.setElementEnabled("surprise_claim_button", false);
                    this._SafeStr_1284.claimReward(9);
                    return;
                case "snowstorm_claim_button":
                    this.setElementEnabled("snowstorm_claim_button", false);
                    this._SafeStr_1284.claimReward(10);
                    return;
            };
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


    }
}