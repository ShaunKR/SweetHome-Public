package shaun.sweethome.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import shaun.sweethome.backend.web.dto.AccountRequestDto;
import shaun.sweethome.backend.web.dto.AccountResponseDto;
import shaun.sweethome.backend.service.AccountService;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "가계부 API", description = "수입/지출 내역 관리 API")
@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @Operation(summary = "가계부 내역 조회", description = "시작일과 종료일 기준으로 가계부 내역을 조회합니다.")
    @GetMapping
    public List<AccountResponseDto> getList(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        return service.getList(from, to);
    }

    @Operation(summary = "가계부 내역 등록")
    @PostMapping
    public AccountResponseDto create(@Valid @RequestBody AccountRequestDto request) {
        return service.create(request);
    }

    @Operation(summary = "가계부 내역 수정")
    @PutMapping("/{id}")
    public AccountResponseDto update(
            @PathVariable Long id,
            @Valid @RequestBody AccountRequestDto request
    ) {
        return service.update(id, request);
    }

    @Operation(summary = "가계부 내역 삭제")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
