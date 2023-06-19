package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPackage;
import messages.shop.BuyGoodsRsp;
import messages.shop.ShopGoods;

public class PacketBuyGoodsRsp extends BaseTypedPackage<BuyGoodsRsp> {
    public PacketBuyGoodsRsp(int shopType, int boughtNum, ShopGoods sg) {
        super(new BuyGoodsRsp());

        proto.setShopType(shopType);
        proto.setBuyCount(boughtNum);
        sg.setBoughtNum(boughtNum);
        proto.setGoods(sg);
    }
}
