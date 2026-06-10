package kakaopay_securities.back.stocks.domain.model;

import jakarta.annotation.Resource;
import jakarta.persistence.*;
import kakaopay_securities.back.stocks.domain.model.multiple_key.PriceId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Entity
@Resource
@Table(name = "Price")
@IdClass(PriceId.class)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Price {
    @Id
    @Column(length=8, nullable=false)
    @Comment(value = "기준일")
    private String date;

    @Id
    @ManyToOne
    @JoinColumn(name = "code", referencedColumnName = "code")
    private Stock stock;

    @Column(nullable=false)
    @Comment(value = "현재가")
    private int price;

    @Column(nullable=false)
    @Comment(value = "전일 종가")
    private int previousPrice;

    @Column(nullable=false)
    @Comment(value = "전일 대비")
    private int diffPrice;

    @Column(nullable=false)
    @Comment(value = "시가")
    private int startPrice;

    @Column(nullable=false)
    @Comment(value = "고가")
    private int highPrice;

    @Column(nullable=false)
    @Comment(value = "저가")
    private int lowPrice;

    @Column(nullable=false)
    @Comment(value = "상한가")
    private int upperLimitPrice;

    @Column(nullable=false)
    @Comment(value = "히힌기")
    private int lowerLimitPrice;

    @Column(nullable=false)
    @Comment(value = "거래량")
    private long volume;
}

