package util.others;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GenerateSecurePasswordUtil {

  private static final Random RANDOM = new SecureRandom();
  private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  private static final int PASSWORD_LENGTH = 10;


  public static String generatePassword() {
    return IntStream.rangeClosed(1, PASSWORD_LENGTH).mapToObj(number -> Character.toString(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length()))))
            .collect(Collectors.joining());
  }
}
