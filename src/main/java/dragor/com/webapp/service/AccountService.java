package dragor.com.webapp.service;

import dragor.com.webapp.dto.AccountDto;
import dragor.com.webapp.dto.ConvertDto;
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
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountHelper accountHelper;
    private final AccountRepository accountRepository;
    private final ExchangeRateService exchangeRateService;

    public Account createAccount(AccountDto accountDTO, User user) throws Exception {
        System.out.println("Creating account for user: " + user.getUid());
        return accountHelper.createAccount(accountDTO, user);
    }

    public List<Account> getUserAccounts(String uid) {
        System.out.println("Fetching user accounts for UID: " + uid);
        List<Account> accounts = accountRepository.findAllByOwnerUid(uid);
        System.out.println("Found " + accounts.size() + " accounts for user " + uid);
        return accounts;
    }

    public Transaction transferFunds(TransferDto transferDto, User user) throws Exception {
        System.out.println("Initiating transfer for user: " + user.getUid() + " with amount: " + transferDto.getAmount());

        var senderAccount = accountRepository.findByCodeAndOwnerUid(transferDto.getCode(), user.getUid())
                .orElseThrow(() -> new UnsupportedOperationException("Account of type currency does not exist for the user"));

        var recipientAccount = accountRepository.findByAccountNumber(transferDto.getRecipientAccountNumber())
                .orElseThrow(() -> new UnsupportedOperationException("Recipient account not found"));

        System.out.println("Transfer validated. Proceeding with funds transfer...");
        return accountHelper.performTransfer(senderAccount, recipientAccount, transferDto.getAmount(), user);
    }

    public Map<String, Double> getExchangeRate() {
        System.out.println("Fetching exchange rates...");
        return exchangeRateService.getRates();
    }

    public Transaction convertCurrencies(ConvertDto convertDto, User user) throws Exception {
        System.out.println("Converting currency for user: " + user.getUid());
        return accountHelper.convertCurrency(convertDto, user);
    }
}
