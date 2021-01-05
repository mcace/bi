package com.mcsoft.bi.peeper.job;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileNotFoundException;

/**
 * Created by MC on 2021/1/5.
 *
 * @author MC
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class SpotOrdersExcelJobTest {

    @Autowired
    private SpotOrdersExcelJob spotOrdersExcelJob;

    @Test
    public void export() throws FileNotFoundException {
        spotOrdersExcelJob.export();
    }
}