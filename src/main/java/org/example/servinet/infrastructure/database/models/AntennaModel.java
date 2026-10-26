package org.example.servinet.infrastructure.database.models;

import org.example.servinet.core.domain.entities.Antenna;
import org.example.servinet.core.domain.enums.antenna.StatusAntenna;
import org.example.servinet.core.domain.exception.DatabaseException;
import org.example.servinet.core.domain.utils.ImageConverter;
import org.example.servinet.infrastructure.database.config.LoadDb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AntennaModel {

    public static void deleteAntennaDatabase(Antenna ant) {

        String sql = """
        DELETE FROM antennas
        WHERE uuid = ?
        """;

        Connection conn = LoadDb.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ant.getUuid());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException(
                    "No se pudo eliminar la antena " + ant.getUuid(), e
            );
        }
    }
    //Admin
//Cesar2014abc.
    public static List<Antenna> getAllAntennaDatabase() {

        String sql = """
        SELECT *
        FROM antennas
        """;

        Connection conn = LoadDb.getConnection();
        List<Antenna> antennas = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Antenna antenna = new Antenna(
                        rs.getString("uuid"),
                        rs.getShort("priority"),
                        rs.getString("name"),
                        rs.getBoolean("for_repair"),
                        rs.getBoolean("for_maintenance"),
                        rs.getTimestamp("date_last_maintenance").toLocalDateTime(),
                        rs.getInt("count_days_on"),
                        rs.getInt("count_days_off"),
                        ImageConverter.toImage(rs.getBytes("image")),
                        StatusAntenna.valueOf(rs.getString("status")),
                        rs.getTimestamp("date_create").toLocalDateTime()
                );

                antennas.add(antenna);
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                    "No se pudieron obtener las antenas",
                    e
            );
        }

        return antennas;
    }

    public static void setAntennaDatabase(Antenna ant) {

        String sql = """
    INSERT INTO antennas (
         uuid,
         priority,
         name,
         for_repair,
         for_maintenance,
         date_create,
         date_last_maintenance,
         count_days_on,
         count_days_off,
         image,
         status
     )
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
    """;

        Connection conn = LoadDb.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ant.getUuid());
            stmt.setShort(2, ant.getPriority());
            stmt.setString(3, ant.getName());
            stmt.setBoolean(4, ant.isForReair()); // Asegúrate si tu getter es isForRepair o isForReair
            stmt.setBoolean(5, ant.isForMaintenance());
            stmt.setTimestamp(6, ant.getDateCreate() != null ? Timestamp.valueOf(ant.getDateCreate()) : null);
            stmt.setTimestamp(7, ant.getDateLastMaintenance() != null ? Timestamp.valueOf(ant.getDateLastMaintenance()) : null);
            stmt.setInt(8, ant.getCountDaysOn());
            stmt.setInt(9, ant.getCountDaysOff());

            // Convertir la imagen a bytes si existe (asumiendo que tienes un método auxiliar o de conversión)
            // Si ant.getImage() devuelve Image de JavaFX, usa ImageConverter.toBytes(...) o ajústalo según tu lógica
            stmt.setBytes(10, ImageConverter.toBytes(ant.getImage()));

            stmt.setString(11, ant.getStatus() != null ? ant.getStatus().name() : "ACTIVE");

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Error al guardar la antena en la base de datos", e
            );
        }
    }
}
