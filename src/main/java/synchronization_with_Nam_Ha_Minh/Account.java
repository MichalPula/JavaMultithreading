package synchronization_with_Nam_Ha_Minh;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Account {
    private int balance = 0;

    public Account(int balance) {
        this.balance = balance;
    }

    public void withdraw(int amount) {
        this.balance -= amount;
    }

    public void deposit(int amount) {
        this.balance += amount;
    }
}
