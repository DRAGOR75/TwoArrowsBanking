package dragor.com.webapp.service.helper;

import dragor.com.webapp.dto.AccountDto;
import dragor.com.webapp.entity.Account;
import dragor.com.webapp.entity.User;
import dragor.com.webapp.repository.AccountRepository;
import dragor.com.webapp.util.RandomUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.naming.OperationNotSupportedException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Getter
public class AccountHelper {
    private final AccountRepository accountRepository;

    private final Map<String, String> CURRENCIES = Map.of(
            "USD", "United States Dollar",
            "EUR", "Euro",
            "INR", "Indian Rupee",
            "JPY", "Japanese Yen"
    );

    public Account createAccount(AccountDto accountDTO, User user) throws Exception {
        long accountNumber;
        validateAccountNotExistsForUser( accountDTO.getCode(), user.getUid());
        do {
            accountNumber = new RandomUtil().generateRandom(10);
        } while (accountRepository.existsByAccountNumber(accountNumber));

        var account = Account.builder()
                .accountNumber(accountNumber)
                .balance(1000)
                .owner(user)
                .code(accountDTO.getCode())
                .symbol(accountDTO.getSymbol())
                .label(CURRENCIES.get(accountDTO.getCode()))
                .build();
        return accountRepository.save(account);

    }

    public void validateAccountNotExistsForUser(String code,String uid) throws Exception {
        if(accountRepository.existsByOwnerUidAndCode(uid,code)){
            throw new Exception("Account of this type already exists for this user");
        }
    }
}
