import java.lang.constant.Constable;
import java.util.HashMap;
import java.util.Map;

public class TestFile {

  @SafeVarargs
  public final <E> E elements(E... args) {
    return null;
  }

  public void test1() {
    var v = elements("a", 1);
  }
}