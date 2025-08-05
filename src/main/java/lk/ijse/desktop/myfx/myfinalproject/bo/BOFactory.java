package lk.ijse.desktop.myfx.myfinalproject.bo;

import lk.ijse.desktop.myfx.myfinalproject.bo.custom.impl.BuffaloBOImpl;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.impl.CustomerBOImpl;
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
        };
    }
}
