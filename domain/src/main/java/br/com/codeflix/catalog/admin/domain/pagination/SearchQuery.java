package br.com.codeflix.catalog.admin.domain.pagination;

public record SearchQuery(
        int page,
        int perPage,
        String terms,
        String sort,
        String direction
) {

    public static SearchQuery with(final int page, final int perPage, final String terms, final String sort,
                                   final String direction) {
        return new SearchQuery(page, perPage, terms, sort, direction);
    }
}
