package com.luxinx.account;

import com.luxinx.cron.Tzcrond;
import com.luxinx.service.ServiceDataAccount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class TZTests {

    @Autowired
    private Tzcrond tzcrond;

    @Test
    public void testCrond(){
        tzcrond.configureTaskFund();
    }
}
