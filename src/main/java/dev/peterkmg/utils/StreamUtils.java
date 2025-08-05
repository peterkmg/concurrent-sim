package dev.peterkmg.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class StreamUtils {
  public static <T> Collector<T, ?, List<T>> toShuffledList() {
    return Collectors.collectingAndThen(Collectors.toCollection(ArrayList::new), list -> {
      Collections.shuffle(list);
      return list;
    });
  }
}
