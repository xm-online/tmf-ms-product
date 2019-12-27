package com.icthh.xm.tmf.ms.product.lep.keyresolver;

import com.icthh.xm.commons.lep.AppendLepKeyResolver;
import com.icthh.xm.lep.api.LepManagerService;
import com.icthh.xm.lep.api.LepMethod;
import com.icthh.xm.lep.api.commons.SeparatorSegmentedLepKey;
import org.springframework.stereotype.Component;

@Component
public class ProfileChannelKeyResolver extends AppendLepKeyResolver {

    @Override
    protected String[] getAppendSegments(SeparatorSegmentedLepKey baseKey,
                                         LepMethod method,
                                         LepManagerService managerService) {
        String product = getRequiredParam(method, "profile", String.class);
        String channelId = getRequiredParam(method, "channelId", String.class);
        product = translateToLepConvention(product);
        channelId = translateToLepConvention(channelId);
        return new String[]{product, channelId};
    }
}
