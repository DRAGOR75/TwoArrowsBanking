package dragor.com.webapp.service;

import dragor.com.webapp.entity.Card;
import dragor.com.webapp.entity.Transaction;
import dragor.com.webapp.entity.Type;
import dragor.com.webapp.entity.User;
import dragor.com.webapp.repository.AccountRepository;
import dragor.com.webapp.repository.TransactionRepository;
import dragor.com.webapp.repository.WalletRepository;
import dragor.com.webapp.service.helper.AccountHelper;
import dragor.com.webapp.util.RandomUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class WalletService {
    private final WalletRepository walletRepository;
    private final AccountHelper accountHelper;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public Card getWallet(User user) {
        return walletRepository.findByOwnerUid(user.getUid()).orElseThrow();
    }
    public Card createWallet(double amount,User user) throws Exception {
        if (amount<100){
            throw new IllegalArgumentException("amount must be greater than ₹100");
        }
        if(accountRepository.existsByOwnerUidAndCode("INR",user.getUid())){
            throw new IllegalArgumentException("INR account not found for this user so wallet cannot be created");
        }
        var INRaccount = accountRepository.findByCodeAndOwnerUid("INR",user.getUid()).orElseThrow();
        accountHelper.validateSufficientFunds(INRaccount,amount);
        INRaccount.setBalance(INRaccount.getBalance()-amount);
        long walletNumber ;
        do{
            walletNumber=generateWalletNumber();
        }while(walletRepository.existsByCardNumber(walletNumber));
        Card card = Card.builder()
                .cardHolder(user.getFirstName()+" " + user.getLastName())
                .cardNumber(walletNumber)
                .balance(amount-1)
                .build();
        accountRepository.save(INRaccount);
        card= walletRepository.save(card);
        accountHelper.createAccountTransaction(1, Type.WITHDRAWAL,0.00,user,INRaccount);
        accountHelper.createAccountTransaction(amount-1, Type.WITHDRAWAL,0.00,user,INRaccount);
        createWalletTransaction(amount,Type.WITHDRAWAL,0.00,user,card);
        accountRepository.save(INRaccount);
        return card;


    }
    private long generateWalletNumber(){
        return new RandomUtil().generateRandom(16);
    }

    public Transaction creditWallet(double amount, User user) {
        var INRaccount = accountRepository.findByCodeAndOwnerUid("INR",user.getUid()).orElseThrow();
        INRaccount.setBalance(INRaccount.getBalance()-amount);
        accountHelper.createAccountTransaction(amount, Type.WITHDRAWAL,0.00,user,INRaccount);
        var wallet = user.getCard();
        wallet.setBalance(wallet.getBalance()+amount);
        walletRepository.save(wallet);
        return createWalletTransaction(amount,Type.CREDIT,0.00,user,wallet);
    }

    public Transaction debitWallet(double amount, User user) {
        var INRaccount = accountRepository.findByCodeAndOwnerUid("INR",user.getUid()).orElseThrow();
        INRaccount.setBalance(INRaccount.getBalance()+amount);
        accountHelper.createAccountTransaction(amount-1, Type.WITHDRAWAL,0.00,user,INRaccount);
        var wallet = user.getCard();
        wallet.setBalance(wallet.getBalance()-amount);
        walletRepository.save(wallet);
        return createWalletTransaction(amount,Type.DEBIT,0.00,user,wallet);
    }
    private Transaction createWalletTransaction(double amount, Type type,double tfees,User user,Card card) {
        var tx=Transaction.builder()
                .tfees(tfees)
                .amount(amount)
                .type(type)
                .owner(user)
                .card(card)

                .build();
        return transactionRepository.save(tx);

    }
}
