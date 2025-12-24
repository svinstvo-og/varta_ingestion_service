package varta.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
