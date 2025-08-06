package lk.ijse.desktop.myfx.myfinalproject.Model;

import lk.ijse.desktop.myfx.myfinalproject.Dto.CurdProductionDto;
import lk.ijse.desktop.myfx.myfinalproject.Dto.OrderDetailsDto;
import lk.ijse.desktop.myfx.myfinalproject.Util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

public class CurdProductionModel {

    public static boolean updateCurdProduction(CurdProductionDto curdProductionDto) throws SQLException {
        return CrudUtil.execute(
                "update Curd_Production set Production_Date = ?, Expiry_Date = ?, Quantity = ?, Pots_Size = ?, Ingredients = ?, Storage_ID = ? where Production_ID = ?",
                curdProductionDto.getProductionDate(),
                curdProductionDto.getExpiryDate(),
                curdProductionDto.getQuantity(),
                curdProductionDto.getPotsSize(),
                curdProductionDto.getIngredients(),
                curdProductionDto.getStorageId(),
                curdProductionDto.getProductionId()
        );
    }

    public static ArrayList<Integer> getAllPotsSize() throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT Pots_Size FROM Pots_Inventory");
        HashSet<Integer> uniquePotsSize = new HashSet<>();
        while (rst.next()) {
            Integer pots = rst.getInt(1);
            uniquePotsSize.add(pots);
        }
        return new ArrayList<>(uniquePotsSize);
    }

    public static ArrayList<String> getAllStorageId() throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT Storage_ID FROM Milk_Storage");
        HashSet<String> storageId = new HashSet<>();
        while (rst.next()) {
            String id = rst.getString(1);
            storageId.add(id);
        }
        return new ArrayList<>(storageId);
    }

    public static int findpotById(String selectedProductionId) throws SQLException {
        ResultSet rst = CrudUtil.execute("select Pots_Size from Curd_Production where Production_ID =?",selectedProductionId);
        if(rst.next()) {
            return rst.getInt("Pots_Size");
        }else {
            return -1;
        }
    }

    public static ArrayList<String> getAllProductionIds() throws SQLException {
        ResultSet rst = CrudUtil.execute("select Production_ID from Curd_Production");
        ArrayList<String> list = new ArrayList<>();
        while (rst.next()) {
            String productionId = rst.getString(1);
            list.add(productionId);
        }
        return list;
    }

//    public static CurdProductionDto findById(String selectedItemId) throws SQLException {
//        ResultSet rst = CrudUtil.execute("select * from Curd_Production where Production_ID = ?",selectedItemId);
//        if(rst.next()) {
//            return new CurdProductionDto(
//                    rst.getString(1),
//                    rst.getString(2),
//                    rst.getString(3),
//                    rst.getInt(4),
//                    rst.getInt(5),
//                    rst.getString(6),
//                    rst.getString(7)
//            );
//        }
//        return null;
//    }

    public static boolean reduceQty(OrderDetailsDto orderDetailsDto) throws SQLException{
        return CrudUtil.execute(
                "update Curd_Production set Quantity = Quantity -? where Production_ID = ?",
                orderDetailsDto.getQuantity(),
                orderDetailsDto.getProductionId()
        );
    }

    public String getNextId() throws SQLException {
        ResultSet resultSet = CrudUtil.execute("select Production_ID from Curd_Production order by Production_ID desc limit 1");
        String prefix = "CP";
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

//    public ArrayList<CurdProductionDto> viewAllCurdProduction() throws ClassNotFoundException, SQLException{
//        ResultSet rs = CrudUtil.execute("SELECT * FROM Curd_Production");
//        ArrayList<CurdProductionDto> viewCurdProduction = new ArrayList<>();
//        while(rs.next()){
//            CurdProductionDto curdProductionDto = new CurdProductionDto(
//                    rs.getString("Production_ID"),
//                    rs.getString("Production_Date"),
//                    rs.getString("Expiry_Date"),
//                    rs.getInt("Quantity"),
//                    rs.getInt("Pots_Size"),
//                    rs.getString("Ingredients"),
//                    rs.getString("Storage_ID")
//            );
//            viewCurdProduction.add(curdProductionDto);
//        }
//        return viewCurdProduction;
//    }
    public static boolean saveCurdProduction(CurdProductionDto curdProductionDto) throws ClassNotFoundException, SQLException{
        return CrudUtil.execute(
                "insert into Curd_Production values (?,?,?,?,?,?,?)",
                curdProductionDto.getProductionId(),
                curdProductionDto.getProductionDate(),
                curdProductionDto.getExpiryDate(),
                curdProductionDto.getQuantity(),
                curdProductionDto.getPotsSize(),
                curdProductionDto.getIngredients(),
                curdProductionDto.getStorageId()
        );
    }

    public boolean deleteCurdProduction(CurdProductionDto curdProductionDto) throws SQLException{
        String sql = "delete from Curd_Production where Production_ID=?";
        return CrudUtil.execute(sql, curdProductionDto.getProductionId());
    }
}
