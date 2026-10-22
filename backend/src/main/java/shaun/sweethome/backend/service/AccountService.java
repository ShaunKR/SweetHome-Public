package shaun.sweethome.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shaun.sweethome.backend.web.dto.AccountRequestDto;
import shaun.sweethome.backend.web.dto.AccountResponseDto;
import shaun.sweethome.backend.entity.AccountTransaction;
import shaun.sweethome.backend.repository.AccountTransactionRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class AccountService {

    private final AccountTransactionRepository repository;

    public AccountService(AccountTransactionRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<AccountResponseDto> getList(LocalDate from, LocalDate to) {
        return repository.findByTransactionDateBetweenOrderByTransactionDateDescIdDesc(from, to)
                .stream()
                .map(AccountResponseDto::new)
                .toList();
    }

    @Transactional
    public AccountResponseDto create(AccountRequestDto request) {
        AccountTransaction entity = new AccountTransaction();

        entity.setTransactionDate(request.getTransactionDate());
        entity.setTransactionType(request.getTransactionType());
        entity.setCategory(request.getCategory());
        entity.setTitle(request.getTitle());
        entity.setAmount(request.getAmount());
        entity.setPaymentMethod(request.getPaymentMethod());
        entity.setMemo(request.getMemo());

        AccountTransaction saved = repository.save(entity);
        return new AccountResponseDto(saved);
    }

    @Transactional
    public AccountResponseDto update(Long id, AccountRequestDto request) {
        AccountTransaction entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("가계부 내역을 찾을 수 없습니다. id=" + id));

        entity.setTransactionDate(request.getTransactionDate());
        entity.setTransactionType(request.getTransactionType());
        entity.setCategory(request.getCategory());
        entity.setTitle(request.getTitle());
        entity.setAmount(request.getAmount());
        entity.setPaymentMethod(request.getPaymentMethod());
        entity.setMemo(request.getMemo());

        return new AccountResponseDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("가계부 내역을 찾을 수 없습니다. id=" + id);
        }
        repository.deleteById(id);
    }
}
