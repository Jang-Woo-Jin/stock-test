package kakaopay_securities.back.stocks.domain.model;

import jakarta.annotation.Resource;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Entity
@Resource
@Table(name = "Stock")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Stock {
    @Id
    @Column(length=20, nullable=false)
    @Comment(value = "종목코드")
    private String code;

    @Column(length=200, nullable=false)
    @Comment(value = "종목명")
    private String name;

    @Column(nullable=false)
    @Comment(value = "조회수")
    private int viewCount = 0;

    public Stock(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
