package dragor.com.webapp.service;

import dragor.com.webapp.dto.AccountDto;
import dragor.com.webapp.dto.TransferDto;
import dragor.com.webapp.entity.Account;
import dragor.com.webapp.entity.Transaction;
import dragor.com.webapp.entity.User;
import dragor.com.webapp.repository.AccountRepository;
import dragor.com.webapp.service.helper.AccountHelper;
import dragor.com.webapp.util.RandomUtil;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountHelper accountHelper;

    public final AccountRepository accountRepository;

    public Account createAccount(AccountDto accountDTO,User user) throws Exception {
        return accountHelper.createAccount(accountDTO,user);

    }

    public List<Account> getUserAccounts(String uid) {
        return accountRepository.findAllByOwnerUid(uid);
    }

    public Transaction transferFunds(TransferDto transferDto, User user) throws Exception {
        var senderAccount =accountRepository.findByCodeAndOwnerUid(transferDto.getCode(),user.getUid())
                .orElseThrow(()->new UnsupportedOperationException("account of type currency does not exists for the user"));

        var recipientAccount =accountRepository.findByAccountNumber(transferDto.getRecipientAccountNumber())
                .orElseThrow();

        return accountHelper.performTransfer(senderAccount, recipientAccount,transferDto.getAmount(),user);
    }
}
