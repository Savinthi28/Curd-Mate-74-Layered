package lk.ijse.desktop.myfx.myfinalproject.Model;

import lk.ijse.desktop.myfx.myfinalproject.Dto.PotsInventoryDto;
import lk.ijse.desktop.myfx.myfinalproject.Util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PotsInventoryModel {

    public static boolean updatePotsInventory(PotsInventoryDto potsInventoryDto) throws SQLException {
        return CrudUtil.execute(
                "update Pots_Inventory set Quantity = ?, Pots_Size = ?, `Condition` = ? where Inventory_ID = ?",
                potsInventoryDto.getQuantity(),
                potsInventoryDto.getPotsSize(),
                potsInventoryDto.getCondition(),
                potsInventoryDto.getId()
        );
    }

    public static ArrayList<Integer> getAllPotsSize() throws SQLException {
        ResultSet rs = CrudUtil.execute("select DISTINCT Pots_Size from Pots_Inventory");
        ArrayList<Integer> potsSize = new ArrayList<>();
        while (rs.next()) {
            Integer potsSizeInt = rs.getInt(1);
            potsSize.add(potsSizeInt);
        }
        return potsSize;
    }

    public String getNextId() throws SQLException {
        ResultSet resultSet = CrudUtil.execute("select Inventory_ID from Pots_Inventory order by Inventory_ID desc limit 1");
        String prefix = "PI";
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

    public ArrayList<PotsInventoryDto> viewAllPotsInventory() throws SQLException, ClassNotFoundException {
        ResultSet rs = CrudUtil.execute("SELECT * FROM Pots_Inventory");
        ArrayList<PotsInventoryDto> viewPotsInventory = new ArrayList<>();
        while (rs.next()) {
            PotsInventoryDto viewPotsInventoryDto = new PotsInventoryDto(
            rs.getString("Inventory_ID"),
            rs.getInt("Quantity"),
            rs.getInt("Pots_Size"),
            rs.getString("Condition")
            );
            viewPotsInventory.add(viewPotsInventoryDto);
        }
        return viewPotsInventory;
    }
    public boolean savePotsInventory(PotsInventoryDto potsInventoryDto) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute(
                "insert into Pots_Inventory values (?,?,?,?)",
                potsInventoryDto.getId(),
                potsInventoryDto.getQuantity(),
                potsInventoryDto.getPotsSize(),
                potsInventoryDto.getCondition()
        );
    }

    public boolean deletePotsInventory(PotsInventoryDto potsInventoryDto) throws SQLException{
        String sql = "delete from Pots_Inventory where Inventory_ID=?";
        return CrudUtil.execute(sql,potsInventoryDto.getId());
    }
}
