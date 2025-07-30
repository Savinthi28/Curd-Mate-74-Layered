package lk.ijse.desktop.myfx.myfinalproject.bo;

import lk.ijse.desktop.myfx.myfinalproject.bo.custom.impl.CustomerBOImpl;

public class BOFactory {
    private static BOFactory boFactory;
    private BOFactory() {
    }
    public static BOFactory getInstance() {
        return boFactory == null ? (boFactory = new BOFactory()) : boFactory;
    }

    public <T extends SuperBO> T getBO(BOTypes boTypes){
        return switch (boTypes){
            case CUSTOMER -> (T) new CustomerBOImpl();
        };
    }
}
