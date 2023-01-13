package streamlines.jwtsample.jwt;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import streamlines.jwtsample.account.Account;

import java.util.List;

public class AccountDetails extends User {
    private final Account account;

    public AccountDetails(Account account) {
        super(account.getEmail(), account.getPassword(), List.of(new SimpleGrantedAuthority("USER")));
        System.out.println("AccountDetails.AccountDetails");
        System.out.println("account : " + account);
        this.account = account;
    }
}
