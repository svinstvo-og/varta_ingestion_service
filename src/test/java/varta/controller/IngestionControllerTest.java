package varta.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import varta.service.NormalizationService;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = IngestionController.class, excludeAutoConfiguration = { DataSourceAutoConfiguration.class, BatchAutoConfiguration.class})
public class IngestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NormalizationService normalizationService;

    @Test
    public void testNormalizeAllTables() throws Exception {
        mockMvc.perform(post("/api/ingestion/launch"))
                .andExpect(status().isOk());

        verify(normalizationService).normalizeAllTables();
    }
}
