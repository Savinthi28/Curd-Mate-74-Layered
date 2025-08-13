package lk.ijse.desktop.myfx.myfinalproject.dao;

import lk.ijse.desktop.myfx.myfinalproject.dao.custom.impl.*;

public class DAOFactory {
    private static DAOFactory daoFactory;

    private DAOFactory() {
    }
    public static DAOFactory getInstance() {
        return daoFactory == null ? (daoFactory = new DAOFactory()) : daoFactory;
    }

    @SuppressWarnings("unchecked")
    public <T extends SuperDAO> T getDAO(DAOTypes daoType) {
        return (T) switch (daoType){
            case CUSTOMER -> new CustomerDAOImpl();
            case BUFFALO -> new BuffaloDAOImpl();
            case CURD_PRODUCTION -> new CurdProductionDAOImpl();
            case DAILY_INCOME -> new DailyIncomeDAOImpl();
            case DAILY_EXPENSE -> new DailyExpenseDAOImpl();
            case MILK_COLLECTION -> new MilkCollectionDAOImpl();
            case MILK_STORAGE -> new MilkStorageDAOImpl();
            case PAYMENT -> new PaymentDAOImpl();
            case POTS_INVENTORY -> new PotsInventoryDAOImpl();
            case POTS_PURCHASE -> new PotsPurchaseDAOImpl();
            case QUALITY_CHECK -> new QualityCheckDAOImpl();
            case RAW_MATERIAL_PURCHASE -> new RawMaterialPurchaseDAOImpl();
            case REPORT -> new ReportDAOImpl();
            case STOCK -> new StockDAOImpl();
            case SUPPLIER -> new SupplierDAOImpl();
            case USER -> new UserDAOImpl();
            case ORDER -> new OrderDAOImpl(); // NEW
            case ORDER_DETAILS -> new OrderDetailsDAOImpl(); // NEW
        };
    }
}
