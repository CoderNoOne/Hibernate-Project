package helper;

import java.util.Optional;
import java.util.function.Supplier;

public class OptionalHelper<T> {

  private final Optional<T> optional;

  private OptionalHelper(Optional<T> optional) {
    this.optional = optional;
  }

  public static <T> OptionalHelper<T> of(Optional<T> optional) {
    return new OptionalHelper<>(optional);
  }

  public T ifNotPresent(Supplier<T> supplier) {
    if (optional.isEmpty()) {
      return supplier.get();
    }
    return optional.get();
  }

}
