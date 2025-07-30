package lk.ijse.desktop.myfx.myfinalproject.dao;

import lk.ijse.desktop.myfx.myfinalproject.bo.custom.impl.CustomerBOImpl;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.impl.CustomerDAOImpl;

public class DAOFactory {
    private static DAOFactory daoFactory;

    private DAOFactory() {
    }
    public static DAOFactory getInstance() {
        return daoFactory == null ? (daoFactory = new DAOFactory()) : daoFactory;
    }

    public <T extends SuperDAO> T getDAO(DAOTypes daoType) {
        return switch (daoType){
            case CUSTOMER -> (T) new CustomerDAOImpl();
        };
    }
}
