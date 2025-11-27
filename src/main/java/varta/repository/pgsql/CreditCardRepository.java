package varta.repository.pgsql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import varta.model.pgsql.CreditCard;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {

    public CreditCard findByExternalCardId(String externalCardId);
}
