package securities.back.stocks.domain.dao;

import securities.back.stocks.domain.model.Price;
import securities.back.stocks.domain.model.multiple_key.PriceId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceDao extends JpaRepository<Price, PriceId> {
}
