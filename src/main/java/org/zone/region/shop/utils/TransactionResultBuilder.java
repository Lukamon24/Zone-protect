package org.zone.region.shop.utils;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionType;
import org.spongepowered.api.util.Identifiable;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class TransactionResultBuilder {

    private Account account;
    private Currency currency;
    private BigDecimal amount;
    private Set<Context> context;
    private ResultType resultType;
    private TransactionType transactionType;

    public Account getAccount() {
        return this.account;
    }

    public TransactionResultBuilder setAccount(Account account) {
        this.account = account;
        return this;
    }

    public TransactionResultBuilder setAccount(Identifiable player) {
        this.account = Sponge
                .serviceProvider()
                .provide(EconomyService.class)
                .orElseThrow(() -> new IllegalStateException("No eco plugin found"))
                .findOrCreateAccount(player.uniqueId())
                .orElseThrow(() -> new IllegalStateException(
                        "Could not find a account for the user of " + player.uniqueId()));
        return this;
    }

    public Currency getCurrency() {
        return this.currency;
    }

    public TransactionResultBuilder setCurrency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public TransactionResultBuilder setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public Set<Context> getContext() {
        return this.context;
    }

    public TransactionResultBuilder setContext(Set<Context> context) {
        this.context = context;
        return this;
    }

    public TransactionResultBuilder setContext(Collection<Context> context) {
        this.context = new HashSet<>(context);
        return this;
    }

    public TransactionResultBuilder addContext(Context context) {
        this.context.add(context);
        return this;
    }

    public ResultType getResultType() {
        return this.resultType;
    }

    public TransactionResultBuilder setResultType(ResultType resultType) {
        this.resultType = resultType;
        return this;
    }

    public TransactionType getTransactionType() {
        return this.transactionType;
    }

    public TransactionResultBuilder setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
        return this;
    }

    public ZoneTransactionResult build() {
        return new ZoneTransactionResult(this);
    }
}
