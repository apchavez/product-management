package com.products.adapters.in.rest;

import com.products.application.dto.ProductRequest;
import com.products.application.dto.ProductResponse;
import com.products.application.dto.ProductsPagedResponse;
import com.products.application.usecase.ProductUseCase;
import com.products.adapters.in.rest.ApiResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("/api/v1/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ProductResource {

    @Inject
    ProductUseCase productUseCase;

    @POST
    public Response insert(@Valid ProductRequest request) {
        ProductResponse created = productUseCase.insert(request);
        return ApiResponse.created(Map.of("id", created.id()));
    }

    @PUT
    @Path("{id}")
    public Response update(
            @PathParam("id") @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "id has invalid format") String id,
            @Valid ProductRequest request) {
        productUseCase.update(id, request);
        return ApiResponse.updated();
    }

    @DELETE
    @Path("{id}")
    public Response delete(
            @PathParam("id") @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "id has invalid format") String id) {
        productUseCase.delete(id);
        return ApiResponse.deleted();
    }

    @GET
    @Path("{id}")
    public Response findById(
            @PathParam("id") @Pattern(regexp = "^[a-fA-F0-9]{24}$", message = "id has invalid format") String id) {
        ProductResponse product = productUseCase.findById(id);
        return ApiResponse.ok(product);
    }

    @GET
    public Response findAll(
            @QueryParam("page") @DefaultValue("0") @Min(value = 0, message = "page must be greater than or equal to 0") Integer page,
            @QueryParam("size") @DefaultValue("10") @Min(value = 1, message = "size must be greater than 0") @Max(value = 100, message = "size must be at most 100") Integer size) {
        ProductsPagedResponse response = productUseCase.findAll(page, size);
        return ApiResponse.ok(response);
    }

    @GET
    @Path("/sku/{sku}")
    public Response findBySku(
            @PathParam("sku") @NotBlank(message = "sku is required") @Pattern(regexp = "^[A-Za-z0-9_-]{1,50}$", message = "sku has invalid format") String sku) {
        ProductResponse product = productUseCase.findBySku(sku);
        return ApiResponse.ok(product);
    }

    @GET
    @Path("/search")
    public Response findByNamePrefix(
            @QueryParam("prefix") @NotBlank(message = "prefix is required") @Size(max = 100, message = "prefix is too long") String prefix) {
        List<ProductResponse> results = productUseCase.findByNamePrefix(prefix);
        return ApiResponse.ok(results);
    }
}
