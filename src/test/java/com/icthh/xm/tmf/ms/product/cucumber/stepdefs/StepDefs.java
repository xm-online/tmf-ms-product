package com.icthh.xm.tmf.ms.product.cucumber.stepdefs;

import com.icthh.xm.tmf.ms.product.ProductApp;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import org.springframework.boot.test.context.SpringBootTest;

@WebAppConfiguration
@SpringBootTest
@ContextConfiguration(classes = ProductApp.class)
public abstract class StepDefs {

    protected ResultActions actions;

}
