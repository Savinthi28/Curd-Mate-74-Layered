package lk.ijse.desktop.myfx.myfinalproject.dao.custom;

import lk.ijse.desktop.myfx.myfinalproject.dao.CurdDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.CurdProduction;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CurdProductionDAO extends CurdDAO<CurdProduction,String> {
    List<Integer> getAllPotsSizes() throws SQLException;
    List<String> getAllStorageIds() throws SQLException;
    Optional<Integer> findPotsSizeById(String productionId) throws SQLException;
    List<String> getAllProductionIds() throws SQLException;
    boolean reduceQuantity(String productionId, int quantity) throws SQLException;
}
