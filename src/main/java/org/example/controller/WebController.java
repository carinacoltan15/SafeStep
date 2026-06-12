package org.example.controller;

import org.example.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class WebController {

    @Autowired private NodeRepository nodeRepository;
    @Autowired private ParentRepository parentRepository;

    private volatile boolean isDangerActive = false;
    private volatile boolean isMissionAccomplished = false;
    private volatile boolean missionActive = false;

    private static class LiveChildState {
        double latitude = 45.6427;
        double longitude = 25.5887;
        String avatar = "🤖";
        String kidName = "";
        String sessionId = "";
        long updatedAt = 0;
        int xpPoints = 0;      // accumulated XP — persists across child page reloads
    }

    private final LiveChildState liveChild = new LiveChildState();
    private final List<Map<String, Object>> alertHistory = new CopyOnWriteArrayList<>();

    // --- PARENT ---
    @PostMapping("/parent/register")
    public ResponseEntity<?> register(@RequestBody Parent parent) {
        if (parentRepository.findByEmail(parent.getEmail()).isPresent())
            return ResponseEntity.badRequest().body("Email deja folosit!");
        parentRepository.save(parent);
        return ResponseEntity.ok("Succes!");
    }

    @PostMapping("/parent/login")
    public ResponseEntity<?> login(@RequestBody Parent parent) {
        return parentRepository.findByEmail(parent.getEmail())
                .filter(p -> p.getPassword().equals(parent.getPassword()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build());
    }

    @PostMapping("/parent/generate-code")
    public String generateCode(@RequestParam String email) {
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        parentRepository.findByEmail(email).ifPresent(p -> {
            p.setPairingCode(code);
            parentRepository.save(p);
        });
        return code;
    }

    @GetMapping("/parent/check-code/{code}")
    public ResponseEntity<?> checkPairingCode(@PathVariable String code) {
        Optional<Parent> parent = parentRepository.findAll().stream()
                .filter(p -> code.equals(p.getPairingCode()))
                .findFirst();
        if (parent.isPresent()) return ResponseEntity.ok(parent.get());
        return ResponseEntity.status(404).body("Cod invalid!");
    }

    // --- NODES / MISSION ---
    @GetMapping("/nodes")
    public List<Node> getAllNodes() { return nodeRepository.findAll(); }

    @PostMapping("/nodes/add")
    public Node addNode(@RequestBody Node node) { return nodeRepository.save(node); }

    @PostMapping("/nodes/visit/{id}")
    public ResponseEntity<Map<String, Object>> markVisited(@PathVariable Long id) {
        return nodeRepository.findById(id).map(n -> {
            n.setVisited(true);
            nodeRepository.save(n);
            long total = nodeRepository.count();
            long visited = nodeRepository.findAll().stream().filter(Node::isVisited).count();
            int progressPct = total > 0 ? (int) Math.round((visited * 100.0) / total) : 0;
            Map<String, Object> body = new HashMap<>();
            body.put("success", true);
            body.put("visitedCount", visited);
            body.put("totalCount", total);
            body.put("progressPercent", progressPct);
            body.put("pointsAwarded", 100);
            return ResponseEntity.ok(body);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/mission/progress")
    public ResponseEntity<Map<String, Object>> missionProgress() {
        List<Node> nodes = nodeRepository.findAll();
        long total = nodes.size();
        long visited = nodes.stream().filter(Node::isVisited).count();
        int progressPct = total > 0 ? (int) Math.round((visited * 100.0) / total) : 0;
        Map<String, Object> body = new HashMap<>();
        body.put("totalCount", total);
        body.put("visitedCount", visited);
        body.put("progressPercent", progressPct);
        body.put("activeCount", total - visited);
        body.put("missionActive", missionActive);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/mission/launch")
    public ResponseEntity<Map<String, Object>> launchMission() {
        missionActive = true;
        isMissionAccomplished = false;
        // Do NOT reset liveChild.latitude/longitude here — the child may have already
        // synced their real GPS position; overwriting it with hardcoded coords causes
        // the parent's map marker to jump to the wrong location.
        liveChild.updatedAt = Instant.now().toEpochMilli();
        Map<String, Object> body = new HashMap<>();
        body.put("missionActive", true);
        return ResponseEntity.ok(body);
    }

    // --- LIVE CHILD POSITION (DEMO SYNC) ---
    @PostMapping("/child/position")
    public ResponseEntity<Map<String, Object>> updateChildPosition(@RequestBody Map<String, Object> payload) {
        if (payload.get("latitude") != null) liveChild.latitude = ((Number) payload.get("latitude")).doubleValue();
        if (payload.get("longitude") != null) liveChild.longitude = ((Number) payload.get("longitude")).doubleValue();
        if (payload.get("avatar") != null) liveChild.avatar = String.valueOf(payload.get("avatar"));
        if (payload.get("kidName") != null) liveChild.kidName = String.valueOf(payload.get("kidName"));
        if (payload.get("sessionId") != null) liveChild.sessionId = String.valueOf(payload.get("sessionId"));
        liveChild.updatedAt = Instant.now().toEpochMilli();

        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("updatedAt", liveChild.updatedAt);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/child/position")
    public ResponseEntity<Map<String, Object>> getChildPosition() {
        Map<String, Object> body = new HashMap<>();
        body.put("latitude", liveChild.latitude);
        body.put("longitude", liveChild.longitude);
        body.put("avatar", liveChild.avatar);
        body.put("kidName", liveChild.kidName);
        body.put("sessionId", liveChild.sessionId);
        body.put("updatedAt", liveChild.updatedAt);
        body.put("missionActive", missionActive);
        body.put("dangerActive", isDangerActive);
        return ResponseEntity.ok(body);
    }

    // --- CHILD XP (in-memory, persists while the server is running) ---

    /**
     * Returns the stored XP for a given kidName (or the current liveChild if no name given).
     * Used by the child frontend to restore XP after a page reload.
     */
    @GetMapping("/child/xp")
    public ResponseEntity<Map<String, Object>> getChildXP(
            @RequestParam(required = false) String kidName) {
        Map<String, Object> body = new HashMap<>();
        boolean nameMatch = kidName == null || kidName.isBlank()
                || kidName.equalsIgnoreCase(liveChild.kidName);
        body.put("kidName",   liveChild.kidName);
        body.put("xpPoints",  nameMatch ? liveChild.xpPoints : 0);
        body.put("matched",   nameMatch);
        return ResponseEntity.ok(body);
    }

    /**
     * Saves the child's accumulated XP to the server so it survives a page reload.
     * Payload: { "kidName": "...", "xpPoints": 350 }
     */
    @PostMapping("/child/xp")
    public ResponseEntity<Map<String, Object>> updateChildXP(
            @RequestBody Map<String, Object> payload) {
        if (payload.get("xpPoints") instanceof Number n)
            liveChild.xpPoints = n.intValue();
        if (payload.get("kidName") instanceof String s && !s.isBlank())
            liveChild.kidName = s;
        Map<String, Object> body = new HashMap<>();
        body.put("success",  true);
        body.put("xpPoints", liveChild.xpPoints);
        return ResponseEntity.ok(body);
    }

    // --- ALERTS ---
    @PostMapping("/parent/alert-danger")
    public ResponseEntity<Map<String, Object>> alertDanger(@RequestBody(required = false) Map<String, Object> payload) {
        isDangerActive = true;
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("id", UUID.randomUUID().toString());
        entry.put("timestamp", Instant.now().toString());
        entry.put("message", payload != null && payload.get("message") != null
                ? String.valueOf(payload.get("message"))
                : "Copilul a ieșit din zona de siguranță!");
        entry.put("kidName", liveChild.kidName);
        alertHistory.add(0, entry);
        if (alertHistory.size() > 50) alertHistory.remove(alertHistory.size() - 1);

        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("dangerActive", true);
        body.put("message", entry.get("message"));
        return ResponseEntity.ok(body);
    }

    @GetMapping("/parent/check-alert")
    public ResponseEntity<Map<String, Object>> checkAlert() {
        Map<String, Object> body = new HashMap<>();
        body.put("dangerActive", isDangerActive);
        body.put("successActive", isMissionAccomplished);
        body.put("childName", liveChild.kidName);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/parent/clear-alert")
    public ResponseEntity<Map<String, Object>> clearAlert() {
        isDangerActive = false;
        Map<String, Object> body = new HashMap<>();
        body.put("dangerActive", false);
        body.put("successActive", isMissionAccomplished);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/parent/alert-success")
    public ResponseEntity<Map<String, Object>> alertSuccess(@RequestBody(required = false) Map<String, Object> payload) {
        isMissionAccomplished = true;
        if (payload != null && payload.get("childName") != null) {
            liveChild.kidName = String.valueOf(payload.get("childName"));
        }

        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("id", UUID.randomUUID().toString());
        entry.put("timestamp", Instant.now().toString());
        entry.put("message", payload != null && payload.get("message") != null
                ? String.valueOf(payload.get("message"))
                : "Misiune îndeplinită! Copilul a ajuns în siguranță la destinație.");
        entry.put("kidName", liveChild.kidName);
        alertHistory.add(0, entry);
        if (alertHistory.size() > 50) alertHistory.remove(alertHistory.size() - 1);

        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("dangerActive", isDangerActive);
        body.put("successActive", true);
        body.put("childName", liveChild.kidName);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/parent/clear-success")
    public ResponseEntity<Map<String, Object>> clearSuccess() {
        isMissionAccomplished = false;
        Map<String, Object> body = new HashMap<>();
        body.put("dangerActive", isDangerActive);
        body.put("successActive", false);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/parent/alert-history")
    public List<Map<String, Object>> alertHistory() {
        return new ArrayList<>(alertHistory);
    }

    // --- RESET DEMO (stare curată, fără repornire server) ---
    @PostMapping("/system/reset")
    public ResponseEntity<Map<String, Object>> systemReset() {
        isDangerActive = false;
        isMissionAccomplished = false;
        missionActive = false;
        alertHistory.clear();
        nodeRepository.deleteAll();

        parentRepository.findAll().forEach(p -> {
            p.setPairingCode(null);
            parentRepository.save(p);
        });

        liveChild.latitude = 45.6427;
        liveChild.longitude = 25.5887;
        liveChild.avatar = "🤖";
        liveChild.kidName = "";
        liveChild.sessionId = "";
        liveChild.xpPoints = 0;
        liveChild.updatedAt = Instant.now().toEpochMilli();

        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("message", "Demo resetat — misiune, alerte și pairing curățate.");
        body.put("dangerActive", false);
        body.put("successActive", false);
        body.put("missionActive", false);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/system/reset")
    public ResponseEntity<Map<String, Object>> systemResetGet() {
        return systemReset();
    }
}
