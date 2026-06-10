package securities.back.stocks.api.v1.stock.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import securities.back.stocks.api.v1.stock.dto.controller.test.UpdateFieldValueDto;
import securities.back.stocks.api.v1.stock.service.TestService;
import securities.back.stocks.common.exception.dto.ExceptionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Tag(name = "Test", description = "테스트용 정보조작 API")
@RequestMapping(value = "/api/v1/test")
public class TestController {
    private final TestService testService;

    @PatchMapping(value = "/filed-value")
    @Operation(summary = "특정 데이터 변경 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 종목의 특정 값 변경 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "E002: 올바르지 않은 종목코드, 필드명\n\n" +
                                                                "S001: 가격이 상한가/하한가를 벗어남",
                    content = @Content(schema = @Schema(implementation = ExceptionResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "E001: 정의되지 않은 필드명",
                    content = @Content(schema = @Schema(implementation = ExceptionResponseDto.class)))
    })
    public ResponseEntity<Void> updateValue(@Valid@RequestBody UpdateFieldValueDto.Request reqDto) throws Exception {
        testService.updateFieldValue(reqDto.toServiceDto());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping(value = "/filed-value/random")
    @Operation(summary = "데이터 랜덤 변경 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "랜덤 값 변경 성공", useReturnTypeSchema = true)
    })
    public ResponseEntity<Void> updateValueRandom() throws Exception {
        testService.updateFieldValueRandom();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
