package com.luxinx.account;

import com.luxinx.bean.BeanAccount;
import com.luxinx.bean.BeanWater;
import com.luxinx.service.ServiceDataAccount;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AccountApplicationTests {

    @Autowired
    private ServiceDataAccount serviceDataAccount;
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeTestClass
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    @DisplayName("controller.AccountQueryAllAccounts")
    public void testcontrollerAccountQueryAllAccounts() throws Exception {
        setUp();
        RequestBuilder request = null;
        //构造请求
        request = post("/rest/list")
                .param("appId", "1001");
        //执行请求
        mockMvc.perform(request)
                .andExpect(status().isOk())//返回HTTP状态为200
                .andDo(print());//打印结果
    }

    @Test
    @DisplayName("serviceDataAccount.queryAllAccounts")
    public void testServiceDataAccountQueryAllAccounts(){
        List<BeanAccount> rst = serviceDataAccount.queryAccount();
        Assert.noNullElements(rst,"element not null");
    }
}
