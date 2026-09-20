package com.sumit.orderplatform.inventory.service;

public record ReservationResult(Outcome outcome, String reason) {

    public enum Outcome {
        RESERVED,
        REJECTED,
        DUPLICATE
    }

    public static ReservationResult reserved() {
        return new ReservationResult(Outcome.RESERVED, null);
    }

    public static ReservationResult rejected(String reason) {
        return new ReservationResult(Outcome.REJECTED, reason);
    }

    public static ReservationResult duplicate() {
        return new ReservationResult(Outcome.DUPLICATE, null);
    }
}
