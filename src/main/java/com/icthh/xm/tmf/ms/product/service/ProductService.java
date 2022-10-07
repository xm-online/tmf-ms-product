package com.icthh.xm.tmf.ms.product.service;

import com.icthh.xm.commons.lep.LogicExtensionPoint;
import com.icthh.xm.commons.lep.spring.LepService;
import com.icthh.xm.tmf.ms.product.lep.keyresolver.OptionalProfileKeyResolver;
import com.icthh.xm.tmf.ms.product.web.api.model.Product;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@LepService(group = "service")
public class ProductService {

    @LogicExtensionPoint(value = "GetProducts", resolver = OptionalProfileKeyResolver.class)
    public ResponseEntity<List<Product>> listProduct(String fields,
                                                     Integer offset,
                                                     Integer limit) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
