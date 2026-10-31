package org.example.servinet.ui.controllers.center;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.servinet.infrastructure.database.config.LoadDb;
import org.example.servinet.ui.controllers.IndexController;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

public class RegistrarPagoController {

    private IndexController indexController;
    private ObservableList<String> listaClientes = FXCollections.observableArrayList();
    private FilteredList<String> filteredClientes;

    // --- Controles FXML ---
    @FXML private ComboBox<String> cmbBuscarCliente;
    @FXML private ComboBox<String> cmbMetodoPago;

    @FXML private Label lblDniClient;
    @FXML private Label lblNombreClient;
    @FXML private Label lblTelefonoClient;
    @FXML private Label lblDireccionClient;
    @FXML private Label lblEmailClient;

    @FXML private DatePicker dpFechaPago;
    @FXML private TextArea txtObservacion;
    @FXML private Label lblNombreComprobante;
    @FXML private ImageView imgComprobante;

    private File selectedComprobanteFile;

    public void setIndexController(IndexController indexController) {
        this.indexController = indexController;
    }

    @FXML
    public void initialize() {
        System.out.println(">>> INICIALIZANDO REGISTRAR PAGO CONTROLLER <<<");
        cargarMetodosPago();
        cargarClientesParaBusqueda();

        // Inicializar la fecha de pago con el día actual por defecto
        if (dpFechaPago != null) {
            dpFechaPago.setValue(LocalDate.now());
        }

        // Búsqueda predictiva y en tiempo real con FilteredList
        if (cmbBuscarCliente != null) {
            cmbBuscarCliente.setEditable(true);

            filteredClientes = new FilteredList<>(listaClientes, b -> true);
            cmbBuscarCliente.setItems(filteredClientes);

            cmbBuscarCliente.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
                Platform.runLater(() -> {
                    String filtro = (newValue == null) ? "" : newValue.trim().toLowerCase();

                    filteredClientes.setPredicate(clienteStr -> {
                        if (filtro.isEmpty()) {
                            return true;
                        }
                        return clienteStr.toLowerCase().contains(filtro);
                    });

                    if (!filtro.isEmpty() && !filteredClientes.isEmpty() && !cmbBuscarCliente.isShowing()) {
                        cmbBuscarCliente.show();
                    }
                });
            });

