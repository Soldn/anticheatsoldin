package dev.sldn.anticheat.api;

public record Violation(CheckType type, int amount, String reason) {}
