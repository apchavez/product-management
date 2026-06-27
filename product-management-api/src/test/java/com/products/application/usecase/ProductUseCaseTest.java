package com.products.application.usecase;

import com.products.adapters.out.persistence.MongoProductRepository;
import com.products.application.dto.ProductRequest;
import com.products.application.dto.ProductResponse;
import com.products.application.dto.ProductsPagedResponse;
import com.products.application.mapper.ProductMapper;
import com.products.domain.model.PagedResponse;
import com.products.domain.model.Product;
import com.products.exception.DuplicateSkuException;
import com.products.exception.ProductNotFoundException;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@QuarkusTest
class ProductUseCaseTest {

    @InjectMock
    MongoProductRepository productRepository;

    @InjectMock
    ProductMapper productMapper;

    @Inject
    ProductUseCase productUseCase;

    @Test
    void insert_shouldReturnProductResponse_whenProductIsInserted() {
        ProductRequest request = mock(ProductRequest.class);
        Product product = new Product();
        product.id = new ObjectId();
        ProductResponse expectedResponse = mock(ProductResponse.class);

        when(productMapper.toEntity(request)).thenReturn(product);
        when(productRepository.insert(product)).thenReturn(true);
        when(productMapper.toResponse(product)).thenReturn(expectedResponse);

        ProductResponse result = productUseCase.insert(request);

        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    void insert_shouldThrowDuplicateSkuException_whenProductAlreadyExists() {
        ProductRequest request = mock(ProductRequest.class);
        Product product = new Product();
        product.id = new ObjectId();

        when(productMapper.toEntity(request)).thenReturn(product);
        when(productRepository.insert(product)).thenReturn(false);

        assertThatThrownBy(() -> productUseCase.insert(request))
                .isInstanceOf(DuplicateSkuException.class);
    }

    @Test
    void findById_shouldReturnProductResponse_whenProductExists() {
        ObjectId id = new ObjectId();
        Product product = new Product();
        ProductResponse productResponse = mock(ProductResponse.class);

        when(productRepository.findByObjectId(any(ObjectId.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        ProductResponse result = productUseCase.findById(id.toHexString());

        assertThat(result).isEqualTo(productResponse);
    }

    @Test
    void findById_shouldThrowProductNotFoundException_whenProductDoesNotExist() {
        ObjectId id = new ObjectId();

        when(productRepository.findByObjectId(any(ObjectId.class))).thenReturn(null);

        assertThatThrownBy(() -> productUseCase.findById(id.toHexString()))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void findAll_shouldReturnPagedResponse() {
        Product product = new Product();
        ProductResponse responseDto = mock(ProductResponse.class);
        PagedResponse<Product> pagedResponse = new PagedResponse<>(List.of(product), 0, 1, 1);

        when(productRepository.findAllProducts(0, 10)).thenReturn(pagedResponse);
        when(productMapper.toResponseList(anyList())).thenReturn(List.of(responseDto));

        ProductsPagedResponse result = productUseCase.findAll(0, 10);

        assertThat(result).isNotNull();
        assertThat(result.products()).hasSize(1);
        assertThat(result.totalPages()).isEqualTo(1);
    }

    @Test
    void findBySku_shouldReturnProductResponse_whenProductExists() {
        Product product = new Product();
        ProductResponse responseDto = mock(ProductResponse.class);

        when(productRepository.findBySku("SKU-001")).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(responseDto);

        ProductResponse result = productUseCase.findBySku("SKU-001");

        assertThat(result).isEqualTo(responseDto);
    }

    @Test
    void findBySku_shouldThrowProductNotFoundException_whenProductDoesNotExist() {
        when(productRepository.findBySku("SKU-404")).thenReturn(null);

        assertThatThrownBy(() -> productUseCase.findBySku("SKU-404"))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void findByNamePrefix_shouldReturnList_whenProductsExist() {
        Product product = new Product();
        ProductResponse responseDto = mock(ProductResponse.class);

        when(productRepository.findByNamePrefix("Lap")).thenReturn(List.of(product));
        when(productMapper.toResponseList(anyList())).thenReturn(List.of(responseDto));

        List<ProductResponse> result = productUseCase.findByNamePrefix("Lap");

        assertThat(result).hasSize(1);
    }

    @Test
    void findByNamePrefix_shouldReturnEmptyList_whenNoProductsExist() {
        when(productRepository.findByNamePrefix("XYZ")).thenReturn(List.of());
        when(productMapper.toResponseList(anyList())).thenReturn(List.of());

        List<ProductResponse> result = productUseCase.findByNamePrefix("XYZ");

        assertThat(result).isEmpty();
    }

    @Test
    void update_shouldComplete_whenProductExists() {
        ObjectId id = new ObjectId();
        ProductRequest request = mock(ProductRequest.class);
        Product existing = new Product();

        when(productRepository.findByObjectId(any(ObjectId.class))).thenReturn(existing);
        doNothing().when(productMapper).updateEntity(request, existing);
        doNothing().when(productRepository).update(existing);

        productUseCase.update(id.toHexString(), request);
    }

    @Test
    void update_shouldThrowProductNotFoundException_whenProductDoesNotExist() {
        ObjectId id = new ObjectId();
        ProductRequest request = mock(ProductRequest.class);

        when(productRepository.findByObjectId(any(ObjectId.class))).thenReturn(null);

        assertThatThrownBy(() -> productUseCase.update(id.toHexString(), request))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void delete_shouldComplete_whenProductExists() {
        ObjectId id = new ObjectId();

        when(productRepository.deleteByObjectId(any(ObjectId.class))).thenReturn(true);

        productUseCase.delete(id.toHexString());
    }

    @Test
    void delete_shouldThrowProductNotFoundException_whenProductDoesNotExist() {
        ObjectId id = new ObjectId();

        when(productRepository.deleteByObjectId(any(ObjectId.class))).thenReturn(false);

        assertThatThrownBy(() -> productUseCase.delete(id.toHexString()))
                .isInstanceOf(ProductNotFoundException.class);
    }
}
