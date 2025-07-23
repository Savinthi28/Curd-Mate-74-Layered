package lk.ijse.desktop.myfx.myfinalproject.Model;

import lk.ijse.desktop.myfx.myfinalproject.Dto.MilkStorageDto;
import lk.ijse.desktop.myfx.myfinalproject.Util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MilkStorageModel {

    public static boolean updateMilkStorage(MilkStorageDto milkStorageDto) throws SQLException {
        return CrudUtil.execute(
                "update Milk_Storage set Collection_ID = ?, Storage_Date = ?, Duration = ?, Temperature = ? where Storage_ID = ?",
                milkStorageDto.getCollectionId(),
                milkStorageDto.getDate(),
                milkStorageDto.getDuration(),
                milkStorageDto.getTemperature(),
                milkStorageDto.getStorageId()
        );
    }

    public static ArrayList<String> getAllMilkStorage() throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT DISTINCT Collection_ID FROM Milk_Collection");
        ArrayList<String> milkStorageIds = new ArrayList<>();
        while (rst.next()) {
            String milkStorageId = rst.getString(1);
            milkStorageIds.add(milkStorageId);
        }
        return milkStorageIds;
    }

    public String getNextId() throws SQLException {
        ResultSet resultSet = CrudUtil.execute("select Storage_ID from Milk_Storage order by Storage_ID desc limit 1");
        String prefix = "MSI";
        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            String lastIdNumberString = lastId.substring(prefix.length());
            int lastIdNumber = Integer.parseInt(lastIdNumberString);
            int nextIdNumber = lastIdNumber + 1;
            String nextIdString = String.format(prefix + "%03d", nextIdNumber);
            return nextIdString;
        }
        return prefix + "001";
    }

    public ArrayList<MilkStorageDto> viewAllMilkStorage() throws SQLException, ClassNotFoundException {
        ResultSet rs = CrudUtil.execute("SELECT * FROM Milk_Storage");
        ArrayList<MilkStorageDto> list = new ArrayList<>();
        while (rs.next()) {
            MilkStorageDto milkStorageDto = new MilkStorageDto(
                    rs.getString("Storage_ID"),
                    rs.getString("Collection_ID"),
                    rs.getString("Storage_Date"),
                    rs.getTime("Duration"),
                    rs.getDouble("Temperature")
            );
            list.add(milkStorageDto);
        }
        return list;
    }
    public boolean saveMilkStorage(MilkStorageDto milkStorageDto) throws SQLException {
        return CrudUtil.execute(
                "insert into Milk_Storage values(?,?,?,?,?)",
                milkStorageDto.getStorageId(),
                milkStorageDto.getCollectionId(),
                milkStorageDto.getDate(),
                milkStorageDto.getDuration(),
                milkStorageDto.getTemperature()
        );
    }

    public boolean deleteMilkStorage(MilkStorageDto milkStorageDto) throws SQLException {
        String sql = "delete from Milk_Storage where Storage_ID=?";
        return CrudUtil.execute(sql, milkStorageDto.getStorageId());
    }
}
