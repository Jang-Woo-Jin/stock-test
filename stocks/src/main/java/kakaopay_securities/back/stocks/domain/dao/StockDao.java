package kakaopay_securities.back.stocks.domain.dao;

import kakaopay_securities.back.stocks.domain.model.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockDao extends JpaRepository<Stock, String> {
    Page<Stock> findAll(Pageable page);
}
