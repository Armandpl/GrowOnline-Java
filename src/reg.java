import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class reg
{
  public static String[] s(String regexToCompile, String sourceDocument)
  {
    Pattern urlPattern = Pattern.compile(regexToCompile);
    Matcher urlMatcher = urlPattern.matcher(sourceDocument);
    

    return extractRegexResults(urlMatcher);
  }
  
  private static String[] extractRegexResults(Matcher regexMatcher)
  {
    ArrayList<String> extractedResults = new ArrayList();
    while (regexMatcher.find()) {
      extractedResults.add(regexMatcher.group());
    }
    return (String[])extractedResults.toArray(new String[extractedResults.size()]);
  }
}
