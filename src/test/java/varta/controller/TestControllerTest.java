package varta.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import varta.service.MockService;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestController.class)
public class TestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MockService mockService;

    @Test
    public void testMockTransaction() throws Exception {
        mockMvc.perform(post("/api/test/mock/credit-transaction"))
                .andExpect(status().isOk());

        verify(mockService).createMockTransaction();
    }

    @Test
    public void testRepeatRandomTransaction() throws Exception {
        mockMvc.perform(post("/api/test/repeat/random/credit-transaction"))
                .andExpect(status().isOk());

        verify(mockService).repeatRandomTransaction();
    }

    @Test
    public void testDeleteMockTransactions() throws Exception {
        mockMvc.perform(delete("/api/test/mock/credit-transactions"))
                .andExpect(status().isOk());

        verify(mockService).deleteMockTransactions();
    }
}

