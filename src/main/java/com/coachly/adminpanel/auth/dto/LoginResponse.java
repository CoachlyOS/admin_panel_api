package com.coachly.adminpanel.auth.dto;

public record LoginResponse(String token, AdministratorProfileResponse administrator) {}
