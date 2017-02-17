package org.viepik.sandbox.palindrome;

public class PalindromeCalculationResult {
    public final long calcTime;
    public final Long palindrome;
    public final Long[] nextToCheck;

    public PalindromeCalculationResult(long calcTime, Long palindrome, Long[] nextToCheck) {
        this.calcTime = calcTime;
        this.palindrome = palindrome;
        this.nextToCheck = nextToCheck;
    }
}
