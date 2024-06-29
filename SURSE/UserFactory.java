package org.example;

public class UserFactory {
    // Applying Factory pattern to initialize users.
    public static User<Comparable<Object>> factory(AccountType acc) {
        if(acc.equals(AccountType.Regular)) {
            return new Regular<>();
        }
        if(acc.equals(AccountType.Admin)) {
            return new Admin<>();
        }
        if(acc.equals(AccountType.Contributor)) {
            return new Contributor<>();
        }
        return null;
    }
}
