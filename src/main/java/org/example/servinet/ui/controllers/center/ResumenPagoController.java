package org.example.servinet.ui.controllers.center;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.example.servinet.core.application.dto.SaleSumaryDto;
import javafx.scene.image.ImageView;
import java.io.File;

public class ResumenPagoController {
    @FXML
    private Label lblCliente;
    @FXML private Label lblPlan;
    @FXML private Label lblMonto;
    @FXML private Label lblMetodo;
    @FXML private Label lblObservacion;
    @FXML private ImageView imgComprobanteResumen;
    @FXML private Button btnCerrar;

    // Método que recibe el DTO con toda la información del contrato y pago
    public void initData(SaleSumaryDto summary) {
        if (summary != null) {
            lblCliente.setText(summary.getClienteNombreCompleto() + " (DNI: " + summary.getClienteDni() + ")");
            lblPlan.setText(summary.getPlanNombre() + " - " + summary.getPlanVelocidad() + " Mbps");
            lblMonto.setText(String.format("S/ %.2f", summary.getMonto()));
            lblMetodo.setText(summary.getMetodoPago());

            String obs = summary.getObservacion();
            lblObservacion.setText(obs != null && !obs.isEmpty() ? obs : "Ninguna");

            if (summary.getComprobanteFile() != null && summary.getComprobanteFile().exists()) {
                imgComprobanteResunnemImage(summary.getComprobanteFile());
            }
        }
    }

    private void imgComprobanteResunnemImage(File file) {
        imgComprobanteResumen.setImage(new Image(file.toURI().toString()));
    }

    @FXML
    private void cerrarModal() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }
}
