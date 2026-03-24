public class UseCase11PalindromeChecker {
      public static void main(String[] args) {
        String input = "racecar";

        PalindromeService palindromeService = new PalindromeService();
        boolean isPalindrome = palindromeService.checkPalindrome(input);

        System.out.println("Input : " + input);
        System.out.println("Is Palindrome? : " + isPalindrome);
    }
}

class PalindromeService {
    public boolean checkPalindrome(String input) {
        if (input == null) {
            return false;
        }

        int start = 0;
        int end = input.length() - 1;

        while (start < end) {
            if (input.charAt(start) != input.charAt(end)) {
                return false;
            }
            start++;
            end--;
        }

        return true;
    }
}
