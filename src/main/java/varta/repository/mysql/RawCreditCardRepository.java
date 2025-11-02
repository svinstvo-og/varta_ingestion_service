package varta.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import varta.model.mysql.RawCreditCard;

@Repository
public interface RawCreditCardRepository extends JpaRepository<RawCreditCard, Long> {

}
