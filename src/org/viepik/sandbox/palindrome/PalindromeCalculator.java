package org.viepik.sandbox.palindrome;

public interface PalindromeCalculator<I, O> {
    O calculatePalindrome(I input);

    boolean isPalindrome(Long palindrome);
}
