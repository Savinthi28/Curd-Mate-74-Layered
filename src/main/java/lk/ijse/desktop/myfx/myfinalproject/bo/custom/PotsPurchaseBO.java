package lk.ijse.desktop.myfx.myfinalproject.bo.custom;

import lk.ijse.desktop.myfx.myfinalproject.Dto.PotsPurchaseDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.SuperBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface PotsPurchaseBO extends SuperBO {
    List<PotsPurchaseDto> getAllPotsPurchases() throws SQLException;
    void savePotsPurchase(PotsPurchaseDto dto) throws DuplicateException, SQLException;
    void updatePotsPurchase(PotsPurchaseDto dto) throws NotFoundException, SQLException;
    boolean deletePotsPurchase(String id) throws NotFoundException, SQLException;
    String getNextPotsPurchaseId() throws SQLException;
    PotsPurchaseDto findPotsPurchaseById(String id) throws SQLException;
    List<Integer> getAllPotsSizesForPurchase() throws SQLException;
}
