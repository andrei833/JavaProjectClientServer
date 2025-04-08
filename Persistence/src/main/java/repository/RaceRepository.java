package repository;

import JDBC.JDBCUtils;
import model.Race;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RaceRepository implements IRepo<Integer, Race> {
    private static final Logger logger = LogManager.getLogger(RaceRepository.class);
    private final JDBCUtils jdbcUtils;

    public RaceRepository() {
        jdbcUtils = new JDBCUtils();
    }

    @Override
    public int size() {
        logger.traceEntry("Getting size of the Race table");
        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) as SIZE FROM Races");
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
    public void add(Race elem) {
        logger.traceEntry("Adding race: {}", elem);
        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO Races (id, distance, style, numberOfParticipants) VALUES (?, ?, ?, ?)")) {
            ps.setInt(1, elem.getId());
            ps.setInt(2, elem.getDistance());
            ps.setString(3, elem.getStyle());
            ps.setInt(4, elem.getNumberOfParticipants());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error db: {}", e.getMessage());
            System.out.println("Error db" + e.getMessage());
        }
    }

    @Override
    public void delete(Race elem) {
        logger.traceEntry("Deleting race: {}", elem);
        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM Races WHERE id = ?")) {
            ps.setInt(1, elem.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error db: {}", e.getMessage());
            System.out.println("Error db" + e.getMessage());
        }
    }

    @Override
    public void update(Integer id, Race elem) {
        logger.traceEntry("Updating race with id: {}", id);
        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE Races SET distance = ?, style = ?, numberOfParticipants = ? WHERE id = ?")) {
            ps.setInt(1, elem.getDistance());
            ps.setString(2, elem.getStyle());
            ps.setInt(3, elem.getNumberOfParticipants());
            ps.setInt(4, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error db: {}", e.getMessage());
            System.out.println("Error db" + e.getMessage());
        }
    }

    @Override
    public Race findById(Integer id) {
        logger.traceEntry("Finding race with id: {}", id);
        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Races WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Race(rs.getInt("id"), rs.getInt("distance"), rs.getString("style"), rs.getInt("numberOfParticipants"));
                }
            }
        } catch (SQLException e) {
            logger.error("Error db: {}", e.getMessage());
            System.out.println("Error db" + e.getMessage());
        }
        return null;
    }

    @Override
    public Iterable<Race> findAll() {
        logger.traceEntry("Finding all races");
        List<Race> races = new ArrayList<>();
        try (Connection conn = jdbcUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Races");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                races.add(new Race(rs.getInt("id"), rs.getInt("distance"), rs.getString("style"), rs.getInt("numberOfParticipants")));
            }
        } catch (SQLException e) {
            logger.error("Error db: {}", e.getMessage());
            System.out.println("Error db" + e.getMessage());
        }
        return races;
    }

    @Override
    public Collection<Race> getAll() {
        return (Collection<Race>) findAll();
    }
}