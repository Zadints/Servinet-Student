package org.example.servinet.core.application.dto;

import java.io.File;
import java.math.BigDecimal;

public class SaleSumaryDto {
    private String clienteNombreCompleto;
    private String clienteDni;
    private String planNombre;
    private String planVelocidad;
    private BigDecimal monto;
    private String metodoPago;
    private String observacion;
    private File comprobanteFile;

    public SaleSumaryDto(String clienteNombreCompleto, String clienteDni, String planNombre,
                          String planVelocidad, BigDecimal monto, String metodoPago,
                          String observacion, File comprobanteFile) {
        this.clienteNombreCompleto = clienteNombreCompleto;
        this.clienteDni = clienteDni;
        this.planNombre = planNombre;
        this.planVelocidad = planVelocidad;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.observacion = observacion;
        this.comprobanteFile = comprobanteFile;
    }

    // Getters y Setters
    public String getClienteNombreCompleto() { return clienteNombreCompleto; }
    public void setClienteNombreCompleto(String clienteNombreCompleto) { this.clienteNombreCompleto = clienteNombreCompleto; }

    public String getClienteDni() { return clienteDni; }
    public void setClienteDni(String clienteDni) { this.clienteDni = clienteDni; }

    public String getPlanNombre() { return planNombre; }
    public void setPlanNombre(String planNombre) { this.planNombre = planNombre; }

    public String getPlanVelocidad() { return planVelocidad; }
    public void setPlanVelocidad(String planVelocidad) { this.planVelocidad = planVelocidad; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public File getComprobanteFile() { return comprobanteFile; }
    public void setComprobanteFile(File comprobanteFile) { this.comprobanteFile = comprobanteFile; }
}
