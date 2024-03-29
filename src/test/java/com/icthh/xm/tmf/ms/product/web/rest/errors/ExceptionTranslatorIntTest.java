package com.icthh.xm.tmf.ms.product.web.rest.errors;

import com.icthh.xm.commons.i18n.error.web.ExceptionTranslator;
import com.icthh.xm.tmf.ms.product.ProductApp;
import com.icthh.xm.tmf.ms.product.config.SecurityBeanOverrideConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the ExceptionTranslator controller advice.
 *
 * @see ExceptionTranslator
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SecurityBeanOverrideConfiguration.class, ProductApp.class})
public class ExceptionTranslatorIntTest {

    @Autowired
    private ExceptionTranslatorTestController controller;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter)
            .build();
    }

    @Test
    public void testConcurrencyFailure() throws Exception {
        mockMvc.perform(get("/test/concurrency-failure"))
            .andExpect(status().isConflict())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.error").value(com.icthh.xm.commons.exceptions.ErrorConstants.ERR_CONCURRENCY_FAILURE));
    }

    @Test
    public void testMethodArgumentNotValid() throws Exception {
         mockMvc.perform(post("/test/method-argument").content("{}").contentType(MediaType.APPLICATION_JSON))
             .andExpect(status().isBadRequest())
             .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
             .andExpect(jsonPath("$.error").value(ErrorConstants.ERR_VALIDATION))
             .andExpect(jsonPath("$.fieldErrors.[0].objectName").value("testDTO"))
             .andExpect(jsonPath("$.fieldErrors.[0].field").value("test"))
             .andExpect(jsonPath("$.fieldErrors.[0].message").value("NotNull"));
    }

    @Test
    public void testParameterizedError() throws Exception {
        mockMvc.perform(get("/test/parameterized-error"))
            .andExpect(status().isInternalServerError())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.error").value("error.internalServerError"))
            .andExpect(jsonPath("$.error_description").value("Internal server error, please try later"));
    }

    @Test
    public void testParameterizedError2() throws Exception {
        mockMvc.perform(get("/test/parameterized-error2"))
            .andExpect(status().isInternalServerError())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.error").value("error.internalServerError"))
            .andExpect(jsonPath("$.error_description").value("Internal server error, please try later"));
    }

    @Test
    public void testMissingServletRequestPartException() throws Exception {
        mockMvc.perform(get("/test/missing-servlet-request-part"))
            .andExpect(status().isInternalServerError())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.error").value("error.internalServerError"))
            .andExpect(jsonPath("$.error_description").value("Internal server error, please try later"));
    }

    @Test
    public void testMissingServletRequestParameterException() throws Exception {
        mockMvc.perform(get("/test/missing-servlet-request-parameter"))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testAccessDenied() throws Exception {
        mockMvc.perform(get("/test/access-denied"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error").value(com.icthh.xm.commons.exceptions.ErrorConstants.ERR_ACCESS_DENIED))
            .andExpect(jsonPath("$.error_description").value("Access denied"));
    }

    @Test
    public void testUnauthorized() throws Exception {
        mockMvc.perform(get("/test/unauthorized"))
            .andExpect(status().isInternalServerError())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.error").value("error.internalServerError"))
            .andExpect(jsonPath("$.error_description").value("Internal server error, please try later"));
    }

    @Test
    public void testMethodNotSupported() throws Exception {
        mockMvc.perform(post("/test/access-denied"))
            .andExpect(status().isMethodNotAllowed())
            .andExpect(jsonPath("$.error").value(com.icthh.xm.commons.exceptions.ErrorConstants.ERR_METHOD_NOT_SUPPORTED))
            .andExpect(jsonPath("$.error_description").value("Method not supported"));
    }

    @Test
    public void testExceptionWithResponseStatus() throws Exception {
        mockMvc.perform(get("/test/response-status"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("error.400"))
            .andExpect(jsonPath("$.error_description").value("Invalid request"));
    }

    @Test
    public void testInternalServerError() throws Exception {
        mockMvc.perform(get("/test/internal-server-error"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.error").value(com.icthh.xm.commons.exceptions.ErrorConstants.ERR_INTERNAL_SERVER_ERROR))
            .andExpect(jsonPath("$.error_description").value("Internal server error, please try later"));
    }

    public void testBusinessException() throws Exception {
        mockMvc.perform(get("/test/business-exception"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("error.business"))
            .andExpect(jsonPath("$.error_description").value("test-message"));
    }

}
