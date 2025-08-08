package lk.ijse.desktop.myfx.myfinalproject.bo.custom.impl;

import lk.ijse.desktop.myfx.myfinalproject.Dto.ReportsDto;
import lk.ijse.desktop.myfx.myfinalproject.bo.custom.ReportBO;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.DuplicateException;
import lk.ijse.desktop.myfx.myfinalproject.bo.exception.NotFoundException;
import lk.ijse.desktop.myfx.myfinalproject.bo.util.EntityDTOConverter;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOFactory;
import lk.ijse.desktop.myfx.myfinalproject.dao.DAOTypes;
import lk.ijse.desktop.myfx.myfinalproject.dao.custom.ReportDAO;
import lk.ijse.desktop.myfx.myfinalproject.entity.Report;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReportBOImpl implements ReportBO {
    private final ReportDAO reportDAO = DAOFactory.getInstance().getDAO(DAOTypes.REPORT);
    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public List<ReportsDto> getAllReports() throws SQLException {
        List<Report> entities = reportDAO.getAll();
        List<ReportsDto> dtos = new ArrayList<>();
        for (Report entity : entities) {
            dtos.add(converter.getReportsDTO(entity));
        }
        return dtos;
    }

    @Override
    public void saveReport(ReportsDto dto) throws DuplicateException, SQLException {
        Optional<Report> existingReport = reportDAO.findById(dto.getReportId());
        if (existingReport.isPresent()) {
            throw new DuplicateException("Report with ID " + dto.getReportId() + " already exists.");
        }
        Report entity = converter.getReport(dto);
        boolean saved = reportDAO.save(entity);
        if (!saved) {
            throw new SQLException("Failed to save report record.");
        }
    }

    @Override
    public void updateReport(ReportsDto dto) throws NotFoundException, SQLException {
        Optional<Report> existingReport = reportDAO.findById(dto.getReportId());
        if (existingReport.isEmpty()) {
            throw new NotFoundException("Report with ID " + dto.getReportId() + " not found.");
        }
        Report entity = converter.getReport(dto);
        boolean updated = reportDAO.update(entity);
        if (!updated) {
            throw new SQLException("Failed to update report record.");
        }
    }

    @Override
    public boolean deleteReport(String id) throws NotFoundException, SQLException {
        Optional<Report> existingReport = reportDAO.findById(id);
        if (existingReport.isEmpty()) {
            throw new NotFoundException("Report with ID " + id + " not found.");
        }
        return reportDAO.delete(id);
    }

    @Override
    public String getNextReportId() throws SQLException {
        return reportDAO.getNextId();
    }

    @Override
    public ReportsDto findReportById(String id) throws SQLException {
        Optional<Report> optionalReport = reportDAO.findById(id);
        return optionalReport.map(converter::getReportsDTO).orElse(null);
    }

    @Override
    public List<String> getAllUserIds() throws SQLException {
        return reportDAO.getAllUserIds();
    }
}