package kakaopay_securities.back.stocks.common.extend;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableDto {
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Request {
        @Schema(title = "페이지", defaultValue = "0")
        @Min(0)
        private Integer page = 0;
        @Schema(title = "크기", defaultValue = "15")
        @Min(1)
        private Integer size = 15;

        public void setPageData(int page, int size) {
            this.page = page;
            this.size = size;
        }

        public Pageable getPageRequestWithSort(Sort sort) {
            return PageRequest.of(this.page, this.size, sort);
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Response {
        private int page;
        private long totalRecords;
    }
}
