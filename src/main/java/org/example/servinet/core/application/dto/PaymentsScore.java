package org.example.servinet.core.application.dto;

import org.example.servinet.core.domain.entities.Payment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PaymentsScore {
    private int countPendingPayments;
    private int countPaidPayments;
    private List<Payment> PendingPayments;
    private List<Payment> paidPayments;
    private Payment ActualPayments;
    private List<Payment> allPayments;

    public PaymentsScore(List<Payment> pendingPayments, List<Payment> paidPayments, Payment actualPayments) {
        PendingPayments = pendingPayments;
        this.paidPayments = paidPayments;
        ActualPayments = actualPayments;

        countPendingPayments = pendingPayments.size();
        countPaidPayments = paidPayments.size();

        allPayments = new ArrayList<>(Stream.concat(paidPayments.stream(), pendingPayments.stream()).toList());
    }

    public void addNewPayments(Payment newPaid) {
        if (this.PendingPayments == null) {
            this.PendingPayments = new ArrayList<>();
        }
        if (this.allPayments == null) {
            this.allPayments = new ArrayList<>();
        }

        this.ActualPayments = newPaid;
        this.PendingPayments.add(newPaid);
        this.allPayments.add(newPaid);

        // Actualizar el contador de pagos pendientes para que refleje el nuevo elemento
        this.countPendingPayments = this.PendingPayments.size();
    }

    public List<Payment> getAllPayments() {
        return allPayments;
    }

    public int getCountPendingPayments() {
        return countPendingPayments;
    }

    public int getCountPaidPayments() {
        return countPaidPayments;
    }

    public List<Payment> getPendingPayments() {
        return PendingPayments;
    }

    public List<Payment> getPaidPayments() {
        return paidPayments;
    }

    public Payment getActualPayments() {
        return ActualPayments;
    }
}
