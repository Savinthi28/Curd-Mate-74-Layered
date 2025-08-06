package lk.ijse.desktop.myfx.myfinalproject.bo.custom;

import lk.ijse.desktop.myfx.myfinalproject.Dto.CurdProductionDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.SuperBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.InUseException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CurdProductionBO extends SuperBO {
    List<CurdProductionDto> getAllCurdProductions() throws SQLException;
    void saveCurdProduction(CurdProductionDto dto) throws DuplicateException, SQLException;
    void updateCurdProduction(CurdProductionDto dto) throws NotFoundException, SQLException;
    boolean deleteCurdProduction(String productionId) throws NotFoundException, InUseException, SQLException;
    String getNextCurdProductionId() throws SQLException;
    CurdProductionDto findCurdProductionById(String productionId) throws SQLException;
    List<Integer> getAllPotsSizes() throws SQLException;
    List<String> getAllStorageIds() throws SQLException;
    Optional<Integer> getPotsSizeByProductionId(String productionId) throws SQLException;
    List<String> getAllCurdProductionIds() throws SQLException;
    boolean reduceCurdQuantity(String productionId, int quantity) throws SQLException;
}
