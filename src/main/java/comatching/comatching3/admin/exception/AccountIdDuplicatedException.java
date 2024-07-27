package comatching.comatching3.admin.exception;

public class AccountIdDuplicatedException extends RuntimeException {
    public AccountIdDuplicatedException(String message) {
        super(message);
    }
}
