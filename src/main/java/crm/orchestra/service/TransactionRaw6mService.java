package crm.orchestra.service;

import crm.orchestra.model.TransactionRaw6m;
import crm.orchestra.repository.TransactionRaw6mRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransactionRaw6mService {

    private final TransactionRaw6mRepository transactionRaw6mRepository;

    public TransactionRaw6mService(TransactionRaw6mRepository transactionRaw6mRepository) {
        this.transactionRaw6mRepository = transactionRaw6mRepository;
    }

    public Optional<TransactionRaw6m> getTransactionById(Integer id) {
        return transactionRaw6mRepository.findById(id);
    }

}
