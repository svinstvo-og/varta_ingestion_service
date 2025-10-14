package crm.orchestra.controller;

import crm.orchestra.service.TransactionRaw6mService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController("api/test/ingestion/")
@Slf4j
public class TestIngestionController {

    final
    TransactionRaw6mService transactionRaw6mService;

    public TestIngestionController(TransactionRaw6mService transactionRaw6mService) {
        this.transactionRaw6mService = transactionRaw6mService;
    }

    @GetMapping({"datasets/6m/{id}"})
    public void fetchTransaction(@PathVariable Integer id) {
        transactionRaw6mService.getTransactionById(id);
    }
}
