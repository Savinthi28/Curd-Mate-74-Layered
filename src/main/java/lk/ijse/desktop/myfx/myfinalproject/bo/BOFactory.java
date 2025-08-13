package lk.ijse.desktop.myfx.myfinalproject.bo;

import lk.ijse.desktop.myfx.myfinalproject.bo.custom.impl.*;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.impl.BuffaloDAOImpl;

public class BOFactory {
    private static BOFactory boFactory;
    private BOFactory() {
    }
    public static BOFactory getInstance() {
        return boFactory == null ? (boFactory = new BOFactory()) : boFactory;
    }

    @SuppressWarnings("unchecked")
    public <T extends SuperBO> T getBO(BOTypes boTypes){
        return (T) switch (boTypes){
            case CUSTOMER -> new CustomerBOImpl();
            case BUFFALO -> new BuffaloBOImpl();
            case CURD_PRODUCTION -> new CurdProductionBOImpl();
            case DAILY_INCOME -> new DailyIncomeBOImpl();
            case DAILY_EXPENSE -> new DailyExpenseBOImpl();
            case MILK_COLLECTION -> new MilkCollectionBOImpl();
            case MILK_STORAGE -> new MilkStorageBOImpl();
            case PAYMENT -> new PaymentBOImpl();
            case POTS_INVENTORY -> new PotsInventoryBOImpl();
            case POTS_PURCHASE -> new PotsPurchaseBOImpl();
            case QUALITY_CHECK -> new QualityCheckBOImpl();
            case RAW_MATERIAL_PURCHASE -> new RawMaterialPurchaseBOImpl();
            case REPORT -> new ReportBOImpl();
            case STOCK -> new StockBOImpl();
            case SUPPLIER -> new SupplierBOImpl();
            case USER -> new UserBOImpl();
            case ORDER -> new OrderBOImpl();
        };
    }
}
