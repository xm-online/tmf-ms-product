package com.icthh.xm.tmf.ms.product.lep.keyresolver;

import com.icthh.xm.commons.lep.XmLepConstants;
import com.icthh.xm.commons.lep.spring.LepServiceHandler;
import com.icthh.xm.lep.api.LepKey;
import com.icthh.xm.lep.api.LepKeyResolver;
import com.icthh.xm.lep.api.LepManager;
import com.icthh.xm.lep.api.LepMethod;
import com.icthh.xm.lep.api.Version;
import com.icthh.xm.lep.core.CoreLepManager;
import com.icthh.xm.tmf.ms.product.web.rest.ProductDelegate;
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProfileChannelKeyResolverTest {

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

    @Mock
    private HttpServletRequest request;

    @Test
    public void testResolveLepKeyByProfileAndChannelId() throws Throwable {

        Method method = ProductDelegate.class.getDeclaredMethod("listProduct", String.class, String.class);

        when(applicationContext.getBean(LepManager.class)).thenReturn(lepManager);

        ProfileChannelKeyResolver resolver = new ProfileChannelKeyResolver();
        when(applicationContext.getBean(ProfileChannelKeyResolver.class)).thenReturn(resolver);

        lepServiceHandler.onMethodInvoke(ProductDelegate.class,
            new ProductDelegate(request), method, new Object[]{"RTM", "MYVODAFONE"});

        verify(lepManager)
            .processLep(baseLepKey.capture(), version.capture(), keyResolver.capture(), lepMethod.capture());

        LepKey resolvedKey = resolver.resolve(baseLepKey.getValue(), lepMethod.getValue(), null);

        assertEquals(
            String.join(XmLepConstants.EXTENSION_KEY_SEPARATOR,
                "service", "GetProducts", "RTM", "MYVODAFONE"), resolvedKey.getId());
    }
}
