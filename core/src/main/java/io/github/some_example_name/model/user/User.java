package io.github.some_example_name.model.user;


import io.github.some_example_name.model.enums.BackpackType;
import io.github.some_example_name.model.enums.TrashCanType;
import io.github.some_example_name.model.user.inventory.Backpack;
import io.github.some_example_name.model.user.inventory.TrashCan;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User {
    private String username;
    private String password;
    private String hashedPassword;
    private String nickname;
    private String email;
    private String gender;
    private String securityQuestion;
    private String hashedSecurityAnswer;
    private int score = 0;
    private int highestScore = 0;
    private int gameCount = 0;
    private int energy;
    private io.github.some_example_name.model.user.inventory.Inventory inventory;
    private Wallet wallet;


    public User(String username, String password, String nickname,
                String email, String gender, String securityQuestion, String securityAnswer) {
        this.username = username;
        this.password = password;
        this.hashedPassword = hashSHA256(password);
        this.nickname = nickname;
        this.email = email;
        this.gender = gender;
        this.securityQuestion = securityQuestion;
        if (securityAnswer != null) {
            this.hashedSecurityAnswer = hashSHA256(securityAnswer);
        } else {
            this.hashedSecurityAnswer = null;
        }
        this.hashedSecurityAnswer = hashSHA256(securityAnswer);
        this.energy = 200;
//        this.inventory = new Inventory(new Backpack(BackpackType.SMALL), new TrashCan(TrashCanType.BASIC));
        this.wallet = new Wallet();
    }

    private String hashSHA256(String input) {
        if (input == null) {
            System.out.println("Input is null");
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public String getHashedSecurityAnswer() {
        return hashedSecurityAnswer;
    }

    public boolean checkPassword(String newPassword) {
        return newPassword.equals(password);
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public void changeNickname(String newNick) {
        this.nickname = newNick;
    }

    public void changeEmail(String newEmail) {
        this.email = newEmail;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }


    public int getScore() {
        return score;
    }

    public int getGameCount() {
        return gameCount;
    }

    public void increaseScore(int amount) {
        this.score += amount;
    }

    public void incrementGameCount() {
        this.gameCount++;
    }

//    public Inventory getInventory() {
//        return inventory;
//    }

    public Wallet getWallet() {
        return wallet;
    }

    private EnergySystem energySystem = new EnergySystem();

    public void performAction(int energyCost) {
        energySystem.consume(energyCost);
        if (energySystem.isPassedOut()) {
        }

    }

    public int getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(int highestScore) {
        this.highestScore = highestScore;
    }
}
