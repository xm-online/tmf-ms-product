package com.icthh.xm.tmf.ms.product.keyresolver;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.icthh.xm.commons.lep.XmLepConstants;
import com.icthh.xm.commons.lep.spring.LepServiceHandler;
import com.icthh.xm.lep.api.LepKey;
import com.icthh.xm.lep.api.LepKeyResolver;
import com.icthh.xm.lep.api.LepManager;
import com.icthh.xm.lep.api.LepMethod;
import com.icthh.xm.lep.api.Version;
import com.icthh.xm.lep.core.CoreLepManager;
import com.icthh.xm.tmf.ms.product.lep.keyresolver.OptionalProfileKeyResolver;
import com.icthh.xm.tmf.ms.product.web.rest.ProductDelegate;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(SpringExtension.class)
class OptionalProfileKeyResolverTest {
    private static final String PROFILE_KEY = "profile";
    private static final String PROFILE_VALUE = "TEST-PROFILE";
    private static final String PROFILE_VALUE_RESOLVED = "TEST_PROFILE";

    @InjectMocks
    private LepServiceHandler lepServiceHandler;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private CoreLepManager lepManager;

    @Captor
    private ArgumentCaptor<LepKey> baseLepKey;

    @Captor
    private ArgumentCaptor<LepKeyResolver> keyResolver;

    @Captor
    private ArgumentCaptor<LepMethod> lepMethod;

    @Captor
    private ArgumentCaptor<Version> version;

    @Test
    void shouldResolveLepByHeader() throws Throwable {

        Method method = ProductDelegate.class.getMethod("listProduct", String.class, Integer.class, Integer.class);

        when(applicationContext.getBean(LepManager.class)).thenReturn(lepManager);

        OptionalProfileKeyResolver resolver = new OptionalProfileKeyResolver();
        when(applicationContext.getBean(OptionalProfileKeyResolver.class)).thenReturn(resolver);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(PROFILE_KEY, PROFILE_VALUE);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        lepServiceHandler.onMethodInvoke(ProductDelegate.class,
            new ProductDelegate(), method, new Object[]{null});

        verify(lepManager)
            .processLep(baseLepKey.capture(), version.capture(), keyResolver.capture(), lepMethod.capture());

        LepKey resolvedKey = resolver.resolve(baseLepKey.getValue(), lepMethod.getValue(), null);

        assertEquals(
            String.join(XmLepConstants.EXTENSION_KEY_SEPARATOR,
                "service", "GetProducts", PROFILE_VALUE_RESOLVED), resolvedKey.getId());
    }
}
