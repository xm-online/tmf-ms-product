package com.icthh.xm.tmf.ms.product.web.rest;

import com.google.common.collect.ImmutableList;
import com.icthh.xm.commons.lep.LogicExtensionPoint;
import com.icthh.xm.commons.lep.spring.LepService;
import com.icthh.xm.tmf.ms.product.lep.keyresolver.ProfileChannelKeyResolver;
import com.icthh.xm.tmf.ms.product.web.api.ProductApiDelegate;
import com.icthh.xm.tmf.ms.product.web.api.model.Product;
import io.micrometer.core.annotation.Timed;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

@Component
@Slf4j
@LepService(group = "service")
public class ProductDelegate implements ProductApiDelegate {

    private final HttpServletRequest request;

    @Autowired
    public ProductDelegate(final HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.of(new ServletWebRequest(request));
    }

    @Timed
    @PreAuthorize("hasPermission({'profile': #profile}, 'PRODUCT.GET-LIST')")
    @Override
    public ResponseEntity<List<Product>> listProduct(String fields,
                                                     Integer offset,
                                                     Integer limit) {
        return getRequest().map(req -> {
            log.info("Native request {}", req.getNativeRequest());
            String profile = req.getHeader("profile");
            String channelId = req.getHeader("channel.id");
            return listProduct(profile, channelId);
        }).orElse(ResponseEntity.ok(ImmutableList.of(new Product())));
    }

    @LogicExtensionPoint(value = "GetProducts", resolver = ProfileChannelKeyResolver.class)
    private ResponseEntity<List<Product>> listProduct(String profile, String channelId) {
        return ResponseEntity.ok(ImmutableList.of(new Product()));
    }
}
