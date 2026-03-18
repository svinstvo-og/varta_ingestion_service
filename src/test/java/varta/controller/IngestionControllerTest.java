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

import javax.sql.DataSource;
import jakarta.transaction.TransactionManager;

@WebMvcTest(IngestionController.class)
public class IngestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NormalizationService normalizationService;

    @MockitoBean(name = "transactionManager")
    private TransactionManager transactionManager;

    @MockitoBean(name = "dataSource")
    private DataSource dataSource;

    @Test3
    public void testNormalizeAllTables() throws Exception {
        mockMvc.perform(post("/api/ingestion/launch"))
                .andExpect(status().isOk());

        verify(normalizationService).normalizeAllTables();
    }
}

