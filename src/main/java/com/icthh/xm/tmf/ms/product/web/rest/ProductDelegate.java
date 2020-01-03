package com.icthh.xm.tmf.ms.product.web.rest;

import com.google.common.collect.ImmutableList;
import com.icthh.xm.commons.lep.LogicExtensionPoint;
import com.icthh.xm.commons.lep.spring.LepService;
import com.icthh.xm.tmf.ms.product.lep.keyresolver.ProfileChannelKeyResolver;
import com.icthh.xm.tmf.ms.product.web.api.ProductApiDelegate;
import com.icthh.xm.tmf.ms.product.web.api.model.Product;
import io.micrometer.core.annotation.Timed;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component
@LepService(group = "service")
public class ProductDelegate implements ProductApiDelegate {

    @LogicExtensionPoint(value = "GetProducts", resolver = ProfileChannelKeyResolver.class)
    @Timed
    @PreAuthorize("hasPermission({'profile': #profile}, 'PRODUCT.GET-LIST')")
    @Override
    public ResponseEntity<List<Product>> listProduct(String fields,
                                                     Integer offset,
                                                     Integer limit) {
        return ResponseEntity.ok(ImmutableList.of(new Product()));
    }
}
