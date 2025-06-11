package dragor.com.webapp.service.helper;

import dragor.com.webapp.dto.AccountDto;
import dragor.com.webapp.entity.*;
import dragor.com.webapp.repository.AccountRepository;
import dragor.com.webapp.repository.TransactionRepository;
import dragor.com.webapp.dto.ConvertDto;
import dragor.com.webapp.service.ExchangeRateService;
import dragor.com.webapp.util.RandomUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Getter
public class AccountHelper {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final ExchangeRateService exchangeRateService;

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
                .accountName(user.getFirstName()+" "+user.getLastName())
                .balance(1000)
                .owner(user)
                .code(accountDTO.getCode())
                .symbol(accountDTO.getSymbol())
                .label(CURRENCIES.get(accountDTO.getCode()))
                .build();
        return accountRepository.save(account);

    }
    public Transaction performTransfer(Account senderAccount, Account recipientAccount, double amount,User user) throws Exception {
        validateSufficientFunds(senderAccount, (amount*1.01));
        senderAccount.setBalance(senderAccount.getBalance()-amount*1.01);
        recipientAccount.setBalance(recipientAccount.getBalance()+amount);
        accountRepository.saveAll(List.of(senderAccount, recipientAccount));
        var senderTransaction=Transaction.builder()
                .account(senderAccount)
                .status(Status.COMPLETED)
                .type(Type.WITHDRAWAL)
                .tfees(amount*0.01)
                .owner(user)
                .build();
        var receiverTransaction=Transaction.builder()
                .account(senderAccount)
                .status(Status.COMPLETED)
                .type(Type.DEPOSIT)
                .amount(amount)
                .owner(recipientAccount.getOwner())
                .build();

        return transactionRepository.saveAll(List.of(senderTransaction,receiverTransaction)).getFirst();

    }
    public void validateAccountNotExistsForUser(String code,String uid) throws Exception {
        if(accountRepository.existsByOwnerUidAndCode(uid,code)){
            throw new Exception("Account of this type already exists for this user");
        }
    }
    
    public void validateAccountOwner(Account account, User user) throws Exception {
        if(!account.getOwner().getUid().equals(user.getUid())){
            throw new Exception("Invalid account number");
        }
    }

    public void validateSufficientFunds(Account account,double amount) throws Exception {
        if(account.getBalance()<amount){
            throw new Exception("Insufficient funds");
        }
    }
    public void validateAmmount(double amount) throws Exception {
        if(amount<=0){
            throw new Exception("Insufficient amount");
        }
    }
    public void validateDifferentCurrency(ConvertDto convertDto) throws Exception {
        if(convertDto.getToCurrency().equals (convertDto.getFromCurrency())){
            throw new Exception("conversion between same currencies is not allowed");
        }

    }
    public void validateAccountOwnership(ConvertDto convertDto ,String uid) throws Exception {
        accountRepository.findByCodeAndOwnerUid(convertDto.getFromCurrency(),uid).orElseThrow();
        accountRepository.findByCodeAndOwnerUid(convertDto.getToCurrency(),uid).orElseThrow();
    }

    public void validateConversion(ConvertDto convertDto,String uid) throws Exception {
        validateDifferentCurrency(convertDto);
        validateAccountOwnership(convertDto,uid);
        validateAmmount(convertDto.getAmount());
        validateSufficientFunds(accountRepository.findByCodeAndOwnerUid(convertDto.getFromCurrency(),uid).get(),convertDto.getAmount());


    }
    public Transaction convertCurrency(ConvertDto convertDto,User user) throws Exception {
        validateConversion(convertDto,user.getUid());
        var rates=exchangeRateService.getRates();
        var sendingRates=rates.get(convertDto.getFromCurrency());
        var receivingRates=rates.get(convertDto.getToCurrency());
        var computedAmount=(receivingRates/sendingRates)*convertDto.getAmount();
        var fromAccount= accountRepository.findByCodeAndOwnerUid(convertDto.getFromCurrency(),user.getUid()).get();
        var toAccount= accountRepository.findByCodeAndOwnerUid(convertDto.getToCurrency(), user.getUid()).get();
        fromAccount.setBalance(fromAccount.getBalance()-(convertDto.getAmount()*1.01));
        toAccount.setBalance(toAccount.getBalance()+computedAmount);
        accountRepository.saveAll(List.of(fromAccount, toAccount));
        var transaction=Transaction.builder()
                .owner(user)
                .amount(convertDto.getAmount())
                .tfees(convertDto.getAmount()*0.01)
                .account(fromAccount)
                .status(Status.COMPLETED)
                .type(Type.CONVERSION)
                .build();
        return transactionRepository.save(transaction);
    }

    public Transaction createAccountTransaction(double amount,Type type,double tfees,User user,Account account) {
        var tx=Transaction.builder()
                .tfees(tfees)
                .amount(amount)
                .type(type)
                .status(Status.COMPLETED)
                .owner(user)
                .account(account)
                .build();
        return transactionRepository.save(tx);
    }
}
