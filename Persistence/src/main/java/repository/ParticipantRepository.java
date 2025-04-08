package repository;

import JDBC.JDBCUtils;
import model.Participant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ParticipantRepository implements IRepo<Integer, Participant> {
    private static final Logger logger = LogManager.getLogger(ParticipantRepository.class);
    private final JDBCUtils dbUtils;

    public ParticipantRepository() {
        dbUtils = new JDBCUtils();
    }

    @Override
    public int size() {
        logger.traceEntry("Getting size of the Participant table");
        try (Connection conn = dbUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) as SIZE FROM participants");
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
    public void add(Participant elem) {
        logger.traceEntry("Adding participant: {}", elem);
        try (Connection conn = dbUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO Participants (id, name, age) VALUES (?, ?, ?)")) {
            ps.setInt(1, elem.getId());
            ps.setString(2, elem.getName());
            ps.setInt(3, elem.getAge());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error db: {}", e.getMessage());
            System.out.println("Error db" + e.getMessage());
        }
    }

    @Override
    public void delete(Participant elem) {
        logger.traceEntry("Deleting participant: {}", elem);
        try (Connection conn = dbUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM Participants WHERE id = ?")) {
            ps.setInt(1, elem.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error db: {}", e.getMessage());
            System.out.println("Error db" + e.getMessage());
        }
    }

    @Override
    public void update(Integer id, Participant elem) {
        logger.traceEntry("Updating participant with id: {}", id);
        try (Connection conn = dbUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE Participants SET name = ?, age = ? WHERE id = ?")) {
            ps.setString(1, elem.getName());
            ps.setInt(2, elem.getAge());
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error db: {}", e.getMessage());
            System.out.println("Error db" + e.getMessage());
        }
    }

    @Override
    public Participant findById(Integer id) {
        logger.traceEntry("Finding participant with id: {}", id);
        try (Connection conn = dbUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Participants WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Participant(rs.getInt("id"), rs.getString("name"), rs.getInt("age"));
                }
            }
        } catch (SQLException e) {
            logger.error("Error db: {}", e.getMessage());
            System.out.println("Error db" + e.getMessage());
        }
        return null;
    }

    @Override
    public Iterable<Participant> findAll() {
        logger.traceEntry("Finding all participants");
        List<Participant> participants = new ArrayList<>();
        try (Connection conn = dbUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Participants");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                participants.add(new Participant(rs.getInt("id"), rs.getString("name"), rs.getInt("age")));
            }
        } catch (SQLException e) {
            logger.error("Error db: {}", e.getMessage());
            System.out.println("Error db" + e.getMessage());
        }
        return participants;
    }

    @Override
    public Collection<Participant> getAll() {
        return (Collection<Participant>) findAll();
    }
}
