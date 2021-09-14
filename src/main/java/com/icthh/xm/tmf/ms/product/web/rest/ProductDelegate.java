package com.icthh.xm.tmf.ms.product.web.rest;

import com.icthh.xm.commons.lep.LogicExtensionPoint;
import com.icthh.xm.commons.lep.spring.LepService;
import com.icthh.xm.commons.permission.annotation.PrivilegeDescription;
import com.icthh.xm.tmf.ms.product.lep.keyresolver.OptionalProfileKeyResolver;
import com.icthh.xm.tmf.ms.product.web.api.ProductApiDelegate;
import com.icthh.xm.tmf.ms.product.web.api.model.Product;
import io.micrometer.core.annotation.Timed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@LepService(group = "service")
public class ProductDelegate implements ProductApiDelegate {

    @Timed
    @LogicExtensionPoint(value = "GetProducts", resolver = OptionalProfileKeyResolver.class)
    @PreAuthorize("hasPermission({'profile': @headerRequestExtractor.profile}, 'PRODUCT.GET-LIST')")
    @PrivilegeDescription("Privilege to get list of products")
    @Override
    public ResponseEntity<List<Product>> listProduct(String fields,
                                                     Integer offset,
                                                     Integer limit) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
