package dev.oauth.consent.dto;

public record ConsentScopeView(
        String scope,
        String description
) {
}