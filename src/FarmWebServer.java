import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** Local web adapter around the original Java simulation, without dependencies. */
public class FarmWebServer {
    public static void main(String[] args) throws IOException {
        int port = args.length == 0 ? 8080 : Integer.parseInt(args[0]);
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        Path webRoot = Path.of("web").toAbsolutePath().normalize();
        server.createContext("/", exchange -> {
            try {
                if (!exchange.getRequestMethod().equals("GET")) {
                    exchange.getResponseHeaders().set("Allow", "GET");
                    send(exchange, 405, "application/json", "{\"error\":\"GET required\"}");
                    return;
                }
                if (exchange.getRequestURI().getPath().equals("/api/simulate")) {
                    send(exchange, 200, "application/json", simulate(parseQuery(exchange.getRequestURI().getRawQuery())));
                    return;
                }
                String requestPath = exchange.getRequestURI().getPath();
                Path file = webRoot.resolve(requestPath.equals("/") ? "index.html" : requestPath.substring(1)).normalize();
                if (!file.startsWith(webRoot) || !Files.isRegularFile(file)) {
                    send(exchange, 404, "text/plain", "Not found");
                    return;
                }
                String mime = file.toString().endsWith(".html") ? "text/html" :
                    file.toString().endsWith(".css") ? "text/css" :
                    file.toString().endsWith(".js") ? "text/javascript" :
                    file.toString().endsWith(".png") ? "image/png" : "application/octet-stream";
                byte[] bytes = Files.readAllBytes(file);
                headers(exchange, mime);
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
            } catch (IllegalArgumentException e) {
                send(exchange, 400, "application/json", "{\"error\":" + quote(e.getMessage()) + "}");
            } catch (Exception e) {
                System.err.println("Request failed: " + e.getClass().getSimpleName());
                send(exchange, 500, "application/json", "{\"error\":\"Unable to run simulation. Check server terminal.\"}");
            } finally { exchange.close(); }
        });
        server.start();
        System.out.println("Alpaca Guard is ready: http://127.0.0.1:" + port);
        System.out.println("Java simulation engine · press Ctrl+C to stop");
    }

    private static void headers(HttpExchange exchange, String mime) {
        exchange.getResponseHeaders().set("Content-Type", mime + (mime.startsWith("text/") || mime.equals("application/json") ? "; charset=utf-8" : ""));
        exchange.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        exchange.getResponseHeaders().set("Content-Security-Policy", "default-src 'self'; img-src 'self' data:; style-src 'self'; script-src 'self'; connect-src 'self'; object-src 'none'; frame-ancestors 'none'");
    }
    private static void send(HttpExchange exchange, int code, String mime, String text) throws IOException {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        headers(exchange, mime);
        exchange.sendResponseHeaders(code, bytes.length);
        exchange.getResponseBody().write(bytes);
    }
    static Map<String, String> parseQuery(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null || query.length() > 2000) throw new IllegalArgumentException("Missing or oversized inputs");
        for (String field : query.split("&")) {
            String[] pair = field.split("=", 2);
            if (pair.length != 2) throw new IllegalArgumentException("Invalid query");
            String key = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
            if (result.put(key, URLDecoder.decode(pair[1], StandardCharsets.UTF_8)) != null)
                throw new IllegalArgumentException("Duplicate input: " + key);
        }
        return result;
    }
    static String simulate(Map<String, String> query) {
        String name = query.getOrDefault("name", "").trim();
        String stateName = query.getOrDefault("state", "");
        if (name.length() < 6 || name.length() > 60 || name.chars().anyMatch(Character::isISOControl))
            throw new IllegalArgumentException("Farm name must be 6–60 characters");
        if (!List.of("VIC", "NSW", "SA", "WA").contains(stateName))
            throw new IllegalArgumentException("Choose VIC, NSW, SA or WA");
        int sheep, lambs;
        long seed;
        try {
            sheep = Integer.parseInt(query.getOrDefault("sheep", ""));
            lambs = Integer.parseInt(query.getOrDefault("lambs", ""));
            seed = Long.parseLong(query.getOrDefault("seed", ""));
        } catch (NumberFormatException e) { throw new IllegalArgumentException("Use whole numbers for animals and seed"); }
        if (sheep < 0 || lambs < 0 || (long) sheep + lambs < 1 || (long) sheep + lambs > 100)
            throw new IllegalArgumentException("Choose 1–100 animals in total");
        State state = new FileIO().readPredatorFile().stream().filter(s -> s.getName().equals(stateName)).findFirst()
            .orElseThrow(() -> new IllegalStateException("Missing state data"));
        Farm farm = new Farm(name, stateName, sheep, lambs);
        StringJoiner scenarios = new StringJoiner(",", "[", "]");
        int best = 0;
        double lowest = Double.POSITIVE_INFINITY;
        for (int guards = 0; guards <= 2; guards++) {
            Simulation simulation = new Simulation(farm, state, guards, new Random(seed + guards));
            simulation.runSimulation();
            double mean = simulation.getAverageCost();
            if (mean < lowest) { lowest = mean; best = guards; }
            StringJoiner predators = new StringJoiner(",", "{", "}");
            simulation.getAverageKillsByPredator().entrySet().stream().sorted(Map.Entry.comparingByKey())
                .forEach(e -> predators.add(quote(e.getKey()) + ":" + e.getValue()));
            scenarios.add("{\"guards\":" + guards + ",\"meanCost\":" + mean +
                ",\"sheepLost\":" + simulation.getAverageSheepLost() +
                ",\"lambsLost\":" + simulation.getAverageLambsLost() +
                ",\"alpacasLost\":" + simulation.getAverageAlpacasLost() +
                ",\"costs\":" + Arrays.toString(simulation.getTrialCosts()) +
                ",\"sheepLosses\":" + Arrays.toString(simulation.getTrialSheepLost()) +
                ",\"lambLosses\":" + Arrays.toString(simulation.getTrialLambsLost()) +
                ",\"alpacaLosses\":" + Arrays.toString(simulation.getTrialAlpacasLost()) +
                ",\"predators\":" + predators + "}");
        }
        return "{\"name\":" + quote(name) + ",\"state\":" + quote(stateName) +
            ",\"sheep\":" + sheep + ",\"lambs\":" + lambs + ",\"seed\":" + quote(Long.toString(seed)) +
            ",\"trials\":10,\"best\":" + best + ",\"scenarios\":" + scenarios + "}";
    }
    static String quote(String value) {
        StringBuilder result = new StringBuilder("\"");
        for (char c : value.toCharArray()) {
            if (c == '"' || c == '\\') result.append('\\');
            if (c < 32) result.append(String.format("\\u%04x", (int)c));
            else result.append(c);
        }
        return result.append('"').toString();
    }
}
