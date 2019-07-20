import java.io.Serializable;
import java.util.List;

public class TestFile {

  @SafeVarargs
  public final <E> E elements(E... args) {
    return null;
  }

  public void test1() {
    var v = elements("a", x());
  }

  public List<Integer> & Serializable & CharSequence x() {
    return null;
  }
}