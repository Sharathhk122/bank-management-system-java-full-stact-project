// AccountService.java
package com.bmsp.bmsp.service.account;

import com.bmsp.bmsp.dto.request.account.AccountCreateRequest;
import com.bmsp.bmsp.dto.response.account.AccountResponse;
import com.bmsp.bmsp.model.auth.User;

import java.util.List;

public interface AccountService {
    AccountResponse createAccount(AccountCreateRequest request, User user);
    List<AccountResponse> getUserAccounts(User user);
    AccountResponse getAccountDetails(String accountNumber, User user);
    void deleteAccount(String accountNumber, User user);
}