import java.util.*;

/** Exercises the web adapter without opening a socket. */
public class WebAdapterTest {
    private static int checks;
    private static void check(boolean ok, String message) {
        checks++;
        if (!ok) throw new AssertionError(message);
    }
    private static void rejects(Map<String,String> query) {
        try { FarmWebServer.simulate(query); throw new AssertionError("Input should be rejected"); }
        catch (IllegalArgumentException expected) { checks++; }
    }
    public static void main(String[] args) {
        Map<String,String> query = FarmWebServer.parseQuery("name=Sample+Farm&state=VIC&sheep=80&lambs=20&seed=42");
        String json = FarmWebServer.simulate(query);
        check(json.equals(FarmWebServer.simulate(query)), "Endpoint results must replay exactly");
        check(json.contains("\"best\":2"), "Seed 42 selects two guards");
        check(json.contains("\"meanCost\":3220.0"), "No-guard mean must match CLI");
        check(json.contains("\"sheepLost\":3.3"), "Two-guard sheep mean must match CLI");
        check(json.contains("\"lambsLost\":0.8"), "Two-guard lamb mean must match CLI");
        check(json.contains("\"costs\":["), "Response includes trial costs");
        check(json.contains("\"sheepLosses\":["), "Response includes trial loss counts");
        check(FarmWebServer.quote("\"\\\n").equals("\"\\\"\\\\\\u000a\""), "JSON escaping");
        for (String state : List.of("VIC", "NSW", "SA", "WA")) {
            var copy = new HashMap<>(query); copy.put("state", state);
            check(FarmWebServer.simulate(copy).contains("\"state\":\""+state+"\""), "All states supported");
        }
        for (String[] change : new String[][] {{"sheep","2147483647"},{"lambs","-1"},{"state","QLD"},
                {"name","short"},{"seed","1.2"},{"seed","9223372036854775808"},{"sheep","1e2"}}) {
            var copy = new HashMap<>(query); copy.put(change[0],change[1]); rejects(copy);
        }
        var zero = new HashMap<>(query); zero.put("sheep","0");zero.put("lambs","0"); rejects(zero);
        try { FarmWebServer.parseQuery("seed=42&seed=43"); throw new AssertionError("Duplicate field accepted"); }
        catch (IllegalArgumentException expected) { checks++; }
        System.out.println("PASS: " + checks + " web adapter checks");
        if (args.length > 0 && args[0].equals("--json")) System.out.println(json);
    }
}
