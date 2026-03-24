public class UseCase2PalindromeChecker {
     public static void main(String[] args) {
        String inputText = "madam";
        boolean isPalindrome = true;

        int left = 0;
        int right = inputText.length() - 1;

        while (left < right) {
            if (inputText.charAt(left) != inputText.charAt(right)) {
                isPalindrome = false;
                break;
            }
            left++;
            right--;
        }

        System.out.println("Input text: " + inputText);
        System.out.println("Is it a palindrome? : " + isPalindrome);
    }
}
