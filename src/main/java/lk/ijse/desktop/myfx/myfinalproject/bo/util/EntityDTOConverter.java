package lk.ijse.desktop.myfx.myfinalproject.bo.util;

import lk.ijse.desktop.myfx.myfinalproject.Dto.*;
import lk.ijse.desktop.myfx.myfinalproject.entity.*;

public class EntityDTOConverter {

    public Customer getCustomer(CustomerDto dto) {
        return new Customer(
                dto.getCustomerId(),
                dto.getCustomerName(),
                dto.getAddress(),
                dto.getCustomerNumber()
        );
    }

    public CustomerDto getCustomerDTO(Customer customer) {
        return new CustomerDto(
                customer.getCustomerId(),
                customer.getCustomerName(),
                customer.getAddress(),
                customer.getCustomerNumber()
        );
    }

    public Buffalo getBuffalo(BuffaloDto dto) {
        return new Buffalo(
                dto.getBuffaloID(),
                dto.getMilkProduction(),
                dto.getGender(),
                dto.getAge(),
                dto.getHealthStatus()
        );
    }

    public BuffaloDto getBuffaloDTO(Buffalo buffalo) {
        return new BuffaloDto(
                buffalo.getBuffaloId(),
                buffalo.getMilkProduction(),
                buffalo.getGender(),
                buffalo.getAge(),
                buffalo.getHealthStatus()
        );
    }

    public CurdProduction getCurdProduction(CurdProductionDto dto) {
        return new CurdProduction(
                dto.getProductionId(),
                dto.getProductionDate(),
                dto.getExpiryDate(),
                dto.getQuantity(),
                dto.getPotsSize(),
                dto.getIngredients(),
                dto.getStorageId()
        );
    }

    public CurdProductionDto getCurdProductionDTO(CurdProduction curdProduction) {
        return new CurdProductionDto(
                curdProduction.getProductionId(),
                curdProduction.getProductionDate(),
                curdProduction.getExpiryDate(),
                curdProduction.getQuantity(),
                curdProduction.getPotsSize(),
                curdProduction.getIngredients(),
                curdProduction.getStorageId()
        );
    }

    public DailyIncome getDailyIncome(DailyIncomeDto dto) {
        return new DailyIncome(
                dto.getId(),
                dto.getCustomerName(),
                dto.getDate(),
                dto.getDescription(),
                dto.getAmount()
        );
    }

    public DailyIncomeDto getDailyIncomeDTO(DailyIncome dailyIncome) {
        return new DailyIncomeDto(
                dailyIncome.getId(),
                dailyIncome.getCustomerName(),
                dailyIncome.getDate(),
                dailyIncome.getDescription(),
                dailyIncome.getAmount()
        );
    }

    public DailyExpense getDailyExpense(DailyExpenseDto dto) {
        return new DailyExpense(
                dto.getId(),
                dto.getDate(),
                dto.getDescription(),
                dto.getAmount(),
                dto.isDailyExpense()
        );
    }

    public DailyExpenseDto getDailyExpenseDTO(DailyExpense dailyExpense) {
        return new DailyExpenseDto(
                dailyExpense.getId(),
                dailyExpense.getDate(),
                dailyExpense.getDescription(),
                dailyExpense.getAmount(),
                dailyExpense.isDailyExpense()
        );
    }

    public MilkCollection getMilkCollection(MilkCollectionDto dto) {
        return new MilkCollection(
                dto.getId(),
                dto.getDate(),
                dto.getQuantity(),
                dto.getBuffaloId()
        );
    }

    public MilkCollectionDto getMilkCollectionDTO(MilkCollection milkCollection) {
        return new MilkCollectionDto(
                milkCollection.getId(),
                milkCollection.getDate(),
                milkCollection.getQuantity(),
                milkCollection.getBuffaloId()
        );
    }

    public MilkStorage getMilkStorage(MilkStorageDto dto) {
        return new MilkStorage(
                dto.getStorageId(),
                dto.getCollectionId(),
                dto.getDate(),
                dto.getDuration(),
                dto.getTemperature()
        );
    }

    public MilkStorageDto getMilkStorageDTO(MilkStorage milkStorage) {
        return new MilkStorageDto(
                milkStorage.getStorageId(),
                milkStorage.getCollectionId(),
                milkStorage.getDate(),
                milkStorage.getDuration(),
                milkStorage.getTemperature()
        );
    }

    public Payment getPayment(PaymentDto dto) {
        return new Payment(
                dto.getPaymentId(),
                dto.getOrderId(),
                dto.getCustomerId(),
                dto.getDate(),
                dto.getPaymentMethod(),
                dto.getAmount()
        );
    }

    public PaymentDto getPaymentDTO(Payment payment) {
        return new PaymentDto(
                payment.getPaymentId(),
                payment.getOrderId(),
                payment.getCustomerId(),
                payment.getDate(),
                payment.getPaymentMethod(),
                payment.getAmount()
        );
    }

    public PotsInventory getPotsInventory(PotsInventoryDto dto) {
        return new PotsInventory(
                dto.getId(),
                dto.getQuantity(),
                dto.getPotsSize(),
                dto.getCondition()
        );
    }

