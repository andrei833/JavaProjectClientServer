package repository;

import JDBC.JDBCUtils;
import model.Registration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RegistrationRepository implements IRepo<Integer, Registration> {
    private static final Logger logger = LogManager.getLogger(RegistrationRepository.class);
    private final JDBCUtils dbUtils;

    public RegistrationRepository() {
        dbUtils = new JDBCUtils();
    }

    @Override
    public int size() {
        logger.traceEntry("Getting size of the Registration table");
        try (Connection conn = dbUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) as SIZE FROM Registrations");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("SIZE");
            }
        } catch (SQLException e) {
            logger.error("Error db: {}", e.getMessage());
            System.out.println("Error db" + e.getMessage());
        }
        return 0;
    }

    @Override
    public void add(Registration elem) {
        logger.traceEntry("Adding registration: {}", elem);
        try (Connection conn = dbUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO Registrations (id, participantId, raceId) VALUES (?, ?, ?)")) {
            ps.setInt(1, elem.getId());
            ps.setInt(2, elem.getParticipantId());
            ps.setInt(3, elem.getRaceId());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error db: {}", e.getMessage());
            System.out.println("Error db" + e.getMessage());
        }
    }

    @Override
    public void delete(Registration elem) {
        logger.traceEntry("Deleting registration: {}", elem);
        try (Connection conn = dbUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM Registrations WHERE id = ?")) {
            ps.setInt(1, elem.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error db: {}", e.getMessage());
            System.out.println("Error db" + e.getMessage());
        }
    }

    @Override
    public void update(Integer id, Registration elem) {
        logger.traceEntry("Updating registration with id: {}", id);
        try (Connection conn = dbUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE Registrations SET participantId = ?, raceId = ? WHERE id = ?")) {
            ps.setInt(1, elem.getParticipantId());
            ps.setInt(2, elem.getRaceId());
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error db: {}", e.getMessage());
            System.out.println("Error db" + e.getMessage());
        }
    }

    @Override
    public Registration findById(Integer id) {
        logger.traceEntry("Finding registration with id: {}", id);
        try (Connection conn = dbUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Registrations WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Registration(rs.getInt("id"), rs.getInt("participantId"), rs.getInt("raceId"));
                }
            }
        } catch (SQLException e) {
            logger.error("Error db: {}", e.getMessage());
            System.out.println("Error db" + e.getMessage());
        }
        return null;
    }

    @Override
    public Iterable<Registration> findAll() {
        logger.traceEntry("Finding all registrations");
        List<Registration> registrations = new ArrayList<>();
        try (Connection conn = dbUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Registrations");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                registrations.add(new Registration(rs.getInt("id"), rs.getInt("participantId"), rs.getInt("raceId")));
            }
        } catch (SQLException e) {
            logger.error("Error db: {}", e.getMessage());
            System.out.println("Error db" + e.getMessage());
        }
        return registrations;
    }

    @Override
    public Collection<Registration> getAll() {
        return (Collection<Registration>) findAll();
    }
}
