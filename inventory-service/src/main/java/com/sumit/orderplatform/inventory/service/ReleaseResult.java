package com.sumit.orderplatform.inventory.service;

public record ReleaseResult(Outcome outcome) {

    public enum Outcome {
        RELEASED,
        ALREADY_RELEASED,
        NOT_FOUND
    }

    public static ReleaseResult released() {
        return new ReleaseResult(Outcome.RELEASED);
    }

    public static ReleaseResult alreadyReleased() {
        return new ReleaseResult(Outcome.ALREADY_RELEASED);
    }

    public static ReleaseResult notFound() {
        return new ReleaseResult(Outcome.NOT_FOUND);
    }
}
