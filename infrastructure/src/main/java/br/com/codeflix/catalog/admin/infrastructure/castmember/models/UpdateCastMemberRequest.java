package br.com.codeflix.catalog.admin.infrastructure.castmember.models;

import br.com.codeflix.catalog.admin.domain.castmember.CastMemberType;

public record UpdateCastMemberRequest(String name, CastMemberType type) {
}