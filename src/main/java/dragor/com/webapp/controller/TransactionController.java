package dragor.com.webapp.controller;

import dragor.com.webapp.entity.Transaction;
import dragor.com.webapp.entity.User;
import dragor.com.webapp.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions") // Fixed mapping
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Retrieves all transactions for the authenticated user.
     *
     * @param page The page number for pagination.
     * @param auth The current authentication object.
     * @return A list of transactions.
     */
    @GetMapping
    public List<Transaction> getAllTransactions(@RequestParam String page, Authentication auth) {
        return transactionService.getAllTransactions(page, (User) auth.getPrincipal());
    }

    /**
     * Retrieves transactions for a specific card.
     *
     * @param cardId The ID of the card.
     * @param page The page number for pagination.
     * @param auth The current authentication object.
     * @return A list of transactions.
     */
    @GetMapping("/c/{cardId}")
    public List<Transaction> getTransactionsByCardId(@PathVariable String cardId, @RequestParam String page, Authentication auth) {
        return transactionService.getTransactionsByCardId(cardId, page, (User) auth.getPrincipal());
    }

    /**
     * Retrieves transactions for a specific account.
     *
     * @param accountId The ID of the account.
     * @param page The page number for pagination.
     * @param auth The current authentication object.
     * @return A list of transactions.
     */
    @GetMapping("/a/{accountId}")
    public List<Transaction> getTransactionsByAccountId(@PathVariable String accountId, @RequestParam String page, Authentication auth) {
        return transactionService.getTransactionsByAccountId(accountId, page, (User) auth.getPrincipal());
    }
}
