package com.jpmc.midascore.entity;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import jakarta.persistence.*;
import org.apache.catalina.User;


@Entity
@Table(name = "Transactions")
public class TransactionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "id", nullable = false)
    private UserRecord senderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", referencedColumnName = "id", nullable = false)
    private UserRecord recipientId;

    @Column(nullable = false)
    private float amount;

    public TransactionRecord() {
    }

    public TransactionRecord(UserRecord senderId, UserRecord recipientId, float amount) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.amount = amount;
    }


    public long getId() {
        return id;
    }
    public UserRecord getSender() {
        return senderId;
    }
    public void setSender(UserRecord sender) {
        this.senderId = sender;
    }
    public UserRecord getRecipient() {
        return recipientId;
    }
    public void setRecipient(UserRecord recipient) { this.recipientId = recipient;}
    public float getAmount() {
        return amount;
    }
    public void setAmount(float amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "TransactionRecord {id=" + id + ", senderId=" + senderId + ", recipientId=" + recipientId + ", amount=" + amount + "}";
    }




}
