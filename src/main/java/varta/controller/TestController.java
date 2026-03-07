package varta.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import varta.service.MockService;

@RestController
@RequestMapping("api/test")
@Slf4j
public class TestController {

    final
    MockService mockService;

    public TestController(MockService mockService) {
        this.mockService = mockService;
    }

    @PostMapping("/mock/credit-transaction")
    public void mockTransaction() {
        log.info("Accepted mock transaction call");
        mockService.createMockTransaction();
    }

    @PostMapping("/repeat/random/credit-transaction")
    public void repeatRandomTransaction() {
        log.info("Accepted repeat random transaction call");
        mockService.repeatRandomTransaction();
    }

    @DeleteMapping("mock/credit-transactions")
    public void deleteMockTransactions() {
        log.info("Accepted delete mock transactions call");
        mockService.deleteMockTransactions();
    }
}