    public PotsInventoryDto getPotsInventoryDTO(PotsInventory potsInventory) {
        return new PotsInventoryDto(
                potsInventory.getId(),
                potsInventory.getQuantity(),
                potsInventory.getPotsSize(),
                potsInventory.getCondition()
        );
    }

    public PotsPurchase getPotsPurchase(PotsPurchaseDto dto) {
        return new PotsPurchase(
                dto.getPurchaseId(),
                dto.getPotsSize(),
                dto.getDate(),
                dto.getQuantity(),
                dto.getPrice()
        );
    }

    public PotsPurchaseDto getPotsPurchaseDTO(PotsPurchase potsPurchase) {
        return new PotsPurchaseDto(
                potsPurchase.getPurchaseId(),
                potsPurchase.getPotsSize(),
                potsPurchase.getDate(),
                potsPurchase.getQuantity(),
                potsPurchase.getPrice()
        );
    }

    public QualityCheck getQualityCheck(QualityCheckDto dto) {
        return new QualityCheck(
                dto.getCheckId(),
                dto.getCollectionId(),
                dto.getAppearance(),
                dto.getFatContent(),
                dto.getTemperature(),
                dto.getDate(),
                dto.getNotes()
        );
    }

    public QualityCheckDto getQualityCheckDTO(QualityCheck qualityCheck) {
        return new QualityCheckDto(
                qualityCheck.getCheckId(),
                qualityCheck.getCollectionId(),
                qualityCheck.getAppearance(),
                qualityCheck.getFatContent(),
                qualityCheck.getTemperature(),
                qualityCheck.getDate(),
                qualityCheck.getNotes()
        );
    }

    public RawMaterialPurchase getRawMaterialPurchase(RawMaterialPurchaseDto dto) {
        return new RawMaterialPurchase(
                dto.getPurchaseId(),
                dto.getSupplierId(),
                dto.getMaterialName(),
                dto.getDate(),
                dto.getQuantity(),
                dto.getUnitPrice()
        );
    }

    public RawMaterialPurchaseDto getRawMaterialPurchaseDTO(RawMaterialPurchase rawMaterialPurchase) {
        return new RawMaterialPurchaseDto(
                rawMaterialPurchase.getPurchaseId(),
                rawMaterialPurchase.getSupplierId(),
                rawMaterialPurchase.getMaterialName(),
                rawMaterialPurchase.getDate(),
                rawMaterialPurchase.getQuantity(),
                rawMaterialPurchase.getUnitPrice()
        );
    }

    public Report getReport(ReportsDto dto) {
        return new Report(
                dto.getReportId(),
                dto.getDate(),
                dto.getUserId(),
                dto.getReportType(),
                dto.getGenerateBy()
        );
    }

    public ReportsDto getReportsDTO(Report report) {
        return new ReportsDto(
                report.getReportId(),
                report.getDate(),
                report.getUserId(),
                report.getReportType(),
                report.getGenerateBy()
        );
    }

    public Stock getStock(StockDto dto) {
        return new Stock(
                dto.getStockId(),
                dto.getProductionId(),
                dto.getDate(),
                dto.getQuantity(),
                dto.getStockType()
        );
    }

    public StockDto getStockDTO(Stock stock) {
        return new StockDto(
                stock.getStockId(),
                stock.getProductionId(),
                stock.getDate(),
                stock.getQuantity(),
                stock.getStockType()
        );
    }

    public Supplier getSupplier(SupplierDto dto) {
        return new Supplier(
                dto.getSupplierId(),
                dto.getSupplierName(),
                dto.getContactNumber(),
                dto.getAddress()
        );
    }

    public SupplierDto getSupplierDTO(Supplier supplier) {
        return new SupplierDto(
                supplier.getSupplierId(),
                supplier.getSupplierName(),
                supplier.getContactNumber(),
                supplier.getAddress()
        );
    }

    public User getUser(UserDto dto) {
        return new User(
                dto.getId(),
                dto.getUserName(),
                dto.getPassword(),
                dto.getEmail()
        );
    }

    public UserDto getUserDTO(User user) {
        return new UserDto(
                user.getId(),
                user.getUserName(),
                user.getPassword(),
                user.getEmail()
        );
    }

    public Order getOrder(OrderDto dto) {
        return new Order(
                dto.getOrderId(),
                dto.getCustomerId(),
                dto.getDate()
        );
    }

    public OrderDto getOrderDTO(Order entity) {
        return new OrderDto(
                entity.getOrderId(),
                entity.getCustomerId(),
                entity.getOrderDate()
        );
    }

    public OrderDetails getOrderDetails(OrderDetailsDto dto) {
        return new OrderDetails(
                dto.getOrderId(),
                dto.getProductionId(),
                dto.getQuantity(),
                dto.getUnitPrice()
        );
    }

    public OrderDetailsDto getOrderDetailsDTO(OrderDetails entity) {
        return new OrderDetailsDto(
                entity.getOrderId(),
                entity.getProductionId(),
                entity.getQuantity(),
                entity.getUnitPrice(),
                entity.getQuantity() * entity.getUnitPrice()
        );
    }
}