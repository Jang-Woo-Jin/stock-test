package kakaopay_securities.back.stocks.domain.model.multiple_key;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
public class PriceId implements Serializable {
    private String date;
    private String stock;
}
