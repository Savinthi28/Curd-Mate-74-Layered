package lk.ijse.desktop.myfx.myfinalproject.bo.custom;

import lk.ijse.desktop.myfx.myfinalproject.Dto.BuffaloDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.SuperBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.InUseException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public interface BuffaloBO extends SuperBO {
    List<BuffaloDto> getAllBuffaloes() throws SQLException;
    void saveBuffalo(BuffaloDto dto) throws DuplicateException, SQLException;
    void updateBuffalo(BuffaloDto dto) throws NotFoundException, SQLException;
    boolean deleteBuffalo(String id) throws NotFoundException, InUseException, SQLException;
    String getNextBuffaloId() throws SQLException;
    BuffaloDto findBuffaloById(String id) throws SQLException;
    List<String> getAllBuffaloGender() throws Exception;
}
