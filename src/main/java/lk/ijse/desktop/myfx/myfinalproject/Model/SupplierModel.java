package lk.ijse.desktop.myfx.myfinalproject.Model;

import lk.ijse.desktop.myfx.myfinalproject.Dto.SupplierDto;
import lk.ijse.desktop.myfx.myfinalproject.Util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SupplierModel {

    public static boolean updateSupplier(SupplierDto supplierDto) throws SQLException {
        return CrudUtil.execute(
                "update Supplier set Supplier_Name = ?, ContactNumber = ?, Address = ? where Supplier_ID = ?",
                supplierDto.getSupplierName(),
                supplierDto.getContactNumber(),
                supplierDto.getAddress(),
                supplierDto.getSupplierId()
        );
    }

    public String getNextId() throws SQLException {
        ResultSet resultSet = CrudUtil.execute("select Supplier_ID from Supplier order by Supplier_ID desc limit 1");
        String prefix = "SI";
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

    public ArrayList<SupplierDto> viewAllSupplier() throws ClassNotFoundException, SQLException {
        ResultSet rs = CrudUtil.execute("SELECT * FROM Supplier");
        ArrayList<SupplierDto> viewSupplier = new ArrayList<>();
        while (rs.next()) {
            SupplierDto supplierDto = new SupplierDto(
                    rs.getString("Supplier_ID"),
                    rs.getString("Supplier_Name"),
                    rs.getString("ContactNumber"),
                    rs.getString("Address")
            );
            viewSupplier.add(supplierDto);
        }
        return viewSupplier;
    }
    public boolean saveSupplier(SupplierDto supplierDto) throws ClassNotFoundException, SQLException {
        return CrudUtil.execute(
                "insert into Supplier values (?,?,?,?)",
                supplierDto.getSupplierId(),
                supplierDto.getSupplierName(),
                supplierDto.getContactNumber(),
                supplierDto.getAddress()
        );
    }

    public boolean deleteSupplier(SupplierDto supplierDto) throws SQLException{
        String sql = "delete from Supplier where Supplier_ID=?";
        return CrudUtil.execute(sql,supplierDto.getSupplierId());
    }
}
