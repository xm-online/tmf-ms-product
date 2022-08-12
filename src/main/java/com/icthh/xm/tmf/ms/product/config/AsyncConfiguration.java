package com.icthh.xm.tmf.ms.product.config;

import static com.icthh.xm.commons.lep.XmLepConstants.THREAD_CONTEXT_KEY_AUTH_CONTEXT;
import static com.icthh.xm.commons.lep.XmLepConstants.THREAD_CONTEXT_KEY_TENANT_CONTEXT;

import com.icthh.xm.commons.security.XmAuthenticationContextHolder;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
import com.icthh.xm.commons.tenant.TenantKey;
import com.icthh.xm.lep.api.LepManager;
import io.github.jhipster.config.JHipsterProperties;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
@EnableAsync
@RequiredArgsConstructor
@EnableScheduling
public class AsyncConfiguration implements AsyncConfigurer, SchedulingConfigurer {

    private final JHipsterProperties jHipsterProperties;
    private final LepManager lepManager;
    private final TenantContextHolder tenantContextHolder;
    private final XmAuthenticationContextHolder authContextHolder;

    @Bean
    protected WebMvcConfigurer webMvcConfigurer(AsyncTaskExecutor taskExecutor) {
        return new WebMvcConfigurer() {
            @Override
            public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
                configurer.setTaskExecutor(taskExecutor);
            }
        };
    }

    @Bean("taskExecutor")
    @Override
    public ThreadPoolTaskExecutor getAsyncExecutor() {
        log.debug("Creating Async Task Executor");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(jHipsterProperties.getAsync().getCorePoolSize());
        executor.setMaxPoolSize(jHipsterProperties.getAsync().getMaxPoolSize());
        executor.setQueueCapacity(jHipsterProperties.getAsync().getQueueCapacity());
        executor.setThreadNamePrefix("product-Executor-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setTaskDecorator(new ContextPropagatorTaskDecorator());

        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(scheduledTaskExecutor());
    }

    @Bean
    public Executor scheduledTaskExecutor() {
        return Executors.newScheduledThreadPool(jHipsterProperties.getAsync().getCorePoolSize());
    }

    class ContextPropagatorTaskDecorator implements TaskDecorator {
        @Override
        public Runnable decorate(Runnable runnable) {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            SecurityContext securityContext = SecurityContextHolder.getContext();
            Map<String, String> mdcContext = MDC.getCopyOfContextMap();
            TenantKey tenantKey = TenantContextUtils.getTenantKey(tenantContextHolder).orElse(TenantKey.SUPER);
            return () -> {
                try {
                    MDC.setContextMap(mdcContext);
                    RequestContextHolder.setRequestAttributes(requestAttributes);
                    SecurityContextHolder.setContext(securityContext);
                    TenantContextUtils.setTenant(tenantContextHolder, tenantKey);
                    lepManager.beginThreadContext(threadContext -> {
                        threadContext.setValue(THREAD_CONTEXT_KEY_TENANT_CONTEXT, tenantContextHolder.getContext());
                        threadContext.setValue(THREAD_CONTEXT_KEY_AUTH_CONTEXT, authContextHolder.getContext());
                    });
                    runnable.run();
                } finally {
                    MDC.clear();
                    RequestContextHolder.resetRequestAttributes();
                    SecurityContextHolder.clearContext();
                    lepManager.endThreadContext();
                    tenantContextHolder.getPrivilegedContext().destroyCurrentContext();
                }
            };

        }
    }
}
