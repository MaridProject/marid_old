import java.lang.constant.Constable;

public class TestFile {

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
}