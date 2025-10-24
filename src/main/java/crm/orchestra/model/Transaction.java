package crm.orchestra.model;

import crm.orchestra.dto.TransactionType;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.hibernate.type.descriptor.jdbc.TimestampWithTimeZoneJdbcType;

@Entity
public class Transaction {

    //Transaction data
    @Id
    Long transactionId;
    TimestampWithTimeZoneJdbcType timestamp;
    double amount;
    String currency;
    Enum<TransactionType> transactionType;
    Enum<TransactionStatus> status;

    //Parties
    String nameOrigin;
    String nameDest;
    boolean originIsMerchant;
    boolean destIsMerchant;

    //Balances
    long oldBalanceOrigin;
    long newBalanceOrigin;
    long oldBalanceDest;
    long newBalanceDest;

    //Detection data
    boolean isFraud;
    double riskScore;
    

    @Nullable
    String dataset;
}