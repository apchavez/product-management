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
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class ProductUseCase {

    private static final Logger log = Logger.getLogger(ProductUseCase.class);

    @Inject
    MongoProductRepository productRepository;

    @Inject
    ProductMapper productMapper;

    public ProductResponse insert(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        product.create("SYSTEM");

        boolean inserted = productRepository.insert(product);
        if (!inserted) {
            throw new DuplicateSkuException();
        }

        log.debug("Product inserted successfully");
        return productMapper.toResponse(product);
    }

    public void update(String id, ProductRequest request) {
        ObjectId objectId = new ObjectId(id);

        Product existing = productRepository.findByObjectId(objectId);
        if (existing == null) {
            throw new ProductNotFoundException();
        }

        productMapper.updateEntity(request, existing);
        existing.markUpdated("SYSTEM");
        productRepository.update(existing);

        log.debug("Product updated successfully");
    }

    public void delete(String id) {
        ObjectId objectId = new ObjectId(id);

        boolean deleted = productRepository.deleteByObjectId(objectId);
        if (!deleted) {
            throw new ProductNotFoundException();
        }

        log.debug("Product deleted successfully");
    }

    public ProductResponse findById(String id) {
        ObjectId objectId = new ObjectId(id);
        Product product = productRepository.findByObjectId(objectId);
        if (product == null) {
            throw new ProductNotFoundException();
        }

        return productMapper.toResponse(product);
    }

    public ProductsPagedResponse findAll(int page, int size) {
        PagedResponse<Product> paged = productRepository.findAllProducts(page, size);

        List<ProductResponse> responseList = productMapper.toResponseList(paged.data());

        return new ProductsPagedResponse(responseList, paged.currentPage(), paged.totalPages(), paged.totalItems());
    }

    public ProductResponse findBySku(String sku) {
        Product product = productRepository.findBySku(sku);
        if (product == null) {
            throw new ProductNotFoundException();
        }

        return productMapper.toResponse(product);
    }

    public List<ProductResponse> findByNamePrefix(String prefix) {
        List<Product> products = productRepository.findByNamePrefix(prefix);
        return productMapper.toResponseList(products);
    }
}
