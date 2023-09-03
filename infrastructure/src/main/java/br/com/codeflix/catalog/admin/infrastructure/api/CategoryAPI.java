package br.com.codeflix.catalog.admin.infrastructure.api;

import br.com.codeflix.catalog.admin.domain.pagination.Pagination;
import br.com.codeflix.catalog.admin.infrastructure.category.models.CreateCategoryApiInput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "categories")
@Tag(name = "Categories")
public interface CategoryAPI {

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Create a new category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created successfully"),
            @ApiResponse(responseCode = "422", description = "Validation error"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
    })
    ResponseEntity<?> createCategory(@RequestBody CreateCategoryApiInput input);

    @GetMapping
    @Operation(summary = "List all categories paginated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listed successfully"),
            @ApiResponse(responseCode = "422", description = "Invalid parameter error"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
    })
    Pagination<?> listCategories(
            @RequestParam(value = "search", required = false, defaultValue = "") final String search,
            @RequestParam(value = "page", required = false, defaultValue = "0") final int page,
            @RequestParam(value = "perPage", required = false, defaultValue = "10") final int perPage,
            @RequestParam(value = "sort", required = false, defaultValue = "name") final String sort,
            @RequestParam(value = "dir", required = false, defaultValue = "asc") final String direction
    );
}