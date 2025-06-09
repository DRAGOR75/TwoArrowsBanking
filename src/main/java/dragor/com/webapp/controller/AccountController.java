package dragor.com.webapp.controller;

import dragor.com.webapp.dto.AccountDto;
import dragor.com.webapp.dto.ConvertDto;
import dragor.com.webapp.dto.TransferDto;
import dragor.com.webapp.entity.Account;
import dragor.com.webapp.entity.Transaction;
import dragor.com.webapp.entity.User;
import dragor.com.webapp.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/accounts") // Fixed mapping
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody AccountDto accountdto, Authentication authentication) throws Exception {
        var user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(accountService.createAccount(accountdto,user));
    }
    @GetMapping
    public ResponseEntity<List<Account>> getUsersAccounts(Authentication authentication){
        var user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(accountService.getUserAccounts(user.getUid()));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Transaction>transferFunds(@RequestBody TransferDto transferDto, Authentication authentication) throws Exception {
        var user=(User)authentication.getPrincipal();
        return ResponseEntity.ok(accountService.transferFunds(transferDto, user));
    }
    @GetMapping("/rates")
    public ResponseEntity<Map<String,Double>>getExchangeRates(){
        return ResponseEntity.ok(accountService.getExchangeRate());
    }

    @PostMapping("/convert")
    public ResponseEntity<Transaction>conevrtCurrencies(@RequestBody ConvertDto convertDto,Authentication authentication)throws Exception{
        var user=(User)authentication.getPrincipal();
        return ResponseEntity.ok(accountService.convertCurrencies(convertDto,user));
    }

}
