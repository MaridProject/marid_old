import java.lang.constant.Constable;
import java.util.HashMap;
import java.util.Map;

public class TestFile {

  private final Map<String, Object> map = new HashMap<>();

  @SafeVarargs
  public final <E> E elements(E... args) {
    return null;
  }

  public void test1() {
    var v = elements("a", 1);
  }

  public void test2() {
    var v = elements("a", (Comparable<String> & Constable) null);
  }

  public void test3() {
    var v = elements("a", (Object & Comparable<String> & Constable) null);
  }
}