            cmbBuscarCliente.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.isEmpty()) {
                    seleccionarClientePorFiltro(newVal);
                }
            });
        }
    }

    private void cargarMetodosPago() {
        if (cmbMetodoPago != null && cmbMetodoPago.getItems().isEmpty()) {
            cmbMetodoPago.getItems().addAll("EFECTIVO", "YAPE", "PLIN", "TRANSFERENCIA");
        }
    }

    private void cargarClientesParaBusqueda() {
        listaClientes.clear();
        String sql = "SELECT client_dni, client_name, client_last_name, client_address FROM clients WHERE client_status = 'ACTIVO'";

        try (Connection conn = LoadDb.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String dni = rs.getString("client_dni");
                String nombre = rs.getString("client_name");
                String apellido = rs.getString("client_last_name");
                String direccion = rs.getString("client_address");

                String item = dni + " - " + nombre + " " + apellido + " (" + direccion + ")";
                listaClientes.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void seleccionarClientePorFiltro(String seleccion) {
        if (seleccion == null || !seleccion.contains(" - ")) return;

        String dni = seleccion.split(" - ")[0];

        String sql = "SELECT * FROM clients WHERE client_dni = ?";
        try (Connection conn = LoadDb.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dni);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    if (lblDniClient != null) lblDniClient.setText(rs.getString("client_dni"));
                    if (lblNombreClient != null) lblNombreClient.setText(rs.getString("client_name") + " " + rs.getString("client_last_name"));
                    if (lblTelefonoClient != null) lblTelefonoClient.setText(rs.getString("client_phone"));
                    if (lblDireccionClient != null) lblDireccionClient.setText(rs.getString("client_address"));
                    if (lblEmailClient != null) lblEmailClient.setText(rs.getString("client_email"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void seleccionarComprobante() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Comprobante de Pago");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos de Imagen", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            this.selectedComprobanteFile = file;

            if (lblNombreComprobante != null) {
                lblNombreComprobante.setText("Comprobante: " + file.getName());
            }

            if (imgComprobante != null) {
                try {
                    imgComprobante.setImage(new Image(file.toURI().toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    public void seleccionarImagen() {
        seleccionarComprobante();
    }

    @FXML
    public void closeForm() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar salida");
        alert.setHeaderText(null);
        alert.setContentText("¿Estás seguro de que quieres salir?");

        alert.getButtonTypes().setAll(
                new ButtonType("Sí, salir", ButtonBar.ButtonData.OK_DONE),
                new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE)
        );

        alert.showAndWait().ifPresent(response -> {
            if (response.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                if (indexController != null) {
                    indexController.renderizarFxml("sell.fxml");
                }
            }
        });
    }

    @FXML
    public void cancelForm() {
        if (indexController != null) {
            indexController.renderizarFxml("sell.fxml");
        }
    }



    @FXML
    public void registrarPagoAccion() {
        System.out.println(">>> REGISTRANDO PAGO INDEPENDIENTE <<<");

        String dni = (lblDniClient != null) ? lblDniClient.getText().trim() : "";
        String metodo = (cmbMetodoPago != null) ? cmbMetodoPago.getValue() : null;
        String observacion = (txtObservacion != null && txtObservacion.getText() != null) ? txtObservacion.getText().trim() : "";
        LocalDate fechaPago = (dpFechaPago != null && dpFechaPago.getValue() != null) ? dpFechaPago.getValue() : LocalDate.now();

        // Validaciones
        if (dni.isEmpty() || dni.equals("-")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Cliente no seleccionado", "Por favor busca y selecciona un cliente válido.");
            return;
        }

        if (metodo == null || metodo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Método de pago requerido", "Por favor selecciona un método de pago.");
            return;
        }

        if (selectedComprobanteFile == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Comprobante requerido", "Por favor adjunta la imagen del comprobante de pago.");
            return;
        }

        try {
            UUID paymentId = UUID.randomUUID();
            BigDecimal montoEstandar = new BigDecimal("89.90");

            // 1. Crear el pago pendiente en la base de datos
            String sqlInsert = "EXEC sp_CreatePendingPayment @client_dni = ?, @payment_amount = ?, @payment_description = ?, @payment_reference = ?, @payment_id = ?";

            try (Connection conn = LoadDb.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {

                stmt.setString(1, dni);
                stmt.setBigDecimal(2, montoEstandar);
                stmt.setString(3, observacion.isEmpty() ? "Pago registrado independientemente" : observacion);
                stmt.setString(4, metodo);
                stmt.setObject(5, paymentId);
                stmt.executeUpdate();
            }

            // 2. Completar el pago con la imagen y la fecha seleccionada
            String sqlComplete = "UPDATE payments SET payment_date_paid = ?, payment_method = ?, payment_image = ?, payment_ispaid = 1 WHERE payment_id = ?";
            try (Connection conn = LoadDb.getConnection();
                 PreparedStatement stmtImg = conn.prepareStatement(sqlComplete);
                 FileInputStream fis = new FileInputStream(selectedComprobanteFile)) {

                stmtImg.setObject(1, fechaPago.atStartOfDay());
                stmtImg.setString(2, metodo);
                stmtImg.setBinaryStream(3, fis, (int) selectedComprobanteFile.length());
                stmtImg.setObject(4, paymentId);
                stmtImg.executeUpdate();
            }

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "El pago se ha registrado correctamente con fecha " + fechaPago + ".");

            // 4. Redirigir al menú de ventas al cerrar el modal
            if (indexController != null) {
                indexController.renderizarFxml("sell.fxml");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Ocurrió un fallo al registrar el pago: " + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String msj) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msj);
        alert.showAndWait();
    }
}