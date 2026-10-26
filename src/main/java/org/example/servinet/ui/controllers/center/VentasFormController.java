package org.example.servinet.ui.controllers.center;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.servinet.core.application.dto.ClientCreateDto;
import org.example.servinet.core.application.dto.SaleSumaryDto;
import org.example.servinet.core.application.usecase.ClientsUseCase;
import org.example.servinet.core.application.usecase.PaymentsUseCase;
import org.example.servinet.core.application.usecase.PlanUseCase;
import org.example.servinet.core.domain.entities.ClientPlan;
import org.example.servinet.core.domain.entities.Payment;
import org.example.servinet.ui.controllers.IndexController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;

public class VentasFormController {

    private IndexController indexController;

    // --- Controles FXML ---
    @FXML private ComboBox<ClientPlan> cmbPlan; // Corregido a ClientPlan
    @FXML private ComboBox<String> cmbMetodoPago;

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDni;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtEmail;

    @FXML private TextArea txtObservacion;
    @FXML private Label lblNombreComprobante;
    @FXML private ImageView imgComprobante;

    @FXML private Label lblResumenCliente;
    @FXML private Label lblResumenPlan;
    @FXML private Label lblResumenMonto;

    private File selectedComprobanteFile;
    // Declarar los fx:id de tus tarjetas en el FXML
    @FXML private Label lblMonthlySales;
    @FXML private Label lblTodaySales;
    @FXML private Label lblMonthlyCommission;
    @FXML private Label lblMonthlyGoal;

