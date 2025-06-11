package dragor.com.webapp.controller;


import dragor.com.webapp.entity.Card;
import dragor.com.webapp.entity.Transaction;
import dragor.com.webapp.entity.User;
import dragor.com.webapp.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet") // Fixed mapping
@RequiredArgsConstructor
public class WalletController {
    public final WalletService walletService;

    @GetMapping
    ResponseEntity<Card>getWallet(Authentication authentication){
        var user = (User) authentication.getPrincipal();
        return ResponseEntity.ok( walletService.getWallet(user));

    }

    @PostMapping("/create")
    public ResponseEntity<Card> createWallet(@RequestParam double amount, Authentication authentication) throws Exception {
        var user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(walletService.createWallet(amount,user));
    }

    @PostMapping("/credit")
    public ResponseEntity<Transaction> creditWallet(@RequestParam double amount,Authentication authentication){
        var user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(walletService.creditWallet(amount,user));
    }
    @PostMapping("/debit")
    public ResponseEntity<Transaction> debitWallet(@RequestParam double amount,Authentication authentication){
        var user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(walletService.debitWallet(amount,user));
    }
}
