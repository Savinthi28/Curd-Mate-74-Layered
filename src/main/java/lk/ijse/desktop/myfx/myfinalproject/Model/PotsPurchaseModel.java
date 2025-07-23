package lk.ijse.desktop.myfx.myfinalproject.Model;

import lk.ijse.desktop.myfx.myfinalproject.Dto.PotsPurchaseDto;
import lk.ijse.desktop.myfx.myfinalproject.Util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PotsPurchaseModel {

    public static boolean updatePotsPurchase(PotsPurchaseDto potsPurchaseDto) throws SQLException {
        return CrudUtil.execute(
                "update Pots_Purchase_ID set Pots_Size = ?, Purchase_Date = ?, Quantity = ?, Unit_Price = ? where Purchase_ID = ?",
                potsPurchaseDto.getPotsSize(),
                potsPurchaseDto.getDate(),
                potsPurchaseDto.getQuantity(),
                potsPurchaseDto.getPrice(),
                potsPurchaseDto.getPurchaseId()
        );
    }

    public static ArrayList<Integer> getAllPotsSize()throws SQLException {
        ResultSet rs = CrudUtil.execute("select Pots_Size from Pots_Inventory");
        ArrayList<Integer> potsSize = new ArrayList<>();
        while (rs.next()) {
            Integer potsSizeInt = rs.getInt(1);
            potsSize.add(potsSizeInt);
        }
        return potsSize;
    }


    public String getNextId() throws SQLException {
        ResultSet resultSet = CrudUtil.execute("select Purchase_ID from Pots_Purchase_ID order by Purchase_ID desc limit 1");
        String prefix = "PP";
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

    public ArrayList<PotsPurchaseDto> viewAllPotsPurchase() throws SQLException {
        ResultSet rs = CrudUtil.execute("SELECT * FROM Pots_Purchase_ID");
        ArrayList<PotsPurchaseDto> potsPurchase = new ArrayList<>();
        while (rs.next()) {
            PotsPurchaseDto potsPurchaseDto = new PotsPurchaseDto(
                    rs.getString("Purchase_ID"),
                    rs.getInt("Pots_Size"),
                    rs.getString("Purchase_Date"),
                    rs.getInt("Quantity"),
                    rs.getDouble("Unit_Price")
            );
            potsPurchase.add(potsPurchaseDto);
        }
        return potsPurchase;
    }
    public boolean savePotsPurchase(PotsPurchaseDto potsPurchaseDto) throws SQLException {
        return CrudUtil.execute(
                "insert into Pots_Purchase_ID values (?,?,?,?,?)",
                potsPurchaseDto.getPurchaseId(),
                potsPurchaseDto.getPotsSize(),
                potsPurchaseDto.getDate(),
                potsPurchaseDto.getQuantity(),
                potsPurchaseDto.getPrice()
        );
    }

    public boolean deletePotsPurchase(PotsPurchaseDto potsPurchaseDto) throws SQLException {
        String sql = "delete from Pots_Purchase_ID where Purchase_ID=?";
        return CrudUtil.execute(sql,potsPurchaseDto.getPurchaseId());
    }
}
