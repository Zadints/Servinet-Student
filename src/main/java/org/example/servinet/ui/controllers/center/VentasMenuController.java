package org.example.servinet.ui.controllers.center;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.servinet.ui.controllers.IndexController;
import org.example.servinet.infrastructure.database.config.LoadDb;

public class VentasMenuController {

    private IndexController indexController;
    @FXML private Label lblMonthlySales;
    @FXML private Label lblWeeklySales;   // Para mostrar "X ventas" de la semana
    @FXML private Label lblMonthlyContracts;  // Para mostrar "X contratos" del mes
    @FXML private Label lblConversionRate;
    @FXML private Label lblTodaySales;
    @FXML private Label lblMonthlyCommission;
    @FXML
    public void initialize() {
        cargarMetricasVentas();
    }

    private void cargarMetricasVentas() {
        // Consultas exclusivas para métricas
        String sqlMes = "SELECT COUNT(p.payment_id) AS total_ventas, ISNULL(SUM(p.payment_amount), 0) AS monto_total " +
                "FROM payments p " +
                "WHERE MONTH(p.payment_date_paid) = MONTH(GETDATE()) " +
                "  AND YEAR(p.payment_date_paid) = YEAR(GETDATE())";
        // Ventas de esta semana (usando la fecha de creación de la orden o pago)
        String sqlSemana = "SELECT COUNT(order_id) FROM orders WHERE order_create >= DATEADD(week, DATEDIFF(week, 0, GETDATE()), 0)";
        String sqlHoy = "SELECT COUNT(payment_id) FROM payments WHERE CAST(payment_date_paid AS DATE) = CAST(GETDATE() AS DATE)";

        String sqlTotalClientes = "SELECT COUNT(*) FROM clients";
        String sqlTotalOrdenes = "SELECT COUNT(*) FROM orders";
        // Usamos la conexión centralizada
        try (Connection conn = LoadDb.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sqlSemana);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int totalSemana = rs.getInt(1);
                    if (lblWeeklySales != null) {
                        lblWeeklySales.setText(totalSemana + " ventas");
                    }
                }
            }
            try (PreparedStatement stmt = conn.prepareStatement(sqlHoy);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int totalHoy = rs.getInt(1);
                    if (lblTodaySales != null) {
                        lblTodaySales.setText(totalHoy + " ventas"); // O solo String.valueOf(totalHoy) según tu diseño
                    }
                }
            }
            // 4. Contratos de este Mes
            try (PreparedStatement stmt = conn.prepareStatement(sqlMes);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int contratosMes = rs.getInt(1);
                    double montoTotal = rs.getDouble("monto_total");
                    if (lblMonthlyContracts != null) {
                        lblMonthlyContracts.setText(contratosMes + " contratos");
                        lblMonthlySales.setText(contratosMes + " contratos");
                        lblMonthlyCommission.setText(String.format("S/ %.2f", montoTotal * 0.10));
                    }
                }
            }

            // 5. Cálculo de Tasa de Conversión
            try (PreparedStatement stmtClientes = conn.prepareStatement(sqlTotalClientes);
                 ResultSet rsClientes = stmtClientes.executeQuery();
                 PreparedStatement stmtOrdenes = conn.prepareStatement(sqlTotalOrdenes);
                 ResultSet rsOrdenes = stmtOrdenes.executeQuery()) {

                int totalClientes = rsClientes.next() ? rsClientes.getInt(1) : 0;
                int totalOrdenes = rsOrdenes.next() ? rsOrdenes.getInt(1) : 0;

                if (totalClientes > 0) {
                    double conversion = ((double) totalOrdenes / totalClientes) * 100;
                    if (lblConversionRate != null) {
                        lblConversionRate.setText(String.format("%.1f %%", conversion));
                    }
                } else {
                    if (lblConversionRate != null) {
                        lblConversionRate.setText("0 %");
                    }
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setIndexController(IndexController indexController) {
        this.indexController = indexController;
    }

    @FXML
    protected void newContract(){
        indexController.renderizarFxml("sell-form.fxml");
    }

    @FXML
    protected void showSelling() {
        if (indexController != null) {
            indexController.renderizarFxml("sell-pays.fxml");
        }
    }

    @FXML
    protected void showOffers() {
        if (indexController != null) {
            indexController.renderizarFxml("ofertas.fxml");
        }
    }

    @FXML
    protected void showContracts() {
        if (indexController != null) {
            indexController.renderizarFxml("contracts.fxml");
        }
    }

    @FXML
    protected void selectOffer() {
        if (indexController != null) {
            indexController.renderizarFxml("sell-form.fxml");
        }
    }

    @FXML
    protected void editContract() {
        if (indexController != null) {
            indexController.renderizarFxml("editar-contrato.fxml");
        }
    }

    @FXML
    protected void showStatistics() {
        if (indexController != null) {
            indexController.renderizarFxml("estadisticas-ventas.fxml");
        }
    }

    @FXML
    protected void showProspects() {
        if (indexController != null) {
            indexController.renderizarFxml("prospectos.fxml");
        }
    }
}