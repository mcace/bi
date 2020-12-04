package com.mcsoft.bi.peeper.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by MC on 2020/12/4.
 *
 * @author MC
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
class OrderAnalysisDataGenerateServiceTest {

    @Autowired
    private OrderAnalysisDataGenerateService orderAnalysisDataGenerateService;

    @Test
    void generateOrderAnalysisData() {
        orderAnalysisDataGenerateService.generateOrderAnalysisData();
    }
}