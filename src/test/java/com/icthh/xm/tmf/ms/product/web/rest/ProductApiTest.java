package com.icthh.xm.tmf.ms.product.web.rest;

import com.icthh.xm.commons.security.XmAuthenticationContextHolder;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
import com.icthh.xm.lep.api.LepManager;
import com.icthh.xm.tmf.ms.product.ProductApp;
import com.icthh.xm.tmf.ms.product.config.SecurityBeanOverrideConfiguration;
import com.icthh.xm.tmf.ms.product.web.api.ProductApiController;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static com.icthh.xm.commons.lep.XmLepConstants.THREAD_CONTEXT_KEY_TENANT_CONTEXT;
import static com.icthh.xm.commons.lep.XmLepScriptConstants.BINDING_KEY_AUTH_CONTEXT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {SecurityBeanOverrideConfiguration.class, ProductApp.class})
@WithMockUser(authorities = {"SUPER-ADMIN"})
public class ProductApiTest {

    @Autowired
    private ProductApiController controller;

    @Autowired
    private LepManager lepManager;

    @Autowired
    private TenantContextHolder tenantContextHolder;

    @Autowired
    private XmAuthenticationContextHolder xmAuthenticationContextHolder;

    private MockMvc mockMvc;


    @BeforeEach
    public void setup() {
        TenantContextUtils.setTenant(tenantContextHolder, "RESINTTEST");
        lepManager.beginThreadContext(scopedContext -> {
            scopedContext.setValue(THREAD_CONTEXT_KEY_TENANT_CONTEXT, tenantContextHolder.getContext());
            scopedContext.setValue(BINDING_KEY_AUTH_CONTEXT, xmAuthenticationContextHolder.getContext());
        });

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .build();
    }

    @SneakyThrows
    @Test
    public void testConcurrencyFailure() {
        Map<String, Object> request = of(
            "name", "test"
        );

        mockMvc.perform(
                patch("/api/product/1").contentType("application/json")
                    .content(TestUtil.convertObjectToJsonBytes(request)))
            .andDo(print())
            .andExpect(status().isNotImplemented());
    }
}
