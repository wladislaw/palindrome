package org.viepik.sandbox.palindrome;

public class PalindromeCalculatorImpl implements PalindromeCalculator<Long, PalindromeCalculationResult> {
    @Override
    public PalindromeCalculationResult calculatePalindrome(Long input) {
        long startTime = System.nanoTime();
        Long[] nextToCheck;
        char[] binaryString = Long.toBinaryString(input).toCharArray();
        if (binaryString.length % 2 == 0) {
            nextToCheck = new Long[2];
            nextToCheck[0] = insertOneByte(binaryString, (byte) 0);
            nextToCheck[1] = insertOneByte(binaryString, (byte) 1);
        } else {
            nextToCheck = new Long[]{insertOneByte(binaryString, (byte) (binaryString[binaryString.length / 2] - 48))};
        }
        return new PalindromeCalculationResult(System.nanoTime() - startTime, isPalindrome(input) ? input : null, nextToCheck);
    }

    private Long insertOneByte(char[] binaryString, byte b) {
        char[] newBinaryPalindrome = new char[binaryString.length + 1];
        System.arraycopy(binaryString, 0, newBinaryPalindrome, 0, binaryString.length / 2);
        newBinaryPalindrome[binaryString.length / 2] = (char) (b + 48);
        System.arraycopy(binaryString, binaryString.length / 2 + 1 - 1, newBinaryPalindrome,
                binaryString.length / 2 + 1, binaryString.length - binaryString.length / 2);
        return Long.valueOf(String.valueOf(newBinaryPalindrome), 2);
    }

    @Override
    public boolean isPalindrome(Long palindrome) {
        long temp = palindrome;
        long reminder, revers = 0;

        while (temp > 0) {
            reminder = temp % 10;
            revers = (revers * 10) + reminder;
            temp /= 10;
        }
        return revers == palindrome;
    }
}
