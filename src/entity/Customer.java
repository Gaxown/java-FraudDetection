package entity;

public record Customer(
    int customerId,
    String fullName,
    String email,
    String phoneNumber
) {}
