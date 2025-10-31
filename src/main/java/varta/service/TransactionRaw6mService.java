package varta.service;

import varta.model.pgsql.Sixm;
import varta.repository.pgsql.TransactionRaw6mRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransactionRaw6mService {

    private final TransactionRaw6mRepository transactionRaw6mRepository;

    public TransactionRaw6mService(TransactionRaw6mRepository transactionRaw6mRepository) {
        this.transactionRaw6mRepository = transactionRaw6mRepository;
    }

    public Optional<Sixm> getTransactionById(Integer id) {
        return transactionRaw6mRepository.findById(id);
    }

    public void normalize(Sixm sixm) {

    }
}