    // ¡IMPRESCINDIBLE LA ANOTACIÓN @FXML!
    @FXML
    public void initialize() {
        System.out.println(">>> INICIALIZANDO VENTASFORMCONTROLLER <<<");
        cargarDatosVentana();
        ClientsUseCase.loadClientsAndOrders();
        cargarResumenVentas();
    }
    private void cargarResumenVentas() {
        try {
            // 1. Obtener los valores reales
            long ventasMes = ClientsUseCase.contarVentasDelMes();
            long ventasHoy = ClientsUseCase.contarVentasDeHoy();

            // 2. Calcular comisión estimada (Ejemplo: S/ 15.00 por cada contrato del mes)
            double comisionPorVenta = 15.00;
            double comisionTotal = ventasMes * comisionPorVenta;

            // 3. Calcular objetivo mensual (Ejemplo: Meta fija de 20 contratos)
            int metaMensual = 20;
            double porcentajeProgreso = metaMensual > 0 ? ((double) ventasMes / metaMensual) * 100 : 0;
            if (porcentajeProgreso > 100) porcentajeProgreso = 100; // Tope máximo opcional

            // 4. Asignar los textos a los Labels de la interfaz
            if (lblMonthlySales != null) lblMonthlySales.setText(String.valueOf(ventasMes));
            if (lblTodaySales != null) lblTodaySales.setText(String.valueOf(ventasHoy));
            if (lblMonthlyCommission != null) lblMonthlyCommission.setText(String.format("S/ %.2f", comisionTotal));
            if (lblMonthlyGoal != null) lblMonthlyGoal.setText(String.format("%.0f %%", porcentajeProgreso));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setIndexController(IndexController indexController) {
        this.indexController = indexController;

    }

    private void cargarDatosVentana() {
        // 1. Cargar planes desde el pool hacia el ComboBox y unificar listeners
        if (cmbPlan != null) {
            cmbPlan.getItems().setAll(PlanUseCase.getPoolPlans());

            // Forzar celdas limpias y evitar el apilamiento de estilos CSS
            cmbPlan.setCellFactory(param -> new ListCell<ClientPlan>() {
                @Override
                protected void updateItem(ClientPlan item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        // Muestra una sola línea ordenada: Nombre, velocidad y precio
                        setText(item.getName() + " - " + item.getSpeed() + " Mbps (S/ " + String.format("%.2f", item.getPrice()) + ")");
                    }
                }
            });

            cmbPlan.setButtonCell(new ListCell<ClientPlan>() {
                @Override
                protected void updateItem(ClientPlan item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("Seleccionar plan");
                    } else {
                        setText(item.getName() + " - " + item.getSpeed() + " Mbps (S/ " + String.format("%.2f", item.getPrice()) + ")");
                    }
                }
            });

            // Listener unificado: actualiza el resumen y opcionalmente muestra la alerta de prueba
            cmbPlan.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    // Si la alerta era solo de prueba temporal, puedes comentarla o borrarla:
                /*
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Plan Seleccionado",
                        "Elegiste: " + newVal.getName() + " - S/ " + newVal.getPrice()
                );
                */

                    // Actualiza el resumen en tiempo real
                    actualizarResumen();
                }
            });
        }

        // 2. Poblar Métodos de Pago (si está vacío)
        if (cmbMetodoPago != null && cmbMetodoPago.getItems().isEmpty()) {
            cmbMetodoPago.getItems().addAll("EFECTIVO", "YAPE", "PLIN", "TRANSFERENCIA");
        }

        // 3. Restricción DNI (solo 8 números)
        if (txtDni != null) {
            txtDni.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    if (!newValue.matches("\\d*") || newValue.length() > 8) {
                        txtDni.setText(oldValue == null ? "" : oldValue);
                    }
                }
            });
        }
        if (txtTelefono != null) {
            txtTelefono.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    if (!newValue.matches("\\d*") || newValue.length() > 9) {
                        txtTelefono.setText(oldValue == null ? "" : oldValue);
                    }
                }
            });
        }

        // 4. Listener para actualizar el resumen cuando cambie el nombre
        if (txtNombre != null) {
            txtNombre.textProperty().addListener((obs, oldVal, newVal) -> actualizarResumen());
        }
    }

    private void actualizarResumen() {
        if (lblResumenCliente != null && txtNombre != null) {
            lblResumenCliente.setText(txtNombre.getText().trim().isEmpty() ? "Sin seleccionar" : txtNombre.getText().trim());
        }

        if (cmbPlan != null && cmbPlan.getValue() != null) {
            ClientPlan planSel = cmbPlan.getValue();

            if (lblResumenPlan != null) {
                lblResumenPlan.setText(planSel.getName());
            }

            if (lblResumenMonto != null) {
                Double precio = planSel.getPrice() != null ? planSel.getPrice() : 0.0;
                lblResumenMonto.setText(String.format("S/ %.2f", precio));
            }
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
                imgComprobante.setImage(new Image(file.toURI().toString()));
            }
        }
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
    public void registerSale() {
        System.out.println(">>> REGISTRANDO VENTA <<<");

        String nombre = (txtNombre != null && txtNombre.getText() != null) ? txtNombre.getText().trim() : "";
        String apellido = (txtApellido != null && txtApellido.getText() != null) ? txtApellido.getText().trim() : "";
        String dni = (txtDni != null && txtDni.getText() != null) ? txtDni.getText().trim() : "";
        String telefono = (txtTelefono != null && txtTelefono.getText() != null) ? txtTelefono.getText().trim() : "";
        String email = (txtEmail != null && txtEmail.getText() != null) ? txtEmail.getText().trim() : "";
        String direccion = (txtDireccion != null && txtDireccion.getText() != null) ? txtDireccion.getText().trim() : "";

        // Campo de observación (asegúrate de tener tu txtObservacion en la vista y declarada arriba)
        String observacion = (txtObservacion != null && txtObservacion.getText() != null) ? txtObservacion.getText().trim() : "";

        // Validaciones básicas de campos vacíos
        if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || telefono.isEmpty() ||
                cmbPlan.getValue() == null || cmbMetodoPago.getValue() == null || selectedComprobanteFile == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Incompletos", "Por favor completa todos los datos requeridos y adjunta el comprobante.");
            return;
        }

        // Validación de DNI (8 dígitos)
        if (!dni.matches("\\d{8}")) {
            mostrarAlerta(Alert.AlertType.WARNING, "DNI Inválido", "El DNI debe tener exactamente 8 dígitos.");
            txtDni.requestFocus();
            return;
        }
        if (!telefono.matches("\\d{9}")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Celular Inválido", "El numero de celular debe tener exactamente 9 dígitos.");
            txtTelefono.requestFocus();
            return;
        }

        try {
            ClientPlan planSeleccionado = cmbPlan.getValue();
            BigDecimal monto = BigDecimal.valueOf(planSeleccionado.getPrice() != null ? planSeleccionado.getPrice() : 0.0);
            String metodo = cmbMetodoPago.getValue();

            // 1. Registrar cliente y orden en la BD
            ClientCreateDto clientDto = new ClientCreateDto(
                    dni,
                    nombre,
                    apellido,
                    telefono,
                    direccion,
                    email,
                    telefono,
                    planSeleccionado
            );
            ClientsUseCase.addClientOrder(clientDto);

            // 2. Registrar el pago pendiente en la BD
            Payment paymentRegistrado = PaymentsUseCase.generateNewPaidsUser(
                    dni,
                    monto,
                    metodo
            );

            if (paymentRegistrado != null) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "El contrato y el pago fueron registrados correctamente.");

                // 3. EMPAQUETAR DATOS EN EL DTO Y ABRIR LA VENTANA DE RESUMEN (MODAL)
                SaleSumaryDto summaryDto = new SaleSumaryDto(
                        nombre + " " + apellido,
                        dni,
                        planSeleccionado.getName(),
                        String.valueOf(planSeleccionado.getSpeed()),
                        monto,
                        metodo,
                        observacion,
                        selectedComprobanteFile
                );

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/servinet/center/resumen-pago.fxml"));
                Parent root = loader.load();

                ResumenPagoController resumenController = loader.getController();
                resumenController.initData(summaryDto);

                Stage stage = new Stage();
                stage.setTitle("Resumen de Contrato y Pago");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root));
                stage.showAndWait(); // Pausa la ventana principal hasta que cierren el resumen

                // 4. Redirigir al menú de ventas al cerrar el modal
                if (indexController != null) {
                    indexController.renderizarFxml("sell.fxml");
                }
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo registrar el pago en la base de datos.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Ocurrió un fallo al procesar la venta: " + e.getMessage());
        }
    }

    private byte[] convertirArchivoABytes(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[(int) file.length()];
            fis.read(buffer);
            return buffer;
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