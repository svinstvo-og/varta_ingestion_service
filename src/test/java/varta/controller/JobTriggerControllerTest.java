package varta.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import varta.service.NormalizationService;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JobTriggerController.class)
public class JobTriggerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NormalizationService normalizationService;

    @Test
    public void testLaunchCreditUserJob() throws Exception {
        mockMvc.perform(post("/api/job/start/credit-user"))
                .andExpect(status().isOk());

        verify(normalizationService).launchCreditUserJob();
    }

    @Test
    public void testLaunchCreditStoreJob() throws Exception {
        mockMvc.perform(post("/api/job/start/credit-store"))
                .andExpect(status().isOk());

        verify(normalizationService).launchCreditStoreJob();
    }

    @Test
    public void testLaunchCreditCardJob() throws Exception {
        mockMvc.perform(post("/api/job/start/credit-card"))
                .andExpect(status().isOk());

        verify(normalizationService).launchCreditCardJob();
    }

    @Test
    public void testLaunchFinancialTransactionJob() throws Exception {
        mockMvc.perform(post("/api/job/start/financial-transaction"))
                .andExpect(status().isOk());

        verify(normalizationService).launchFinancialTransactionJob();
    }

    @Test
    public void testLaunchCreditTransactionJob() throws Exception {
        mockMvc.perform(post("/api/job/start/credit-transaction"))
                .andExpect(status().isOk());

        verify(normalizationService).launchCreditTransactionJob();
    }
}

