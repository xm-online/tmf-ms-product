package com.icthh.xm.tmf.ms.product.web.rest;

import com.icthh.xm.commons.permission.annotation.PrivilegeDescription;
import com.icthh.xm.tmf.ms.product.service.ProductService;
import com.icthh.xm.tmf.ms.product.web.api.ProductApiDelegate;
import com.icthh.xm.tmf.ms.product.web.api.model.Product;
import com.icthh.xm.tmf.ms.product.web.api.model.ProductCreate;
import com.icthh.xm.tmf.ms.product.web.api.model.ProductUpdate;
import io.micrometer.core.annotation.Timed;
import java.util.List;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductApi implements ProductApiDelegate {

    private final ProductService productService;

    @Override
    public Callable<ResponseEntity<Product>> createProduct(ProductCreate productCreate) {
        return () -> ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public Callable<ResponseEntity<Void>> deleteProduct(String id) {
        return () -> ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Timed
    @PreAuthorize("hasPermission({'profile': @headerRequestExtractor.profile}, 'PRODUCT.GET-LIST')")
    @PrivilegeDescription("Privilege to get list of products")
    @Override
    public Callable<ResponseEntity<List<Product>>> listProduct(String fields,
                                                               Integer offset,
                                                               Integer limit) {
        return () -> productService.listProduct(fields, offset, limit);
    }

    @Override
    public Callable<ResponseEntity<Product>> patchProduct(String id, ProductUpdate productUpdate) {
        return () -> ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public Callable<ResponseEntity<Product>> retrieveProduct(String id, String fields) {
        return () -> ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
